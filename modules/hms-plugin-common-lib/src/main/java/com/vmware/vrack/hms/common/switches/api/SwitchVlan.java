/* ********************************************************************************
 * SwitchVlan.java
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

import java.util.Set;

public class SwitchVlan
{
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public Set<String> getTaggedPorts()
    {
        return taggedPorts;
    }

    public void setTaggedPorts( Set<String> taggedPorts )
    {
        this.taggedPorts = taggedPorts;
    }

    public Set<String> getUntaggedPorts()
    {
        return untaggedPorts;
    }

    public void setUntaggedPorts( Set<String> untaggedPorts )
    {
        this.untaggedPorts = untaggedPorts;
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

    public Integer getMtu()
    {
        return mtu;
    }

    public void setMtu( Integer mtu )
    {
        this.mtu = mtu;
    }

    public SwitchVlanIgmp getIgmp()
    {
        return igmp;
    }

    public void setIgmp( SwitchVlanIgmp igmp )
    {
        this.igmp = igmp;
    }

    private String name;

    private String id;

    private Set<String> taggedPorts;

    private Set<String> untaggedPorts;

    private String ipAddress;

    private String netmask;

    private SwitchVlanIgmp igmp;

    private Integer mtu;
}
