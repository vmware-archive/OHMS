/* ********************************************************************************
 * SwitchBgpConfigDisassemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.ArrayList;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchBgpConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchNetworkPrefix;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;

public class SwitchBgpConfigDisassemblers
{
    public static SwitchBgpConfig fromSwitchBgpConfig( NBSwitchBgpConfig config )
    {
        SwitchBgpConfig lConfig = new SwitchBgpConfig();
        if ( config == null )
            return null;
        lConfig.setLocalAsn( config.getLocalAsn() );
        lConfig.setLocalIpAddress( config.getLocalIpAddress() );
        lConfig.setPeerAsn( config.getPeerAsn() );
        lConfig.setPeerIpAddress( config.getPeerIpAddress() );
        lConfig.setExportedNetworks( new ArrayList<String>() );
        lConfig.setEnabled( true );
        if ( config.getExportedNetworks() != null )
        {
            for ( NBSwitchNetworkPrefix prefix : config.getExportedNetworks() )
            {
                lConfig.getExportedNetworks().add( SwitchNetworkPrefixDisassemblers.fromSwitchNetworkPrefix( prefix ) );
            }
        }
        return lConfig;
    }
}
