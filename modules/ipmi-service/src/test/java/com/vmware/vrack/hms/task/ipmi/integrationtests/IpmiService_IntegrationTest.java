/* ********************************************************************************
 * IpmiService_IntegrationTest.java
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
package com.vmware.vrack.hms.task.ipmi.integrationtests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.veraxsystems.vxipmi.coding.commands.session.SessionCustomPayload;
import com.veraxsystems.vxipmi.coding.security.CipherSuite;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.BiosBootType;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceSelector;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceType;
import com.vmware.vrack.hms.common.resource.chassis.BootOptionsValidity;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.resource.fru.ChassisInfo;
import com.vmware.vrack.hms.common.resource.fru.FruRecord;
import com.vmware.vrack.hms.common.resource.fru.ProductInfo;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.resource.sel.SelRecord;
import com.vmware.vrack.hms.common.resource.sel.SelTask;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.plugin.testlib.IntegrationTest;
import com.vmware.vrack.hms.ipmiservice.IpmiConnectionPool;
import com.vmware.vrack.hms.ipmiservice.IpmiConnectionSettings;
import com.vmware.vrack.hms.task.ipmi.AcpiPowerStateTask;
import com.vmware.vrack.hms.task.ipmi.ChassisIdentifyTask;
import com.vmware.vrack.hms.task.ipmi.FindMacAddressTask;
import com.vmware.vrack.hms.task.ipmi.FruDataTask;
import com.vmware.vrack.hms.task.ipmi.GetSystemBootOptionsTask;
import com.vmware.vrack.hms.task.ipmi.IpmiTaskConnector;
import com.vmware.vrack.hms.task.ipmi.IsHostAvailableTask;
import com.vmware.vrack.hms.task.ipmi.ListBmcUsersTask;
import com.vmware.vrack.hms.task.ipmi.PowerStatusServerTask;
import com.vmware.vrack.hms.task.ipmi.PowerUpServerTask;
import com.vmware.vrack.hms.task.ipmi.SelInfoTask;
import com.vmware.vrack.hms.task.ipmi.SelfTestTask;
import com.vmware.vrack.hms.task.ipmi.SensorStatusTask;
import com.vmware.vrack.hms.task.ipmi.ServerInfoTask;
import com.vmware.vrack.hms.task.ipmi.SetSystemBootOptionsTask;
import com.vmware.vrack.hms.utils.Constants;

/**
 * Integration tests for IPMI Service
 *
 * @author VMware Inc.
 */
@Category( IntegrationTest.class )
public class IpmiService_IntegrationTest
{
    private static Logger logger = Logger.getLogger( IpmiService_IntegrationTest.class );

    private static ServiceServerNode node = new ServiceServerNode();

    public static IpmiTaskConnector connector = null;

    private static final boolean FAIL_ON_CONNECTION = true;

    public static boolean connectorFlag = false;

    private static Properties properties;

    @BeforeClass
    public static void startTests()
    {
        logger.info( "IPMI Service Integration tests started" );
    }

    @AfterClass
    public static void cleanup()
    {
        tearDown();
        logger.info( "IPMI Service Integration testing done" );
    }

    @Before
    public void setup()
    {
        try
        {
            properties = new Properties();
            properties.load( this.getClass().getResourceAsStream( "/ipmitest.properties" ) );
            node.setManagementIp( properties.getProperty( "ipAddress" ) );
            node.setManagementUserName( properties.getProperty( "username" ) );
            node.setManagementUserPassword( properties.getProperty( "password" ) );
            if ( connectorFlag == false )
            {
                connectorFlag = true;
                connector = getIpmiTaskConnector( node );
            }
            if ( connector != null )
            {
                logger.info( "IPMI Service connection test successful" );
            }
        }
        catch ( Exception e )
        {
            logger.info( "IPMI Service Connection Test Failed" );
            logger.debug( e.getStackTrace() );
        }
    }

    private static Map<String, IpmiConnectionSettings> connectionSettings =
        new HashMap<String, IpmiConnectionSettings>();

    private IpmiTaskConnector getIpmiTaskConnector( ServiceServerNode node )
        throws Exception
    {
        IpmiConnectionSettings settings = null;
        SessionCustomPayload customOpenSessionPayload =
            new SessionCustomPayload( (byte) 0x01, (byte) 0x01, (byte) 0x01 );
        CipherSuite cs = new CipherSuite( (byte) 0, (byte) 1, (byte) 0, (byte) 0 );
        synchronized ( this )
        {
            if ( connectionSettings.containsKey( node.getNodeID() ) )
                settings = connectionSettings.get( node.getNodeID() );
            else
            {
                settings = createConnectionSettings( node, 0, false, cs, customOpenSessionPayload );
                connectionSettings.put( node.getNodeID(), settings );
            }
        }
        return IpmiConnectionPool.getInstance().getPool().borrowObject( settings );
        // return
        // IpmiTaskConnectorFactory.getIpmiTaskConnector(node,cipherSuiteIndex,encryptData,cipherSuite,customSessionPayload);
    }

    private static void returnIpmiTaskConnector( ServiceServerNode node, IpmiTaskConnector connector )
        throws Exception
    {
        IpmiConnectionSettings settings = connectionSettings.get( node.getNodeID() );
        IpmiConnectionPool.getInstance().getPool().returnObject( settings, connector );
        // return
        // IpmiTaskConnectorFactory.getIpmiTaskConnector(node,cipherSuiteIndex,encryptData,cipherSuite,customSessionPayload);
    }

    private IpmiConnectionSettings createConnectionSettings( ServiceServerNode node, int cipherSuiteIndex,
                                                             boolean encryptData, CipherSuite cipherSuite,
                                                             SessionCustomPayload customSessionPayload )
    {
        IpmiConnectionSettings settings = new IpmiConnectionSettings();
        settings.setNode( node );
        settings.setCipherSuite( cipherSuite );
        settings.setCipherSuiteIndex( cipherSuiteIndex );
        settings.setEncryptData( encryptData );
        settings.setSessionOpenPayload( customSessionPayload );
        return settings;
    }

    public static void tearDown()
    {
        try
        {
            returnIpmiTaskConnector( node, connector );
            logger.info( "IPMI Connection Closed" );
        }
        catch ( Exception e )
        {
            logger.info( "IPMI tearDown Test Failed" );
            logger.debug( e.getStackTrace() );
        }
    }

    @Test
    public void IsHostAvailableTaskTest()
    {
        logger.info( "IPMI Service test Is Host Available Task Test" );
        if ( connector != null )
        {
            try
            {
                IsHostAvailableTask isHostAvailableTask = new IsHostAvailableTask( node, connector );
                boolean status = isHostAvailableTask.executeTask();
                assertTrue( "Expected Host Available status  is true, actual result:" + status, status );
            }
            catch ( Exception e )
            {
                logger.info( "IPMI Service Test Is Host Available Task Test Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the  Is Host Available Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    @Test
    public void PowerStatusServerTaskTest()
    {
        logger.info( "IPMI Service Test Power Status Server Task" );
        if ( connector != null )
        {
            try
            {
                PowerStatusServerTask powerStatusServerTask = new PowerStatusServerTask( node, connector );
                boolean powerStatus = powerStatusServerTask.executeTask();
                assertTrue( "Expected Host Power Status Server Task Status result: true, actual result:" + powerStatus,
                            powerStatus );
            }
            catch ( Exception e )
            {
                logger.info( "IPMI Service Test Power Status Server Task Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the Power Status Server Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    @Test
    public void AcpiPowerStateTaskTest()
    {
        logger.info( "IPMI Service Test ACPI Power State Task" );
        if ( connector != null )
        {
            try
            {
                AcpiPowerStateTask acpiPowerStateTask = new AcpiPowerStateTask( node, connector );
                AcpiPowerState powerState = acpiPowerStateTask.executeTask();
                assertNotNull( "acpiPowerState cannot be null!", powerState );
                assertNotNull( "acpiPowerState deviceAcpiPowerState cannot be null",
                               powerState.getDeviceAcpiPowerState() );
                assertNotNull( "acpiPowerState systemAcpiPowerState cannot be null",
                               powerState.getSystemAcpiPowerState() );
            }
            catch ( Exception e )
            {
                logger.info( "IPMI Service Test ACPI Power State Task Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the ACPI Power State Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    @Test
    public void ChassisIdentifyTaskTest()
    {
        logger.info( "Test IPMI Service Chassis Identity Task" );
        if ( connector != null )
        {
            try
            {
                ChassisIdentifyOptions chassisIdentifyOptions = new ChassisIdentifyOptions();
                ChassisIdentifyTask chassisIdentifyTask =
                    new ChassisIdentifyTask( node, connector, chassisIdentifyOptions );
                boolean status = chassisIdentifyTask.executeTask();
                assertTrue( "Expected setChassisIdentification result is true, actual result:" + status, status );
            }
            catch ( Exception e )
            {
                logger.info( "Test IPMI Service Chassis Identity Task Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the Chassis Identity Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    @Test
    public void FindMacAddressTaskTest()
    {
        logger.info( "Test IPMI Service Find MAC address Task" );
        if ( connector != null )
        {
            try
            {
                FindMacAddressTask findMacAddressTask = new FindMacAddressTask( node, connector );
                String macAddress = findMacAddressTask.executeTask();
                assertTrue( "Expected MAC Address: xx.xx.xx.xx, actual result:" + macAddress, macAddress != null );
            }
            catch ( Exception e )
            {
                logger.info( "Test IPMI Service Find MAC address Task Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the Find MAC Address Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    public void FruDataTaskTest()
    {
        logger.info( "Test IPMI Service FRU Data Task" );
        if ( connector != null )
        {
            try
            {
                FruDataTask fruDataTask = new FruDataTask( node, connector );
                List<Object> fruInfo = fruDataTask.executeTask();
                for ( int i = 0; i < fruInfo.size(); i++ )
                {
                    FruRecord record = (FruRecord) fruInfo.get( i );
                    if ( record instanceof BoardInfo )
                    {
                        BoardInfo bi = (BoardInfo) record;
                        assertNotNull( "Expected FRU Board Info is NOT NULL, actual result:" + bi, bi );
                    }
                    else if ( record instanceof ChassisInfo )
                    {
                        ChassisInfo ci = (ChassisInfo) record;
                        assertNotNull( "Expected FRU Chassis Info is NOT NULL, actual result:" + ci, ci );
                    }
                    else if ( record instanceof ProductInfo )
                    {
                        ProductInfo pi = (ProductInfo) record;
                        assertNotNull( "Expected FRU Product Info is NOT NULL, actual result:" + pi, pi );
                    }
                    else
                    {
                        logger.info( "Other instace not in the FRU list" );
                    }
                }
            }
            catch ( Exception e )
            {
                logger.info( "Test IPMI Service FRU Data Task Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the FRU data Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    @Test
    public void GetSystemBootOptionsTaskTest()
    {
        logger.info( "Test IPMI Service Get System Boot Options Task Test" );
        if ( connector != null )
        {
            try
            {
                GetSystemBootOptionsTask getSystemBootOptionsTask = new GetSystemBootOptionsTask( node, connector );
                SystemBootOptions systemBootOptions = getSystemBootOptionsTask.executeTask();
                assertNotNull( "systemBootOptions cannot be null!", systemBootOptions );
                assertNotNull( "systemBootOptions boogFlagsValid cannot be null!",
                               systemBootOptions.getBootFlagsValid() );
                assertNotNull( "systemBootOptions bootDeviceInstanceNumber cannot be null!",
                               systemBootOptions.getBootDeviceInstanceNumber() );
                assertNotNull( "systemBootOptions bootDeviceSelector cannot be null!",
                               systemBootOptions.getBootDeviceSelector() );
                assertNotNull( "systemBootOptions biosBootType cannot be null!", systemBootOptions.getBiosBootType() );
                assertNotNull( "systemBootOptions bootOptionsValidty cannot be null!",
                               systemBootOptions.getBootOptionsValidity() );
                assertNotNull( "systemBootOptions bootDeviceType cannot be null!",
                               systemBootOptions.getBootDeviceType() );
            }
            catch ( Exception e )
            {
                logger.info( "Test IPMI Service Get System Boot Options Task Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the Get System Boot Options Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    @Test
    public void SetSystemBootOptionsTaskTest()
    {
        logger.info( "IPMI Service Test Set System Boot Options Task" );
        if ( connector != null )
        {
            try
            {
                SystemBootOptions sysBootOptions = new SystemBootOptions();
                sysBootOptions.setBootFlagsValid( true );
                sysBootOptions.setBootOptionsValidity( BootOptionsValidity.Persistent );
                sysBootOptions.setBiosBootType( BiosBootType.Legacy );
                sysBootOptions.setBootDeviceType( BootDeviceType.External );
                sysBootOptions.setBootDeviceSelector( BootDeviceSelector.PXE );
                sysBootOptions.setBootDeviceInstanceNumber( 2 );
                SetSystemBootOptionsTask setSystemBootOptionsTask =
                    new SetSystemBootOptionsTask( node, connector, sysBootOptions );
                boolean status = setSystemBootOptionsTask.executeTask();
                assertTrue( "Expected setBootOptions result is true, actual result:" + status, status );
            }
            catch ( Exception e )
            {
                logger.info( "IPMI Service Test Set System Boot Options Task Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the Set System Boot Options Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    @Test
    public void ListBmcUsersTaskTest()
    {
        logger.info( "IPMI Service Testing Task List BMC/ME management Users" );
        if ( connector != null )
        {
            try
            {
                List<BmcUser> bmcUsers = new ArrayList<BmcUser>();
                ListBmcUsersTask listBmcUsersTask = new ListBmcUsersTask( node, connector );
                bmcUsers = listBmcUsersTask.executeTask();
                assertNotNull( "BmcUsers cannot be null!", bmcUsers );
                for ( int i = 0; i < bmcUsers.size(); i++ )
                {
                    logger.info( "Expected user ID is NOT NULL, actual result:" + bmcUsers.get( i ).getUserId() );
                    logger.info( "Expected user name is NOT NULL, actual result:" + bmcUsers.get( i ).getUserName() );
                }
            }
            catch ( Exception e )
            {
                logger.info( "IPMI Service Testing Task List BMC/ME management Users Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the List BMC Management Users Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    @Test
    public void SelfTestTaskTest()
    {
        logger.info( "IPMI Service Test Self Test Task" );
        if ( connector != null )
        {
            try
            {
                SelfTestTask selfTestTask = new SelfTestTask( node, connector );
                SelfTestResults results = selfTestTask.executeTask();
                assertNotNull( "selfTetResults cannot be null!", results );
                assertNotNull( "Expected self test result code cannot be NULL, actual result:"
                    + results.getSelfTestResultCode(), results.getSelfTestResultCode() );
                assertNotNull( "selftTestResults FailureCode cannot be NULL!", results.getSelfTestResultFailureCode() );
            }
            catch ( Exception e )
            {
                logger.info( "IPMI Service Test Self Test Task Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the Self Test Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    @Test
    public void SelInfoTaskTest()
    {
        logger.info( "IPMI Service Test Sel Info and details Task" );
        if ( connector != null )
        {
            try
            {
                List<SelRecord> selFilters = null;
                Integer recordCount = 5;
                SelInfoTask selInfoTask;
                SelInfo selInfo;
                selInfoTask = new SelInfoTask( node, connector, SelTask.SelInfo, Constants.DEFAULT_MAX_SEL_COUNT,
                                               SelFetchDirection.RecentEntries );
                selInfo = selInfoTask.executeTask();
                assertNotNull( "selInfo cannot be null!", selInfo );
                assertNotNull( "selInfo TotalSelCount cannot be null!", selInfo.getTotalSelCount() );
                assertNotNull( "selInfo FetchedSelCounnt cannot be null!", selInfo.getFetchedSelCount() );
                assertNotNull( "selInfo SelVersion cannot be null!", selInfo.getSelVersion() );
                assertNotNull( "selInfo LastAdditionTimeStamp cannot be null!", selInfo.getLastAddtionTimeStamp() );
                assertNotNull( "selInfo LastEraseTimeStamp cannot be null!", selInfo.getLastEraseTimeStamp() );
                selInfo = null;
                selInfoTask = new SelInfoTask( node, connector, SelTask.SelDetails, Constants.DEFAULT_MAX_SEL_COUNT,
                                               SelFetchDirection.RecentEntries );
                selInfo = selInfoTask.executeTaskWithFilter( selFilters );
                assertNotNull( selInfo );
                logger.info( "IPMI Service Getting all the SEL Records" );
                for ( int i = 0; i < selInfo.getSelRecords().size(); i++ )
                {
                    assertNotNull( "selInfo RecordId cannot be null!", selInfo.getSelRecords().get( i ).getRecordId() );
                    assertNotNull( "selInfo RecordType cannot be null!",
                                   selInfo.getSelRecords().get( i ).getRecordType() );
                    assertNotNull( "selInfo Timestamp cannot be null!",
                                   selInfo.getSelRecords().get( i ).getTimestamp() );
                    assertNotNull( "selInfo SensorType cannot be null!",
                                   selInfo.getSelRecords().get( i ).getSensorType() );
                    assertNotNull( "selInfo SensorNumber cannot be null!",
                                   selInfo.getSelRecords().get( i ).getSensorNumber() );
                    assertNotNull( "selInfo EventDirection cannot be null!",
                                   selInfo.getSelRecords().get( i ).getEventDirection() );
                    assertNotNull( "selInfo Event cannot be null!", selInfo.getSelRecords().get( i ).getEvent() );
                    assertNotNull( "selInfo Reading cannot be null!", selInfo.getSelRecords().get( i ).getReading() );
                }
                selInfo = null;
                selInfoTask = new SelInfoTask( node, connector, SelTask.SelDetails, recordCount,
                                               SelFetchDirection.OldestEntries );
                selInfo = selInfoTask.executeTaskWithFilter( selFilters );
                assertNotNull( selInfo );
                logger.info( "IPMI Service Getting all the Filetered SEL Records" );
                for ( int i = 0; i < selInfo.getSelRecords().size(); i++ )
                {
                    assertNotNull( "selInfo RecordId cannot be null!", selInfo.getSelRecords().get( i ).getRecordId() );
                    assertNotNull( "selInfo RecordType cannot be null!",
                                   selInfo.getSelRecords().get( i ).getRecordType() );
                    assertNotNull( "selInfo Timestamp cannot be null!",
                                   selInfo.getSelRecords().get( i ).getTimestamp() );
                    assertNotNull( "selInfo SensorType cannot be null!",
                                   selInfo.getSelRecords().get( i ).getSensorType() );
                    assertNotNull( "selInfo SensorNumber cannot be null!",
                                   selInfo.getSelRecords().get( i ).getSensorNumber() );
                    assertNotNull( "selInfo EventDirection cannot be null!",
                                   selInfo.getSelRecords().get( i ).getEventDirection() );
                    assertNotNull( "selInfo Event cannot be null!", selInfo.getSelRecords().get( i ).getEvent() );
                    assertNotNull( "selInfo Reading cannot be null!", selInfo.getSelRecords().get( i ).getReading() );
                }
            }
            catch ( Exception e )
            {
                logger.info( "IPMI Service Test Sel Info and details Task Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the SEL info and details Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    @Test
    public void SensorStatusTaskTest()
    {
        logger.info( "IPMI Service Test Senosor Status Task" );
        if ( connector != null )
        {
            try
            {
                properties = new Properties();
                properties.load( this.getClass().getResourceAsStream( "/ipmitest.properties" ) );
                List<Integer> listsensorNumber = new ArrayList<Integer>();
                List<Map<String, String>> sensorData = new ArrayList<>();
                Integer headerSize = Integer.parseInt( properties.getProperty( "headerSize" ) );
                Integer initialChunkSize = Integer.parseInt( properties.getProperty( "intialChunkSize" ) );
                Integer chunkSize = Integer.parseInt( properties.getProperty( "chunkSize" ) );
                String[] sensornumber = properties.getProperty( "sensornumber" ).split( "," );
                ServiceHmsNode hmsNode = node;
                for ( int i = 0; i < sensornumber.length; i++ )
                {
                    listsensorNumber.add( Integer.parseInt( sensornumber[i].trim() ) );
                }
                SensorStatusTask sensorStatusTask = new SensorStatusTask( hmsNode, connector );
                sensorStatusTask.setChunkSizes( headerSize, initialChunkSize, chunkSize );
                sensorData = sensorStatusTask.executeTask( listsensorNumber );
                assertNotNull( sensorData );
            }
            catch ( Exception e )
            {
                logger.info( "IPMI Service Test Sensor Status Task Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the Sensor Status Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    @Test
    public void ServerInfoTaskTest()
    {
        logger.info( "IPMI Service Test Server Info Task Test" );
        if ( connector != null )
        {
            try
            {
                properties = new Properties();
                properties.load( this.getClass().getResourceAsStream( "/ipmitest.properties" ) );
                ServerInfoTask serverInfoTask = new ServerInfoTask( node, connector );
                serverInfoTask.setFruReadPacketSize( Integer.parseInt( properties.getProperty( "fruReadOcpServerInfo" ) ) );
                ServerNodeInfo serverNodeInfo = serverInfoTask.executeTask();
                assertNotNull( "nodeInfo cannot be null!", serverNodeInfo );
                assertNotNull( "nodeInfo boardProductName cannot be null!",
                               serverNodeInfo.getComponentIdentifier().getProduct() );
                assertNotNull( "nodeInfo boardVendor cannot be null!",
                               serverNodeInfo.getComponentIdentifier().getManufacturer() );
            }
            catch ( Exception e )
            {
                logger.info( "IPMI Service Test Server Info Task Test Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the Server Info Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }

    @Test
    public void PowerUpServerTaskTest()
    {
        logger.info( "IPMI Service Test Power UP Server Task" );
        if ( connector != null )
        {
            try
            {
                PowerUpServerTask powerUpServerTask = new PowerUpServerTask( node, connector );
                boolean powerStatus = powerUpServerTask.executeTask();
                assertTrue( "Expected Power UP Server Task Status result is true, actual result:" + powerStatus,
                            powerStatus );
            }
            catch ( Exception e )
            {
                logger.info( "IPMI Service Test Power UP Server Task Failed!" );
                logger.debug( e.getStackTrace() );
            }
        }
        else
        {
            logger.info( "IPMI connection failed...couldn't run the Power Up Server Task Test " );
            if ( FAIL_ON_CONNECTION )
                fail();
        }
    }
}
