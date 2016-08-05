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

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class MemoryResource
    extends RedfishResource
{
    @JsonProperty( "CapacityMiB" )
    private Integer capacityMiB;

    @JsonProperty( "OperatingSpeedMHz" )
    private Integer operatingSpeedMHz;

    @JsonProperty( "MemoryDeviceType" )
    private String memoryDeviceType;

    @JsonProperty( "AllowedSpeedsMHz" )
    private List<Long> allowedSpeedsMHz;

    public Integer getCapacityMiB()
    {
        return capacityMiB;
    }

    public Integer getOperatingSpeedMHz()
    {
        return operatingSpeedMHz;
    }

    public String getMemoryDeviceType()
    {
        return memoryDeviceType;
    }

    public List<Long> getAllowedSpeedsMHz()
    {
        return allowedSpeedsMHz;
    }

    @Override
    public Set<OdataId> getRelatedResources()
    {
        return emptySet();
    }
}
