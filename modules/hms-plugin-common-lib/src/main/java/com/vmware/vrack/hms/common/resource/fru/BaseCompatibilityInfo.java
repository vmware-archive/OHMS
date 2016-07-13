/* ********************************************************************************
 * BaseCompatibilityInfo.java
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
 * Base Compatibility Information record from FRU Multi Record Area
 */
public class BaseCompatibilityInfo
    extends MultiRecordInfo
{
    private Integer manufacturerId;

    private EntityId entityId;

    private Integer compatibilityBase;

    private Integer codeStart;

    private byte[] codeRangeMasks;

    public Integer getManufacturerId()
    {
        return manufacturerId;
    }

    public void setManufacturerId( Integer manufacturerId )
    {
        this.manufacturerId = manufacturerId;
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public void setEntityId( EntityId entityId )
    {
        this.entityId = entityId;
    }

    public Integer getCompatibilityBase()
    {
        return compatibilityBase;
    }

    public void setCompatibilityBase( Integer compatibilityBase )
    {
        this.compatibilityBase = compatibilityBase;
    }

    public Integer getCodeStart()
    {
        return codeStart;
    }

    public void setCodeStart( Integer codeStart )
    {
        this.codeStart = codeStart;
    }

    public byte[] getCodeRangeMasks()
    {
        return codeRangeMasks;
    }

    public void setCodeRangeMasks( byte[] codeRangeMasks )
    {
        this.codeRangeMasks = codeRangeMasks;
    }
}
