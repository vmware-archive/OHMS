/* ********************************************************************************
 * EventType.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.notification;

@Deprecated
public enum EventType
{
    HOST_FAILURE,
    SWITCH_FAILURE,
    HOST_UP,
    SWITCH_UP,
    HOST_MONITOR,
    SWITCH_MONITOR,
    HMS_OUT_OF_RESOURCES,
    HMS_FAILURE,
    BMC_FW_HEALTH,
    IPMI_WATCHDOG,
    POWER_SUPPLY,
    SYSTEMBOARD_TEMPERATURE,
    POWER_SUPPLY_FAN,
    HDD_STATUS,
    CHASSIS_SECURITY,
    PROCESSOR,
    PROCESSOR_FAN,
    PROCESSOR_VOLTAGE,
    PROCESSOR_TEMPERATURE,
    MEMORY,
    MEMORY_VOLTAGE,
    MEMORY_TEMPERATURE,
    HMS_HANDSHAKE,
    CPU_STATUS,
    MEMORY_STATUS,
    POWER_UNIT_STATUS;
    public static EventType getEnumFromStr( String eventTypeStr )
    {
        for ( EventType eventType : EventType.values() )
        {
            if ( eventType.toString().equalsIgnoreCase( eventTypeStr ) )
            {
                return eventType;
            }
        }
        return null;
    }
}
