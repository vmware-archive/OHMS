/* ********************************************************************************
 * NodeEvent.java
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
package com.vmware.vrack.hms.common.servernodes.api.event;

import com.vmware.vrack.common.event.enums.EventCatalog;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;

/**
 * @author VMware, Inc. ENUM to list various Sensors supported by HMS OOB API will need to map board specific sensors to
 *         HMS defined sensor. Sensor mentioned here will only be monitored and eligible for Event Generation.
 */
public enum NodeEvent
{
    CPU_TEMP_ABOVE_THRESHHOLD( ServerComponent.CPU, EventValueType.READING,
        EventCatalog.CPU_TEMPERATURE_ABOVE_UPPER_THRESHOLD ),
    CPU_TEMP_BELOW_THRESHHOLD( ServerComponent.CPU, EventValueType.READING,
        EventCatalog.CPU_TEMPERATURE_BELOW_LOWER_THRESHOLD ),
    CPU_THERMAL_TRIP( ServerComponent.CPU, EventValueType.DISCRETE, EventCatalog.CPU_THERMAL_TRIP ),
    CPU_CAT_ERROR( ServerComponent.CPU, EventValueType.DISCRETE, EventCatalog.CPU_CAT_ERROR ),
    CPU_INIT_ERROR( ServerComponent.CPU, EventValueType.DISCRETE, EventCatalog.CPU_INITIALIZATION_ERROR ),
    CPU_MACHINE_CHECK_ERROR( ServerComponent.CPU, EventValueType.DISCRETE, EventCatalog.CPU_MACHINE_CHECK_ERROR ),
    CPU_POST_FAILURE( ServerComponent.CPU, EventValueType.DISCRETE, EventCatalog.CPU_POST_FAILURE ),
    @Deprecated CPU_TEMPERATURE( ServerComponent.CPU, EventValueType.READING ),
    CPU_VOLTS( ServerComponent.CPU, EventValueType.READING ),
    @Deprecated CPU_STATUS( ServerComponent.CPU, EventValueType.DISCRETE ),
    @Deprecated CPU_FAILURE( ServerComponent.CPU, EventValueType.DISCRETE ),
    PCH_TEMP_ABOVE_THRESHOLD( ServerComponent.SERVER, EventValueType.READING,
        EventCatalog.PCH_TEMPERATURE_ABOVE_THRESHOLD ),
    MEMORY_TEMP_ABOVE_THRESHOLD( ServerComponent.MEMORY, EventValueType.READING,
        EventCatalog.DIMM_TEMPERATURE_ABOVE_UPPER_THRESHOLD ),
    @Deprecated MEMORY_THEMAL_MARGIN_CRITICAL_THRESHOLD( ServerComponent.MEMORY, EventValueType.DISCRETE ),
    MEMORY_ECC_ERROR( ServerComponent.MEMORY, EventValueType.DISCRETE, EventCatalog.DIMM_ECC_ERROR ),
    @Deprecated MEMORY_TEMPERATURE( ServerComponent.MEMORY, EventValueType.READING ),
    @Deprecated MEMORY_STATUS( ServerComponent.MEMORY, null ),
    @Deprecated MEMORY_FAILURE( ServerComponent.MEMORY, EventValueType.DISCRETE ),
    HDD_DOWN( ServerComponent.STORAGE, EventValueType.DISCRETE, EventCatalog.HDD_DOWN ),
    HDD_UP( ServerComponent.STORAGE, EventValueType.DISCRETE, EventCatalog.HDD_UP ),
    @Deprecated HDD_FAILURE( ServerComponent.STORAGE, EventValueType.DISCRETE ),
    @Deprecated HDD_STATUS( ServerComponent.STORAGE, EventValueType.DISCRETE ),
    HDD_WRITE_ERROR( ServerComponent.STORAGE, EventValueType.DISCRETE, EventCatalog.HDD_EXCESSIVE_WRITE_ERRORS ),
    HDD_READ_ERROR( ServerComponent.STORAGE, EventValueType.DISCRETE, EventCatalog.HDD_EXCESSIVE_READ_ERRORS ),
    HDD_TEMP_ABOVE_THRESHOLD( ServerComponent.STORAGE, EventValueType.READING,
        EventCatalog.HDD_TEMPERATURE_ABOVE_THRESHOLD ),
    HDD_WEAROUT_ABOVE_THRESHOLD( ServerComponent.STORAGE, EventValueType.DISCRETE,
        EventCatalog.HDD_WEAROUT_ABOVE_THRESHOLD ),
    @Deprecated HDD_HEALTH_CRITICAL( ServerComponent.STORAGE, EventValueType.DISCRETE ),
    @Deprecated HDD_EMPTY_DISK_BAY( ServerComponent.STORAGE, EventValueType.DISCRETE ),
    SSD_UP( ServerComponent.STORAGE, EventValueType.DISCRETE, EventCatalog.SSD_UP ),
    SSD_DOWN( ServerComponent.STORAGE, EventValueType.DISCRETE, EventCatalog.SSD_DOWN ),
    SSD_WRITE_ERROR( ServerComponent.STORAGE, EventValueType.DISCRETE, EventCatalog.SSD_EXCESSIVE_WRITE_ERRORS ),
    SSD_READ_ERROR( ServerComponent.STORAGE, EventValueType.DISCRETE, EventCatalog.SSD_EXCESSIVE_READ_ERRORS ),
    SSD_TEMP_ABOVE_THRESHOLD( ServerComponent.STORAGE, EventValueType.READING,
        EventCatalog.SSD_TEMPERATURE_ABOVE_THRESHOLD ),
    SSD_WEAROUT_ABOVE_THRESHOLD( ServerComponent.STORAGE, EventValueType.DISCRETE,
        EventCatalog.SSD_WEAROUT_ABOVE_THRESHOLD ),
    STORAGE_CONTROLLER_UP( ServerComponent.STORAGE_CONTROLLER, EventValueType.DISCRETE,
        EventCatalog.STORAGE_CONTROLLER_UP ),
    STORAGE_CONTROLLER_DOWN( ServerComponent.STORAGE_CONTROLLER, EventValueType.DISCRETE,
        EventCatalog.STORAGE_CONTROLLER_DOWN ),
    // Drive slot/Bay specific events
    @Deprecated HDD_SLOT_EMPTY( ServerComponent.STORAGE, EventValueType.DISCRETE ),
    @Deprecated HDD_SLOT_FULL( ServerComponent.STORAGE, EventValueType.DISCRETE ),
    @Deprecated FAN_SPEED( ServerComponent.FAN, EventValueType.READING ),
    @Deprecated FAN_FAILURE( ServerComponent.FAN, EventValueType.DISCRETE ),
    @Deprecated FAN_STATUS_NON_RECOVERABLE( ServerComponent.FAN, EventValueType.DISCRETE ),
    @Deprecated FAN_SPEED_THRESHHOLD( ServerComponent.FAN, EventValueType.READING ),
    @Deprecated POWERUNIT_TEMP_ABOVE_THRESHOLD( ServerComponent.POWERUNIT, EventValueType.READING ),
    @Deprecated POWER_UNIT_STATUS_FAILURE( ServerComponent.POWERUNIT, EventValueType.DISCRETE ),
    @Deprecated POWER_UNIT_STATUS( ServerComponent.POWERUNIT, EventValueType.DISCRETE ),
    BMC_NOT_REACHABLE( ServerComponent.BMC, EventValueType.DISCRETE, EventCatalog.BMC_NOT_REACHABLE ),
    BMC_FAILURE( ServerComponent.BMC, EventValueType.DISCRETE, EventCatalog.BMC_MANAGEMENT_FAILURE ),
    BMC_AUTHENTICATION_FAILURE( ServerComponent.BMC, EventValueType.DISCRETE, EventCatalog.BMC_AUTHENTICATION_FAILURE ),
    @Deprecated BMC_STATUS( ServerComponent.BMC, EventValueType.DISCRETE ),
    SYSTEM_PCIE_ERROR( ServerComponent.SERVER, EventValueType.DISCRETE, EventCatalog.SERVER_PCIE_ERROR ),
    SYSTEM_POST_ERROR( ServerComponent.SERVER, EventValueType.DISCRETE, EventCatalog.SERVER_POST_ERROR ),
    @Deprecated SYSTEM_REBOOT( ServerComponent.SYSTEM, EventValueType.DISCRETE ),
    @Deprecated SYSTEM_POWERUP_FAILURE( ServerComponent.SYSTEM, EventValueType.DISCRETE ),
    @Deprecated SYSTEM_SET_BOOT_ORDER_FAILURE( ServerComponent.SYSTEM, EventValueType.DISCRETE ),
    @Deprecated SYSTEM_OS_BOOTUP_FAILURE( ServerComponent.SYSTEM, EventValueType.DISCRETE ),
    @Deprecated SYSTEM_STATUS( ServerComponent.SYSTEM, EventValueType.DISCRETE ),
    NIC_LINK_DOWN( ServerComponent.NIC, EventValueType.DISCRETE, EventCatalog.NIC_LINK_DOWN ),
    NIC_PORT_DOWN( ServerComponent.NIC, EventValueType.DISCRETE, EventCatalog.NIC_PORT_DOWN ),
    NIC_PORT_UP( ServerComponent.NIC, EventValueType.DISCRETE, EventCatalog.NIC_PORT_UP ),
    NIC_PACKET_DROP_ABOVE_THRESHHOLD( ServerComponent.NIC, EventValueType.READING,
        EventCatalog.NIC_PACKET_DROP_ABOVE_THRESHOLD ),
    NIC_TEMPERATURE_ABOVE_THRESHHOLD( ServerComponent.NIC, EventValueType.READING ),
    NIC_PACKET_TRANSFER_RATE( ServerComponent.NIC, EventValueType.READING ),
    NIC_TEMPERATURE( ServerComponent.NIC, EventValueType.READING ),
    MANAGEMENT_SWITCH_DOWN( SwitchComponentEnum.SWITCH, EventValueType.DISCRETE, EventCatalog.MANAGEMENT_SWITCH_DOWN ),
    MANAGEMENT_SWITCH_UP( SwitchComponentEnum.SWITCH, EventValueType.DISCRETE, EventCatalog.MANAGEMENT_SWITCH_UP ),
    TOR_SWITCH_DOWN( SwitchComponentEnum.SWITCH, EventValueType.DISCRETE, EventCatalog.TOR_SWITCH_DOWN ),
    TOR_SWITCH_UP( SwitchComponentEnum.SWITCH, EventValueType.DISCRETE, EventCatalog.TOR_SWITCH_UP ),
    SPINE_SWITCH_DOWN( SwitchComponentEnum.SWITCH, EventValueType.DISCRETE, EventCatalog.SPINE_SWITCH_DOWN ),
    SPINE_SWITCH_UP( SwitchComponentEnum.SWITCH, EventValueType.DISCRETE, EventCatalog.SPINE_SWITCH_UP ),
    MANAGEMENT_SWITCH_PORT_DOWN( SwitchComponentEnum.SWITCH_PORT, EventValueType.DISCRETE,
        EventCatalog.MANAGEMENT_SWITCH_PORT_DOWN ),
    MANAGEMENT_SWITCH_PORT_UP( SwitchComponentEnum.SWITCH_PORT, EventValueType.DISCRETE,
        EventCatalog.MANAGEMENT_SWITCH_PORT_UP ),
    TOR_SWITCH_PORT_DOWN( SwitchComponentEnum.SWITCH_PORT, EventValueType.DISCRETE, EventCatalog.TOR_SWITCH_PORT_DOWN ),
    TOR_SWITCH_PORT_UP( SwitchComponentEnum.SWITCH_PORT, EventValueType.DISCRETE, EventCatalog.TOR_SWITCH_PORT_UP ),
    SPINE_SWITCH_PORT_DOWN( SwitchComponentEnum.SWITCH_PORT, EventValueType.DISCRETE,
        EventCatalog.SPINE_SWITCH_PORT_DOWN ),
    SPINE_SWITCH_PORT_UP( SwitchComponentEnum.SWITCH_PORT, EventValueType.DISCRETE, EventCatalog.SPINE_SWITCH_PORT_UP ),
    @Deprecated HMS_AGENT_NON_RESPONSIVE( ServerComponent.HMS, EventValueType.DISCRETE ),
    @Deprecated HMS_AGENT_CPU_STATUS( ServerComponent.HMS, EventValueType.READING ),
    @Deprecated HMS_AGENT_MEMORY_STATUS( ServerComponent.HMS, EventValueType.READING ),
    @Deprecated HMS_AGENT_THREAD_COUNT( ServerComponent.HMS, EventValueType.READING ),
    @Deprecated HMS_OOB_AGENT_RESTHANDLER_MEAN_RESPONSETIME( ServerComponent.HMS, EventValueType.DISCRETE ),
    @Deprecated HMS_OOB_AGENT_RESTHANDLER_STATUS( ServerComponent.HMS, EventValueType.DISCRETE ),
    @Deprecated HMS_OOB_AGENT_RESTHANDLER_STARTED_DURATION( ServerComponent.HMS, EventValueType.DISCRETE ),
    @Deprecated HMS_OOB_AGENT_RESTHANDLER_MESSAGE_OUT_COUNT( ServerComponent.HMS, EventValueType.READING ),
    @Deprecated HMS_OOB_AGENT_RESTHANDLER_MESSAGE_IN_COUNT( ServerComponent.HMS, EventValueType.READING ),
    @Deprecated HOST_OS_NOT_RESPONSIVE( ServerComponent.OPERATING_SYSTEM, EventValueType.DISCRETE ),
    HOST_UP( ServerComponent.SERVER, EventValueType.DISCRETE, EventCatalog.SERVER_UP ),
    HOST_DOWN( ServerComponent.SERVER, EventValueType.DISCRETE, EventCatalog.SERVER_DOWN ),
    HMS_AGENT_UP( ServerComponent.HMS, EventValueType.DISCRETE, EventCatalog.HMS_AGENT_UP ),
    HMS_AGENT_DOWN( ServerComponent.HMS, EventValueType.DISCRETE, EventCatalog.HMS_AGENT_DOWN ),
    INVALID();
    private final ServerComponent component;

    private final SwitchComponentEnum switchComponentEnum;

    private final EventValueType valueType;

    private final EventCatalog eventID;

    private NodeEvent( ServerComponent component, EventValueType valueType, EventCatalog event )
    {
        this.component = component;
        this.valueType = valueType;
        this.eventID = event;
        this.switchComponentEnum = null;
    }

    private NodeEvent( SwitchComponentEnum switchComponentEnum, EventValueType valueType, EventCatalog event )
    {
        this.switchComponentEnum = switchComponentEnum;
        this.valueType = valueType;
        this.eventID = event;
        this.component = null;
    }

    private NodeEvent( ServerComponent component, EventValueType valueType )
    {
        this.component = component;
        this.valueType = valueType;
        this.eventID = null;
        this.switchComponentEnum = null;
    }

    private NodeEvent( SwitchComponentEnum switchComponentEnum, EventValueType valueType )
    {
        this.switchComponentEnum = switchComponentEnum;
        this.valueType = valueType;
        this.eventID = null;
        this.component = null;
    }

    private NodeEvent()
    {
        this.component = null;
        this.switchComponentEnum = null;
        this.valueType = null;
        this.eventID = null;
    }

    public ServerComponent getComponent()
    {
        return this.component;
    }

    public SwitchComponentEnum getSwitchComponentEnum()
    {
        return this.switchComponentEnum;
    }

    public EventValueType getValueType()
    {
        return this.valueType;
    }

    public EventCatalog getEventID()
    {
        return eventID;
    }
}
