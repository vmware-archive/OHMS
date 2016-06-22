/* ********************************************************************************
 * CPUInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api.cpu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.servernodes.api.AbstractServerComponent;

/**
 * Class for CPU related Properties CPUInfo has the FRU component indentifiers which helps to identify the Server
 * component CPU FRU
 *
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class CPUInfo
    extends AbstractServerComponent
{
    private Long maxClockFrequency;

    private Integer totalCpuCores;

    private String firmwareVersion;

    public Long getMaxClockFrequency()
    {
        return maxClockFrequency;
    }

    public void setMaxClockFrequency( Long maxClockFrequency )
    {
        this.maxClockFrequency = maxClockFrequency;
    }

    public Integer getTotalCpuCores()
    {
        return totalCpuCores;
    }

    public void setTotalCpuCores( Integer totalCpuCores )
    {
        this.totalCpuCores = totalCpuCores;
    }

    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }

    public void setFirmwareVersion( String firmwareVersion )
    {
        this.firmwareVersion = firmwareVersion;
    }
}
