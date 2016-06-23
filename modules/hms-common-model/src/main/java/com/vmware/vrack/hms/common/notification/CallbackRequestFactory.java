/* ********************************************************************************
 * CallbackRequestFactory.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.notification;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Deprecated
public class CallbackRequestFactory
{
    public static HMSNotificationRequest getNotificationRequest( EventType type, String targetId,
                                                                 List<Map<String, String>> listData )
    {
        HMSNotificationRequest notification = new HMSNotificationRequest();
        notification.setEventTime( ( new Date() ).getTime() );
        notification.setTargetId( targetId );
        notification.setListData( listData );
        switch ( type )
        {
            case SWITCH_MONITOR:
                notification.setEventType( EventType.SWITCH_MONITOR.toString() );
                break;
            case HOST_FAILURE:
                notification.setEventType( EventType.HOST_FAILURE.toString() );
                break;
            case HOST_UP:
                notification.setEventType( EventType.HOST_UP.toString() );
                break;
            case HOST_MONITOR:
                notification.setEventType( EventType.HOST_MONITOR.toString() );
                break;
            case SWITCH_FAILURE:
                notification.setEventType( EventType.SWITCH_FAILURE.toString() );
                break;
            case SWITCH_UP:
                notification.setEventType( EventType.SWITCH_UP.toString() );
                break;
            case HMS_OUT_OF_RESOURCES:
                notification.setEventType( EventType.HMS_OUT_OF_RESOURCES.toString() );
                break;
            case HMS_FAILURE:
                notification.setEventType( EventType.HMS_FAILURE.toString() );
                break;
            case BMC_FW_HEALTH:
                notification.setEventType( EventType.BMC_FW_HEALTH.toString() );
                break;
            case IPMI_WATCHDOG:
                notification.setEventType( EventType.IPMI_WATCHDOG.toString() );
                break;
            case POWER_SUPPLY:
                notification.setEventType( EventType.POWER_SUPPLY.toString() );
                break;
            case SYSTEMBOARD_TEMPERATURE:
                notification.setEventType( EventType.SYSTEMBOARD_TEMPERATURE.toString() );
                break;
            case POWER_SUPPLY_FAN:
                notification.setEventType( EventType.POWER_SUPPLY_FAN.toString() );
                break;
            case HDD_STATUS:
                notification.setEventType( EventType.HDD_STATUS.toString() );
                break;
            case CHASSIS_SECURITY:
                notification.setEventType( EventType.CHASSIS_SECURITY.toString() );
                break;
            case PROCESSOR:
                notification.setEventType( EventType.PROCESSOR.toString() );
                break;
            case PROCESSOR_FAN:
                notification.setEventType( EventType.PROCESSOR_FAN.toString() );
                break;
            case PROCESSOR_VOLTAGE:
                notification.setEventType( EventType.PROCESSOR_VOLTAGE.toString() );
                break;
            case PROCESSOR_TEMPERATURE:
                notification.setEventType( EventType.PROCESSOR_TEMPERATURE.toString() );
                break;
            case MEMORY:
                notification.setEventType( EventType.MEMORY.toString() );
                break;
            case MEMORY_VOLTAGE:
                notification.setEventType( EventType.MEMORY_VOLTAGE.toString() );
                break;
            case MEMORY_TEMPERATURE:
                notification.setEventType( EventType.MEMORY_TEMPERATURE.toString() );
                break;
            default:
                break;
        }
        return notification;
    }
}
