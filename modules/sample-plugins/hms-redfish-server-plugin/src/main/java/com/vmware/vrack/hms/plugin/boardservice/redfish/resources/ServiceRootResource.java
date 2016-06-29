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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class ServiceRootResource
    extends RedfishResource
{
    @JsonProperty( "RedfishVersion" )
    private String redfishVersion;

    @JsonProperty( "UUID" )
    private UUID uuid;

    @JsonProperty( "Systems" )
    private OdataId systems;

    @JsonProperty( "Chassis" )
    private OdataId chassis;

    @JsonProperty( "Managers" )
    private OdataId managers;

    @JsonProperty( "EventService" )
    private OdataId eventService;

    @JsonProperty( "Oem" )
    private Object oem;

    @JsonProperty( "Links" )
    private Object links;

    public String getRedfishVersion()
    {
        return redfishVersion;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    @Override
    public Set<OdataId> getRelatedResources()
    {
        Set<OdataId> relatedResources = new HashSet<>();
        relatedResources.add( systems );
        relatedResources.add( chassis );
        relatedResources.add( managers );
        relatedResources.add( eventService );
        return relatedResources;
    }
}
