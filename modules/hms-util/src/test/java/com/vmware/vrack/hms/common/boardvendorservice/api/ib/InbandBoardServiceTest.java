/* ********************************************************************************
 * InbandBoardServiceTest.java
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
package com.vmware.vrack.hms.common.boardvendorservice.api.ib;

import java.util.ArrayList;
import java.util.List;

import com.vmware.vrack.hms.common.boardvendorservice.api.model.SystemDetails;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.bios.BiosInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.esxinfo.HostNameInfo;
import com.vmware.vrack.hms.common.servernodes.api.esxinfo.OSInfo;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;

/*
 * InbandBoardServiceTest class is dummy class to unit test InBandServiceFactory
 */
@InBandServiceImplementation( name = "TEST_ESXI" )
public class InbandBoardServiceTest
    implements IInbandService
{

    private List<HypervisorInfo> supportedHypervisor;

    public InbandBoardServiceTest()
    {
        super();
        HypervisorInfo hypervisor = new HypervisorInfo();
        hypervisor.setName( "testesxi" );
        hypervisor.setProvider( "testware" );
        addSupportedBoard( hypervisor );
    }

    public boolean addSupportedBoard( HypervisorInfo hypervisor )
    {
        if ( supportedHypervisor == null )
        {
            supportedHypervisor = new ArrayList<HypervisorInfo>();
        }

        return supportedHypervisor.add( hypervisor );
    }

    @Override
    public List<ServerComponentEvent> getComponentEventList( ServiceHmsNode serviceNode, ServerComponent component )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<HmsApi> getSupportedHmsApi( ServiceHmsNode serviceNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CPUInfo> getCpuInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        List<CPUInfo> cpus = new ArrayList<CPUInfo>();
        CPUInfo cpu1 = new CPUInfo();
        ComponentIdentifier cpuIdentifier1 = new ComponentIdentifier();
        cpuIdentifier1.setManufacturer( "INTEL" );
        cpuIdentifier1.setProduct( "Intel Xeon Processor" );
        cpu1.setComponentIdentifier( cpuIdentifier1 );
        cpu1.setId( "1" );
        cpu1.setMaxClockFrequency( (long) 2600 );
        cpu1.setTotalCpuCores( 4 );
        cpus.add( cpu1 );

        CPUInfo cpu2 = new CPUInfo();
        ComponentIdentifier cpuIdentifier2 = new ComponentIdentifier();
        cpuIdentifier2.setManufacturer( "INTEL" );
        cpuIdentifier2.setProduct( "Intel Xeon Processor" );
        cpu1.setComponentIdentifier( cpuIdentifier2 );
        cpu2.setId( "2" );
        cpu2.setMaxClockFrequency( (long) 2600 );
        cpu2.setTotalCpuCores( 4 );
        cpus.add( cpu2 );

        return cpus;
    }

    @Override
    public List<PhysicalMemory> getSystemMemoryInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        List<PhysicalMemory> memories = new ArrayList<PhysicalMemory>();
        PhysicalMemory memory1 = new PhysicalMemory();
        ComponentIdentifier memoryIdentifier1 = new ComponentIdentifier();
        memoryIdentifier1.setProduct( "DIMM" );
        memoryIdentifier1.setManufacturer( "Kingston" );
        memory1.setComponentIdentifier( memoryIdentifier1 );
        memory1.setLocation( "DIMM1" );
        memory1.setId( "1" );
        memory1.setMaxMemorySpeedInHertz( (long) 1600000 );
        memories.add( memory1 );

        PhysicalMemory memory2 = new PhysicalMemory();
        ComponentIdentifier memoryIdentifier2 = new ComponentIdentifier();
        memoryIdentifier2.setProduct( "DIMM" );
        memoryIdentifier2.setManufacturer( "Samsung" );
        memory1.setComponentIdentifier( memoryIdentifier2 );
        memory2.setLocation( "DIMM2" );
        memory2.setId( "2" );
        memory2.setMaxMemorySpeedInHertz( (long) 1600000 );
        memories.add( memory2 );

        try
        {
            Thread.sleep( 7000L );
        }
        catch ( InterruptedException e )
        {
        }

        return memories;
    }

    @Override
    public List<EthernetController> getNicInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<HddInfo> getHddInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<HypervisorInfo> getSupportedHypervisorInfo()
    {

        return supportedHypervisor;
    }

    @Override
    public OSInfo getOperatingSystemInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BiosInfo getBiosInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SystemDetails getSystemDetails( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void init( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public HostNameInfo getHostName( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<StorageControllerInfo> getStorageControllerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
