/* ********************************************************************************
 * HmsOobAgentDummyDataProvider.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.switches;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.switches.GetSwitchesResponse;
import com.vmware.vrack.hms.common.switches.GetSwitchesResponse.GetSwitchesResponseItem;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchMclagInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchNode.SwitchRoleType;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfGlobalConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfInterfaceConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfInterfaceConfig.InterfaceMode;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfNetworkConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortAutoNegMode;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortDuplexMode;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortStatus;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortType;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfGlobalConfig.OspfMode;

public class HmsOobAgentDummyDataProvider
{
    public static SwitchInfo getSwitchInfo()
    {
        SwitchInfo switchInfo = new SwitchInfo();
        ComponentIdentifier id = new ComponentIdentifier();
        switchInfo.setAdminStatus( "OPERATIONAL" );
        switchInfo.setComponentIdentifier( id );
        switchInfo.setDiscoverable( true );
        switchInfo.setFirmwareName( "TEST-FIRMWARE" );
        switchInfo.setFirmwareVersion( "TEST-FIRMWARE-VERSION" );
        switchInfo.setFruId( "TEST-FRU-ID" );
        switchInfo.setIpAddress( "5.5.5.5" );
        switchInfo.setLocation( "TEST-LOCATION" );
        switchInfo.setMangementPort( "eth0" );
        switchInfo.setOsName( "TEST-OS" );
        switchInfo.setOsVersion( "TEST-OS-VERSION" );
        switchInfo.setPowered( true );
        switchInfo.setManagementMacAddress( "aa:bb:cc:dd:ee:ff" );
        switchInfo.setOperational_status( "OPERATIONAL" );
        switchInfo.setRole( SwitchRoleType.TOR );
        switchInfo.setSwitchId( "S1" );
        switchInfo.setSwitchPorts( Arrays.asList( "swp1" ) );
        switchInfo.setValidationStatus( "VALIDATED" );
        return switchInfo;
    }

    public static SwitchPort getSwitchPort()
    {
        SwitchPort switchPort = new SwitchPort();
        switchPort.setAutoneg( PortAutoNegMode.ON );
        switchPort.setDuplex( PortDuplexMode.FULL );
        switchPort.setFlags( "BROADCAST,MULTICAST,UP,LOWER_UP" );
        switchPort.setIpAddress( "1.1.1.1/24" );
        switchPort.setMtu( 5000 );
        switchPort.setName( "swp1" );
        switchPort.setSpeed( "40G" );
        switchPort.setStatus( PortStatus.DOWN );
        switchPort.setType( PortType.SYNC );
        return switchPort;
    }

    public static List<SwitchPort> getSwitchPortList()
    {
        List<SwitchPort> switchPortList = new ArrayList<SwitchPort>();
        switchPortList.add( getSwitchPort() );
        return switchPortList;
    }

    public static List<String> getSwitchPortNameList()
    {
        return Arrays.asList( "swp1" );
    }

    public static SwitchVlan getSwitchVlan()
    {
        SwitchVlan vlan = new SwitchVlan();
        Set<String> taggedPorts = new HashSet<String>();
        Set<String> untaggedPorts = new HashSet<String>();
        taggedPorts.add( "swp1" );
        taggedPorts.add( "swp2" );
        taggedPorts.add( "swp3" );
        untaggedPorts.add( "swp10" );
        untaggedPorts.add( "swp11" );
        vlan.setId( "2011" );
        vlan.setIpAddress( "1.1.1.1" );
        vlan.setName( "no-name" );
        vlan.setNetmask( "255.255.255.0" );
        vlan.setTaggedPorts( taggedPorts );
        vlan.setUntaggedPorts( untaggedPorts );
        return vlan;
    }

    public static List<SwitchVlan> getSwitchVlanList()
    {
        List<SwitchVlan> vlanList = new ArrayList<SwitchVlan>();
        vlanList.add( getSwitchVlan() );
        return vlanList;
    }

    public static List<String> getSwitchVlanNameList()
    {
        return Arrays.asList( "2011" );
    }

    public static SwitchLacpGroup getSwitchLacpGroup()
    {
        SwitchLacpGroup lag = new SwitchLacpGroup();
        lag.setIpAddress( "1.1.1.1/24" );
        lag.setMode( "802.3ad" );
        lag.setName( "bd-test" );
        lag.setPorts( Arrays.asList( "swp20", "swp21", "swp22" ) );
        return lag;
    }

    public static List<SwitchLacpGroup> getSwitchLacpGroupList()
    {
        List<SwitchLacpGroup> lacpGroupList = new ArrayList<SwitchLacpGroup>();
        lacpGroupList.add( getSwitchLacpGroup() );
        return lacpGroupList;
    }

    public static List<String> getSwitchLacpGroupNameList()
    {
        return Arrays.asList( "bd-test" );
    }

    public static SwitchOspfConfig getSwitchOspf()
    {
        SwitchOspfConfig ospf = new SwitchOspfConfig();
        SwitchOspfGlobalConfig global = new SwitchOspfGlobalConfig();
        SwitchOspfNetworkConfig network = new SwitchOspfNetworkConfig();
        SwitchOspfInterfaceConfig iface = new SwitchOspfInterfaceConfig();
        iface.setMode( InterfaceMode.ACTIVE );
        iface.setName( "swp5" );
        network.setArea( "0.0.0.0" );
        network.setNetwork( "1.1.1.0/24" );
        global.setDefaultMode( OspfMode.PASSIVE );
        global.setInterfaces( Arrays.asList( iface ) );
        global.setNetworks( Arrays.asList( network ) );
        global.setRouterId( "2.2.2.1" );
        ospf.setEnabled( true );
        ospf.setGlobal( global );
        return ospf;
    }

    public static SwitchBgpConfig getSwitchBgp()
    {
        SwitchBgpConfig bgp = new SwitchBgpConfig();
        bgp.setEnabled( true );
        bgp.setExportedNetworks( Arrays.asList( "1.1.10.0/24", "1.1.10.0/24" ) );
        bgp.setLocalAsn( 100 );
        bgp.setLocalIpAddress( "1.1.1.1" );
        bgp.setPeerAsn( 200 );
        bgp.setPeerIpAddress( "1.1.1.2" );
        return bgp;
    }

    public static SwitchMclagInfo getSwitchMcLag()
    {
        SwitchMclagInfo mcLag = new SwitchMclagInfo();
        mcLag.setEnabled( true );
        mcLag.setInterfaceName( "swp46" );
        mcLag.setIpAddress( "1.1.1.1" );
        mcLag.setNetmask( "255.255.255.0" );
        mcLag.setPeerIp( "1.1.1.2" );
        mcLag.setSharedMac( "aq:bb:cc:dd:ee:ff" );
        return mcLag;
    }

    public static GetSwitchesResponse getSwitchIdList()
    {
        GetSwitchesResponse switchList = new GetSwitchesResponse();
        switchList.add( new GetSwitchesResponseItem( "S1", "192.168.100.20" ) );
        return switchList;
    }
}
