/* ********************************************************************************
 * Constants.java
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
package com.vmware.vrack.hms.boardservice.ib.api;

/**
 * Constants used in HMS Inband
 * 
 * @author Yagnesh Chawda
 */
public class Constants
{
    /**
     * CLI Command for the Hdd Smart data via esxi cli
     */
    public static final String GET_HDD_SMART_DATA_COMMAND =
        "esxcli --formatter=csv storage core device smart get -d %s";

    /**
     * CLI Command for NIC packet data drop info
     */
    public static final String GET_NIC_PACKET_DROP_INFO_COMMAND = "esxcli --formatter=csv network nic stats get -n %s";

    /**
     * CLI command to get NIC firmware information
     */
    public static final String GET_NIC_FIRMWARE_INFO_COMMAND = "esxcli --formatter=csv network nic get -n %s";

    public static final String NIC_PORT_DOWN_SENSOR_DISCRETE_VALUE = "NIC port Down";

    public static final String NIC_PORT_UP_SENSOR_DISCRETE_VALUE = "NIC port Up";

    public static final String HEALTH_STATUS = "Health Status";

    public static final String MEDIA_WEAROUT_INDICATOR = "Media Wearout Indicator";

    public static final String WRITE_ERROR_COUNT = "Write Error Count";

    public static final String READ_ERROR_COUNT = "Read Error Count";

    public static final String POWER_ON_HOURS = "Power-on Hours";

    public static final String POWER_CYCLE_COUNT = "Power Cycle Count";

    public static final String REALLOCATED_SECTORS_COUNT = "Reallocated Sector Count";

    public static final String RAW_READ_ERROR_RATE = "Raw Read Error Rate";

    public static final String DRIVE_TEMPERATURE = "Drive Temperature";

    public static final String DRIVER_RATED_MAX_TEMPERATURE = "Driver Rated Max Temperature";

    public static final String WRITE_SECTORS_TOT_COUNT = "Write Sectors TOT Count";

    public static final String READ_SECTORS_TOT_COUNT = "Read Sectors TOT Count";

    public static final String INITIAL_BAD_BLOCK_COUNT = "Initial Bad Block Count";

    public static final String GET_ESXI_HOSTNAME_COMMAND = "esxcli system hostname get";

    public static final String HOST_CONNECTED_SWITCH_PORT =
        "vim-cmd hostsvc/net/query_networkhint | grep portId | awk -F'\"' '{print $2 }'";

    // Provides a mapping between HBAs and the storage devices
    public static final String GET_STORAGE_DEVICE_CONNECTED = "esxcfg-scsidevs -A";

    // Command helps to get the IsCapacityFlash given the storage device name
    public static final String GET_IS_CAPACITY_FLASH = "vdq -q -d {device}";

    public static final String OFFLINE = "offline";

    public static final String ONLINE = "online";

    public static final String UNBOUND = "unbound";

    public static final String UNKNOWN = "unknown";

}
