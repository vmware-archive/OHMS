/* ********************************************************************************
 * MemoryInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;

/**
 * Class for Memory related Properties
 *
 * @author VMware Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class MemoryInfo
    extends FruComponent
{
    private String id;

    private BigInteger memoryCapacityInBytes;

    private String memoryType;

    private Long memorySpeedInHertz;

    private String operationalStatus;

    private String hostId;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getMemoryType()
    {
        return memoryType;
    }

    public void setMemoryType( String memoryType )
    {
        this.memoryType = memoryType;
    }

    public BigInteger getMemoryCapacityInBytes()
    {
        return memoryCapacityInBytes;
    }

    public void setMemoryCapacityInBytes( BigInteger memoryCapacityInBytes )
    {
        this.memoryCapacityInBytes = memoryCapacityInBytes;
    }

    public Long getMemorySpeedInHertz()
    {
        return memorySpeedInHertz;
    }

    public void setMemorySpeedInHertz( Long memorySpeedInHertz )
    {
        this.memorySpeedInHertz = memorySpeedInHertz;
    }

    public String getOperationalStatus()
    {
        return operationalStatus;
    }

    public void setOperationalStatus( String operationalStatus )
    {
        this.operationalStatus = operationalStatus;
    }

    public String getHostId()
    {
        return hostId;
    }

    public void setHostId( String hostId )
    {
        this.hostId = hostId;
    }

    /**
     * Get the Physical Memory FRU Information Wrapper method to get the MemoryInfo object for the node
     *
     * @param serverNodeMemoryInfo
     * @param nodeID
     * @return MemoryInfo
     */
    public MemoryInfo getMemoryInfo( PhysicalMemory serverNodeMemoryInfo, String nodeID )
    {
        MemoryInfo memoryInfo = new MemoryInfo();
        memoryInfo.setId( serverNodeMemoryInfo.getId() );
        memoryInfo.setMemoryCapacityInBytes( serverNodeMemoryInfo.getCapacityInBytes() );
        memoryInfo.setMemorySpeedInHertz( serverNodeMemoryInfo.getMaxMemorySpeedInHertz() );
        memoryInfo.setMemoryType( serverNodeMemoryInfo.getMemoryType() );
        memoryInfo.setLocation( serverNodeMemoryInfo.getLocation() );
        memoryInfo.setHostId( nodeID );
        if ( serverNodeMemoryInfo.getComponentIdentifier() != null )
        {
            memoryInfo.setComponentIdentifier( serverNodeMemoryInfo.getComponentIdentifier() );
        }
        return memoryInfo;
    }
}
