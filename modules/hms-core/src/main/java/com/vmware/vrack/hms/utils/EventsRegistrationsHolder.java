/* ********************************************************************************
 * EventsRegistrationsHolder.java
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
package com.vmware.vrack.hms.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vmware.vrack.hms.common.notification.Event;
import com.vmware.vrack.hms.common.notification.EventHolder;
import com.vmware.vrack.hms.common.notification.EventType;

public class EventsRegistrationsHolder
{
    public static final String APP_TYPE = "appType";

    public static final String EVENT_TYPE = "eventType";

    public static final String TARGET_ID = "targetId";

    private EventsRegistrationsHolder()
    {
    }

    static EventsRegistrationsHolder registerEventUtil = null;

    private static volatile Map<String, Object> eventRegistrationMap = new HashMap<String, Object>();

    public static Map<String, Object> getEventRegistrationMap()
    {
        return eventRegistrationMap;
    }

    public static void setEventRegistrationMap( Map<String, Object> eventRegistrationMap )
    {
        EventsRegistrationsHolder.eventRegistrationMap = eventRegistrationMap;
    }

    public static EventsRegistrationsHolder getInstance()
    {
        if ( registerEventUtil == null )
        {
            registerEventUtil = new EventsRegistrationsHolder();
        }
        return registerEventUtil;
    }

    public void setEventDetails( EventHolder eventHolder )
    {
        if ( !eventRegistrationMap.containsKey( eventHolder.getRequester().getAppType() ) )
        {
            eventRegistrationMap.put( eventHolder.getRequester().getAppType(), eventHolder );
        }
        else
        {
            eventRegistrationMap.remove( eventHolder.getRequester().getAppType() );
            eventRegistrationMap.put( eventHolder.getRequester().getAppType(), eventHolder );
        }
        // return (EventHolder) eventRegistrationMap.get(eventHolder.getRequester().getAppType());
    }

    public EventHolder getEventDetails( String AppType )
    {
        if ( eventRegistrationMap.containsKey( AppType ) )
        {
            return (EventHolder) eventRegistrationMap.get( AppType );
        }
        else
        {
            return null;
        }
        // return (EventHolder) eventRegistrationMap.get(eventHolder.getRequester().getAppType());
    }

    /**
     * takes HashMap as Input with keys as appType, eventType, targetId, declared as constants in this class. returns
     * list of EventHolder. filtering of Events will be done with input HashmMap values.
     * 
     * @param filters
     * @return
     */
    public List<EventHolder> getEventsHolders( Map<String, Object> filters )
    {
        List<EventHolder> eventHolderList = new ArrayList<EventHolder>();
        // Loop through each Keys in the EventRegistrationHolder
        if ( filters != null )
        {
            Set<String> appTypes = eventRegistrationMap.keySet();
            for ( String appType : appTypes )
            {
                EventHolder resultantEventHolder = new EventHolder();
                boolean addEventHolder = false;
                // Check if filter contains the appType filter or not
                String filterAppType = (String) filters.get( APP_TYPE );
                if ( appType != null )
                {
                    if ( ( filters == null ) || ( filterAppType == null )
                        || ( filterAppType != null && appType.equals( filterAppType ) ) )
                    {
                        // Process data within it
                        EventType filterEventType = null;
                        if ( filters.get( EVENT_TYPE ) != null )
                        {
                            filterEventType = EventType.valueOf( filters.get( EVENT_TYPE ).toString() );
                        }
                        EventHolder eventHolder = (EventHolder) eventRegistrationMap.get( appType );
                        // Iterate through Events in the evenHolder of current appType\
                        if ( eventHolder != null && eventHolder.getEvents() != null )
                        {
                            List<Event> resultantEvents = new ArrayList<Event>();
                            List<Event> originalEvents = eventHolder.getEvents();
                            int eventsCount = originalEvents.size();
                            for ( int i = 0; i < eventsCount; i++ )
                            {
                                Event event = originalEvents.get( i );
                                if ( event != null )
                                {
                                    EventType eventType = event.getEventType();
                                    if ( ( filterEventType == null )
                                        || ( filterEventType != null && filterEventType.equals( eventType ) ) )
                                    {
                                        String filterTargetId = (String) filters.get( TARGET_ID );
                                        String targetId = event.getTargetId();
                                        if ( ( targetId == null ) || ( filterTargetId == null )
                                            || ( filterTargetId != null && filterTargetId.equals( targetId ) ) )
                                        {
                                            resultantEvents.add( event );
                                            addEventHolder = true;
                                        }
                                    }
                                }
                            }
                            if ( addEventHolder )
                            {
                                resultantEventHolder.setEvents( resultantEvents );
                                resultantEventHolder.setRequester( eventHolder.getRequester() );
                                eventHolderList.add( resultantEventHolder );
                            }
                        }
                    }
                }
                else
                {
                    continue;
                }
            }
        }
        else
        {
            Set<String> appTypes = eventRegistrationMap.keySet();
            for ( String appType : appTypes )
            {
                EventHolder eventHolder = (EventHolder) eventRegistrationMap.get( appType );
                if ( eventHolder.getEvents() != null && !eventHolder.getEvents().isEmpty() )
                {
                    eventHolderList.add( eventHolder );
                }
            }
        }
        return eventHolderList;
    }
}
