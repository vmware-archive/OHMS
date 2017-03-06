/* ********************************************************************************
 * SwitchConfig.java
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
package com.vmware.vrack.hms.common.core.model.switches;

import java.util.List;

import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;

public class SwitchConfig
{

    private List<SwitchVlanConfig> vlans;

    private SwitchBgpConfig bgp;

    private SwitchOspfv2Config ospf;

    private List<SwitchLagConfig> bonds;

    private SwitchMcLagConfig mcLag;

    private NodeAdminStatus adminStatus;

    private SwitchNtpConfig ntp;

    /**
     * @return the vlans
     */
    public List<SwitchVlanConfig> getVlans()
    {
        return vlans;
    }

    /**
     * @param vlans the vlans to set
     */
    public void setVlans( List<SwitchVlanConfig> vlans )
    {
        this.vlans = vlans;
    }

    /**
     * @return the bgp
     */
    public SwitchBgpConfig getBgp()
    {
        return bgp;
    }

    /**
     * @param bgp the bgp to set
     */
    public void setBgp( SwitchBgpConfig bgp )
    {
        this.bgp = bgp;
    }

    /**
     * @return the ospf
     */
    public SwitchOspfv2Config getOspf()
    {
        return ospf;
    }

    /**
     * @param ospf the ospf to set
     */
    public void setOspf( SwitchOspfv2Config ospf )
    {
        this.ospf = ospf;
    }

    /**
     * @return the bonds
     */
    public List<SwitchLagConfig> getBonds()
    {
        return bonds;
    }

    /**
     * @param bonds the bonds to set
     */
    public void setBonds( List<SwitchLagConfig> bonds )
    {
        this.bonds = bonds;
    }

    /**
     * @return the mcLag
     */
    public SwitchMcLagConfig getMcLag()
    {
        return mcLag;
    }

    /**
     * @param mcLag the mcLag to set
     */
    public void setMcLag( SwitchMcLagConfig mcLag )
    {
        this.mcLag = mcLag;
    }

    /**
     * @return the adminStatus
     */
    public NodeAdminStatus getAdminStatus()
    {
        return adminStatus;
    }

    /**
     * @param adminStatus the adminStatus to set
     */
    public void setAdminStatus( NodeAdminStatus adminStatus )
    {
        this.adminStatus = adminStatus;
    }

    /**
     * @return the ntp
     */
    public SwitchNtpConfig getNtp()
    {
        return ntp;
    }

    /**
     * @param ntp the ntp to set
     */
    public void setNtp( SwitchNtpConfig ntp )
    {
        this.ntp = ntp;
    }
}
