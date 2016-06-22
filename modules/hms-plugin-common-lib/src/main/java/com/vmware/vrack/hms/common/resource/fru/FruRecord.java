/* ********************************************************************************
 * FruRecord.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource.fru;

/**
 * General class for FRU Inventory area records.
 */
public abstract class FruRecord
{
    private FruType fruType;

    public FruType getFruType()
    {
        return fruType;
    }

    public void setFruType( FruType fruType )
    {
        this.fruType = fruType;
    }

    public FruRecord()
    {
    }
}
