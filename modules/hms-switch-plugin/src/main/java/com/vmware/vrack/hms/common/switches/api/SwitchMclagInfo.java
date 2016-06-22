/* ********************************************************************************
 * SwitchMclagInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SwitchMclagInfo
{
    @JsonProperty( "interface" )
    private String interfaceName;

    private String ipAddress;

    private String netmask;

    private String peerIp;

    private String sharedMac;

    private boolean enabled;

    public String getInterfaceName()
    {
        return interfaceName;
    }

    public void setInterfaceName( String interfaceName )
    {
        this.interfaceName = interfaceName;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress( String ipAddress )
    {
        this.ipAddress = ipAddress;
    }

    public String getNetmask()
    {
        return netmask;
    }

    public void setNetmask( String netmask )
    {
        this.netmask = netmask;
    }

    public String getPeerIp()
    {
        return peerIp;
    }

    public void setPeerIp( String peerIp )
    {
        this.peerIp = peerIp;
    }

    public String getSharedMac()
    {
        return sharedMac;
    }

    public void setSharedMac( String sharedMac )
    {
        this.sharedMac = sharedMac;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }
}
