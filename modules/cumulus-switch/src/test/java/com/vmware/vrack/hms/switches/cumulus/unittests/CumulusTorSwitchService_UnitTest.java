/* ********************************************************************************
 * CumulusTorSwitchService_UnitTest.java
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
package com.vmware.vrack.hms.switches.cumulus.unittests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.fakeSwitchService.FakeISwitchService;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;
import com.vmware.vrack.hms.common.switches.api.SwitchHardwareInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchOsInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfGlobalConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfInterfaceConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfInterfaceConfig.InterfaceMode;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfNetworkConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo.ChassisTemp;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo.FanSpeed;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo.PsuStatus;
import com.vmware.vrack.hms.common.switches.api.SwitchSession;
import com.vmware.vrack.hms.common.switches.api.SwitchType;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.switches.cumulus.CumulusTorSwitchServiceTest;

/**
 * Unit test for cumulus-switch
 *
 * @author VMware Inc.
 */
public class CumulusTorSwitchService_UnitTest
{

    static FakeISwitchService fakeISwitchService_Obj;

    private static Logger logger = Logger.getLogger( CumulusTorSwitchServiceTest.class );

    static SwitchNode switchNode_Obj = null;

    static List<String> listOfVlansString;

    static List<SwitchVlan> listOfSwitchVlans;

    static List<String> listOfLacpGroupNames;

    static List<String> listOfSwitchPorts;

    private static Properties properties;

    private static final boolean PRINT_STACK_TRACE = false;

    private static final boolean FAIL_ON_EXCEPTION = true;

    /**
     * Function executed once at the initialization, before any tests are run
     */
    @BeforeClass
    public static void startTests()
    {
        System.out.println( "Switch unit tests started - with dummy data." );
        logger.info( "Switch unit tests started - with dummy data." );
    }

    /**
     * Function executed before exiting the class.
     */
    @AfterClass
    public static void cleanup()
    {
        System.out.println( "Switch unit tests finished." );
        logger.info( "Switch unit tests finished." );
    }

    @BeforeClass
    public static void setUpBeforeClass()
        throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass()
        throws Exception
    {
    }

    /**
     * Function to be executed before start of each test method
     */
    @Before
    public void init()
    {
        String deviceIp = "1.2.3.4";
        String deviceUser = "uper";
        String devicePassword = "password";

        properties = new Properties();
        switchNode_Obj = null;
        fakeISwitchService_Obj = new FakeISwitchService();
        switchNode_Obj = new SwitchNode( "S0", "SSH", deviceIp, new Integer( 22 ), deviceUser, devicePassword );
        logger.info( "Initialization of switch node." );
    }

    @Test
    public void testInvalidLogin()
    {
        String deviceIp = "10.28.197.242";
        String deviceUser = "cumulus";
        String devicePassword = "BadPassword";

        SwitchNode switchNode = new SwitchNode( "S0", "SSH", deviceIp, new Integer( 22 ), deviceUser, devicePassword );
        ISwitchService switchService = new FakeISwitchService();

        SwitchOsInfo switchOsInfo = null;
        try
        {
            switchOsInfo = switchService.getSwitchOsInfo( switchNode );
            if ( switchOsInfo != null )
            {
                System.out.println( "[Device IP : " + deviceIp + "] Found Switch OS information = "
                    + switchOsInfo.getOsName() );
            }
            else
            {
                System.out.println( "[Device IP : " + deviceIp + "] No information found for Switch OS." );
            }

        }
        catch ( Exception excep )
        {
            System.out.println( "Error while getting Switch OS information: " + excep.toString() );
        }
    }

    /**
     * Get Switch Type: Using the correct switch object, get the switch type.
     */
    @Test
    public void testGetSwitchType()
    {
        String switchType = "cumulus";
        String switchTypeRetrieved;
        try
        {
            switchTypeRetrieved = fakeISwitchService_Obj.getSwitchType();
            assertNotNull( "Switch Type is null", switchTypeRetrieved );
            if ( switchType.compareTo( switchTypeRetrieved ) == 0 )
            {
                logger.info( "SwitchType matches the function return value: "
                    + fakeISwitchService_Obj.getSwitchType() );
            }
            else
            {
                logger.info( "The values don't match our test-set value: " + switchType );
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while comparing the type of switch: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Discover Switch: With Switch object, confirm (T/F) switch exists.
     */
    @Test
    public void testDiscoverSwitch()
    {
        boolean isCumulus;

        try
        {
            isCumulus = fakeISwitchService_Obj.discoverSwitch( switchNode_Obj );
            assertTrue( "Was NOT able to discover switch. Expected result: True; Value returned: " + isCumulus,
                        isCumulus );
        }
        catch ( Exception e )
        {
            logger.info( "Error when attempting to discover the switch: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }

    }

    /**
     * Get Session: Confirm switch session accessible and able to connect/disconnect.
     */
    @Test
    public void testGetSession()
    {
        SwitchSession switchSession = null;
        try
        {
            if ( switchNode_Obj != null )
            {
                switchSession = fakeISwitchService_Obj.getSession( switchNode_Obj );
                assertNotNull( "The switchSession is NULL", switchSession );
                if ( switchSession != null && switchSession.isConnected() )
                {
                    logger.info( "swithcSession is connected" );
                    switchSession.disconnect();
                }
                else
                {
                    logger.info( "switchSession is NOT connected" );
                }
            }
            else
            {
                logger.info( "switchNode_Obj is null" );
                fail();
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error when getting session: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Is Powered On: Confirm that switch is powered on.
     */
    @Test
    public void testIsPoweredOn()
    {
        boolean isPoweredOn;
        try
        {
            isPoweredOn = fakeISwitchService_Obj.isPoweredOn( switchNode_Obj );
            assertTrue( "The switch is NOT powered on. Expected result: True; Value returned: " + isPoweredOn,
                        isPoweredOn );
        }
        catch ( Exception e )
        {
            logger.info( "Error when testing if Switch is Powered on: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Get Switch OS Info: Get Switch Info. Confirm info object is not null.
     */
    @Test
    public void testGetSwitchOsInfo()
    {

        SwitchOsInfo osInfo = null;
        try
        {
            osInfo = fakeISwitchService_Obj.getSwitchOsInfo( switchNode_Obj );
            assertNotNull( "OSInfo is null", osInfo );
            if ( osInfo != null )
            {
                logger.info( "[Device IP : " + switchNode_Obj.getIpAddress() + "] Found Switch OS information." );
                logger.info( "OS info: " );
                logger.info( "FirmwareName: " + osInfo.getFirmwareName() );
                logger.info( "FirmwareVersion: " + osInfo.getFirmwareVersion() );
                logger.info( "OsName: " + osInfo.getOsName() );
                logger.info( "OsVersion: " + osInfo.getOsVersion() );
                logger.info( "Class Name: " + osInfo.getClass().getName() );
                logger.info( "LastBootTime: " + osInfo.getLastBoot().toString() );
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting Switch OS information: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }

    }

    /**
     * Get Switch Hardware Info: Get Switch Hardware Info. Confirm hwInfo object is not null.
     */
    @Test
    public void testGetSwitchHardwareInfo()
    {
        SwitchHardwareInfo hwInfo = null;
        try
        {
            hwInfo = fakeISwitchService_Obj.getSwitchHardwareInfo( switchNode_Obj );
            assertNotNull( "HWInfo is null", hwInfo );
            if ( hwInfo != null )
            {
                logger.info( "[Device IP : " + switchNode_Obj.getIpAddress() + "] Found Switch Hardware information." );
                logger.info( "Hardware info: " );
                logger.info( "Class Name: " + hwInfo.getClass().getName() );
                logger.info( "Model: " + hwInfo.getModel() );
                logger.info( "Manufacturer: " + hwInfo.getManufacturer() );
                logger.info( "Chassis Serial Id: " + hwInfo.getChassisSerialId() );
                logger.info( "Management Mac Address: " + hwInfo.getManagementMacAddress() );
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting Switch Hardware information: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }

    }

    /**
     * Get SwitchPort list: Collect all the switch port names.
     */
    @Test
    public void testGetSwitchPortList()
    {
        listOfSwitchPorts = null;
        try
        {
            listOfSwitchPorts = fakeISwitchService_Obj.getSwitchPortList( switchNode_Obj );
            assertNotNull( "List of SwitchPorts is null", listOfSwitchPorts );
            if ( listOfSwitchPorts != null && !listOfSwitchPorts.isEmpty() )
            {
                logger.info( "Size of list of ports: " + listOfSwitchPorts.size() );
                logger.info( "Print all Ports:" );
                for ( int i = 0; i < listOfSwitchPorts.size(); i++ )
                {
                    logger.info( listOfSwitchPorts.get( i ) );
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting Switch Port list: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Get Switch Port: Get each switch port by using the switchport names list. More info: Confirm all attributes of
     * the switchport are not null.
     */
    @Test
    public void testGetSwitchPort()
    {
        SwitchPort switchPort_Obj = null;
        try
        {
            if ( listOfSwitchPorts == null )
            {
                listOfSwitchPorts = fakeISwitchService_Obj.getSwitchPortList( switchNode_Obj );
                assertNotNull( "The list of switch ports is null", listOfSwitchPorts );
            }
            logger.info( "List of Switchports exist. Check each switch port." );
            logger.info( "Size of list of Switchports: " + listOfSwitchPorts.size() );
            for ( int i = 0; i < listOfSwitchPorts.size(); i++ )
            {
                logger.info( "from list, get each i: " + i + " list of switchports value: "
                    + listOfSwitchPorts.get( i ) );
                switchPort_Obj = fakeISwitchService_Obj.getSwitchPort( switchNode_Obj, listOfSwitchPorts.get( i ) );
                if ( switchPort_Obj != null )
                {
                    assertNotNull( switchPort_Obj.getType() );
                    assertNotNull( switchPort_Obj.getStatus() );
                    assertNotNull( switchPort_Obj.getStatistics() );
                    logger.info( "i: " + i + " objMap: " + switchPort_Obj.getObjMap() );
                    assertNotNull( switchPort_Obj.getObjMap() );
                    assertNotNull( switchPort_Obj.getName() );
                    assertNotNull( switchPort_Obj.getMtu() );
                    assertNotNull( switchPort_Obj.getMacAddress() );
                    logger.info( "i: " + i + " linkedPort: " + switchPort_Obj.getLinkedMacAddresses() );
                    logger.info( "i: " + i + " MacAddress: " + switchPort_Obj.getLinkedMacAddresses() );
                    logger.info( "i: " + i + " IPAddress: " + switchPort_Obj.getIpAddress() );
                    assertNotNull( switchPort_Obj.getIfNumber() );
                    assertNotNull( switchPort_Obj.getFlags() );
                    assertNotNull( switchPort_Obj.getClass() );
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting switch port" );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Get Switchport status: Get switch port status, confirm not null.
     */
    @Test
    public void testGetSwitchPortStatus()
    {
        SwitchPort switchPort_Obj = null;
        try
        {
            if ( listOfSwitchPorts == null )
            {
                listOfSwitchPorts = fakeISwitchService_Obj.getSwitchPortList( switchNode_Obj );
                assertNotNull( "List of switch ports is null", listOfSwitchPorts );
            }
            logger.info( "List of Switchports exist. Check each switch port." );
            logger.info( "size of switchports:: " + listOfSwitchPorts.size() );
            for ( int i = 0; i < listOfSwitchPorts.size(); i++ )
            {
                switchPort_Obj = fakeISwitchService_Obj.getSwitchPort( switchNode_Obj, listOfSwitchPorts.get( i ) );
                if ( switchPort_Obj != null )
                {
                    assertNotNull( "The switchPort status is null", switchPort_Obj.getStatus() );
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting switch port status" );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Get Switch Vlans: Get list of all vlans.
     */
    @Test
    public void testGetSwitchVlans()
    {
        listOfVlansString = null;
        try
        {
            listOfVlansString = fakeISwitchService_Obj.getSwitchVlans( switchNode_Obj );
            assertNotNull( "List of vlans (string) is null", listOfVlansString );
            if ( listOfVlansString != null && !listOfVlansString.isEmpty() )
            {
                logger.info( "Size of list of vlans: " + listOfVlansString.size() );
                logger.info( "Print all vlans:" );
                for ( int i = 0; i < listOfVlansString.size(); i++ )
                {
                    logger.info( listOfVlansString.get( i ) );
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting Switch Vlans list: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Get Switch Vlan: Using the list of vlans, get each vlan object and confirm not null.
     */
    @Test
    public void testGetSwitchVlan()
    {
        SwitchVlan switchVlan_Obj = null;
        try
        {
            if ( listOfVlansString == null )
            {
                listOfVlansString = fakeISwitchService_Obj.getSwitchVlans( switchNode_Obj );
                assertNotNull( "The list of vlans (string) is null", listOfVlansString );
            }
            if ( listOfVlansString != null && !listOfVlansString.isEmpty() )
            {
                logger.info( "Size of list of vlans: " + listOfVlansString.size() );
                for ( int i = 0; i < listOfVlansString.size(); i++ )
                {
                    switchVlan_Obj = fakeISwitchService_Obj.getSwitchVlan( switchNode_Obj, listOfVlansString.get( i ) );
                    assertNotNull( "SwitchVlan object is null", switchVlan_Obj );
                    if ( switchVlan_Obj != null )
                    {
                        assertNotNull( switchVlan_Obj.getName() );
                        logger.info( "i: " + i + " id: " + switchVlan_Obj.getId() );
                        assertNotNull( switchVlan_Obj.getClass() );
                        logger.info( "i: " + i + " IPAddress: " + switchVlan_Obj.getIpAddress() );
                        logger.info( "i: " + i + " getMtu: " + switchVlan_Obj.getMtu() );
                        logger.info( "i: " + i + " getNetmask: " + switchVlan_Obj.getNetmask() );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting a single vlan value" );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Get Switch Vlans Bulk list: Get all the Vlans in a bulk list, and confirm that they exist.
     */
    @Test
    public void testGetSwitchVlansBulk()
    {
        listOfSwitchVlans = null;
        boolean printAll = true;
        try
        {
            listOfSwitchVlans = fakeISwitchService_Obj.getSwitchVlansBulk( switchNode_Obj );
            assertNotNull( "List of switch vlans is null", listOfSwitchVlans );
            if ( listOfSwitchVlans != null && !listOfSwitchVlans.isEmpty() )
            {
                logger.info( "Size of list of SwitchVlans object: " + listOfSwitchVlans.size() );
                logger.info( "Print all SwitchVlan objects: " );
                if ( printAll )
                {
                    for ( int i = 0; i < listOfSwitchVlans.size(); i++ )
                    {
                        logger.info( listOfSwitchVlans.get( i ).getId() );
                        logger.info( listOfSwitchVlans.get( i ).getIpAddress() );
                        logger.info( listOfSwitchVlans.get( i ).getName() );
                        logger.info( listOfSwitchVlans.get( i ).getNetmask() );
                        logger.info( listOfSwitchVlans.get( i ).getClass().toString() );
                        logger.info( listOfSwitchVlans.get( i ).getMtu() );
                        logger.info( ( listOfSwitchVlans.get( i ).getTaggedPorts().isEmpty() ) ? "taggedPorts is Empty"
                                        : "taggedPorts is NOT Empty" );
                        logger.info( ( listOfSwitchVlans.get( i ).getUntaggedPorts().isEmpty() )
                                        ? "untaggedPorts is Empty" : "untaggedPorts is NOT Empty" );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting list of Switch Vlans" );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Create Vlan: Use this test to create a Vlan object. Currently Ignore.
     */
    @Ignore
    @Test
    public void testCreateVlan()
    {
        logger.info( "Create VLAN" );
        boolean createVlanSuccess = false;
        try
        {
            if ( listOfSwitchVlans == null )
            {
                listOfSwitchVlans = fakeISwitchService_Obj.getSwitchVlansBulk( switchNode_Obj );
                assertNotNull( "The list of switch vlans is null", listOfSwitchVlans );
            }
            else
            {
                for ( int vlan = 0; vlan < listOfSwitchVlans.size(); vlan++ )
                {
                    logger.info( "in here" );
                    createVlanSuccess =
                        fakeISwitchService_Obj.createVlan( switchNode_Obj, listOfSwitchVlans.get( vlan ) );
                    assertTrue( "Vlan failed to get created", createVlanSuccess );
                    logger.info( "Test was on: " + listOfSwitchVlans.get( vlan ).getName() );
                    logger.info( "Creating Vlan on specified switch a success." );
                }
            }
        }
        catch ( HmsException e )
        {
            logger.info( "There was error in create vLan: " + e.getMessage() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Get Switch Lacp Groups: Gather the list of all Lacp Group names.
     */
    @Test
    public void testGetSwitchLacpGroups()
    {
        logger.info( "switch Lacp Groups" );
        listOfLacpGroupNames = null;
        try
        {
            listOfLacpGroupNames = fakeISwitchService_Obj.getSwitchLacpGroups( switchNode_Obj );
            assertNotNull( "listOfLacpGroupNames is null, No Lacp Groups", listOfLacpGroupNames );
            if ( listOfLacpGroupNames != null && !listOfLacpGroupNames.isEmpty() )
            {
                logger.info( "Size of list of switch lacp groups" );
                for ( int i = 0; i < listOfLacpGroupNames.size(); i++ )
                {
                    logger.info( listOfLacpGroupNames.get( i ) );
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting switch Lacp Groups" );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Get Switch Lacp Group: Get the unique Lacp Group name, Mode and ports associated with it.
     */
    @Test
    public void testGetSwitchLacpGroup()
    {
        SwitchLacpGroup switchLacpGroup_Obj = null;
        try
        {
            if ( listOfLacpGroupNames == null )
            {
                listOfLacpGroupNames = fakeISwitchService_Obj.getSwitchLacpGroups( switchNode_Obj );
                assertNotNull( "listOfLacpGroupNames is null. No Lacp Groups.", listOfLacpGroupNames );
            }
            else
            {
                if ( listOfLacpGroupNames != null && !listOfLacpGroupNames.isEmpty() )
                {
                    logger.info( "Size of list of SwitchLacpGroups: " + listOfLacpGroupNames.size() );
                    for ( int i = 0; i < listOfLacpGroupNames.size(); i++ )
                    {
                        switchLacpGroup_Obj =
                            fakeISwitchService_Obj.getSwitchLacpGroup( switchNode_Obj, listOfLacpGroupNames.get( i ) );
                        assertNotNull( "Switch Lacp Group is null", switchLacpGroup_Obj );
                        if ( switchLacpGroup_Obj != null )
                        {
                            logger.info( "Object not null." );
                            logger.info( "Mode: " + switchLacpGroup_Obj.getMode() );
                            logger.info( "Name: " + switchLacpGroup_Obj.getName() );
                            logger.info( "Class: " + switchLacpGroup_Obj.getClass().toString() );
                            List<String> ports = switchLacpGroup_Obj.getPorts();
                            logger.info( "Ports below (size:" + ports.size() + ") of ports List" );
                            for ( int j = 0; j < ports.size(); j++ )
                            {
                                logger.info( ports.get( j ) );
                            }
                        }
                    }
                }
                else
                {
                    logger.info( "List of switch lacp groups is either null or empty" );
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting a single switch lacp group" );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
            {
                fail();
            }
        }
    }

    /**
     * Get Switch Sensor Info: Gather the switch Sensor information: timestamp, chassis temp, fan speed and psu status.
     */
    @Test
    public void testGetSwitchSensorInfo()
    {
        SwitchSensorInfo sensorInfo_Obj = null;
        try
        {
            sensorInfo_Obj = fakeISwitchService_Obj.getSwitchSensorInfo( switchNode_Obj );
            assertNotNull( "SensorInfo is null", sensorInfo_Obj );
            if ( sensorInfo_Obj != null )
            {
                logger.info( "Get Sensor Info" );
                logger.info( "Timestamp: " + sensorInfo_Obj.getTimestamp() );
                logger.info( "Class: " + sensorInfo_Obj.getClass() );
                List<ChassisTemp> chassisTempList = sensorInfo_Obj.getChassisTemps();
                List<FanSpeed> fanSpeedList = sensorInfo_Obj.getFanSpeeds();
                List<PsuStatus> psuStatusList = sensorInfo_Obj.getPsuStatus();

                assertNotNull( "ChassisTemp list is null", chassisTempList );
                if ( chassisTempList.isEmpty() && chassisTempList != null )
                {
                    logger.info( "ChassisTemp List is not null/empty. List: " );
                    for ( int i = 0; i < chassisTempList.size(); i++ )
                    {
                        logger.info( chassisTempList.get( i ) );
                    }
                }

                assertNotNull( "fanSpeed list is null", fanSpeedList );
                if ( fanSpeedList.isEmpty() && fanSpeedList != null )
                {
                    logger.info( "fanSpeed List is not null/empty. List: " );
                    for ( int i = 0; i < fanSpeedList.size(); i++ )
                    {
                        logger.info( fanSpeedList.get( i ) );
                    }
                }

                assertNotNull( "psuStatus list is null", psuStatusList );
                if ( psuStatusList.isEmpty() && psuStatusList != null )
                {
                    logger.info( "psuStatus List is not null/empty. List: " );
                    for ( int i = 0; i < psuStatusList.size(); i++ )
                    {
                        logger.info( psuStatusList.get( i ) );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting Switch OS information: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Get Ospf: Using our switch object, confirm the global config info -- Interface and network list.
     */
    @Test
    public void testGetOspf()
    {
        SwitchOspfConfig ospfConfig_Obj = null;
        try
        {
            ospfConfig_Obj = fakeISwitchService_Obj.getOspf( switchNode_Obj );
            assertNotNull( "The ospfConfig is null", ospfConfig_Obj );
            if ( ospfConfig_Obj != null )
            {
                logger.info( "class name: " + ospfConfig_Obj.getClass().getName() );
                SwitchOspfGlobalConfig globalConfig = ospfConfig_Obj.getGlobal();
                assertNotNull( "The global config is null", globalConfig );
                logger.info( "Router Id: " + globalConfig.getRouterId() );
                logger.info( "Default Mode: " + globalConfig.getDefaultMode() );
                List<SwitchOspfNetworkConfig> networkConfigList = globalConfig.getNetworks();
                List<SwitchOspfInterfaceConfig> interfaceConfigList = globalConfig.getInterfaces();
                assertNotNull( "The network config list is null", networkConfigList );
                assertNotNull( "The interface config list is null", interfaceConfigList );
                for ( int i = 0; i < networkConfigList.size(); i++ )
                {
                    logger.info( "Network: " + networkConfigList.get( i ).getNetwork() );
                    logger.info( "Area: " + networkConfigList.get( i ).getArea() );
                }
                for ( int i = 0; i < interfaceConfigList.size(); i++ )
                {
                    logger.info( "Name: " + interfaceConfigList.get( i ).getName() );
                    InterfaceMode interfaceMode = interfaceConfigList.get( i ).getMode();
                    logger.info( "InterfaceMode name: " + interfaceMode.getClass().getName() );
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting Ospf information: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Get Supported Switch Types: Find all switch types that are supported by the plugin.
     */
    @Test
    public void testGetSupportedSwitchTypes()
    {
        List<SwitchType> listOfSupportedSwitchTypes = null;
        try
        {
            listOfSupportedSwitchTypes = fakeISwitchService_Obj.getSupportedSwitchTypes();
            assertNotNull( "The list of supported switch types is null", listOfSupportedSwitchTypes );
            if ( listOfSupportedSwitchTypes != null && !listOfSupportedSwitchTypes.isEmpty() )
            {
                logger.info( "Size of list of supported switch types: " + listOfSupportedSwitchTypes.size() );
                logger.info( "Print all supported types:" );
                for ( int i = 0; i < listOfSupportedSwitchTypes.size(); i++ )
                {
                    logger.info( listOfSupportedSwitchTypes.get( i ).getManufacturer() );
                    logger.info( listOfSupportedSwitchTypes.get( i ).getModel() );
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting Switch Port list: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }

    }

    /**
     * Get Switch Port Bulk info: For the specified switch node, get the switch port details (bulk).
     */
    @Test
    public void testGetSwitchPortListBulk()
    {
        List<SwitchPort> listOfPorts = null;
        try
        {
            listOfPorts = fakeISwitchService_Obj.getSwitchPortListBulk( switchNode_Obj );
            assertNotNull( "The Switch Port Bulk list is null", listOfPorts );
            if ( listOfPorts != null && !listOfPorts.isEmpty() )
            {
                logger.info( "Print all Port List Bulk:" );
                for ( int i = 0; i < listOfPorts.size(); i++ )
                {
                    logger.info( "object " + i + ":" );
                    logger.info( listOfPorts.get( i ).getIfNumber() );
                    logger.info( listOfPorts.get( i ).getName() );
                    logger.info( listOfPorts.get( i ).getSpeed() );
                    logger.info( listOfPorts.get( i ).getFlags() );
                    logger.info( listOfPorts.get( i ).getMtu() );
                    logger.info( listOfPorts.get( i ).getStatus() );
                    logger.info( listOfPorts.get( i ).getType() );
                    logger.info( listOfPorts.get( i ).getMacAddress() );
                    if ( listOfPorts.get( i ).getStatistics() != null )
                    {
                        logger.info( listOfPorts.get( i ).getStatistics().getTimestamp() );
                        logger.info( listOfPorts.get( i ).getStatistics().getTxSentPackets() );
                        logger.info( listOfPorts.get( i ).getStatistics().getTxDroppedPackets() );
                        logger.info( listOfPorts.get( i ).getStatistics().getTxErrors() );
                        logger.info( listOfPorts.get( i ).getStatistics().getRxReceivedPackets() );
                        logger.info( listOfPorts.get( i ).getStatistics().getRxDroppedPackets() );
                        logger.info( listOfPorts.get( i ).getStatistics().getRxErrors() );
                    }
                    logger.info( listOfPorts.get( i ).getLinkedMacAddresses() );
                    if ( listOfPorts.get( i ).getLinkedPort() != null )
                    {
                        logger.info( listOfPorts.get( i ).getLinkedPort().getPortName() );
                        logger.info( listOfPorts.get( i ).getLinkedPort().getMac() );
                        logger.info( listOfPorts.get( i ).getLinkedPort().getDeviceName() );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting Switch Port Bulk list: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }

    }

    /**
     * Get Component Event List: Sensor Info such as Fan Speed, Chassis Temperature, and PSU Status.
     */
    @Test
    public void testGetComponentEventList()
    {
        List<ServerComponentEvent> listOfEvents = null;
        ServiceHmsNode serviceNode = new ServiceHmsNode();
        SwitchComponentEnum component = null;

        try
        {
            listOfEvents = fakeISwitchService_Obj.getComponentSwitchEventList( serviceNode, component );
            assertNotNull( "List of events is null", listOfEvents );
            if ( listOfEvents != null && !listOfEvents.isEmpty() )
            {
                logger.info( "Print out component Event List details" );
                for ( int i = 0; i < listOfEvents.size(); i++ )
                {
                    logger.info( listOfEvents.get( i ).getEventName() );
                    logger.info( listOfEvents.get( i ).getEventId() );
                    logger.info( listOfEvents.get( i ).getValue() );
                    logger.info( listOfEvents.get( i ).getDiscreteValue() );
                    logger.info( listOfEvents.get( i ).getComponentId() );
                    logger.info( listOfEvents.get( i ).getUnit() );
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting Component Event List list: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

    /**
     * Get Supported HMS Api values. STatic values added to the API list.
     */
    @Test
    public void testGetSupportedHmsApi()
    {
        List<HmsApi> supportedHmsApis = null;
        ServiceHmsNode serviceNode = new ServiceHmsNode();

        try
        {
            supportedHmsApis = fakeISwitchService_Obj.getSupportedHmsSwitchApi( serviceNode );
            assertNotNull( "List of Supported HMS Apis is null", supportedHmsApis );
            if ( supportedHmsApis != null && !supportedHmsApis.isEmpty() )
            {
                logger.info( "Print out list of supported HMS Apis" );
                for ( int i = 0; i < supportedHmsApis.size(); i++ )
                {
                    logger.info( supportedHmsApis.get( i ) );
                }
            }
        }
        catch ( Exception e )
        {
            logger.info( "Error while getting Supported HMS Apis list: " + e.toString() );
            if ( PRINT_STACK_TRACE )
                logger.debug( e.getStackTrace() );
            if ( FAIL_ON_EXCEPTION )
                fail();
        }
    }

}
