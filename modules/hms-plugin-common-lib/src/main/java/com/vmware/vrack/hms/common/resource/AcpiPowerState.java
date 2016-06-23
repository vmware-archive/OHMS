/* ********************************************************************************
 * AcpiPowerState.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource;

/**
 * Contains the ACPI Power State of System as well as Device power state
 * 
 * @author VMware, Inc.
 */
public class AcpiPowerState
{
    private String systemAcpiPowerState;

    private String deviceAcpiPowerState;

    public String getSystemAcpiPowerState()
    {
        return systemAcpiPowerState;
    }

    public void setSystemAcpiPowerState( String systemAcpiPowerState )
    {
        this.systemAcpiPowerState = systemAcpiPowerState;
    }

    public String getDeviceAcpiPowerState()
    {
        return deviceAcpiPowerState;
    }

    public void setDeviceAcpiPowerState( String deviceAcpiPowerState )
    {
        this.deviceAcpiPowerState = deviceAcpiPowerState;
    }
}
