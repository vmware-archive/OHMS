/*
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.vmware.vrack.hms.plugin.boardservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.boardvendorservice.api.BoardServiceImplementation;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.OperationNotSupportedOOBException;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.PowerOperationAction;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.servernodes.api.fan.FanInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;
import com.vmware.vrack.hms.plugin.boardservice.redfish.client.RedfishActionInvoker;
import com.vmware.vrack.hms.plugin.boardservice.redfish.client.RedfishClientException;
import com.vmware.vrack.hms.plugin.boardservice.redfish.discovery.InventoryTraverser;
import com.vmware.vrack.hms.plugin.boardservice.redfish.discovery.RedfishResourcesInventory;
import com.vmware.vrack.hms.plugin.boardservice.redfish.discovery.RedfishResourcesInventoryException;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.BootOptionsMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.ComputerSystemMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.EthernetInterfaceMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.ManagerMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.MappingException;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.MemoryMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.ProcessorMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.ResetTypeMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.StorageControllerMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.StorageDeviceMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource.Actions.ResetType;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource.Boot;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.EthernetInterfaceResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ManagerResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.MemoryResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ProcessorResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.RedfishResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.SimpleStorageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.vmware.vrack.hms.plugin.ServerPluginConstants.BOARD_MANUFACTURER;
import static com.vmware.vrack.hms.plugin.ServerPluginConstants.BOARD_NAME;
import static java.util.Arrays.asList;

@BoardServiceImplementation( name = BOARD_NAME )
public class Redfish_serverPlugin
    implements IBoardService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Redfish_serverPlugin.class );

    private static final String redfishServicesConfigurationFile = "config/redfish-services.json";

    private static final List<HmsApi> SUPPORTED_HMS_API = asList(
        HmsApi.CPU_INFO,
        HmsApi.NIC_INFO,
        HmsApi.SERVER_POWER_STATUS,
        HmsApi.SERVER_POWER_OPERATIONS,
        HmsApi.BOOT_OPTIONS,
        HmsApi.SET_BOOT_OPTIONS,
        HmsApi.SERVER_INFO,
        HmsApi.STORAGE_CONTROLLER_INFO,
        HmsApi.STORAGE_INFO,
        HmsApi.MEMORY_INFO,
        HmsApi.SUPPORTED_HMS_API
    );

    private static Map<UUID, URI> computerSystemResources = new ConcurrentHashMap<>();

    private final List<URI> redfishServices;

    private List<BoardInfo> supportedBoards = new ArrayList<>();

    public Redfish_serverPlugin()
    {
        BoardInfo boardInfo = new BoardInfo();
        boardInfo.setBoardManufacturer( BOARD_MANUFACTURER );
        boardInfo.setBoardProductName( BOARD_NAME );
        supportedBoards.add( boardInfo );
        redfishServices = readRedfishServices( redfishServicesConfigurationFile );
    }

    List<URI> readRedfishServices( String redfishServicesConfigurationFilePath )
    {
        File redfishServicesFile = new File( redfishServicesConfigurationFilePath );

        if ( !redfishServicesFile.exists() )
        {
            LOGGER.warn( "{} configuration file does not exist", redfishServicesConfigurationFilePath );
            return Collections.emptyList();
        }

        ObjectMapper mapper = new ObjectMapper();
        try
        {
            RedfishServices redfishServices =
                mapper.readValue( redfishServicesFile, RedfishServices.class );
            return redfishServices.getServices();
        }
        catch ( IOException e )
        {
            LOGGER.warn( "Could not read configuration file: {}, cause: ", redfishServicesConfigurationFilePath,
                         e.getMessage() );
            return Collections.emptyList();
        }
    }

    @Override
    public List<BoardInfo> getSupportedBoard()
    {
        return supportedBoards;
    }

    @Override
    public boolean getServerPowerStatus( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        ComputerSystemResource system = getComputerSystem( serviceHmsNode );
        ComputerSystemResource.PowerState powerState = system.getPowerState();
        return ComputerSystemResource.PowerState.On.equals( powerState );
    }

    private ComputerSystemResource getComputerSystem( ServiceHmsNode node )
        throws HmsException
    {
        UUID uuid = UUID.fromString( node.getNodeID() );
        if ( computerSystemResources.get( uuid ) != null )
        {
            URI uri = computerSystemResources.get( uuid );
            return fetchComputerSystemResource( uri );
        }

        try
        {
            ComputerSystemResource computerSystemResource = findComputerSystemResource( uuid );
            computerSystemResources.put( uuid, computerSystemResource.getOrigin() );
            return computerSystemResource;
        }
        catch ( HmsException | IllegalArgumentException e )
        {
            throw new HmsException( "Requested ComputerSystem: " + node.getNodeID() + " is not available", e );
        }
    }

    private ComputerSystemResource fetchComputerSystemResource( URI uri )
        throws HmsException
    {
        try (RedfishResourcesInventory inventory = createRedfishResourcesInventory())
        {
            RedfishResource resource = inventory.getResourceByURI( uri );
            if ( resource instanceof ComputerSystemResource )
            {
                return (ComputerSystemResource) resource;
            }
            throw new HmsException( "Resource at uri " + uri + " is not a ComputerSystemResource" );
        }
        catch ( RedfishResourcesInventoryException e )
        {
            throw new HmsException( "Could not fetch ComputerSystem from uri:" + uri, e );
        }
    }

    private ComputerSystemResource findComputerSystemResource( UUID uuid )
        throws HmsException
    {
        try (RedfishResourcesInventory inventory = createRedfishResourcesInventory())
        {
            for ( URI redfishService : redfishServices )
            {
                Set<ComputerSystemResource> computerSystemResources =
                    inventory.getResourcesByClass( redfishService, ComputerSystemResource.class );
                for ( ComputerSystemResource computerSystemResource : computerSystemResources )
                {
                    if ( Objects.equals( computerSystemResource.getUuid(), uuid ) )
                    {
                        return computerSystemResource;
                    }
                }
            }
        }
        throw new HmsException( "Could not find ComputerSystemResource with UUID: " + uuid );
    }

    @Override
    public boolean powerOperations( ServiceHmsNode serviceHmsNode, PowerOperationAction powerOperationAction )
        throws HmsException
    {
        LOGGER.trace( "Setting Power Status for {} to {}", serviceHmsNode.getNodeID(),
                      powerOperationAction.getPowerActionString() );

        ComputerSystemResource system = getComputerSystem( serviceHmsNode );
        ResetTypeMapper mapper = new ResetTypeMapper();
        ResetType resetType = mapper.map( powerOperationAction );

        try (RedfishActionInvoker redfishActionInvoker = createRedfishActionInvoker())
        {
            redfishActionInvoker.invokeResetAction( system, resetType );
            return true;
        }
        catch ( RedfishClientException e )
        {
            throw new HmsException( "Setting Power Status for " + serviceHmsNode.getNodeID()
                                        + " to " + powerOperationAction.getPowerActionString() + " failed" );
        }
    }

    protected RedfishActionInvoker createRedfishActionInvoker()
    {
        return new RedfishActionInvoker();
    }

    @Override
    public String getManagementMacAddress( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        LOGGER.trace( "Getting Management MAC for {}", serviceHmsNode.getNodeID() );

        try (RedfishResourcesInventory inventory = createRedfishResourcesInventory())
        {
            ComputerSystemResource system = getComputerSystem( serviceHmsNode );
            InventoryTraverser traverser = new InventoryTraverser( inventory );
            ManagerMapper managerMapper = new ManagerMapper();

            ManagerResource managementController =
                managerMapper.getManagementController( traverser.getManagers( system ) );
            List<EthernetInterfaceResource> nics = traverser.getEthernetInterfaces( managementController );

            EthernetInterfaceMapper nicMapper = new EthernetInterfaceMapper();

            return nicMapper.mapManagementMacAddress( nics );
        }
        catch ( MappingException e )
        {
            throw new HmsException( "Unable to obtain management MAC Address", e );
        }
        catch ( RedfishResourcesInventoryException e )
        {
            throw new HmsException( "Traversing Redfish Services Inventory failed", e );
        }
    }

    @Override
    public List<BmcUser> getManagementUsers( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        LOGGER.trace( "Getting Management Users for {}", serviceHmsNode.getNodeID() );
        throw new OperationNotSupportedOOBException( "Operation not supported" );
    }

    @Override
    public SelfTestResults runSelfTest( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        LOGGER.trace( "Running Self Test for {}", serviceHmsNode.getNodeID() );
        throw new OperationNotSupportedOOBException( "Operation not supported" );
    }

    @Override
    public AcpiPowerState getAcpiPowerState( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        LOGGER.trace( "Getting ACPI Power State for {}", serviceHmsNode.getNodeID() );
        throw new OperationNotSupportedOOBException( "Operation not supported" );
    }

    @Override
    public List<CPUInfo> getCpuInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        LOGGER.trace( "Getting CPU info for {}", serviceHmsNode.getNodeID() );

        try (RedfishResourcesInventory inventory = createRedfishResourcesInventory())
        {
            InventoryTraverser traverser = new InventoryTraverser( inventory );
            ComputerSystemResource computerSystem = getComputerSystem( serviceHmsNode );
            ProcessorMapper processorMapper = new ProcessorMapper();
            try
            {
                List<ProcessorResource> processors = traverser.getProcessors( computerSystem );
                List<CPUInfo> cpuInfoList = new ArrayList<>( processors.size() );
                for ( ProcessorResource processor : processors )
                {
                    cpuInfoList.add( processorMapper.map( processor ) );
                }
                return cpuInfoList;
            }
            catch ( RedfishResourcesInventoryException e )
            {
                throw new HmsException( "Unable to get CPUInfo for ComputerSystem " + serviceHmsNode.getNodeID()
                                            + " is not available", e );
            }
        }
    }

    @Override
    public List<FanInfo> getFanInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        LOGGER.trace( "Getting Fan info for {}", serviceHmsNode.getNodeID() );
        throw new OperationNotSupportedOOBException( "Operation not supported" );
    }

    @Override
    public List<EthernetController> getEthernetControllersInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        LOGGER.trace( "Getting Ethernet Controller info for {}", serviceHmsNode.getNodeID() );

        try (RedfishResourcesInventory inventory = createRedfishResourcesInventory())
        {

            InventoryTraverser traverser = new InventoryTraverser( inventory );
            ComputerSystemResource computerSystem = getComputerSystem( serviceHmsNode );
            EthernetInterfaceMapper nicMapper = new EthernetInterfaceMapper();
            try
            {
                List<EthernetInterfaceResource> ethernetInterfaces = traverser.getEthernetInterfaces( computerSystem );
                List<EthernetController> nics = new ArrayList<>( ethernetInterfaces.size() );
                for ( EthernetInterfaceResource ethernetInterface : ethernetInterfaces )
                {
                    nics.add( nicMapper.mapEthernetController( ethernetInterface ) );
                }
                return nics;
            }
            catch ( RedfishResourcesInventoryException e )
            {
                throw new HmsException( "Unable to get NICs for ComputerSystem: " + serviceHmsNode.getNodeID()
                                            + " is not available", e );
            }
        }
    }

    @Override
    public SystemBootOptions getBootOptions( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        LOGGER.trace( "Getting Boot options for {}", serviceHmsNode.getNodeID() );

        ComputerSystemMapper mapper = new ComputerSystemMapper();
        ComputerSystemResource computerSystem = getComputerSystem( serviceHmsNode );

        return mapper.mapBootOptions( computerSystem );
    }

    @Override
    public boolean setBootOptions( ServiceHmsNode serviceHmsNode, SystemBootOptions data )
        throws HmsException
    {
        final String dataString = "SystemBootOptions{" +
            "bootFlagsValid=" + data.getBootFlagsValid() +
            ", bootOptionsValidity=" + data.getBootOptionsValidity() +
            ", biosBootType=" + data.getBiosBootType() +
            ", bootDeviceType=" + data.getBootDeviceType() +
            ", bootDeviceSelector=" + data.getBootDeviceSelector() +
            ", bootDeviceInstanceNumber=" + data.getBootDeviceInstanceNumber() +
            '}';

        LOGGER.trace( "Setting Boot Options for {} using {}", serviceHmsNode.getNodeID(), dataString );

        if ( data.getBootFlagsValid() != null ||
            data.getBiosBootType() != null ||
            data.getBootDeviceInstanceNumber() != null ||
            data.getBootDeviceType() != null )
        {
            throw new OperationNotSupportedOOBException( "Operation not supported" );
        }

        BootOptionsMapper mapper = new BootOptionsMapper();
        Boot bootOptions = mapper.map( data );
        ComputerSystemResource computerSystem = getComputerSystem( serviceHmsNode );
        try (RedfishActionInvoker actionInvoker = createRedfishActionInvoker())
        {
            actionInvoker.invokeSetBootOptionsAction( computerSystem, bootOptions );
            return true;
        }
        catch ( RedfishClientException e )
        {
            throw new HmsException( "Setting Boot Options for " + serviceHmsNode.getNodeID()
                                        + " to " + bootOptions + " failed" );
        }
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        LOGGER.trace( "Getting Server Node info for {}", serviceHmsNode.getNodeID() );

        ComputerSystemMapper mapper = new ComputerSystemMapper();
        ComputerSystemResource computerSystem = getComputerSystem( serviceHmsNode );
        ServerNodeInfo nodeInfo = mapper.mapNodeInfo( computerSystem );

        // TODO if this is utilized in matching resources in north layers, this ID should be set
        // nodeInfo.setId( serviceHmsNode.getNodeID() );

        return nodeInfo;
    }

    @Override
    public List<HddInfo> getHddInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        LOGGER.trace( "Getting Hdd info for {}", serviceHmsNode.getNodeID() );

        try (RedfishResourcesInventory inventory = createRedfishResourcesInventory())
        {
            InventoryTraverser traverser = new InventoryTraverser( inventory );
            ComputerSystemResource computerSystem = getComputerSystem( serviceHmsNode );
            StorageDeviceMapper storageDeviceMapper = new StorageDeviceMapper();
            try
            {
                List<SimpleStorageResource> simpleStorages = traverser.getSimpleStorages( computerSystem );
                List<HddInfo> hddInfoList = new ArrayList<>( simpleStorages.size() );
                for ( SimpleStorageResource simpleStorage : simpleStorages )
                {
                    for ( SimpleStorageResource.Device device : simpleStorage.getDevices() )
                    {
                        hddInfoList.add( storageDeviceMapper.mapDevice( device ) );
                    }
                }
                return hddInfoList;
            }
            catch ( RedfishResourcesInventoryException e )
            {
                throw new HmsException( "Unable to get HddInfo for ComputerSystem " + serviceHmsNode.getNodeID()
                                            + " is not available", e );
            }
        }
    }

    protected RedfishResourcesInventory createRedfishResourcesInventory()
    {
        return new RedfishResourcesInventory();
    }

    @Override
    public List<StorageControllerInfo> getStorageControllerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        LOGGER.trace( "Getting StorageController info for {}", serviceHmsNode.getNodeID() );

        try (RedfishResourcesInventory inventory = createRedfishResourcesInventory())
        {

            InventoryTraverser traverser = new InventoryTraverser( inventory );
            ComputerSystemResource computerSystem = getComputerSystem( serviceHmsNode );
            StorageControllerMapper storageControllerMapper = new StorageControllerMapper();
            try
            {
                List<SimpleStorageResource> simpleStorages = traverser.getSimpleStorages( computerSystem );
                List<StorageControllerInfo> simpleStorageList = new ArrayList<>( simpleStorages.size() );
                for ( SimpleStorageResource simpleStorage : simpleStorages )
                {
                    simpleStorageList.add( storageControllerMapper.map( simpleStorage ) );
                }
                return simpleStorageList;
            }
            catch ( RedfishResourcesInventoryException e )
            {
                throw new HmsException(
                    "Unable to get StorageControllerInfo for ComputerSystem " + serviceHmsNode.getNodeID()
                        + " is not available", e );
            }
        }
    }

    @Override
    public boolean setChassisIdentification( ServiceHmsNode serviceHmsNode, ChassisIdentifyOptions data )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "SetChassisIdentification operation is not supported" );
    }

    @Override
    public boolean setManagementIPAddress( ServiceHmsNode serviceHmsNode, String ipAddress )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "SetManagementIPAddress operation is not supported" );
    }

    @Override
    public boolean setBmcPassword( ServiceHmsNode serviceHmsNode, String username, String newPassword )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "SetBmcPassword operation is not supported" );
    }

    @Override
    public boolean createManagementUser( ServiceHmsNode serviceHmsNode, BmcUser bmcUser )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "CreateManagementUser operation is not supported" );
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode, Integer recordCount, SelFetchDirection direction )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "GetSelDetails operation is not supported" );
    }

    @Override
    public boolean isHostManageable( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        ComputerSystemResource computerSystem = getComputerSystem( serviceHmsNode );
        return !computerSystem.getManagedBy().isEmpty();
    }

    @Override
    public List<PhysicalMemory> getPhysicalMemoryInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        LOGGER.trace( "Getting PhysicalMemory info for {}", serviceHmsNode.getNodeID() );

        try (RedfishResourcesInventory inventory = createRedfishResourcesInventory())
        {

            InventoryTraverser traverser = new InventoryTraverser( inventory );
            ComputerSystemResource computerSystem = getComputerSystem( serviceHmsNode );
            MemoryMapper memoryMapper = new MemoryMapper();
            try
            {
                List<MemoryResource> memoryList = traverser.getMemory( computerSystem );
                List<PhysicalMemory> physicalMemoryList = new ArrayList<>( memoryList.size() );
                for ( MemoryResource memory : memoryList )
                {
                    physicalMemoryList.add( memoryMapper.map( memory ) );
                }
                return physicalMemoryList;
            }
            catch ( RedfishResourcesInventoryException e )
            {
                throw new HmsException(
                    "Unable to get PhysicalMemoryInfo for ComputerSystem " + serviceHmsNode.getNodeID()
                        + " is not available", e );
            }
        }
    }

    @Override
    public List<ServerComponentEvent> getComponentEventList( ServiceHmsNode serviceNode, ServerComponent component )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "GetComponentEventList operation is not supported" );
    }

    @Override
    public List<HmsApi> getSupportedHmsApi( ServiceHmsNode serviceNode )
        throws HmsException
    {
        return SUPPORTED_HMS_API;
    }

    private static class RedfishServices
    {
        @JsonProperty( "services" )
        private List<URI> services;

        List<URI> getServices()
        {
            return services;
        }
    }
}
