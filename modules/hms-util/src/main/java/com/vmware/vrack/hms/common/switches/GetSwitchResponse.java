/* ********************************************************************************
 * GetSwitchResponse.java
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
package com.vmware.vrack.hms.common.switches;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.vrack.hms.common.switches.api.SwitchHardwareInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode.SwitchRoleType;
import com.vmware.vrack.hms.common.switches.api.SwitchOsInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchVxlan;

/**
 * The GetSwitchResponse represents the JSON response that you'll receive when you invoke the GET
 * /api/1.0/hms/switches/S1 endpoint.
 * 
 * @author sunil
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class GetSwitchResponse
{
    public GetSwitchResponse( String switchId, String protocol, String ipAddress, Integer port, String username,
                              String password )
    {
        switchNode = new SwitchNode( switchId, protocol, ipAddress, port, username, password );
    }

    public GetSwitchResponse( SwitchNode switchNode )
    {
        this.switchNode = switchNode;
    }

    public String getSwitchId()
    {
        return switchNode.getSwitchId();
    }

    public void setSwitchId( String switchId )
    {
        switchNode.setSwitchId( switchId );
    }

    public String getProtocol()
    {
        return switchNode.getProtocol();
    }

    public void setProtocol( String protocol )
    {
        switchNode.setProtocol( protocol );
    }

    @JsonIgnore
    public String getIpAddress()
    {
        return switchNode.getIpAddress();
    }

    public void setIpAddress( String ipAddress )
    {
        switchNode.setIpAddress( ipAddress );
    }

    public Integer getPort()
    {
        return switchNode.getPort();
    }

    public void setPort( Integer port )
    {
        switchNode.setPort( port );
    }

    public String getUsername()
    {
        return switchNode.getUsername();
    }

    public void setUsername( String username )
    {
        switchNode.setUsername( username );
    }

    @JsonIgnore
    public String getPassword()
    {
        return switchNode.getPassword();
    }

    public void setPassword( String password )
    {
        switchNode.setPassword( password );
    }

    public String getManagementIpAddress()
    {
        return getIpAddress();
    }

    public boolean isPowered()
    {
        return powered;
    }

    public void setPowered( boolean powered )
    {
        this.powered = powered;
    }

    public boolean isDiscoverable()
    {
        return discoverable;
    }

    public void setDiscoverable( boolean discoverable )
    {
        this.discoverable = discoverable;
    }

    public String getManagementMacAddress()
    {
        return hardwareInfo.getManagementMacAddress();
    }

    public void setManagementMacAddress( String managementMacAddress )
    {
        hardwareInfo.setManagementMacAddress( managementMacAddress );
    }

    public String getHardwareModel()
    {
        return hardwareInfo.getModel();
    }

    public void setHardwareModel( String hardwareModel )
    {
        hardwareInfo.setModel( hardwareModel );
    }

    public String getSerialNumber()
    {
        return hardwareInfo.getChassisSerialId();
    }

    public void setSerialNumber( String serialNumber )
    {
        hardwareInfo.setChassisSerialId( serialNumber );
    }

    public String getChassisId()
    {
        return hardwareInfo.getChassisSerialId();
    }

    public void setChassisId( String chassisId )
    {
        hardwareInfo.setChassisSerialId( chassisId );
    }

    public Date getLastBoot()
    {
        return osInfo.getLastBoot();
    }

    public void setLastBoot( Date lastBoot )
    {
        osInfo.setLastBoot( lastBoot );
    }

    public Map<String, Object> getNodeOsDetails()
    {
        Map<String, Object> osMap = new TreeMap<String, Object>();
        osMap.put( "osName", osInfo.getOsName() );
        osMap.put( "osVersion", osInfo.getOsVersion() );
        osMap.put( "firmwareName", osInfo.getFirmwareName() );
        osMap.put( "firmwareVersion", osInfo.getFirmwareVersion() );
        return osMap;
    }

    public void setOsInfo( SwitchOsInfo osInfo )
    {
        this.osInfo = osInfo;
    }

    public void setHardwareInfo( SwitchHardwareInfo hwInfo )
    {
        this.hardwareInfo = hwInfo;
    }

    @JsonIgnore
    public SwitchHardwareInfo getHardwareInfo()
    {
        return hardwareInfo;
    }

    public String getLocation()
    {
        return switchNode.getLocation();
    }

    public void setLocation( String location )
    {
        switchNode.setLocation( location );
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public SwitchRoleType getRole()
    {
        return switchNode.getRole();
    }

    @JsonIgnore
    public void setRole( SwitchRoleType role )
    {
        switchNode.setRole( role );
    }

    @JsonProperty( "role" )
    public void setRole( String role )
    {
        switchNode.setRole( SwitchRoleType.valueOf( role.toUpperCase() ) );
    }

    public List<String> getSwitchPortList()
    {
        return switchPortList;
    }

    public void setSwitchPortList( List<String> switchPortList )
    {
        this.switchPortList = switchPortList;
    }

    public List<String> getSwitchLacpGroups()
    {
        return switchLacpGroups;
    }

    public void setSwitchLacpGroups( List<String> switchLacpGroups )
    {
        this.switchLacpGroups = switchLacpGroups;
    }

    public List<String> getSwitchVlans()
    {
        return switchVlans;
    }

    public void setSwitchVxlans( List<SwitchVxlan> switchVxlans )
    {
        this.switchVxlans = switchVxlans;
    }

    public List<SwitchVxlan> getSwitchVxlans()
    {
        return switchVxlans;
    }

    public void setSwitchVlans( List<String> switchVlans )
    {
        this.switchVlans = switchVlans;
    }

    public SwitchSensorInfo getSensorInfo()
    {
        return sensorInfo;
    }

    public void setSensorInfo( SwitchSensorInfo sensorInfo )
    {
        this.sensorInfo = sensorInfo;
    }

    private SwitchNode switchNode;

    private SwitchOsInfo osInfo;

    private SwitchHardwareInfo hardwareInfo;

    private boolean powered;

    private boolean discoverable;

    private String type;

    private List<String> switchPortList;

    private List<String> switchLacpGroups;

    private List<String> switchVlans;

    private List<SwitchVxlan> switchVxlans;

    private SwitchSensorInfo sensorInfo;
}
