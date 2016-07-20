/* ********************************************************************************
 * BoardService_serverPlugin_UnitTest.java
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
package com.vmware.vrack.hms.plugin.unittests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vmware.vrack.hms.common.boardvendorservice.api.helper.parsers.HmsEventMapper;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.fakeipmiservice.FakeIpmiServiceExecutor;
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
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.plugin.boardservice.BoardService_serverPlugin;

/**
 * Unit test for hms-sample-server-plugin
 *
 * @author VMware Inc.
 */
public class BoardService_serverPlugin_UnitTest
{
    BoardService_serverPlugin bsServerPlugin = new BoardService_serverPlugin();

    static ServiceServerNode node;

    private static Properties properties;

    private static Logger logger = Logger.getLogger( BoardService_serverPlugin_UnitTest.class );

    /**
     * Function executed once at the initialization, before tests are run
     *
     * @TODO Change the BoardName
     */
    @BeforeClass
    public static void startTests()
    {
        logger.info( "<BoardName> unit test started" );
    }

    /**
     * Function executed before exiting the class.
     *
     * @TODO Change the BoardName
     */
    @AfterClass
    public static void cleanup()
    {
        logger.info( "<BoardName> board testing done" );
    }

    /**
     * Function to be executed before start of each test method
     */
    @Before
    public void init()
        throws IOException
    {
        properties = new Properties();
        node = new ServiceServerNode();
        properties.load( this.getClass().getResourceAsStream( "/test.properties" ) );
        node.setManagementIp( properties.getProperty( "ipAddress" ) );
        node.setManagementUserName( properties.getProperty( "username" ) );
        node.setManagementUserPassword( properties.getProperty( "password" ) );
        bsServerPlugin.setIpmiServiceExecutor( new FakeIpmiServiceExecutor() );
    }

    /**
     * Check if the host is reachable or not; If yes, returns true
     */
    @Test
    public void testIsHostManageable()
        throws HmsException
    {
        logger.info( "Test Board Service isHostAvailable" );
        boolean result = bsServerPlugin.isHostManageable( node );
        assertTrue( "result should be true as the host is manageable!", result );
    }

    /**
     * Get the power status of the board
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testGetServerInfo()
        throws HmsException
    {
        logger.info( "Test Board Service getServerInfo" );
        ServerNodeInfo nodeInfo = bsServerPlugin.getServerInfo( node );
        assertNotNull( "nodeInfo cannot be null!", nodeInfo );
        assertNotNull( "nodeInfo boardProductName cannot be null!", nodeInfo.getComponentIdentifier().getProduct() );
        assertNotNull( "nodeInfo boardVendor cannot be null!", nodeInfo.getComponentIdentifier().getManufacturer() );
    }

    /**
     * Perform Chassis identification (Blinking lights)
     */
    @Test
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
    @Test
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
    @Test
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
     * Test BoardService getComponentSensorList
     */
    @Test
    public void testGetComponentEventList()
        throws HmsException
    {
        logger.info( "Test Board Service getComponentEventList" );
        List<ServerComponentEvent> serverComponentSensor = new ArrayList<>();
        serverComponentSensor = bsServerPlugin.getComponentEventList( node, ServerComponent.CPU );
        assertServerComponentSensorDataValid( serverComponentSensor );
        serverComponentSensor = bsServerPlugin.getComponentEventList( node, ServerComponent.MEMORY );
        assertServerComponentSensorDataValid( serverComponentSensor );
        serverComponentSensor = bsServerPlugin.getComponentEventList( node, ServerComponent.FAN );
        assertServerComponentSensorDataValid( serverComponentSensor );
        serverComponentSensor = bsServerPlugin.getComponentEventList( node, ServerComponent.STORAGE );
        assertServerComponentSensorDataValid( serverComponentSensor );
    }

    /**
     * Test BoardService getSupportedBoardInfos
     */
    @Test
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

    @Test
    public void testGetSupportBoard()
    {
        logger.info( "Test Board Service getSupportedBoard" );
        List<BoardInfo> boardInfoList = new ArrayList<>();
        boardInfoList = bsServerPlugin.getSupportedBoard();
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

    /**
     * Test HmsEventMapper getInstance
     */
    @Test
    public void testGetInstance()
    {
        logger.info( "Test HmsEventMapper getInstance" );
        assertNotNull( "HmsEventMapper getInstance should always return an instance", HmsEventMapper.getInstance() );
    }
}
