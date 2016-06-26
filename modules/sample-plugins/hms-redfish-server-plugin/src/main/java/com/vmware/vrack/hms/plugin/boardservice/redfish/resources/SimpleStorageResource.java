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
