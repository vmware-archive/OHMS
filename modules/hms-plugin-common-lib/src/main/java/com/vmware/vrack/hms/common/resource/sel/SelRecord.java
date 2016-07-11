/* ********************************************************************************
 * SelRecord.java
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
package com.vmware.vrack.hms.common.resource.sel;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.resource.fru.SensorType;

/**
 * @author VMware, Inc.
 */
@JsonInclude( JsonInclude.Include.NON_NULL )
public class SelRecord
{
    private int recordId;

    private SelRecordType recordType;

    private Date timestamp;

    /**
     * {@link SensorType} code for sensor that generated the event
     */
    private SensorType sensorType;

    /**
     * Number of sensor that generated the event
     */
    private int sensorNumber;

    private EventDirection eventDirection;

    private ReadingType event;

    /**
     * Reading that triggered event. Provided in raw value (need to do {@link FullSensorRecord#calcFormula(int)}). Only
     * for threshold sensors.
     */
    private byte reading;

    /*
     * public static SelRecord populateSelRecord(byte[] data) { SelRecord record = new SelRecord(); byte[] buffer = new
     * byte[4]; buffer[0] = data[0]; buffer[1] = data[1]; buffer[2] = 0; buffer[3] = 0;
     * record.setRecordId(TypeConverter.littleEndianByteArrayToInt(buffer));
     * record.setRecordType(SelRecordType.parseInt(TypeConverter.byteToInt(data[2]))); System.arraycopy(data, 3, buffer,
     * 0, 4); record.setTimestamp(TypeConverter.decodeDate(TypeConverter.littleEndianByteArrayToInt(buffer)));
     * record.setSensorType(SensorType.parseInt(TypeConverter.byteToInt(data[10])));
     * record.setSensorNumber(TypeConverter.byteToInt(data[11]));
     * record.setEventDirection(EventDirection.parseInt((TypeConverter.byteToInt(data[12]) & 0x80) >> 7)); int eventType
     * = TypeConverter.byteToInt(data[12]) & 0x7f; int eventOffset = TypeConverter.byteToInt(data[13]) & 0xf;
     * record.setEvent(ReadingType.parseInt(record.getSensorType(), eventType, eventOffset));
     * record.setReading(data[14]); return record; }
     */
    public void setRecordId( int recordId )
    {
        this.recordId = recordId;
    }

    public int getRecordId()
    {
        return recordId;
    }

    public SelRecordType getRecordType()
    {
        return recordType;
    }

    public void setRecordType( SelRecordType recordType )
    {
        this.recordType = recordType;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public SensorType getSensorType()
    {
        return sensorType;
    }

    public void setSensorType( SensorType sensorType )
    {
        this.sensorType = sensorType;
    }

    public int getSensorNumber()
    {
        return sensorNumber;
    }

    public void setSensorNumber( int sensorNumber )
    {
        this.sensorNumber = sensorNumber;
    }

    public EventDirection getEventDirection()
    {
        return eventDirection;
    }

    public void setEventDirection( EventDirection eventDirection )
    {
        this.eventDirection = eventDirection;
    }

    public ReadingType getEvent()
    {
        return event;
    }

    public void setEvent( ReadingType event )
    {
        this.event = event;
    }

    /**
     * Reading that triggered event. Provided in raw value (need to do {@link FullSensorRecord#calcFormula(int)}). Only
     * for threshold sensors.
     */
    public byte getReading()
    {
        return reading;
    }

    public void setReading( byte reading )
    {
        this.reading = reading;
    }
}
