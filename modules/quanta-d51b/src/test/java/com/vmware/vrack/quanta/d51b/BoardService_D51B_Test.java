/* Copyright 2015 VMware, Inc. All rights reserved. */

package com.vmware.vrack.quanta.d51b;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.*;
import com.vmware.vrack.hms.common.resource.chassis.*;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.quanta.d51b.boardservice.BoardService_D51B;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test cases for quanta ODM Board D51B Service API plug-in
 *
 * @author VMware Inc.
 */
public class BoardService_D51B_Test {

    BoardService_D51B bsQuantaD51B = new BoardService_D51B();
    final ServiceServerNode node = new ServiceServerNode();

    private static Properties properties;
    private static Logger logger = Logger.getLogger(BoardService_D51B_Test.class);

    String mgmtIpAddress = null;
    String mgmtUserName = null;
    String mgmtPassword = null;

    /**
     * Main function for all the unit tests
     */
    @Test
    public void testApp()
    {
        properties = new Properties();

        try
        {
            properties.load(this.getClass().getResourceAsStream("/test.properties"));

            node.setManagementIp(properties.getProperty("ipAddress"));
            node.setManagementUserName(properties.getProperty("username"));
            node.setManagementUserPassword(properties.getProperty("password"));

            /* Change boardService to use seed data for tests(comment out the line for server testing) */
            bsQuantaD51B.setIpmiServiceExecutor(new IpmiServiceExecutorTest());

            if (exercise_BoardService_isHostManageable(node) == true) {
                logger.info("quanta ODM board D51B unit test started");

                exercise_BoardService_getSupportedBoardInfos();
                exercise_BoardService_getServerPowerStatus(node);
                exercise_BoardService_isHostManageable(node);
                exercise_BoardService_powerOperations(node);
                exercise_BoardService_getManagementMacAddress(node);
                exercise_BoardService_getManagementUsers(node);
                exercise_BoardService_getAcpiPowerState(node);
                exercise_BoardService_getBootOptions(node);
                exercise_BoardService_setBootOptions(node);
                exercise_BoardService_getBootOptions(node);
                exercise_BoardService_getServerInfo(node);
                exercise_BoardService_getSensorData(node);
                exercise_BoardService_setChassisIdentification(node);
                exercise_BoardService_getChasssisSerialNumber(node);
                exercise_BoardService_getBoardSerialNumber(node);
                exercise_BoardService_getSelInfo(node);
                exercise_BoardService_getSelDetails(node);
                exercise_BoardService_getComponentEventList(node);
                exercise_BoardService_runSelfTest(node);

                logger.info("quanta ODM board D51B unit testing done");
            }
            else
            {
                logger.info("Couldn't connect to node...quanta ODM board D51B unit tests are not executed");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Check if the host is reachable or not; If yes, returns true
     */
    public boolean exercise_BoardService_isHostManageable(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service isHostAvailable");
        try
        {
            boolean result = bsQuantaD51B.isHostManageable(serviceHmsNode);
            assertTrue(result);
            return true;
        }
        catch (Exception e)
        {
            logger.info("Test isHostAvailable Failed");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the power status of the board
     */
    public void exercise_BoardService_getServerPowerStatus(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service getSeverPowerStatus");
        try
        {
            boolean result = bsQuantaD51B.getServerPowerStatus(serviceHmsNode);
            logger.info("Expected Server Power Status result: true, actual result:" +result);
            assertTrue(result);
        }
        catch (Exception e)
        {
            logger.info("Test getSeverPowerStatus Failed");
            e.printStackTrace();
        }
    }


    /**
     * Get Power Operations (power up/power down/power cycle/hard reset/cold reset)status
     */
    public void exercise_BoardService_powerOperations(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service powerOperations");
        try
        {
            boolean result = false;

            result = bsQuantaD51B.powerOperations(serviceHmsNode, PowerOperationAction.POWERDOWN);
            logger.info("Expected Server Power Operations Status result: true, actual result:" + result);
            assertTrue(result);

            result = false;
            result = bsQuantaD51B.powerOperations(serviceHmsNode, PowerOperationAction.POWERUP);
            logger.info("Expected Server Power Operations Status result: true, actual result:" +result);
            assertTrue(result);

            result = false;
            result = bsQuantaD51B.powerOperations(serviceHmsNode, PowerOperationAction.POWERCYCLE);
            logger.info("Expected Server Power Operations Status result: true, actual result:" +result);
            assertTrue(result);

            result = false;
            result = bsQuantaD51B.powerOperations(serviceHmsNode, PowerOperationAction.HARDRESET);
            logger.info("Expected Server Power Operations Status result: true, actual result:" + result);
            assertTrue(result);

            result = false;
            result = bsQuantaD51B.powerOperations(serviceHmsNode, PowerOperationAction.COLDRESET);
            logger.info("Expected Server Power Operations Status result: true, actual result:" +result);
            assertTrue(result);
        }
        catch (Exception e)
        {
            logger.info("Test PowerOperation Failed");
            e.printStackTrace();
        }
    }

    /**
     * Get MAC address of BMC
     */
    public void exercise_BoardService_getManagementMacAddress(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service getManagementMacAddress");
        try
        {
            String macAddress = bsQuantaD51B.getManagementMacAddress(serviceHmsNode);
            logger.info("Expected MAC Address: xx.xx.xx.xx, actual result:" +macAddress);
            assertNotNull(macAddress);
        }
        catch (Exception e)
        {
            logger.info("Test getManagementMacAddress Failed");
            e.printStackTrace();
        }
    }

    /**
     * Get management users of BMC
     */
    public void exercise_BoardService_getManagementUsers(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service getManagementUsers");
        try
        {
            List<BmcUser> bmcUsers = new ArrayList<>();
            bmcUsers = bsQuantaD51B.getManagementUsers(serviceHmsNode);

            for(int i=0; i<bmcUsers.size(); i++)
            {
                logger.info("Expected user ID is NOT NULL, actual result:" +bmcUsers.get(i).getUserId());
                logger.info("Expected user name is NOT NULL, actual result:" +bmcUsers.get(i).getUserName());
            }
            assertNotNull(bmcUsers);
        }
        catch (Exception e)
        {
            logger.info("Test getManagementUsers Failed");
            e.printStackTrace();
        }
    }

    /**
     * BMC to do a Self Test
     */
    public void exercise_BoardService_runSelfTest(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service runSelfTest");
        try
        {
            SelfTestResults selfTestResults = bsQuantaD51B.runSelfTest(serviceHmsNode);
            logger.info("Expected self test result is NOT NULL, actual result:" +selfTestResults.getSelfTestResultCode());
            assertNotNull(selfTestResults);
            assertNotNull(selfTestResults.getSelfTestResultCode());
            assertNotNull(selfTestResults.getSelfTestResultFailureCode());
        }
        catch (Exception e)
        {
            logger.info("Test runSelfTest Failed");
            e.printStackTrace();
        }
    }

    /**
     * Get ACPI Power state of the board
     */
    public void exercise_BoardService_getAcpiPowerState(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service getAcpiPowerState");
        try
        {
            AcpiPowerState acpiPowerState = bsQuantaD51B.getAcpiPowerState(serviceHmsNode);
            logger.info("Expected Acpi Power State result is NOT NULL, actual result:" +acpiPowerState.getSystemAcpiPowerState()+ " " +acpiPowerState.getDeviceAcpiPowerState());
            assertNotNull(acpiPowerState.getSystemAcpiPowerState());
            assertNotNull(acpiPowerState.getDeviceAcpiPowerState());
        }
        catch (Exception e)
        {
            logger.info("Test getAcpiPowerState Failed");
            e.printStackTrace();
        }
    }

    /**
     * Get already set Boot Options
     */
    public void exercise_BoardService_getBootOptions(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service getBootOptions");
        try
        {
            SystemBootOptions systemBootOptions = bsQuantaD51B.getBootOptions(serviceHmsNode);
            assertNotNull(systemBootOptions);
            assertNotNull(systemBootOptions.getBootFlagsValid());
            assertNotNull(systemBootOptions.getBootDeviceInstanceNumber());
            assertNotNull(systemBootOptions.getBootDeviceSelector());
            assertNotNull(systemBootOptions.getBiosBootType());
            assertNotNull(systemBootOptions.getBootOptionsValidity());
            assertNotNull(systemBootOptions.getBootDeviceType());
        }
        catch (Exception e)
        {
            logger.info("Test getBootOptions Failed");
            e.printStackTrace();
        }
    }

    /**
     * Set Boot Options
     */
    public void exercise_BoardService_setBootOptions(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service setBootOptions");
        final int INSTANCE_NUM = 2;

        try
        {
            SystemBootOptions sysBootOptions = new SystemBootOptions();

            sysBootOptions.setBootFlagsValid(true);
            sysBootOptions.setBootOptionsValidity(BootOptionsValidity.Persistent);
            sysBootOptions.setBiosBootType(BiosBootType.Legacy);
            sysBootOptions.setBootDeviceType(BootDeviceType.External);
            sysBootOptions.setBootDeviceSelector(BootDeviceSelector.PXE);
            sysBootOptions.setBootDeviceInstanceNumber(INSTANCE_NUM);

            boolean status = bsQuantaD51B.setBootOptions(serviceHmsNode, sysBootOptions);
            logger.info("Expected setBootOptions result is true, actual result:" +status);
            assertTrue(status);
        }
        catch (Exception e)
        {
            logger.info("Test setBootOptions Failed");
            e.printStackTrace();
        }
    }

    /**
     * Get server Info of the board (board product name, vendor name etc...)
     **/
    public void exercise_BoardService_getServerInfo(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service getServerInfo");
        try
        {
            ServerNodeInfo nodeInfo = bsQuantaD51B.getServerInfo(serviceHmsNode);
            logger.info("Expected get Server Info result is NOT NULL, actual result:"
                    +nodeInfo.getComponentIdentifier().getProduct()+ " " +nodeInfo.getComponentIdentifier().getManufacturer());
            assertNotNull(nodeInfo.getComponentIdentifier().getProduct());
            assertNotNull(nodeInfo.getComponentIdentifier().getManufacturer());
        }
        catch (Exception e)
        {
            logger.info("Test getServerInfo Failed");
            e.printStackTrace();
        }
    }

    /**
     * Get Sensor Data for the current node
     **/
    public void exercise_BoardService_getSensorData(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service getSensorData");
        try
        {
            List<Map<String, String>> sensorData = new ArrayList<>();

            sensorData= bsQuantaD51B.getSensorData(serviceHmsNode);
            assertNotNull(sensorData);
        }
        catch (Exception e)
        {
            logger.info("Test getSensorData failed");
            e.printStackTrace();
        }
    }

    /**
     * Perform Chassis identification (Blinking lights)
     */
    public void exercise_BoardService_setChassisIdentification(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service setChassisIdentification");
        try
        {
            ChassisIdentifyOptions data = new ChassisIdentifyOptions();
            boolean status = bsQuantaD51B.setChassisIdentification(serviceHmsNode, data);
            logger.info("Expected setChassisIdentification result is true, actual result:" +status);
            assertTrue(status);
        }
        catch (Exception e)
        {
            logger.info("Test setChassisIdentification Failed");
            e.printStackTrace();
        }
    }

    /**
     * Get Chasssis Serial Number for the board
     */
    public void exercise_BoardService_getChasssisSerialNumber(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service getChasssisSerialNumber");
        try
        {
            String chasssisSerialNumber = bsQuantaD51B.getChasssisSerialNumber(serviceHmsNode);
            logger.info("Expected getChasssisSerialNumber result is Not NULL, actual result:" +chasssisSerialNumber);
            assertNotNull(chasssisSerialNumber);
        }
        catch (Exception e)
        {
            logger.info("Test getChasssisSerialNumber Failed");
            e.printStackTrace();
        }
    }

    /**
     * Get Board Serial Number for the board
     */
    public void exercise_BoardService_getBoardSerialNumber(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service getBoardSerialNumber");
        try
        {
            String boardSerialNumber = bsQuantaD51B.getBoardSerialNumber(serviceHmsNode);
            logger.info("Expected getBoardSerialNumber result is Not NULL, actual result:" +boardSerialNumber);
            assertNotNull(boardSerialNumber);
        }
        catch (Exception e)
        {
            logger.info("Test getBoardSerialNumber Failed");
            e.printStackTrace();
        }
    }

    /**
     * Get System Event Log Information Only. Gives idea about total entries count, last addition time, last erase time.
     */
    public void exercise_BoardService_getSelInfo(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service getSelInfo");
        try
        {
            SelInfo selInfo = bsQuantaD51B.getSelInfo(serviceHmsNode);

            assertNotNull(selInfo);
            assertNotNull(selInfo.getTotalSelCount());
            assertNotNull(selInfo.getFetchedSelCount());
            assertNotNull(selInfo.getSelVersion());
            assertNotNull(selInfo.getLastAddtionTimeStamp());
            assertNotNull(selInfo.getLastEraseTimeStamp());
        }
        catch (Exception e)
        {
            logger.info("Test getSelInfo Failed");
            e.printStackTrace();
        }
    }

    /**
     * Get System Event Log details
     */
    public void exercise_BoardService_getSelDetails(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service getSelDetails");
        try
        {
            Integer recordCount = null;
            SelFetchDirection direction = null;

            SelInfo selInfo = new SelInfo ();

            selInfo = bsQuantaD51B.getSelDetails(node, recordCount, direction);
            assertNotNull(selInfo);

            for (int i=0; i < selInfo.getSelRecords().size(); i++ )
            {
                assertNotNull(selInfo.getSelRecords().get(i).getRecordId());
                assertNotNull(selInfo.getSelRecords().get(i).getRecordType());
                assertNotNull(selInfo.getSelRecords().get(i).getTimestamp());
                assertNotNull(selInfo.getSelRecords().get(i).getSensorType());
                assertNotNull(selInfo.getSelRecords().get(i).getSensorNumber());
                assertNotNull(selInfo.getSelRecords().get(i).getEventDirection());
                assertNotNull(selInfo.getSelRecords().get(i).getEvent());
                assertNotNull(selInfo.getSelRecords().get(i).getReading());
            }

        }
        catch (Exception e)
        {
            logger.info("Test to get System event Log failed");
            e.printStackTrace();
        }
    }

    /**
     * Test BoardService getComponentSensorList
     */
    public void exercise_BoardService_getComponentEventList(ServiceHmsNode serviceHmsNode)
    {
        logger.info("Test Board Service getComponentSensorList");

        try
        {
            List<ServerComponentEvent> serverComponentSensor;

            //For CPU events
            serverComponentSensor =  new ArrayList<ServerComponentEvent>();
            serverComponentSensor = bsQuantaD51B.getComponentEventList(serviceHmsNode, ServerComponent.CPU);
            assertNotNull(serverComponentSensor);

            for (int i=0; i<serverComponentSensor.size(); i++)
            {
                assertNotNull(serverComponentSensor.get(i).getEventId());
                assertNotNull(serverComponentSensor.get(i).getUnit());
                assertNotNull(serverComponentSensor.get(i).getValue());
                assertNotNull(serverComponentSensor.get(i).getEventName());
                assertNotNull(serverComponentSensor.get(i).getComponentId());
                assertNotNull(serverComponentSensor.get(i).getDiscreteValue());
            }

            //For Memory events
            serverComponentSensor =  new ArrayList<ServerComponentEvent>();
            serverComponentSensor = bsQuantaD51B.getComponentEventList(serviceHmsNode, ServerComponent.MEMORY);
            assertNotNull(serverComponentSensor);

            for (int i=0; i<serverComponentSensor.size(); i++)
            {
                assertNotNull(serverComponentSensor.get(i).getEventId());
                assertNotNull(serverComponentSensor.get(i).getUnit());
                assertNotNull(serverComponentSensor.get(i).getValue());
                assertNotNull(serverComponentSensor.get(i).getEventName());
                assertNotNull(serverComponentSensor.get(i).getComponentId());
                assertNotNull(serverComponentSensor.get(i).getDiscreteValue());
            }

            //For HDD events
            serverComponentSensor =  new ArrayList<ServerComponentEvent>();
            serverComponentSensor = bsQuantaD51B.getComponentEventList(serviceHmsNode, ServerComponent.STORAGE);
            assertNotNull(serverComponentSensor);

            for (int i=0; i<serverComponentSensor.size(); i++)
            {
                assertNotNull(serverComponentSensor.get(i).getEventId());
                assertNotNull(serverComponentSensor.get(i).getUnit());
                assertNotNull(serverComponentSensor.get(i).getValue());
                assertNotNull(serverComponentSensor.get(i).getEventName());
                assertNotNull(serverComponentSensor.get(i).getComponentId());
                assertNotNull(serverComponentSensor.get(i).getDiscreteValue());
            }

            //For FAN events
            serverComponentSensor =  new ArrayList<ServerComponentEvent>();
            serverComponentSensor = bsQuantaD51B.getComponentEventList(serviceHmsNode, ServerComponent.FAN);
            assertNotNull(serverComponentSensor);

            for (int i=0; i<serverComponentSensor.size(); i++) {
                assertNotNull(serverComponentSensor.get(i).getEventId());
                assertNotNull(serverComponentSensor.get(i).getUnit());
                assertNotNull(serverComponentSensor.get(i).getValue());
                assertNotNull(serverComponentSensor.get(i).getEventName());
                assertNotNull(serverComponentSensor.get(i).getComponentId());
                assertNotNull(serverComponentSensor.get(i).getDiscreteValue());
            }

            //For BMC events
            serverComponentSensor =  new ArrayList<ServerComponentEvent>();
            serverComponentSensor = bsQuantaD51B.getComponentEventList(serviceHmsNode, ServerComponent.BMC);
            assertNotNull(serverComponentSensor);

            for (int i=0; i<serverComponentSensor.size(); i++)
            {
                assertNotNull(serverComponentSensor.get(i).getEventId());
                assertNotNull(serverComponentSensor.get(i).getUnit());
                assertNotNull(serverComponentSensor.get(i).getValue());
                assertNotNull(serverComponentSensor.get(i).getEventName());
                assertNotNull(serverComponentSensor.get(i).getComponentId());
                assertNotNull(serverComponentSensor.get(i).getDiscreteValue());
            }

            //For Power Unit events
            serverComponentSensor =  new ArrayList<ServerComponentEvent>();
            serverComponentSensor = bsQuantaD51B.getComponentEventList(serviceHmsNode, ServerComponent.POWERUNIT);
            assertNotNull(serverComponentSensor);

            for (int i=0; i<serverComponentSensor.size(); i++)
            {
                assertNotNull(serverComponentSensor.get(i).getEventId());
                assertNotNull(serverComponentSensor.get(i).getUnit());
                assertNotNull(serverComponentSensor.get(i).getValue());
                assertNotNull(serverComponentSensor.get(i).getEventName());
                assertNotNull(serverComponentSensor.get(i).getComponentId());
                assertNotNull(serverComponentSensor.get(i).getDiscreteValue());
            }

            //For SYSTEM events
            serverComponentSensor =  new ArrayList<ServerComponentEvent>();
            serverComponentSensor = bsQuantaD51B.getComponentEventList(serviceHmsNode, ServerComponent.SYSTEM);
            assertNotNull(serverComponentSensor);

            for (int i=0; i<serverComponentSensor.size(); i++)
            {
                assertNotNull(serverComponentSensor.get(i).getEventId());
                assertNotNull(serverComponentSensor.get(i).getUnit());
                assertNotNull(serverComponentSensor.get(i).getValue());
                assertNotNull(serverComponentSensor.get(i).getEventName());
                assertNotNull(serverComponentSensor.get(i).getComponentId());
                assertNotNull(serverComponentSensor.get(i).getDiscreteValue());
            }

        }
        catch (Exception e)
        {
            logger.info("Test getComponentSensorList Failed");
            e.printStackTrace();
        }
    }

    /**
     * Test BoardService getSupportedBoardInfos
     */
    public void exercise_BoardService_getSupportedBoardInfos()
    {
        logger.info("Test Board Service getSupportedBoardInfos");

        try
        {
            List<BoardInfo> supportedBoards;

            supportedBoards = bsQuantaD51B.getSupportedBoard();
            assertNotNull(supportedBoards);
            for (int i=0; i<supportedBoards.size(); i++)
            {
                logger.info("Expected supported Board result is Not NULL, actual result:" +supportedBoards.get(i).getBoardProductName());
                assertNotNull(supportedBoards.get(i).getBoardProductName());
                assertNotNull(supportedBoards.get(i).getBoardManufacturer());
            }
        }
        catch (Exception e)
        {
            logger.info("Test getSupportedBoardInfos Failed");
            e.printStackTrace();
        }
    }

}
