/* ********************************************************************************
 * EventHolder.java
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
package com.vmware.vrack.hms.common.notification;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude( JsonInclude.Include.NON_NULL )
public class EventHolder
    extends BaseResponse
{
    private EventRequester requester;

    private List<Event> events = new ArrayList<Event>();

    public EventRequester getRequester()
    {
        return requester;
    }

    public void setRequester( EventRequester requester )
    {
        this.requester = requester;
    }

    public List<Event> getEvents()
    {
        return events;
    }

    public void setEvents( List<Event> events )
    {
        this.events = events;
    }

    public Event getCallBackEvent( EventType type )
    {
        Event callBackEvent = null;
        for ( Event event : events )
        {

            if ( event.getEventType() == type && event.canPostCallBack() )
            {
                callBackEvent = event;
                break;
            }

        }
        return callBackEvent;
    }

    public List<Event> getCallBackEvents( EventType type )
    {
        List<Event> eventList = new ArrayList<Event>();

        for ( Event event : events )
        {
            Event callBackEvent = null;
            if ( event.getEventType() == type && event.canPostCallBack() )
            {
                callBackEvent = event;
                eventList.add( callBackEvent );
            }
        }
        return eventList;
    }

    @Override
    public String toString()
    {
        return "EventHolder [requester=" + requester + ", events=" + events + "]";
    }

}
