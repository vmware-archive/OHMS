/* ********************************************************************************
 * SwitchServiceTest.java
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
package com.vmware.vrack.hms.common.switchservice.api;

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

/**
 * @author Vmware Inc.
 */
@SwitchServiceImplementation( name = "Sample_Switch_Service" )
public class SwitchServiceTest
    implements ISwitchService
{
    @Override
    public String getSwitchType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SwitchType> getSupportedSwitchTypes()
    {
        SwitchType s1 = new SwitchType();
        s1.setManufacturer( "Arista" );
        s1.setModel( "*" );
        s1.setRegexMatching( true );
        ArrayList<SwitchType> retList = new ArrayList<SwitchType>();
        retList.add( s1 );
        return retList;
    }

    // TESTED
    @Override
    public boolean discoverSwitch( SwitchNode switchNode )
    {
        return ( true );
    }

    @Override
    public SwitchSession getSession( SwitchNode switchNode )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isPoweredOn( SwitchNode switchNode )
    {
        return true;
    }

    @Override
    public SwitchOsInfo getSwitchOsInfo( SwitchNode switchNode )
    {
        SwitchOsInfo osInfo = new SwitchOsInfo();
        osInfo.setOsName( "Arista OS" );
        osInfo.setOsVersion( "1.0.0" );
        osInfo.setFirmwareVersion( "1.0.1" );
        osInfo.setFirmwareName( "Arista XX" );
        return osInfo;
    }

    @Override
    public SwitchHardwareInfo getSwitchHardwareInfo( SwitchNode switchNode )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean updateSwitchIpAddress( SwitchNode switchNode, String ipAddress, String netmask, String gateway )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean updateSwitchTimeServer( SwitchNode switchNode, String timeServer )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> getSwitchPortList( SwitchNode switchNode )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SwitchPort getSwitchPort( SwitchNode switchNode, String portName )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SwitchPort> getSwitchPortListBulk( SwitchNode switchNode )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PortStatus getSwitchPortStatus( SwitchNode switchNode, String portName )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean setSwitchPortStatus( SwitchNode switchNode, String portName, PortStatus portStatus )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean updateSwitchPort( SwitchNode switchNode, String portName, SwitchPort portInfo )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> getSwitchVlans( SwitchNode switchNode )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SwitchVlan getSwitchVlan( SwitchNode switchNode, String vlanName )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SwitchVlan> getSwitchVlansBulk( SwitchNode switchNode )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean createVlan( SwitchNode switchNode, SwitchVlan vlan )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean updateVlan( SwitchNode switchNode, String vlanName, SwitchVlan vlan )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean deleteVlan( SwitchNode switchNode, String vlanName )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> getSwitchLacpGroups( SwitchNode switchNode )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SwitchLacpGroup getSwitchLacpGroup( SwitchNode switchNode, String lacpGroupName )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean createLacpGroup( SwitchNode switchNode, SwitchLacpGroup lacpGroup )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean deleteLacpGroup( SwitchNode switchNode, String lacpGroupName )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean reboot( SwitchNode switchNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean upgrade( SwitchNode switchNode, SwitchUpgradeInfo upgradeInfo )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<SwitchVxlan> getSwitchVxlans( SwitchNode switchNode )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SwitchVxlan> getSwitchVxlansMatchingVlan( SwitchNode switchNode, String vlanName )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean createVxlan( SwitchNode switchNode, SwitchVxlan vxlan )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean deleteVxlan( SwitchNode switchNode, String vxlanName, String vlanName )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean configureOspf( SwitchNode switchNode, SwitchOspfConfig ospf )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public SwitchOspfConfig getOspf( SwitchNode switchNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean configureBgp( SwitchNode switchNode, SwitchBgpConfig bgp )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public SwitchBgpConfig getBgpConfiguration( SwitchNode switchNode )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SwitchSensorInfo getSwitchSensorInfo( SwitchNode switchNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean configureMclag( SwitchNode switchNode, SwitchMclagInfo mclag )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public SwitchMclagInfo getMclagStatus( SwitchNode switchNode )
    {
        // TODO Auto-generated method stub
        return null;
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
        return null;
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
        return null;
    }
}
