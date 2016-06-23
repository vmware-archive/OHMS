/* ********************************************************************************
 * ServerInfo.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Class for Server Information related properties
 *
 * @author VMware Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ServerInfo
    extends FruComponent
{
    private String nodeId;

    private String managementIpAddress;

    private String inBandIpAddress;

    @Deprecated
    private boolean isPowered; // Chassis Power State

    @Deprecated
    private boolean isDiscoverable; // RMCP Discoverable State

    private String osName;

    private String osVendor;

    private String firmwareVersion;

    private List<CpuInfo> cpuInfo;

    private List<EthernetController> ethernetController;

    private List<StorageInfo> storageInfo;

    private List<StorageController> storageController;

    private List<MemoryInfo> memoryInfo;

    private String operationalStatus;

    private String adminStatus;

    private String validationStatus;

    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeId( String nodeId )
    {
        this.nodeId = nodeId;
    }

    public String getManagementIpAddress()
    {
        return managementIpAddress;
    }

    public void setManagementIpAddress( String managementIpAddress )
    {
        this.managementIpAddress = managementIpAddress;
    }

    public String getInBandIpAddress()
    {
        return inBandIpAddress;
    }

    public void setInBandIpAddress( String inBandIpAddress )
    {
        this.inBandIpAddress = inBandIpAddress;
    }

    /*
     * isPowered and isDiscoverable properties will be removed, use operationalStatus
     */
    @Deprecated
    public boolean isPowered()
    {
        return isPowered;
    }

    @Deprecated
    public void setPowered( boolean isPowered )
    {
        this.isPowered = isPowered;

    }

    @Deprecated
    public boolean isDiscoverable()
    {
        return isDiscoverable;
    }

    @Deprecated
    public void setDiscoverable( boolean isDiscoverable )
    {

        this.isDiscoverable = isDiscoverable;
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

    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }

    public void setFirmwareVersion( String firmwareVersion )
    {
        this.firmwareVersion = firmwareVersion;
    }

    public List<CpuInfo> getCpuInfo()
    {
        return cpuInfo;
    }

    public void setCpuInfo( List<CpuInfo> cpuInfo )
    {
        this.cpuInfo = cpuInfo;
    }

    public List<EthernetController> getEthernetControllerList()
    {
        return ethernetController;
    }

    public void setEthernetControllerList( List<EthernetController> ethernetController )
    {
        this.ethernetController = ethernetController;
    }

    public void setEthernetController( EthernetController ethernet )
    {
        if ( this.ethernetController == null && ethernet != null )
        {
            ArrayList<EthernetController> controllers = new ArrayList<EthernetController>();
            controllers.add( ethernet );
            setEthernetControllerList( controllers );
        }
        else if ( ethernetController != null && !getEthernetControllerList().contains( ethernet ) )
        {
            getEthernetControllerList().add( ethernet );
        }
    }

    public List<StorageInfo> getStorageInfo()
    {
        return storageInfo;
    }

    public void setStorageInfo( List<StorageInfo> storageInfo )
    {
        this.storageInfo = storageInfo;
    }

    public List<MemoryInfo> getMemoryInfo()
    {
        return memoryInfo;
    }

    public void setMemoryInfo( List<MemoryInfo> memoryInfo )
    {
        this.memoryInfo = memoryInfo;
    }

    public String getOperationalStatus()
    {
        return operationalStatus;
    }

    public void setOperationalStatus( String operationalStatus )
    {
        this.operationalStatus = operationalStatus;
    }

    public String getAdminStatus()
    {
        return adminStatus;
    }

    public void setAdminStatus( String adminStatus )
    {
        this.adminStatus = adminStatus;
    }

    public String getValidationStatus()
    {
        return validationStatus;
    }

    public void setValidationStatus( String validationStatus )
    {
        this.validationStatus = validationStatus;
    }

    public List<StorageController> getStorageController()
    {
        return storageController;
    }

    public void setStorageController( List<StorageController> storageController )
    {
        this.storageController = storageController;
    }
}
