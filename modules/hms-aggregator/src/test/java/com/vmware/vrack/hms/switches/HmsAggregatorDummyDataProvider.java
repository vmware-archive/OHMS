/* ********************************************************************************
 * HmsAggregatorDummyDataProvider.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.switches;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vmware.vrack.hms.common.rest.model.switches.*;
import com.vmware.vrack.hms.common.rest.model.switches.bulk.*;
import com.vmware.vrack.hms.common.switches.adapters.SwitchNetworkPrefixAssemblers;

public class HmsAggregatorDummyDataProvider
{
    private static final String DEFAULT_ROUTER_ID = "100.1.1.1";

    public static List<NBSwitchBulkConfig> getNBSwitchBulkConfigList()
    {
        List<NBSwitchBulkConfig> retVal = new ArrayList<NBSwitchBulkConfig>();
        retVal.add( getNBSwitchBulkConfig( NBSwitchBulkConfigEnum.BOND_MTU, 1 ) );
        retVal.add( getNBSwitchBulkConfig( NBSwitchBulkConfigEnum.PHYSICAL_SWITCH_PORT_MTU, 1 ) );
        return retVal;
    }

    private static NBSwitchBulkConfig getNBSwitchBulkConfig( NBSwitchBulkConfigEnum type, Integer index )
    {
        NBSwitchBulkConfig config = new NBSwitchBulkConfig();
        config.setFilters( new ArrayList<String>() );
        config.setType( type );
        config.setValues( new ArrayList<String>() );
        config.getValues().add( "1500" );
        switch ( type )
        {
            case BOND_MTU:
                config.getFilters().addAll( Arrays.asList( getBondName( index + 1 ), getBondName( index + 2 ),
                                                           getBondName( index + 3 ) ) );
                break;
            case PHYSICAL_SWITCH_PORT_MTU:
                config.getFilters().addAll( Arrays.asList( getPortName( index + 1 ), getPortName( index + 2 ),
                                                           getPortName( index + 3 ) ) );
                break;
        }
        return config;
    }

    public static NBSwitchNtpConfig getNBSwitchNtpConfig()
    {
        NBSwitchNtpConfig config = new NBSwitchNtpConfig();
        config.setTimeServerIpAddress( getIpAddress( 1 ) );
        return config;
    }

    public static NBSwitchPortConfig getNBSwitchPortConfig()
    {
        NBSwitchPortConfig config = new NBSwitchPortConfig();
        config.setAutoneg( NBSwitchPortConfig.PortAutoNegMode.ON );
        config.setDuplex( NBSwitchPortConfig.PortDuplexMode.HALF );
        config.setIpAddress( getNetworkPrefix( 1, 24 ) );
        config.setMtu( 1500 );
        config.setSpeed( "10G" );
        return config;
    }

    public static NBSwitchBgpConfig getNBSwitchBgpConfig()
    {
        NBSwitchBgpConfig config = new NBSwitchBgpConfig();
        config.setExportedNetworks( Arrays.asList( getNetworkPrefix( 0, 24 ), getNetworkPrefix( 1, 24 ) ) );
        config.setLocalAsn( 100 );
        config.setLocalIpAddress( getIpAddress( 1 ) );
        config.setPeerAsn( 200 );
        config.setPeerIpAddress( getIpAddress( 2 ) );
        return config;
    }

    public static NBSwitchLagConfig getNBSwitchLagConfig()
    {
        NBSwitchLagConfig config = new NBSwitchLagConfig();
        config.setMode( "802.3ad" );
        config.setMtu( 1500 );
        config.setName( getBondName( 1 ) );
        config.setIpAddress( getNetworkPrefix( 1, 24 ) );
        config.setPorts( ( getPortSet( 20, 2 ) ) );
        return config;
    }

    public static NBSwitchMcLagConfig getNBSwitchMcLagConfig()
    {
        NBSwitchMcLagConfig config = new NBSwitchMcLagConfig();
        config.setInterfaceName( getPortName( 1 ) );
        config.setMyIp( getIpAddress( 1 ) );
        config.setNetmask( "255.255.255.0" );
        config.setPeerIp( getIpAddress( 2 ) );
        config.setSystemId( "aa:bb:cc:dd:ee:ff" );
        return config;
    }

    private static NBSwitchNetworkPrefix getNetworkPrefix( Integer index, Integer prefixLen )
    {
        NBSwitchNetworkPrefix prefix = new NBSwitchNetworkPrefix();
        String ipAddress = String.format( "%s/%s", getIpAddress( index ), prefixLen );
        prefix = SwitchNetworkPrefixAssemblers.toSwitchNetworkPrefix( ipAddress );
        return prefix;
    }

    private static String getIpAddress( Integer index )
    {
        return String.format( "1.1.1.%s", index );
    }

    private static String getPortName( Integer index )
    {
        return String.format( "swp%s", index );
    }

    private static String getBondName( Integer index )
    {
        return String.format( "bd-%s", index );
    }

    public static NBSwitchOspfv2Config getNBSwitchOspfv2Config()
    {
        NBSwitchOspfv2Config config = new NBSwitchOspfv2Config();
        config.setDefaultMode( NBSwitchOspfv2Config.Mode.ACTIVE );
        config.setInterfaces( Arrays.asList( getNBSwitchOspfv2ConfigInterface( 1 ),
                                             getNBSwitchOspfv2ConfigInterface( 2 ) ) );
        config.setNetworks( Arrays.asList( getNBSwitchOspfv2ConfigNetwork( 1 ), getNBSwitchOspfv2ConfigNetwork( 2 ),
                                           getNBSwitchOspfv2ConfigNetwork( 3 ) ) );
        config.setRouterId( DEFAULT_ROUTER_ID );
        return config;
    }

    private static NBSwitchOspfv2Config.Network getNBSwitchOspfv2ConfigNetwork( Integer index )
    {
        NBSwitchOspfv2Config.Network config = new NBSwitchOspfv2Config.Network();
        config.setAreaId( "0" ); // backbone area
        config.setNetwork( getNetworkPrefix( 1, 24 ) );
        return config;
    }

    private static NBSwitchOspfv2Config.Interface getNBSwitchOspfv2ConfigInterface( Integer index )
    {
        NBSwitchOspfv2Config.Interface config = new NBSwitchOspfv2Config.Interface();
        config.setMode( NBSwitchOspfv2Config.Mode.PASSIVE );
        config.setName( getPortName( 1 ) );
        return config;
    }

    public static NBSwitchVlanConfig getNBSwitchVlanConfig()
    {
        NBSwitchVlanConfig config = new NBSwitchVlanConfig();
        config.setIgmp( getNBSwitchVlanConfigIgmp() );
        config.setIpAddress( getNetworkPrefix( 1, 24 ) );
        config.setTaggedPorts( getPortSet( 1, 3 ) );
        config.setUntaggedPorts( getPortSet( 10, 3 ) );
        config.setVid( "2015" );
        return config;
    }

    private static NBSwitchVlanConfig.Igmp getNBSwitchVlanConfigIgmp()
    {
        NBSwitchVlanConfig.Igmp config = new NBSwitchVlanConfig.Igmp();
        config.setIgmpQuerier( true );
        return config;
    }

    private static Set<String> getPortSet( Integer startIndex, Integer count )
    {
        Set<String> ports = new HashSet<String>();
        for ( Integer i = 0; i < count; ++i )
        {
            ports.add( getPortName( startIndex + i ) );
        }
        return ports;
    }
}
