/* ********************************************************************************
 * EventFilterService.java
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
package com.vmware.vrack.hms.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.Header;
import com.vmware.vrack.common.event.enums.EventCatalog;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;

/**
 * Class to hold old events for each node's all components and massage them before sent to vrm-prm
 *
 * @author vmware
 */
public class EventFilterService
{
    private static Logger logger = Logger.getLogger( EventFilterService.class );

    // Map<nodeId, Map<ServerComponent, List<Event>>> to hold old events for each nde's all components
    private static final Map<String, Map<ServerComponent, List<Event>>> allNodesComponentEventsMap =
        new ConcurrentHashMap<String, Map<ServerComponent, List<Event>>>();

    private static final Map<String, Map<SwitchComponentEnum, List<Event>>> switchNodesComponentEventsMap =
        new ConcurrentHashMap<String, Map<SwitchComponentEnum, List<Event>>>();

    private static final Map<ServerComponent, Map<String, List<Event>>> hmsEventsMap =
        new ConcurrentHashMap<ServerComponent, Map<String, List<Event>>>();

    private static final String HMS_AGENT_STATUS = "HMS_AGENT_STATUS";

    /**
     * @param nodeId
     * @param serverComponent
     * @param newEvents
     * @return List<Event>
     */
    @SuppressWarnings( "deprecation" )
    public static List<Event> filterOrMassageEvents( String nodeId, ServerComponent serverComponent,
                                                     List<Event> newEvents )
        throws HmsException
    {
        List<Event> massagedEvents = null;
        synchronized ( nodeId )
        {
            massagedEvents = newEvents;
            boolean isHmsCase = false;
            boolean isFilterOn = false;
            if ( serverComponent != null )
            {
                switch ( serverComponent )
                {
                    case STORAGE_CONTROLLER:
                    {
                        massagedEvents = filterStorageControllerEvents( nodeId, serverComponent, newEvents );
                        isFilterOn = true;
                        break;
                    }
                    case NIC:
                    {
                        massagedEvents = filterNicEvents( nodeId, serverComponent, newEvents );
                        isFilterOn = true;
                        break;
                    }
                    case STORAGE:
                    {
                        massagedEvents = filterStorageEvents( nodeId, serverComponent, newEvents );
                        isFilterOn = true;
                        break;
                    }
                    case HMS:
                    {
                        massagedEvents = filterHmsAgentEvents( serverComponent, newEvents );
                        isHmsCase = true;
                        break;
                    }
                    default:
                        break;
                }
            }
            // Put newEvents in componentEventStatusMap, to refer for next call.
            // called because of the Monitoring + onDemand events call over rest
            if ( isHmsCase )
            {
                putEventsToComponentStatusMap( serverComponent, newEvents );
            }
            else if ( isFilterOn )
            {
                putEventsToComponentStatusMap( nodeId, serverComponent, newEvents );
            }
        }
        return massagedEvents;
    }

    /**
     * @param nodeId
     * @param switchComponent
     * @param newEvents
     * @return List<Event>
     * @throws HmsException
     */
    public static List<Event> filterOrMassageSwitchEvents( String nodeId, SwitchComponentEnum switchComponent,
                                                           List<Event> newEvents )
        throws HmsException
    {
        List<Event> massagedEvents = newEvents;
        boolean isFilterOn = false;
        if ( switchComponent != null )
        {
            switch ( switchComponent )
            {
                case SWITCH:
                {
                    massagedEvents = filterSwitchUpDownEvents( nodeId, switchComponent, newEvents );
                    isFilterOn = true;
                    break;
                }
                case SWITCH_PORT:
                {
                    massagedEvents = filterSwitchPortUpDownEvents( nodeId, switchComponent, newEvents );
                    isFilterOn = true;
                    break;
                }
                default:
                    break;
            }
        }
        if ( isFilterOn )
            putSwitchEventsToComponentStatusMap( nodeId, switchComponent, newEvents );
        return massagedEvents;
    }

    /**
     * @param nodeId
     * @param serverComponent
     * @param newEvents
     * @return
     */
    private static List<Event> filterNicEvents( String nodeId, ServerComponent serverComponent, List<Event> newEvents )
    {
        List<Event> nicEventsAgrregated = null;
        try
        {
            logger.debug( "nodeId: " + nodeId + ", serverComponent: " + serverComponent );
            List<Event> oldEvents = getExistingEventsForComponentInNode( nodeId, serverComponent );
            if ( oldEvents != null && newEvents != null )
            {
                nicEventsAgrregated = new ArrayList<Event>();
                Header newEventHeader = null;
                Map<EventComponent, String> newEventComponentIdentifier = null;
                Map<String, String> newEventData = null;
                EventCatalog newEventCatalog = null;
                Header oldEventHeader = null;
                Map<EventComponent, String> oldEventComponentIdentifier = null;
                Map<String, String> oldEventData = null;
                EventCatalog oldEventCatalog = null;
                for ( Event newEvent : newEvents )
                {
                    newEventHeader = newEvent.getHeader();
                    newEventComponentIdentifier = newEvent.getHeader().getComponentIdentifier();
                    newEventData = newEvent.getBody().getData();
                    newEventCatalog = newEventHeader.getEventName();
                    // flag is set to true if we find any matching for the NIC Component in the oldEvents as well as the
                    // newEvents.
                    // Flag to handle if the new NIC added.
                    boolean flag = false;
                    if ( ( newEventCatalog == EventCatalog.NIC_PORT_UP )
                        || ( newEventCatalog == EventCatalog.NIC_PORT_DOWN ) )
                    {
                        String newEventFruId = newEventComponentIdentifier.get( EventComponent.NIC );
                        logger.debug( "New NIC port status for node: " + nodeId + ", " + newEventFruId + ": "
                            + newEventCatalog );
                        for ( Event oldEvent : oldEvents )
                        {
                            oldEventHeader = oldEvent.getHeader();
                            oldEventComponentIdentifier = oldEvent.getHeader().getComponentIdentifier();
                            oldEventData = oldEvent.getBody().getData();
                            String oldEventFruId = oldEventComponentIdentifier.get( EventComponent.NIC );
                            if ( newEventFruId != null && newEventFruId.equals( oldEventFruId ) )
                            {
                                flag = true;
                                oldEventCatalog = oldEventHeader.getEventName();
                                logger.debug( "old & new NIC port status for node: " + nodeId + ", " + oldEventFruId
                                    + ": " + newEventFruId );
                                if ( ( oldEventCatalog == EventCatalog.NIC_PORT_UP )
                                    || ( oldEventCatalog == EventCatalog.NIC_PORT_DOWN ) )
                                {
                                    logger.debug( "event data b/w old & new: " + oldEventData.get( "value" ) + " : "
                                        + newEventData.get( "value" ) );
                                    if ( newEventData.get( "value" ).equals( oldEventData.get( "value" ) ) )
                                    {
                                        // old and new port status for this NIC port is same, so we will not generate
                                        // new event
                                        continue;
                                    }
                                    else
                                    {
                                        logger.debug( "Adding NIC port status event unconditionally for node:" + nodeId
                                            + ", " + newEventFruId + ": " + newEventCatalog );
                                        nicEventsAgrregated.add( newEvent );
                                    }
                                }
                            }
                        }
                        if ( flag == false )
                            nicEventsAgrregated.add( newEvent );
                    }
                    else
                    {
                        nicEventsAgrregated.add( newEvent );
                    }
                }
            }
            else
            {
                // Because no past events are present, no need to do any massaging
                nicEventsAgrregated = newEvents;
            }
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Events for server component NIC: " + nodeId, e );
        }
        if ( nicEventsAgrregated == null )
        {
            // We will not return NULL, We will atleast return Empty Array
            nicEventsAgrregated = new ArrayList<Event>();
        }
        return nicEventsAgrregated;
    }

    /**
     * @param nodeId
     * @param serverComponent
     * @param newEvents
     * @return
     */
    private static List<Event> filterStorageControllerEvents( String nodeId, ServerComponent serverComponent,
                                                              List<Event> newEvents )
    {
        List<Event> storageControllerEventsAgrregated = null;
        try
        {
            List<Event> oldEvents = getExistingEventsForComponentInNode( nodeId, serverComponent );
            if ( oldEvents != null && newEvents != null )
            {
                storageControllerEventsAgrregated = new ArrayList<Event>();
                Header newEventHeader = null;
                Map<EventComponent, String> newEventComponentIdentifier = null;
                Map<String, String> newEventData = null;
                EventCatalog newEventCatalog = null;
                Header oldEventHeader = null;
                Map<EventComponent, String> oldEventComponentIdentifier = null;
                Map<String, String> oldData = null;
                EventCatalog oldEventCatalog = null;
                for ( Event newEvent : newEvents )
                {
                    newEventHeader = newEvent.getHeader();
                    newEventComponentIdentifier = newEvent.getHeader().getComponentIdentifier();
                    newEventCatalog = newEventHeader.getEventName();
                    // flag is set to true if we find any matching for the STORAGE_CONTROLLER Component in oldEvents as
                    // well as newEvents.
                    // Flag to handle if the new storage controller added.
                    boolean flag = false;
                    if ( ( newEventCatalog == EventCatalog.STORAGE_CONTROLLER_DOWN )
                        || ( newEventCatalog == EventCatalog.STORAGE_CONTROLLER_UP ) )
                    {
                        logger.debug( "HMS Storage controller operational status events are available for this node: "
                            + nodeId );
                        for ( Event oldEvent : oldEvents )
                        {
                            oldEventHeader = oldEvent.getHeader();
                            oldEventComponentIdentifier = oldEvent.getHeader().getComponentIdentifier();
                            if ( oldEventComponentIdentifier.get( EventComponent.STORAGE_CONTROLLER ).equals( newEventComponentIdentifier.get( EventComponent.STORAGE_CONTROLLER ) ) )
                            {
                                flag = true;
                                oldEventCatalog = oldEventHeader.getEventName();
                                if ( ( oldEventCatalog == EventCatalog.STORAGE_CONTROLLER_UP )
                                    || ( oldEventCatalog == EventCatalog.STORAGE_CONTROLLER_DOWN ) )
                                {
                                    newEventData = newEvent.getBody().getData();
                                    oldData = oldEvent.getBody().getData();
                                    logger.debug( "Getting Storage controller component Identifier for comparision for the node: "
                                        + nodeId );
                                    if ( newEventData.get( "value" ).equals( oldData.get( "value" ) ) )
                                    {
                                        // Status has NOT changed for Storage Controller, so no need to generate new
                                        // event.
                                        continue;
                                    }
                                    else
                                    {
                                        logger.debug( "Adding Storage Controller operational status event as the component status change found for the node: "
                                            + nodeId );
                                        storageControllerEventsAgrregated.add( newEvent );
                                    }
                                }
                            }
                        }
                        if ( flag == false )
                            storageControllerEventsAgrregated.add( newEvent );
                    }
                    else
                    {
                        storageControllerEventsAgrregated.add( newEvent );
                    }
                }
            }
            else
            {
                // Because no past events are present, no need to do any massaging
                storageControllerEventsAgrregated = newEvents;
            }
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Events for server component Storage Controller:" + nodeId,
                          e );
        }
        if ( storageControllerEventsAgrregated == null )
        {
            // We will not return NULL, We will atleast return Empty Array
            storageControllerEventsAgrregated = new ArrayList<Event>();
        }
        return storageControllerEventsAgrregated;
    }

    /**
     * Check if there is a change between the operational status from last and the current snapshot of the component
     * status for the STORAGE state change events
     *
     * @param nodeId
     * @param serverComponent
     * @param newEvents
     * @return
     */
    private static List<Event> filterStorageEvents( String nodeId, ServerComponent serverComponent,
                                                    List<Event> newEvents )
    {
        List<Event> storageEventsAgrregated = null;
        try
        {
            List<Event> oldEvents = getExistingEventsForComponentInNode( nodeId, serverComponent );
            if ( oldEvents != null && newEvents != null )
            {
                storageEventsAgrregated = new ArrayList<Event>();
                Header newEventHeader = null;
                Map<EventComponent, String> newEventComponentIdentifier = null;
                Map<String, String> newEventData = null;
                EventCatalog newEventCatalog = null;
                Header oldEventHeader = null;
                Map<EventComponent, String> oldEventComponentIdentifier = null;
                Map<String, String> oldData = null;
                EventCatalog oldEventCatalog = null;
                for ( Event newEvent : newEvents )
                {
                    newEventHeader = newEvent.getHeader();
                    newEventComponentIdentifier = newEvent.getHeader().getComponentIdentifier();
                    newEventCatalog = newEventHeader.getEventName();
                    // flag is set to true if we find any matching for the STORAGE Component in oldEvents as well as
                    // newEvents.
                    // Flag to handle if the new storage disk added/inserted.
                    boolean flag = false;
                    if ( ( newEventCatalog == EventCatalog.HDD_UP ) || ( newEventCatalog == EventCatalog.HDD_DOWN )
                        || ( newEventCatalog == EventCatalog.SSD_UP ) || ( newEventCatalog == EventCatalog.SSD_DOWN ) )
                    {
                        logger.debug( "HMS Storage operational status events are available for this node: " + nodeId );
                        for ( Event oldEvent : oldEvents )
                        {
                            oldEventHeader = oldEvent.getHeader();
                            oldEventComponentIdentifier = oldEvent.getHeader().getComponentIdentifier();
                            logger.debug( "Getting Storage component Identifier for comparision for the node: "
                                + nodeId );
                            if ( oldEventComponentIdentifier.get( EventComponent.STORAGE ).equals( newEventComponentIdentifier.get( EventComponent.STORAGE ) ) )
                            {
                                oldEventCatalog = oldEventHeader.getEventName();
                                flag = true;
                                if ( ( oldEventCatalog == EventCatalog.HDD_UP )
                                    || ( oldEventCatalog == EventCatalog.HDD_DOWN )
                                    || ( oldEventCatalog == EventCatalog.SSD_UP )
                                    || ( oldEventCatalog == EventCatalog.SSD_DOWN ) )
                                {
                                    newEventData = newEvent.getBody().getData();
                                    oldData = oldEvent.getBody().getData();
                                    logger.debug( "Getting Storage operational status for comparision for the node: "
                                        + nodeId );
                                    if ( newEventData.get( "value" ).equals( oldData.get( "value" ) ) )
                                    {
                                        // Status has NOT changed for Storage Events, so no need to generate new event.
                                        continue;
                                    }
                                    else
                                    {
                                        logger.debug( "Adding Storage operational status event as the component status change found for the node: "
                                            + nodeId );
                                        storageEventsAgrregated.add( newEvent );
                                    }
                                }
                            }
                        }
                        if ( flag == false )
                            storageEventsAgrregated.add( newEvent );
                    }
                    else
                    {
                        storageEventsAgrregated.add( newEvent );
                    }
                }
            }
            else
            {
                // Because no old events are present, no need to do any filter or massaging
                storageEventsAgrregated = newEvents;
            }
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Events for server component Storage :" + nodeId, e );
        }
        if ( storageEventsAgrregated == null )
        {
            // We will not return NULL, We will atleast return Empty Array
            storageEventsAgrregated = new ArrayList<Event>();
        }
        return storageEventsAgrregated;
    }

    /**
     * Check if there is a change between the operational status from last and the current snapshot of the component
     * status for the HMS state change events
     *
     * @param serverComponent
     * @param newEvents
     * @return
     */
    private static List<Event> filterHmsAgentEvents( ServerComponent serverComponent, List<Event> newEvents )
    {
        List<Event> hmsEventsAgrregated = null;
        try
        {
            List<Event> oldEvents = getExistingEventsForHms( serverComponent );
            if ( oldEvents != null && newEvents != null )
            {
                hmsEventsAgrregated = new ArrayList<Event>();
                Map<String, String> newEventData = null;
                Map<String, String> oldEventData = null;
                for ( Event newEvent : newEvents )
                {
                    if ( EventsUtil.isHmsSupportedEvent( newEvent ) )
                    {
                        for ( Event oldEvent : oldEvents )
                        {
                            if ( EventsUtil.isHmsSupportedEvent( oldEvent ) )
                            {
                                newEventData = newEvent.getBody().getData();
                                oldEventData = oldEvent.getBody().getData();
                                if ( newEventData != null && oldEventData != null )
                                {
                                    String newData = newEventData.get( "value" );
                                    String oldData = oldEventData.get( "value" );
                                    if ( newData != null && newData.equals( oldData ) )
                                    {
                                        // Status has NOT changed for HMS Events, so no need to generate new event.
                                        logger.debug( "component status change not found for HMS" );
                                        continue;
                                    }
                                    else
                                    {
                                        logger.debug( "Adding operational status event as the component status change found for HMS" );
                                        hmsEventsAgrregated.add( newEvent );
                                    }
                                }
                                else
                                {
                                    logger.debug( "Null data found- oldEventData:" + oldEventData + "::newEventData: "
                                        + newEventData );
                                }
                            }
                        }
                    }
                    else
                    {
                        hmsEventsAgrregated.add( newEvent );
                    }
                }
            }
            else
            {
                // Because no old events are present, no need to do any filter or massaging
                hmsEventsAgrregated = newEvents;
            }
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Events for HMS", e );
        }
        if ( hmsEventsAgrregated == null )
        {
            // We will not return NULL, We will atleast return Empty Array
            hmsEventsAgrregated = new ArrayList<Event>();
        }
        return hmsEventsAgrregated;
    }

    /**
     * Check if there is a change between the switch port status from last and the current snapshot of the switch port
     * status for the SWITCH_PORT state change events
     *
     * @param nodeId
     * @param serverComponent
     * @param newEvents
     * @return List<Event>
     */
    private static List<Event> filterSwitchPortUpDownEvents( String nodeId, SwitchComponentEnum switchComponent,
                                                             List<Event> newEvents )
    {
        List<Event> switchPortEventsAgrregated = null;
        try
        {
            List<Event> oldEvents = getExistingSwitchEventsForComponentInNode( nodeId, switchComponent );
            if ( oldEvents != null && newEvents != null )
            {
                switchPortEventsAgrregated = new ArrayList<Event>();
                Header newEventHeader = null;
                Map<EventComponent, String> newEventComponentIdentifier = null;
                Map<String, String> newEventData = null;
                EventCatalog newEventCatalog = null;
                Header oldEventHeader = null;
                Map<EventComponent, String> oldEventComponentIdentifier = null;
                Map<String, String> oldData = null;
                EventCatalog oldEventCatalog = null;
                for ( Event newEvent : newEvents )
                {
                    newEventHeader = newEvent.getHeader();
                    newEventComponentIdentifier = newEvent.getHeader().getComponentIdentifier();
                    newEventCatalog = newEventHeader.getEventName();
                    if ( ( newEventCatalog == EventCatalog.MANAGEMENT_SWITCH_PORT_UP )
                        || ( newEventCatalog == EventCatalog.MANAGEMENT_SWITCH_PORT_DOWN )
                        || ( newEventCatalog == EventCatalog.TOR_SWITCH_PORT_UP )
                        || ( newEventCatalog == EventCatalog.TOR_SWITCH_PORT_DOWN )
                        || ( newEventCatalog == EventCatalog.SPINE_SWITCH_PORT_UP )
                        || ( newEventCatalog == EventCatalog.SPINE_SWITCH_PORT_DOWN ) )
                    {
                        logger.debug( "HMS Switch Port status events are available for this switch node: " + nodeId );
                        for ( Event oldEvent : oldEvents )
                        {
                            oldEventHeader = oldEvent.getHeader();
                            oldEventComponentIdentifier = oldEvent.getHeader().getComponentIdentifier();
                            logger.debug( "Getting Switch Port component Identifier for comparision for the node: "
                                + nodeId );
                            if ( oldEventComponentIdentifier.get( EventComponent.SWITCH_PORT ).equals( newEventComponentIdentifier.get( EventComponent.SWITCH_PORT ) ) )
                            {
                                oldEventCatalog = oldEventHeader.getEventName();
                                if ( ( oldEventCatalog == EventCatalog.MANAGEMENT_SWITCH_PORT_UP )
                                    || ( oldEventCatalog == EventCatalog.MANAGEMENT_SWITCH_PORT_DOWN )
                                    || ( oldEventCatalog == EventCatalog.TOR_SWITCH_PORT_UP )
                                    || ( oldEventCatalog == EventCatalog.TOR_SWITCH_PORT_DOWN )
                                    || ( oldEventCatalog == EventCatalog.SPINE_SWITCH_PORT_UP )
                                    || ( oldEventCatalog == EventCatalog.SPINE_SWITCH_PORT_DOWN ) )
                                {
                                    newEventData = newEvent.getBody().getData();
                                    oldData = oldEvent.getBody().getData();
                                    logger.debug( "Getting Switch Port status for comparision for the switch node: "
                                        + nodeId );
                                    if ( newEventData.get( "value" ).equals( oldData.get( "value" ) ) )
                                    {
                                        // Status has NOT changed for Storage Events, so no need to generate new event.
                                        continue;
                                    }
                                    else
                                    {
                                        logger.debug( "Adding Switch Port status event as the component status change found for the switch node: "
                                            + nodeId );
                                        switchPortEventsAgrregated.add( newEvent );
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        switchPortEventsAgrregated.add( newEvent );
                    }
                }
            }
            else
            {
                // Because no old events are present, no need to do any filter or massaging
                switchPortEventsAgrregated = newEvents;
            }
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Events for Switch component Switch Port :" + nodeId, e );
        }
        if ( switchPortEventsAgrregated == null )
        {
            // We will not return NULL, We will atleast return Empty Array
            switchPortEventsAgrregated = new ArrayList<Event>();
        }
        return switchPortEventsAgrregated;
    }

    /**
     * Check if there is a change between the switch status from last and the current snapshot of the switch status for
     * the SWITCH UP/DOWN state change events
     *
     * @param nodeId
     * @param serverComponent
     * @param newEvents
     * @return List<Event>
     */
    private static List<Event> filterSwitchUpDownEvents( String nodeId, SwitchComponentEnum switchComponent,
                                                         List<Event> newEvents )
    {
        List<Event> switchEventsAgrregated = null;
        try
        {
            List<Event> oldEvents = getExistingSwitchEventsForComponentInNode( nodeId, switchComponent );
            if ( oldEvents != null && newEvents != null )
            {
                switchEventsAgrregated = new ArrayList<Event>();
                Header newEventHeader = null;
                Map<EventComponent, String> newEventComponentIdentifier = null;
                Map<String, String> newEventData = null;
                EventCatalog newEventCatalog = null;
                Header oldEventHeader = null;
                Map<EventComponent, String> oldEventComponentIdentifier = null;
                Map<String, String> oldData = null;
                EventCatalog oldEventCatalog = null;
                for ( Event newEvent : newEvents )
                {
                    newEventHeader = newEvent.getHeader();
                    newEventComponentIdentifier = newEvent.getHeader().getComponentIdentifier();
                    newEventCatalog = newEventHeader.getEventName();
                    // flag is set to true if we find any matching for the SWITCH Component in oldEvents as well as
                    // newEvents.
                    // Flag to handle if the new Switch added.
                    boolean flag = false;
                    if ( ( newEventCatalog == EventCatalog.MANAGEMENT_SWITCH_UP )
                        || ( newEventCatalog == EventCatalog.MANAGEMENT_SWITCH_DOWN )
                        || ( newEventCatalog == EventCatalog.TOR_SWITCH_UP )
                        || ( newEventCatalog == EventCatalog.TOR_SWITCH_DOWN )
                        || ( newEventCatalog == EventCatalog.SPINE_SWITCH_UP )
                        || ( newEventCatalog == EventCatalog.SPINE_SWITCH_DOWN ) )
                    {
                        logger.debug( "HMS Switch status events are available for this switch node: " + nodeId );
                        for ( Event oldEvent : oldEvents )
                        {
                            oldEventHeader = oldEvent.getHeader();
                            oldEventComponentIdentifier = oldEvent.getHeader().getComponentIdentifier();
                            logger.debug( "Getting Switch component Identifier for comparision for the node: " + nodeId );
                            if ( oldEventComponentIdentifier.get( EventComponent.SWITCH ).equals( newEventComponentIdentifier.get( EventComponent.SWITCH ) ) )
                            {
                                flag = true;
                                oldEventCatalog = oldEventHeader.getEventName();
                                if ( ( oldEventCatalog == EventCatalog.MANAGEMENT_SWITCH_UP )
                                    || ( oldEventCatalog == EventCatalog.MANAGEMENT_SWITCH_DOWN )
                                    || ( oldEventCatalog == EventCatalog.TOR_SWITCH_UP )
                                    || ( oldEventCatalog == EventCatalog.TOR_SWITCH_DOWN )
                                    || ( oldEventCatalog == EventCatalog.SPINE_SWITCH_UP )
                                    || ( oldEventCatalog == EventCatalog.SPINE_SWITCH_DOWN ) )
                                {
                                    newEventData = newEvent.getBody().getData();
                                    oldData = oldEvent.getBody().getData();
                                    logger.debug( "Getting Switch status for comparision for the switch node: "
                                        + nodeId );
                                    if ( newEventData.get( "value" ).equals( oldData.get( "value" ) ) )
                                    {
                                        // Status has NOT changed for Storage Events, so no need to generate new event.
                                        continue;
                                    }
                                    else
                                    {
                                        logger.debug( "Adding Switch status event as the component status change found for the switch node: "
                                            + nodeId );
                                        switchEventsAgrregated.add( newEvent );
                                    }
                                }
                            }
                        }
                        if ( flag == false )
                            switchEventsAgrregated.add( newEvent );
                    }
                    else
                    {
                        switchEventsAgrregated.add( newEvent );
                    }
                }
            }
            else
            {
                // Because no old events are present, no need to do any filter or massaging
                switchEventsAgrregated = newEvents;
            }
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Events for Switch component Switch :" + nodeId, e );
        }
        if ( switchEventsAgrregated == null )
        {
            // We will not return NULL, We will atleast return Empty Array
            switchEventsAgrregated = new ArrayList<Event>();
        }
        return switchEventsAgrregated;
    }

    /**
     * @param serverComponent
     * @return
     */
    private static List<Event> getExistingEventsForHms( ServerComponent serverComponent )
    {
        Map<String, List<Event>> componentStatusMap = hmsEventsMap.get( serverComponent );
        if ( componentStatusMap != null )
        {
            return componentStatusMap.get( HMS_AGENT_STATUS );
        }
        else
        {
            return null;
        }
    }

    /**
     * @param nodeId
     * @param serverComponent
     * @return
     */
    private static List<Event> getExistingEventsForComponentInNode( String nodeId, ServerComponent serverComponent )
    {
        Map<ServerComponent, List<Event>> componentStatusMap = getComponentStatusMap( nodeId );
        if ( componentStatusMap != null )
        {
            return componentStatusMap.get( serverComponent );
        }
        else
        {
            return null;
        }
    }

    /**
     * @param nodeId
     * @param serverComponent
     * @return
     */
    private static List<Event> getExistingSwitchEventsForComponentInNode( String nodeId,
                                                                          SwitchComponentEnum switchComponent )
    {
        Map<SwitchComponentEnum, List<Event>> componentStatusMap = getSwitchComponentStatusMap( nodeId );
        if ( componentStatusMap != null )
        {
            return componentStatusMap.get( switchComponent );
        }
        else
        {
            return null;
        }
    }

    /**
     * @param nodeId
     * @return
     */
    private static Map<ServerComponent, List<Event>> getComponentStatusMap( String nodeId )
    {
        return allNodesComponentEventsMap.get( nodeId );
    }

    /**
     * @param nodeId
     * @return
     */
    private static Map<SwitchComponentEnum, List<Event>> getSwitchComponentStatusMap( String nodeId )
    {
        return switchNodesComponentEventsMap.get( nodeId );
    }

    /**
     * @param nodeId
     * @param serverComponent
     * @param events
     */
    private static void putEventsToComponentStatusMap( String nodeId, ServerComponent serverComponent,
                                                       List<Event> events )
    {
        Map<ServerComponent, List<Event>> componentEventsMap = allNodesComponentEventsMap.get( nodeId );
        ;
        if ( componentEventsMap == null )
        {
            componentEventsMap = new HashMap<ServerComponent, List<Event>>();
        }
        if ( events.size() > 0 )
        {
            List<Event> oldEvents = getExistingEventsForComponentInNode( nodeId, serverComponent );
            if ( oldEvents != null )
            {
                EventComponent eventcomponent = serverComponent.getEventComponent();
                List<Event> aggregatedEvents = new ArrayList<Event>();
                List<Event> newEvents = events;
                for ( Event evento : oldEvents )
                {
                    Map<EventComponent, String> oldEventComponentIdentifier =
                        evento.getHeader().getComponentIdentifier();
                    // flag is set to true if we find any matching for the serverComponent in oldEvents as well as
                    // newEvents.
                    boolean flag = false;
                    for ( Event eventn : newEvents )
                    {
                        Map<EventComponent, String> newEventComponentIdentifier =
                            eventn.getHeader().getComponentIdentifier();
                        if ( oldEventComponentIdentifier.get( eventcomponent ).equals( newEventComponentIdentifier.get( eventcomponent ) ) )
                        {
                            aggregatedEvents.add( eventn );
                            flag = true;
                            break;
                        }
                    }
                    if ( flag == false )
                        aggregatedEvents.add( evento );
                }
                // adding all the new events which were not in the oldEvents List.
                newEvents.removeAll( aggregatedEvents );
                aggregatedEvents.addAll( newEvents );
                events = aggregatedEvents; // Finally update the events.
            }
            componentEventsMap.put( serverComponent, events );
            allNodesComponentEventsMap.put( nodeId, componentEventsMap );
        }
    }

    /**
     * @param serverComponent
     * @param events
     */
    private static void putEventsToComponentStatusMap( ServerComponent serverComponent, List<Event> events )
    {
        Map<String, List<Event>> componentEventsMap = hmsEventsMap.get( HMS_AGENT_STATUS );
        if ( componentEventsMap == null )
        {
            componentEventsMap = new HashMap<String, List<Event>>();
        }
        componentEventsMap.put( HMS_AGENT_STATUS, events );
        hmsEventsMap.put( serverComponent, componentEventsMap );
    }

    /**
     * @param nodeId
     * @param switchComponent
     * @param events
     */
    private static void putSwitchEventsToComponentStatusMap( String nodeId, SwitchComponentEnum switchComponent,
                                                             List<Event> events )
    {
        Map<SwitchComponentEnum, List<Event>> componentEventsMap = switchNodesComponentEventsMap.get( nodeId );
        if ( componentEventsMap == null )
        {
            componentEventsMap = new HashMap<SwitchComponentEnum, List<Event>>();
        }
        if ( events.size() > 0 )
        {
            List<Event> oldEvents = getExistingSwitchEventsForComponentInNode( nodeId, switchComponent );
            if ( oldEvents != null )
            {
                EventComponent eventcomponent = switchComponent.getEventComponent();
                List<Event> aggregatedEvents = new ArrayList<Event>();
                List<Event> newEvents = events;
                for ( Event evento : oldEvents )
                {
                    Map<EventComponent, String> oldEventComponentIdentifier =
                        evento.getHeader().getComponentIdentifier();
                    // flag is set to true if we find any matching for the switchComponent in oldEvents as well as
                    // newEvents.
                    boolean flag = false;
                    for ( Event eventn : newEvents )
                    {
                        Map<EventComponent, String> newEventComponentIdentifier =
                            eventn.getHeader().getComponentIdentifier();
                        if ( oldEventComponentIdentifier.get( eventcomponent ).equals( newEventComponentIdentifier.get( eventcomponent ) ) )
                        {
                            aggregatedEvents.add( eventn );
                            flag = true;
                            break;
                        }
                    }
                    if ( flag == false )
                        aggregatedEvents.add( evento );
                }
                // adding all the new events which were not in the oldEvents List.
                newEvents.removeAll( aggregatedEvents );
                aggregatedEvents.addAll( newEvents );
                events = aggregatedEvents; // Finally update the events.
            }
            componentEventsMap.put( switchComponent, events );
            switchNodesComponentEventsMap.put( nodeId, componentEventsMap );
        }
    }
}
