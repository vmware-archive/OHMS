/* ********************************************************************************
 * BaseCompatibilityInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
