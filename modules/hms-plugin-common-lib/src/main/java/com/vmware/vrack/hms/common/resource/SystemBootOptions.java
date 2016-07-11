/* ********************************************************************************
 * SystemBootOptions.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
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
