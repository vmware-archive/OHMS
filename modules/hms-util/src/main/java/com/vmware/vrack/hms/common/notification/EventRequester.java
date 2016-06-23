/* ********************************************************************************
 * EventRequester.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.notification;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Model for Event Requester
 *
 * @author ychawda
 */
@JsonInclude( JsonInclude.Include.NON_NULL )
public class EventRequester
{
    private String appType;

    private String baseUrl;

    private String subscriberId;

    public String getAppType()
    {
        return appType;
    }

    public void setAppType( String appType )
    {
        this.appType = appType;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl( String baseUrl )
    {
        this.baseUrl = baseUrl;
    }

    public String getSubscriberId()
    {
        return subscriberId;
    }

    public void setSubscriberId( String subscriberId )
    {
        this.subscriberId = subscriberId;
    }

    @Override
    public String toString()
    {
        return "EventRequester [appType=" + appType + ", baseUrl=" + baseUrl + ", subscriberId=" + subscriberId + "]";
    }
}
