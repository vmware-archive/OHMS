/* ********************************************************************************
 * NBSwitchConfig.java
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
