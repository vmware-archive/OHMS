/* ********************************************************************************
 * HostDataAggregator.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vmware.vrack.hms.aggregator.util.AggregatorUtil;
import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.aggregator.util.ServerInfoHelperUtil;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodePowerStatus;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.inventory.ServerDataChangeMessage;

/**
 * @author sgakhar Purpose of this Class is to aggregate inband and out of band host data/Server Node Object.
 */
@Component
public class HostDataAggregator
{
    @Autowired
    private ApplicationContext context;

    private static Logger logger = Logger.getLogger( HostDataAggregator.class );

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
        aggregteInBandData( node );
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
        ServerInfoHelperUtil serverInfoHelperUtil = new ServerInfoHelperUtil();
        ServerNode node = getServerNodeOOB( node_id );
        if ( node != null && ( "true".equalsIgnoreCase( node.getOperationalStatus() ) ) )
        {
            aggregteInBandData( node );
        }
        serverInfo = serverInfoHelperUtil.convertServerNodeToServerInfo( node );
        context.publishEvent( new ServerDataChangeMessage( serverInfo, ServerComponent.SERVER ) );
        return serverInfo;
    }

    /**
     * gets HOST ServerNode aggregated from OOB and IB componnet data
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
    private void aggregteInBandData( ServerNode node )
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
