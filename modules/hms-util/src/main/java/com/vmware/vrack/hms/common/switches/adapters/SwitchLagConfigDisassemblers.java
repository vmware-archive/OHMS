/* ********************************************************************************
 * SwitchLagConfigDisassemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.ArrayList;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchLagConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;

public class SwitchLagConfigDisassemblers
{
    public static SwitchLacpGroup fromSwitchLagConfig( NBSwitchLagConfig config )
    {
        SwitchLacpGroup lConfig = new SwitchLacpGroup();
        if ( config == null )
            return null;
        lConfig.setMode( config.getMode() );
        lConfig.setMtu( config.getMtu() );
        lConfig.setName( config.getName() );
        lConfig.setPorts( new ArrayList<String>() );
        lConfig.setIpAddress( SwitchNetworkPrefixDisassemblers.fromSwitchNetworkPrefix( config.getIpAddress() ) );
        if ( config.getPorts() != null )
        {
            for ( String port : config.getPorts() )
            {
                lConfig.getPorts().add( port );
            }
        }
        return lConfig;
    }
}
