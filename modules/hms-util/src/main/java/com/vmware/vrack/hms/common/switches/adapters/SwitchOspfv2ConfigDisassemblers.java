/* ********************************************************************************
 * SwitchOspfv2ConfigDisassemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.ArrayList;
import java.util.List;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchOspfv2Config;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfGlobalConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfInterfaceConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfNetworkConfig;

public class SwitchOspfv2ConfigDisassemblers
{
    public static SwitchOspfConfig fromSwitchOspfv2Config( NBSwitchOspfv2Config config )
    {
        SwitchOspfConfig lConfig = new SwitchOspfConfig();
        if ( config == null )
            return null;
        lConfig.setGlobal( new SwitchOspfGlobalConfig() );
        lConfig.getGlobal().setDefaultMode( fromOspfMode( config.getDefaultMode() ) );
        lConfig.getGlobal().setInterfaces( fromInterfaces( config.getInterfaces() ) );
        lConfig.getGlobal().setNetworks( fromNetworks( config.getNetworks() ) );
        lConfig.getGlobal().setRouterId( config.getRouterId() );
        lConfig.setEnabled( true );
        return lConfig;
    }

    private static SwitchOspfNetworkConfig fromNetwork( NBSwitchOspfv2Config.Network config )
    {
        SwitchOspfNetworkConfig lConfig = new SwitchOspfNetworkConfig();
        if ( config == null )
            return null;
        lConfig.setArea( config.getAreaId() );
        lConfig.setNetwork( SwitchNetworkPrefixDisassemblers.fromSwitchNetworkPrefix( config.getNetwork() ) );
        return lConfig;
    }

    private static List<SwitchOspfNetworkConfig> fromNetworks( List<NBSwitchOspfv2Config.Network> configs )
    {
        List<SwitchOspfNetworkConfig> lNetworks = new ArrayList<SwitchOspfNetworkConfig>();
        if ( configs == null )
            return null;
        for ( NBSwitchOspfv2Config.Network config : configs )
        {
            lNetworks.add( fromNetwork( config ) );
        }
        return lNetworks;
    }

    private static SwitchOspfInterfaceConfig fromInterface( NBSwitchOspfv2Config.Interface config )
    {
        SwitchOspfInterfaceConfig lConfig = new SwitchOspfInterfaceConfig();
        if ( config == null )
            return null;
        lConfig.setMode( fromInterfaceMode( config.getMode() ) );
        lConfig.setName( config.getName() );
        return lConfig;
    }

    private static List<SwitchOspfInterfaceConfig> fromInterfaces( List<NBSwitchOspfv2Config.Interface> configs )
    {
        List<SwitchOspfInterfaceConfig> lInterfaces = new ArrayList<SwitchOspfInterfaceConfig>();
        if ( configs == null )
            return null;
        for ( NBSwitchOspfv2Config.Interface config : configs )
        {
            lInterfaces.add( fromInterface( config ) );
        }
        return lInterfaces;
    }

    private static SwitchOspfGlobalConfig.OspfMode fromOspfMode( NBSwitchOspfv2Config.Mode mode )
    {
        SwitchOspfGlobalConfig.OspfMode lMode = null;
        if ( mode == null )
            return null;
        switch ( mode )
        {
            case ACTIVE:
                lMode = SwitchOspfGlobalConfig.OspfMode.ACTIVE;
                break;
            case PASSIVE:
                lMode = SwitchOspfGlobalConfig.OspfMode.PASSIVE;
                break;
            default:
                break;
        }
        return lMode;
    }

    private static SwitchOspfInterfaceConfig.InterfaceMode fromInterfaceMode( NBSwitchOspfv2Config.Mode mode )
    {
        SwitchOspfInterfaceConfig.InterfaceMode lMode = null;
        if ( mode == null )
            return null;
        switch ( mode )
        {
            case ACTIVE:
                lMode = SwitchOspfInterfaceConfig.InterfaceMode.ACTIVE;
                break;
            case PASSIVE:
                lMode = SwitchOspfInterfaceConfig.InterfaceMode.PASSIVE;
                break;
            default:
                break;
        }
        return lMode;
    }
}
