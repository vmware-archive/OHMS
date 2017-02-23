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
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@SuppressWarnings( "deprecation" )
public class EventFilterService
{
    private static Logger logger = LoggerFactory.getLogger( EventFilterService.class );

    // Map<nodeId, Map<ServerComponent, List<Event>>> to hold old events for
    // each node's all components
    private static final Map<String, Map<ServerComponent, List<Event>>> allNodesComponentEventsMap =
        new ConcurrentHashMap<String, Map<ServerComponent, List<Event>>>();

    private static final Map<String, Map<SwitchComponentEnum, List<Event>>> switchNodesComponentEventsMap =
        new ConcurrentHashMap<String, Map<SwitchComponentEnum, List<Event>>>();

    private static final Map<ServerComponent, Map<String, List<Event>>> hmsEventsMap =
        new ConcurrentHashMap<ServerComponent, Map<String, List<Event>>>();

    private static final ReentrantLock switchEventFilterUpdateLock = new ReentrantLock();

    private static final String HMS_AGENT_STATUS = "HMS_AGENT_STATUS";

    /**
     * @param nodeId
     * @param serverComponent
     * @param newEvents
     * @return List<Event>
     */
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
        try
        {
            // Acquired the lock to update switch event status
            switchEventFilterUpdateLock.lock();
        }
        catch ( Exception e )
        {
            logger.error( "Lock Acquisition failed to filter or message switch events.", e );
            return new ArrayList<Event>();
        }

        try
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
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while filtering switch events:{}", nodeId, e );
        }
        finally
        {
            // release the acquired lock for other thread to update server up or
            // down event status
            switchEventFilterUpdateLock.unlock();
        }
        return new ArrayList<Event>();
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
            logger.debug( "Filtering NIC Events for host:{}, serverComponent:{} ", nodeId, serverComponent );
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

                    // flag is set to true if we find any matching event for the
                    // NIC Component in the oldEvents as well as the newEvents.
                    // Flag to handle if the new NIC added.
                    boolean flag = false;

                    if ( ( newEventCatalog == EventCatalog.NIC_PORT_UP )
                        || ( newEventCatalog == EventCatalog.NIC_PORT_DOWN ) )
                    {
                        String newEventFruId = newEventComponentIdentifier.get( EventComponent.NIC );
                        String newEventFruIdPort = newEventComponentIdentifier.get( EventComponent.PORT );

                        for ( Event oldEvent : oldEvents )
                        {
                            oldEventHeader = oldEvent.getHeader();
                            oldEventComponentIdentifier = oldEvent.getHeader().getComponentIdentifier();
                            oldEventData = oldEvent.getBody().getData();

                            String oldEventFruId = oldEventComponentIdentifier.get( EventComponent.NIC );
                            String oldEventFruIdPort = oldEventComponentIdentifier.get( EventComponent.PORT );
                            logger.debug( "Host:{}, old NIC:{} Port: {}, new NIC:{} Port:{}", nodeId, oldEventFruId,
                                          oldEventFruIdPort, newEventFruId, newEventFruIdPort );
                            // Checking PORT also as we have PORT resource hierarchy in NIC_PORT_UP and NIC_PORT_DOWN
                            // events
                            if ( newEventFruId != null && newEventFruIdPort != null
                                && newEventFruId.equals( oldEventFruId )
                                && newEventFruIdPort.equals( oldEventFruIdPort ) )
                            {
                                flag = true;
                                oldEventCatalog = oldEventHeader.getEventName();

                                if ( ( oldEventCatalog == EventCatalog.NIC_PORT_UP )
                                    || ( oldEventCatalog == EventCatalog.NIC_PORT_DOWN ) )
                                {
                                    if ( newEventData.get( "value" ).equals( oldEventData.get( "value" ) ) )
                                    {
                                        // old and new port status for this NIC
                                        // port is same, so we will not generate
                                        // new event
                                        continue;
                                    }
                                    else
                                    {
                                        logger.debug( "Adding NIC port status event for host:{} with NIC: {} port: {}, "
                                            + "EventCatalog:{},oldEventvalue: {}, newEventValue: {}", nodeId,
                                                      newEventFruId, newEventFruIdPort, newEventCatalog,
                                                      oldEventData.get( "value" ), newEventData.get( "value" ) );

                                        nicEventsAgrregated.add( newEvent );
                                    }
                                }
                            }
                        }
                        if ( flag == false )
                        {
                            logger.debug( "A new NIC component has been detected for host:{} with NIC: {} port:{} and eventCatalog:{}.",
                                          nodeId, newEventFruId, newEventFruIdPort, newEventCatalog );
                            nicEventsAgrregated.add( newEvent );
                        }
                    }
                    else
                    {
                        logger.debug( "Adding NIC port status event for host:{} with EventCatalog:{}", nodeId,
                                      newEventCatalog );
                        nicEventsAgrregated.add( newEvent );
                    }
                }
            }
            else
            {
                // Because no past events are present, no need to do any
                // massaging
                logger.debug( "Adding the complete set of newEvents for host:{} with serverComponent:{} "
                    + "since oldEvents/newEvents set is null.", nodeId, serverComponent );
                nicEventsAgrregated = newEvents;
            }
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Events for server component NIC:{}", nodeId, e );
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
            logger.debug( "Filtering STORAGE CONTROLLER Events for host:{}, serverComponent:{} ", nodeId,
                          serverComponent );
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

                    // flag is set to true if we find any matching event for the
                    // STORAGE_CONTROLLER Component in oldEvents as well as
                    // newEvents.
                    // Flag to handle if the new storage controller added.
                    boolean flag = false;

                    if ( ( newEventCatalog == EventCatalog.STORAGE_CONTROLLER_DOWN )
                        || ( newEventCatalog == EventCatalog.STORAGE_CONTROLLER_UP ) )
                    {
                        for ( Event oldEvent : oldEvents )
                        {
                            oldEventHeader = oldEvent.getHeader();
                            oldEventComponentIdentifier = oldEvent.getHeader().getComponentIdentifier();
                            logger.debug( "Host:{}, old Storage Controller ID:{}, new Storage Controller ID:{}", nodeId,
                                          oldEventComponentIdentifier.get( EventComponent.STORAGE_CONTROLLER ),
                                          ( newEventComponentIdentifier.get( EventComponent.STORAGE_CONTROLLER ) ) );

                            if ( oldEventComponentIdentifier.get( EventComponent.STORAGE_CONTROLLER ).equals( newEventComponentIdentifier.get( EventComponent.STORAGE_CONTROLLER ) ) )
                            {
                                flag = true;
                                oldEventCatalog = oldEventHeader.getEventName();

                                if ( ( oldEventCatalog == EventCatalog.STORAGE_CONTROLLER_UP )
                                    || ( oldEventCatalog == EventCatalog.STORAGE_CONTROLLER_DOWN ) )
                                {
                                    newEventData = newEvent.getBody().getData();
                                    oldData = oldEvent.getBody().getData();

                                    if ( newEventData.get( "value" ).equals( oldData.get( "value" ) ) )
                                    {
                                        // Status has NOT changed for Storage
                                        // Controller, so no need to generate
                                        // new event.
                                        continue;
                                    }
                                    else
                                    {
                                        logger.debug( "Adding Storage Controller operational status event for the host:{} with "
                                            + "storage controller:{}, oldEventvalue: {}, newEventValue: {}", nodeId,
                                                      newEventComponentIdentifier.get( EventComponent.STORAGE_CONTROLLER ),
                                                      oldData.get( "value" ), newEventData.get( "value" ) );
                                        storageControllerEventsAgrregated.add( newEvent );
                                    }
                                }
                            }
                        }
                        if ( flag == false )
                        {
                            logger.debug( "A new Storage Controller component has been detected for host:{} "
                                + "with storage controller:{} and eventCatalog:{}.", nodeId,
                                          newEventComponentIdentifier.get( EventComponent.STORAGE_CONTROLLER ),
                                          newEventCatalog );
                            storageControllerEventsAgrregated.add( newEvent );
                        }
                    }
                    else
                    {
                        logger.debug( "Adding Storage Controller event for host:{} with EventCatalog:{}", nodeId,
                                      newEventCatalog );
                        storageControllerEventsAgrregated.add( newEvent );
                    }
                }
            }
            else
            {
                // Because no past events are present, no need to do any
                // massaging
                logger.debug( "Adding the complete set of newEvents for host:{} with "
                    + "serverComponent:{} since oldEvents/newEvents set is null.", nodeId, serverComponent );
                storageControllerEventsAgrregated = newEvents;
            }
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Events for server component Storage Controller:{}",
                          nodeId, e );
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
            logger.debug( "Filtering STORAGE Events for host:{}, serverComponent:{} ", nodeId, serverComponent );
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

                    // flag is set to true if we find any matching event for the
                    // STORAGE Component in oldEvents as well as newEvents.
                    // Flag to handle if the new storage disk added/inserted.
                    boolean flag = false;

                    if ( ( newEventCatalog == EventCatalog.HDD_UP ) || ( newEventCatalog == EventCatalog.HDD_DOWN )
                        || ( newEventCatalog == EventCatalog.SSD_UP ) || ( newEventCatalog == EventCatalog.SSD_DOWN ) )
                    {

                        for ( Event oldEvent : oldEvents )
                        {
                            oldEventHeader = oldEvent.getHeader();
                            oldEventComponentIdentifier = oldEvent.getHeader().getComponentIdentifier();

                            logger.debug( "Host:{}, old Storage Id:{}, new Storage Id:{}", nodeId,
                                          oldEventComponentIdentifier.get( EventComponent.STORAGE ),
                                          newEventComponentIdentifier.get( EventComponent.STORAGE ) );

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

                                    if ( newEventData.get( "value" ).equals( oldData.get( "value" ) ) )
                                    {
                                        // Status has NOT changed for Storage
                                        // Events, so no need to generate new
                                        // event.
                                        continue;
                                    }
                                    else
                                    {
                                        logger.debug( "Adding Storage operational status event for the host:{} "
                                            + "with storageId:{}, oldData:{}, newData:{}", nodeId,
                                                      newEventComponentIdentifier.get( EventComponent.STORAGE ),
                                                      oldData.get( "value" ), newEventData.get( "value" ) );
                                        storageEventsAgrregated.add( newEvent );
                                    }
                                }
                            }
                        }

                        if ( flag == false )
                        {
                            logger.debug( "A new Storage component has been detected for host:{} with storageId:{} and eventCatalog:{}.",
                                          nodeId, newEventComponentIdentifier.get( EventComponent.STORAGE ),
                                          newEventCatalog );
                            storageEventsAgrregated.add( newEvent );
                        }

                    }
                    else
                    {
                        logger.debug( "Adding Storage event for host:{} with EventCatalog:{}", nodeId,
                                      newEventCatalog );
                        storageEventsAgrregated.add( newEvent );
                    }
                }
            }
            else
            {
                // Because no old events are present, no need to do any filter
                // or massaging
                logger.debug( "Adding the complete set of newEvents for host:{} with serverComponent:{} since oldEvents/newEvents set is null.",
                              nodeId, serverComponent );
                storageEventsAgrregated = newEvents;
            }

        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Events for host:{} for server component Storage.", nodeId,
                          e );
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
                                        // Status has NOT changed for HMS
                                        // Events, so no need to generate new
                                        // event.
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
                                    logger.debug( "Null data found- oldEventData:{} :: newEventData:{}", oldEventData,
                                                  newEventData );
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
                // Because no old events are present, no need to do any filter
                // or massaging
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
            logger.debug( "Filtering SWITCH PORT Events for switch:{}, switchComponent:{} ", nodeId, switchComponent );
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

                        for ( Event oldEvent : oldEvents )
                        {
                            oldEventHeader = oldEvent.getHeader();
                            oldEventComponentIdentifier = oldEvent.getHeader().getComponentIdentifier();

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

                                    if ( newEventData.get( "value" ).equals( oldData.get( "value" ) ) )
                                    {
                                        // Status has NOT changed for Storage
                                        // Events, so no need to generate new
                                        // event.
                                        continue;
                                    }
                                    else
                                    {
                                        logger.debug( "Adding Switch Port status event as the status change found for the "
                                            + "switch:{} with switch port:{}, oldData:{}, newData:{}", nodeId,
                                                      newEventComponentIdentifier.get( EventComponent.SWITCH_PORT ),
                                                      oldData.get( "value" ), newEventData.get( "value" ) );
                                        switchPortEventsAgrregated.add( newEvent );
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        logger.debug( "Adding Switch Port event for switch:{} with EventCatalog:{}", nodeId,
                                      newEventCatalog );
                        switchPortEventsAgrregated.add( newEvent );
                    }
                }
            }
            else
            {
                // Because no old events are present, no need to do any filter
                // or massaging
                logger.debug( "Adding the complete set of newEvents for switch:{} with switchComponent:{} "
                    + "since oldEvents/newEvents set is null.", nodeId, switchComponent );
                switchPortEventsAgrregated = newEvents;
            }
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Events for Switch Port of switch:{}", nodeId, e );
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
            logger.debug( "Filtering SWITCH Events for switch:{}, switchComponent:{} ", nodeId, switchComponent );
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

                    // flag is set to true if we find any matching event for the
                    // SWITCH Component in oldEvents as well as newEvents.
                    // Flag to handle if the new Switch added.
                    boolean flag = false;

                    if ( ( newEventCatalog == EventCatalog.MANAGEMENT_SWITCH_UP )
                        || ( newEventCatalog == EventCatalog.MANAGEMENT_SWITCH_DOWN )
                        || ( newEventCatalog == EventCatalog.TOR_SWITCH_UP )
                        || ( newEventCatalog == EventCatalog.TOR_SWITCH_DOWN )
                        || ( newEventCatalog == EventCatalog.SPINE_SWITCH_UP )
                        || ( newEventCatalog == EventCatalog.SPINE_SWITCH_DOWN ) )
                    {

                        for ( Event oldEvent : oldEvents )
                        {
                            oldEventHeader = oldEvent.getHeader();
                            oldEventComponentIdentifier = oldEvent.getHeader().getComponentIdentifier();

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

                                    if ( newEventData.get( "value" ).equals( oldData.get( "value" ) ) )
                                    {
                                        // Status has NOT changed for Storage
                                        // Events, so no need to generate new
                                        // event.
                                        continue;
                                    }
                                    else
                                    {
                                        logger.debug( "Adding Switch status event as the status change found for "
                                            + "the switch:{}, oldData:{}, newData:{}", nodeId, oldData.get( "value" ),
                                                      newEventData.get( "value" ) );

                                        switchEventsAgrregated.add( newEvent );
                                    }
                                }
                            }
                        }
                        if ( flag == false )
                        {
                            logger.debug( "A new Switch component has been detected for switch:{} and eventCatalog:{}.",
                                          nodeId, newEventCatalog );
                            switchEventsAgrregated.add( newEvent );
                        }
                    }
                    else
                    {
                        logger.debug( "Adding SWITCH event for switch:{} with EventCatalog:{}", nodeId,
                                      newEventCatalog );
                        switchEventsAgrregated.add( newEvent );
                    }
                }
            }
            else
            {
                // Because no old events are present, no need to do any filter
                // or massaging
                logger.debug( "Adding the complete set of newEvents for switch:{} with switchComponent:{} "
                    + "since oldEvents/newEvents set is null.", nodeId, switchComponent );
                switchEventsAgrregated = newEvents;
            }
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Events for Switch component of Switch:{}", nodeId, e );
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

        if ( events != null && events.size() > 0 )
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

                    // flag is set to true if we find any matching event for the
                    // serverComponent in oldEvents as well as newEvents.
                    boolean flag = false;

                    for ( Event eventn : newEvents )
                    {
                        Map<EventComponent, String> newEventComponentIdentifier =
                            eventn.getHeader().getComponentIdentifier();
                        if ( oldEventComponentIdentifier.get( eventcomponent ).equals( newEventComponentIdentifier.get( eventcomponent ) ) )
                        {
                            Header eventHeader = eventn.getHeader();
                            EventCatalog eventCatalog = eventHeader.getEventName();
                            // Checking PORT also as we have PORT resource hierarchy in NIC_PORT_UP and NIC_PORT_DOWN
                            // events
                            if ( ( eventCatalog == EventCatalog.NIC_PORT_UP )
                                || ( eventCatalog == EventCatalog.NIC_PORT_DOWN ) )
                            {
                                if ( oldEventComponentIdentifier.get( EventComponent.PORT ).equals( newEventComponentIdentifier.get( EventComponent.PORT ) ) )
                                {
                                    aggregatedEvents.add( eventn );
                                    flag = true;
                                    break;
                                }

                            }
                            else
                            {
                                aggregatedEvents.add( eventn );
                                flag = true;
                                break;
                            }
                        }
                    }

                    if ( flag == false )
                        aggregatedEvents.add( evento );

                }
                // adding all the new events which were not in the oldEvents
                // List.
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

        if ( events != null && events.size() > 0 )
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

                    // flag is set to true if we find any matching event for the
                    // switchComponent in oldEvents as well as newEvents.
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
                // adding all the new events which were not in the oldEvents
                // List.
                newEvents.removeAll( aggregatedEvents );
                aggregatedEvents.addAll( newEvents );

                events = aggregatedEvents; // Finally update the events.
            }
            componentEventsMap.put( switchComponent, events );
            switchNodesComponentEventsMap.put( nodeId, componentEventsMap );
        }
    }
}
