/* ********************************************************************************
 * SwitchMclagInfo.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
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
