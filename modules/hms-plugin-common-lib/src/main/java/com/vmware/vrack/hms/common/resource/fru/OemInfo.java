/* ********************************************************************************
 * OemInfo.java
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
package com.vmware.vrack.hms.common.resource.fru;

/**
 * OEM record from FRU Multi Record Area
 */
public class OemInfo
    extends MultiRecordInfo
{
    private Integer manufacturerId;

    private byte[] oemData;

    public Integer getManufacturerId()
    {
        return manufacturerId;
    }

    public void setManufacturerId( Integer manufacturerId )
    {
        this.manufacturerId = manufacturerId;
    }

    public byte[] getOemData()
    {
        return oemData;
    }

    public void setOemData( byte[] oemData )
    {
        this.oemData = oemData;
    }
}
