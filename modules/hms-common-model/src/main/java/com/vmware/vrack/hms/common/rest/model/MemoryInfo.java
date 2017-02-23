/* ********************************************************************************
 * MemoryInfo.java
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

    private BigInteger memoryCapacityInBytes = BigInteger.ZERO;

    private String memoryType;

    private Long memorySpeedInHertz = 0L;

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
        if ( memoryCapacityInBytes != null )
        {
            this.memoryCapacityInBytes = memoryCapacityInBytes;
        }
    }

    public Long getMemorySpeedInHertz()
    {
        return memorySpeedInHertz;
    }

    public void setMemorySpeedInHertz( Long memorySpeedInHertz )
    {
        if ( memorySpeedInHertz != null )
        {
            this.memorySpeedInHertz = memorySpeedInHertz;
        }
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

        if ( serverNodeMemoryInfo.getCapacityInBytes() != null )
        {
            memoryInfo.setMemoryCapacityInBytes( serverNodeMemoryInfo.getCapacityInBytes() );
        }
        if ( serverNodeMemoryInfo.getMaxMemorySpeedInHertz() != null )
        {
            memoryInfo.setMemorySpeedInHertz( serverNodeMemoryInfo.getMaxMemorySpeedInHertz() );
        }
        memoryInfo.setMemoryType( serverNodeMemoryInfo.getMemoryType() );
        memoryInfo.setLocation( serverNodeMemoryInfo.getLocation() );
        memoryInfo.setHostId( nodeID );

        if ( serverNodeMemoryInfo.getFruOperationalStatus() != null )
        {
            memoryInfo.setOperationalStatus( serverNodeMemoryInfo.getFruOperationalStatus().toString() );
        }

        if ( serverNodeMemoryInfo.getComponentIdentifier() != null )
        {
            memoryInfo.setComponentIdentifier( serverNodeMemoryInfo.getComponentIdentifier() );
        }

        return memoryInfo;
    }
}
