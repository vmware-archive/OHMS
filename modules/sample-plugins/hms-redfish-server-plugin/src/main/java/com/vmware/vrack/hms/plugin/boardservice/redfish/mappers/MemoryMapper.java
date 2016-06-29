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
