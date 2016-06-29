package com.vmware.vrack.hms.plugin.boardservice;

import com.vmware.vrack.hms.common.boardvendorservice.api.BoardServiceImplementation;
import com.vmware.vrack.hms.common.boardvendorservice.api.IRedfishService;
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
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.DimmConfigMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.EthernetInterfaceMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.ManagerMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.MappingException;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.ProcessorMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.ResetTypeMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.StorageControllerMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.mappers.StorageDeviceMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource.Actions.ResetType;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource.Boot;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.DimmConfigResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.EthernetInterfaceResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ManagerResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.OdataId;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ProcessorResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.SimpleStorageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.vmware.vrack.hms.plugin.ServerPluginConstants.BOARD_MANUFACTURER;
import static com.vmware.vrack.hms.plugin.ServerPluginConstants.BOARD_NAME;
import static com.vmware.vrack.hms.plugin.boardservice.redfish.discovery.IdentificationHelper.decodeResourceUniqueId;
import static com.vmware.vrack.hms.plugin.boardservice.redfish.discovery.IdentificationHelper.getUniqueIdForResource;
import static java.util.Arrays.asList;

@BoardServiceImplementation( name = BOARD_NAME )
public class Redfish_serverPlugin
    implements IRedfishService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Redfish_serverPlugin.class );

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

    private List<BoardInfo> supportedBoards = new ArrayList<>();

    public Redfish_serverPlugin()
    {
        BoardInfo boardInfo = new BoardInfo();
        boardInfo.setBoardManufacturer( BOARD_MANUFACTURER );
        boardInfo.setBoardProductName( BOARD_NAME );
        supportedBoards.add( boardInfo );
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
        try (RedfishResourcesInventory inventory = createRedfishResourcesInventory())
        {
            OdataId odataId = decodeResourceUniqueId( node.getNodeID() );
            return inventory.getResourceByURI( odataId.toUri() );
        }
        catch ( RedfishResourcesInventoryException e )
        {
            throw new HmsException( "Requested ComputerSystem {} " + node.getNodeID() + " is not available", e );
        }
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
        SystemBootOptions bootOptions = mapper.mapBootOptions( computerSystem );

        return bootOptions;
    }

    @Override
    public boolean setBootOptions( ServiceHmsNode serviceHmsNode, SystemBootOptions data )
        throws HmsException
    {
        final String dataString = new StringBuilder( "SystemBootOptions{" )
            .append( "bootFlagsValid=" ).append( data.getBootFlagsValid() )
            .append( ", bootOptionsValidity=" ).append( data.getBootOptionsValidity() )
            .append( ", biosBootType=" ).append( data.getBiosBootType() )
            .append( ", bootDeviceType=" ).append( data.getBootDeviceType() )
            .append( ", bootDeviceSelector=" ).append( data.getBootDeviceSelector() )
            .append( ", bootDeviceInstanceNumber=" ).append( data.getBootDeviceInstanceNumber() )
            .append( '}' ).toString();

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
            DimmConfigMapper dimmConfigMapper = new DimmConfigMapper();
            try
            {
                List<DimmConfigResource> dimmConfigs = traverser.getDimmConfig( computerSystem );
                List<PhysicalMemory> physicalMemoryList = new ArrayList<>( dimmConfigs.size() );
                for ( DimmConfigResource dimmConfig : dimmConfigs )
                {
                    physicalMemoryList.add( dimmConfigMapper.map( dimmConfig ) );
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

    /**
     * Method used for initial discovery of Computer Systems that maps to HMS Server Node
     *
     * @param serviceEndpoint
     * @return
     * @throws HmsException
     */
    @Override
    public List<ServiceHmsNode> getNodesForComputerSystems( URI serviceEndpoint )
        throws HmsException
    {
        LOGGER.trace( "Getting Nodes for ComputerSystems from service {}", serviceEndpoint );

        try (RedfishResourcesInventory inventory = createRedfishResourcesInventory())
        {
            Set<ComputerSystemResource> systems =
                inventory.getResourcesByClass( serviceEndpoint, ComputerSystemResource.class );

            List<ServiceHmsNode> systemsDiscovered = new ArrayList<>();
            for ( ComputerSystemResource systemResource : systems )
            {
                try
                {
                    ServiceHmsNode node = new ServiceHmsNode();
                    node.setNodeID( getUniqueIdForResource( serviceEndpoint, systemResource.getOdataId() ) );
                    node.setUuid( systemResource.getUuid() == null ? null : systemResource.getUuid().toString() );
                    systemsDiscovered.add( node );
                }
                catch ( RedfishResourcesInventoryException e )
                {
                    LOGGER.error( "Could not discover ComputerSystem " + systemResource.getOdataId(), e );
                }
            }
            return systemsDiscovered;
        }
    }
}
