/* ********************************************************************************
 * SwitchBgpConfigAssemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.ArrayList;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchBgpConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchNetworkPrefix;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;

public final class SwitchBgpConfigAssemblers
{
    public static NBSwitchBgpConfig toSwitchBgpConfig( SwitchBgpConfig config )
    {
        NBSwitchBgpConfig lConfig = new NBSwitchBgpConfig();
        if ( config == null || config.isEnabled() == false )
            return null;
        lConfig.setLocalAsn( config.getLocalAsn() );
        lConfig.setLocalIpAddress( config.getLocalIpAddress() );
        lConfig.setPeerAsn( config.getPeerAsn() );
        lConfig.setPeerIpAddress( config.getPeerIpAddress() );
        lConfig.setExportedNetworks( new ArrayList<NBSwitchNetworkPrefix>() );
        if ( config.getExportedNetworks() != null )
        {
            for ( String prefix : config.getExportedNetworks() )
            {
                lConfig.getExportedNetworks().add( SwitchNetworkPrefixAssemblers.toSwitchNetworkPrefix( prefix ) );
            }
        }
        return lConfig;
    }
}
