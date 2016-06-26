package com.vmware.vrack.hms.plugin.boardservice.redfish.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class RedfishCollection
    extends RedfishResource
{
    @JsonProperty( "Members@odata.count" )
    private long membersOdataCount;

    @JsonProperty( "Members" )
    private List<OdataId> members;

    public long getMembersOdataCount()
    {
        return membersOdataCount;
    }

    public List<OdataId> getMembers()
    {
        return ( members == null ) ? Collections.<OdataId>emptyList() : members;
    }

    @Override
    public Set<OdataId> getRelatedResources()
    {
        return new HashSet<>( getMembers() );
    }
}
