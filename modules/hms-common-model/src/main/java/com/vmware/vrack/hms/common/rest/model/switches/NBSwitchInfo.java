/* ********************************************************************************
 * NBSwitchInfo.java
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
package com.vmware.vrack.hms.common.rest.model.switches;

import java.util.List;

import com.vmware.vrack.hms.common.resource.ValidationStatus;
import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.rest.model.FruComponent;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;

public class NBSwitchInfo
    extends FruComponent
{
    public enum SwitchRoleType
    {
        TOR, MANAGEMENT, SPINE
    };

    private String switchId;

    private String osName;

    private String osVersion;

    private String firmwareName;

    private String firmwareVersion;

    private FruOperationalStatus operationalStatus;

    private NodeAdminStatus adminStatus;

    private SwitchRoleType role;

    private String ipAddress;

    private String mangementPort;

    private NBSwitchSensorInfo sensors;

    private ValidationStatus validationStatus;

    private String managementMacAddress;

    private List<NBSwitchPortInfo> ports;

    /* Read/Write configuration of this switch */
    private NBSwitchConfig config;

    /**
     * @return the switchId
     */
    public String getSwitchId()
    {
        return switchId;
    }

    /**
     * @param switchId the switchId to set
     */
    public void setSwitchId( String switchId )
    {
        this.switchId = switchId;
    }

    /**
     * @return the osName
     */
    public String getOsName()
    {
        return osName;
    }

    /**
     * @param osName the osName to set
     */
    public void setOsName( String osName )
    {
        this.osName = osName;
    }

    /**
     * @return the osVersion
     */
    public String getOsVersion()
    {
        return osVersion;
    }

    /**
     * @param osVersion the osVersion to set
     */
    public void setOsVersion( String osVersion )
    {
        this.osVersion = osVersion;
    }

    /**
     * @return the firmwareName
     */
    public String getFirmwareName()
    {
        return firmwareName;
    }

    /**
     * @param firmwareName the firmwareName to set
     */
    public void setFirmwareName( String firmwareName )
    {
        this.firmwareName = firmwareName;
    }

    /**
     * @return the firmwareVersion
     */
    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }

    /**
     * @param firmwareVersion the firmwareVersion to set
     */
    public void setFirmwareVersion( String firmwareVersion )
    {
        this.firmwareVersion = firmwareVersion;
    }

    /**
     * @return the operational_status
     */
    public FruOperationalStatus getOperationalStatus()
    {
        return operationalStatus;
    }

    /**
     * @param operational_status the operational_status to set
     */
    public void setOperationalStatus( FruOperationalStatus operationalStatus )
    {
        this.operationalStatus = operationalStatus;
    }

    /**
     * @return the role
     */
    public SwitchRoleType getRole()
    {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole( SwitchRoleType role )
    {
        this.role = role;
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress()
    {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress( String ipAddress )
    {
        this.ipAddress = ipAddress;
    }

    /**
     * @return the mangementPort
     */
    public String getMangementPort()
    {
        return mangementPort;
    }

    /**
     * @param mangementPort the mangementPort to set
     */
    public void setMangementPort( String mangementPort )
    {
        this.mangementPort = mangementPort;
    }

    /**
     * @return the portRuntimeInfos
     */
    public List<NBSwitchPortInfo> getPorts()
    {
        return ports;
    }

    /**
     * @param portRuntimeInfos the portRuntimeInfos to set
     */
    public void setPorts( List<NBSwitchPortInfo> ports )
    {
        this.ports = ports;
    }

    /**
     * @return the config
     */
    public NBSwitchConfig getConfig()
    {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig( NBSwitchConfig config )
    {
        this.config = config;
    }

    /**
     * @return the sensors
     */
    public NBSwitchSensorInfo getSensors()
    {
        return sensors;
    }

    /**
     * @param sensors the sensors to set
     */
    public void setSensors( NBSwitchSensorInfo sensors )
    {
        this.sensors = sensors;
    }

    /**
     * @return the adminStatus
     */
    public NodeAdminStatus getAdminStatus()
    {
        return adminStatus;
    }

    /**
     * @param adminStatus the adminStatus to set
     */
    public void setAdminStatus( NodeAdminStatus adminStatus )
    {
        this.adminStatus = adminStatus;
    }

    public ValidationStatus getValidationStatus()
    {
        return validationStatus;
    }

    public void setValidationStatus( ValidationStatus validationStatus )
    {
        this.validationStatus = validationStatus;
    }

    public String getManagementMacAddress()
    {
        return managementMacAddress;
    }

    public void setManagementMacAddress( String managementMacAddress )
    {
        this.managementMacAddress = managementMacAddress;
    }
}
