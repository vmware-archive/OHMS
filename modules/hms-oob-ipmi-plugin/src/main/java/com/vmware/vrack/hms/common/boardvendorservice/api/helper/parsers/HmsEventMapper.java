/* ********************************************************************************
 * HmsEventMapper.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.api.helper.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vmware.vrack.hms.common.resource.fru.SensorType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.util.JsonUtils;

/**
 * Type of the reading of the discrete sensor.
 *
 * @author VMware, Inc.
 */
public class HmsEventMapper
{
    private static final String OEM_PROP_FILE_NAME = "config/partner-event-config.properties";

    private static final String LOCAL_EVENT_CONFIG_FILE = "hms-default-eventMap.json";

    private static final String DEFAULT_EVENT_CONFIG_FILE = "config/hms-default-eventMap.json";

    private static HashMap<Integer, NodeEvent[]> hmsNodeEventListMap = new HashMap<>();

    private static HashMap<Integer, String> hmsNodeEventNameMap = new HashMap<>();

    private static HmsEventMapper hmsMapper = null;

    private static Logger logger = Logger.getLogger( HmsEventMapper.class );

    private HmsEventMapper()
    {
        Properties prop = new Properties();
        Boolean processOemData = false;
        int oemEventConfigFileCount = 0;
        String oemEventConfigFile = null;
        try
        {
            logger.info( "Trying to load default event configuration file" );
            Path path = Paths.get( DEFAULT_EVENT_CONFIG_FILE );
            if ( Files.exists( path, LinkOption.NOFOLLOW_LINKS ) )
            {
                if ( !addEvents( path.toString() ) )
                {
                    // Missing default configuration file is a serious error
                    logger.error( "Unable to initialize HmsEventMapper with default event list" );
                }
            }
            else
            {
                // Try loading file from inside JAR
                if ( !addEvents( LOCAL_EVENT_CONFIG_FILE ) )
                {
                    // Missing the local configuration file is a serious error
                    logger.error( "Unable to initialize HmsEventMapper with default event list" );
                }
            }
        }
        catch ( NullPointerException e )
        {
            logger.error( "Default event mapper configuration property missing" );
        }
        try
        {
            // Load the config properties listing the mapping configuration files
            Path path = Paths.get( OEM_PROP_FILE_NAME );
            if ( Files.exists( path, LinkOption.NOFOLLOW_LINKS ) )
            {
                prop.load( HmsEventMapper.class.getClassLoader().getResourceAsStream( OEM_PROP_FILE_NAME ) );
                processOemData = true;
            }
        }
        catch ( IOException e )
        {
            logger.error( "Unable to get OEM Hms Event properties file" );
            processOemData = false;
        }
        if ( processOemData )
        {
            try
            {
                oemEventConfigFileCount = new Integer( prop.getProperty( "hms.eventList.oem.configuration.count" ) );
                // Ensure only one OEM event config file is specified. If more than one is specified,
                // log a warning and process only the first config file
                if ( oemEventConfigFileCount > 1 )
                {
                    logger.warn( "More than one OEM event config file specified. Only the first config file will be processed." );
                }
                oemEventConfigFile = prop.getProperty( "hms.eventList.oem.configuration.1" );
                if ( oemEventConfigFile != null )
                {
                    logger.info( "OEM event configuration file specified: " + oemEventConfigFile );
                    logger.info( "Adding oem events" );
                    if ( !addEvents( oemEventConfigFile ) )
                    {
                        logger.error( "Unable to initialize HmsEventMapper with OEM event list" );
                    }
                }
                else
                {
                    logger.info( "No OEM event configuration file specified." );
                }
            }
            catch ( NullPointerException e )
            {
                // OEM's are not expected to provide a configuration file unless required to map OEM sensors
                logger.info( "No OEM event mapper configuration file specified" );
            }
            catch ( NumberFormatException e )
            {
                logger.warn( "Incorrect OEM config file counter. Not processing OEM events." );
            }
        }
    }

    /**
     * Returns the singleton instance of the HmsEventMapper class.
     * <p>
     * If the instance is not created yet, a new instance is created and returned back to the caller.
     *
     * @return Singleton instance of the HmsEventMapper class
     */
    public static HmsEventMapper getInstance()
    {
        // Return a singleton object of the class
        if ( hmsMapper == null )
        {
            hmsMapper = new HmsEventMapper();
        }
        return hmsMapper;
    }

    private static String getJsonData( String filePath )
    {
        String configData = "";
        try
        {
            String curLine;
            InputStream is = HmsEventMapper.class.getClassLoader().getResourceAsStream( filePath );
            BufferedReader configReader = new BufferedReader( new InputStreamReader( is ) );
            while ( ( curLine = configReader.readLine() ) != null )
            {
                configData += curLine;
            }
        }
        catch ( IOException e )
        {
            logger.error( "Unable to load data from config file. Cannot proceed." );
        }
        return configData;
    }

    private static NodeEvent[] getNodeEvents( JsonNode node, int nodeCount )
    {
        ArrayList<NodeEvent> nodeEvents = new ArrayList<>();
        // For a given offset get all the listed node events
        ArrayNode nodeEventList = (ArrayNode) node.get( "nodeEventList" + nodeCount );
        for ( JsonNode nodeEvent : nodeEventList )
        {
            nodeEvents.add( getNodeEventFromName( nodeEvent.asText() ) );
        }
        NodeEvent[] nodeEventArray = new NodeEvent[nodeEvents.size()];
        return nodeEvents.toArray( nodeEventArray );
    }

    private static synchronized Boolean addEvents( String filePath )
    {
        JsonNode jNode = null;
        try
        {
            jNode = JsonUtils.getJsonTreeFromJsonString( getJsonData( filePath ) );
            ArrayNode eventList = null;
            JsonNode events = jNode.get( "events" );
            // All entries in the configuration file are represented as entries of the event array
            if ( !events.isArray() )
            {
                logger.error( "Badly formed configuration file" );
                return false;
            }
            else
            {
                logger.debug( "Found number of events: " + events.size() );
                eventList = (ArrayNode) events;
            }
            for ( JsonNode eventNode : eventList )
            {
                NodeEvent[] nodeEvents;
                int sensorCode, nodeCount, eventCode;
                String nodeEventName;
                logger.debug( "Processing eventNode" );
                // For each event array entry, get the eventType, sensorType and the number of event lists
                // based on the available offsets.
                eventCode = eventNode.get( "eventType" ).intValue();
                sensorCode = eventNode.get( "sensorType" ).intValue();
                nodeCount = eventNode.get( "nodeEventListCount" ).intValue();
                for ( int offset = 0; offset < nodeCount; offset++ )
                {
                    // Apart from the node events, get the names as well, as the callers
                    // require names of the node events as well
                    nodeEventName = eventNode.get( "nodeEventName" + offset ).asText();
                    nodeEvents = getNodeEvents( eventNode, offset );
                    addToHmsMapper( sensorCode, eventCode, offset, nodeEventName, nodeEvents );
                }
            }
        }
        catch ( Exception e )
        {
            logger.error( "Failed to read data from configuration file or configuration file missing" );
            return false;
        }
        logger.info( "Successfully loaded configuration from " + filePath );
        return true;
    }

    private static synchronized void addToHmsMapper( int sensorCode, int eventReadingCode, int offset,
                                                     String nodeEventName, NodeEvent[] events )
    {
        // Encode and map the eventCode to a given list of node events.
        int eventCode = encode( sensorCode, eventReadingCode, offset );
        logger.debug( "Adding following NodeEvents for eventName with encoded value: " + nodeEventName + ":"
            + Arrays.toString( events ) + ":" + eventCode );
        hmsNodeEventNameMap.put( eventCode, nodeEventName );
        hmsNodeEventListMap.put( eventCode, events );
    }

    private static int encode( int sensorCode, int eventReadingCode, int offset )
    {
        int value;
        if ( eventReadingCode == 0x6f && sensorCode <= 0x4 )
        {
            offset = 0;
        }
        value = ( eventReadingCode << 8 ) | offset;
        if ( eventReadingCode == 0x6f )
        { // sensor-specific reading type
            value |= sensorCode << 16;
        }
        return value;
    }

    /**
     * Provides a list of NodeEvents corresponding to any give triplet of IPMI sensor, eventReading type and the
     * corresponding error offset as mapped in the configuration file.
     *
     * @param sensorType Represents one of the pre-defined IPMI sensor types
     * @param eventReadingType Represents one of the pre-defined IPMI event reading types
     * @param offset Represents the offset matching the error condition for a given sensor
     * @return List of applicable NodeEvents as mapped in the configuration file
     */
    public static synchronized NodeEvent[] getHmsNodeEventList( SensorType sensorType, int eventReadingType,
                                                                int offset )
    {
        logger.debug( "Looking up NodeEvents for sensor: " + sensorType.getCode() + ", eventReadingType: "
            + eventReadingType + ", offset: " + offset );
        return getHmsNodeEventList( encode( sensorType.getCode(), eventReadingType, offset ) );
    }

    /**
     * Given a nodeEventCode representing the sensorType, eventReadingType and offset triplet, this function returns the
     * corresponding list of NodeEvents as mapped out in the configuration file.
     *
     * @param nodeEventCode Encoded integer value of the sensorType, eventReadingType and offset triplet
     * @return List of applicable NodeEvents as mapped in the configuration file
     */
    public static synchronized NodeEvent[] getHmsNodeEventList( int nodeEventCode )
    {
        NodeEvent[] mappedList;
        logger.debug( "Looking up NodeEvents for: " + nodeEventCode );
        mappedList = hmsNodeEventListMap.get( nodeEventCode );
        logger.debug( "Found NodeEvents: " + mappedList );
        return mappedList;
    }

    /**
     * Given a nodeEventCode representing the sensorType, eventReadingType and offset triplet, this function returns the
     * corresponding NodeEvent name as mapped out in the configuration file.
     *
     * @param nodeEventCode Encoded integer value of the sensorType, eventReadingType and offset triplet
     * @return NodeEvent name as mapped in the configuration file
     */
    public static synchronized String getHmsNodeEventName( int nodeEventCode )
    {
        String nodeEventName;
        logger.debug( "Looking up NodeEventName for: " + nodeEventCode );
        try
        {
            nodeEventName = new String( hmsNodeEventNameMap.get( nodeEventCode ) );
        }
        catch ( NullPointerException e )
        {
            logger.warn( "No Node event name found for code: " + nodeEventCode );
            nodeEventName = null;
        }
        logger.debug( "Found NodeEventName: " + nodeEventName );
        return nodeEventName;
    }

    /**
     * Provides NodeEvent name corresponding to any give triplet of IPMI sensor, eventReading type and the corresponding
     * error offset as mapped in the configuration file.
     *
     * @param sensorType Represents one of the pre-defined IPMI sensor types
     * @param eventReadingType Represents one of the pre-defined IPMI event reading types
     * @param offset Represents the offset matching the error condition for a given sensor
     * @return The applicable NodeEvent name as mapped in the configuration file
     */
    public static synchronized String getHmsNodeEventName( SensorType sensorType, int eventReadingType, int offset )
    {
        logger.debug( "Looking up NodeEventName for sensor: " + sensorType.getCode() + ", eventReadingType: "
            + eventReadingType + ", offset: " + offset );
        return getHmsNodeEventName( encode( sensorType.getCode(), eventReadingType, offset ) );
    }

    private static NodeEvent getNodeEventFromName( String nodeEventName )
    {
        // Given a node event name, return the appropriate node event code.
        // This mapping needs to be updated as and when new NodeEvents are added.
        switch ( nodeEventName )
        {
            case "CPU_TEMP_ABOVE_THRESHHOLD":
                return NodeEvent.CPU_TEMP_ABOVE_THRESHHOLD;
            case "CPU_TEMP_BELOW_THRESHHOLD":
                return NodeEvent.CPU_TEMP_BELOW_THRESHHOLD;
            case "CPU_THERMAL_TRIP":
                return NodeEvent.CPU_THERMAL_TRIP;
            case "CPU_CAT_ERROR":
                return NodeEvent.CPU_CAT_ERROR;
            case "CPU_INIT_ERROR":
                return NodeEvent.CPU_INIT_ERROR;
            case "CPU_MACHINE_CHECK_ERROR":
                return NodeEvent.CPU_MACHINE_CHECK_ERROR;
            case "CPU_POST_FAILURE":
                return NodeEvent.CPU_POST_FAILURE;
            case "CPU_TEMPERATURE":
                return NodeEvent.CPU_TEMPERATURE;
            case "CPU_VOLTS":
                return NodeEvent.CPU_VOLTS;
            case "CPU_STATUS":
                return NodeEvent.CPU_STATUS;
            case "CPU_FAILURE":
                return NodeEvent.CPU_FAILURE;
            case "PCH_TEMP_ABOVE_THRESHOLD":
                return NodeEvent.PCH_TEMP_ABOVE_THRESHOLD;
            case "MEMORY_TEMP_ABOVE_THRESHOLD":
                return NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD;
            case "MEMORY_THEMAL_MARGIN_CRITICAL_THRESHOLD":
                return NodeEvent.MEMORY_THEMAL_MARGIN_CRITICAL_THRESHOLD;
            case "MEMORY_ECC_ERROR":
                return NodeEvent.MEMORY_ECC_ERROR;
            case "MEMORY_TEMPERATURE":
                return NodeEvent.MEMORY_TEMPERATURE;
            case "MEMORY_STATUS":
                return NodeEvent.MEMORY_STATUS;
            case "MEMORY_FAILURE":
                return NodeEvent.MEMORY_FAILURE;
            case "HDD_DOWN":
                return NodeEvent.HDD_DOWN;
            case "HDD_FAILURE":
                return NodeEvent.HDD_FAILURE;
            case "HDD_STATUS":
                return NodeEvent.HDD_STATUS;
            case "HDD_WRITE_ERROR":
                return NodeEvent.HDD_WRITE_ERROR;
            case "HDD_READ_ERROR":
                return NodeEvent.HDD_READ_ERROR;
            case "HDD_TEMP_ABOVE_THRESHOLD":
                return NodeEvent.HDD_TEMP_ABOVE_THRESHOLD;
            case "HDD_WEAROUT_ABOVE_THRESHOLD":
                return NodeEvent.HDD_WEAROUT_ABOVE_THRESHOLD;
            case "HDD_HEALTH_CRITICAL":
                return NodeEvent.HDD_HEALTH_CRITICAL;
            case "HDD_EMPTY_DISK_BAY":
                return NodeEvent.HDD_EMPTY_DISK_BAY;
            case "HDD_SLOT_EMPTY":
                return NodeEvent.HDD_SLOT_EMPTY;
            case "HDD_SLOT_FULL":
                return NodeEvent.HDD_SLOT_FULL;
            case "FAN_SPEED":
                return NodeEvent.FAN_SPEED;
            case "FAN_FAILURE":
                return NodeEvent.FAN_FAILURE;
            case "FAN_STATUS_NON_RECOVERABLE":
                return NodeEvent.FAN_STATUS_NON_RECOVERABLE;
            case "FAN_SPEED_THRESHHOLD":
                return NodeEvent.FAN_SPEED_THRESHHOLD;
            case "POWERUNIT_TEMP_ABOVE_THRESHOLD":
                return NodeEvent.POWERUNIT_TEMP_ABOVE_THRESHOLD;
            case "POWER_UNIT_STATUS_FAILURE":
                return NodeEvent.POWER_UNIT_STATUS_FAILURE;
            case "POWER_UNIT_STATUS":
                return NodeEvent.POWER_UNIT_STATUS;
            case "BMC_NOT_REACHABLE":
                return NodeEvent.BMC_NOT_REACHABLE;
            case "BMC_FAILURE":
                return NodeEvent.BMC_FAILURE;
            case "BMC_AUTHENTICATION_FAILURE":
                return NodeEvent.BMC_AUTHENTICATION_FAILURE;
            case "BMC_STATUS":
                return NodeEvent.BMC_STATUS;
            case "SYSTEM_PCIE_ERROR":
                return NodeEvent.SYSTEM_PCIE_ERROR;
            case "SYSTEM_POST_ERROR":
                return NodeEvent.SYSTEM_POST_ERROR;
            case "SYSTEM_REBOOT":
                return NodeEvent.SYSTEM_REBOOT;
            case "SYSTEM_POWERUP_FAILURE":
                return NodeEvent.SYSTEM_POWERUP_FAILURE;
            case "SYSTEM_SET_BOOT_ORDER_FAILURE":
                return NodeEvent.SYSTEM_SET_BOOT_ORDER_FAILURE;
            case "SYSTEM_OS_BOOTUP_FAILURE":
                return NodeEvent.SYSTEM_OS_BOOTUP_FAILURE;
            case "SYSTEM_STATUS":
                return NodeEvent.SYSTEM_STATUS;
            case "NIC_LINK_DOWN":
                return NodeEvent.NIC_LINK_DOWN;
            case "NIC_PORT_DOWN":
                return NodeEvent.NIC_PORT_DOWN;
            case "NIC_PACKET_DROP_ABOVE_THRESHHOLD":
                return NodeEvent.NIC_PACKET_DROP_ABOVE_THRESHHOLD;
            case "NIC_TEMPERATURE_ABOVE_THRESHHOLD":
                return NodeEvent.NIC_TEMPERATURE_ABOVE_THRESHHOLD;
            case "NIC_PACKET_TRANSFER_RATE":
                return NodeEvent.NIC_PACKET_TRANSFER_RATE;
            case "NIC_TEMPERATURE":
                return NodeEvent.NIC_TEMPERATURE;
            case "MANAGEMENT_SWITCH_DOWN":
                return NodeEvent.MANAGEMENT_SWITCH_DOWN;
            case "MANAGEMENT_SWITCH_UP":
                return NodeEvent.MANAGEMENT_SWITCH_UP;
            case "TOR_SWITCH_DOWN":
                return NodeEvent.TOR_SWITCH_DOWN;
            case "TOR_SWITCH_UP":
                return NodeEvent.TOR_SWITCH_UP;
            case "SPINE_SWITCH_DOWN":
                return NodeEvent.SPINE_SWITCH_DOWN;
            case "SPINE_SWITCH_UP":
                return NodeEvent.SPINE_SWITCH_UP;
            case "MANAGEMENT_SWITCH_PORT_DOWN":
                return NodeEvent.MANAGEMENT_SWITCH_PORT_DOWN;
            case "MANAGEMENT_SWITCH_PORT_UP":
                return NodeEvent.MANAGEMENT_SWITCH_PORT_UP;
            case "TOR_SWITCH_PORT_DOWN":
                return NodeEvent.TOR_SWITCH_PORT_DOWN;
            case "TOR_SWITCH_PORT_UP":
                return NodeEvent.TOR_SWITCH_PORT_UP;
            case "SPINE_SWITCH_PORT_DOWN":
                return NodeEvent.SPINE_SWITCH_PORT_DOWN;
            case "SPINE_SWITCH_PORT_UP":
                return NodeEvent.SPINE_SWITCH_PORT_UP;
            case "HMS_AGENT_NON_RESPONSIVE":
                return NodeEvent.HMS_AGENT_NON_RESPONSIVE;
            case "HMS_AGENT_CPU_STATUS":
                return NodeEvent.HMS_AGENT_CPU_STATUS;
            case "HMS_AGENT_MEMORY_STATUS":
                return NodeEvent.HMS_AGENT_MEMORY_STATUS;
            case "HMS_AGENT_THREAD_COUNT":
                return NodeEvent.HMS_AGENT_THREAD_COUNT;
            case "HMS_OOB_AGENT_RESTHANDLER_MEAN_RESPONSETIME":
                return NodeEvent.HMS_OOB_AGENT_RESTHANDLER_MEAN_RESPONSETIME;
            case "HMS_OOB_AGENT_RESTHANDLER_STATUS":
                return NodeEvent.HMS_OOB_AGENT_RESTHANDLER_STATUS;
            case "HMS_OOB_AGENT_RESTHANDLER_STARTED_DURATION":
                return NodeEvent.HMS_OOB_AGENT_RESTHANDLER_STARTED_DURATION;
            case "HMS_OOB_AGENT_RESTHANDLER_MESSAGE_OUT_COUNT":
                return NodeEvent.HMS_OOB_AGENT_RESTHANDLER_MESSAGE_OUT_COUNT;
            case "HMS_OOB_AGENT_RESTHANDLER_MESSAGE_IN_COUNT":
                return NodeEvent.HMS_OOB_AGENT_RESTHANDLER_MESSAGE_IN_COUNT;
            case "HOST_OS_NOT_RESPONSIVE":
                return NodeEvent.HOST_OS_NOT_RESPONSIVE;
            case "HOST_UP":
                return NodeEvent.HOST_UP;
            case "HOST_DOWN":
                return NodeEvent.HOST_DOWN;
            default:
                logger.warn( "Unknown Node event listed: " + nodeEventName );
                return NodeEvent.INVALID;
        }
    }
}
