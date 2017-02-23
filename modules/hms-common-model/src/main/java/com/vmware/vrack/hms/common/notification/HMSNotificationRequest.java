/* ********************************************************************************
 * HMSNotificationRequest.java
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

import java.util.List;
import java.util.Map;

/**
 * Class used to marshall-unmarshall HMS notifications JSON request between modules.
 * 
 * @author ambi
 */
@Deprecated
public class HMSNotificationRequest
{
    private String targetId;

    private Long eventTime; // Epoch time in milliseconds.

    private String eventType;

    private List<Map<String, String>> listData;

    public String getTargetId()
    {
        return targetId;
    }

    public void setTargetId( String targetId )
    {
        this.targetId = targetId;
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

    public List<Map<String, String>> getListData()
    {
        return listData;
    }

    public void setListData( List<Map<String, String>> listData )
    {
        this.listData = listData;
    }

    @Override
    public String toString()
    {
        return String.format( "HMSNotificationRequest[ targetId = %s, eventTime = %s, eventType = %s, listData = %s ]",
                              this.targetId, this.eventTime, this.eventType, this.listData );
    }
}