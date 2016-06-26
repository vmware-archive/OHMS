package com.vmware.vrack.hms.plugin.boardservice.redfish.discovery;

import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.OdataId;
import org.jboss.resteasy.util.Base64;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public final class IdentificationHelper
{
    private IdentificationHelper()
    {
    }

    public static String getUniqueIdForResource( URI serviceEndpoint, String odataId )
        throws RedfishResourcesInventoryException
    {
        URI baseUri = UriBuilder.fromUri( serviceEndpoint ).replacePath( null ).build();
        URI targetUri = URI.create( baseUri + odataId );
        return Base64.encodeBytes( targetUri.toString().getBytes() );
    }

    public static OdataId decodeResourceUniqueId( String nodeID )
        throws RedfishResourcesInventoryException
    {
        try
        {
            return OdataId.fromString( new String( Base64.decode( nodeID ) ) );
        }
        catch ( IOException e )
        {
            throw new RedfishResourcesInventoryException( "Provided NodeId is not compatible" );
        }
    }
}
