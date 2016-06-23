/* ********************************************************************************
 * ManagementAccessInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource.fru;

/**
 * Management Access Information record from FRU Multi Record Area
 */
public class ManagementAccessInfo
    extends MultiRecordInfo
{
    private ManagementAccessRecordType recordType;

    private String accessInfo;

    /**
     * Creates and populates record
     *
     * @param fruData - raw data containing record
     * @param offset - offset to the record in the data
     * @param length - length of the record
     */
    public ManagementAccessRecordType getRecordType()
    {
        return recordType;
    }

    public void setRecordType( ManagementAccessRecordType recordType )
    {
        this.recordType = recordType;
    }

    public String getAccessInfo()
    {
        return accessInfo;
    }

    public void setAccessInfo( String accessInfo )
    {
        this.accessInfo = accessInfo;
    }
}
