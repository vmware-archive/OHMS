/* ********************************************************************************
 * ChassisInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource.fru;

/**
 * FRU record containing Chassis info.<br>
 * This area is used to hold Serial Number, Part Number, and other information about the system chassis.
 */
public class ChassisInfo
    extends FruRecord
{
    private ChassisType chassisType;

    private String chassisPartNumber;

    private String chassisSerialNumber;

    private String[] customChassisInfo = new String[0];

    public ChassisType getChassisType()
    {
        return chassisType;
    }

    public void setChassisType( ChassisType chassisType )
    {
        this.chassisType = chassisType;
    }

    public String getChassisPartNumber()
    {
        return chassisPartNumber;
    }

    public void setChassisPartNumber( String chassisPartNumber )
    {
        this.chassisPartNumber = chassisPartNumber;
    }

    public String getChassisSerialNumber()
    {
        return chassisSerialNumber;
    }

    public void setChassisSerialNumber( String chassisSerialNumber )
    {
        this.chassisSerialNumber = chassisSerialNumber;
    }

    public String[] getCustomChassisInfo()
    {
        return customChassisInfo;
    }

    public void setCustomChassisInfo( String[] customChassisInfo )
    {
        this.customChassisInfo = customChassisInfo;
    }
}
