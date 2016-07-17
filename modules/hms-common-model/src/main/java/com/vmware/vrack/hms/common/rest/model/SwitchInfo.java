/* ********************************************************************************
 * SwitchInfo.java
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
package com.vmware.vrack.hms.common.rest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.switches.api.SwitchNode.SwitchRoleType;

@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class SwitchInfo
    extends FruComponent
{
    private String switchId;

    private List<String> switchPorts;

    private String ipAddress;

    private String mangementPort;

    private String managementMacAddress;

    private String operational_status;

    private String osName;

    private String osVersion;

    private String firmwareName;

    private String firmwareVersion;

    private String adminStatus;

    private SwitchRoleType role;

    private String validationStatus;

    private boolean powered;

    private boolean discoverable;

    public SwitchRoleType getRole()
    {
        return role;
    }

    public void setRole( SwitchRoleType role )
    {
        this.role = role;
    }

    public String getSwitchId()
    {
        return switchId;
    }

    public void setSwitchId( String switchId )
    {
        this.switchId = switchId;
    }

    public List<String> getSwitchPorts()
    {
        return switchPorts;
    }

    public void setSwitchPorts( List<String> switchPorts )
    {
        this.switchPorts = switchPorts;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress( String ipAddress )
    {
        this.ipAddress = ipAddress;
    }

    public String getMangementPort()
    {
        return mangementPort;
    }

    public void setMangementPort( String mangementPort )
    {
        this.mangementPort = mangementPort;
    }

    public String getOperational_status()
    {
        return operational_status;
    }

    public void setOperational_status( String operational_status )
    {
        this.operational_status = operational_status;
    }

    public String getOsName()
    {
        return osName;
    }

    public void setOsName( String osName )
    {
        this.osName = osName;
    }

    public String getOsVersion()
    {
        return osVersion;
    }

    public void setOsVersion( String osVersion )
    {
        this.osVersion = osVersion;
    }

    public String getFirmwareName()
    {
        return firmwareName;
    }

    public void setFirmwareName( String firmwareName )
    {
        this.firmwareName = firmwareName;
    }

    public String getAdminStatus()
    {
        return adminStatus;
    }

    public void setAdminStatus( String adminStatus )
    {
        this.adminStatus = adminStatus;
    }

    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }

    public void setFirmwareVersion( String firmwareVersion )
    {
        this.firmwareVersion = firmwareVersion;
    }

    public String getManagementMacAddress()
    {
        return managementMacAddress;
    }

    public void setManagementMacAddress( String managementMacAddress )
    {
        this.managementMacAddress = managementMacAddress;
    }

    public String getValidationStatus()
    {
        return validationStatus;
    }

    public void setValidationStatus( String validationStatus )
    {
        this.validationStatus = validationStatus;
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
}
