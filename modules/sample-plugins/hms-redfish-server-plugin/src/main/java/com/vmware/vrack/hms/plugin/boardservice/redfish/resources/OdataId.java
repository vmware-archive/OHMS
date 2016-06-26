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
