/* ********************************************************************************
 * FakeISwitchServiceData.java
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
package com.vmware.vrack.hms.common.fakeSwitchService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.HmsSensorState;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchHardwareInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchLinkedPort;
import com.vmware.vrack.hms.common.switches.api.SwitchMclagInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchNetworkConfiguration;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchOsInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfGlobalConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfInterfaceConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfNetworkConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortStatus;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortType;
import com.vmware.vrack.hms.common.switches.api.SwitchPortStatistics;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo.ChassisTemp;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo.FanSpeed;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo.PsuStatus;
import com.vmware.vrack.hms.common.switches.api.SwitchSession;
import com.vmware.vrack.hms.common.switches.api.SwitchType;
import com.vmware.vrack.hms.common.switches.api.SwitchUpgradeInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.switches.api.SwitchVxlan;

public class FakeISwitchServiceData
{

    private static final boolean discoverSwitch = true;

    private static final String CUMULUS_SWITCH_TYPE = "cumulus";

    private static final int HMS_SWITCH_CONNECTION_TIMEOUT = 20000;

    private static final String MAC_ADDRESS = "AA:BB:CC:DD:EE:FF";

    private static SwitchHardwareInfo switchHardwareInfo;

    private static SwitchOsInfo switchOsInfo;

    private static final Map<SwitchNode, List<String>> portListCache = new HashMap<SwitchNode, List<String>>();

    /**
     * Discover this switch and report whether you support it.
     * 
     * @param switchNode
     * @return true if supported, false if not
     */
    public static boolean discoverSwitch( SwitchNode switchNode )
    {
        return discoverSwitch;
    }

    /**
     * Name describing the switch type, for e.g. "cumulus", "arista", etc.
     * 
     * @return
     */
    public static String getSwitchType()
    {
        return CUMULUS_SWITCH_TYPE;
    }

    /**
     * Switch plugin to return list of switch types that are supported by the plugin.
     * 
     * @return List<SwitchType>
     */
    public static List<SwitchType> getSupportedSwitchTypes()
    {
        SwitchType s1 = new SwitchType();
        s1.setManufacturer( "Generic" );
        s1.setModel( ".*" );
        s1.setRegexMatching( true );

        ArrayList<SwitchType> retList = new ArrayList<SwitchType>();
        retList.add( s1 );
        return retList;
    }

    /**
     * Get session that's connected to the ToR switch
     * 
     * @return
     */
    public static SwitchSession getSession( SwitchNode switchNode )
    {
        Map<String, SwitchSession> sessionMap = new HashMap<String, SwitchSession>();
        String id = switchNode.getSwitchId();
        SwitchSession switchSession = sessionMap.get( id );

        switchSession = new FakeISwitchSession();
        switchSession.setSwitchNode( switchNode );

        sessionMap.put( id, switchSession );
        return switchSession;
    }

    /**
     * Is the switch node powered on?
     * 
     * @param switchNode
     * @return true if powered, false if not
     */
    public static boolean isPoweredOn( SwitchNode switchNode )
    {
        String addr = switchNode.getIpAddress();

        return ( ( addr != null ) ? true : false );
    }

    /**
     * Get switch os and firmware information, such as name and version
     * 
     * @param switchNode
     * @return
     */
    public static SwitchOsInfo getSwitchOsInfo( SwitchNode switchNode )
    {
        switchOsInfo = new SwitchOsInfo();
        switchOsInfo.setFirmwareName( "ONIE" );
        switchOsInfo.setFirmwareVersion( "1.6.1.2" );
        switchOsInfo.setLastBoot( new Date() );
        switchOsInfo.setOsName( "Cumulus Linux" );
        switchOsInfo.setOsVersion( "2.5.0" );
        return switchOsInfo;
    }

    /**
     * Get switch hardware information
     * 
     * @param switchNode
     * @return
     */
    public static SwitchHardwareInfo getSwitchHardwareInfo( SwitchNode switchNode )
    {
        switchHardwareInfo = new SwitchHardwareInfo();
        switchHardwareInfo.setChassisSerialId( "123456X1234567" );
        switchHardwareInfo.setManagementMacAddress( MAC_ADDRESS );
        switchHardwareInfo.setManufacturer( "Manufacturer" );
        switchHardwareInfo.setModel( "model" );
        return switchHardwareInfo;
    }

    /**
     * Update switch node information
     * 
     * @param switchNode
     * @param switchProperties
     * @return
     */
    public static boolean updateSwitchIpAddress( SwitchNode switchNode, String ipAddress, String netmask,
                                                 String gateway )
        throws HmsException
    {
        return false;
    }

    public static boolean rotateSwitchPassword( SwitchNode switchNode, String username, String newPassword )
        throws HmsException
    {
        return false;
    }

    /**
     * Get list of switch port names.
     * 
     * @return
     */
    public static List<String> getSwitchPortList( SwitchNode switchNode )
    {
        List<String> portList =
            Arrays.asList( "lo", "eth0", "swp1", "swp2", "swp3", "swp4", "swp5", "swp6", "swp7", "swp8", "swp9",
                           "swp10", "swp11", "swp12", "swp13", "swp14", "swp15", "swp16", "swp17", "swp18", "swp19",
                           "swp20", "swp21", "swp22", "swp23", "swp24", "swp25", "swp26", "swp27", "swp28", "swp29",
                           "swp30", "swp31", "swp32" );
        portListCache.put( switchNode, portList );

        if ( portList != null )
        {
            return portList;
        }
        else
        {
            portList = new ArrayList<String>();
        }

        return portList;
    }

    /**
     * Get switch port details for a particular port.
     * 
     * @param portName
     * @return TorSwitchPort
     */
    public static SwitchPort getSwitchPort( SwitchNode switchNode, String portName )
    {

        SwitchPort retPort = null;

        if ( portName != null && !portName.trim().equals( "" ) )
        {
            List<SwitchPort> allPorts = getSwitchPortListBulk( switchNode );
            for ( SwitchPort port : allPorts )
            {
                if ( portName.trim().equalsIgnoreCase( port.getName() ) )
                {
                    retPort = port;
                }
            }
        }

        return ( retPort );
    }

    /**
     * Get list of all switch ports
     * 
     * @return
     */
    public static List<SwitchPort> getSwitchPortListBulk( SwitchNode switchNode )
    {

        List<SwitchPort> switchPortList = new ArrayList();

        SwitchPort switchPort1 = new SwitchPort();
        switchPort1.setIfNumber( 2 );
        switchPort1.setName( "eth0" );
        switchPort1.setSpeed( null );
        switchPort1.setFlags( "BROADCAST,MULTICAST,UP,LOWER_UP" );
        switchPort1.setMtu( 1500 );
        switchPort1.setStatus( PortStatus.UP );
        switchPort1.setType( PortType.MANAGEMENT );
        switchPort1.setMacAddress( "70:72:cf:ac:75:d7" );
        SwitchPortStatistics stats1 = new SwitchPortStatistics();
        stats1.setTimestamp( new Date() );
        stats1.setTxSentPackets( 173240 );
        stats1.setTxDroppedPackets( 0 );
        stats1.setTxErrors( 0 );
        stats1.setRxReceivedPackets( 4801260 );
        stats1.setRxDroppedPackets( 1156 );
        stats1.setRxErrors( 0 );
        switchPort1.setStatistics( stats1 );
        switchPort1.setLinkedMacAddresses( null );
        SwitchLinkedPort linkedPort1 = new SwitchLinkedPort();
        linkedPort1.setPortName( "vmnic0" );
        linkedPort1.setMac( null );
        linkedPort1.setDeviceName( "localhost" );
        switchPort1.setLinkedPort( linkedPort1 );
        switchPortList.add( switchPort1 );

        SwitchPort switchPort2 = new SwitchPort();
        switchPort2.setIfNumber( 3 );
        switchPort2.setName( "swp1" );
        switchPort2.setSpeed( "40G" );
        switchPort2.setFlags( "BROADCAST,MULTICAST" );
        switchPort2.setMtu( 1500 );
        switchPort2.setStatus( PortStatus.DOWN );
        switchPort2.setType( PortType.SERVER );
        switchPort2.setMacAddress( "70:72:cf:ac:75:d8" );
        SwitchPortStatistics stats2 = new SwitchPortStatistics();
        stats2.setTimestamp( new Date() );
        stats2.setTxSentPackets( 0 );
        stats2.setTxDroppedPackets( 0 );
        stats2.setTxErrors( 0 );
        stats2.setRxReceivedPackets( 0 );
        stats2.setRxDroppedPackets( 0 );
        stats2.setRxErrors( 0 );
        switchPort2.setStatistics( stats2 );
        switchPort2.setLinkedMacAddresses( null );
        switchPort2.setLinkedPort( null );
        switchPortList.add( switchPort2 );

        SwitchPort switchPort3 = new SwitchPort();
        switchPort3.setIfNumber( 4 );
        switchPort3.setName( "swp2" );
        switchPort3.setSpeed( "40G" );
        switchPort3.setFlags( "NO-CARRIER,BROADCAST,MULTICAST,SLAVE,UP" );
        switchPort3.setMtu( 1500 );
        switchPort3.setStatus( PortStatus.UP );
        switchPort3.setType( PortType.SERVER );
        switchPort3.setMacAddress( "70:72:cf:ac:75:dc" );
        SwitchPortStatistics stats3 = new SwitchPortStatistics();
        stats3.setTimestamp( new Date() );
        stats3.setTxSentPackets( 0 );
        stats3.setTxDroppedPackets( 0 );
        stats3.setTxErrors( 0 );
        stats3.setRxReceivedPackets( 0 );
        stats3.setRxDroppedPackets( 0 );
        stats3.setRxErrors( 0 );
        switchPort3.setStatistics( stats3 );
        switchPort3.setLinkedMacAddresses( null );
        switchPort3.setLinkedPort( null );
        switchPortList.add( switchPort3 );

        return switchPortList;
    }

    /**
     * Get status of the specified port name.
     * 
     * @param switchNode
     * @param portName
     * @return PortStatus.UP or PortStatus.DOWN
     */
    public static PortStatus getSwitchPortStatus( SwitchNode switchNode, String portName )
    {
        SwitchPort port = getSwitchPort( switchNode, portName );

        return ( ( port != null ) ? port.getStatus() : null );
    }

    /**
     * Set status of the specified port name to the specified state (UP/DOWN).
     * 
     * @param switchNode
     * @param portName
     * @param portStatus
     * @return True if the operation was successful, false if not
     * @throws HmsException
     */
    public static boolean setSwitchPortStatus( SwitchNode switchNode, String portName, PortStatus portStatus )
        throws HmsException
    {
        return false;
    }

    /**
     * Update switch port configuration based on the specified portInfo.
     * 
     * @param switchNode
     * @param portName
     * @param portInfo
     * @return True if the operation was successful, false if not
     * @throws HmsException
     */
    public static boolean updateSwitchPort( SwitchNode switchNode, String portName, SwitchPort portInfo )
        throws HmsException
    {
        return false;
    }

    /**
     * Get list of vlans on this switch
     * 
     * @param switchNode
     * @return
     */
    public static List<String> getSwitchVlans( SwitchNode switchNode )
    {
        List<String> vlanNameList = new ArrayList<String>();
        List<SwitchVlan> vlanList = getSwitchVlansBulk( switchNode );

        for ( SwitchVlan vlan : vlanList )
        {
            vlanNameList.add( vlan.getName() );
        }

        return vlanNameList;
    }

    /**
     * Get details for a VLAN with specified ID
     * 
     * @param switchNode
     * @param vlanName
     * @return
     * @throws HmsException
     */
    public static SwitchVlan getSwitchVlan( SwitchNode switchNode, String vlanName )
        throws HmsException
    {

        List<SwitchVlan> vlanList = getSwitchVlansBulk( switchNode );

        for ( SwitchVlan vlan : vlanList )
        {
            if ( vlan.getName().equals( vlanName ) )
                return vlan;
        }

        return null;

    }

    /**
     * Get list of vlans on this switch
     * 
     * @param switchNode
     * @return
     */
    public static List<SwitchVlan> getSwitchVlansBulk( SwitchNode switchNode )
    {
        List<SwitchVlan> vlanList = new ArrayList();

        SwitchVlan switchVlanObj1 = new SwitchVlan();
        switchVlanObj1.setName( "1" );
        switchVlanObj1.setId( "1" );
        Set<String> taggedPorts = new HashSet<String>();
        switchVlanObj1.setTaggedPorts( taggedPorts );
        Set<String> unTaggedPorts = new HashSet<String>();
        unTaggedPorts.add( "swp16" );
        switchVlanObj1.setUntaggedPorts( unTaggedPorts );
        switchVlanObj1.setIpAddress( null );
        switchVlanObj1.setNetmask( null );
        switchVlanObj1.setMtu( null );
        vlanList.add( switchVlanObj1 );

        SwitchVlan switchVlanObj2 = new SwitchVlan();
        switchVlanObj2.setName( "12" );
        switchVlanObj2.setId( "12" );
        Set<String> taggedPorts2 = new HashSet<String>();
        taggedPorts.add( "swp16" );
        switchVlanObj2.setTaggedPorts( taggedPorts2 );
        Set<String> unTaggedPorts2 = new HashSet<String>();
        switchVlanObj2.setUntaggedPorts( unTaggedPorts2 );
        switchVlanObj2.setIpAddress( null );
        switchVlanObj2.setNetmask( null );
        switchVlanObj2.setMtu( null );
        vlanList.add( switchVlanObj2 );

        return vlanList;
    }

    /**
     * Create VLAN on the specified switch
     * 
     * @param switchNode
     * @param vlan
     * @return true if successful, false if not
     */
    public static boolean createVlan( SwitchNode switchNode, SwitchVlan vlan )
        throws HmsException
    {
        return false;
    }

    /**
     * Update VLAN with the specified details
     * 
     * @param switchNode
     * @param vlanName
     * @param vlan
     * @return
     * @throws HmsException
     */
    public static boolean updateVlan( SwitchNode switchNode, String vlanName, SwitchVlan vlan )
        throws HmsException
    {
        return false;
    }

    /**
     * Delete VLAN with specified ID
     * 
     * @param switchNode
     * @param vlanName
     * @return
     * @throws HmsException
     */
    public static boolean deleteVlan( SwitchNode switchNode, String vlanName )
        throws HmsException
    {
        return false;
    }

    /**
     * Get list of lacp groups on this switch
     * 
     * @param switchNode
     * @return
     */
    public static List<String> getSwitchLacpGroups( SwitchNode switchNode )
    {
        List<String> bondList = Arrays.asList( "bond1234", "bond12345", "bond7" );

        return bondList;
    }

    /**
     * Get details for a LACP Group with specified ID
     * 
     * @param switchNode
     * @param lacpGroupName
     * @return
     * @throws HmsException
     */
    public static SwitchLacpGroup getSwitchLacpGroup( SwitchNode switchNode, String lacpGroupName )
        throws HmsException
    {
        SwitchLacpGroup switchlacpgroup = new SwitchLacpGroup();
        switchlacpgroup.setName( "bond12345" );
        switchlacpgroup.setMode( "802.3ad" );
        List<String> ports = new ArrayList();
        ports.add( "swp8" );
        switchlacpgroup.setPorts( ports );

        return switchlacpgroup;
    }

    /**
     * Create LACP group (LAG) on the specified switch
     * 
     * @param switchNode
     * @param lacpGroup
     * @return
     * @throws HmsException
     */
    public static boolean createLacpGroup( SwitchNode switchNode, SwitchLacpGroup lacpGroup )
        throws HmsException
    {
        return false;
    }

    /**
     * Delete LACP group (LAG) with specified name
     * 
     * @param switchNode
     * @param lacpGroupName
     * @return true if successful, false if not
     * @throws HmsException
     */
    public static boolean deleteLacpGroup( SwitchNode switchNode, String lacpGroupName )
        throws HmsException
    {
        return false;
    }

    /**
     * Reboot the specified switchNode
     * 
     * @param switchNode Switch node
     * @return true if reboot was successful, false if not
     * @throws HmsException
     */
    public static boolean reboot( SwitchNode switchNode )
        throws HmsException
    {
        return false;
    }

    /**
     * Upgrade the specified switchNode
     * 
     * @param switchNode
     * @param upgradeInfo Upgrade-related information
     * @return true if upgrade was successful, false if not
     * @throws HmsException
     */
    public static boolean upgrade( SwitchNode switchNode, SwitchUpgradeInfo upgradeInfo )
        throws HmsException
    {
        return false;
    }

    /**
     * Apply network configuration to the specified switch node.
     * 
     * @param switchNode
     * @param networkConfiguration
     * @return true if successful, false if not
     * @throws HmsException
     */
    @Deprecated
    public static boolean applyNetworkConfiguration( SwitchNode switchNode,
                                                     SwitchNetworkConfiguration networkConfiguration )
        throws HmsException
    {
        return false;
    }

    /**
     * Get list of vxlans on this switch
     * 
     * @param switchNode
     * @return
     */
    public static List<SwitchVxlan> getSwitchVxlans( SwitchNode switchNode )
    {
        return null;
    }

    /**
     * Get list of vxlans on this switch for a prticular VLAN
     * 
     * @param switchNode
     * @return
     */
    public static List<SwitchVxlan> getSwitchVxlansMatchingVlan( SwitchNode switchNode, String vlanName )
    {
        return null;
    }

    /**
     * Create VxLAN on the specified switch
     * 
     * @param switchNode
     * @param vlan
     * @return true if successful, false if not
     */
    public static boolean createVxlan( SwitchNode switchNode, SwitchVxlan vxlan )
        throws HmsException
    {
        return false;
    }

    /**
     * Delete VxLAN with specified ID
     * 
     * @param switchNode
     * @param vlanName
     * @return
     * @throws HmsException
     */
    public static boolean deleteVxlan( SwitchNode switchNode, String vxlanName, String vlanName )
        throws HmsException
    {
        return false;
    }

    /**
     * Configure OSPF on switch with specified configuration
     * 
     * @param switchNode
     * @param ospf
     * @return
     * @throws HmsException
     */
    public static boolean configureOspf( SwitchNode switchNode, SwitchOspfConfig ospf )
        throws HmsException
    {
        return false;
    }

    /**
     * Get entire OSPF configuration of this switch
     * 
     * @param switchNode
     * @return
     */
    public static SwitchOspfConfig getOspf( SwitchNode switchNode )
        throws HmsException
    {
        SwitchOspfConfig ospfObj = new SwitchOspfConfig();
        ospfObj.setEnabled( false );
        SwitchOspfGlobalConfig global = new SwitchOspfGlobalConfig();
        global.setNetworks( new ArrayList<SwitchOspfNetworkConfig>() );
        global.setInterfaces( new ArrayList<SwitchOspfInterfaceConfig>() );
        global.setDefaultMode( null );
        global.setRouterId( null );
        ;
        ospfObj.setGlobal( global );
        return ospfObj;
    }

    /**
     * Configure BGP (Border Gateway Protocol) on the switch node with the specified configuration.
     * 
     * @param switchNode
     * @param bgp
     * @return true if configuration was successful, false otherwise
     * @throws HmsException
     */
    public static boolean configureBgp( SwitchNode switchNode, SwitchBgpConfig bgp )
        throws HmsException
    {
        return false;
    }

    /**
     * Get current BGP configuration on the switch node.
     * 
     * @param switchNode
     * @return
     */
    public static SwitchBgpConfig getBgpConfiguration( SwitchNode switchNode )
    {
        SwitchBgpConfig bgpconfiguration = new SwitchBgpConfig();
        bgpconfiguration.setEnabled( true );
        bgpconfiguration.setExportedNetworks( new ArrayList<String>() );
        bgpconfiguration.setLocalAsn( 0 );
        bgpconfiguration.setLocalIpAddress( "70:72:cf:ac:76:10" );
        bgpconfiguration.setPeerAsn( 0 );
        bgpconfiguration.setPeerIpAddress( "70:72:cf:ac:76:10" );

        return bgpconfiguration;
    }

    /**
     * Get switch sensor information.
     * 
     * @param switchNode
     * @return
     * @throws HmsException
     */
    public static SwitchSensorInfo getSwitchSensorInfo( SwitchNode switchNode )
        throws HmsException
    {
        SwitchSensorInfo sensorInfo = new SwitchSensorInfo();
        sensorInfo.setTimestamp( 1426419005 );

        List<FanSpeed> fanSpeeds = new ArrayList<FanSpeed>();
        FanSpeed s1 = new FanSpeed();
        s1.setFanName( "Fan1" );
        s1.setFanId( 1 );
        s1.setValue( 18856 );
        s1.setUnit( EventUnitType.RPM );
        s1.setStatus( HmsSensorState.Ok );
        fanSpeeds.add( s1 );
        FanSpeed s2 = new FanSpeed();
        s2.setFanName( "Fan2" );
        s2.setFanId( 2 );
        s2.setValue( 15669 );
        s2.setUnit( EventUnitType.RPM );
        s2.setStatus( HmsSensorState.Ok );
        fanSpeeds.add( s2 );
        FanSpeed s3 = new FanSpeed();
        s3.setFanName( "Fan3" );
        s3.setFanId( 3 );
        s3.setValue( 18856 );
        s3.setUnit( EventUnitType.RPM );
        s3.setStatus( HmsSensorState.Ok );
        fanSpeeds.add( s3 );
        sensorInfo.setFanSpeeds( fanSpeeds );

        List<ChassisTemp> chassisTemps = new ArrayList<ChassisTemp>();
        ChassisTemp ct1 = new ChassisTemp();
        ct1.setTempName( "Temp1" );
        ct1.setTempId( 1 );
        ct1.setValue( (float) 28.125 );
        ct1.setUnit( EventUnitType.DEGREES_CELSIUS );
        ct1.setStatus( HmsSensorState.Ok );
        chassisTemps.add( ct1 );
        ChassisTemp ct2 = new ChassisTemp();
        ct2.setTempName( "Temp2" );
        ct2.setTempId( 2 );
        ct2.setValue( (float) 47.25 );
        ct2.setUnit( EventUnitType.DEGREES_CELSIUS );
        ct2.setStatus( HmsSensorState.Ok );
        chassisTemps.add( ct2 );
        ChassisTemp ct3 = new ChassisTemp();
        ct3.setTempName( "Temp3" );
        ct3.setTempId( 3 );
        ct3.setValue( (float) 32.25 );
        ct3.setUnit( EventUnitType.DEGREES_CELSIUS );
        ct3.setStatus( HmsSensorState.Ok );
        chassisTemps.add( ct3 );
        sensorInfo.setChassisTemps( chassisTemps );

        List<PsuStatus> psuStatus = new ArrayList<PsuStatus>();
        PsuStatus ps1 = new PsuStatus();
        ps1.setPsuName( "PSU1" );
        ps1.setPsuId( 1 );
        ps1.setStatus( HmsSensorState.Invalid );
        psuStatus.add( ps1 );
        PsuStatus ps2 = new PsuStatus();
        ps2.setPsuName( "PSU2" );
        ps2.setPsuId( 2 );
        ps2.setStatus( HmsSensorState.Ok );
        psuStatus.add( ps2 );
        sensorInfo.setPsuStatus( psuStatus );

        return sensorInfo;
    }

    /**
     * Config MC-LAG on this switch with the specified configuration.
     * 
     * @param switchNode
     * @param mclag
     * @throws HmsException
     */
    public static boolean configureMclag( SwitchNode switchNode, SwitchMclagInfo mclag )
        throws HmsException
    {
        return false;
    }

    public static List<ServerComponentEvent> getComponentEventList( ServiceHmsNode serviceNode,
                                                                    SwitchComponentEnum component )
        throws HmsException
    {

        List<ServerComponentEvent> list = new ArrayList<ServerComponentEvent>();
        SwitchSensorInfo sensorInfo = getSwitchSensorInfo( new SwitchNode( serviceNode ) );

        /* Convert fan speeds into component sensors. */
        if ( sensorInfo.getFanSpeeds() != null )
        {
            for ( FanSpeed fanSpeed : sensorInfo.getFanSpeeds() )
            {
                list.add( fanSpeed.toServerComponentSensor() );
            }
        }

        /* Convert chassis temps into component sensors. */
        if ( sensorInfo.getChassisTemps() != null )
        {
            for ( ChassisTemp temp : sensorInfo.getChassisTemps() )
            {
                list.add( temp.toServerComponentSensor() );
            }
        }

        /* Convert PSU status into component sensors. */
        if ( sensorInfo.getPsuStatus() != null )
        {
            for ( PsuStatus psu : sensorInfo.getPsuStatus() )
            {
                list.add( psu.toServerComponentSensor() );
            }
        }

        ServerComponentEvent switchEvent = new ServerComponentEvent();
        switchEvent.setEventId( "MANAGEMENT" );
        switchEvent.setComponentId( "S1" );
        switchEvent.setEventName( NodeEvent.MANAGEMENT_SWITCH_UP );
        switchEvent.setDiscreteValue( "Switch is up" );
        switchEvent.setUnit( EventUnitType.DISCRETE );
        list.add( switchEvent );

        ServerComponentEvent switchPortEvent = new ServerComponentEvent();
        switchPortEvent.setEventId( "MANAGEMENT" );
        switchPortEvent.setComponentId( "swp1" );
        switchPortEvent.setEventName( NodeEvent.MANAGEMENT_SWITCH_PORT_UP );
        switchPortEvent.setDiscreteValue( "Switch port is up" );
        switchPortEvent.setUnit( EventUnitType.DISCRETE );
        list.add( switchPortEvent );

        return list;
    }

    public static List<HmsApi> getSupportedHmsApi( ServiceHmsNode serviceNode )
        throws HmsException
    {
        List<HmsApi> apiList = new ArrayList<HmsApi>();
        apiList.add( HmsApi.SWITCH_INFO );
        apiList.add( HmsApi.SWITCH_PORT_INFO );
        apiList.add( HmsApi.SWITCH_SENSOR_INFO );
        apiList.add( HmsApi.SWITCH_PORT_SENSOR_INFO );
        apiList.add( HmsApi.SWITCH_FAN_SENSOR_INFO );
        apiList.add( HmsApi.SWITCH_POWERUNIT_SENSOR_INFO );
        return apiList;

    }

}
