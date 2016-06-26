package com.vmware.vrack.hms.plugin.boardservice.redfish.mappers;

import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ManagerResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ManagerMapper
{

    public ManagerResource getBMC( List<ManagerResource> managers )
        throws MappingException
    {
        List<ManagerResource> detectedBmcs = new ArrayList<>();
        for ( ManagerResource manager : managers )
        {
            if ( Objects.equals( manager.getManagerType(), ManagerResource.ManagerType.BMC ) )
            {
                detectedBmcs.add( manager );
            }
        }

        if ( detectedBmcs.size() != 1 )
        {
            throw new MappingException( "No BMC or multiple BMC detected, unable to determine single MAC Address" );
        }
        return detectedBmcs.get( 0 );
    }
}
