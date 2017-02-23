/* ********************************************************************************
 * HMSNotificationResponse.java
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

/**
 * Class used to marshall-unmarshall HMS notifications JSON response between modules.
 * 
 * @author ambi
 */
public class HMSNotificationResponse
{
    private String targetId;

    private Long eventTime;

    private String eventType;

    private String status;

    public String getStatus()
    {
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
    }

    public Long getEventTime()
    {
        return eventTime;
    }

    public void setEventTime( Long eventTime )
    {
        this.eventTime = eventTime;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType( String eventType )
    {
        this.eventType = eventType;
    }

    public String getTargetId()
    {
        return targetId;
    }

    public void setTargetId( String targetId )
    {
        this.targetId = targetId;
    }

    @Override
    public String toString()
    {
        return String.format( "HMSNotificationResponse[ targetId = %s, eventTime = %s, eventType = %s, status = %s ]",
                              this.targetId, this.eventTime, this.eventType, this.status );
    }

}