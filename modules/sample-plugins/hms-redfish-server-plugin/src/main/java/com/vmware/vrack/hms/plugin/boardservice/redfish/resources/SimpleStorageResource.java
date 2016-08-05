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

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class SimpleStorageResource
    extends RedfishResource
{
    @JsonProperty( "Status" )
    private Status status;

    @JsonProperty( "Devices" )
    private List<Device> devices;

    public Status getStatus()
    {
        return status;
    }

    public List<Device> getDevices()
    {
        return devices;
    }

    @Override
    public Set<OdataId> getRelatedResources()
    {
        return emptySet();
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    public static class Device
    {
        @JsonProperty( "CapacityBytes" )
        private BigInteger capacityBytes;

        @JsonProperty( "Status" )
        private Status status;

        @JsonProperty( "Name" )
        private String name;

        @JsonProperty( "Manufacturer" )
        private String manufacturer;

        @JsonProperty( "Model" )
        private String model;

        public BigInteger getCapacityBytes()
        {
            return capacityBytes;
        }

        public Status getStatus()
        {
            return status;
        }

        public String getName()
        {
            return name;
        }

        public String getManufacturer()
        {
            return manufacturer;
        }

        public String getModel()
        {
            return model;
        }
    }
}
