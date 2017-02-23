/* ********************************************************************************
 * BaseEventMonitoringSubscription.java
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

package com.vmware.vrack.hms.common.events;

import com.vmware.vrack.hms.common.RequestMethod;
import com.vmware.vrack.hms.common.notification.BaseResponse;

/**
 * Base Class for Event Registration subscription
 * 
 * @author VMware Inc.
 */
public class BaseEventMonitoringSubscription
    extends BaseResponse
{
    /**
     * Specify what HTTP Request method should be used for the endpoint
     */
    private RequestMethod requestMethod = RequestMethod.POST;

    /**
     * Absolute URl to be called by HMS to send the Events to the subscriber
     */
    private String notificationEndpoint;

    /**
     * Absolute URl to be called by HMS to send the Events to the subscriber
     * 
     * @return
     */
    public String getNotificationEndpoint()
    {
        return notificationEndpoint;
    }

    public void setNotificationEndpoint( String notificationEndpoint )
    {
        this.notificationEndpoint = notificationEndpoint;
    }

    public RequestMethod getRequestMethod()
    {
        return requestMethod;
    }

    public void setRequestMethod( RequestMethod requestMethod )
    {
        this.requestMethod = requestMethod;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
            return true;

        boolean result = false;
        if ( object == null || object.getClass() != getClass() )
        {
            result = false;
        }
        else
        {
            BaseEventMonitoringSubscription other = (BaseEventMonitoringSubscription) object;
            if ( ( this.requestMethod != null && this.requestMethod.equals( other.getRequestMethod() ) )
                && ( this.notificationEndpoint != null
                    && this.notificationEndpoint.equals( other.getNotificationEndpoint() ) ) )
            {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        if ( requestMethod != null )
        {
            hash = 7 * hash + this.requestMethod.hashCode();
        }

        if ( notificationEndpoint != null )
        {
            hash = 7 * hash + this.notificationEndpoint.hashCode();
        }

        return hash;
    }

}
