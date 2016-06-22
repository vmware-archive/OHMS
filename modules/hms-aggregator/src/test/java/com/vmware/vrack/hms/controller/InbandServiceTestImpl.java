/* ********************************************************************************
 * InbandServiceTestImpl.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.controller;

import java.util.ArrayList;
import java.util.List;

import com.vmware.vrack.hms.common.boardvendorservice.api.ib.HypervisorInfo;
import com.vmware.vrack.hms.common.boardvendorservice.api.ib.IInbandService;
import com.vmware.vrack.hms.common.boardvendorservice.api.model.SystemDetails;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.SpeedInfo;
import com.vmware.vrack.hms.common.servernodes.api.SpeedUnit;
import com.vmware.vrack.hms.common.servernodes.api.bios.BiosInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.esxinfo.HostNameInfo;
import com.vmware.vrack.hms.common.servernodes.api.esxinfo.OSInfo;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.nic.NicStatus;
import com.vmware.vrack.hms.common.servernodes.api.nic.PortInfo;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;

/**
 * Dummy Inband service implementation class for Test cases
 *
 * @author VMware Inc.
 */
public class InbandServiceTestImpl
    implements IInbandService
{
    @Override
    public List<ServerComponentEvent> getComponentEventList( ServiceHmsNode serviceNode, ServerComponent component )
        throws HmsException
    {
        if ( component != null )
        {
            List<ServerComponentEvent> serverComponentEvents = new ArrayList<ServerComponentEvent>();
            switch ( component )
            {
                case NIC:
                    serverComponentEvents = new ArrayList<ServerComponentEvent>();
                    ServerComponentEvent nicEvent;
                    nicEvent = new ServerComponentEvent();
                    nicEvent.setEventId( "NIC1" );
                    nicEvent.setComponentId( "vmnic0" );
                    nicEvent.setEventName( NodeEvent.NIC_PACKET_DROP_ABOVE_THRESHHOLD );
                    nicEvent.setDiscreteValue( "Nic Packet Drop above thread" );
                    serverComponentEvents.add( nicEvent );
                    nicEvent = new ServerComponentEvent();
                    nicEvent.setEventId( "NIC1" );
                    nicEvent.setComponentId( "vmnic1" );
                    nicEvent.setEventName( NodeEvent.NIC_LINK_DOWN );
                    nicEvent.setDiscreteValue( "NIC Link Down" );
                    serverComponentEvents.add( nicEvent );
                    return serverComponentEvents;
                case STORAGE:
                    serverComponentEvents = new ArrayList<ServerComponentEvent>();
                    ServerComponentEvent hddEvent = new ServerComponentEvent();
                    hddEvent.setEventId( "HDD1" );
                    hddEvent.setComponentId( "HDD" );
                    hddEvent.setEventName( NodeEvent.HDD_WEAROUT_ABOVE_THRESHOLD );
                    hddEvent.setDiscreteValue( "Error" );
                    serverComponentEvents.add( hddEvent );
                    return serverComponentEvents;
                case MEMORY:
                    serverComponentEvents = new ArrayList<ServerComponentEvent>();
                    ServerComponentEvent memoryEvent = new ServerComponentEvent();
                    memoryEvent.setEventId( "DIMM1" );
                    memoryEvent.setComponentId( "MEMORY" );
                    memoryEvent.setEventName( NodeEvent.MEMORY_ECC_ERROR );
                    memoryEvent.setDiscreteValue( "Status" );
                    serverComponentEvents.add( memoryEvent );
                    return serverComponentEvents;
                case CPU:
                    serverComponentEvents = new ArrayList<ServerComponentEvent>();
                    ServerComponentEvent cpuEvent = new ServerComponentEvent();
                    cpuEvent.setEventId( "CPU1" );
                    cpuEvent.setComponentId( "CPU" );
                    cpuEvent.setEventName( NodeEvent.CPU_POST_FAILURE );
                    cpuEvent.setDiscreteValue( "CPU POST Failed" );
                    serverComponentEvents.add( cpuEvent );
                    return serverComponentEvents;
                case BMC:
                    serverComponentEvents = new ArrayList<ServerComponentEvent>();
                    ServerComponentEvent bmcEvent = new ServerComponentEvent();
                    bmcEvent.setEventId( "TestNode" );
                    bmcEvent.setComponentId( "BMC" );
                    bmcEvent.setEventName( NodeEvent.BMC_AUTHENTICATION_FAILURE );
                    bmcEvent.setDiscreteValue( "BMC Authentication" );
                    serverComponentEvents.add( bmcEvent );
                    return serverComponentEvents;
                default:
                    throw new HmsException( "Operation getComponentSensorList not supported for component: "
                        + component );
            }
        }
        else
        {
            throw new HmsException( "Operation getComponentSensorList not supported for component: " + component );
        }
    }

    @Override
    public List<HmsApi> getSupportedHmsApi( ServiceHmsNode serviceNode )
        throws HmsException
    {
        List<HmsApi> hmsApis = new ArrayList<HmsApi>();
        hmsApis.add( HmsApi.CPU_SENSOR_INFO );
        hmsApis.add( HmsApi.STORAGE_SENSOR_INFO );
        return hmsApis;
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
        return memories;
    }

    @Override
    public List<EthernetController> getNicInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        List<EthernetController> nics = new ArrayList<EthernetController>();
        EthernetController controller1 = new EthernetController();
        ComponentIdentifier nicIdentifier = new ComponentIdentifier();
        nicIdentifier.setManufacturer( "Intel" );
        nicIdentifier.setProduct( "Ethernet Controller x540EC" );
        controller1.setComponentIdentifier( nicIdentifier );
        List<PortInfo> portInfos = new ArrayList<PortInfo>();
        PortInfo portInfo1 = new PortInfo();
        SpeedInfo info = new SpeedInfo();
        info.setSpeed( (long) 1000 );
        info.setUnit( SpeedUnit.Mbps );
        portInfo1.setLinkSpeedInMBps( info );
        portInfo1.setDeviceName( "vmnic0" );
        portInfo1.setLinkStatus( NicStatus.OK );
        portInfos.add( portInfo1 );
        PortInfo portInfo2 = new PortInfo();
        portInfo2.setLinkSpeedInMBps( info );
        portInfo2.setDeviceName( "vmnic1" );
        portInfo2.setLinkStatus( NicStatus.DISCONNECTED );
        portInfos.add( portInfo2 );
        controller1.setPortInfos( portInfos );
        nics.add( controller1 );
        return nics;
    }

    @Override
    public List<HddInfo> getHddInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        List<HddInfo> hddInfos = new ArrayList<HddInfo>();
        HddInfo hddInfo1 = new HddInfo();
        hddInfo1.setName( "HDD1" );
        hddInfo1.setDiskCapacityInMB( 808080 );
        hddInfo1.setFirmwareInfo( "ABC09" );
        hddInfo1.setType( "HDD" );
        hddInfos.add( hddInfo1 );
        HddInfo hddinfo2 = new HddInfo();
        hddinfo2.setName( "HDD2" );
        hddinfo2.setDiskCapacityInMB( 808080 );
        hddinfo2.setFirmwareInfo( "ABC09" );
        hddinfo2.setType( "HDD" );
        hddInfos.add( hddinfo2 );
        return hddInfos;
    }

    @Override
    public List<HypervisorInfo> getSupportedHypervisorInfo()
    {
        // TODO Auto-generated method stub
        return null;
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
        List<StorageControllerInfo> storageControllerInfoList = new ArrayList<StorageControllerInfo>();
        StorageControllerInfo storageControllerInfo1 = new StorageControllerInfo();
        ComponentIdentifier componentIdentifier1 = new ComponentIdentifier();
        componentIdentifier1.setManufacturer( "Intel Corporation" );
        componentIdentifier1.setProduct( "Patsburg 6 Port SATA AHCI Controller" );
        storageControllerInfo1.setComponentIdentifier( componentIdentifier1 );
        storageControllerInfo1.setDeviceName( "vmhba1" );
        storageControllerInfo1.setNumOfStorageDevicesConnected( 1 );
        storageControllerInfo1.setDriver( "ahci" );
        storageControllerInfo1.setFirmwareVersion( "23fh.56" );
        storageControllerInfoList.add( storageControllerInfo1 );
        StorageControllerInfo storageControllerInfo2 = new StorageControllerInfo();
        ComponentIdentifier componentIdentifier2 = new ComponentIdentifier();
        componentIdentifier2.setManufacturer( "LSI" );
        componentIdentifier2.setProduct( "LSI2008" );
        storageControllerInfo2.setComponentIdentifier( componentIdentifier2 );
        storageControllerInfo2.setDeviceName( "vmhba2" );
        storageControllerInfo2.setNumOfStorageDevicesConnected( 9 );
        storageControllerInfo2.setDriver( "mpt2sas" );
        storageControllerInfo2.setFirmwareVersion( "59fh.51" );
        storageControllerInfoList.add( storageControllerInfo2 );
        return storageControllerInfoList;
    }
}
