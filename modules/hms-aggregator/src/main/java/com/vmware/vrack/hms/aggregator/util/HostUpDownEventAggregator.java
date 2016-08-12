/* ********************************************************************************
 * HostUpDownEventAggregator.java
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
package com.vmware.vrack.hms.aggregator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.aggregator.HostDataAggregator;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodePowerStatus;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;

/*
* Helps to generate the HMS monitoring event Host UP or DOWN
* Generate events only if there is a change in component status meaning power status change based on that event will be generated.
*/
public class HostUpDownEventAggregator
{
    private static Logger logger = Logger.getLogger( HostUpDownEventAggregator.class );

    private static final Map<String, ServerNodePowerStatus> serverNodesPowerStatus =
        new ConcurrentHashMap<String, ServerNodePowerStatus>();

    private ReentrantLock serverUpDownEventUpdateLock = new ReentrantLock();

    public List<Event> getHostUpDownEvent( HmsNode hmsNode )
    {
        try
        {
            // Acquired the lock to update server up or down event status
            serverUpDownEventUpdateLock.lock();
            logger.info( String.format( "Acquired the lock to update server up or down event status." ) );
        }
        catch ( Exception e )
        {
            logger.error( "Lock Acquisition failed to update server up or down event status.", e );
            return new ArrayList<Event>();
        }
        try
        {
            List<ServerComponentEvent> serverComponentEvents = new ArrayList<ServerComponentEvent>();
            ServerComponentEvent hostUpDownEvent = new ServerComponentEvent();
            boolean forceSendPowerStatusEvent = false;
            HostDataAggregator aggregator = new HostDataAggregator();
            String hostID = hmsNode.getNodeID();
            ServerNodePowerStatus prevServerNodePowerStatus;
            prevServerNodePowerStatus = getServerPowerStatus( hostID );
            if ( prevServerNodePowerStatus == null )
            {
                prevServerNodePowerStatus = new ServerNodePowerStatus();
                forceSendPowerStatusEvent = true;
            }
            ServerNodePowerStatus serverNodePowerStatus = aggregator.getServerNodePowerStatus( hostID );
            if ( serverNodePowerStatus.isPowered() != prevServerNodePowerStatus.isPowered()
                || forceSendPowerStatusEvent )
            {
                if ( serverNodePowerStatus.isPowered() )
                {
                    hostUpDownEvent = new ServerComponentEvent();
                    hostUpDownEvent.setEventName( NodeEvent.HOST_UP );
                    hostUpDownEvent.setDiscreteValue( "Host is Up" );
                    hostUpDownEvent.setComponentId( hmsNode.getNodeID() );
                    hostUpDownEvent.setEventId( "Host Status" );
                    hostUpDownEvent.setUnit( EventUnitType.DISCRETE );
                    serverComponentEvents.add( hostUpDownEvent );
                }
                else
                {
                    hostUpDownEvent = new ServerComponentEvent();
                    hostUpDownEvent.setEventName( NodeEvent.HOST_DOWN );
                    hostUpDownEvent.setDiscreteValue( "Host is Down" );
                    hostUpDownEvent.setComponentId( hmsNode.getNodeID() );
                    hostUpDownEvent.setEventId( "Host Status" );
                    hostUpDownEvent.setUnit( EventUnitType.DISCRETE );
                    serverComponentEvents.add( hostUpDownEvent );
                }
                logger.debug( "HMS Generated the server up and down event: " + hmsNode.getNodeID() + ": "
                    + hostUpDownEvent.getEventName() + ": Power Status" + ": " + serverNodePowerStatus.isPowered() );
                serverNodesPowerStatus.put( hostID, serverNodePowerStatus );
            }
            hmsNode.addComponentSensorData( ServerComponent.SERVER, serverComponentEvents );
            return EventMonitoringSubscriptionHolder.getEventList( hmsNode, ServerComponent.SERVER );
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Event Host Up Down for Node:" + hmsNode.getNodeID(), e );
        }
        finally
        {
            // release the acquired lock for other thread to update server up or down event status
            serverUpDownEventUpdateLock.unlock();
            logger.info( "Released the lock for other thread to update server up or down event status." );
        }
        return null;
    }

    private static ServerNodePowerStatus getServerPowerStatus( String hostID )
    {
        return serverNodesPowerStatus.get( hostID );
    }
}
