/* ********************************************************************************
 * SystemBootOptions.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource;

import com.vmware.vrack.hms.common.resource.chassis.BiosBootType;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceSelector;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceType;
import com.vmware.vrack.hms.common.resource.chassis.BootOptionsValidity;

/**
 * Class to hold the System Boot Options received by GetSystemBootOptionsCommand
 * 
 * @author VMware, Inc.
 */
public class SystemBootOptions
{
    private Boolean bootFlagsValid;

    private BootOptionsValidity bootOptionsValidity;

    private BiosBootType biosBootType;

    private BootDeviceType bootDeviceType;

    private BootDeviceSelector bootDeviceSelector;

    private Integer bootDeviceInstanceNumber;

    public Boolean getBootFlagsValid()
    {
        return bootFlagsValid;
    }

    public void setBootFlagsValid( Boolean bootFlagsValid )
    {
        this.bootFlagsValid = bootFlagsValid;
    }

    public BootOptionsValidity getBootOptionsValidity()
    {
        return bootOptionsValidity;
    }

    public void setBootOptionsValidity( BootOptionsValidity bootOptionsValidity )
    {
        this.bootOptionsValidity = bootOptionsValidity;
    }

    public BiosBootType getBiosBootType()
    {
        return biosBootType;
    }

    public void setBiosBootType( BiosBootType biosBootType )
    {
        this.biosBootType = biosBootType;
    }

    public BootDeviceType getBootDeviceType()
    {
        return bootDeviceType;
    }

    public void setBootDeviceType( BootDeviceType bootDeviceType )
    {
        this.bootDeviceType = bootDeviceType;
    }

    public BootDeviceSelector getBootDeviceSelector()
    {
        return bootDeviceSelector;
    }

    public void setBootDeviceSelector( BootDeviceSelector bootDeviceSelector )
    {
        this.bootDeviceSelector = bootDeviceSelector;
    }

    public Integer getBootDeviceInstanceNumber()
    {
        return bootDeviceInstanceNumber;
    }

    public void setBootDeviceInstanceNumber( Integer bootDeviceInstanceNumber )
    {
        this.bootDeviceInstanceNumber = bootDeviceInstanceNumber;
    }
}
