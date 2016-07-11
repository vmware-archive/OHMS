/* ********************************************************************************
 * ServerNodeConnector.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.node.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.ConnectorStatistics;
import org.eclipse.jetty.server.Server;

import com.vmware.vrack.hms.HMSMonitorService;
import com.vmware.vrack.hms.HmsApp;
import com.vmware.vrack.hms.TaskRequestHandler;
import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.configuration.HmsInventoryConfiguration;
import com.vmware.vrack.hms.common.configuration.ServerItem;
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
import com.vmware.vrack.hms.task.TaskType;
import com.vmware.vrack.hms.utils.NodeDiscoveryUtil;

public class ServerNodeConnector
{
    private static Logger logger = Logger.getLogger( ServerNodeConnector.class );

    private static volatile ServerNodeConnector instance = new ServerNodeConnector();

    private ServerNode applicationNode = new ServerNode( "HMS_OOB_AGENT", null, null, null );

    public Map<String, HmsNode> nodeMap = new ConcurrentHashMap<String, HmsNode>();

    public Server server = null;

    public boolean shutDownJetty = false;

    private boolean enableMonitoring =
        Boolean.parseBoolean( HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS, "enable_monitoring" ) );

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

    /**
     * Calls each BoardService implementing class to figureout which all board it can support and puts that mapping in
     * the BoardServiceProvider
     */
    private void prepareNodeToBoardServiceMapping()
        throws Exception
    {
        // For now to keep thing simple, doing discovery of node in single thread, but we can surely put them in
        // Executor service lateron
        // ExecutorService executor = Executors.newFixedThreadPool(3);
        List<HmsNode> hmsNodes = new ArrayList<HmsNode>( nodeMap.values() );
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
        node.setLocation( server.getLocation() );
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

    /*
     * public void initHandshake() throws Exception { TaskResponse response = new TaskResponse();
     * TaskRequestHandler.getInstance().executeServerTask(TaskType.HmsPrmHandshake,response); } public void
     * initHmsResourceMonitor() throws Exception { TaskResponse response = new TaskResponse();
     * TaskRequestHandler.getInstance().executeServerTask(TaskType.HMSResourceMonitor,response); }
     */
    public static void notifyHMSFailure( String failureCode, String failureMessage )
    {
        ArrayList<Map<String, String>> results = new ArrayList<Map<String, String>>();
        Map<String, String> failure = new HashMap<String, String>();
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
            new ArrayList<ServerComponent>( Arrays.asList( ServerComponent.values() ) );
        return monitoredComponents;
    }

    private void initMonitoring()
        throws Exception
    {
        MonitoringTaskRequestHandler.init( Integer.parseInt( HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS,
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
                            new MonitoringTaskResponse( ( node ), getMonitoredServerComponents(), boardService );
                        MonitorTaskSuite task =
                            new MonitorTaskSuite( monitoringResponse,
                                                  Long.parseLong( HmsConfigHolder.getHMSConfigProperty( "HOST_NODE_MONITOR_FREQUENCY" ) ) );
                        MonitoringTaskRequestHandler.getInstance().executeServerMonitorTask( task );
                    }
                }
                catch ( Exception e )
                {
                    logger.error( "Error occured during submission of node [ " + node.getNodeID()
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
