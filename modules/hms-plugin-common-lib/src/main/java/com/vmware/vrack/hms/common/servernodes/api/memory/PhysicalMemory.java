/* ********************************************************************************
 * PhysicalMemory.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api.memory;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.servernodes.api.AbstractServerComponent;

/**
 * Class for Physical Memory PhysicalMemory has the FRU component indentifiers which helps to identify the Server
 * component MEMORY FRU
 *
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class PhysicalMemory
    extends AbstractServerComponent
{
    private Long maxMemorySpeedInHertz;

    private BigInteger capacityInBytes;

    private String memoryType;

    public Long getMaxMemorySpeedInHertz()
    {
        return maxMemorySpeedInHertz;
    }

    public void setMaxMemorySpeedInHertz( Long maxMemorySpeedInHertz )
    {
        this.maxMemorySpeedInHertz = maxMemorySpeedInHertz;
    }

    public BigInteger getCapacityInBytes()
    {
        return capacityInBytes;
    }

    public void setCapacityInBytes( BigInteger capacityInBytes )
    {
        this.capacityInBytes = capacityInBytes;
    }

    public String getMemoryType()
    {
        return memoryType;
    }

    public void setMemoryType( String memoryType )
    {
        this.memoryType = memoryType;
    }
}
