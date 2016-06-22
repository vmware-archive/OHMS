/* ********************************************************************************
 * SwitchItem.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.vrack.hms.common.switches.api.SwitchNode.SwitchRoleType;
import com.vmware.vrack.hms.common.switches.api.SwitchType;

@JsonInclude( JsonInclude.Include.NON_NULL )
public class SwitchItem
{
    private String id;

    private String protocol;

    private String ipAddress;

    private Integer port;

    private String username;

    private String password;

    private String topology;

    private String location;

    private SwitchRoleType role;

    private SwitchType type;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol( String protocol )
    {
        this.protocol = protocol;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress( String ipAddress )
    {
        this.ipAddress = ipAddress;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort( Integer port )
    {
        this.port = port;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getTopology()
    {
        return topology;
    }

    public void setTopology( String topology )
    {
        this.topology = topology;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation( String location )
    {
        this.location = location;
    }

    public SwitchRoleType getRole()
    {
        return role;
    }

    @JsonIgnore
    public void setRole( SwitchRoleType role )
    {
        this.role = role;
    }

    @JsonProperty( "role" )
    public void setRole( String role )
    {
        if ( role != null )
        {
            this.role = SwitchRoleType.valueOf( role.toUpperCase() );
        }
    }

    @JsonProperty( "switchInfo" )
    public SwitchType getType()
    {
        return type;
    }

    @JsonProperty( "switchInfo" )
    public void setType( SwitchType type )
    {
        this.type = type;
    }
}
