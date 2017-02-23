/* ********************************************************************************
 * FakeISwitchService.java
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

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchHardwareInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchMclagInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchNetworkConfiguration;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchOsInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortStatus;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchSession;
import com.vmware.vrack.hms.common.switches.api.SwitchSnmpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchType;
import com.vmware.vrack.hms.common.switches.api.SwitchUpgradeInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.switches.api.SwitchVxlan;
import com.vmware.vrack.hms.common.switches.model.bulk.PluginSwitchBulkConfig;

import java.util.List;

/**
 * Class to implement IpmiService for testing purposes. Relevant functions call Test Data class to return the test data
 * for junits.
 *
 * @author VMware Inc.
 */
public class FakeISwitchService
    implements ISwitchService
{

    /**
     * Discover this switch and report whether you support it.
     * 
     * @param switchNode
     * @return true if supported, false if not
     */
    @Override
    public boolean discoverSwitch( SwitchNode switchNode )
    {
        return FakeISwitchServiceData.discoverSwitch( switchNode );
    }

    /**
     * Name describing the switch type, for e.g. "cumulus", "arista", etc.
     * 
     * @return
     */
    @Override
    public String getSwitchType()
    {
        return FakeISwitchServiceData.getSwitchType();
    }

    /**
     * Switch plugin to return list of switch types that are supported by the plugin.
     * 
     * @return List<SwitchType>
     */
    @Override
    public List<SwitchType> getSupportedSwitchTypes()
    {
        return FakeISwitchServiceData.getSupportedSwitchTypes();
    }

    /**
     * Get session that's connected to the ToR switch
     * 
     * @return
     */
    @Override
    public SwitchSession getSession( SwitchNode switchNode )
    {
        return FakeISwitchServiceData.getSession( switchNode );
    }

    /**
     * Is the switch node powered on?
     * 
     * @param switchNode
     * @return true if powered, false if not
     */
    @Override
    public boolean isPoweredOn( SwitchNode switchNode )
    {
        return FakeISwitchServiceData.isPoweredOn( switchNode );
    }

    /**
     * Get switch os and firmware information, such as name and version
     * 
     * @param switchNode
     * @return
     */
    @Override
    public SwitchOsInfo getSwitchOsInfo( SwitchNode switchNode )
    {
        return FakeISwitchServiceData.getSwitchOsInfo( switchNode );
    }

    /**
     * Get switch hardware information
     * 
     * @param switchNode
     * @return
     */
    @Override
    public SwitchHardwareInfo getSwitchHardwareInfo( SwitchNode switchNode )
    {
        return FakeISwitchServiceData.getSwitchHardwareInfo( switchNode );
    }

    /**
     * Update switch node information
     * 
     * @param switchNode
     * @param ipAddress
     * @param netmask
     * @param gateway
     * @return
     * @throws HmsException
     */
    @Override
    public boolean updateSwitchIpAddress( SwitchNode switchNode, String ipAddress, String netmask, String gateway )
        throws HmsException
    {
        return FakeISwitchServiceData.updateSwitchIpAddress( switchNode, ipAddress, netmask, gateway );
    }

    /**
     * Update the switch password with provided one
     * 
     * @param switchNode
     * @param username
     * @param newPassword
     * @return
     * @throws HmsException
     */
    @Override
    public boolean setPassword( SwitchNode switchNode, String username, String newPassword )
        throws HmsException
    {
        return FakeISwitchServiceData.rotateSwitchPassword( switchNode, username, newPassword );
    }

    /**
     * Get list of switch port names.
     * 
     * @return
     */
    @Override
    public List<String> getSwitchPortList( SwitchNode switchNode )
    {
        return FakeISwitchServiceData.getSwitchPortList( switchNode );
    }

    /**
     * Get switch port details for a particular port.
     * 
     * @param portName
     * @return TorSwitchPort
     */
    @Override
    public SwitchPort getSwitchPort( SwitchNode switchNode, String portName )
    {
        return FakeISwitchServiceData.getSwitchPort( switchNode, portName );
    }

    /**
     * Get list of all switch ports
     * 
     * @return
     */
    @Override
    public List<SwitchPort> getSwitchPortListBulk( SwitchNode switchNode )
    {
        return FakeISwitchServiceData.getSwitchPortListBulk( switchNode );
    }

    /**
     * Get status of the specified port name.
     * 
     * @param switchNode
     * @param portName
     * @return PortStatus.UP or PortStatus.DOWN
     */
    @Override
    public PortStatus getSwitchPortStatus( SwitchNode switchNode, String portName )
    {
        return FakeISwitchServiceData.getSwitchPortStatus( switchNode, portName );
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
    @Override
    public boolean setSwitchPortStatus( SwitchNode switchNode, String portName, PortStatus portStatus )
        throws HmsException
    {
        return FakeISwitchServiceData.setSwitchPortStatus( switchNode, portName, portStatus );
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
    @Override
    public boolean updateSwitchPort( SwitchNode switchNode, String portName, SwitchPort portInfo )
        throws HmsException
    {
        return FakeISwitchServiceData.updateSwitchPort( switchNode, portName, portInfo );
    }

    /**
     * Get list of vlans on this switch
     * 
     * @param switchNode
     * @return
     */
    @Override
    public List<String> getSwitchVlans( SwitchNode switchNode )
    {
        return FakeISwitchServiceData.getSwitchVlans( switchNode );
    }

    /**
     * Get details for a VLAN with specified ID
     * 
     * @param switchNode
     * @param vlanName
     * @return
     * @throws HmsException
     */
    @Override
    public SwitchVlan getSwitchVlan( SwitchNode switchNode, String vlanName )
        throws HmsException
    {
        return FakeISwitchServiceData.getSwitchVlan( switchNode, vlanName );
    }

    /**
     * Get list of vlans on this switch
     * 
     * @param switchNode
     * @return
     */
    @Override
    public List<SwitchVlan> getSwitchVlansBulk( SwitchNode switchNode )
    {
        return FakeISwitchServiceData.getSwitchVlansBulk( switchNode );
    }

    /**
     * Create VLAN on the specified switch
     * 
     * @param switchNode
     * @param vlan
     * @return true if successful, false if not
     */
    @Override
    public boolean createVlan( SwitchNode switchNode, SwitchVlan vlan )
        throws HmsException
    {
        return FakeISwitchServiceData.createVlan( switchNode, vlan );
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
    @Override
    public boolean updateVlan( SwitchNode switchNode, String vlanName, SwitchVlan vlan )
        throws HmsException
    {
        return FakeISwitchServiceData.updateVlan( switchNode, vlanName, vlan );
    }

    /**
     * Delete VLAN with specified ID
     * 
     * @param switchNode
     * @param vlanName
     * @return
     * @throws HmsException
     */
    @Override
    public boolean deleteVlan( SwitchNode switchNode, String vlanName )
        throws HmsException
    {
        return FakeISwitchServiceData.deleteVlan( switchNode, vlanName );
    }

    /**
     * Get list of lacp groups on this switch
     * 
     * @param switchNode
     * @return
     */
    @Override
    public List<String> getSwitchLacpGroups( SwitchNode switchNode )
    {
        return FakeISwitchServiceData.getSwitchLacpGroups( switchNode );
    }

    /**
     * Get details for a LACP Group with specified ID
     * 
     * @param switchNode
     * @param lacpGroupName
     * @return
     * @throws HmsException
     */
    @Override
    public SwitchLacpGroup getSwitchLacpGroup( SwitchNode switchNode, String lacpGroupName )
        throws HmsException
    {
        return FakeISwitchServiceData.getSwitchLacpGroup( switchNode, lacpGroupName );
    }

    /**
     * Create LACP group (LAG) on the specified switch
     * 
     * @param switchNode
     * @param lacpGroup
     * @return
     * @throws HmsException
     */
    @Override
    public boolean createLacpGroup( SwitchNode switchNode, SwitchLacpGroup lacpGroup )
        throws HmsException
    {
        return FakeISwitchServiceData.createLacpGroup( switchNode, lacpGroup );
    }

    /**
     * Delete LACP group (LAG) with specified name
     * 
     * @param switchNode
     * @param lacpGroupName
     * @return true if successful, false if not
     * @throws HmsException
     */
    @Override
    public boolean deleteLacpGroup( SwitchNode switchNode, String lacpGroupName )
        throws HmsException
    {
        return FakeISwitchServiceData.deleteLacpGroup( switchNode, lacpGroupName );
    }

    /**
     * Reboot the specified switchNode
     * 
     * @param switchNode Switch node
     * @return true if reboot was successful, false if not
     * @throws HmsException
     */
    @Override
    public boolean reboot( SwitchNode switchNode )
        throws HmsException
    {
        return FakeISwitchServiceData.reboot( switchNode );
    }

    /**
     * Upgrade the specified switchNode
     * 
     * @param switchNode
     * @param upgradeInfo Upgrade-related information
     * @return true if upgrade was successful, false if not
     * @throws HmsException
     */
    @Override
    public boolean upgrade( SwitchNode switchNode, SwitchUpgradeInfo upgradeInfo )
        throws HmsException
    {
        return FakeISwitchServiceData.upgrade( switchNode, upgradeInfo );
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
    @Override
    public boolean applyNetworkConfiguration( SwitchNode switchNode, SwitchNetworkConfiguration networkConfiguration )
        throws HmsException
    {
        return FakeISwitchServiceData.applyNetworkConfiguration( switchNode, networkConfiguration );
    }

    /**
     * Get list of vxlans on this switch
     * 
     * @param switchNode
     * @return
     */
    @Override
    public List<SwitchVxlan> getSwitchVxlans( SwitchNode switchNode )
    {
        return FakeISwitchServiceData.getSwitchVxlans( switchNode );
    }

    /**
     * Get list of vxlans on this switch for a prticular VLAN
     * 
     * @param switchNode
     * @return
     */
    @Override
    public List<SwitchVxlan> getSwitchVxlansMatchingVlan( SwitchNode switchNode, String vlanName )
    {
        return FakeISwitchServiceData.getSwitchVxlansMatchingVlan( switchNode, vlanName );
    }

    /**
     * Create VxLAN on the specified switch
     * 
     * @param switchNode
     * @param vxlan
     * @return
     * @throws HmsException
     */
    @Override
    public boolean createVxlan( SwitchNode switchNode, SwitchVxlan vxlan )
        throws HmsException
    {
        return FakeISwitchServiceData.createVxlan( switchNode, vxlan );
    }

    /**
     * Delete VxLAN with specified ID
     * 
     * @param switchNode
     * @param vlanName
     * @return
     * @throws HmsException
     */
    @Override
    public boolean deleteVxlan( SwitchNode switchNode, String vxlanName, String vlanName )
        throws HmsException
    {
        return FakeISwitchServiceData.deleteVxlan( switchNode, vxlanName, vlanName );
    }

    /**
     * Configure OSPF on switch with specified configuration
     * 
     * @param switchNode
     * @param ospf
     * @return
     * @throws HmsException
     */
    @Override
    public boolean configureOspf( SwitchNode switchNode, SwitchOspfConfig ospf )
        throws HmsException
    {
        return FakeISwitchServiceData.configureOspf( switchNode, ospf );
    }

    /**
     * Get entire OSPF configuration of this switch
     * 
     * @param switchNode
     * @return
     */
    @Override
    public SwitchOspfConfig getOspf( SwitchNode switchNode )
        throws HmsException
    {
        return FakeISwitchServiceData.getOspf( switchNode );
    }

    /**
     * Configure BGP (Border Gateway Protocol) on the switch node with the specified configuration.
     * 
     * @param switchNode
     * @param bgp
     * @return true if configuration was successful, false otherwise
     * @throws HmsException
     */
    @Override
    public boolean configureBgp( SwitchNode switchNode, SwitchBgpConfig bgp )
        throws HmsException
    {
        return FakeISwitchServiceData.configureBgp( switchNode, bgp );
    }

    /**
     * Get current BGP configuration on the switch node.
     * 
     * @param switchNode
     * @return
     */
    @Override
    public SwitchBgpConfig getBgpConfiguration( SwitchNode switchNode )
    {
        return FakeISwitchServiceData.getBgpConfiguration( switchNode );
    }

    /**
     * Get switch sensor information.
     * 
     * @param switchNode
     * @return
     * @throws HmsException
     */
    @Override
    public SwitchSensorInfo getSwitchSensorInfo( SwitchNode switchNode )
        throws HmsException
    {
        return FakeISwitchServiceData.getSwitchSensorInfo( switchNode );
    }

    /**
     * Config MC-LAG on this switch with the specified configuration.
     * 
     * @param switchNode
     * @param mclag
     * @throws HmsException
     */
    @Override
    public boolean configureMclag( SwitchNode switchNode, SwitchMclagInfo mclag )
        throws HmsException
    {
        return FakeISwitchServiceData.configureMclag( switchNode, mclag );
    }

    @Override
    public SwitchMclagInfo getMclagStatus( SwitchNode switchNode )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean updateSwitchTimeServer( SwitchNode switchNode, String timeServer )
        throws HmsException
    {
        return false;
    }

    @Override
    public void applySwitchBulkConfigs( SwitchNode switchNode, List<PluginSwitchBulkConfig> switchBulkConfigs )
        throws HmsOobNetworkException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void configureIpv4DefaultRoute( SwitchNode switchNode, String gateway, String portId )
        throws HmsOobNetworkException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteIpv4DefaultRoute( SwitchNode switchNode )
        throws HmsOobNetworkException
    {
        // TODO Auto-generated method stub

    }

    /**
     * getComponentSwitchEventList
     *
     * @param serviceNode
     * @param component
     * @return List<ServerComponentEvent>
     * @throws HmsException
     */
    @Override
    public List<ServerComponentEvent> getComponentSwitchEventList( ServiceHmsNode serviceNode,
                                                                   SwitchComponentEnum component )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return FakeISwitchServiceData.getComponentEventList( serviceNode, component );
    }

    /**
     * getSupportedHmsSwitchApi
     *
     * @param serviceNode
     * @return List<HmsApi>
     * @throws HmsException
     */
    @Override
    public List<HmsApi> getSupportedHmsSwitchApi( ServiceHmsNode serviceNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return FakeISwitchServiceData.getSupportedHmsApi( serviceNode );
    }

    @Override
    public void deletePortOrBondFromVlan( SwitchNode switchNode, String vlanId, String portOrBondName )
        throws HmsOobNetworkException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void deletePortFromLacpGroup( SwitchNode switchNode, String lacpGroupName, String portName )
        throws HmsOobNetworkException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSwitchTime( SwitchNode switchNode, long time )
        throws HmsOobNetworkException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void configureSnmp( SwitchNode switchNode, SwitchSnmpConfig config )
        throws HmsOobNetworkException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public SwitchSnmpConfig getSnmp( SwitchNode switchNode )
        throws HmsOobNetworkException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
