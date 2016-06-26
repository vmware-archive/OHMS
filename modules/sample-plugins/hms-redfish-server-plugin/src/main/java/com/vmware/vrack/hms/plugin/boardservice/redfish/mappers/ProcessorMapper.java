package com.vmware.vrack.hms.plugin.boardservice.redfish.mappers;

import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ProcessorResource;

/**
 * Class responsible for mapping Redfish Processor properties to HMS-specific data model objects
 */
public class ProcessorMapper
{

    public CPUInfo map( ProcessorResource processor )
    {
        CPUInfo cpuInfo = new CPUInfo();

        cpuInfo.setComponent( ServerComponent.CPU );

        if ( processor.getMaxSpeedMhz() != null )
        {
            cpuInfo.setMaxClockFrequency( processor.getMaxSpeedMhz().longValue() );
        }

        cpuInfo.setTotalCpuCores( processor.getTotalCores() );

        return cpuInfo;
    }

}
