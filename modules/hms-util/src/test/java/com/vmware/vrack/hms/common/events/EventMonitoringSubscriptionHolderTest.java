/* ********************************************************************************
 * EventMonitoringSubscriptionHolderTest.java
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
package com.vmware.vrack.hms.common.events;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.vmware.vrack.common.event.Body;
import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.Header;
import com.vmware.vrack.common.event.enums.EventCatalog;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.common.event.enums.EventSeverity;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;

public class EventMonitoringSubscriptionHolderTest
{
    private static Logger logger = Logger.getLogger( EventMonitoringSubscriptionHolderTest.class );

    @Test
    public void test()
    {
        logger.info( "Testing EventMonitoringSubscriptionHolderTest" );
        try
        {
            EventMonitoringSubscription eventMonitoringSubscription;
            ServerNode node = new ServerNode();
            node.setManagementIp( "xx.xx.xx.xx" );
            node.setManagementUserName( "test" );
            node.setManagementUserPassword( "test" );
            HmsNode hmsnode = node;
            EventMonitoringSubscriptionHolder eventMonitoringSubscriptionHolder = null;
            eventMonitoringSubscription = new EventMonitoringSubscription();
            eventMonitoringSubscription.setNodeId( "testnode" );
            eventMonitoringSubscription.setSubscriberId( "123" );
            eventMonitoringSubscription.setComponent( EventComponent.CPU );
            eventMonitoringSubscriptionHolder = EventMonitoringSubscriptionHolder.getInstance();
            // Add the event monitoring subscription
            eventMonitoringSubscriptionHolder.addEventMonitoringSubscription( eventMonitoringSubscription );
            // Adds Entry in Nme monitoring
            eventMonitoringSubscriptionHolder.addNmeMonitoringSubscription( eventMonitoringSubscription );
            String key =
                EventMonitoringSubscriptionHolder.getEventMonitoringSubscriptionKey( eventMonitoringSubscription.getSubscriberId(),
                                                                                     eventMonitoringSubscription.getNodeId(),
                                                                                     eventMonitoringSubscription.getComponent() );
            assertNotNull( key );
            eventMonitoringSubscription = new EventMonitoringSubscription();
            eventMonitoringSubscription = eventMonitoringSubscriptionHolder.getEventMonitoringSubscription( key );
            assertNotNull( eventMonitoringSubscription.getNodeId() );
            assertNotNull( eventMonitoringSubscription.getComponent() );
            assertNotNull( eventMonitoringSubscription.getSubscriberId() );
            List<EventMonitoringSubscription> list;
            list =
                EventMonitoringSubscriptionHolder.getEventSubscriberList( eventMonitoringSubscription.getNodeId(),
                                                                          eventMonitoringSubscription.getComponent() );
            for ( int i = 0; i < list.size(); i++ )
            {
                assertNotNull( list.get( i ).getNodeId() );
                assertNotNull( list.get( i ).getComponent() );
                assertNotNull( list.get( i ).getSubscriberId() );
            }
            List<Event> listEvent = new ArrayList<Event>();
            listEvent = EventMonitoringSubscriptionHolder.getEventList( hmsnode, ServerComponent.CPU );
            for ( int j = 0; j < listEvent.size(); j++ )
            {
                assertNotNull( listEvent.get( j ).getBody().getDescription() );
                assertNotNull( listEvent.get( j ).getHeader().getEventName() );
            }
            listEvent = EventMonitoringSubscriptionHolder.getSwitchEventList( hmsnode, SwitchComponentEnum.SWITCH );
            for ( int j = 0; j < listEvent.size(); j++ )
            {
                assertNotNull( listEvent.get( j ).getBody().getDescription() );
                assertNotNull( listEvent.get( j ).getHeader().getEventName() );
            }
            List<Event> events = new ArrayList<Event>();
            Event event = new Event();
            Body body = new Body();
            Header header = new Header();
            // Adding dummy Event 1
            body.setDescription( "CPU for rack EVO:RACK node N5 and CPU processor 1 has shutdown due to POST Failure." );
            header.setAgent( "HMS" );
            header.setEventName( EventCatalog.CPU_POST_FAILURE );
            header.setSeverity( EventSeverity.CRITICAL );
            header.setVersion( "1.0" );
            event.setBody( body );
            event.setHeader( header );
            events.add( event );
            event = new Event();
            body = new Body();
            header = new Header();
            // Adding dummy Event 2
            body.setDescription( "Memory for rack EVO:RACK node N6 and Memory Bank Label A slot 1 has uncorrectable ECC error detected" );
            header.setAgent( "HMS" );
            header.setEventName( EventCatalog.DIMM_ECC_ERROR );
            header.setSeverity( EventSeverity.ERROR );
            header.setVersion( "1.0" );
            event.setBody( body );
            event.setHeader( header );
            events.add( event );
            // Get the critical events only
            List<Event> eventsCritical = new ArrayList<Event>();
            eventsCritical = EventMonitoringSubscriptionHolder.getCriticalEventsOnly( events );
            for ( int i = 0; i < eventsCritical.size(); i++ )
            {
                assertNotNull( eventsCritical.get( i ).getBody().getDescription() );
                assertNotNull( eventsCritical.get( i ).getHeader().getAgent() );
                assertNotNull( eventsCritical.get( i ).getHeader().getEventName() );
                assertNotNull( eventsCritical.get( i ).getHeader().getSeverity() );
            }
            // Get list of subscribers
            List<EventMonitoringSubscription> subscribedEvents = new ArrayList<EventMonitoringSubscription>();
            subscribedEvents =
                EventMonitoringSubscriptionHolder.getSubscribers( eventMonitoringSubscription.getNodeId(),
                                                                  EventComponent.CPU, "CPU" );
            for ( int i = 0; i < subscribedEvents.size(); i++ )
            {
                assertNotNull( subscribedEvents.get( i ).getComponent() );
                assertNotNull( subscribedEvents.get( i ).getNodeId() );
                assertNotNull( subscribedEvents.get( i ).getSubscriberId() );
            }
            // Filters the given list of events, and group all events that to be sent at same
            List<Event> listEvents = new ArrayList<Event>();
            Event singleEvent = new Event();
            Body eventBody = new Body();
            Header eventHeader = new Header();
            // Adding dummy Event 1
            Map<EventComponent, String> newComponent = new HashMap<EventComponent, String>();
            newComponent.put( EventComponent.CPU, "CPU1" );
            eventBody.setDescription( "CPU for rack EVO:RACK node N5 and CPU processor 1 has shutdown due to POST Failure." );
            eventHeader.setAgent( "HMS" );
            eventHeader.setEventName( EventCatalog.CPU_POST_FAILURE );
            eventHeader.setSeverity( EventSeverity.CRITICAL );
            eventHeader.setVersion( "1.0" );
            eventHeader.addComponentIdentifier( newComponent );
            singleEvent.setBody( eventBody );
            singleEvent.setHeader( eventHeader );
            listEvents.add( singleEvent );
            Map<BaseEventMonitoringSubscription, List<Event>> mapListEvents =
                new HashMap<BaseEventMonitoringSubscription, List<Event>>();
            mapListEvents = EventMonitoringSubscriptionHolder.getFilteredEvents( listEvents );
            assertNotNull( mapListEvents );
            // Delete the event monitoring subscription
            eventMonitoringSubscriptionHolder.removeEventMonitoringSubscription( eventMonitoringSubscription );
        }
        catch ( Exception e )
        {
            logger.info( "Test EventMonitoringSubscriptionHolderTest Failed!" );
            e.printStackTrace();
        }
    }
}
