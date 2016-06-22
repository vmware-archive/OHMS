/* ********************************************************************************
 * IInbandService.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.api.ib;

import java.util.List;

import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.boardvendorservice.api.model.SystemDetails;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.servernodes.api.bios.BiosInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.esxinfo.HostNameInfo;
import com.vmware.vrack.hms.common.servernodes.api.esxinfo.OSInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;

/**
 * Interface to be implemented For performing Inband Operations.
 *
 * @author VMware, Inc.
 */
public interface IInbandService
    extends IComponentEventInfoProvider
{
    /**
     * Get CPU Information
     * 
     * @param hmsNode
     */
    public List<CPUInfo> getCpuInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Get Memory Information
     * 
     * @param hmsNode
     */
    public List<PhysicalMemory> getSystemMemoryInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Get NIC information
     * 
     * @param hmsNode
     */
    public List<EthernetController> getNicInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Get the Storage/HDD/SSD device information
     * 
     * @param node
     * @return List<HddInfo>
     * @throws HmsException
     */
    public List<HddInfo> getHddInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Get the Storage controller/HBA adapter information
     * 
     * @param node
     * @return List<StorageControllerInfo>
     * @throws HmsException
     */
    public List<StorageControllerInfo> getStorageControllerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Get the supported Hypervisor information
     * 
     * @return List<HypervisorInfo>
     */
    public List<HypervisorInfo> getSupportedHypervisorInfo();

    /**
     * Returns information about system
     * 
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    public OSInfo getOperatingSystemInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Returns information about bios
     * 
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    public BiosInfo getBiosInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Provides details about basic components of system. Such as CPU, NIC, HDD, and Memory
     * 
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    public SystemDetails getSystemDetails( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Initialize Inband properties.
     * 
     * @param serviceHmsNode
     * @throws HmsException
     */
    public void init( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Get Host Name of the Node.
     * 
     * @param serviceHmsNode
     * @throws HmsException
     */
    public HostNameInfo getHostName( ServiceHmsNode serviceHmsNode )
        throws HmsException;
}
