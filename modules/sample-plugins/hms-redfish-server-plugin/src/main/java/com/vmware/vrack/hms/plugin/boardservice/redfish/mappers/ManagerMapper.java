package com.vmware.vrack.hms.plugin.boardservice.redfish.mappers;

import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ManagerResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ManagerMapper
{

    public ManagerResource getManagementController( List<ManagerResource> managers )
        throws MappingException
    {
        List<ManagerResource> detectedManagementControllers = new ArrayList<>();
        for ( ManagerResource manager : managers )
        {
            if ( Objects.equals( manager.getManagerType(), ManagerResource.ManagerType.ManagementController ) )
            {
                detectedManagementControllers.add( manager );
            }
        }

        if ( detectedManagementControllers.size() != 1 )
        {
            throw new MappingException(
                "No Management Controller or multiple Management Controllers detected, unable to determine single MAC Address" );
        }
        return detectedManagementControllers.get( 0 );
    }
}
