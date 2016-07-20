/* ********************************************************************************
 * ISwitchService.java
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
package com.vmware.vrack.hms.common.switches.api;

import java.util.List;

import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentSwitchEventInfoProvider;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortStatus;
import com.vmware.vrack.hms.common.switches.model.bulk.PluginSwitchBulkConfig;

/**
 * Switch Interface for implementing switch service
 *
 * @author VMware, Inc.
 */
public interface ISwitchService
    extends IComponentSwitchEventInfoProvider
{
    /**
     * Discover this switch and report whether you support it.
     *
     * @param switchNode
     * @return true if supported, false if not
     */
    boolean discoverSwitch( SwitchNode switchNode );

    /**
     * Name describing the switch type, for e.g. "cumulus", "arista", etc.
     *
     * @return
     */
    String getSwitchType();

    /**
     * Switch plugin to return list of switch types that are supported by the plugin.
     *
     * @return List<SwitchType>
     */
    List<SwitchType> getSupportedSwitchTypes();

    /**
     * Get session that's connected to the ToR switch
     *
     * @return
     */
    SwitchSession getSession( SwitchNode switchNode );

    /**
     * Is the switch node powered on?
     *
     * @param switchNode
     * @return true if powered, false if not
     */
    boolean isPoweredOn( SwitchNode switchNode );

    /**
     * Get switch os and firmware information, such as name and version
     *
     * @param switchNode
     * @return
     */
    SwitchOsInfo getSwitchOsInfo( SwitchNode switchNode );

    /**
     * Get switch hardware information
     *
     * @param switchNode
     * @return
     */
    SwitchHardwareInfo getSwitchHardwareInfo( SwitchNode switchNode );

    /**
     * Update switch IP address information
     *
     * @param switchNode
     * @param ipAddress
     * @param netmask
     * @param gateway
     * @return
     */
    boolean updateSwitchIpAddress( SwitchNode switchNode, String ipAddress, String netmask, String gateway )
        throws HmsException;

    /**
     * Update NTP time server on switch node
     *
     * @param switchNode
     * @param timeServer IP address or hostname of NTP time server
     * @return true if successful, false if not
     */
    boolean updateSwitchTimeServer( SwitchNode switchNode, String timeServer )
        throws HmsException;

    /**
     * Get list of switch port names.
     *
     * @return
     */
    List<String> getSwitchPortList( SwitchNode switchNode );

    /**
     * Get switch port details for a particular port.
     *
     * @param portName
     * @return TorSwitchPort
     */
    SwitchPort getSwitchPort( SwitchNode switchNode, String portName );

    /**
     * Get list of all switch ports
     *
     * @return
     */
    List<SwitchPort> getSwitchPortListBulk( SwitchNode switchNode );

    /**
     * Get status of the specified port name.
     *
     * @param switchNode
     * @param portName
     * @return PortStatus.UP or PortStatus.DOWN
     */
    PortStatus getSwitchPortStatus( SwitchNode switchNode, String portName );

    /**
     * Set status of the specified port name to the specified state (UP/DOWN).
     *
     * @param switchNode
     * @param portName
     * @param portStatus
     * @return True if the operation was successful, false if not
     * @throws HmsException
     */
    boolean setSwitchPortStatus( SwitchNode switchNode, String portName, PortStatus portStatus )
        throws HmsException;

    /**
     * Update switch port configuration based on the specified portInfo.
     *
     * @param switchNode
     * @param portName
     * @param portInfo
     * @return True if the operation was successful, false if not
     * @throws HmsException
     */
    boolean updateSwitchPort( SwitchNode switchNode, String portName, SwitchPort portInfo )
        throws HmsException;

    /**
     * Get list of vlans on this switch
     *
     * @param switchNode
     * @return
     */
    List<String> getSwitchVlans( SwitchNode switchNode );

    /**
     * Get details for a VLAN with specified ID
     *
     * @param switchNode
     * @param vlanName
     * @return
     * @throws HmsException
     */
    SwitchVlan getSwitchVlan( SwitchNode switchNode, String vlanName )
        throws HmsException;

    /**
     * Get list of vlans on this switch
     *
     * @param switchNode
     * @return
     */
    List<SwitchVlan> getSwitchVlansBulk( SwitchNode switchNode );

    /**
     * Create VLAN on the specified switch
     *
     * @param switchNode
     * @param vlan
     * @return true if successful, false if not
     */
    boolean createVlan( SwitchNode switchNode, SwitchVlan vlan )
        throws HmsException;

    /**
     * Update VLAN with the specified details
     *
     * @param switchNode
     * @param vlanName
     * @param vlan
     * @return
     * @throws HmsException
     */
    boolean updateVlan( SwitchNode switchNode, String vlanName, SwitchVlan vlan )
        throws HmsException;

    /**
     * Delete VLAN with specified ID
     *
     * @param switchNode
     * @param vlanName
     * @return
     * @throws HmsException
     */
    boolean deleteVlan( SwitchNode switchNode, String vlanName )
        throws HmsException;

    /**
     * Get list of lacp groups on this switch
     *
     * @param switchNode
     * @return
     */
    List<String> getSwitchLacpGroups( SwitchNode switchNode );

    /**
     * Get details for a LACP Group with specified ID
     *
     * @param switchNode
     * @param lacpGroupName
     * @return
     * @throws HmsException
     */
    SwitchLacpGroup getSwitchLacpGroup( SwitchNode switchNode, String lacpGroupName )
        throws HmsException;

    /**
     * Create LACP group (LAG) on the specified switch
     *
     * @param switchNode
     * @param lacpGroup
     * @return
     * @throws HmsException
     */
    boolean createLacpGroup( SwitchNode switchNode, SwitchLacpGroup lacpGroup )
        throws HmsException;

    /**
     * Delete LACP group (LAG) with specified name
     *
     * @param switchNode
     * @param lacpGroupName
     * @return true if successful, false if not
     * @throws HmsException
     */
    boolean deleteLacpGroup( SwitchNode switchNode, String lacpGroupName )
        throws HmsException;

    /**
     * Reboot the specified switchNode
     *
     * @param switchNode Switch node
     * @return true if reboot was successful, false if not
     * @throws HmsException
     */
    boolean reboot( SwitchNode switchNode )
        throws HmsException;

    /**
     * Upgrade the specified switchNode
     *
     * @param switchNode
     * @param upgradeInfo Upgrade-related information
     * @return true if upgrade was successful, false if not
     * @throws HmsException
     */
    boolean upgrade( SwitchNode switchNode, SwitchUpgradeInfo upgradeInfo )
        throws HmsException;

    /**
     * Get list of vxlans on this switch
     *
     * @param switchNode
     * @return
     */
    List<SwitchVxlan> getSwitchVxlans( SwitchNode switchNode );

    /**
     * Get list of vxlans on this switch for a prticular VLAN
     *
     * @param switchNode
     * @return
     */
    List<SwitchVxlan> getSwitchVxlansMatchingVlan( SwitchNode switchNode, String vlanName );

    /**
     * Create VxLAN on the specified switch
     *
     * @param switchNode
     * @param vxlan
     * @return true if successful, false if not
     */
    boolean createVxlan( SwitchNode switchNode, SwitchVxlan vxlan )
        throws HmsException;

    /**
     * Delete VxLAN with specified ID
     *
     * @param switchNode
     * @param vlanName
     * @return
     * @throws HmsException
     */
    boolean deleteVxlan( SwitchNode switchNode, String vxlanName, String vlanName )
        throws HmsException;

    /**
     * Configure OSPF on switch with specified configuration
     *
     * @param switchNode
     * @param ospf
     * @return
     * @throws HmsException
     */
    boolean configureOspf( SwitchNode switchNode, SwitchOspfConfig ospf )
        throws HmsException;

    /**
     * Get entire OSPF configuration of this switch
     *
     * @param switchNode
     * @return
     */
    SwitchOspfConfig getOspf( SwitchNode switchNode )
        throws HmsException;

    /**
     * Configure BGP (Border Gateway Protocol) on the switch node with the specified configuration.
     *
     * @param switchNode
     * @param bgp
     * @return true if configuration was successful, false otherwise
     * @throws HmsException
     */
    boolean configureBgp( SwitchNode switchNode, SwitchBgpConfig bgp )
        throws HmsException;

    /**
     * Get current BGP configuration on the switch node.
     *
     * @param switchNode
     * @return
     */
    SwitchBgpConfig getBgpConfiguration( SwitchNode switchNode );

    /**
     * Get switch sensor information.
     *
     * @param switchNode
     * @return
     * @throws HmsException
     */
    SwitchSensorInfo getSwitchSensorInfo( SwitchNode switchNode )
        throws HmsException;

    /**
     * Config MC-LAG on this switch with the specified configuration.
     *
     * @param switchNode
     * @param mclag
     * @throws HmsException
     */
    boolean configureMclag( SwitchNode switchNode, SwitchMclagInfo mclag )
        throws HmsException;

    /**
     * Get MC-LAG status and running information.
     *
     * @param switchNode
     * @return SwitchMclagInfo object with appropriate values
     */
    SwitchMclagInfo getMclagStatus( SwitchNode switchNode );

    /**
     * Sets (overwrites) the values specified in the bulk configuration on the switch in the specified/all configurable
     * places
     *
     * @param switchNode
     * @param configs
     * @throws HmsOobNetworkException
     */
    void applySwitchBulkConfigs( SwitchNode switchNode, List<PluginSwitchBulkConfig> configs )
        throws HmsOobNetworkException;

    /**
     * @param switchNode
     * @param gateway
     * @param portId
     * @throws HmsOobNetworkException
     */
    void configureIpv4DefaultRoute( SwitchNode switchNode, String gateway, String portId )
        throws HmsOobNetworkException;

    /**
     * @param switchNode
     * @throws HmsOobNetworkException
     */
    void deleteIpv4DefaultRoute( SwitchNode switchNode )
        throws HmsOobNetworkException;
}
