/* ********************************************************************************
 * SwitchLagConfigAssemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchLagConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchNetworkPrefix;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;

public final class SwitchLagConfigAssemblers
{
    public static NBSwitchLagConfig toSwitchLagConfig( SwitchLacpGroup config )
    {
        NBSwitchLagConfig lConfig = new NBSwitchLagConfig();
        if ( config == null )
            return null;
        lConfig.setMode( config.getMode() );
        lConfig.setMtu( config.getMtu() );
        lConfig.setName( config.getName() );
        lConfig.setIpAddress( SwitchNetworkPrefixAssemblers.toSwitchNetworkPrefix( config.getIpAddress() ) );
        lConfig.setPorts( new HashSet<String>() );
        if ( config.getPorts() != null )
        {
            for ( String port : config.getPorts() )
            {
                lConfig.getPorts().add( port );
            }
        }
        return lConfig;
    }

    public static List<NBSwitchLagConfig> toSwitchLagConfigs( List<SwitchLacpGroup> configs )
    {
        List<NBSwitchLagConfig> lConfigs = new ArrayList<NBSwitchLagConfig>();
        if ( configs == null )
            return lConfigs;
        for ( SwitchLacpGroup config : configs )
        {
            lConfigs.add( toSwitchLagConfig( config ) );
        }
        return lConfigs;
    }
}
