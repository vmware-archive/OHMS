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
