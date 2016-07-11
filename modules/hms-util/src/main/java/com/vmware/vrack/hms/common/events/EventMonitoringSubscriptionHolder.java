/* ********************************************************************************
 * EventMonitoringSubscriptionHolder.java
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
package com.vmware.vrack.hms.common.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Body;
import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.Header;
import com.vmware.vrack.common.event.enums.EventCatalog;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.common.event.enums.EventSeverity;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.EventsUtil;

/**
 * Singleton class to Hold Events monitoring Registrations/Subscriptions done by all subscribers for NMe and regular
 * subscribtions of Events.
 *
 * @author Yagnesh Chawda
 */
public class EventMonitoringSubscriptionHolder
{
    private static Logger logger = Logger.getLogger( EventMonitoringSubscriptionHolder.class );

    public static final String APP_TYPE = "subscriberId";

    public static final String NODE_ID = "nodeId";

    public static final String TARGET_ID = "targetId";

    private EventMonitoringSubscriptionHolder()
    {
    }

    static EventMonitoringSubscriptionHolder eventMonitoringSubscriptionHolder = null;

    private static volatile Map<String, EventMonitoringSubscription> eventMonitoringSubscriptionMap =
        new HashMap<String, EventMonitoringSubscription>();

    private static volatile Map<String, BaseEventMonitoringSubscription> nmeMonitoringSubscriptionMap =
        new HashMap<String, BaseEventMonitoringSubscription>();

    /**
     * Returns Singleton EventMonitoringSubscriptionHolder instance
     * 
     * @return
     */
    public static EventMonitoringSubscriptionHolder getInstance()
    {
        if ( eventMonitoringSubscriptionHolder == null )
        {
            eventMonitoringSubscriptionHolder = new EventMonitoringSubscriptionHolder();
        }
        return eventMonitoringSubscriptionHolder;
    }

    /**
     * Return EventRegistrationMap that contains all the subscribed Events
     * 
     * @return
     */
    public static Map<String, EventMonitoringSubscription> getEventMonitoringSubscriptionMap()
    {
        return eventMonitoringSubscriptionMap;
    }

    /**
     * Return EventRegistrationMap that contains all the Non Maskable Events, which will be subscribed for all apps
     * 
     * @return
     */
    public static Map<String, BaseEventMonitoringSubscription> getNmeMonitoringSubscriptionMap()
    {
        return nmeMonitoringSubscriptionMap;
    }

    /**
     * Adds a new entry to eventMonitoringSubsciptionMap with key as
     * [subscriberId||nodeId||component||componentTargetId].
     * 
     * @param eventMonitoringSubscription
     */
    public void addEventMonitoringSubscription( EventMonitoringSubscription eventMonitoringSubscription )
    {
        String key = null;
        if ( eventMonitoringSubscription != null )
        {
            try
            {
                key = getEventMonitoringSubscriptionKey( eventMonitoringSubscription.getSubscriberId(),
                                                         eventMonitoringSubscription.getNodeId(),
                                                         eventMonitoringSubscription.getComponent() );
            }
            catch ( IllegalArgumentException e )
            {
                logger.error( "Error while getting Event Monitoring Subscription Key." );
            }
        }
        if ( key != null )
        {
            eventMonitoringSubscriptionMap.put( key, eventMonitoringSubscription );
        }
    }

    /**
     * Removes an entry from eventMonitoringSubsciptionMap
     * 
     * @param eventMonitoringSubscription
     */
    public void removeEventMonitoringSubscription( EventMonitoringSubscription eventMonitoringSubscription )
    {
        String key = null;
        if ( eventMonitoringSubscription != null )
        {
            key = getEventMonitoringSubscriptionKey( eventMonitoringSubscription.getSubscriberId(),
                                                     eventMonitoringSubscription.getNodeId(),
                                                     eventMonitoringSubscription.getComponent() );
        }
        if ( key != null )
        {
            eventMonitoringSubscriptionMap.remove( key );
        }
    }

    /**
     * Adds Entry in NmeMap, for Non Maskable Events, with key as [highestKey in the Map + 1]
     * 
     * @param baseEventMonitoringSubscription
     */
    public void addNmeMonitoringSubscription( BaseEventMonitoringSubscription baseEventMonitoringSubscription )
    {
        String key = null;
        boolean alreadyAdded = false;
        if ( baseEventMonitoringSubscription != null )
        {
            for ( BaseEventMonitoringSubscription subscription : nmeMonitoringSubscriptionMap.values() )
            {
                if ( baseEventMonitoringSubscription.equals( subscription ) )
                {
                    alreadyAdded = true;
                    break;
                }
            }
            if ( !alreadyAdded )
            {
                key = getNmeEventKey();
            }
        }
        if ( key != null )
        {
            nmeMonitoringSubscriptionMap.put( key, baseEventMonitoringSubscription );
        }
    }

    /**
     * Returns Single EventMonitoringSubscription from eventMonitoringSubsciptionMap
     * 
     * @param key
     * @return
     */
    public EventMonitoringSubscription getEventMonitoringSubscription( String key )
    {
        return eventMonitoringSubscriptionMap.get( key );
    }

    /**
     * Returns Single BaseEventMonitoringSubscription from nmeMap
     * 
     * @param key
     * @return
     */
    public BaseEventMonitoringSubscription getNmeMonitoringSubscription( String key )
    {
        return nmeMonitoringSubscriptionMap.get( key );
    }

    /**
     * Generates Subscribe Event key on the basis of subscribeId, nodeId, component, target. Neither of them can be
     * empty, for successful key generation. Return format is [subscriberId||nodeId||component||componentTargetId]
     *
     * @param subscriberId
     * @param nodeId
     * @param component
     * @param target
     * @return
     */
    public static String getEventMonitoringSubscriptionKey( String subscriberId, String nodeId,
                                                            EventComponent component )
                                                                throws IllegalArgumentException
    {
        if ( EventComponent.HMS.equals( component ) )
        {
            nodeId = "";
        }
        if ( subscriberId != null && nodeId != null && component != null )
        {
            if ( !"".equals( subscriberId.trim() ) )
            {
                String key = subscriberId + "||" + nodeId + "||" + component.toString();
                return key;
            }
            else
            {
                throw new IllegalArgumentException( "Cannot create subscriber Event Key. SubscriberId:[" + subscriberId
                    + "] ,nodeId:[" + nodeId + "]" + "Component:[" + component + "]" );
            }
        }
        else
        {
            throw new IllegalArgumentException( "Cannot create subscriber Event Key. SubscriberId:[" + subscriberId
                + "] ,nodeId:[" + nodeId + "]" + "Component:[" + component + "]" );
        }
    }

    /**
     * Generates key for NmeMap on the basis of the Highest Key in the Map. Returns in the Format [HighestKey +1]
     * 
     * @return
     */
    public static String getNmeEventKey()
    {
        if ( !nmeMonitoringSubscriptionMap.isEmpty() )
        {
            int highestValue = 0;
            for ( String key : nmeMonitoringSubscriptionMap.keySet() )
            {
                int value = Integer.parseInt( key );
                highestValue = ( value > highestValue ) ? value : highestValue;
            }
            return String.valueOf( highestValue + 1 );
        }
        else
        {
            return String.valueOf( 1 );
        }
    }

    /**
     * Returns all subscribers for given node and component.
     * 
     * @param Node ID
     * @param Component ID
     * @param EventComponent
     * @return List<EventMonitoringSubscription>
     */
    public static List<EventMonitoringSubscription> getEventSubscriberList( String nodeId, EventComponent source )
    {
        List<EventMonitoringSubscription> subscribers = new ArrayList<EventMonitoringSubscription>();
        Collection<EventMonitoringSubscription> allSubscribers = eventMonitoringSubscriptionMap.values();
        Iterator<EventMonitoringSubscription> iterSubscriber = allSubscribers.iterator();
        while ( iterSubscriber.hasNext() )
        {
            EventMonitoringSubscription subscriber = iterSubscriber.next();
            if ( subscriber.getComponent() == source && subscriber.getNodeId().equalsIgnoreCase( nodeId ) )
                subscribers.add( subscriber );
        }
        return subscribers;
    }

    /**
     * Returns List of EventObject for given node and component.
     * 
     * @param AbstractServerComponent
     * @param Node ID
     * @return List<Event>
     */
    public static List<Event> getEventList( HmsNode node, ServerComponent component, boolean criticalEventOnly )
    {
        List<Event> events = new ArrayList<Event>();
        List<ServerComponentEvent> componentSensors;
        boolean isServer = ( node instanceof ServerNode );
        if ( criticalEventOnly )
            componentSensors = node.getCriticalComponentSensor( component );
        else
            componentSensors = node.getComponentSensor( component );
        if ( componentSensors != null )
        {
            for ( ServerComponentEvent sensor : componentSensors )
            {
                if ( sensor.getEventName().getEventID() == null )
                    continue;
                String description = sensor.getEventName().getEventID().getEventText();
                if ( description == null )
                    description = "";
                List<EventComponent> eventSourceList = sensor.getEventName().getEventID().getComponentList();
                Map<EventComponent, String> source = new HashMap<EventComponent, String>();
                if ( component.getEventComponent() != EventComponent.HMS )
                {
                    if ( isServer )
                    {
                        source.put( EventComponent.SERVER, node.getNodeID() );
                        description = description.replace( "{" + EventComponent.SERVER + "}", node.getNodeID() );
                    }
                    if ( component.getEventComponent() != null )
                    {
                        source.put( component.getEventComponent(), sensor.getComponentId() );
                    }
                }
                if ( ( eventSourceList != null ) && eventSourceList.size() > 0 )
                {
                    EventComponent eventComponent = eventSourceList.get( eventSourceList.size() - 1 );
                    // Below piece of code will be executed in all the cases
                    // except if the eventComponent is RACK
                    if ( !EventComponent.RACK.equals( eventComponent ) )
                    {
                        if ( sensor.getComponentId() != null )
                        {
                            description = description.replace( "{" + eventComponent + "}", sensor.getComponentId() );
                        }
                        else
                        {
                            description = description.replace( "{" + eventComponent + "}", "NA" );
                        }
                    }
                }
                Header header = new Header();
                header.setEventName( sensor.getEventName().getEventID() );
                header.setEventCategoryList( sensor.getEventName().getEventID().getCategoryList() );
                header.setSeverity( sensor.getEventName().getEventID().getSeverity() );
                header.setEventType( sensor.getEventName().getEventID().getEventType() );
                header.setAgent( Constants.HMS_EVENT_GENERATOR_ID );
                header.addComponentIdentifier( source );
                Body body = new Body();
                body.setData( sensor.toEventDataMap() );
                description = formatString( body.getData(), description );
                body.setDescription( description );
                Event event = new Event();
                event.setHeader( header );
                event.setBody( body );
                events.add( event );
            }
        }
        return events;
    }

    /**
     * Returns List of EventObject for given node and component for Switch
     *
     * @param node
     * @param component
     * @param criticalEventOnly
     * @return List<Event>
     */
    public static List<Event> getSwitchEventList( HmsNode node, SwitchComponentEnum component,
                                                  boolean criticalEventOnly )
    {
        List<Event> events = new ArrayList<Event>();
        List<ServerComponentEvent> componentSensors;
        if ( criticalEventOnly )
            componentSensors = node.getCriticalSwitchComponentSensor( component );
        else
            componentSensors = node.getSwitchComponentSensor( component );
        if ( componentSensors != null )
        {
            for ( ServerComponentEvent sensor : componentSensors )
            {
                if ( sensor.getEventName().getEventID() == null )
                    continue;
                String description = sensor.getEventName().getEventID().getEventText();
                if ( description == null )
                    description = "";
                List<EventComponent> eventSourceList = sensor.getEventName().getEventID().getComponentList();
                Map<EventComponent, String> source = new HashMap<EventComponent, String>();
                if ( component.getEventComponent() != EventComponent.HMS )
                {
                    source.put( EventComponent.SWITCH, node.getNodeID() );
                    description = description.replace( "{" + EventComponent.SWITCH + "}", node.getNodeID() );
                }
                if ( component.getEventComponent() != null )
                {
                    source.put( component.getEventComponent(), sensor.getComponentId() );
                }
                if ( ( eventSourceList != null ) && eventSourceList.size() > 0 )
                {
                    EventComponent eventComponent = eventSourceList.get( eventSourceList.size() - 1 );
                    if ( sensor.getComponentId() != null )
                    {
                        description = description.replace( "{" + eventComponent + "}", sensor.getComponentId() );
                    }
                    else
                    {
                        description = description.replace( "{" + eventComponent + "}", "NA" );
                    }
                }
                Header header = new Header();
                header.setEventName( sensor.getEventName().getEventID() );
                header.setEventCategoryList( sensor.getEventName().getEventID().getCategoryList() );
                header.setSeverity( sensor.getEventName().getEventID().getSeverity() );
                header.setEventType( sensor.getEventName().getEventID().getEventType() );
                header.setAgent( Constants.HMS_EVENT_GENERATOR_ID );
                header.addComponentIdentifier( source );
                Body body = new Body();
                body.setData( sensor.toEventDataMap() );
                description = formatString( body.getData(), description );
                body.setDescription( description );
                Event event = new Event();
                event.setHeader( header );
                event.setBody( body );
                events.add( event );
            }
        }
        return events;
    }

    public static List<Event> getCriticalEventsOnly( List<Event> events )
    {
        if ( events != null && !events.isEmpty() )
        {
            List<Event> criticalEvents = new ArrayList<Event>();
            for ( Event event : events )
            {
                if ( event.getHeader().getSeverity() == EventSeverity.CRITICAL
                    || event.getHeader().getSeverity() == EventSeverity.ERROR )
                {
                    criticalEvents.add( event );
                }
            }
            return criticalEvents;
        }
        else
        {
            String err = "Cannot filter Critical events because events list is either null or is Empty.";
            logger.error( err );
        }
        return null;
    }

    public static List<Event> getEventList( HmsNode node, ServerComponent component )
    {
        return getEventList( node, component, false );
    }

    public static List<Event> getSwitchEventList( HmsNode node, SwitchComponentEnum component )
    {
        return getSwitchEventList( node, component, false );
    }

    public static String formatString( Map<String, String> valueMap, String str )
    {
        for ( Map.Entry<String, String> e : valueMap.entrySet() )
        {
            if ( e != null )
            {
                str = str.replace( "{" + e.getKey() + "}", ( e.getValue() != null ) ? e.getValue() : "" );
            }
        }
        return str;
    }

    /**
     * Returns List of Subscriptions for a particular Event. Lets say 3 Subscribers Subscribed for the events
     * 
     * @param nodeId
     * @param source
     * @param componentTarget
     * @return
     */
    public static List<EventMonitoringSubscription> getSubscribers( String nodeId, EventComponent source,
                                                                    String componentTarget )
    {
        List<EventMonitoringSubscription> subscribedEvents = new ArrayList<EventMonitoringSubscription>();
        for ( EventMonitoringSubscription eventSub : eventMonitoringSubscriptionMap.values() )
        {
            // Check if nodeID is not empty, if it is empty it means caller wants Events for all Nodes with the
            // specified source and target
            String subsNodeId = eventSub.getNodeId();
            if ( nodeId == null || ( nodeId != null && nodeId.equals( subsNodeId ) ) )
            {
                // Here will again check if the Component is not empty.
                // If it is empty, this again implies the same thing,
                // as caller want Subscribers for all components with the specified target Id
                EventComponent subsComponent = eventSub.getComponent();
                if ( source == null || ( source != null && source.equals( subsComponent ) ) )
                {
                    subscribedEvents.add( eventSub );
                }
            }
        }
        return subscribedEvents;
    }

    /**
     * Returns the list of filtered events for each Event Subscription
     * 
     * @param subscription
     * @param totalEvents
     * @return
     */
    private static List<Event> getFilteredEvents( EventMonitoringSubscription subscription, List<Event> totalEvents )
    {
        // Event List that will be the filtered list of events from totalevents
        List<Event> filteredEvents = new ArrayList<Event>();
        if ( subscription != null && totalEvents != null )
        {
            String subsNodeId = subscription.getNodeId();
            EventComponent subsComponent = subscription.getComponent();
            // Iterate through the list of the events
            for ( Event event : totalEvents )
            {
                Header eventHeader = event.getHeader();
                if ( eventHeader != null && eventHeader.getComponentIdentifier() != null )
                {
                    String eventNodeId = null;
                    EventComponent eventComponent = null;
                    // Required to get the nodeId , Component and the Component Target Id
                    for ( EventComponent key : eventHeader.getComponentIdentifier().keySet() )
                    {
                        // For example for Event Component CPU Events: componentIdentifiers are {SERVER and CPU}.
                        // So eventNodeId is value against the SERVER. The eventComponent is CPU.
                        // For Example for Event Component SERVER Events: componentIdentifier is {SERVER}.
                        // So eventNodeId is value against the SERVER. The eventComponent is SERVER.
                        // For Example for Event Component SWITCH Events: componentIdentifier is {SWITCH}.
                        // So eventNodeId is value against the SWITCH.The eventComponent is SWITCH.
                        if ( EventComponent.SERVER.equals( key ) || EventComponent.SWITCH.equals( key ) )
                        {
                            eventNodeId = eventHeader.getComponentIdentifier().get( key );
                            if ( eventComponent == null )
                            {
                                eventComponent = key;
                            }
                        }
                        else
                        {
                            eventComponent = key;
                            eventHeader.getComponentIdentifier().get( key );
                        }
                    }
                    EventCatalog eventName = eventHeader.getEventName();
                    // Now we have event's required info to map it with Events Registration
                    // Check if the subscription node Id matches with the one in the events.
                    logger.debug( "subNodeId:" + subsNodeId + "::subsComponent: " + subsComponent + "::eventComponent:"
                        + eventComponent + "::eventName:" + eventName );
                    if ( subsNodeId == null || ( subsNodeId != null && subsNodeId.equals( eventNodeId ) ) )
                    {
                        if ( subsComponent == null
                            || ( subsComponent != null && subsComponent.equals( eventComponent ) )
                            || EventsUtil.isHmsSupportedEvent( eventName ) )
                        {
                            filteredEvents.add( event );
                        }
                    }
                }
                else
                {
                    logger.error( "Event Header and event source Should not be null" );
                }
            }
        }
        else
        {
            logger.error( "Either EventMonitoringSubscription object or events List is empty." );
        }
        return filteredEvents;
    }

    /**
     * Filters the given list of events, and group all events that to be sent at same endpoint with same request method.
     * 
     * @param totalEvents
     * @return
     * @throws HmsException
     */
    public static Map<BaseEventMonitoringSubscription, List<Event>> getFilteredEvents( List<Event> totalEvents )
        throws HmsException
    {
        if ( totalEvents == null )
        {
            logger.error( "Cannot get filtered Events, as the events list to be filtered is null" );
            throw new HmsException( "Cannot get filtered Events, as the events list to be filtered is null" );
        }
        Map<BaseEventMonitoringSubscription, List<Event>> filteredMap =
            new HashMap<BaseEventMonitoringSubscription, List<Event>>();
        for ( EventMonitoringSubscription eventSubscription : eventMonitoringSubscriptionMap.values() )
        {
            // Null checks will be done in the following functions
            List<Event> filteredEvent = getFilteredEvents( eventSubscription, totalEvents );
            // If filtered events list is empty there is no point going forward
            if ( !filteredEvent.isEmpty() )
            {
                // Create a new BaseEventMonitoringSubscription object that will be used as key
                // Equals() and hashcode() are already overloaded , so there won't be any problem
                BaseEventMonitoringSubscription subscriberEndpointDetails = new BaseEventMonitoringSubscription();
                subscriberEndpointDetails.setRequestMethod( eventSubscription.getRequestMethod() );
                subscriberEndpointDetails.setNotificationEndpoint( eventSubscription.getNotificationEndpoint() );
                // If the subscriber endpoint is already present in the Hashmap,
                // it will just add the list received just now to the already in the hashmap
                if ( filteredMap.containsKey( subscriberEndpointDetails ) )
                {
                    List<Event> subEventList = filteredMap.get( subscriberEndpointDetails );
                    if ( subEventList == null )
                    {
                        subEventList = new ArrayList<Event>();
                    }
                    subEventList.addAll( filteredEvent );
                }
                else
                {
                    filteredMap.put( subscriberEndpointDetails, filteredEvent );
                }
            }
        }
        return filteredMap;
    }

    public static List<ServerComponent> getSupportedSensorServerComponnet( HmsNode node,
                                                                           IComponentEventInfoProvider provider )
    {
        List<ServerComponent> sensorComponents = new ArrayList<ServerComponent>();
        try
        {
            List<HmsApi> supportedAPI = provider.getSupportedHmsApi( node.getServiceObject() );
            for ( HmsApi api : supportedAPI )
            {
                switch ( api )
                {
                    case CPU_SENSOR_INFO:
                        sensorComponents.add( ServerComponent.CPU );
                        break;
                    case MEMORY_SENSOR_INFO:
                        sensorComponents.add( ServerComponent.MEMORY );
                        break;
                    case STORAGE_SENSOR_INFO:
                        sensorComponents.add( ServerComponent.STORAGE );
                        break;
                    case FAN_SENSOR_INFO:
                        sensorComponents.add( ServerComponent.FAN );
                        break;
                    case NIC_SENSOR_INFO:
                        sensorComponents.add( ServerComponent.NIC );
                        break;
                    case POWERUNIT_SENSOR_INFO:
                        sensorComponents.add( ServerComponent.POWERUNIT );
                        break;
                    case SYSTEM_SENSOR_INFO:
                        sensorComponents.add( ServerComponent.SYSTEM );
                        break;
                    case STORAGE_CONTROLLER_SENSOR_INFO:
                        sensorComponents.add( ServerComponent.STORAGE_CONTROLLER );
                        break;
                }
            }
        }
        catch ( HmsException e )
        {
            logger.info( "error generating sensor componnets supported by board.", e );
        }
        return sensorComponents;
    }

    public static ServerComponent getMappedServerComponents( EventComponent source )
    {
        for ( ServerComponent comp : ServerComponent.values() )
        {
            if ( comp.getEventComponent() == source )
                return comp;
        }
        return null;
    }

    public static SwitchComponentEnum getMappedSwitchComponents( EventComponent source )
    {
        for ( SwitchComponentEnum comp : SwitchComponentEnum.values() )
        {
            if ( comp.getEventComponent() == source )
                return comp;
        }
        return null;
    }
}
