/* ********************************************************************************
 * SwitchNode.java
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
package com.vmware.vrack.hms.common.switches.api;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.configuration.SwitchItem;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;

/**
 * The TorSwitchNode represents all of the basic information given to HMS about the switch node, i.e. a unique ID, IP
 * address, port, protocol, username and password.
 *
 * @author VMware, Inc.
 */
public class SwitchNode
{
    public enum SwitchRoleType
    {
        TOR, MANAGEMENT, SPINE
    };

    private String switchId;

    private String protocol;

    private String ipAddress;

    private Integer port;

    private String username;

    private String password;

    private String location; // physical U location

    private String topology;

    private SwitchRoleType role;

    private SwitchType type;

    private ComponentIdentifier componentIdentifier; // FRU component identifiers

    private String firmwareName;

    private String firmwareVersion;

    private String osName;

    private String osVendor;

    public SwitchNode( String switchId, String protocol, String ipAddress, Integer port, String username,
                       String password )
    {
        super();
        this.switchId = switchId;
        this.protocol = protocol;
        this.ipAddress = ipAddress;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public SwitchNode( SwitchItem switchItem )
    {
        super();
        this.switchId = switchItem.getId();
        this.protocol = switchItem.getProtocol();
        this.ipAddress = switchItem.getIpAddress();
        this.port = switchItem.getPort();
        this.username = switchItem.getUsername();
        this.password = switchItem.getPassword();
        this.location = switchItem.getLocation();
        this.topology = switchItem.getTopology();
        this.role = switchItem.getRole();
        this.type = switchItem.getType();
    }

    /**
     * Construct a TorSwitchNode from a ServiceHmsNode
     * 
     * @param hmsNode
     */
    public SwitchNode( ServiceHmsNode hmsNode )
    {
        super();
        this.switchId = hmsNode.getNodeID();
        this.protocol = "SSH";
        this.ipAddress = hmsNode.getManagementIp();
        this.port = 22;
        this.username = hmsNode.getManagementUserName();
        this.password = hmsNode.getManagementUserPassword();
    }

    public String getSwitchId()
    {
        return switchId;
    }

    public void setSwitchId( String switchId )
    {
        this.switchId = switchId;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol( String protocol )
    {
        this.protocol = protocol;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress( String ipAddress )
    {
        this.ipAddress = ipAddress;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort( Integer port )
    {
        this.port = port;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation( String location )
    {
        this.location = location;
    }

    public String getTopology()
    {
        return topology;
    }

    public void setTopology( String topology )
    {
        this.topology = topology;
    }

    public SwitchRoleType getRole()
    {
        return role;
    }

    public void setRole( SwitchRoleType role )
    {
        this.role = role;
    }

    public void setRole( String role )
    {
        this.role = SwitchRoleType.valueOf( role.toUpperCase() );
    }

    public SwitchType getType()
    {
        return type;
    }

    public void setType( SwitchType type )
    {
        this.type = type;
    }

    public ComponentIdentifier getComponentIdentifier()
    {
        return componentIdentifier;
    }

    public void setComponentIdentifier( ComponentIdentifier componentIdentifier )
    {
        this.componentIdentifier = componentIdentifier;
    }

    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }

    public void setFirmwareVersion( String firmwareVersion )
    {
        this.firmwareVersion = firmwareVersion;
    }

    public String getFirmwareName()
    {
        return firmwareName;
    }

    public void setFirmwareName( String firmwareName )
    {
        this.firmwareName = firmwareName;
    }

    public String getOsName()
    {
        return osName;
    }

    public void setOsName( String osName )
    {
        this.osName = osName;
    }

    public String getOsVendor()
    {
        return osVendor;
    }

    public void setOsVendor( String osVendor )
    {
        this.osVendor = osVendor;
    }
}
