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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Logger logger = LoggerFactory.getLogger( HostUpDownEventAggregator.class );

    private static final Map<String, ServerNodePowerStatus> serverNodesPowerStatus =
        new ConcurrentHashMap<String, ServerNodePowerStatus>();

    private static final ReentrantLock serverUpDownEventUpdateLock = new ReentrantLock();

    public List<Event> getHostUpDownEvent( HmsNode hmsNode )
    {
        if ( hmsNode != null )
        {
            try
            {
                // Acquired the lock to update server up or down event status
                serverUpDownEventUpdateLock.lock();
                logger.info( "Acquired the lock to update server up or down event status for node : {}",
                             hmsNode.getNodeID() );
            }
            catch ( Exception e )
            {
                logger.error( String.format( "Lock Acquisition failed to update server up or down event status for node : %s",
                                             hmsNode.getNodeID() ),
                              e );
                return new ArrayList<Event>();
            }

            try
            {
                HostDataAggregator aggregator = new HostDataAggregator();

                String hostID = hmsNode.getNodeID();
                ServerNodePowerStatus serverNodePowerStatus = aggregator.getServerNodePowerStatus( hostID );

                return poppulateHostUpDownEvent( hmsNode, serverNodePowerStatus );
            }
            catch ( Exception e )
            {
                logger.error( "HMS aggregator Error while getting Event Host Up Down for Node:" + hmsNode.getNodeID(),
                              e );
            }
            finally
            {
                // release the acquired lock for other thread to update server up or down event status
                serverUpDownEventUpdateLock.unlock();
                logger.info( "Released the lock for other thread to update server up or down event status." );
            }
        }

        return null;
    }

    /**
     * Compare old and new power status to determine if event needs to be generate or not. We will generate event only
     * if previous and new power status are different from each other.
     *
     * @param hmsNode
     * @param newPowerStatus
     * @return
     */
    public List<Event> poppulateHostUpDownEvent( HmsNode hmsNode, ServerNodePowerStatus newPowerStatus )
    {
        List<Event> events = null;
        if ( hmsNode != null )
        {
            String hostId = hmsNode.getNodeID();

            List<ServerComponentEvent> serverComponentEvents = poppulateHostUpDownEvent( hostId, newPowerStatus );

            hmsNode.addComponentSensorData( ServerComponent.SERVER, serverComponentEvents );
            return EventMonitoringSubscriptionHolder.getEventList( hmsNode, ServerComponent.SERVER );
        }
        else
        {
            events = new ArrayList<Event>();
        }

        return events;
    }

    /**
     * Compare old and new power status to determine if event needs to be generate or not. We will generate event only
     * if previous and new power status are different from each other.
     *
     * @param newPowerStatus
     * @param hostId
     * @return
     */
    private List<ServerComponentEvent> poppulateHostUpDownEvent( String hostId, ServerNodePowerStatus newPowerStatus )
    {
        try
        {
            // Acquired the lock to update server up or down event status
            serverUpDownEventUpdateLock.lock();
            logger.info( "poppulateHostUpDownEvent: Acquired the lock to update server up or down event status for node : {}.",
                         hostId );
        }
        catch ( Exception e )
        {
            logger.error( "poppulateHostUpDownEvent: Lock Acquisition failed to update server up or down event status.",
                          e );
            return new ArrayList<ServerComponentEvent>();
        }

        List<ServerComponentEvent> serverComponentEvents = new ArrayList<ServerComponentEvent>();
        try
        {
            ServerNodePowerStatus prevPowerStatus = getCachedServerPowerStatus( hostId );

            ServerComponentEvent hostUpDownEvent = null;
            boolean forceSendPowerStatusEvent = false;

            if ( prevPowerStatus == null )
            {
                prevPowerStatus = new ServerNodePowerStatus();
                forceSendPowerStatusEvent = true;
            }

            if ( newPowerStatus.isPowered() != prevPowerStatus.isPowered() || forceSendPowerStatusEvent )
            {
                if ( newPowerStatus.isPowered() )
                {
                    hostUpDownEvent = new ServerComponentEvent();

                    hostUpDownEvent.setEventName( NodeEvent.HOST_UP );
                    hostUpDownEvent.setDiscreteValue( "Host is Up" );
                    hostUpDownEvent.setComponentId( hostId );
                    hostUpDownEvent.setEventId( "Host Status" );
                    hostUpDownEvent.setUnit( EventUnitType.DISCRETE );
                    serverComponentEvents.add( hostUpDownEvent );
                }
                else
                {
                    hostUpDownEvent = new ServerComponentEvent();

                    hostUpDownEvent.setEventName( NodeEvent.HOST_DOWN );
                    hostUpDownEvent.setDiscreteValue( "Host is Down" );
                    hostUpDownEvent.setComponentId( hostId );
                    hostUpDownEvent.setEventId( "Host Status" );
                    hostUpDownEvent.setUnit( EventUnitType.DISCRETE );
                    serverComponentEvents.add( hostUpDownEvent );
                }
            }

            if ( serverComponentEvents.size() > 0 )
            {
                logger.debug( "HMS Generated the server up and down event: " + hostId + ": "
                    + hostUpDownEvent.getEventName() + ": Power Status" + ": " + newPowerStatus.isPowered() );
                serverNodesPowerStatus.put( hostId, newPowerStatus );
            }
        }
        finally
        {
            // release the acquired lock for other thread to update server up or down event status
            serverUpDownEventUpdateLock.unlock();
            logger.info( "poppulateHostUpDownEvent: Released the lock for other thread to update server up or down event status." );
        }

        return serverComponentEvents;
    }

    /**
     * @param hostID
     * @return
     */
    private static ServerNodePowerStatus getCachedServerPowerStatus( String hostID )
    {
        return serverNodesPowerStatus.get( hostID );
    }

}
