/* ********************************************************************************
 * SwitchVxlan.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.api;

public class SwitchVxlan
{
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getVni()
    {
        return vni;
    }

    public void setVni( String vni )
    {
        this.vni = vni;
    }

    public String getVlanName()
    {
        return vlanName;
    }

    public void setVlanName( String vlanName )
    {
        this.vlanName = vlanName;
    }

    private String name;

    private String vni;

    private String vlanName;
}
