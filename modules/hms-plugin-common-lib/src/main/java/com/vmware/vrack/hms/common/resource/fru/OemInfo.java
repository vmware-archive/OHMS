/* ********************************************************************************
 * OemInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
