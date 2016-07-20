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
package com.vmware.vrack.hms.vsphere;

/**
 * Author: Tao Ma Date: 2/27/14
 */
public interface Constants
{
    // TODO: It depends on the default security for preconfiguration.
    // public static final String DEFAULT_HOST_USERNAME = "root";
    // public static final String DEFAULT_HOST_PASSWORD = "root123"; //"ca$hc0w";
    //
    /**
     * Initial admin user name in the newly instantiated appliance
     */
    public static final String DEFAULT_VC_USERNAME = "administrator@vsphere.local";

    /**
     * Initial admin password in the newly instantiated appliance
     */
    public static final String DEFAULT_VC_PASSWORD = "vmware";

    public static final String DEFAULT_DC_NAME = "DC-vRack";

    public static final String DEFAULT_CLUSTER_NAME = "Cluster-01";

    // Transaction
    public static final String TRAN_NEW_PORT_GROUP_NAME = "NEW_PORT_GROUP_NAME";

    public static final String TRAN_NEW_VMKNIC_DEVICE = "NEW_VMKNIC_DEVICE";

    // ESXi host configuration
    public static final String DEFAULT_VSWITCH_NAME = "vSwitch-vRack";

    public static final String[] ENPTY_STRING_ARRAY = new String[0];

    public static final String VRACK_NETWORK_MGMT = "vRack-DPortGroup-Mgmt";

    public static final String VRACK_NETWORK_VMOTION = "vRack-DPortGroup-vMotion";

    public static final String VRACK_NETWORK_VSAN = "vRack-DPortGroup-VSAN";

    public static final String VRACK_NETWORK_VM = "vRack-DPortGroup-VM";

    public static final String VRACK_NETWORK_NON_ROUTABLE = "vRack-DPortGroup-NonRoutable";

    public static final String VRACK_NETWORK_MGMT_HOST = "vRack-PortGroup-Mgmt";

    public static final String[] VRACK_DVPORTGROUPS =
        { VRACK_NETWORK_MGMT, VRACK_NETWORK_VMOTION, VRACK_NETWORK_VSAN, VRACK_NETWORK_VM, VRACK_NETWORK_NON_ROUTABLE };

    public static final String VRACK_DATACENTER = "vRack-Datacenter";

    public static final String VRACK_CLUSTER = "vRack-Cluster";

    public static final String VRACK_DVS = "vRack-DSwitch";

    public static final String DVSWITCH_UUID = "vRack-DvSwitch-UUID";

    public static final String IP_MGMT = "MGMT";

    public static final String IP_VMOTION = "VMOTION";

    public static final String IP_VSAN = "VSAN";

    public static final String IP_VXLAN = "VXLAN";

    public static final String IP_PUBLIC = "PUBLIC";

    public static final String IP_EXTERNAL = "EXTERNAL";

    public static final String IP_NON_ROUTABLE = "NON-ROUTABLE";

    public static final String IP_EXTERNAL_CONNECTION = "EXTERNAL_CONNECTION";

    public static final String IP_VSAN_MASTER_BROADCAST = "VSAN_MASTER_BROADCAST";

    public static final String IP_VSAN_AGENT_BROADCAST = "VSAN_AGENT_BROADCAST";
}
