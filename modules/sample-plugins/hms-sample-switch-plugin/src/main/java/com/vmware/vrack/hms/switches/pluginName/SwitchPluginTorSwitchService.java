/* ********************************************************************************
 * SwitchPluginTorSwitchService.java
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
package com.vmware.vrack.hms.switches.pluginName;

import java.util.ArrayList;
import java.util.List;

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
import com.vmware.vrack.hms.common.switches.api.SwitchServiceImplementation;
import com.vmware.vrack.hms.common.switches.api.SwitchSession;
import com.vmware.vrack.hms.common.switches.api.SwitchType;
import com.vmware.vrack.hms.common.switches.api.SwitchUpgradeInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.switches.api.SwitchVxlan;
import com.vmware.vrack.hms.common.switches.model.bulk.PluginSwitchBulkConfig;

@SwitchServiceImplementation( name = "pluginName" )
public class SwitchPluginTorSwitchService
    implements ISwitchService
{
    @SuppressWarnings( "unused" )
    private final static String SWITCH_TYPE = "pluginName";

    public List<SwitchType> getSupportedSwitchTypes()
    {
        SwitchType s1 = new SwitchType();
        s1.setManufacturer( "Sample" );
        s1.setModel( ".*" );
        s1.setRegexMatching( true );
        ArrayList<SwitchType> retList = new ArrayList<SwitchType>();
        retList.add( s1 );
        return retList;
    }

    public boolean discoverSwitch( SwitchNode switchNode )
    {
        return false;
    }

    public String getSwitchType()
    {
        return null;
    }

    public SwitchSession getSession( SwitchNode switchNode )
    {
        return null;
    }

    public boolean isPoweredOn( SwitchNode switchNode )
    {
        return false;
    }

    public SwitchOsInfo getSwitchOsInfo( SwitchNode switchNode )
    {
        return null;
    }

    public SwitchHardwareInfo getSwitchHardwareInfo( SwitchNode switchNode )
    {
        return null;
    }

    public boolean updateSwitchIpAddress( SwitchNode switchNode, String ipAddress, String netmask, String gateway )
        throws HmsException
    {
        return false;
    }

    public List<String> getSwitchPortList( SwitchNode switchNode )
    {
        return null;
    }

    public SwitchPort getSwitchPort( SwitchNode switchNode, String portName )
    {
        return null;
    }

    public List<SwitchPort> getSwitchPortListBulk( SwitchNode switchNode )
    {
        return null;
    }

    public PortStatus getSwitchPortStatus( SwitchNode switchNode, String portName )
    {
        return null;
    }

    public boolean setSwitchPortStatus( SwitchNode switchNode, String portName, PortStatus portStatus )
        throws HmsException
    {
        return false;
    }

    public boolean updateSwitchPort( SwitchNode switchNode, String portName, SwitchPort portInfo )
        throws HmsException
    {
        return false;
    }

    public List<String> getSwitchVlans( SwitchNode switchNode )
    {
        return null;
    }

    public SwitchVlan getSwitchVlan( SwitchNode switchNode, String vlanName )
        throws HmsException
    {
        return null;
    }

    public List<SwitchVlan> getSwitchVlansBulk( SwitchNode switchNode )
    {
        return null;
    }

    public boolean createVlan( SwitchNode switchNode, SwitchVlan vlan )
        throws HmsException
    {
        return false;
    }

    public boolean updateVlan( SwitchNode switchNode, String vlanName, SwitchVlan vlan )
        throws HmsException
    {
        return false;
    }

    public boolean deleteVlan( SwitchNode switchNode, String vlanName )
        throws HmsException
    {
        return false;
    }

    public List<String> getSwitchLacpGroups( SwitchNode switchNode )
    {
        return null;
    }

    public SwitchLacpGroup getSwitchLacpGroup( SwitchNode switchNode, String lacpGroupName )
        throws HmsException
    {
        return null;
    }

    public boolean createLacpGroup( SwitchNode switchNode, SwitchLacpGroup lacpGroup )
        throws HmsException
    {
        return false;
    }

    public boolean deleteLacpGroup( SwitchNode switchNode, String lacpGroupName )
        throws HmsException
    {
        return false;
    }

    public boolean reboot( SwitchNode switchNode )
        throws HmsException
    {
        return false;
    }

    public boolean upgrade( SwitchNode switchNode, SwitchUpgradeInfo upgradeInfo )
        throws HmsException
    {
        return false;
    }

    public List<SwitchVxlan> getSwitchVxlans( SwitchNode switchNode )
    {
        return null;
    }

    public List<SwitchVxlan> getSwitchVxlansMatchingVlan( SwitchNode switchNode, String vlanName )
    {
        return null;
    }

    public boolean createVxlan( SwitchNode switchNode, SwitchVxlan vxlan )
        throws HmsException
    {
        return false;
    }

    public boolean deleteVxlan( SwitchNode switchNode, String vxlanName, String vlanName )
        throws HmsException
    {
        return false;
    }

    public boolean configureOspf( SwitchNode switchNode, SwitchOspfConfig ospf )
        throws HmsException
    {
        return false;
    }

    public SwitchOspfConfig getOspf( SwitchNode switchNode )
        throws HmsException
    {
        return null;
    }

    public SwitchSensorInfo getSwitchSensorInfo( SwitchNode switchNode )
        throws HmsException
    {
        return null;
    }

    public boolean configureMclag( SwitchNode switchNode, SwitchMclagInfo mclag )
        throws HmsException
    {
        return false;
    }

    public boolean configureBgp( SwitchNode switchNode, SwitchBgpConfig bgp )
        throws HmsException
    {
        return false;
    }

    public SwitchBgpConfig getBgpConfiguration( SwitchNode switchNode )
    {
        return null;
    }

    public SwitchMclagInfo getMclagStatus( SwitchNode switchNode )
    {
        return null;
    }

    public boolean updateSwitchTimeServer( SwitchNode switchNode, String timeServer )
        throws HmsException
    {
        return false;
    }

    public void applySwitchBulkConfigs( SwitchNode switchNode, List<PluginSwitchBulkConfig> switchBulkConfigs )
        throws HmsOobNetworkException
    {
        // TODO Auto-generated method stub
    }

    public void configureIpv4DefaultRoute( SwitchNode switchNode, String gateway, String portId )
        throws HmsOobNetworkException
    {
        // TODO Auto-generated method stub
    }

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
    public List<ServerComponentEvent> getComponentSwitchEventList( ServiceHmsNode serviceNode,
                                                                   SwitchComponentEnum component )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * getSupportedHmsSwitchApi
     *
     * @param serviceNode
     * @return List<HmsApi>
     * @throws HmsException
     */
    public List<HmsApi> getSupportedHmsSwitchApi( ServiceHmsNode serviceNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

	public boolean applyNetworkConfiguration(SwitchNode switchNode, SwitchNetworkConfiguration networkConfiguration)
			throws HmsException {
		// TODO Auto-generated method stub
		return false;
	}
}
