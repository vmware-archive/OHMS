/* ********************************************************************************
 * SwitchHardwareInfo.java
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
package com.vmware.vrack.hms.common.switches.api;

public class SwitchHardwareInfo
{
    private String manufacturer;

    private String model;

    private String partNumber;

    private String chassisSerialId;

    private String managementMacAddress;

    private String manufactureDate;

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer( String manufacturer )
    {
        this.manufacturer = manufacturer;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel( String model )
    {
        this.model = model;
    }

    public String getChassisSerialId()
    {
        return chassisSerialId;
    }

    public void setChassisSerialId( String chassisSerialId )
    {
        this.chassisSerialId = chassisSerialId;
    }

    public String getManagementMacAddress()
    {
        return managementMacAddress;
    }

    public void setManagementMacAddress( String managementMacAddress )
    {
        this.managementMacAddress = managementMacAddress;
    }

    public String getPartNumber()
    {
        return partNumber;
    }

    public void setPartNumber( String partNumber )
    {
        this.partNumber = partNumber;
    }

    public String getManufactureDate()
    {
        return manufactureDate;
    }

    public void setManufactureDate( String manufactureDate )
    {
        this.manufactureDate = manufactureDate;
    }
}
