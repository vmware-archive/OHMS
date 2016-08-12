/* ********************************************************************************
 * EventType.java
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
