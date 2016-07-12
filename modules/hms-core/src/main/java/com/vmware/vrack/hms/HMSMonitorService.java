/* ********************************************************************************
 * HMSMonitorService.java
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
package com.vmware.vrack.hms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.notification.Event;
import com.vmware.vrack.hms.common.notification.EventHolder;
import com.vmware.vrack.hms.common.notification.EventType;
import com.vmware.vrack.hms.common.notification.HMSNotificationRequest;
import com.vmware.vrack.hms.common.util.HttpUtil;
import com.vmware.vrack.hms.utils.EventsRegistrationsHolder;
import com.vmware.vrack.hms.utils.HttpClientService;

public class HMSMonitorService
    implements Observer
{
    private static Logger logger = Logger.getLogger( HMSMonitorService.class );

    public void update( Observable node, Object arg )
    {
        if ( node instanceof HmsNode )
        {
            HmsNode hmsNode = (HmsNode) node;
            HMSNotificationRequest notification = (HMSNotificationRequest) arg;
            EventsRegistrationsHolder eventHolder = EventsRegistrationsHolder.getInstance();
            Map<String, Object> eventFilters = new HashMap<String, Object>();
            // eventFilters.put(EventsRegistrationsHolder.TARGET_ID, hmsNode.getNodeID());
            eventFilters.put( EventsRegistrationsHolder.EVENT_TYPE, notification.getEventType() );
            List<EventHolder> eventHolders = eventHolder.getEventsHolders( eventFilters );
            logger.debug( "Got Change notification event " + notification.getEventType() );
            hmsNode.setMonitorExecutionLog( "GOT CHANGE NOTIFICATION EVENT : " + notification.getEventType()
                + " THREAD : " + Thread.currentThread().getId() + " TIME :" + ( new Date() ).toString(), true );
            for ( EventHolder holder : eventHolders )
            {
                try
                {
                    postEventCallBack( holder, notification, hmsNode );
                }
                catch ( Exception e )
                {
                    logger.error( "Exception received while Sending Post notification for AppType="
                        + holder.getRequester().getAppType() + ", with NotificationType=" + notification.getEventType()
                        + " , and TargetId=" + notification.getTargetId(), e );
                }
            }
            return;
        }
    }

    public void postEventCallBack( EventHolder holder, HMSNotificationRequest notification, HmsNode hmsNode )
    {
        logger.debug( "Got subscribers for event " + notification.getEventType() );
        hmsNode.setMonitorExecutionLog( "GOT SUBSCRIBERS FOR EVENT : " + notification.getEventType() + " THREAD : "
            + Thread.currentThread().getId() + " TIME :" + ( new Date() ).toString(), true );
        ObjectMapper mapper = new ObjectMapper();
        HMSNotificationRequest[] arrNotification = { notification };
        // Event event = holder.getCallBackEvent(EventType.valueOf(notification.getEventType()));
        List<Event> events = holder.getCallBackEvents( EventType.valueOf( notification.getEventType() ) );
        if ( events != null )
        {
            for ( int i = 0; i < events.size(); i++ )
            {
                Event event = events.get( i );
                if ( event != null )
                {
                    try
                    {
                        // String[] urlParts = {holder.getRequester().getBaseUrl(), event.getNotificationUrl()};
                        List<String> urlParts = new ArrayList<String>();
                        urlParts.add( holder.getRequester().getBaseUrl() );
                        urlParts.add( event.getNotificationUrl() );
                        String url = HttpUtil.buildUrl( urlParts );// "http://10.113.225.133:8080/vrm-ui/rest/notifications/";//
                        // String url = holder.getRequester().getBaseUrl()+event.getNotificationUrl();
                        logger.debug( "Triggered notification callback for event " + notification.getEventType()
                            + " to URL " + url + " for node" + hmsNode.getManagementIp() );
                        hmsNode.setMonitorExecutionLog( "TRIGGER HTTP CALLBACK FOR EVENT : "
                            + notification.getEventType() + " URL : " + url + " THREAD : "
                            + Thread.currentThread().getId() + " TIME :" + ( new Date() ).toString(), true );
                        switch ( event.getEventType() )
                        {
                            case SWITCH_FAILURE:
                            case SWITCH_UP:
                            case HOST_FAILURE:
                            case HOST_UP:
                            case HMS_OUT_OF_RESOURCES:
                            case HMS_FAILURE:
                                HttpClientService.getInstance().post( url, mapper.writeValueAsString( arrNotification ),
                                                                      true, true );
                                // HttpUtil.executeRequestAsync(url, RequestMethod.POST,
                                // mapper.writeValueAsString(arrNotification));
                                event.setLastUpdatedTime( ( new Date() ).getTime() );
                                break;
                            case HOST_MONITOR:
                            case SWITCH_MONITOR:
                                // HttpUtil.executeRequestAsync(url, RequestMethod.POST,
                                // mapper.writeValueAsString(arrNotification));
                                HttpClientService.getInstance().post( url, mapper.writeValueAsString( arrNotification ),
                                                                      true, true );
                                event.setLastUpdatedTime( ( new Date() ).getTime() );
                                break;
                            case BMC_FW_HEALTH:
                            case IPMI_WATCHDOG:
                            case POWER_SUPPLY:
                            case SYSTEMBOARD_TEMPERATURE:
                            case POWER_SUPPLY_FAN:
                            case HDD_STATUS:
                            case CHASSIS_SECURITY:
                            case PROCESSOR:
                            case PROCESSOR_FAN:
                            case PROCESSOR_VOLTAGE:
                            case PROCESSOR_TEMPERATURE:
                            case MEMORY:
                            case MEMORY_VOLTAGE:
                            case MEMORY_TEMPERATURE:
                                HttpClientService.getInstance().post( url, mapper.writeValueAsString( arrNotification ),
                                                                      true, true );
                                event.setLastUpdatedTime( ( new Date() ).getTime() );
                                break;
                        }
                        logger.debug( "Callback completed for event " + notification.getEventType() + " to URL "
                            + url );
                        hmsNode.setMonitorExecutionLog( "HTTP CALLBACK COMPLETE FOR EVENT : "
                            + notification.getEventType() + " URL : " + url + " THREAD : "
                            + Thread.currentThread().getId() + " TIME :" + ( new Date() ).toString(), true );
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Exception received while posting notification for "
                            + events.get( i ).getEventType(), e );
                    }
                }
            }
        }
    }
}
