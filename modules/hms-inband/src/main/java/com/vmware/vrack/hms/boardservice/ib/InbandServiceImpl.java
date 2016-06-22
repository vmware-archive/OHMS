/* ********************************************************************************
 * InbandServiceImpl.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.boardservice.ib;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim.binding.vim.HostSystem;
import com.vmware.vim.binding.vim.host.HardwareInfo;
import com.vmware.vrack.hms.boardservice.ib.api.BiosInfoHelper;
import com.vmware.vrack.hms.boardservice.ib.api.CpuInfoHelper;
import com.vmware.vrack.hms.boardservice.ib.api.ESXIInfoHelper;
import com.vmware.vrack.hms.boardservice.ib.api.HddInfoHelper;
import com.vmware.vrack.hms.boardservice.ib.api.NicInfoHelper;
import com.vmware.vrack.hms.boardservice.ib.api.StorageControllerInfoHelper;
import com.vmware.vrack.hms.boardservice.ib.api.cim.MemoryInfoHelper;
import com.vmware.vrack.hms.common.boardvendorservice.api.ib.HypervisorInfo;
import com.vmware.vrack.hms.common.boardvendorservice.api.ib.IInbandService;
import com.vmware.vrack.hms.common.boardvendorservice.api.ib.InBandServiceImplementation;
import com.vmware.vrack.hms.common.boardvendorservice.api.model.SystemDetails;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
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
import com.vmware.vrack.hms.common.util.HostProxyProvider;
import com.vmware.vrack.hms.vsphere.HostProxy;
import com.vmware.vrack.hms.vsphere.VsphereClient;

/**
 * @author Vmware
 */
@InBandServiceImplementation( name = "VMWARE_ESXI" )
public class InbandServiceImpl
    implements IInbandService
{
    private static Logger logger = LoggerFactory.getLogger( InbandServiceImpl.class );

    private List<HypervisorInfo> supportedHypervisor;

    private static final String SERVER_NODE_NULL_ERR = "Node is NOT Server Node or is NULL: ";

    public InbandServiceImpl()
    {
        super();
        HypervisorInfo hypervisor = new HypervisorInfo();
        hypervisor.setName( "ESXI" );
        hypervisor.setProvider( "VMWARE" );
        addSupportedBoard( hypervisor );
    }

    @Override
    public List<CPUInfo> getCpuInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            List<CPUInfo> cpuInfos = null;
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            try
            {
                HostProxy hostProxy = getHostProxy( node );
                cpuInfos = getCpuInfo( node, hostProxy );
            }
            catch ( Exception e )
            {
                String err = "Cannot get CPU info for Node:" + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return cpuInfos;
        }
        else
        {
            logger.error( SERVER_NODE_NULL_ERR + serviceHmsNode );
            throw new HmsException( SERVER_NODE_NULL_ERR + serviceHmsNode );
        }
    }

    /**
     * Overriden function to get CPU Info while reusing the HostProxy object
     *
     * @param serviceHmsNode
     * @param hostProxy
     * @return
     * @throws HmsException
     */
    public List<CPUInfo> getCpuInfo( ServiceHmsNode serviceHmsNode, HostProxy hostProxy )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode && hostProxy != null )
        {
            List<CPUInfo> cpuInfos = null;
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            try
            {
                HostSystem hostSystem = hostProxy.getHostSystem();
                if ( hostSystem != null && hostSystem.getHardware() != null )
                {
                    HardwareInfo hardwareInfo = hostSystem.getHardware();
                    cpuInfos = CpuInfoHelper.getCpuInfo( hardwareInfo );
                }
            }
            catch ( Exception e )
            {
                String err = "Cannot get CPU info for Node:" + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return cpuInfos;
        }
        else
        {
            String err =
                "Node is NOT Server Node or HostProxy is NULL." + "HostProxy: " + hostProxy + "Node: " + serviceHmsNode;
            logger.error( err );
            throw new HmsException( err );
        }
    }

    @Override
    public List<PhysicalMemory> getSystemMemoryInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            List<PhysicalMemory> physicalMemories = null;
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            try
            {
                try
                {
                    MemoryInfoHelper memoryInfoHelper = new MemoryInfoHelper( node );
                    physicalMemories = memoryInfoHelper.getMemoryInfo();
                }
                catch ( Exception e )
                {
                    String err = "Cannot get Memory info for Node via CIM: " + node.getNodeID();
                    logger.error( err, e );
                    logger.warn( "Unable to get Memory info via CIM for node: " + node.getNodeID()
                        + ", now trying with Vsphere." );
                    HostProxy hostProxy = getHostProxy( node );
                    physicalMemories = getSystemMemoryInfo( node, hostProxy );
                }
            }
            catch ( Exception e )
            {
                String err = "Cannot get Memory info for Node: " + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return physicalMemories;
        }
        else
        {
            logger.error( SERVER_NODE_NULL_ERR + serviceHmsNode );
            throw new HmsException( SERVER_NODE_NULL_ERR + serviceHmsNode );
        }
    }

    /**
     * Fallback option for Memory Information via VSphere Client, in case CIM Client fails
     *
     * @param serviceHmsNode
     * @param hostProxy
     * @return
     * @throws HmsException
     */
    public List<PhysicalMemory> getSystemMemoryInfo( ServiceHmsNode serviceHmsNode, HostProxy hostProxy )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode && hostProxy != null )
        {
            List<PhysicalMemory> physicalMemories = null;
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            try
            {
                HostSystem hostSystem = hostProxy.getHostSystem();
                if ( hostSystem != null && hostSystem.getHardware() != null )
                {
                    HardwareInfo hardwareInfo = hostSystem.getHardware();
                    physicalMemories =
                        com.vmware.vrack.hms.boardservice.ib.api.MemoryInfoHelper.getMemoryInfo( hardwareInfo );
                }
            }
            catch ( Exception e )
            {
                String err = "Cannot get Memory info via Vsphere for Node:" + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return physicalMemories;
        }
        else
        {
            logger.error( SERVER_NODE_NULL_ERR + serviceHmsNode );
            throw new HmsException( SERVER_NODE_NULL_ERR + serviceHmsNode );
        }
    }

    @Override
    public List<HddInfo> getHddInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            List<HddInfo> hddInfoList = null;
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            try
            {
                HostProxy hostProxy = getHostProxy( node );
                hddInfoList = getHddInfo( node, hostProxy );
            }
            catch ( Exception e )
            {
                String err = "Cannot get Hdd info for Node:" + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return hddInfoList;
        }
        else
        {
            logger.error( SERVER_NODE_NULL_ERR + serviceHmsNode );
            throw new HmsException( SERVER_NODE_NULL_ERR + serviceHmsNode );
        }
    }

    /**
     * Overriden function to get HDD Info while reusing the HostProxy object
     *
     * @param serviceHmsNode
     * @param hostProxy
     * @return
     * @throws HmsException
     */
    public List<HddInfo> getHddInfo( ServiceHmsNode serviceHmsNode, HostProxy hostProxy )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode && hostProxy != null )
        {
            List<HddInfo> hddInfoList = null;
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            try
            {
                HostSystem hostSystem = hostProxy.getHostSystem();
                VsphereClient client = hostProxy.getVsphereClient();
                if ( hostSystem != null )
                {
                    hddInfoList = HddInfoHelper.getHddInfo( hostSystem, client, node );
                }
            }
            catch ( Exception e )
            {
                String err = "Cannot get Hdd info for Node:" + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return hddInfoList;
        }
        else
        {
            logger.error( "Node is NOT Server Node or HostProxy is NULL." + "HostProxy: " + hostProxy + "Node: "
                + serviceHmsNode );
            throw new HmsException( "Node is NOT Server Node or HostProxy is NULL." + "HostProxy: " + hostProxy
                + "Node: " + serviceHmsNode );
        }
    }

    @Override
    public List<EthernetController> getNicInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            List<EthernetController> ethernetControllers = null;
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            try
            {
                HostProxy hostProxy = getHostProxy( node );
                ethernetControllers = getNicInfo( node, hostProxy );
            }
            catch ( Exception e )
            {
                String err = "Cannot get Nic info for Node:" + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return ethernetControllers;
        }
        else
        {
            logger.error( SERVER_NODE_NULL_ERR + serviceHmsNode );
            throw new HmsException( SERVER_NODE_NULL_ERR + serviceHmsNode );
        }
    }

    /**
     * Overriden function to get NIC Info while reusing the HostProxy object
     *
     * @param serviceHmsNode
     * @param hostProxy
     * @return
     * @throws HmsException
     */
    public List<EthernetController> getNicInfo( ServiceHmsNode serviceHmsNode, HostProxy hostProxy )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode && hostProxy != null )
        {
            List<EthernetController> ethernetControllers = null;
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            try
            {
                ethernetControllers = NicInfoHelper.getNicInfo( hostProxy, node );
            }
            catch ( Exception e )
            {
                String err = "Cannot get Nic info for Node:" + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return ethernetControllers;
        }
        else
        {
            logger.error( "Node is NOT Server Node or HostProxy is NULL." + "HostProxy: " + hostProxy + "Node: "
                + serviceHmsNode );
            throw new HmsException( "Node is NOT Server Node or HostProxy is NULL." + "HostProxy: " + hostProxy
                + "Node: " + serviceHmsNode );
        }
    }

    @Override
    public List<HypervisorInfo> getSupportedHypervisorInfo()
    {
        return supportedHypervisor;
    }

    public boolean addSupportedBoard( HypervisorInfo hypervisor )
    {
        if ( supportedHypervisor == null )
        {
            supportedHypervisor = new ArrayList<HypervisorInfo>();
        }
        return supportedHypervisor.add( hypervisor );
    }

    /**
     * Returns BIOS information
     *
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    @Override
    public BiosInfo getBiosInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            BiosInfo biosInfo = null;
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            try
            {
                HostProxy hostProxy = getHostProxy( node );
                // HostManager.getInstance().connect(node.getIbIpAddress(), node.getOsUserName(), node.getOsPassword());
                HostSystem hostSystem = hostProxy.getHostSystem();
                if ( hostSystem != null && hostSystem.getHardware() != null )
                {
                    HardwareInfo hardwareInfo = hostSystem.getHardware();
                    biosInfo = BiosInfoHelper.getBiosInfo( hardwareInfo );
                }
            }
            catch ( Exception e )
            {
                String err = "Cannot get BIOS info for Node:" + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return biosInfo;
        }
        else
        {
            logger.error( SERVER_NODE_NULL_ERR + serviceHmsNode );
            throw new HmsException( SERVER_NODE_NULL_ERR + serviceHmsNode );
        }
    }

    /**
     * Get Server Component Specific Sensor Data
     *
     * @param serviceNode
     * @param component
     * @return List<ServerComponentSensor>
     * @throws HmsException
     */
    @Override
    public List<ServerComponentEvent> getComponentEventList( ServiceHmsNode serviceNode, ServerComponent component )
        throws HmsException
    {
        if ( component != null )
        {
            switch ( component )
            {
                case NIC:
                    return NicInfoHelper.getNicSensor( serviceNode, component, this );
                case STORAGE:
                    return HddInfoHelper.getHddSensor( serviceNode, component, this );
                case STORAGE_CONTROLLER:
                    return StorageControllerInfoHelper.getServerComponentStorageControllerEvent( serviceNode,
                                                                                                 component, this );
                case CPU:
                    return CpuInfoHelper.getCpuSensor( serviceNode, component, this );
                case MEMORY:
                    return com.vmware.vrack.hms.boardservice.ib.api.MemoryInfoHelper.getMemorySensor( serviceNode,
                                                                                                      component, this );
                default:
                    throw new HmsException( "Operation getComponentSensorList not supported for component: "
                        + component );
            }
        }
        else
        {
            logger.error( "The ServerComponent is null or invalid" );
            throw new HmsException( "The ServerComponent is null or invalid" );
        }
    }

    @Override
    public List<HmsApi> getSupportedHmsApi( ServiceHmsNode serviceNode )
        throws HmsException
    {
        List<HmsApi> supportedAPI = new ArrayList<HmsApi>();
        supportedAPI.add( HmsApi.CPU_INFO );
        supportedAPI.add( HmsApi.MEMORY_INFO );
        supportedAPI.add( HmsApi.STORAGE_INFO );
        supportedAPI.add( HmsApi.NIC_INFO );
        supportedAPI.add( HmsApi.STORAGE_CONTROLLER_INFO );
        supportedAPI.add( HmsApi.CPU_SENSOR_INFO );
        supportedAPI.add( HmsApi.MEMORY_SENSOR_INFO );
        supportedAPI.add( HmsApi.NIC_SENSOR_INFO );
        supportedAPI.add( HmsApi.STORAGE_SENSOR_INFO );
        supportedAPI.add( HmsApi.STORAGE_CONTROLLER_SENSOR_INFO );
        return supportedAPI;
    }

    /**
     * Returns ESXi information
     *
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    @Override
    public OSInfo getOperatingSystemInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            OSInfo esxiInfo = null;
            try
            {
                HostProxy hostProxy = getHostProxy( node );
                // HostManager.getInstance().connect(node.getIbIpAddress(), node.getOsUserName(), node.getOsPassword());
                HostSystem hostSystem = hostProxy.getHostSystem();
                VsphereClient client = hostProxy.getVsphereClient();
                if ( client != null )
                {
                    esxiInfo = ESXIInfoHelper.getEsxiInfo( client );
                }
            }
            catch ( Exception e )
            {
                String err = "Cannot get ESXI info for Node:" + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return esxiInfo;
        }
        else
        {
            logger.error( SERVER_NODE_NULL_ERR + serviceHmsNode );
            throw new HmsException( SERVER_NODE_NULL_ERR + serviceHmsNode );
        }
    }

    /**
     * Gives the SystemDetails Object that contains the CPU, NIC, HDD and Memory information
     */
    @Override
    public SystemDetails getSystemDetails( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            SystemDetails systemDetails = new SystemDetails();
            HostProxy hostProxy = null;
            try
            {
                hostProxy = getHostProxy( node );
            }
            catch ( Exception e )
            {
                logger.error( "Unable to create HostProxy object: " + node, e );
            }
            if ( hostProxy != null )
            {
                try
                {
                    systemDetails.setCpuInfos( getCpuInfo( node, hostProxy ) );
                }
                catch ( Exception e )
                {
                    logger.error( "Unable to get CPU details for node: " + node, e );
                }
                try
                {
                    systemDetails.setNicInfos( getNicInfo( node, hostProxy ) );
                }
                catch ( Exception e )
                {
                    logger.error( "Unable to get Nic details for node: " + node, e );
                }
                try
                {
                    systemDetails.setHddInfos( getHddInfo( node, hostProxy ) );
                }
                catch ( Exception e )
                {
                    logger.error( "Unable to get Hdd details for node: " + node, e );
                }
            }
            try
            {
                systemDetails.setMemoryInfos( getSystemMemoryInfo( node ) );
            }
            catch ( Exception e )
            {
                logger.error( "Unable to get System Memory details for node: " + node, e );
            }
            return systemDetails;
        }
        else
        {
            logger.error( SERVER_NODE_NULL_ERR + serviceHmsNode );
            throw new HmsException( SERVER_NODE_NULL_ERR + serviceHmsNode );
        }
    }

    /**
     * Gets the HostProxy object that can be reused by other function calls
     *
     * @param node
     * @return
     * @throws HmsException
     */
    private HostProxy getHostProxy( ServiceServerNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null )
        {
            ServiceServerNode node = serviceHmsNode;
            try
            {
                HostProxy hostProxy = HostProxyProvider.getInstance().getHostProxy( node );
                return hostProxy;
            }
            catch ( Exception e )
            {
                throw new HmsException( "Cannot create HostProxy object for: " + node.getNodeID(), e );
            }
        }
        else
        {
            logger.error( SERVER_NODE_NULL_ERR + serviceHmsNode );
            throw new HmsException( SERVER_NODE_NULL_ERR + serviceHmsNode );
        }
    }

    @Override
    public void init( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceHmsNode )
        {
            final ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            ExecutorService service = Executors.newFixedThreadPool( 1 );
            ;
            service.execute( new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        getHostProxy( node );
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Unable to initialize hostProxy for node: " + node.getNodeID(), e );
                    }
                }
            } );
            // Make sure Executor will NOT take any new tasks further and will close itself once tasks are completed
            service.shutdown();
        }
        else
        {
            logger.error( SERVER_NODE_NULL_ERR + serviceHmsNode );
            throw new HmsException( SERVER_NODE_NULL_ERR + serviceHmsNode );
        }
    }

    @Override
    public HostNameInfo getHostName( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            HostNameInfo hostNameInfo = new HostNameInfo();
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            try
            {
                hostNameInfo = ESXIInfoHelper.getEsxiHostNameInfo( node );
            }
            catch ( Exception e )
            {
                String err = "Cannot get Esxi HostName Info for Node:" + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return hostNameInfo;
        }
        else
        {
            logger.error( SERVER_NODE_NULL_ERR + serviceHmsNode );
            throw new HmsException( SERVER_NODE_NULL_ERR + serviceHmsNode );
        }
    }

    /**
     * Get Storage controller information
     *
     * @param serviceHmsNode
     * @return List<StorageControllerInfo>
     * @throws HmsException
     */
    @Override
    public List<StorageControllerInfo> getStorageControllerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            List<StorageControllerInfo> storageControllerInfoList = null;
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            try
            {
                HostProxy hostProxy = getHostProxy( node );
                storageControllerInfoList = getStorageControllerInfo( node, hostProxy );
            }
            catch ( Exception e )
            {
                String err = "Cannot get Storage Controller Info for Node:" + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return storageControllerInfoList;
        }
        else
        {
            logger.error( SERVER_NODE_NULL_ERR + serviceHmsNode );
            throw new HmsException( SERVER_NODE_NULL_ERR + serviceHmsNode );
        }
    }

    /**
     * Overridden function to get Storage Controller Info while reusing the HostProxy object
     *
     * @param serviceHmsNode
     * @param hostProxy
     * @return List<StorageControllerInfo>
     * @throws HmsException
     */
    public List<StorageControllerInfo> getStorageControllerInfo( ServiceHmsNode serviceHmsNode, HostProxy hostProxy )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode && hostProxy != null )
        {
            List<StorageControllerInfo> storageControllerInfoList = null;
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            try
            {
                HostSystem hostSystem = hostProxy.getHostSystem();
                VsphereClient client = hostProxy.getVsphereClient();
                if ( hostSystem != null )
                {
                    storageControllerInfoList =
                        StorageControllerInfoHelper.getStorageControllerInfo( hostSystem, client, hostProxy, node );
                }
            }
            catch ( Exception e )
            {
                String err = "Cannot get Storage Controller info for Node:" + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return storageControllerInfoList;
        }
        else
        {
            logger.error( "Node is NOT Server Node or HostProxy is NULL." + "HostProxy: " + hostProxy + "Node: "
                + serviceHmsNode );
            throw new HmsException( "Node is NOT Server Node or HostProxy is NULL." + "HostProxy: " + hostProxy
                + "Node: " + serviceHmsNode );
        }
    }
}
