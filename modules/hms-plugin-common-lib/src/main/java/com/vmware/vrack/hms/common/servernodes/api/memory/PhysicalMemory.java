/* ********************************************************************************
 * PhysicalMemory.java
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
