/* ********************************************************************************
 * CPUCache.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api.cpu;

import java.math.BigInteger;

/**
 * Class for different cache type of Processor
 * 
 * @author VMware, Inc.
 */
public class CPUCache
{
    private String type;

    private BigInteger sizeInBytes;

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public BigInteger getSizeInBytes()
    {
        return sizeInBytes;
    }

    public void setSizeInBytes( BigInteger sizeInBytes )
    {
        this.sizeInBytes = sizeInBytes;
    }
}
