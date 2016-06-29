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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.vmware.vrack.hms.plugin.boardservice.redfish.client.OdataTypeResolver;

import java.net.URI;
import java.util.Set;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@odata.type",
    visible = true
)
@JsonTypeIdResolver( OdataTypeResolver.class )
@JsonIgnoreProperties( ignoreUnknown = true )
public abstract class RedfishResource
{
    @JsonIgnore
    private URI origin;

    @JsonProperty( "@odata.context" )
    private String odataContext;

    @JsonProperty( "@odata.id" )
    private String odataId;

    @JsonProperty( "@odata.type" )
    private String odataType;

    @JsonProperty( "Id" )
    private String id;

    @JsonProperty( "Description" )
    private String description;

    @JsonProperty( "Name" )
    private String name;

    public URI getOrigin()
    {
        return origin;
    }

    public void setOrigin( URI origin )
    {
        this.origin = origin;
    }

    public String getOdataContext()
    {
        return odataContext;
    }

    public String getOdataId()
    {
        return odataId;
    }

    public String getOdataType()
    {
        return odataType;
    }

    public String getId()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Returns all Links to other resources (represented by Redfish NavigationProperties)
     *
     * @return Set<URI> URIs of related resources
     */
    public abstract Set<OdataId> getRelatedResources();
}
