/* ********************************************************************************
 * IpmiSensorParser.java
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
package com.vmware.vrack.hms.common.boardvendorservice.api.helper.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.coding.commands.sdr.SensorState;
import com.vmware.vrack.hms.common.resource.fru.EntityId;
import com.vmware.vrack.hms.common.resource.fru.SensorType;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;

/**
 * Parser for ServerComponenets
 *
 * @author VMware Inc.
 */
public class IpmiSensorParser
{
    private static Logger logger = Logger.getLogger( IpmiSensorParser.class );

    public EventUnitType getSensorUnitType( String unit )
    {
        switch ( unit )
        {
            // @TODO: Please change the events as per the server board
            // CPU sensor
            case "DegreesC":
                return EventUnitType.DEGREES_CELSIUS;
            case "DegreesF":
                return EventUnitType.DEGREES_FAHRENHEIT;
            case "Volts":
                return EventUnitType.VOLTS;
            case "Rpm":
                return EventUnitType.RPM;
            default:
                return null;
        }
    }

    private NodeEvent getReadingSensorEvent( EntityId entity, SensorState state, SensorType sensorType,
                                             boolean thresholdEvents )
    {
        switch ( sensorType )
        {
            case Temperature:
            case Fan:
                if ( thresholdEvents )
                    return HMSTemperatureSensorEventUtil.getHMSEvent( state, entity );
                else
                    return HMSTemperatureSensorEventUtil.getTemperatureReadingEvents( entity, sensorType );
            default:
                return null;
        }
    }

    public List<ServerComponentEvent> getServerComponentSensor( List<Map<String, String>> sensorData )
        throws Exception
    {
        List<ServerComponentEvent> serverComponentSensorList = new ArrayList<>();
        ServerComponentEvent serverComponentSensor = null;
        try
        {
            HmsEventMapper mapperInstance = HmsEventMapper.getInstance();
            for ( int i = 0; i < sensorData.size(); i++ )
            {
                Map<String, String> data = sensorData.get( i );
                logger.debug( "sensorData   " + sensorData.get( i ) );
                // verify if state is discrete
                if ( data.containsKey( "reading" ) && data.get( "reading" ).equals( "" )
                    && data.containsKey( "StateByteCode" ) && !data.get( "StateByteCode" ).equals( "" ) )
                {
                    List<String> discreteStates = Arrays.asList( data.get( "StateByteCode" ).split( " " ) );
                    NodeEvent[] events = null;
                    for ( String stateCode : discreteStates )
                    {
                        events = mapperInstance.getHmsNodeEventList( Integer.parseInt( stateCode ) );
                        String state = mapperInstance.getHmsNodeEventName( Integer.parseInt( stateCode ) );
                        if ( events != null && events.length > 0 )
                            for ( int l = 0; l < events.length; l++ )
                            {
                                serverComponentSensor = getServerComponent( data );
                                serverComponentSensor.setDiscreteValue( state );
                                serverComponentSensor.setEventName( events[l] );
                                serverComponentSensorList.add( serverComponentSensor );
                            }
                    }
                }
                // verify if sensor is reading type and check for thresholds
                else if ( data.containsKey( "reading" ) && !data.get( "reading" ).equals( "" ) )
                {
                    EntityId entity = EntityId.valueOf( data.get( "entityId" ) );
                    SensorState state = SensorState.valueOf( data.get( "State" ) );
                    SensorType sensorType = SensorType.valueOf( data.get( "sensorType" ) );
                    serverComponentSensor = getServerComponent( data );
                    NodeEvent event = getReadingSensorEvent( entity, state, sensorType, false );
                    if ( event != null )
                    {
                        serverComponentSensor.setEventName( event );
                        serverComponentSensorList.add( serverComponentSensor );
                    }
                    if ( data.containsKey( "State" ) )
                    {
                        event = getReadingSensorEvent( entity, state, sensorType, true );
                        if ( event != null )
                        {
                            serverComponentSensor = getServerComponent( data );
                            serverComponentSensor.setDiscreteValue( data.get( "State" ) );
                            serverComponentSensor.setEventName( event );
                            serverComponentSensor.setUnit( EventUnitType.DISCRETE );
                            serverComponentSensorList.add( serverComponentSensor );
                        }
                    }
                }
            }
        }
        catch ( Exception e )
        {
            logger.error( "Cannot get getServerComponentSensor data", e );
            throw e;
        }
        return serverComponentSensorList;
    }

    private ServerComponentEvent getServerComponent( Map<String, String> sensorData )
    {
        ServerComponentEvent serverComponentEvent = new ServerComponentEvent();
        if ( sensorData.containsKey( "name" ) )
        {
            serverComponentEvent.setEventId( sensorData.get( "name" ) );
        }
        serverComponentEvent.setUnit( getSensorUnitType( sensorData.get( "unit" ) ) );
        serverComponentEvent.setComponentId( sensorData.get( "entityId" ) + "_"
            + sensorData.get( "entityInstanceId" ) );
        if ( sensorData.containsKey( "reading" ) && !sensorData.get( "reading" ).equals( "" ) )
        {
            serverComponentEvent.setValue( Float.parseFloat( sensorData.get( "reading" ) ) );
        }
        return serverComponentEvent;
    }
}
