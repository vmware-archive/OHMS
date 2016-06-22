/* ********************************************************************************
 * NBSwitchConfig.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model.switches;

import java.util.List;

import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;

public class NBSwitchConfig
{
    private List<NBSwitchVlanConfig> vlans;

    private NBSwitchBgpConfig bgp;

    private NBSwitchOspfv2Config ospf;

    private List<NBSwitchLagConfig> bonds;

    private NBSwitchMcLagConfig mcLag;

    /**
     * @return the vlans
     */
    public List<NBSwitchVlanConfig> getVlans()
    {
        return vlans;
    }

    /**
     * @param vlans the vlans to set
     */
    public void setVlans( List<NBSwitchVlanConfig> vlans )
    {
        this.vlans = vlans;
    }

    /**
     * @return the bgp
     */
    public NBSwitchBgpConfig getBgp()
    {
        return bgp;
    }

    /**
     * @param bgp the bgp to set
     */
    public void setBgp( NBSwitchBgpConfig bgp )
    {
        this.bgp = bgp;
    }

    /**
     * @return the ospf
     */
    public NBSwitchOspfv2Config getOspf()
    {
        return ospf;
    }

    /**
     * @param ospf the ospf to set
     */
    public void setOspf( NBSwitchOspfv2Config ospf )
    {
        this.ospf = ospf;
    }

    /**
     * @return the bonds
     */
    public List<NBSwitchLagConfig> getBonds()
    {
        return bonds;
    }

    /**
     * @param bonds the bonds to set
     */
    public void setBonds( List<NBSwitchLagConfig> bonds )
    {
        this.bonds = bonds;
    }

    /**
     * @return the mcLag
     */
    public NBSwitchMcLagConfig getMcLag()
    {
        return mcLag;
    }

    /**
     * @param mcLag the mcLag to set
     */
    public void setMcLag( NBSwitchMcLagConfig mcLag )
    {
        this.mcLag = mcLag;
    }
}
