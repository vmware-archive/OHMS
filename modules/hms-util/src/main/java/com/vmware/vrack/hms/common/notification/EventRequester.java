/* ********************************************************************************
 * EventRequester.java
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
