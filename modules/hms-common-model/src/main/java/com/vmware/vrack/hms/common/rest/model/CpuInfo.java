/* ********************************************************************************
 * CpuInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;

/**
 * Class for CPU related Properties
 *
 * @author VMware Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class CpuInfo
    extends FruComponent
{
    private String id;

    private int numOfCores;

    private long cpuFrequencyInHertz;

    private String firmwareVersion;

    private String operationalStatus;

    private String hostId;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }

    public void setFirmwareVersion( String firmwareVersion )
    {
        this.firmwareVersion = firmwareVersion;
    }

    public int getNumOfCores()
    {
        return numOfCores;
    }

    public void setNumOfCores( int numOfCores )
    {
        this.numOfCores = numOfCores;
    }

    public long getCpuFrequencyInHertz()
    {
        return cpuFrequencyInHertz;
    }

    public void setCpuFrequencyInHertz( long cpuFrequencyInHertz )
    {
        this.cpuFrequencyInHertz = cpuFrequencyInHertz;
    }

    public String getOperationalStatus()
    {
        return operationalStatus;
    }

    public void setOperationalStatus( String operationalStatus )
    {
        this.operationalStatus = operationalStatus;
    }

    public String getHostId()
    {
        return hostId;
    }

    public void setHostId( String hostId )
    {
        this.hostId = hostId;
    }

    /**
     * Get the Physical CPU FRU information. Wrapper method to get the CpuInfo object for the node
     *
     * @param serverNodeCpuInfo
     * @param nodeID
     * @return CpuInfo
     */
    public CpuInfo getCpuInfo( CPUInfo serverNodeCpuInfo, String nodeID )
    {
        CpuInfo cpuInfo = new CpuInfo();
        cpuInfo.setId( serverNodeCpuInfo.getId() );
        cpuInfo.setCpuFrequencyInHertz( serverNodeCpuInfo.getMaxClockFrequency() );
        cpuInfo.setNumOfCores( serverNodeCpuInfo.getTotalCpuCores() );
        cpuInfo.setFirmwareVersion( serverNodeCpuInfo.getFirmwareVersion() );
        cpuInfo.setLocation( serverNodeCpuInfo.getLocation() );
        cpuInfo.setHostId( nodeID );
        if ( serverNodeCpuInfo.getComponentIdentifier() != null )
        {
            cpuInfo.setComponentIdentifier( serverNodeCpuInfo.getComponentIdentifier() );
        }
        return cpuInfo;
    }
}
