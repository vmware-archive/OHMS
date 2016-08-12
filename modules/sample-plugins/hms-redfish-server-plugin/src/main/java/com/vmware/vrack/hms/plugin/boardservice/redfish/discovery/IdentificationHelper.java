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
