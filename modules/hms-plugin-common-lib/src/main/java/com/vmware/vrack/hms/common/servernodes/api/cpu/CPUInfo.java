/* ********************************************************************************
 * CPUInfo.java
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
