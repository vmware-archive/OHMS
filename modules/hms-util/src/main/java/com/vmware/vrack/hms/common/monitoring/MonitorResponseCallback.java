/* ********************************************************************************
 * MonitorResponseCallback.java
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
package com.vmware.vrack.hms.common.monitoring;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.events.BaseEventMonitoringSubscription;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscription;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.util.EventsUtil;

public class MonitorResponseCallback
{
    private static Logger logger = Logger.getLogger( MonitorResponseCallback.class );

    public void callbackEventSubcribers( HmsNode node, List<ServerComponent> components )
    {
        for ( ServerComponent component : components )
        {
            List<EventMonitoringSubscription> subscribers =
                EventMonitoringSubscriptionHolder.getEventSubscriberList( node.getNodeID(),
                                                                          component.getEventComponent() );
            List<Event> events = EventMonitoringSubscriptionHolder.getEventList( node, component );
            postCallBackRequest( subscribers, events );
            List<Event> criticalEvents = EventMonitoringSubscriptionHolder.getEventList( node, component, true );
            if ( criticalEvents != null && criticalEvents.size() > 0 )
            {
                EventsUtil.broadcastNmeEvents( criticalEvents );
            }
        }
    }

    public void callbackSwitchEventSubcribers( HmsNode node, List<SwitchComponentEnum> switchComponents )
    {
        for ( SwitchComponentEnum component : switchComponents )
        {
            List<EventMonitoringSubscription> subscribers =
                EventMonitoringSubscriptionHolder.getEventSubscriberList( node.getNodeID(),
                                                                          component.getEventComponent() );
            List<Event> events = EventMonitoringSubscriptionHolder.getSwitchEventList( node, component );
            postCallBackRequest( subscribers, events );
            List<Event> criticalEvents = EventMonitoringSubscriptionHolder.getSwitchEventList( node, component, true );
            if ( criticalEvents != null && criticalEvents.size() > 0 )
            {
                EventsUtil.broadcastNmeEvents( criticalEvents );
            }
        }
    }

    public void postCallBackRequest( List<EventMonitoringSubscription> subscribers, List<Event> events )
    {
        for ( EventMonitoringSubscription subscriber : subscribers )
        {
            EventsUtil.broadcastEvents( events, subscriber );
        }
    }

    /**
     * Send Events to specific target if Subscriptions are done for those events monitoring
     *
     * @param node
     * @param Events
     */
    public void callbackEventSubcribersUsingEvents( HmsNode node, List<Event> events )
    {
        try
        {
            // Get Map holding URL and corresponding events to be sent there (Maskable)
            Map<BaseEventMonitoringSubscription, List<Event>> eventsToBesent =
                EventMonitoringSubscriptionHolder.getFilteredEvents( events );
            if ( eventsToBesent != null )
            {
                for ( BaseEventMonitoringSubscription bms : eventsToBesent.keySet() )
                {
                    List<Event> curEvents = eventsToBesent.get( bms );
                    if ( curEvents != null && !curEvents.isEmpty() )
                    {
                        EventsUtil.broadcastEvents( curEvents, bms );
                    }
                }
            }
            // Filter Non maskable events from the list of Events, if the events list contains any event that is non
            // maskable event,
            // broadcast it to all the non maskable event endpoints
            List<Event> criticalEvents = EventMonitoringSubscriptionHolder.getCriticalEventsOnly( events );
            if ( criticalEvents != null && !criticalEvents.isEmpty() )
            {
                EventsUtil.broadcastNmeEvents( criticalEvents );
            }
        }
        catch ( HmsException e )
        {
            logger.error( "Unable to broadcast events received from HMS-OOB Agent: ", e );
        }
    }
}
