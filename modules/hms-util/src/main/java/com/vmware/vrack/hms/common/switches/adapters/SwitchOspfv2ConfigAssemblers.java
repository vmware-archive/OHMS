/* ********************************************************************************
 * SwitchOspfv2ConfigAssemblers.java
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
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.ArrayList;
import java.util.List;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchOspfv2Config;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfGlobalConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfInterfaceConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfNetworkConfig;

public final class SwitchOspfv2ConfigAssemblers
{
    public static NBSwitchOspfv2Config toSwitchOspfv2Config( SwitchOspfConfig config )
    {
        NBSwitchOspfv2Config lConfig = new NBSwitchOspfv2Config();
        if ( config == null || config.isEnabled() == false )
            return null;
        lConfig.setDefaultMode( toOspfMode( config.getGlobal().getDefaultMode() ) );
        lConfig.setInterfaces( toInterfaces( config.getGlobal().getInterfaces() ) );
        lConfig.setNetworks( toNetworks( config.getGlobal().getNetworks() ) );
        lConfig.setRouterId( config.getGlobal().getRouterId() );
        return lConfig;
    }

    private static NBSwitchOspfv2Config.Network toNetwork( SwitchOspfNetworkConfig config )
    {
        NBSwitchOspfv2Config.Network lConfig = new NBSwitchOspfv2Config.Network();
        if ( config == null )
            return null;
        lConfig.setAreaId( config.getArea() );
        lConfig.setNetwork( SwitchNetworkPrefixAssemblers.toSwitchNetworkPrefix( config.getNetwork() ) );
        return lConfig;
    }

    private static List<NBSwitchOspfv2Config.Network> toNetworks( List<SwitchOspfNetworkConfig> configs )
    {
        List<NBSwitchOspfv2Config.Network> lNetworks = new ArrayList<NBSwitchOspfv2Config.Network>();
        if ( configs == null )
            return null;
        for ( SwitchOspfNetworkConfig config : configs )
        {
            lNetworks.add( toNetwork( config ) );
        }
        return lNetworks;
    }

    private static NBSwitchOspfv2Config.Interface toInterface( SwitchOspfInterfaceConfig config )
    {
        NBSwitchOspfv2Config.Interface lConfig = new NBSwitchOspfv2Config.Interface();
        if ( config == null )
            return null;
        lConfig.setMode( toInterfaceMode( config.getMode() ) );
        lConfig.setName( config.getName() );
        return lConfig;
    }

    private static List<NBSwitchOspfv2Config.Interface> toInterfaces( List<SwitchOspfInterfaceConfig> configs )
    {
        List<NBSwitchOspfv2Config.Interface> lInterfaces = new ArrayList<NBSwitchOspfv2Config.Interface>();
        if ( configs == null )
            return null;
        for ( SwitchOspfInterfaceConfig config : configs )
        {
            lInterfaces.add( toInterface( config ) );
        }
        return lInterfaces;
    }

    private static NBSwitchOspfv2Config.Mode toOspfMode( SwitchOspfGlobalConfig.OspfMode mode )
    {
        NBSwitchOspfv2Config.Mode lMode = null;
        if ( mode == null )
            return null;
        switch ( mode )
        {
            case ACTIVE:
                lMode = NBSwitchOspfv2Config.Mode.ACTIVE;
                break;
            case PASSIVE:
                lMode = NBSwitchOspfv2Config.Mode.PASSIVE;
                break;
            default:
                break;
        }
        return lMode;
    }

    private static NBSwitchOspfv2Config.Mode toInterfaceMode( SwitchOspfInterfaceConfig.InterfaceMode mode )
    {
        NBSwitchOspfv2Config.Mode lMode = null;
        if ( mode == null )
            return null;
        switch ( mode )
        {
            case ACTIVE:
                lMode = NBSwitchOspfv2Config.Mode.ACTIVE;
                break;
            case PASSIVE:
                lMode = NBSwitchOspfv2Config.Mode.PASSIVE;
                break;
            default:
                break;
        }
        return lMode;
    }
}
