package com.vmware.vrack.hms.plugin.boardservice.redfish.client;

import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.OdataId;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class UriHelper
{
    private UriHelper()
    {
    }

    public static URI toAbsoluteUri( URI origin, OdataId odataId )
    {
        URI targetUri = odataId.toUri();
        if ( targetUri.isAbsolute() )
        {
            return targetUri;
        }
        else
        {
            URI baseUri = UriBuilder.fromUri( origin ).replacePath( null ).build();
            return URI.create( baseUri.toString() + targetUri.toString() );
        }
    }
}
