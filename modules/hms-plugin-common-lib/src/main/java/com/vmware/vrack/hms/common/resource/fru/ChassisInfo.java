/* ********************************************************************************
 * ChassisInfo.java
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
