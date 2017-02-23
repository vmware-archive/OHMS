/* ********************************************************************************
 * LowLevelComponent.java
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
package com.vmware.vrack.hms.common.component.lifecycle.resource;

/**
 * The Enum UpgradeComponent.
 */
public enum LowLevelComponent
{
    /*
     * LCM Type: com.vmware.vrack.lcm.model.bundle.BundleSoftwareType defines - BIOS, FIRMWARE, LSI_FIRMWARE,
     * ESXI_LSI_DRIVER, ESXI, ESXI_VIB, VCENTER, NSX, ESX_HOST, BOOT_LOADER, SWITCH_OS, VRM, HMS_IB, HMS_OOB,
     * LOW_LEVEL_SWITCH, LOW_LEVEL_HOST; TODO: HMS may needs to define subset of the types defined by LCM for Low Level
     * Upgrades.
     */

    /** The bios. */
    BIOS( "BIOS" ),

    /** The bmc. */
    BMC( "BMC" ),

    /** The cpu. */
    CPU( "CPU" ),

    /** The hdd. */
    HDD( "HDD" ),

    /** The nic. */
    NIC( "NIC" ),

    /** The server. */
    SERVER( "SERVER" ),

    /** The switch. */
    SWITCH( "SWITCH" ),

    /** The system. */
    SYSTEM( "SYSTEM" ),

    /** The storage controller. */
    STORAGE_CONTROLLER( "STORAGE_CONTROLLER" );

    /** The component. */
    private String component;

    /**
     * Instantiates a new low level component.
     *
     * @param component the component
     */
    private LowLevelComponent( String component )
    {
        this.component = component;
    }

    /**
     * Gets the low level component.
     *
     * @return the low level component string.
     */
    public String getLowLevelComponent()
    {
        return this.component;
    }

    /**
     * Gets the low level component.
     *
     * @param component the component
     * @return the low level component enum. Returns null if given string not found.
     */
    public static LowLevelComponent getLowLevelComponent( String component )
    {
        if ( component != null )
        {
            for ( LowLevelComponent lowLevelComponent : LowLevelComponent.values() )
            {
                if ( component.equalsIgnoreCase( lowLevelComponent.component ) )
                {
                    return lowLevelComponent;
                }
            }
        }
        return null;
    }
}