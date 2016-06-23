/* ********************************************************************************
 * HmsApi.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api;

/**
 * Enum to hold various API exposed by OOB plugins
 *
 * @author VMware, Inc.
 */
public enum HmsApi
{
    CPU_INFO,
    STORAGE_INFO,
    POWERUNIT_INFO,
    MEMORY_INFO,
    FAN_INFO,
    NIC_INFO,
    STORAGE_CONTROLLER_INFO,
    CPU_SENSOR_INFO,
    STORAGE_SENSOR_INFO,
    POWERUNIT_SENSOR_INFO,
    MEMORY_SENSOR_INFO,
    FAN_SENSOR_INFO,
    NIC_SENSOR_INFO,
    STORAGE_CONTROLLER_SENSOR_INFO,
    SYSTEM_INFO,
    SYSTEM_SENSOR_INFO,
    SWITCH_INFO,
    SWITCH_PORT_INFO,
    SWITCH_SENSOR_INFO,
    SWITCH_PORT_SENSOR_INFO,
    SWITCH_FAN_SENSOR_INFO,
    SWITCH_POWERUNIT_SENSOR_INFO,
    SERVER_INFO,
    SERVER_POWER_STATUS,
    SERVER_POWER_OPERATIONS,
    MANAGEMENT_MAC_ADDRESS,
    MANAGEMENT_USERS,
    SELF_TEST,
    ACPI_POWER_STATE,
    BOOT_OPTIONS,
    SET_BOOT_OPTIONS,
    SET_CHASSIS_IDENTIFICATION,
    SET_MANAGEMENT_IP_ADDRESS,
    CREATE_MANAGEMENT_USER,
    SEL_DETAILS,
    REMOTE_CONSOLE_CAPABILITIES,
    START_REMOTE_CONSOLE_CONNECTION,
    SUPPORTED_HMS_API,
    // Needs a cleanup here, as OOB does NOT support below APIs
    HMS_INFO,
    HMS_HEALTH_INFO
}
