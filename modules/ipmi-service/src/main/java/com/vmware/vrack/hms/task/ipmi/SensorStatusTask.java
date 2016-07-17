/* ********************************************************************************
 * SensorStatusTask.java
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
package com.vmware.vrack.hms.task.ipmi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.api.async.ConnectionHandle;
import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.sdr.GetSdr;
import com.veraxsystems.vxipmi.coding.commands.sdr.GetSdrResponseData;
import com.veraxsystems.vxipmi.coding.commands.sdr.GetSensorReading;
import com.veraxsystems.vxipmi.coding.commands.sdr.GetSensorReadingResponseData;
import com.veraxsystems.vxipmi.coding.commands.sdr.ReserveSdrRepository;
import com.veraxsystems.vxipmi.coding.commands.sdr.ReserveSdrRepositoryResponseData;
import com.veraxsystems.vxipmi.coding.commands.sdr.SensorState;
import com.veraxsystems.vxipmi.coding.commands.sdr.record.CompactSensorRecord;
import com.veraxsystems.vxipmi.coding.commands.sdr.record.FullSensorRecord;
import com.veraxsystems.vxipmi.coding.commands.sdr.record.ReadingType;
import com.veraxsystems.vxipmi.coding.commands.sdr.record.SensorRecord;
import com.veraxsystems.vxipmi.coding.payload.CompletionCode;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.veraxsystems.vxipmi.common.TypeConverter;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.fru.EntityId;
import com.vmware.vrack.hms.common.resource.fru.SensorType;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;

/**
 * Sensor Status Task to get Sesor Information via BMC
 *
 * @author Vmware
 */
public class SensorStatusTask
{
    private static Logger logger = Logger.getLogger( SensorStatusTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in Sensor Status Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    ServiceServerNode node = null;

    IpmiTaskConnector connector = null;

    // private ArrayList<Object> sensorData = new ArrayList<Object>();

    /**
     * This is the value of Last Record ID (FFFFh). In order to retrieve the full set of SDR records, client must repeat
     * reading SDR records until MAX_REPO_RECORD_ID is returned as next record ID. For further information see section
     * 33.12 of the IPMI specification ver. 2.0
     */
    private static final int MAX_REPO_RECORD_ID = 65535;

    /**
     * Size of the initial GetSdr message to get record header and size
     */
    private int INITIAL_CHUNK_SIZE = 8;

    /**
     * Chunk size depending on buffer size of the IPMI server. Bigger values will improve performance. If server is
     * returning "Cannot return number of requested data bytes." error during GetSdr command, CHUNK_SIZE should be
     * decreased.
     */
    private int CHUNK_SIZE = 255;

    /**
     * Size of SDR record header
     */
    private int HEADER_SIZE = 5;

    private int nextRecId;

    public SensorStatusTask( ServiceHmsNode node )
    {
        this.node = (ServiceServerNode) node;
        /*
         * try { // Change default timeout value PropertiesManager.getInstance().setProperty("timeout", "2500"); } catch
         * (Exception e) { logger.error("Error while setting timeout property for getting Sensor Status for node [ " +
         * node.getNodeID() + " ]", e); }
         */
    }

    public SensorStatusTask( ServiceHmsNode node, IpmiTaskConnector connector )
    {
        this( node );
        this.connector = connector;
    }

    /**
     * @param headerSize
     * @param initialChunkSize
     * @param chunkSize
     */
    public void setChunkSizes( Integer headerSize, Integer initialChunkSize, Integer chunkSize )
    {
        if ( headerSize != null )
        {
            this.HEADER_SIZE = headerSize;
        }
        if ( initialChunkSize != null )
        {
            this.INITIAL_CHUNK_SIZE = initialChunkSize;
        }
        if ( chunkSize != null )
        {
            this.CHUNK_SIZE = chunkSize;
        }
    }

    public List<Map<String, String>> executeTask( List<Integer> listSensorNumber )
        throws Exception
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request to execute ipmi SensorStatus task for Node " + node.getNodeID() );
            List<Map<String, String>> result = null;
            if ( node.getManagementIp() == null )
            {
                return result;
            }
            result = getAllSensorData( listSensorNumber );
            return result;
        }
        else
        {
            String err =
                String.format( NULL_CONNECTOR_EXCEPTION_MSG, node, ( node instanceof ServiceServerNode ) ? "" : "NOT ",
                               connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    public List<Map<String, String>> executeTask( List<SensorType> typeList, List<EntityId> entityList )
        throws Exception
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request to execute ipmi SensorStatus task for Node " + node.getNodeID() );
            List<Map<String, String>> result = null;
            if ( node.getManagementIp() == null )
            {
                return result;
            }
            result = getAllSensorData( typeList, entityList );
            return result;
        }
        else
        {
            String err =
                String.format( NULL_CONNECTOR_EXCEPTION_MSG, node, ( node instanceof ServiceServerNode ) ? "" : "NOT ",
                               connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /*
     * This method gets the sensor data record list, which is map of <string, string>
     * @param SensorType - Sensor Type, Processor, Temperature etc
     * @param EntityId - Sensor Entity, Processor, Memory etc..
     * @return List<Map<String, String>> The sensor return data contains Sensor name, Sensor number, Reading (for
     * threshold based), Unit, Sensor Type, Entity ID, state of the sensor and State Byte Code State - Can have more
     * than sensor state. StateByteCode - Contains state byte codes of the sensor. StateByteCode can have more than one
     * state byte code as sensor can have more than one state For example (discrete sensor): If the StateByteCode is
     * 880384 (0x0d6f00) - Drive Presence 0D - Sensor Type Code (Drive Slot/Bay) 6f - Sensor Specific Reading type 00 -
     * Sensor specific Offset (Drive Presence) If StateByteCode (for discrete sensor) has no value meaning none of the
     * state is active - All are Deasserted For example (threshold sensor) : If the StateByteCode is 8 -
     * "AboveUpperNonCritical" StateByteCode=0 (for threshold sensor) meaning state is "OK"
     */
    private List<Map<String, String>> getAllSensorData( List<SensorType> typeList, List<EntityId> entityList )
        throws Exception, IpmiServiceResponseException
    {
        // Clear all sensor data from Node
        List<Map<String, String>> sensorData = new ArrayList<Map<String, String>>();
        // Id 0 indicates first record in SDR. Next IDs can be retrieved from
        // records - they are organized in a list and there is no BMC command to
        // get all of them.
        nextRecId = 0;
        // Some BMCs allow getting sensor records without reservation, so we try
        // to do it that way first
        int reservationId = 0;
        int lastReservationId = -1;
        ConnectionHandle connectionHandle = connector.getHandle();
        // Change timeout of this particular connection (default value for
        // further connections does not change)
        connector.getConnector().setTimeout( connectionHandle, 2750 );
        SensorRecord record = null;
        String sensorName = null;
        Double sensorReading = null;
        String sensorUnit = null;
        String entityInstanceId = null;
        HashMap<String, String> sensorResult = null;
        EntityId entityId = null;
        SensorType sensorType = null;
        SensorState sensorState = null;
        String discreteSensorState = null;
        String discreteSensorStateByteCode = null;
        Integer discreteSensorflag = 0;
        Integer sensorStateByteCode = 0;
        // We get sensor data until we encounter ID = 65535 which means that
        // this record is the last one.
        while ( nextRecId < MAX_REPO_RECORD_ID )
        {
            sensorResult = new HashMap<String, String>();
            record = null;
            sensorName = null;
            sensorReading = null;
            sensorUnit = null;
            sensorType = null;
            entityId = null;
            sensorState = null;
            try
            {
                // Populate the sensor record and get ID of the next record in
                // repository (see #getSensorData for details).
                record = getSensorData( reservationId );
                int recordReadingId = -1;
                // We check if the received record is either FullSensorRecord or
                // CompactSensorRecord, since these types have readings
                // associated with them (see IPMI specification for details).
                if ( record instanceof FullSensorRecord )
                {
                    FullSensorRecord fsr = (FullSensorRecord) record;
                    recordReadingId = TypeConverter.byteToInt( fsr.getSensorNumber() );
                    sensorName = fsr.getName();
                    sensorType = SensorType.parseInt( fsr.getSensorType().getCode() );
                    entityId = EntityId.parseInt( fsr.getEntityId().getCode() );
                    entityInstanceId = String.valueOf( TypeConverter.byteToInt( fsr.getEntityInstanceNumber() ) );
                }
                else if ( record instanceof CompactSensorRecord )
                {
                    CompactSensorRecord csr = (CompactSensorRecord) record;
                    recordReadingId = TypeConverter.byteToInt( csr.getSensorNumber() );
                    sensorName = csr.getName();
                    sensorType = SensorType.parseInt( csr.getSensorType().getCode() );
                    entityId = EntityId.parseInt( csr.getEntityId().getCode() );
                    entityInstanceId = String.valueOf( TypeConverter.byteToInt( csr.getEntityInstanceNumber() ) );
                }
                // If our record has got a reading associated, we get request
                // for it
                GetSensorReadingResponseData data2 = null;
                try
                {
                    if ( ( sensorType != null && typeList.contains( sensorType ) && entityList == null )
                        || ( sensorType != null && typeList.contains( sensorType ) && entityList != null && entityList.contains( entityId ) ) )
                    {
                        data2 =
                            (GetSensorReadingResponseData) connector.getConnector().sendMessage( connectionHandle,
                                                                                                 new GetSensorReading(
                                                                                                                       IpmiVersion.V20,
                                                                                                                       connectionHandle.getCipherSuite(),
                                                                                                                       AuthenticationType.RMCPPlus,
                                                                                                                       recordReadingId ) );
                        if ( record instanceof FullSensorRecord )
                        {
                            discreteSensorflag = 0;
                            FullSensorRecord rec = (FullSensorRecord) record;
                            // Parse sensor reading using information retrieved
                            // from sensor record. See
                            // FullSensorRecord#calcFormula for details.
                            sensorReading = data2.getSensorReading( rec );
                            sensorUnit = rec.getSensorBaseUnit().toString();
                            sensorState = SensorState.parseInt( data2.getSensorState().getCode() );
                            sensorStateByteCode = data2.getSensorState().getCode();
                        }
                        if ( record instanceof CompactSensorRecord )
                        {
                            discreteSensorflag = 1;
                            CompactSensorRecord rec = (CompactSensorRecord) record;
                            // Get states asserted by the sensor
                            List<ReadingType> events =
                                data2.getStatesAsserted( rec.getSensorType(), rec.getEventReadingType() );
                            sensorUnit = rec.getSensorBaseUnit().toString();
                            discreteSensorState = "";
                            List<Integer> bytecodes =
                                data2.getByteCodeAsserted( rec.getSensorType(), rec.getEventReadingType() );
                            discreteSensorStateByteCode = "";
                            for ( int i = 0; i < events.size(); ++i )
                            {
                                discreteSensorState += events.get( i ) + " ";
                                discreteSensorStateByteCode += bytecodes.get( i ) + " ";
                            }
                        }
                        sensorResult.put( "sensorNumber", String.valueOf( recordReadingId ) );
                        sensorResult.put( "name", sensorName );
                        sensorResult.put( "sensorNumber", String.valueOf( recordReadingId ) );
                        sensorResult.put( "entityInstanceId", entityInstanceId );
                        if ( sensorReading != null )
                        {
                            sensorResult.put( "reading", String.valueOf( sensorReading ) );
                        }
                        else
                        {
                            sensorResult.put( "reading", "" );
                        }
                        if ( sensorUnit != null )
                        {
                            sensorResult.put( "unit", sensorUnit );
                        }
                        else
                        {
                            sensorResult.put( "unit", "Unspecified" );
                        }
                        if ( sensorType != null )
                        {
                            sensorResult.put( "sensorType", sensorType.toString() );
                        }
                        else
                        {
                            sensorResult.put( "sensorType", SensorType.OtherUnitsBasedSensor.toString() );
                        }
                        if ( entityId != null )
                        {
                            sensorResult.put( "entityId", entityId.toString() );
                        }
                        else
                        {
                            sensorResult.put( "entityId", EntityId.Unspecified.toString() );
                        }
                        if ( discreteSensorflag == 0 )
                        {
                            if ( sensorState != null )
                            {
                                sensorResult.put( "State", sensorState.toString() );
                            }
                            else
                            {
                                sensorResult.put( "State", SensorState.Invalid.toString() );
                            }
                            sensorResult.put( "StateByteCode", String.valueOf( sensorStateByteCode ) );
                        }
                        else
                        {
                            if ( discreteSensorState != "" )
                            {
                                sensorResult.put( "State", discreteSensorState );
                                sensorResult.put( "StateByteCode", discreteSensorStateByteCode );
                                discreteSensorflag = 0;
                            }
                        }
                        sensorData.add( sensorResult );
                    }
                }
                catch ( IPMIException e )
                {
                    if ( e.getCompletionCode() == CompletionCode.DataNotPresent )
                    {
                        // logger.debug(e.getMessage());
                    }
                    else
                    {
                        logger.error( "Exception while the task SensorStatus at getAllSensorData: "
                            + e.getCompletionCode() + ":" + e.getMessage() );
                        logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
                        throw new IpmiServiceResponseException( e.getCompletionCode() );
                    }
                }
            }
            catch ( IPMIException e )
            {
                // logger.debug("Getting new reservation ID");
                // logger.debug("156: " + e.getMessage());
                // If getting sensor data failed, we check if it already failed
                // with this reservation ID.
                if ( lastReservationId == reservationId )
                    throw new IpmiServiceResponseException( e.getCompletionCode() );
                lastReservationId = reservationId;
                // If the cause of the failure was canceling of the
                // reservation, we get new reservationId and retry. This can
                // happen many times during getting all sensors, since BMC can't
                // manage parallel sessions and invalidates old one if new one
                // appears.
                reservationId =
                    ( (ReserveSdrRepositoryResponseData) connector.getConnector().sendMessage( connectionHandle,
                                                                                               new ReserveSdrRepository(
                                                                                                                         IpmiVersion.V20,
                                                                                                                         connectionHandle.getCipherSuite(),
                                                                                                                         AuthenticationType.RMCPPlus ) ) ).getReservationId();
            }
        }
        return sensorData;
    }

    /*
     * This method gets the sensor data record list, which is map of <string, string>
     * @param listSensorNumber - Board Sensor Number
     * @return List<Map<String, String>> The sensor return data contains Sensor name, Sensor number, Reading (for
     * threshold based), Unit, Sensor Type, Entity ID, state of the sensor and State Byte Code State - Can have more
     * than sensor state. StateByteCode - Contains state byte codes of the sensor. StateByteCode can have more than one
     * state byte code as sensor can have more than one state For example (discrete sensor): If the StateByteCode is
     * 880384 (0x0d6f00) - Drive Presence 0D - Sensor Type Code (Drive Slot/Bay) 6f - Sensor Specific Reading type 00 -
     * Sensor specific Offset (Drive Presence) If StateByteCode (for discrete sensor) has no value meaning none of the
     * state is active - All are Deasserted For example (threshold sensor) : If the StateByteCode is 8 -
     * "AboveUpperNonCritical" StateByteCode=0 (for threshold sensor) meaning state is "OK"
     */
    private List<Map<String, String>> getAllSensorData( List<Integer> listSensorNumber )
        throws Exception, IpmiServiceResponseException
    {
        // Clear all sensor data from Node
        List<Map<String, String>> sensorData = new ArrayList<Map<String, String>>();
        // Id 0 indicates first record in SDR. Next IDs can be retrieved from
        // records - they are organized in a list and there is no BMC command to
        // get all of them.
        nextRecId = 0;
        // Some BMCs allow getting sensor records without reservation, so we try
        // to do it that way first
        int reservationId = 0;
        int lastReservationId = -1;
        ConnectionHandle connectionHandle = connector.getHandle();
        // Change timeout of this particular connection (default value for
        // further connections does not change)
        connector.getConnector().setTimeout( connectionHandle, 2750 );
        SensorRecord record = null;
        String sensorName = null;
        Double sensorReading = null;
        String sensorUnit = null;
        HashMap<String, String> sensorResult = null;
        EntityId entityId = null;
        SensorType sensorType = null;
        SensorState sensorState = null;
        String discreteSensorState = null;
        String discreteSensorStateByteCode = null;
        Integer discreteSensorflag = 0;
        Integer sensorStateByteCode = 0;
        // We get sensor data until we encounter ID = 65535 which means that
        // this record is the last one.
        while ( nextRecId < MAX_REPO_RECORD_ID )
        {
            sensorResult = new HashMap<String, String>();
            record = null;
            sensorName = null;
            sensorReading = null;
            sensorUnit = null;
            sensorType = null;
            entityId = null;
            sensorState = null;
            try
            {
                // Populate the sensor record and get ID of the next record in
                // repository (see #getSensorData for details).
                record = getSensorData( reservationId );
                int recordReadingId = -1;
                // We check if the received record is either FullSensorRecord or
                // CompactSensorRecord, since these types have readings
                // associated with them (see IPMI specification for details).
                if ( record instanceof FullSensorRecord )
                {
                    FullSensorRecord fsr = (FullSensorRecord) record;
                    recordReadingId = TypeConverter.byteToInt( fsr.getSensorNumber() );
                    sensorName = fsr.getName();
                    sensorType = SensorType.parseInt( fsr.getSensorType().getCode() );
                    entityId = EntityId.parseInt( fsr.getEntityId().getCode() );
                }
                else if ( record instanceof CompactSensorRecord )
                {
                    CompactSensorRecord csr = (CompactSensorRecord) record;
                    recordReadingId = TypeConverter.byteToInt( csr.getSensorNumber() );
                    sensorName = csr.getName();
                    sensorType = SensorType.parseInt( csr.getSensorType().getCode() );
                    entityId = EntityId.parseInt( csr.getEntityId().getCode() );
                }
                // If our record has got a reading associated, we get request
                // for it
                GetSensorReadingResponseData data2 = null;
                try
                {
                    for ( int j = 0; j < listSensorNumber.size(); j++ )
                    {
                        if ( listSensorNumber.get( j ) == recordReadingId )
                        {
                            data2 =
                                (GetSensorReadingResponseData) connector.getConnector().sendMessage( connectionHandle,
                                                                                                     new GetSensorReading(
                                                                                                                           IpmiVersion.V20,
                                                                                                                           connectionHandle.getCipherSuite(),
                                                                                                                           AuthenticationType.RMCPPlus,
                                                                                                                           recordReadingId ) );
                            if ( record instanceof FullSensorRecord )
                            {
                                FullSensorRecord rec = (FullSensorRecord) record;
                                // Parse sensor reading using information retrieved
                                // from sensor record. See
                                // FullSensorRecord#calcFormula for details.
                                sensorReading = data2.getSensorReading( rec );
                                sensorUnit = rec.getSensorBaseUnit().toString();
                                sensorState = SensorState.parseInt( data2.getSensorState().getCode() );
                                sensorStateByteCode = data2.getSensorState().getCode();
                            }
                            if ( record instanceof CompactSensorRecord )
                            {
                                discreteSensorflag = 1;
                                CompactSensorRecord rec = (CompactSensorRecord) record;
                                // Get states asserted by the sensor
                                List<ReadingType> events =
                                    data2.getStatesAsserted( rec.getSensorType(), rec.getEventReadingType() );
                                sensorUnit = rec.getSensorBaseUnit().toString();
                                discreteSensorState = "";
                                List<Integer> bytecodes =
                                    data2.getByteCodeAsserted( rec.getSensorType(), rec.getEventReadingType() );
                                discreteSensorStateByteCode = "";
                                for ( int i = 0; i < events.size(); ++i )
                                {
                                    discreteSensorState += events.get( i ) + " ";
                                    discreteSensorStateByteCode += bytecodes.get( i ) + " ";
                                }
                            }
                            break;
                        }
                    }
                }
                catch ( IPMIException e )
                {
                    if ( e.getCompletionCode() == CompletionCode.DataNotPresent )
                    {
                        // logger.debug(e.getMessage());
                    }
                    else
                    {
                        logger.error( "Exception while the task SensorStatus at getAllSensorData: "
                            + e.getCompletionCode() + ":" + e.getMessage() );
                        logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
                        throw new IpmiServiceResponseException( e.getCompletionCode() );
                    }
                }
                for ( int k = 0; k < listSensorNumber.size(); k++ )
                {
                    if ( listSensorNumber.get( k ) == recordReadingId )
                    {
                        if ( sensorName != null )
                        {
                            sensorResult.put( "sensorNumber", String.valueOf( recordReadingId ) );
                            sensorResult.put( "name", sensorName );
                            sensorResult.put( "sensorNumber", String.valueOf( recordReadingId ) );
                            if ( sensorReading != null )
                            {
                                sensorResult.put( "reading", String.valueOf( sensorReading ) );
                            }
                            else
                            {
                                sensorResult.put( "reading", "" );
                            }
                            if ( sensorUnit != null )
                            {
                                sensorResult.put( "unit", sensorUnit );
                            }
                            else
                            {
                                sensorResult.put( "unit", "Unspecified" );
                            }
                            if ( sensorType != null )
                            {
                                sensorResult.put( "sensorType", sensorType.toString() );
                            }
                            else
                            {
                                sensorResult.put( "sensorType", SensorType.OtherUnitsBasedSensor.toString() );
                            }
                            if ( entityId != null )
                            {
                                sensorResult.put( "entityId", entityId.toString() );
                            }
                            else
                            {
                                sensorResult.put( "entityId", EntityId.Unspecified.toString() );
                            }
                            if ( discreteSensorflag == 0 )
                            {
                                if ( sensorState != null )
                                {
                                    sensorResult.put( "State", sensorState.toString() );
                                }
                                else
                                {
                                    sensorResult.put( "State", SensorState.Invalid.toString() );
                                }
                                sensorResult.put( "StateByteCode", String.valueOf( sensorStateByteCode ) );
                            }
                            else
                            {
                                if ( discreteSensorState != "" )
                                {
                                    sensorResult.put( "State", discreteSensorState );
                                    discreteSensorflag = 0;
                                }
                                else
                                {
                                    // A signal is deasserted when in the inactive state
                                    sensorResult.put( "State", ReadingType.StateDeasserted.toString() );
                                    discreteSensorflag = 0;
                                }
                                // If StateByteCode has no value meaning none of the state is active - All are
                                // Deasserted
                                sensorResult.put( "StateByteCode", discreteSensorStateByteCode );
                            }
                            sensorData.add( sensorResult );
                        }
                        break;
                    }
                }
            }
            catch ( IPMIException e )
            {
                // logger.debug("Getting new reservation ID");
                // logger.debug("156: " + e.getMessage());
                // If getting sensor data failed, we check if it already failed
                // with this reservation ID.
                if ( lastReservationId == reservationId )
                    throw new IpmiServiceResponseException( e.getCompletionCode() );
                lastReservationId = reservationId;
                // If the cause of the failure was canceling of the
                // reservation, we get new reservationId and retry. This can
                // happen many times during getting all sensors, since BMC can't
                // manage parallel sessions and invalidates old one if new one
                // appears.
                reservationId =
                    ( (ReserveSdrRepositoryResponseData) connector.getConnector().sendMessage( connectionHandle,
                                                                                               new ReserveSdrRepository(
                                                                                                                         IpmiVersion.V20,
                                                                                                                         connectionHandle.getCipherSuite(),
                                                                                                                         AuthenticationType.RMCPPlus ) ) ).getReservationId();
            }
        }
        return sensorData;
    }

    public SensorRecord getSensorData( int reservationId )
        throws Exception, IpmiServiceResponseException
    {
        ConnectionHandle connectionHandle = connector.getHandle();
        connector.getConnector().setTimeout( connectionHandle, 2750 );
        try
        {
            // BMC capabilities are limited - that means that sometimes the
            // record size exceeds maximum size of the message. Since we don't
            // know what is the size of the record, we try to get
            // whole one first
            GetSdrResponseData data =
                (GetSdrResponseData) connector.getConnector().sendMessage( connectionHandle,
                                                                           new GetSdr(
                                                                                       IpmiVersion.V20,
                                                                                       connectionHandle.getCipherSuite(),
                                                                                       AuthenticationType.RMCPPlus,
                                                                                       reservationId, nextRecId ) );
            // If getting whole record succeeded we create SensorRecord from
            // received data...
            SensorRecord sensorDataToPopulate = SensorRecord.populateSensorRecord( data.getSensorRecordData() );
            // ... and update the ID of the next record
            nextRecId = data.getNextRecordId();
            return sensorDataToPopulate;
        }
        catch ( IPMIException e )
        {
            // logger.debug(e.getCompletionCode() + ": " + e.getMessage());
            // The following error codes mean that record is too large to be
            // sent in one chunk. This means we need to split the data in
            // smaller parts.
            if ( e.getCompletionCode() == CompletionCode.CannotRespond
                || e.getCompletionCode() == CompletionCode.UnspecifiedError )
            {
                // logger.debug("Getting chunks");
                // First we get the header of the record to find out its size.
                GetSdrResponseData data =
                    (GetSdrResponseData) connector.getConnector().sendMessage( connectionHandle,
                                                                               new GetSdr(
                                                                                           IpmiVersion.V20,
                                                                                           connectionHandle.getCipherSuite(),
                                                                                           AuthenticationType.RMCPPlus,
                                                                                           reservationId, nextRecId, 0,
                                                                                           INITIAL_CHUNK_SIZE ) );
                // The record size is 5th byte of the record. It does not take
                // into account the size of the header, so we need to add it.
                int recSize = TypeConverter.byteToInt( data.getSensorRecordData()[4] ) + HEADER_SIZE;
                int read = INITIAL_CHUNK_SIZE;
                byte[] result = new byte[recSize];
                System.arraycopy( data.getSensorRecordData(), 0, result, 0, data.getSensorRecordData().length );
                // We get the rest of the record in chunks (watch out for
                // exceeding the record size, since this will result in BMC's
                // error.
                while ( read < recSize )
                {
                    int bytesToRead = CHUNK_SIZE;
                    if ( recSize - read < bytesToRead )
                    {
                        bytesToRead = recSize - read;
                    }
                    GetSdrResponseData part =
                        (GetSdrResponseData) connector.getConnector().sendMessage( connectionHandle,
                                                                                   new GetSdr(
                                                                                               IpmiVersion.V20,
                                                                                               connectionHandle.getCipherSuite(),
                                                                                               AuthenticationType.RMCPPlus,
                                                                                               reservationId,
                                                                                               nextRecId, read,
                                                                                               bytesToRead ) );
                    System.arraycopy( part.getSensorRecordData(), 0, result, read, bytesToRead );
                    logger.debug( "Received part" );
                    read += bytesToRead;
                }
                // Finally we populate the sensor record with the gathered
                // data...
                SensorRecord sensorDataToPopulate = SensorRecord.populateSensorRecord( result );
                // ... and update the ID of the next record
                nextRecId = data.getNextRecordId();
                return sensorDataToPopulate;
            }
            else
            {
                logger.error( "Exception while the task SensorStatus at getSensorData: " + e.getCompletionCode() + ":"
                    + e.getMessage() );
                logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
                throw new IpmiServiceResponseException( e.getCompletionCode() );
            }
        }
        catch ( Exception e )
        {
            throw e;
        }
    }
}
