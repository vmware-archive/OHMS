package com.vmware.vrack.hms.plugin.boardservice.redfish.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class DimmConfigResource
    extends RedfishResource
{
    @JsonProperty( "CapacityMiB" )
    private Integer capacityMiB;

    @JsonProperty( "OperatingSpeedMHz" )
    private Integer operatingSpeedMHz;

    @JsonProperty( "DimmDeviceType" )
    private String dimmDeviceType;

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

    public String getDimmDeviceType()
    {
        return dimmDeviceType;
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
