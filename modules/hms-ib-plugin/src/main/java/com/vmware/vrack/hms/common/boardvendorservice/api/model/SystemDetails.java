/* ********************************************************************************
 * SystemDetails.java
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
package com.vmware.vrack.hms.common.boardvendorservice.api.model;

import java.util.List;

import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;

/**
 * Model class for basic System components details such as CPU, NIC, Memory, HDD
 * 
 * @author VMware, Inc.
 */
public class SystemDetails
{
    private List<CPUInfo> cpuInfos;

    private List<PhysicalMemory> memoryInfos;

    private List<EthernetController> nicInfos;

    private List<HddInfo> hddInfos;

    public List<CPUInfo> getCpuInfos()
    {
        return cpuInfos;
    }

    public void setCpuInfos( List<CPUInfo> cpuInfos )
    {
        this.cpuInfos = cpuInfos;
    }

    public List<PhysicalMemory> getMemoryInfos()
    {
        return memoryInfos;
    }

    public void setMemoryInfos( List<PhysicalMemory> memoryInfos )
    {
        this.memoryInfos = memoryInfos;
    }

    public List<EthernetController> getNicInfos()
    {
        return nicInfos;
    }

    public void setNicInfos( List<EthernetController> nicInfos )
    {
        this.nicInfos = nicInfos;
    }

    public List<HddInfo> getHddInfos()
    {
        return hddInfos;
    }

    public void setHddInfos( List<HddInfo> hddInfos )
    {
        this.hddInfos = hddInfos;
    }
}
