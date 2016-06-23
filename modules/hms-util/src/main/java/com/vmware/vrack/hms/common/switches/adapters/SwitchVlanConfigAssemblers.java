/* ********************************************************************************
 * SwitchVlanConfigAssemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchVlanConfig;
import com.vmware.vrack.hms.common.switches.IpUtils;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.switches.api.SwitchVlanIgmp;

public final class SwitchVlanConfigAssemblers
{
    public static List<NBSwitchVlanConfig> toSwitchVlanConfigs( List<SwitchVlan> configs )
    {
        List<NBSwitchVlanConfig> lConfigs = new ArrayList<NBSwitchVlanConfig>();
        if ( configs == null )
            return lConfigs;
        for ( SwitchVlan config : configs )
        {
            lConfigs.add( toSwitchVlanConfig( config ) );
        }
        return lConfigs;
    }

    public static NBSwitchVlanConfig toSwitchVlanConfig( SwitchVlan config )
    {
        NBSwitchVlanConfig lConfig = new NBSwitchVlanConfig();
        String ipAddress = "";
        if ( config == null )
            return null;
        if ( config.getIpAddress() != null && config.getNetmask() != null )
        {
            ipAddress =
                String.format( "%s/%d", config.getIpAddress(), IpUtils.netmaskToPrefixLen( config.getNetmask() ) );
        }
        lConfig.setIgmp( toIgmp( config.getIgmp() ) );
        lConfig.setIpAddress( SwitchNetworkPrefixAssemblers.toSwitchNetworkPrefix( ipAddress ) );
        lConfig.setVid( config.getId() );
        lConfig.setTaggedPorts( new HashSet<String>() );
        lConfig.setUntaggedPorts( new HashSet<String>() );
        if ( config.getTaggedPorts() != null )
        {
            for ( String port : config.getTaggedPorts() )
            {
                lConfig.getTaggedPorts().add( port );
            }
        }
        if ( config.getUntaggedPorts() != null )
        {
            for ( String port : config.getUntaggedPorts() )
            {
                lConfig.getUntaggedPorts().add( port );
            }
        }
        return lConfig;
    }

    private static NBSwitchVlanConfig.Igmp toIgmp( SwitchVlanIgmp config )
    {
        NBSwitchVlanConfig.Igmp lConfig = new NBSwitchVlanConfig.Igmp();
        if ( config == null )
            return null;
        lConfig.setIgmpQuerier( config.getIgmpQuerier() );
        return lConfig;
    }
}
