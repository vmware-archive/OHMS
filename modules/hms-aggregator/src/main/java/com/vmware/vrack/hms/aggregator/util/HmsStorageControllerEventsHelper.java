/* ********************************************************************************
 * HmsStorageControllerEventsHelper.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.Header;
import com.vmware.vrack.common.event.enums.EventCatalog;
import com.vmware.vrack.common.event.enums.EventComponent;

/**
 * Helps HMS to monitor the server component Storage Controller operational status events STORAGE_CONTROLLER_UP and
 * STORAGE_CONTROLLER_DOWN. Push events only if there is a change in component operational status.
 */
public class HmsStorageControllerEventsHelper
{
    private static Logger logger = Logger.getLogger( HmsStorageControllerEventsHelper.class );

    private static final Map<String, List<Event>> storageControllerStatusMap = new HashMap<String, List<Event>>();

    /**
     * Method helps to HMS monitor the server component Storage Controller operational status events
     *
     * @param storageControllerEvents
     * @param nodeId
     * @return Events
     */
    public List<Event> getStorageControllerOperationalStatusEvents( List<Event> storageControllerEvents, String nodeId )
    {
        List<Event> storageControllerEventsAgrregated = new ArrayList<Event>();
        List<Event> preStorageControllerEventList = new ArrayList<Event>();
        try
        {
            preStorageControllerEventList = getStorageControllerStatusMap( nodeId );
            if ( preStorageControllerEventList != null )
            {
                for ( int i = 0; i < storageControllerEvents.size(); i++ )
                {
                    Event event = storageControllerEvents.get( i );
                    Header eventHeader = event.getHeader();
                    Map<EventComponent, String> eventComponentIdentifier = event.getHeader().getComponentIdentifier();
                    Map<String, String> data = event.getBody().getData();
                    if ( ( eventHeader.getEventName() == EventCatalog.STORAGE_CONTROLLER_UP )
                        || ( eventHeader.getEventName() == EventCatalog.STORAGE_CONTROLLER_DOWN ) )
                    {
                        logger.debug( "HMS Storage controller operational status events are available for this node: "
                            + nodeId );
                        for ( int j = 0; j < preStorageControllerEventList.size(); j++ )
                        {
                            Event preEvent = preStorageControllerEventList.get( j );
                            Header preEventHeader = preEvent.getHeader();
                            Map<EventComponent, String> preEventComponentIdentifier =
                                preEvent.getHeader().getComponentIdentifier();
                            Map<String, String> preData = preEvent.getBody().getData();
                            if ( ( preEventHeader.getEventName() == EventCatalog.STORAGE_CONTROLLER_UP )
                                || ( preEventHeader.getEventName() == EventCatalog.STORAGE_CONTROLLER_DOWN ) )
                            {
                                if ( preEventComponentIdentifier.get( EventComponent.STORAGE_CONTROLLER ).equals( eventComponentIdentifier.get( EventComponent.STORAGE_CONTROLLER ) ) )
                                {
                                    logger.debug( "Getting Storage controller component Identifier for comparision for the node: "
                                        + nodeId );
                                    if ( data.get( "value" ).equals( preData.get( "value" ) ) )
                                    {
                                        logger.debug( "Getting Storage controller operational status comparision for the node: "
                                            + nodeId );
                                        continue;
                                    }
                                    else
                                    {
                                        logger.debug( "Adding Storage Controller operational status event as the component status change found for the node: "
                                            + nodeId );
                                        storageControllerEventsAgrregated.add( event );
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        storageControllerEventsAgrregated.add( event );
                    }
                }
                storageControllerStatusMap.put( nodeId, storageControllerEvents );
                logger.debug( "Got Events from HMS keeping the hash map" );
            }
            else
            {
                storageControllerStatusMap.put( nodeId, storageControllerEvents );
                return storageControllerEvents;
            }
            return storageControllerEventsAgrregated;
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator Error while getting Events for server component Storage Controller:" + nodeId,
                          e );
        }
        return null;
    }

    private static List<Event> getStorageControllerStatusMap( String hostID )
    {
        return storageControllerStatusMap.get( hostID );
    }
}
