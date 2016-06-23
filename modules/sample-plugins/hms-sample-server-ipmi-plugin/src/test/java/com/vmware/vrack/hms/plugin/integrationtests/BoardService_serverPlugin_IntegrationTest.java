/* ********************************************************************************
 * BoardService_serverPlugin_IntegrationTest.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.plugin.integrationtests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.PowerOperationAction;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.BiosBootType;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceSelector;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceType;
import com.vmware.vrack.hms.common.resource.chassis.BootOptionsValidity;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.plugin.ServerPluginConstants;
import com.vmware.vrack.hms.plugin.boardservice.BoardService_serverPlugin;
import com.vmware.vrack.hms.plugin.testlib.IntegrationTest;
import com.vmware.vrack.hms.plugin.testlib.Repeat;
import com.vmware.vrack.hms.plugin.testlib.RunnerFactory;

/**
 * Integration tests for hms-sample-server-plugin
 *
 * @author VMware Inc.
 */
@Category( IntegrationTest.class )
@RunWith( Parameterized.class )
@Parameterized.UseParametersRunnerFactory( RunnerFactory.class )
public class BoardService_serverPlugin_IntegrationTest
{
    /* Stop a test if they are running for more than 6 minutes */
    private static final long TEST_TIMEOUT = 4 * 60 * 1000;

    /* Repeat Test parameters */
    private static final int RETRY_COUNT = 4;

    private static final long SLEEP_TIME_BEFORE_RETRY = 3000;

    private static final long TIMEOUT = 1000;

    BoardService_serverPlugin bsServerPlugin = new BoardService_serverPlugin();

    static ServiceServerNode node;

    private static Logger logger = Logger.getLogger( BoardService_serverPlugin_IntegrationTest.class );

    private static Boolean isBMCReachable;

    /* Extracting JSON object from test configuration file */
    @JsonInclude( value = Include.NON_NULL )
    @JsonPropertyOrder( { "ipAddress", "username", "password" } )
    static class ServerAccess
    {
        String ipAddress;

        String username;

        String password;

        public String getIpAddress()
        {
            return ipAddress;
        }

        public void setIpAddress( String ipAddress )
        {
            this.ipAddress = ipAddress;
        }

        public String getUsername()
        {
            return username;
        }

        public void setUsername( String username )
        {
            this.username = username;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword( String password )
        {
            this.password = password;
        }
    }

    @JsonInclude( value = Include.NON_NULL )
    @JsonPropertyOrder( { "countTestServers", "servers" } )
    static class TestConfiguration
    {
        Integer countTestServers;

        List<ServerAccess> servers;

        public Integer getCountTestServers()
        {
            return countTestServers;
        }

        public void setCountTestServers( Integer countTestServers )
        {
            this.countTestServers = countTestServers;
        }

        public List<ServerAccess> getServers()
        {
            return servers;
        }

        public void setServers( List<ServerAccess> servers )
        {
            this.servers = servers;
        }
    }

    @Parameter
    public String nodeIpAddress;

    @Parameter( value = 1 )
    public String nodeUserName;

    @Parameter( value = 2 )
    public String nodePassword;

    @Parameters
    public static Iterable<Object[]> data1()
    {
        int countOfServers = 1;
        Object obj[][];
        String filename = "/test.json";
        ObjectMapper objectMapper = new ObjectMapper();
        TestConfiguration testConfig = new TestConfiguration();
        int i;
        try
        {
            testConfig =
                objectMapper.readValue( new InputStreamReader( new BoardService_serverPlugin_IntegrationTest().getClass().getResourceAsStream( filename ) ),
                                        TestConfiguration.class );
            countOfServers = testConfig.countTestServers;
            obj = new Object[countOfServers][3];
            for ( i = 0; i < countOfServers; i++ )
            {
                obj[i][0] = testConfig.servers.get( i ).ipAddress;
                obj[i][1] = testConfig.servers.get( i ).username;
                obj[i][2] = testConfig.servers.get( i ).password;
            }
            return Arrays.asList( obj );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function executed once at the initialization, before tests are run
     */
    @BeforeClass
    public static void startTests()
    {
        logger.info( ServerPluginConstants.BOARD_NAME + " integration test started" );
    }

    /**
     * Function executed before exiting the class.
     */
    @AfterClass
    public static void cleanup()
    {
        logger.info( ServerPluginConstants.BOARD_NAME + " integration test done" );
    }

    /**
     * Function to be executed before start of each test method
     */
    @Before
    public void init()
        throws IOException, HmsException, InterruptedException
    {
        node = new ServiceServerNode();
        node.setManagementIp( nodeIpAddress );
        node.setManagementUserName( nodeUserName );
        node.setManagementUserPassword( nodePassword );
        logger.info( "Node IpAddress:" + node.getManagementIp() );
        /* To be on safe side, do a time out before every function call */
        waitBeforeNewConnection();
        /* If BMC is not reachable, do not run the tests */
        if ( isBMCReachable == null )
            testIsHostManageable();
        assumeTrue( isBMCReachable );
    }

    public void waitBeforeNewConnection()
        throws InterruptedException
    {
        Thread.sleep( TIMEOUT );
    }

    /**
     * Check if the host is reachable or not; If yes, returns true
     */
    public void testIsHostManageable()
        throws HmsException
    {
        logger.info( "Test Board Service isHostAvailable" );
        isBMCReachable = false;
        boolean result = bsServerPlugin.isHostManageable( node );
        isBMCReachable = result;
        assertTrue( "result should be true as the host is manageable!", result );
    }

    /**
     * Get the power status of the board
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetServerPowerStatus()
        throws HmsException
    {
        logger.info( "Test Board Service getSeverPowerStatus" );
        boolean result = bsServerPlugin.getServerPowerStatus( node );
        assertTrue( "Expected Server Power Status result: true, actual result:" + result, result );
    }

    /**
     * Get Power Operations (power up/power down/power cycle/hard reset/cold reset)status
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testPowerOperations()
        throws HmsException
    {
        logger.info( "Test Board Service powerOperations" );
        boolean result = bsServerPlugin.powerOperations( node, PowerOperationAction.POWERUP );
        assertTrue( "Expected Server Power Operations Status result: true, actual result:" + result, result );
    }

    /**
     * Get MAC address of BMC
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetManagementMacAddress()
        throws HmsException
    {
        logger.info( "Test Board Service getManagementMacAddress" );
        String macAddress = bsServerPlugin.getManagementMacAddress( node );
        assertTrue( "Expected MAC Address: xx.xx.xx.xx, actual result:" + macAddress, macAddress != null );
    }

    /**
     * Get management users of BMC
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetManagementUsers()
        throws HmsException
    {
        logger.info( "Test Board Service getManagementUsers" );
        List<BmcUser> bmcUsers = new ArrayList<>();
        bmcUsers = bsServerPlugin.getManagementUsers( node );
        assertNotNull( "BmcUsers cannot be null!", bmcUsers );
        logger.info( "Number of bmcUsers: " + bmcUsers.size() );
        for ( int i = 0; i < bmcUsers.size(); i++ )
        {
            int userId = bmcUsers.get( i ).getUserId();
            String userName = bmcUsers.get( i ).getUserName();
            assertTrue( "Expected user ID is greater than 0, actual result:" + userId, userId > 0 );
            assertNotNull( "Expected user name is NOT NULL, actual result:" + userName, userName );
        }
        assertNotNull( bmcUsers );
    }

    /**
     * BMC to do a Self Test
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testRunSelfTest()
        throws HmsException
    {
        logger.info( "Test Board Service runSelfTest" );
        SelfTestResults selfTestResults = bsServerPlugin.runSelfTest( node );
        assertNotNull( "selfTetResults cannot be null!", selfTestResults );
        assertNotNull( "Expected self test result code cannot be NULL, actual result:"
            + selfTestResults.getSelfTestResultCode(), selfTestResults.getSelfTestResultCode() );
        assertNotNull( "selftTestResults FailureCode cannot be NULL!", selfTestResults.getSelfTestResultFailureCode() );
    }

    /**
     * Get ACPI Power state of the board
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetAcpiPowerState()
        throws HmsException
    {
        logger.info( "Test Board Service getAcpiPowerState" );
        AcpiPowerState acpiPowerState = bsServerPlugin.getAcpiPowerState( node );
        assertNotNull( "acpiPowerState cannot be null!", acpiPowerState );
        assertNotNull( "acpiPowerState deviceAcpiPowerState cannot be null", acpiPowerState.getDeviceAcpiPowerState() );
        assertNotNull( "acpiPowerState systemAcpiPowerState cannot be null", acpiPowerState.getSystemAcpiPowerState() );
    }

    /**
     * Get already set Boot Options
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetBootOptions()
        throws HmsException
    {
        logger.info( "Test Board Service getBootOptions" );
        SystemBootOptions systemBootOptions = bsServerPlugin.getBootOptions( node );
        assertNotNull( "systemBootOptions cannot be null!", systemBootOptions );
        assertNotNull( "systemBootOptions boogFlagsValid cannot be null!", systemBootOptions.getBootFlagsValid() );
        assertNotNull( "systemBootOptions bootDeviceInstanceNumber cannot be null!",
                       systemBootOptions.getBootDeviceInstanceNumber() );
        assertNotNull( "systemBootOptions bootDeviceSelector cannot be null!",
                       systemBootOptions.getBootDeviceSelector() );
        assertNotNull( "systemBootOptions biosBootType cannot be null!", systemBootOptions.getBiosBootType() );
        assertNotNull( "systemBootOptions bootOptionsValidty cannot be null!",
                       systemBootOptions.getBootOptionsValidity() );
        assertNotNull( "systemBootOptions bootDeviceType cannot be null!", systemBootOptions.getBootDeviceType() );
    }

    /**
     * Set Boot Options
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testSetBootOptions()
        throws HmsException
    {
        logger.info( "Test Board Service setBootOptions" );
        final int INSTANCE_NUM = 2;
        SystemBootOptions sysBootOptions = new SystemBootOptions();
        sysBootOptions.setBootFlagsValid( true );
        sysBootOptions.setBootOptionsValidity( BootOptionsValidity.Persistent );
        sysBootOptions.setBiosBootType( BiosBootType.Legacy );
        sysBootOptions.setBootDeviceType( BootDeviceType.External );
        sysBootOptions.setBootDeviceSelector( BootDeviceSelector.PXE );
        sysBootOptions.setBootDeviceInstanceNumber( INSTANCE_NUM );
        boolean status = bsServerPlugin.setBootOptions( node, sysBootOptions );
        assertTrue( "Expected setBootOptions result is true, actual result:" + status, status );
    }

    /**
     * Get server Info of the board (board product name, vendor name etc...)
     **/
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetServerInfo()
        throws HmsException
    {
        logger.info( "Test Board Service getServerInfo" );
        ServerNodeInfo nodeInfo = bsServerPlugin.getServerInfo( node );
        assertNotNull( "nodeInfo cannot be null!", nodeInfo );
        assertNotNull( "nodeInfo boardProductName cannot be null!", nodeInfo.getComponentIdentifier().getPartNumber() );
        assertNotNull( "nodeInfo boardVendor cannot be null!", nodeInfo.getComponentIdentifier().getManufacturer() );
    }

    /**
     * Perform Chassis identification (Blinking lights)
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testSetChassisIdentification()
        throws HmsException
    {
        logger.info( "Test Board Service setChassisIdentification" );
        ChassisIdentifyOptions data = new ChassisIdentifyOptions();
        boolean status = bsServerPlugin.setChassisIdentification( node, data );
        assertTrue( "Expected setChassisIdentification result is true, actual result:" + status, status );
    }

    /**
     * Get System Event Log Information Only. Gives idea about total entries count, last addition time, last erase time.
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetSelInfo()
        throws HmsException
    {
        logger.info( "Test Board Service getSelInfo" );
        SelInfo selInfo = bsServerPlugin.getSelInfo( node );
        assertNotNull( "selInfo cannot be null!", selInfo );
        assertNotNull( "selInfo TotalSelCount cannot be null!", selInfo.getTotalSelCount() );
        assertNotNull( "selInfo FetchedSelCounnt cannot be null!", selInfo.getFetchedSelCount() );
        assertNotNull( "selInfo SelVersion cannot be null!", selInfo.getSelVersion() );
        assertNotNull( "selInfo LastAdditionTimeStamp cannot be null!", selInfo.getLastAddtionTimeStamp() );
        assertNotNull( "selInfo LastEraseTimeStamp cannot be null!", selInfo.getLastEraseTimeStamp() );
    }

    /**
     * Get System Event Log details
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetSelDetails()
        throws HmsException
    {
        logger.info( "Test Board Service getSelDetails" );
        Integer recordCount = null;
        SelFetchDirection direction = null;
        SelInfo selInfo = new SelInfo();
        selInfo = bsServerPlugin.getSelDetails( node, recordCount, direction );
        assertNotNull( selInfo );
        for ( int i = 0; i < selInfo.getSelRecords().size(); i++ )
        {
            assertNotNull( "selInfo RecordId cannot be null!", selInfo.getSelRecords().get( i ).getRecordId() );
            assertNotNull( "selInfo RecordType cannot be null!", selInfo.getSelRecords().get( i ).getRecordType() );
            assertNotNull( "selInfo Timestamp cannot be null!", selInfo.getSelRecords().get( i ).getTimestamp() );
            assertNotNull( "selInfo SensorType cannot be null!", selInfo.getSelRecords().get( i ).getSensorType() );
            assertNotNull( "selInfo SensorNumber cannot be null!", selInfo.getSelRecords().get( i ).getSensorNumber() );
            assertNotNull( "selInfo EventDirection cannot be null!",
                           selInfo.getSelRecords().get( i ).getEventDirection() );
            assertNotNull( "selInfo Event cannot be null!", selInfo.getSelRecords().get( i ).getEvent() );
            assertNotNull( "selInfo Reading cannot be null!", selInfo.getSelRecords().get( i ).getReading() );
        }
    }

    private void assertServerComponentSensorDataValid( List<ServerComponentEvent> serverComponentSensor )
    {
        assertNotNull( "serverComponentSensor cannot be null!", serverComponentSensor );
        for ( int i = 0; i < serverComponentSensor.size(); i++ )
        {
            assertNotNull( "serverComponentSensor EventId cannot be null!", serverComponentSensor );
            assertNotNull( serverComponentSensor.get( i ).getEventId() );
            // assertNotNull(serverComponentSensor.get(i).getUnit()); -- ProcessorPresence may not have any unit
            assertNotNull( serverComponentSensor.get( i ).getValue() );
            assertNotNull( serverComponentSensor.get( i ).getEventName() );
        }
    }

    /**
     * Test BoardService getCpuEventList
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetCpuEventList()
        throws HmsException, InterruptedException
    {
        logger.info( "Test Board Service getCpuEventList" );
        List<ServerComponentEvent> serverComponentSensor = new ArrayList<>();
        serverComponentSensor = bsServerPlugin.getCpuEventList( node );
        assertServerComponentSensorDataValid( serverComponentSensor );
    }

    /**
     * Test BoardService getMemoryEventList
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetMemoryEventList()
        throws HmsException, InterruptedException
    {
        logger.info( "Test Board Service getMemoryEventList" );
        List<ServerComponentEvent> serverComponentSensor = new ArrayList<>();
        serverComponentSensor = bsServerPlugin.getMemoryEventList( node );
        assertServerComponentSensorDataValid( serverComponentSensor );
    }

    /**
     * Test BoardService getFanEventList
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetFanEventList()
        throws HmsException, InterruptedException
    {
        logger.info( "Test Board Service getFanEventList" );
        List<ServerComponentEvent> serverComponentSensor = new ArrayList<>();
        serverComponentSensor = bsServerPlugin.getFanEventList( node );
        assertServerComponentSensorDataValid( serverComponentSensor );
    }

    /**
     * Test BoardService getDriveBayEventList
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetDriveBayEventList()
        throws HmsException, InterruptedException
    {
        logger.info( "Test Board Service getDriveBayEventList" );
        List<ServerComponentEvent> serverComponentSensor = new ArrayList<>();
        serverComponentSensor = bsServerPlugin.getDriveBayEventList( node );
        assertServerComponentSensorDataValid( serverComponentSensor );
    }

    /**
     * Test BoardService getPowerUnitEventList
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetPowerUnitEventList()
        throws HmsException, InterruptedException
    {
        logger.info( "Test Board Service getPowerUnitEventList" );
        List<ServerComponentEvent> serverComponentSensor = new ArrayList<>();
        serverComponentSensor = bsServerPlugin.getPowerUnitEventList( node );
        assertServerComponentSensorDataValid( serverComponentSensor );
    }

    /**
     * Test BoardService getBmcEventList
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetBmcEventList()
        throws HmsException, InterruptedException
    {
        logger.info( "Test Board Service getBmcEventList" );
        List<ServerComponentEvent> serverComponentSensor = new ArrayList<>();
        serverComponentSensor = bsServerPlugin.getBmcEventList( node );
        assertServerComponentSensorDataValid( serverComponentSensor );
    }

    /**
     * Test BoardService getSystemEventList
     */
    @Test( timeout = TEST_TIMEOUT )
    @Repeat( retryCount = RETRY_COUNT, sleepTime = SLEEP_TIME_BEFORE_RETRY )
    public void testGetSystemEventList()
        throws HmsException, InterruptedException
    {
        logger.info( "Test Board Service getSystemEventList" );
        List<ServerComponentEvent> serverComponentSensor = new ArrayList<>();
        serverComponentSensor = bsServerPlugin.getSystemEventList( node );
        assertServerComponentSensorDataValid( serverComponentSensor );
    }

    /**
     * Test BoardService getSupportedBoardInfos
     */
    @Test( timeout = TEST_TIMEOUT )
    public void testGetSupportedBoardInfos()
    {
        logger.info( "Test Board Service getSupportedBoardInfos" );
        List<BoardInfo> supportedBoards;
        supportedBoards = bsServerPlugin.getSupportedBoard();
        assertNotNull( supportedBoards );
        for ( int i = 0; i < supportedBoards.size(); i++ )
        {
            assertNotNull( "supportedBoards BoardProductName cannot be null!",
                           supportedBoards.get( i ).getBoardProductName() );
            assertNotNull( "supportedBoards BoardManufacturer cannot be null!",
                           supportedBoards.get( i ).getBoardManufacturer() );
        }
    }
}
