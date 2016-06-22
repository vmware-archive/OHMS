/* ********************************************************************************
 * ManagementSwitchUpDownEventHelper.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.switches;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switchnodes.api.HMSSwitchNode;
import com.vmware.vrack.hms.inventory.InventoryLoader;

/**
 * Helper class to generate the MANAGEMENT switch UP/DOWN Events.
 */
public class ManagementSwitchUpDownEventHelper
{
    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( ManagementSwitchUpDownEventHelper.class );

    /** The Constant HMS_SWITCH_CONNECTION_TIMEOUT. */
    private static final int HMS_SWITCH_CONNECTION_TIMEOUT = 20000;

    /**
     * method to get MANAGEMENT Switch up or down events.
     *
     * @param switchId the switch id
     * @return List<Event>
     */
    public static List<Event> getManagementSwitchUpDownEvent( String switchId )
    {
        if ( StringUtils.isBlank( switchId ) )
        {
            logger.debug( "Switch ID is either null or blank.", switchId );
            return null;
        }
        boolean reachable = false;
        String ipAddress = InventoryLoader.getInstance().getHmsIpAddr();
        try
        {
            InetAddress iNetAddress = Inet4Address.getByName( ipAddress );
            reachable = iNetAddress.isReachable( HMS_SWITCH_CONNECTION_TIMEOUT );
        }
        catch ( UnknownHostException e )
        {
            logger.error( "Error while getting Inet4Address for the host: {}.", ipAddress );
        }
        catch ( IOException e )
        {
            logger.error( "Error while trying to reach the host: {}.", ipAddress );
        }
        ServerComponentEvent serverComponentEvent = new ServerComponentEvent();
        serverComponentEvent.setComponentId( switchId );
        serverComponentEvent.setEventId( SwitchNode.SwitchRoleType.MANAGEMENT.toString() );
        serverComponentEvent.setUnit( EventUnitType.DISCRETE );
        if ( reachable )
        {
            serverComponentEvent.setEventName( NodeEvent.MANAGEMENT_SWITCH_UP );
            serverComponentEvent.setDiscreteValue( "Switch is up" );
        }
        else
        {
            serverComponentEvent.setEventName( NodeEvent.MANAGEMENT_SWITCH_DOWN );
            serverComponentEvent.setDiscreteValue( "Switch is Down" );
        }
        List<ServerComponentEvent> serverComponentEvents = new ArrayList<ServerComponentEvent>();
        serverComponentEvents.add( serverComponentEvent );
        HmsNode hmsNode = new HMSSwitchNode( switchId, ipAddress );
        hmsNode.addSwitchComponentSensorData( SwitchComponentEnum.SWITCH, serverComponentEvents );
        return EventMonitoringSubscriptionHolder.getSwitchEventList( hmsNode, SwitchComponentEnum.SWITCH );
    }
}
