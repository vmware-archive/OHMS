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

package com.vmware.vrack.hms.plugin.boardservice.redfish.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class OdataId
{
    @JsonProperty( "@odata.id" )
    private String id;

    public OdataId()
    {
    }

    private OdataId( String id )
    {
        this.id = id;
    }

    public static OdataId fromString( String id )
    {
        return new OdataId( id );
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public URI toUri()
    {
        if ( id != null && !id.isEmpty() )
        {
            return URI.create( id );
        }
        else
        {
            return null;
        }
    }

    @Override
    public String toString()
    {
        return id;
    }
}
