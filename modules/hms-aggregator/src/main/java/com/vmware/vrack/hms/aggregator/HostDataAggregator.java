/* ********************************************************************************
 * HostDataAggregator.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.aggregator.util.AggregatorUtil;
import com.vmware.vrack.hms.aggregator.util.HostUpDownEventAggregator;
import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.aggregator.util.ServerInfoHelperUtil;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.MonitorResponseCallback;
import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.rest.model.FruComponent;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodePowerStatus;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.inventory.ServerDataChangeMessage;

/**
 * @author sgakhar Purpose of this Class is to aggregate inband and out of band host data/Server Node Object.
 */
@SuppressWarnings( "deprecation" )
@Component
public class HostDataAggregator
{

    @Autowired
    private ApplicationContext context;

    @Value( "${esxi.ssh.retry.count}" )
    private int sshRetryCount;

    @Value( "${esxi.ssh.retry.delay}" )
    private int sshRetryDelay;

    private static Logger logger = LoggerFactory.getLogger( HostDataAggregator.class );

    /**
     * gets HOST ServerNode aggregated from OOB and IB componnet data
     *
     * @param node_id
     * @return
     * @throws HmsException
     */
    public ServerNode getServerNode( String node_id )
        throws HmsException
    {
        ServerNode node = getServerNodeOOB( node_id );
        aggregateInBandData( node );
        return node;
    }

    /**
     * gets HOST getServerInfo aggregated from OOB and IB component data
     *
     * @param node_id
     * @return
     * @throws HmsException
     */
    public ServerInfo getServerInfo( String node_id )
        throws HmsException
    {
        ServerInfo serverInfo = new ServerInfo();

        ServerNode node = getServerNodeOOB( node_id );
        if ( node != null )
        {
            if ( "true".equalsIgnoreCase( node.getOperationalStatus() ) )
            {
                logger.debug( "Node: {} is operational", node.getNodeID() );
                aggregateInBandData( node );
            }
            else
            {
                // As oob has resulted in non-operational now will go ahead and
                // verify the ESXI reachable state and based on its response
                // will retrieve the FRU stats.
                logger.debug( "Node: {} is not operational, so validates if Inband ip is reachable or not",
                              node.getNodeID() );

                ServerNode serverNode = InventoryLoader.getInstance().getNode( node_id );
                boolean isInbandReachable =
                    AggregatorUtil.isEsxiHostReachable( serverNode, sshRetryCount, sshRetryDelay );

                if ( isInbandReachable )
                {
                    node.setDiscoverable( true );
                    node.setPowered( true );

                    logger.debug( "Fetching the data from Inband for Node: {}", node_id );
                    aggregateInBandData( node );
                    logger.debug( "Successfuly fetched the data from Inband for Node: {}", node_id );
                }
            }
        }
        serverInfo = ServerInfoHelperUtil.convertServerNodeToServerInfo( node );
        context.publishEvent( new ServerDataChangeMessage( serverInfo, ServerComponent.SERVER ) );

        return serverInfo;
    }

    /**
     * Get Server Node power status and also publish it so that cache holding HMSDataCache can be updated for Node's
     * Power status
     *
     * @param nodeId
     * @param serverInfo
     * @return
     * @throws HmsException
     */
    public ServerNodePowerStatus getAndUpdateServerNodePowerStatus( String nodeId, ServerInfo serverInfo )
        throws HmsException
    {

        if ( serverInfo == null )
        {
            serverInfo = new ServerInfo();
            serverInfo.setNodeId( nodeId );
        }

        ServerNodePowerStatus serverNodePowerStatus = new ServerNodePowerStatus();
        try
        {
            serverNodePowerStatus = getServerNodePowerStatus( nodeId );
        }
        catch ( Exception e )
        {
            logger.error( "Exception occured while checking the power status for the node: {}", nodeId );
        }

        if ( serverNodePowerStatus != null
            && !FruOperationalStatus.Operational.name().equals( serverNodePowerStatus.getOperationalStatus() ) )
        {

            ServerNode serverNode = InventoryLoader.getInstance().getNode( nodeId );
            logger.debug( "Node: {} is not operational, so validates if Inband ip is reachable or not", nodeId );

            boolean isInbandReachable = AggregatorUtil.isEsxiHostReachable( serverNode, sshRetryCount, sshRetryDelay );
            if ( isInbandReachable )
            {
                logger.debug( "ESXI is reachable for the nodeId: {}", nodeId );
                serverInfo.setDiscoverable( true );
                serverInfo.setPowered( true );
                serverInfo.setOperationalStatus( FruOperationalStatus.Operational );
                serverNodePowerStatus.setDiscoverable( true );
                serverNodePowerStatus.setPowered( true );
                serverNodePowerStatus.setOperationalStatus( FruOperationalStatus.Operational.name() );
            }
        }
        else
        {
            serverInfo.setDiscoverable( serverNodePowerStatus.isDiscoverable() );
            serverInfo.setPowered( serverNodePowerStatus.isPowered() );
            serverInfo.setOperationalStatus( FruOperationalStatus.Operational.toString().equalsIgnoreCase( serverNodePowerStatus.getOperationalStatus() )
                            ? FruOperationalStatus.Operational : FruOperationalStatus.NonOperational );

        }

        // Check for SERVER_UP/DOWN events
        // If zero count for the Host's power related events are returned, means
        // there is NO power state change.
        // If Non zero, then there is a change in the state. If Host had gone
        // from DOWN to up, then refresh whole Server node else simply update
        // cache for latest state.
        processDataForPowerStatus( nodeId, serverInfo );

        return serverNodePowerStatus;
    }

    /**
     * Process and update HMSDataCache if component is PowerUnit. If zero count for the Host's power related events are
     * returned, means there is NO power state change. If Non zero, then there is a change in the state. Refresh
     * SErverNode in from H/w in that case
     *
     * @param nodeId
     * @param component
     * @param fruComponent
     */
    private void processDataForPowerStatus( String nodeId, FruComponent fruComponent )
    {
        if ( fruComponent instanceof ServerInfo )
        {
            HostUpDownEventAggregator hostUpDownEventAggregator = new HostUpDownEventAggregator();
            ServerInfo latestServerInfo = (ServerInfo) fruComponent;
            try
            {
                logger.debug( "About to generate HOST UP/DOWN events, if needed to be generated for Server Node: {}",
                              nodeId );
                // Generates and broadcasts events for Power Up/Down, if there
                // is a change in the Power state
                ServerNodePowerStatus changedServerNodePowerStatus = getServerNodePowerStatus( latestServerInfo );

                HmsNode hmsNode = new ServerNode();
                hmsNode.setNodeID( latestServerInfo.getNodeId() );

                if ( latestServerInfo.getAdminStatus() != null )
                {
                    hmsNode.setAdminStatus( NodeAdminStatus.valueOf( latestServerInfo.getAdminStatus() ) );
                }

                // Returns a NON-Empty List<Events> if previous power status is
                // different from the current power status
                List<Event> events =
                    hostUpDownEventAggregator.poppulateHostUpDownEvent( hmsNode, changedServerNodePowerStatus );

                // Broadcasts events for Power Up/Down, if there is a change in
                // the Power state ie. List is NON empty
                if ( events != null && events.size() > 0 )
                {
                    MonitorResponseCallback updateSubscriber = new MonitorResponseCallback();

                    // List<Event> events =
                    // EventMonitoringSubscriptionHolder.getEventList(hmsNode,
                    // ServerComponent.SERVER);
                    updateSubscriber.callbackEventSubcribersUsingEvents( hmsNode, events );

                    // If Node PowerStatus is up, then refresh whole node
                    // Call method to make sure to update server info, as Server
                    // power state has changed.
                    // This method internally will update the HmsDataCache
                    if ( latestServerInfo.isPowered() )
                    {
                        this.getServerInfo( nodeId );
                    }
                    else
                    {
                        // Simply update HmsDataCache.
                        context.publishEvent( new ServerDataChangeMessage( latestServerInfo, ServerComponent.SERVER ) );
                    }
                }
            }
            catch ( Exception e )
            {
                // Ignore if something went wrong while sending the event to PRM
                logger.error( String.format( "Error occurred while generating and setting Host UP/DOWN events for node : %s",
                                             nodeId ),
                              e );
            }
        }
        else
        {
            logger.warn( String.format( "Expected FruComponent of type ServerInfo, got [%s]", fruComponent ) );
        }
    }

    /**
     * Get ServerNodePowerStatus object given a ServerInfo
     *
     * @param serverInfo
     * @return
     */
    private ServerNodePowerStatus getServerNodePowerStatus( ServerInfo serverInfo )
    {
        ServerNodePowerStatus serverNodePowerStatus = new ServerNodePowerStatus();

        if ( serverInfo != null )
        {
            serverNodePowerStatus.setDiscoverable( serverInfo.isDiscoverable() );
            serverNodePowerStatus.setOperationalStatus( serverInfo.getOperationalStatus().name() );
            serverNodePowerStatus.setPowered( serverInfo.isPowered() );
        }
        return serverNodePowerStatus;
    }

    /**
     * gets HOST ServerNode aggregated from OOB and IB component data
     *
     * @param node_id
     * @return
     * @throws HmsException
     */
    public ServerNodePowerStatus getServerNodePowerStatus( String node_id )
        throws HmsException
    {
        return getServerNodePowerStatusOOB( node_id );
    }

    /**
     * Get ServerNode OOB data
     *
     * @param node_id
     * @return ServerNode
     * @throws HmsException
     */
    public ServerNode getServerNodeOOBData( String node_id )
        throws HmsException
    {
        return getServerNodeOOB( node_id );
    }

    /**
     * Gets ServerNode from OOB
     *
     * @param node_id
     * @return
     * @throws HmsException
     */
    private ServerNode getServerNodeOOB( String node_id )
        throws HmsException
    {
        String path = Constants.HMS_OOB_HOST_INFO_ENDPOINT;
        path = path.replace( "{host_id}", node_id );

        ServerNode node = MonitoringUtil.getServerNodeOOB( path );
        return node;
    }

    /**
     * Get Switch Node OOB data
     *
     * @param switchId
     * @return ServerNode
     * @throws HmsException
     */
    public SwitchInfo getSwitchNodeOOBData( String switchId )
        throws HmsException
    {
        return getSwitchNodeOOB( switchId );
    }

    /**
     * Gets Switch Node from OOB
     *
     * @param node_id
     * @return
     * @throws HmsException
     */
    private SwitchInfo getSwitchNodeOOB( String switchId )
        throws HmsException
    {
        String path = Constants.HMS_OOB_SWITCH_INFO_ENDPOINT;
        path = path.replace( "{switch_id}", switchId );

        SwitchInfo switchInfo = MonitoringUtil.getSwitchNodeOOB( path );
        return switchInfo;
    }

    /**
     * Get OOB Logs
     *
     * @return
     * @throws HmsException
     */
    public byte[] getOOBLogs()
        throws HmsException
    {
        String path = Constants.HMS_OOB_LOGS_ENDPOINT;

        byte[] oobLogs = MonitoringUtil.getOobHmsLogs( path );
        return oobLogs;
    }

    /**
     * Deletes the temporary OOB log files
     *
     * @throws HmsException
     */
    public void deleteTemporaryOOBLogFiles()
        throws HmsException
    {
        String path = Constants.HMS_OOB_LOGS_ENDPOINT;

        MonitoringUtil.deleteTemporaryOOBHmsLogFile( path );

    }

    /**
     * Gets ServerNodePowerStatus from OOB
     *
     * @param node_id
     * @return
     * @throws HmsException
     */
    private ServerNodePowerStatus getServerNodePowerStatusOOB( String node_id )
        throws HmsException
    {
        String path = Constants.HMS_OOB_HOST_POWER_STATUS_ENDPOINT;
        path = path.replace( "{host_id}", node_id );

        ServerNodePowerStatus status = MonitoringUtil.getServerNodePowerStatusOOB( path );
        return status;
    }

    /**
     * Checks for component data for specified node which is not available OOB, Retrieves from IB Service and sets same
     * in passed node object.
     *
     * @param node
     */
    private void aggregateInBandData( ServerNode node )
    {
        for ( ServerComponent component : ServerComponent.values() )
        {
            if ( !AggregatorUtil.isComponentAvilableOOB( node, component ) )
            {
                try
                {
                    AggregatorUtil.getServerComponentIB( node, component );
                }
                catch ( Exception e )
                {
                    // No action log Exception
                    logger.debug( "Cant get data Inband for component : " + component, e );
                }
            }
        }
    }
}
