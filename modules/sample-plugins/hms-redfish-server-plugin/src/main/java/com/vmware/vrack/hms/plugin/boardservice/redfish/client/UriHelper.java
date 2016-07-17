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

        if ( targetUri == null )
        {
            throw new IllegalArgumentException( "Provided odataId is not valid" );
        }

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
