/*
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.vmware.vrack.hms.plugin.boardservice.redfish.mappers;

import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.MemoryResource;

import java.math.BigInteger;
import java.util.List;

public class MemoryMapper
{
    private static final BigInteger MIB_TO_B_RATIO = BigInteger.valueOf( 1048576 );

    public PhysicalMemory map( MemoryResource memory )
    {
        PhysicalMemory physicalMemory = new PhysicalMemory();
        physicalMemory.setMemoryType( memory.getMemoryDeviceType() );
        physicalMemory.setCapacityInBytes( mapCapacity( memory.getCapacityMiB() ) );
        physicalMemory.setMaxMemorySpeedInHertz( mapMaxMemorySpeed( memory.getAllowedSpeedsMHz() ) );
        return physicalMemory;
    }

    private BigInteger mapCapacity( Integer capacityMiB )
    {
        return BigInteger.valueOf( capacityMiB ).multiply( MIB_TO_B_RATIO );
    }

    private Long mapMaxMemorySpeed( List<Long> allowedSpeedsMHz )
    {
        if ( allowedSpeedsMHz != null )
        {
            Long maxAllowedSpeed = null;
            for ( Long allowedSpeed : allowedSpeedsMHz )
            {
                if ( maxAllowedSpeed == null )
                {
                    maxAllowedSpeed = allowedSpeed;
                }
                else if ( maxAllowedSpeed < allowedSpeed )
                {
                    maxAllowedSpeed = allowedSpeed;
                }
            }
            return maxAllowedSpeed;
        }
        return null;
    }
}
