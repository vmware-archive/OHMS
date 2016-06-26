package com.vmware.vrack.hms.plugin.boardservice.redfish.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class Status
{
    @JsonProperty( "State" )
    private State state;

    @JsonProperty( "Health" )
    private String health;

    @JsonProperty( "HealthRollup" )
    private String healthRollup;

    public State getState()
    {
        return state;
    }

    public String getHealth()
    {
        return health;
    }

    public String getHealthRollup()
    {
        return healthRollup;
    }

    public enum State
    {
        Enabled,
        Disabled,
        Offline,
        InTest,
        Starting,
        Absent
    }
}
