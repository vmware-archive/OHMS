/* ********************************************************************************
 * EventHolder.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
