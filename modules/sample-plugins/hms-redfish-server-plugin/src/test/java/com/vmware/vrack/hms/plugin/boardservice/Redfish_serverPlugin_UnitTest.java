package com.vmware.vrack.hms.plugin.boardservice;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.OperationNotSupportedOOBException;
import com.vmware.vrack.hms.common.resource.PowerOperationAction;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceSelector;
import com.vmware.vrack.hms.common.resource.chassis.BootOptionsValidity;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.plugin.boardservice.redfish.client.IRedfishWebClient;
import com.vmware.vrack.hms.plugin.boardservice.redfish.client.RedfishActionInvoker;
import com.vmware.vrack.hms.plugin.boardservice.redfish.discovery.RedfishResourcesInventory;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Redfish_serverPlugin_UnitTest
{
    private static final URI SERVICE_ENDPOINT = URI.create( "/redfish/v1" );

    private static Logger logger = Logger.getLogger( Redfish_serverPlugin_UnitTest.class );

    Redfish_serverPlugin plugin = new Redfish_serverPlugin()
    {
        @Override
        protected RedfishActionInvoker createRedfishActionInvoker()
        {
            return new RedfishActionInvoker()
            {
                @Override
                protected IRedfishWebClient createRedfishClient()
                {
                    return new MockRedfishClient( this.getClass().getResourceAsStream( "/mock.json" ) );
                }
            };
        }

        @Override
        protected RedfishResourcesInventory createRedfishResourcesInventory()
        {
            return new RedfishResourcesInventory()
            {
                @Override
                protected IRedfishWebClient createRedfishClient()
                {
                    return new MockRedfishClient( this.getClass().getResourceAsStream( "/mock.json" ) );
                }
            };
        }
    };

    ServiceServerNode node;

    /**
     * Function to be executed before start of each test method
     */
    @Before
    public void init()
        throws IOException, HmsException
    {
        List<ServiceHmsNode> nodes = plugin.getNodesForComputerSystems( SERVICE_ENDPOINT );

        ServiceHmsNode hmsNode = nodes.iterator().next();
        node = new ServiceServerNode();
        node.setNodeID( hmsNode.getNodeID() );
    }

    /**
     * Check if the host is reachable or not; If yes, returns true
     */
    @Test
    public void testIsHostManageable()
        throws HmsException
    {
        logger.info( "Test Board Service isHostAvailable" );
        boolean result = plugin.isHostManageable( node );
        assertTrue( "result should be true as the host is manageable!", result );
    }

    /**
     * Get the power status of the board
     */
    @Test
    public void testGetServerPowerStatus()
        throws HmsException
    {

        boolean result = plugin.getServerPowerStatus( node );
        assertTrue( "Expected Server Power Status result: true, actual result:" + result, result );
    }

    /**
     * Get Power Operations (power up/power down/power cycle/hard reset/cold reset)status
     */
    @Test
    public void testPowerOperations()
        throws HmsException
    {
        logger.info( "Test Board Service powerOperations" );
        boolean result = plugin.powerOperations( node, PowerOperationAction.POWERUP );
        assertTrue( "Expected Server Power Operations Status result: true, actual result:" + result, result );
    }

    /**
     * Get MAC address of BMC
     */
    @Test
    public void testGetManagementMacAddress()
        throws HmsException
    {
        logger.info( "Test Board Service getManagementMacAddress" );
        String macAddress = plugin.getManagementMacAddress( node );
        assertTrue( "Expected MAC Address: xx.xx.xx.xx, actual result:" + macAddress, macAddress != null );
    }

    /**
     * Get management users of BMC
     */
    @Test( expected = OperationNotSupportedOOBException.class )
    public void testGetManagementUsers()
        throws HmsException
    {
        logger.info( "Test Board Service getManagementUsers" );
        plugin.getManagementUsers( node );
    }

    /**
     * BMC to do a Self Test
     */
    @Test( expected = OperationNotSupportedOOBException.class )
    public void testRunSelfTest()
        throws HmsException
    {
        logger.info( "Test Board Service runSelfTest" );
        plugin.runSelfTest( node );
    }

    /**
     * Get ACPI Power state of the board
     */
    @Test( expected = OperationNotSupportedOOBException.class )
    public void testGetAcpiPowerState()
        throws HmsException
    {
        logger.info( "Test Board Service getAcpiPowerState" );
        plugin.getAcpiPowerState( node );
    }

    /**
     * Get already set Boot Options
     */
    @Test
    public void testGetBootOptions()
        throws HmsException
    {
        logger.info( "Test Board Service getBootOptions" );
        SystemBootOptions bootOptions = plugin.getBootOptions( node );

        assertNotNull( "bootOptions cannot be null!", bootOptions );
        assertNotNull( "bootOptions bootFlagsValid cannot be null!", bootOptions.getBootFlagsValid() );
        assertNotNull( "bootOptions bootDeviceSelector cannot be null!", bootOptions.getBootDeviceSelector() );
        assertNotNull( "bootOptions biosBootType cannot be null!", bootOptions.getBiosBootType() );
        assertNotNull( "bootOptions bootOptionsValidity cannot be null!", bootOptions.getBootOptionsValidity() );
    }

    /**
     * Set Boot Options
     */
    @Test
    public void testSetBootOptions()
        throws HmsException
    {
        logger.info( "Test Board Service setBootOptions" );
        SystemBootOptions sysBootOptions = new SystemBootOptions();
        sysBootOptions.setBootOptionsValidity( BootOptionsValidity.Persistent );
        sysBootOptions.setBootDeviceSelector( BootDeviceSelector.PXE );
        boolean status = plugin.setBootOptions( node, sysBootOptions );
        assertTrue( "Expected setBootOptions result is true, actual result:" + status, status );
    }

    /**
     * Get server Info of the board (board product name, vendor name etc...)
     **/
    @Test
    public void testGetServerInfo()
        throws HmsException
    {
        logger.info( "Test Board Service getServerInfo" );
        ServerNodeInfo nodeInfo = plugin.getServerInfo( node );
        assertNotNull( "nodeInfo cannot be null!", nodeInfo );
        assertNotNull( "nodeInfo boardProductName cannot be null!", nodeInfo.getComponentIdentifier().getProduct() );
        assertNotNull( "nodeInfo boardVendor cannot be null!", nodeInfo.getComponentIdentifier().getManufacturer() );
    }

    /**
     * Perform Chassis identification (Blinking lights)
     */
    @Test( expected = OperationNotSupportedOOBException.class )
    public void testSetChassisIdentification()
        throws HmsException
    {
        logger.info( "Test Board Service setChassisIdentification" );
        ChassisIdentifyOptions data = new ChassisIdentifyOptions();

        plugin.setChassisIdentification( node, data );
    }

    /**
     * Get System Event Log Information Only. Gives idea about total entries count, last addition time, last erase time.
     */
    @Test( expected = OperationNotSupportedOOBException.class )
    public void testGetSelInfo()
        throws HmsException
    {
        logger.info( "Test Board Service getSelInfo" );
        plugin.getSelDetails( node, null, null );
    }

    /**
     * Get System Event Log details
     */
    @Test( expected = OperationNotSupportedOOBException.class )
    public void testGetSelDetails()
        throws HmsException
    {
        logger.info( "Test Board Service getSelDetails" );
        plugin.getSelDetails( node, null, null );
    }

    /**
     * Test BoardService getComponentSensorList
     */
    @Test( expected = OperationNotSupportedOOBException.class )
    public void testGetComponentEventList()
        throws HmsException
    {
        logger.info( "Test Board Service getComponentEventList" );
        plugin.getComponentEventList( node, ServerComponent.CPU );
    }

    /**
     * Test BoardService getSupportedBoardInfos
     */
    @Test
    public void testGetSupportedBoardInfos()
    {
        logger.info( "Test Board Service getSupportedBoardInfos" );
        List<BoardInfo> supportedBoards;
        supportedBoards = plugin.getSupportedBoard();
        assertNotNull( supportedBoards );
        for ( int i = 0; i < supportedBoards.size(); i++ )
        {
            assertNotNull( "supportedBoards BoardProductName cannot be null!",
                           supportedBoards.get( i ).getBoardProductName() );
            assertNotNull( "supportedBoards BoardManufacturer cannot be null!",
                           supportedBoards.get( i ).getBoardManufacturer() );
        }
    }

    @Test
    public void testGetSupportBoard()
    {
        logger.info( "Test Board Service getSupportedBoard" );
        List<BoardInfo> boardInfoList = plugin.getSupportedBoard();
        for ( int i = 0; i < boardInfoList.size(); i++ )
        {
            assertNotNull( "Expected Board Name is NOT NULL, actual result:"
                               + boardInfoList.get( i ).getBoardProductName() );
            assertNotNull( "Expected Board Name is NOT NULL, actual result:"
                               + boardInfoList.get( i ).getBoardProductName() );
        }
        assertNotNull( "boardInfoList cannot be null!", boardInfoList );
        assertTrue( "boardInfoList cannot be empty, there must be at least 1 Board!", boardInfoList.size() > 0 );
    }

    @Test
    public void testGetNodesForComputerSystems()
        throws HmsException
    {
        logger.info( "Test Redfish Service getNodesForComputerSystems" );
        List<ServiceHmsNode> computerSystems = plugin.getNodesForComputerSystems( SERVICE_ENDPOINT );

        return;
    }
}
