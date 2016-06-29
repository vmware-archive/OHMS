/* ********************************************************************************
 * ServerNodeConnector.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.node.server;

import com.vmware.vrack.hms.HMSMonitorService;
import com.vmware.vrack.hms.HmsApp;
import com.vmware.vrack.hms.TaskRequestHandler;
import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.common.ExternalService;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.configuration.HmsInventoryConfiguration;
import com.vmware.vrack.hms.common.configuration.ServerItem;
import com.vmware.vrack.hms.common.configuration.ServiceItem;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsResourceBusyException;
import com.vmware.vrack.hms.common.monitoring.MonitorTaskSuite;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskRequestHandler;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.notification.CallbackRequestFactory;
import com.vmware.vrack.hms.common.notification.EventType;
import com.vmware.vrack.hms.common.notification.HMSNotificationRequest;
import com.vmware.vrack.hms.common.notification.NodeActionStatus;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switchnodes.api.HMSSwitchNode;
import com.vmware.vrack.hms.task.TaskFactory;
import com.vmware.vrack.hms.task.TaskType;
import com.vmware.vrack.hms.task.oob.redfish.RedfishDiscoverComputerSystemsTask;
import com.vmware.vrack.hms.utils.NodeDiscoveryUtil;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.ConnectorStatistics;
import org.eclipse.jetty.server.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.vmware.vrack.hms.boardservice.BoardServiceProvider.prepareBoardServiceClassesForServices;
import static com.vmware.vrack.hms.task.TaskType.RedfishDiscoverComputerSystems;

public class ServerNodeConnector
{
    private static Logger logger = Logger.getLogger( ServerNodeConnector.class );

    private static volatile ServerNodeConnector instance = new ServerNodeConnector();

    public Map<String, HmsNode> nodeMap = new ConcurrentHashMap<>();

    public Map<String, ExternalService> serviceMap = new ConcurrentHashMap<>();

    public Server server = null;

    public boolean shutDownJetty = false;

    private ServerNode applicationNode = new ServerNode( "HMS_OOB_AGENT", null, null, null );

    private boolean enableMonitoring =
        Boolean.parseBoolean( HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS,
                                                           "enable_monitoring" ) );

    private ServerNodeConnector()
    {
        try
        {
            parseRackInventoryConfig();
        }
        catch ( Exception e )
        {
            ServerNodeConnector.notifyHMSFailure( "ERROR_INITIALIZING_APPLICATION", e.getMessage() );
            logger.error( e.getMessage() );
        }
    }

    /*
     * public IpmiConnector getConnector() { return connector; }
     */
    public static ServerNodeConnector getInstance()
    {
        return instance;
    }

    /*
     * public void initHandshake() throws Exception { TaskResponse response = new TaskResponse();
     * TaskRequestHandler.getInstance().executeServerTask(TaskType.HmsPrmHandshake,response); } public void
     * initHmsResourceMonitor() throws Exception { TaskResponse response = new TaskResponse();
     * TaskRequestHandler.getInstance().executeServerTask(TaskType.HMSResourceMonitor,response); }
     */
    public static void notifyHMSFailure( String failureCode, String failureMessage )
    {
        ArrayList<Map<String, String>> results = new ArrayList<>();
        Map<String, String> failure = new HashMap<>();
        failure.put( failureCode, failureMessage );
        results.add( failure );
        HMSNotificationRequest notification =
            CallbackRequestFactory.getNotificationRequest( EventType.HMS_FAILURE, "HMS_0", results );
        HMSMonitorService notifier = new HMSMonitorService();
        notifier.update( new HMSSwitchNode( "HMS_0", null, null, null ), notification );
    }

    public static List<ServerComponent> getMonitoredServerComponents()
    {
        List<ServerComponent> monitoredComponents =
            new ArrayList<>( Arrays.asList( ServerComponent.values() ) );
        return monitoredComponents;
    }

    public Server getServer()
    {
        return server;
    }

    public void setServer( Server server )
    {
        this.server = server;
        // server.setHandler(new StatisticsHandler());
        ConnectorStatistics stats = server.getBean( ConnectorStatistics.class );
        try
        {
            stats.start();
        }
        catch ( Exception e )
        {
            logger.error( "cont start stats connector", e );
        }
    }

    public void restartJetty()
    {
        shutDownJetty = false;
        stopJetty();
    }

    private void stopJetty()
    {
        try
        {
            // Stop the server.
            new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        logger.info( "Shutting down Jetty..." );
                        server.stop();
                        logger.info( "Jetty has stopped." );
                    }
                    catch ( Exception ex )
                    {
                        logger.error( "Error when stopping Jetty: " + ex.getMessage(), ex );
                    }
                }
            }.start();
        }
        catch ( Exception ex )
        {
            logger.error( "Unable to stop Jetty: " + ex );
            forceSystemRestart();
        }
    }

    public void shutDownJetty()
    {
        shutDownJetty = true;
        stopJetty();
    }

    public void restartHMS()
    {
        HmsApp.HMS_EXIT_CODE = HmsApp.RESTART_HMS;
        shutDownJetty();
        forceSystemRestart();
    }

    public void forceSystemRestart()
    {
        System.exit( HmsApp.RESTART_HMS );
    }

    public void parseRackInventoryConfig()
        throws Exception
    {
        try
        {
            HmsInventoryConfiguration hic = HmsConfigHolder.getHmsInventoryConfiguration();
            for ( ServerItem server : hic.getServers() )
            {
                nodeMap.put( server.getId(), discoverHosts( server ) );
                logger.debug( "Inserting server id " + server.getId() + " into server node map." );
            }
            prepareExternalServices( hic.getServices() );
            Map<String, HmsNode> externalServiceNodes = discoverExternalServiceNodes( hic.getServices() );
            for ( Map.Entry<String, HmsNode> stringHmsNodeEntry : externalServiceNodes.entrySet() )
            {
                HmsNode node = stringHmsNodeEntry.getValue();
                nodeMap.put( stringHmsNodeEntry.getKey(), node );
                logger.debug( "Inserting server id " + node.getNodeID() + " into server node map." );

            }
        }
        catch ( Exception e )
        {
            ServerNodeConnector.notifyHMSFailure( "ERROR_HMS_HOSTS_BOOTUP", e.getMessage() );
            throw e;
        }

        // Discover Node to BoardService Mapping
        prepareNodeToBoardServiceMapping();
        // Perform Bootup task on all Nodes
        performBootupTaskForNodes();
        if ( enableMonitoring )
        {
            initMonitoring();
            // initHandshake();
            // initHmsResourceMonitor(); // available only on demand
        }
    }

    private Map<String, HmsNode> discoverExternalServiceNodes( List<ServiceItem> services )
        throws HmsException
    {
        Map<String, HmsNode> hmsNodeMap = new HashMap<>();
        for ( ServiceItem service : services )
        {
            List<ServerNode> nodes = getNodesForService( service );
            for ( ServerNode node : nodes )
            {
                ServiceItem.InBandAccess ibInfo = getIbInfo( node, service );

                if ( ibInfo != null )
                {
                    ServerNode serverNode = mergeHmsNodeDataWithInBandAccess( node, ibInfo );
                    hmsNodeMap.put( node.getNodeID(), serverNode );
                }
                else
                {
                    hmsNodeMap.put( node.getNodeID(), node );
                }
            }
        }
        return hmsNodeMap;
    }

    private ServerNode mergeHmsNodeDataWithInBandAccess( ServerNode node, ServiceItem.InBandAccess ibInfo )
    {
        ServerNode serverNode = new ServerNode();
        serverNode.setNodeID( node.getNodeID() );
        serverNode.setOsUserName( ibInfo.getUsername() );
        serverNode.setOsPassword( ibInfo.getPassword() );
        serverNode.setIbIpAddress( ibInfo.getIpAddress() );
        serverNode.setHypervisorName( ibInfo.getHypervisorName() );
        serverNode.setBoardProductName( node.getBoardProductName() );
        serverNode.setBoardVendor( node.getBoardVendor() );

        serverNode.setHypervisorProvider( ibInfo.getHypervisorProvider() );

        return serverNode;
    }

    private ServiceItem.InBandAccess getIbInfo( ServerNode node, ServiceItem service )
    {
        for ( ServiceItem.InBandAccess inBandAccess : service.getInBandAccess() )
        {
            if ( node.getUuid().equals( inBandAccess.getUuid() ) )
            {
                return inBandAccess;
            }
        }

        return null;
    }

    private List<ServerNode> getNodesForService( ServiceItem service )
        throws HmsException
    {
        try
        {
            RedfishDiscoverComputerSystemsTask task = (RedfishDiscoverComputerSystemsTask) TaskFactory.getTask(
                RedfishDiscoverComputerSystems, null );
            ExternalService externalService = serviceMap.get( service.getServiceEndpoint() );
            task.setExternalService( externalService );
            task.executeTask();
            return task.getDiscoveredNodes();
        }
        catch ( HmsResourceBusyException e )
        {
            String message = "Encountered HmsResourceBusyException during execution of task";
            logger.error( message, e );
            throw new HmsException( message, e );
        }
        catch ( HmsException e )
        {
            logger.error( "Encountered exception during execution of task", e );
            throw e;
        }
        catch ( Exception e )
        {
            String message = "Encountered exception during execution of task";
            logger.error( message, e );
            throw new HmsException( message );
        }
    }

    private void prepareExternalServices( List<ServiceItem> services )
    {
        for ( ServiceItem service : services )
        {
            serviceMap.put( service.getServiceEndpoint(),
                            new ExternalService( service.getServiceType(), service.getServiceEndpoint() ) );
        }
        prepareBoardServiceClassesForServices( new ArrayList<>( serviceMap.values() ) );
    }

    /**
     * Calls each BoardService implementing class to figureout which all board it can support and puts that mapping in
     * the BoardServiceProvider
     */
    private void prepareNodeToBoardServiceMapping()
        throws Exception
    {
        // For now to keep thing simple, doing discovery of node in single thread, but we can surely put them in
        // Executor service later on
        // ExecutorService executor = Executors.newFixedThreadPool(3);
        List<HmsNode> hmsNodes = new ArrayList<>( nodeMap.values() );
        /*
         * //prepare list of ServiceServerNode from ServerNode final List<ServiceHmsNode> serviceHmsNodes = new
         * ArrayList<ServiceHmsNode>(); for(HmsNode hmsNode: hmsNodes) { serviceHmsNodes.add(((ServerNode)
         * hmsNode).getServiceObject()); } //Generate Node to BoardService mapping in BoardServiceProvider
         * BoardServiceProvider.prepareBoardServiceForNodes(serviceHmsNodes);
         */
        // Generate Node to BoardService mapping in BoardServiceProvider
        BoardServiceProvider.prepareBoardServiceClassesForNodes( hmsNodes );
    }

    /**
     * Run Bootup task for each node
     */
    private void performBootupTaskForNodes()
        throws Exception
    {
        try
        {
            for ( HmsNode node : nodeMap.values() )
            {
                logger.debug( "Calling Boot Work flow for server id " + node.getNodeID() );
                // Putting the node as "RUNNING", indicating it has been submitted for discovery process.
                // Discovery task will take care of changing the discovery status to "SUCCESS" or "ERROR".
                NodeDiscoveryUtil.hostDiscoveryMap.put( node.getNodeID(), NodeActionStatus.RUNNING );
                initBootWorkflow( (ServerNode) node );
            }
        }
        catch ( Exception e )
        {
            ServerNodeConnector.notifyHMSFailure( "ERROR_HMS_HOSTS_BOOTUP", e.getMessage() );
            throw e;
        }
    }

    private ServerNode discoverHosts( ServerItem server )
        throws Exception
    {
        ServerNode node = new ServerNode( server.getId(), server.getOobIpAddress(), server.getOobUsername(),
                                          server.getOobPassword() );
        node.setIbIpAddress( server.getIbIpAddress() );
        node.setOsPassword( server.getIbPassword() );
        node.setOsUserName( server.getIbUsername() );
        node.setOsEncodedPassword( server.getIbPassword() );
        if ( server.getBoardInfo() != null )
        {
            node.setBoardProductName( server.getBoardInfo().getModel() );
            node.setBoardVendor( server.getBoardInfo().getManufacturer() );
        }
        else
        {
            logger.error( "BoardInfo can not be null for node: " + node.getNodeID() );
        }
        if ( server.getHypervisorInfo() != null )
        {
            node.setHypervisorName( server.getHypervisorInfo().getName() );
            node.setHypervisorProvider( server.getHypervisorInfo().getProvider() );
        }
        else
        {
            logger.error( "HypervisorInfo is not available for node: " + node.getNodeID() );
        }
        // 2014-07-03: Commented by Yagnesh as bootup will be done after discovery of BoardService now
        // initBootWorkflow(node);
        return node;
    }

    private void initBootWorkflow( ServerNode node )
        throws Exception
    {
        TaskResponse response = new TaskResponse();
        response.setNode( node );
        response.setTaskType( TaskType.HMSBootUp.toString() );
        TaskRequestHandler.getInstance().executeServerTask( TaskType.HMSBootUp, response );
    }

    private void initMonitoring()
        throws Exception
    {
        MonitoringTaskRequestHandler.init(
            Integer.parseInt( HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS,
                                                           "MONITORING_THREAD_POOL_SIZE" ) ) );
        try
        {
            HMSMonitorService monitor = new HMSMonitorService();
            for ( Object key : nodeMap.keySet() )
            {
                HmsNode node = nodeMap.get( key );
                node.addObserver( monitor );
                try
                {
                    if ( node instanceof ServerNode )
                    {
                        IBoardService boardService = BoardServiceProvider.getBoardService( node.getServiceObject() );
                        // MonitoringTaskResponse monitoringResponse = new MonitoringTaskResponse((node),
                        // ServerComponent.CPU, boardService);
                        MonitoringTaskResponse monitoringResponse =
                            new MonitoringTaskResponse( ( node ), getMonitoredServerComponents(),
                                                        boardService );
                        MonitorTaskSuite task =
                            new MonitorTaskSuite( monitoringResponse,
                                                  Long.parseLong( HmsConfigHolder.getHMSConfigProperty(
                                                      "HOST_NODE_MONITOR_FREQUENCY" ) ) );
                        MonitoringTaskRequestHandler.getInstance().executeServerMonitorTask( task );
                    }
                }
                catch ( Exception e )
                {
                    logger.error( "Error occurred during submission of node [ " + node.getNodeID()
                                      + " ] for monitoring. ", e );
                }
            }
        }
        catch ( Exception e )
        {
            ServerNodeConnector.notifyHMSFailure( "ERROR_INITIALIZING_HOSTS_MONITORING_SERVICE", e.getMessage() );
        }
    }

    public ServerNode getApplicationNode()
    {
        return applicationNode;
    }

    public void setApplicationNode( ServerNode applicationNode )
    {
        this.applicationNode = applicationNode;
    }
}
