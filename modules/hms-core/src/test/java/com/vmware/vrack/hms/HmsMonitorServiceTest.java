/* ********************************************************************************
 * HmsMonitorServiceTest.java
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
package com.vmware.vrack.hms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.notification.Event;
import com.vmware.vrack.hms.common.notification.EventHolder;
import com.vmware.vrack.hms.common.notification.EventRequester;
import com.vmware.vrack.hms.common.notification.EventType;
import com.vmware.vrack.hms.common.notification.HMSNotificationRequest;
import com.vmware.vrack.hms.common.resource.fru.EntityId;
import com.vmware.vrack.hms.common.resource.fru.SensorType;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.util.HttpUtil;
import com.vmware.vrack.hms.utils.EventsRegistrationsHolder;

/**
 * Test Classes for Hms Event Notifications
 *
 * @author Yagnesh Chawda
 */
@Ignore
public class HmsMonitorServiceTest
{
    private static Logger logger = Logger.getLogger( HmsMonitorServiceTest.class );

    ObjectMapper mapper = new ObjectMapper();

    /**
     * Populate EventHolder with Events, If addAll is True, it will add all events irrespective of the selectedEvents It
     * will register events passed in @selectedEvents if addAll is false.
     *
     * @param addAll
     * @param selectedEvents
     */
    public void eventHolderSetup( boolean addAll, List<EventType> selectedEvents )
    {
        // Register for all possible events
        EventHolder holder = new EventHolder();
        EventRequester requester = new EventRequester();
        requester.setAppType( "PRMTest" );
        requester.setBaseUrl( "http://localhost:8080/" );
        requester.setSubscriberId( "23" );
        List<Event> eventList = new ArrayList<>();
        if ( addAll )
        {
            for ( EventType type : EventType.values() )
            {
                Event event = new Event();
                event.setEventType( type );
                event.setNotificationUrl( "events/notify" );
                eventList.add( event );
            }
        }
        else
        {
            if ( selectedEvents != null && !selectedEvents.isEmpty() )
            {
                for ( EventType type : selectedEvents )
                {
                    Event event = new Event();
                    event.setEventType( type );
                    event.setNotificationUrl( "events/notify" );
                    eventList.add( event );
                }
            }
        }
        holder.setRequester( requester );
        holder.setEvents( eventList );
        EventsRegistrationsHolder.getInstance().setEventDetails( holder );
    }

    /**
     * Returns Dummy Sensor Data List with 3 Sensor Data
     *
     * @return
     */
    public List<Map<String, String>> getDummySensorData()
    {
        List<Map<String, String>> sensorData = new ArrayList<>();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "sensor1" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.Processor.toString() );
        sensor1.put( "entityId", EntityId.Processor.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "sensor2" );
        sensor2.put( "reading", "47" );
        sensor2.put( "unit", "Voltage" );
        sensor2.put( "sensorType", SensorType.Voltage.toString() );
        sensor2.put( "entityId", EntityId.MemoryModule.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        Map<String, String> sensor3 = new HashMap<>();
        sensor3.put( "name", "sensor1" );
        sensor3.put( "reading", "48" );
        sensor3.put( "unit", "RPM" );
        sensor3.put( "sensorType", SensorType.Fan.toString() );
        sensor3.put( "entityId", EntityId.Processor.toString() );
        sensor3.put( "state", "ok" );
        sensorData.add( sensor3 );
        return sensorData;
    }

    /**
     * Test for Host Availability Event Notification, Should notify Event Requester with HOST_UP event in case any Host
     * becomes available.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyHostUp()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyHostUp" );
        eventHolderSetup( true, null );
        ServerNode node = new ServerNode( "N1", "10.28.197.208", "root", "root123" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        // Node becomes available(discoverable)
        node.setDiscoverable( true );
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( "HOST_UP", request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Test for Host Failure Event Notification, Should Notify Event Requester with HOST_FAILURE Event in case any
     * Available host goes down.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyHostFailure()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyHostFailure:" );
        eventHolderSetup( true, null );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.setDiscoverable( true );
        node.addObserver( blockingHmsMonitorService );
        // Host becomes unavailable, earlier it was available
        node.setDiscoverable( false );
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( "HOST_FAILURE", request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the HOST_MONITOR event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyHostMonitor()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyHostMonitor:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.HOST_MONITOR );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( "HOST_MONITOR", request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertEquals( 3, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the BMC_FW_HEALTH event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyBmcFwHealth()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyBmcFwHealth:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.BMC_FW_HEALTH );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.BMC_FW_HEALTH.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertTrue( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the BMC_FW_HEALTH event which gets triggered when new new Sensor Data is received. Tested with related
     * Sensor Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyBmcFwHealth_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyBmcFwHealth_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.BMC_FW_HEALTH );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "BMC FW health" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.ManagementSubsystemHealth.toString() );
        sensor1.put( "entityId", EntityId.SystemBoard.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "Module Board" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.ModuleBoard.toString() );
        sensor2.put( "entityId", EntityId.SystemBoard.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.BMC_FW_HEALTH.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 1, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the IPMI_WATCHDOG event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyIpmiWatchDog()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyIpmiWatchDog:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.IPMI_WATCHDOG );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.IPMI_WATCHDOG.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertTrue( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the IPMI_WATCHDOG event which gets triggered when new new Sensor Data is received. Tested with related
     * Sensor Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyIpmiWatchDog_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyIpmiWatchDog_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.IPMI_WATCHDOG );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "BMC FW health" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.ManagementSubsystemHealth.toString() );
        sensor1.put( "entityId", EntityId.SystemBoard.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "System watchDog" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.Watchdog2.toString() );
        sensor2.put( "entityId", EntityId.SystemBoard.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.IPMI_WATCHDOG.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 1, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the POWER_SUPPLY event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyPowerSupply()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyPowerSupply:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.POWER_SUPPLY );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.POWER_SUPPLY.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertTrue( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the POWER_SUPPLY event which gets triggered when new new Sensor Data is received. Tested with related
     * Sensor Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyPowerSupply_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyPowerSupply_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.POWER_SUPPLY );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "Power Supply 2" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.PowerSupply.toString() );
        sensor1.put( "entityId", EntityId.PowerSupply.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "Power Supply 1" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.PowerSupply.toString() );
        sensor2.put( "entityId", EntityId.PowerSupply.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.POWER_SUPPLY.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 2, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the SYSTEMBOARD_TEMPERATURE event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifySystemboardTemperature()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifySystemboardTemperature:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.SYSTEMBOARD_TEMPERATURE );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.SYSTEMBOARD_TEMPERATURE.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertTrue( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the SYSTEMBOARD_TEMPERATURE event which gets triggered when new new Sensor Data is received. Tested with
     * related Sensor Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifySystemboardTemperature_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifySystemboardTemperature_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.SYSTEMBOARD_TEMPERATURE );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "System board tempertature 1" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.Temperature.toString() );
        sensor1.put( "entityId", EntityId.SystemBoard.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "System board tempertature 1" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.Temperature.toString() );
        sensor2.put( "entityId", EntityId.SystemBoard.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.SYSTEMBOARD_TEMPERATURE.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 2, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the POWER_SUPPLY_FAN event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyPowerSupplyFan()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyPowerSupplyFan:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.POWER_SUPPLY_FAN );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.POWER_SUPPLY_FAN.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertTrue( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the POWER_SUPPLY_FAN event which gets triggered when new new Sensor Data is received. Tested with related
     * Sensor Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyPowerSupplyFan_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyPowerSupplyFan_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.POWER_SUPPLY_FAN );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "Power Supply Fan 1" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "RPM" );
        sensor1.put( "sensorType", SensorType.Fan.toString() );
        sensor1.put( "entityId", EntityId.PowerSupply.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "Power Supply fan 2" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "RPM" );
        sensor2.put( "sensorType", SensorType.Fan.toString() );
        sensor2.put( "entityId", EntityId.PowerSupply.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.POWER_SUPPLY_FAN.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 2, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the HDD_STATUS event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyHddStatus()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyHddStatus:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.HDD_STATUS );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.HDD_STATUS.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertTrue( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the HDD_STATUS event which gets triggered when new new Sensor Data is received. Tested with related Sensor
     * Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyHddStatus_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyHddStatus_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.HDD_STATUS );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "Drive Backplane  1" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.DriveBay.toString() );
        sensor1.put( "entityId", EntityId.DriveBackplane.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "Drive Backplane  2" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.DriveBay.toString() );
        sensor2.put( "entityId", EntityId.DriveBackplane.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.HDD_STATUS.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 2, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the CHASSIS_SECURITY event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyChassisSecurity()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyChassisSecurity:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.CHASSIS_SECURITY );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.CHASSIS_SECURITY.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertTrue( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the CHASSIS_SECURITY event which gets triggered when new new Sensor Data is received. Tested with related
     * Sensor Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyChassisSecurity_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyChassisSecurity_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.CHASSIS_SECURITY );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "Drive Backplane  1" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.PhysicalSecurity.toString() );
        sensor1.put( "entityId", EntityId.SystemChassis.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "Drive Backplane  2" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.PhysicalSecurity.toString() );
        sensor2.put( "entityId", EntityId.SystemChassis.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.CHASSIS_SECURITY.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 2, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the PROCESSOR event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyProcessor()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyProcessor:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.PROCESSOR );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.PROCESSOR.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the PROCESSOR event which gets triggered when new new Sensor Data is received. Tested with related Sensor
     * Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyProcessor_IfValidSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyProcessor_IfValidSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.PROCESSOR );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "Processor  1" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.Processor.toString() );
        sensor1.put( "entityId", EntityId.Processor.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "Processor  2" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.Temperature.toString() );
        sensor2.put( "entityId", EntityId.Processor.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.PROCESSOR.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 4, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the PROCESSOR_FAN event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyProcessorFan()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyProcessorFan:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.PROCESSOR_FAN );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.PROCESSOR_FAN.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the PROCESSOR_FAN event which gets triggered when new new Sensor Data is received. Tested with related
     * Sensor Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyProcessorFan_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyProcessorFan_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.PROCESSOR_FAN );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "Processor  1" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.Fan.toString() );
        sensor1.put( "entityId", EntityId.Processor.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "Processor  2" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.Fan.toString() );
        sensor2.put( "entityId", EntityId.Processor.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.PROCESSOR_FAN.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 3, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the PROCESSOR_VOLTAGE event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyProcessorVoltage()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyProcessorVoltage:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.PROCESSOR_VOLTAGE );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.PROCESSOR_VOLTAGE.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertTrue( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the PROCESSOR_VOLTAGE event which gets triggered when new new Sensor Data is received. Tested with related
     * Sensor Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyProcessorVoltage_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyProcessorVoltage_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.PROCESSOR_VOLTAGE );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "Processor Voltage 1" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.Voltage.toString() );
        sensor1.put( "entityId", EntityId.Processor.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "Processor Voltage 2" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.Voltage.toString() );
        sensor2.put( "entityId", EntityId.Processor.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.PROCESSOR_VOLTAGE.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 2, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the PROCESSOR_TEMPERATURE event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyProcessorTemperature()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyProcessorTemperature:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.PROCESSOR_TEMPERATURE );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.PROCESSOR_TEMPERATURE.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertTrue( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the PROCESSOR_TEMPERATURE event which gets triggered when new new Sensor Data is received. Tested with
     * related Sensor Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyProcessorTemperature_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyProcessorTemperature_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.PROCESSOR_TEMPERATURE );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "Processor Temperature 1" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.Temperature.toString() );
        sensor1.put( "entityId", EntityId.Processor.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "Processor Temperature 2" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.Temperature.toString() );
        sensor2.put( "entityId", EntityId.Processor.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.PROCESSOR_TEMPERATURE.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 2, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the MEMORY event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyMemory()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyMemory:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.MEMORY );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.MEMORY.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertTrue( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the MEMORY event which gets triggered when new new Sensor Data is received. Tested with related Sensor Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyMemory_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyMemory_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.MEMORY );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "Memory 1" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.Temperature.toString() );
        sensor1.put( "entityId", EntityId.MemoryDevice.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "Memory 2" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.Temperature.toString() );
        sensor2.put( "entityId", EntityId.MemoryDevice.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.MEMORY.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 2, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the MEMORY_VOLTAGE event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyMemoryVoltage()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyMemoryVoltage:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.MEMORY_VOLTAGE );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.MEMORY_VOLTAGE.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertTrue( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the MEMORY_VOLTAGE event which gets triggered when new new Sensor Data is received. Tested with related
     * Sensor Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyMemoryVoltage_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyMemoryVoltage_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.MEMORY_VOLTAGE );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "Memory 1" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.Voltage.toString() );
        sensor1.put( "entityId", EntityId.MemoryDevice.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "Memory 2" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.Voltage.toString() );
        sensor2.put( "entityId", EntityId.MemoryDevice.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.MEMORY_VOLTAGE.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 2, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the MEMORY_TEMPERATURE event which gets triggered when new new Sensor Data is received.
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyMemoryTemperature()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyMemoryTemperature:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.MEMORY_TEMPERATURE );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.MEMORY_TEMPERATURE.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertTrue( request.getListData().isEmpty() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }

    /**
     * Tests the MEMORY_TEMPERATURE event which gets triggered when new new Sensor Data is received. Tested with related
     * Sensor Data
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void testNotifyMemoryTemperature_IfRelatedSensorDataPresent()
        throws JsonGenerationException, JsonMappingException, IOException
    {
        logger.debug( "TS: testNotifyMemoryTemperature_IfRelatedSensorDataPresent:" );
        List<EventType> selectedEvent = new ArrayList<>();
        selectedEvent.add( EventType.MEMORY_TEMPERATURE );
        eventHolderSetup( false, selectedEvent );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        BlockingHmsMonitorService blockingHmsMonitorService = new BlockingHmsMonitorService();
        node.addObserver( blockingHmsMonitorService );
        List<Map<String, String>> sensorData = getDummySensorData();
        Map<String, String> sensor1 = new HashMap<>();
        sensor1.put( "name", "Memory Temperature 1" );
        sensor1.put( "reading", "46" );
        sensor1.put( "unit", "" );
        sensor1.put( "sensorType", SensorType.Temperature.toString() );
        sensor1.put( "entityId", EntityId.MemoryDevice.toString() );
        sensor1.put( "state", "ok" );
        sensorData.add( sensor1 );
        Map<String, String> sensor2 = new HashMap<>();
        sensor2.put( "name", "Memory Temperature 2" );
        sensor2.put( "reading", "46" );
        sensor2.put( "unit", "" );
        sensor2.put( "sensorType", SensorType.Temperature.toString() );
        sensor2.put( "entityId", EntityId.MemoryDevice.toString() );
        sensor2.put( "state", "ok" );
        sensorData.add( sensor2 );
        // Set SensorData
        // node.setSensorData(sensorData);
        try
        {
            blockingHmsMonitorService.waitUntilUpdateIsCalled();
        }
        catch ( InterruptedException e )
        {
            logger.error( e );
        }
        HMSNotificationRequest[] notificationRequests = blockingHmsMonitorService.getTestNotification();
        assertNotNull( notificationRequests );
        assertEquals( 1, notificationRequests.length );
        for ( HMSNotificationRequest request : notificationRequests )
        {
            assertEquals( EventType.MEMORY_TEMPERATURE.toString(), request.getEventType() );
            assertEquals( "N1", request.getTargetId() );
            assertNotNull( request.getListData() );
            assertFalse( request.getListData().isEmpty() );
            assertEquals( 2, request.getListData().size() );
        }
        logger.info( "Post Url for Notification: " + blockingHmsMonitorService.getPostUrl() );
        logger.info( "Final Post Payload Result: " + mapper.writeValueAsString( notificationRequests ) );
    }
}

class BlockingHmsMonitorService
    extends HMSMonitorService
{
    Logger logger = Logger.getLogger( BlockingHmsMonitorService.class );

    private HMSNotificationRequest[] testNotification = null;

    private String postUrl = null;

    private CountDownLatch latch = new CountDownLatch( 1 );

    /**
     * Overridden method to set Final notification payload and Post Url for the notifications
     */
    @Override
    public void postEventCallBack( EventHolder holder, HMSNotificationRequest notification, HmsNode hmsNode )
    {
        logger.debug( "Got subscribers for event " + notification.getEventType() );
        hmsNode.setMonitorExecutionLog( "GOT SUBSCRIBERS FOR EVENT : " + notification.getEventType() + " THREAD : "
            + Thread.currentThread().getId() + " TIME :" + ( new Date() ).toString(), true );
        HMSNotificationRequest[] arrNotification = { notification };
        // Event event = holder.getCallBackEvent(EventType.valueOf(notification.getEventType()));
        List<Event> events = holder.getCallBackEvents( EventType.valueOf( notification.getEventType() ) );
        if ( events != null )
        {
            for ( int i = 0; i < events.size(); i++ )
            {
                Event event = events.get( i );
                if ( event != null )
                {
                    try
                    {
                        List<String> urlParts = new ArrayList<String>();
                        urlParts.add( holder.getRequester().getBaseUrl() );
                        urlParts.add( event.getNotificationUrl() );
                        String url = HttpUtil.buildUrl( urlParts );// "http://10.113.225.133:8080/vrm-ui/rest/notifications/";//
                        logger.debug( "Triggered notification callback for event " + notification.getEventType()
                            + " to URL " + url + " for node" + hmsNode.getManagementIp() );
                        hmsNode.setMonitorExecutionLog( "TRIGGER HTTP CALLBACK FOR EVENT : "
                            + notification.getEventType() + " URL : " + url + " THREAD : "
                            + Thread.currentThread().getId() + " TIME :" + ( new Date() ).toString(), true );
                        switch ( event.getEventType() )
                        {
                            case SWITCH_FAILURE:
                            case SWITCH_UP:
                            case HOST_FAILURE:
                            case HOST_UP:
                            case HMS_OUT_OF_RESOURCES:
                            case HMS_FAILURE:
                                /*
                                 * HttpClientService.getInstance().post(url, mapper.writeValueAsString(arrNotification),
                                 * true, true);
                                 */
                                // HttpUtil.executeRequestAsync(url, RequestMethod.POST,
                                // mapper.writeValueAsString(arrNotification));
                                setPostUrl( url );
                                setTestNotification( arrNotification );
                                event.setLastUpdatedTime( ( new Date() ).getTime() );
                                break;
                            case HOST_MONITOR:
                            case SWITCH_MONITOR:
                                // HttpUtil.executeRequestAsync(url, RequestMethod.POST,
                                // mapper.writeValueAsString(arrNotification));
                                /*
                                 * HttpClientService.getInstance().post(url, mapper.writeValueAsString(arrNotification),
                                 * true, true);
                                 */
                                setPostUrl( url );
                                setTestNotification( arrNotification );
                                event.setLastUpdatedTime( ( new Date() ).getTime() );
                                break;
                            case BMC_FW_HEALTH:
                            case IPMI_WATCHDOG:
                            case POWER_SUPPLY:
                            case SYSTEMBOARD_TEMPERATURE:
                            case POWER_SUPPLY_FAN:
                            case HDD_STATUS:
                            case CHASSIS_SECURITY:
                            case PROCESSOR:
                            case PROCESSOR_FAN:
                            case PROCESSOR_VOLTAGE:
                            case PROCESSOR_TEMPERATURE:
                            case MEMORY:
                            case MEMORY_VOLTAGE:
                            case MEMORY_TEMPERATURE:
                                /*
                                 * HttpClientService.getInstance().post(url, mapper.writeValueAsString(arrNotification),
                                 * true, true);
                                 */
                                setPostUrl( url );
                                setTestNotification( arrNotification );
                                event.setLastUpdatedTime( ( new Date() ).getTime() );
                                break;
                        }
                        logger.debug( "Callback completed for event " + notification.getEventType() + " to URL "
                            + url );
                        hmsNode.setMonitorExecutionLog( "HTTP CALLBACK COMPLETE FOR EVENT : "
                            + notification.getEventType() + " URL : " + url + " THREAD : "
                            + Thread.currentThread().getId() + " TIME :" + ( new Date() ).toString(), true );
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Exception received while posting notification for "
                            + events.get( i ).getEventType(), e );
                    }
                }
            }
        }
        // Latch.countdown will decrease the Count by 1, once it reaches zero, it will resume the awaiting thread
        latch.countDown();
    }

    public void waitUntilUpdateIsCalled()
        throws InterruptedException
    {
        // Wait until the Monitoring thread is completed.
        latch.await();
    }

    public HMSNotificationRequest[] getTestNotification()
    {
        return testNotification;
    }

    public void setTestNotification( HMSNotificationRequest[] testNotification )
    {
        this.testNotification = testNotification;
    }

    public String getPostUrl()
    {
        return postUrl;
    }

    public void setPostUrl( String postUrl )
    {
        this.postUrl = postUrl;
    }
}
