/* ********************************************************************************
 * SwitchConfigAssemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.List;

import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchConfig;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.switches.api.*;

public final class SwitchConfigAssemblers
{
    public static NBSwitchConfig toSwitchConfig( List<SwitchLacpGroup> lags, List<SwitchVlan> vlans,
                                                 SwitchOspfConfig ospf, SwitchBgpConfig bgp, SwitchMclagInfo mcLag,
                                                 SwitchInfo switchConfig )
    {
        NBSwitchConfig lConfig = new NBSwitchConfig();
        lConfig.setBgp( SwitchBgpConfigAssemblers.toSwitchBgpConfig( bgp ) );
        lConfig.setOspf( SwitchOspfv2ConfigAssemblers.toSwitchOspfv2Config( ospf ) );
        lConfig.setMcLag( SwitchMcLagConfigAssemblers.toSwitchMcLagConfig( mcLag ) );
        lConfig.setVlans( SwitchVlanConfigAssemblers.toSwitchVlanConfigs( vlans ) );
        lConfig.setBonds( SwitchLagConfigAssemblers.toSwitchLagConfigs( lags ) );
        return lConfig;
    }
}
