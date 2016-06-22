/* ********************************************************************************
 * SwitchHardwareInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
