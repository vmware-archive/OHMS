/* ********************************************************************************
 * Event.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.notification;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Model for Events.
 *
 * @author ychawda
 */
@JsonInclude( JsonInclude.Include.NON_NULL )
public class Event
{
    private EventType eventType;;

    private String notificationUrl;

    private String registrationUrl;

    private Double minThreshold;

    private Double maxThreshold;

    private Long frequency = 0L;

    private Integer instance;

    private Boolean registered = false;

    private Long eventRegistrationTime;

    private String targetId;

    private Long lastUpdatedTime = -1L;

    public EventType getEventType()
    {
        return eventType;
    }

    public void setEventType( EventType eventType )
    {
        this.eventType = eventType;
    }

    public String getNotificationUrl()
    {
        return notificationUrl;
    }

    public void setNotificationUrl( String notificationUrl )
    {
        this.notificationUrl = notificationUrl;
    }

    public String getRegistrationUrl()
    {
        return registrationUrl;
    }

    public void setRegistrationUrl( String registrationUrl )
    {
        this.registrationUrl = registrationUrl;
    }

    public Double getMinThreshold()
    {
        return minThreshold;
    }

    public void setMinThreshold( Double minThreshold )
    {
        this.minThreshold = minThreshold;
    }

    public Double getMaxThreshold()
    {
        return maxThreshold;
    }

    public void setMaxThreshold( Double maxThreshold )
    {
        this.maxThreshold = maxThreshold;
    }

    public Long getFrequency()
    {
        return frequency;
    }

    public void setFrequency( Long frequency )
    {
        this.frequency = frequency;
    }

    public Integer getInstance()
    {
        return instance;
    }

    public void setInstance( Integer instance )
    {
        this.instance = instance;
    }

    public Boolean getRegistered()
    {
        return registered;
    }

    public void setRegistered( Boolean registered )
    {
        this.registered = registered;
    }

    public Long getEventRegistrationTime()
    {
        return eventRegistrationTime;
    }

    public void setEventRegistrationTime( Long eventRegistrationTime )
    {
        this.eventRegistrationTime = eventRegistrationTime;
    }

    public String getTargetId()
    {
        return targetId;
    }

    public void setTargetId( String targetId )
    {
        this.targetId = targetId;
    }

    public Long getLastUpdatedTime()
    {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime( Long lastUpdatedTime )
    {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public boolean canPostCallBack()
    {
        if ( lastUpdatedTime == -1L || ( new Date() ).getTime() - lastUpdatedTime > frequency )
            return true;
        return false;
    }

    @Override
    public String toString()
    {
        return "Event [eventType=" + eventType + ", notificationUrl=" + notificationUrl + ", registrationUrl="
            + registrationUrl + ", minThreshold=" + minThreshold + ", maxThreshold=" + maxThreshold + ", frequency="
            + frequency + ", instance=" + instance + ", registered=" + registered + ", eventRegistrationTime="
            + eventRegistrationTime + ", targetId=" + targetId + ", lastUpdatedTime=" + lastUpdatedTime + "]";
    }
}
