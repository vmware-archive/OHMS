/* ********************************************************************************
 * ManagementAccessInfo.java
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
