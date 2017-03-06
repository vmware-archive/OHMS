/* ********************************************************************************
 * CpuInfo.java
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

        if ( serverNodeCpuInfo.getFruOperationalStatus() != null )
        {
            cpuInfo.setOperationalStatus( serverNodeCpuInfo.getFruOperationalStatus().toString() );
        }

        if ( serverNodeCpuInfo.getComponentIdentifier() != null )
        {
            cpuInfo.setComponentIdentifier( serverNodeCpuInfo.getComponentIdentifier() );
        }

        return cpuInfo;
    }
}
