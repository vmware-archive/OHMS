/* ********************************************************************************
 * NBSwitchSensorInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model.switches;

import java.util.List;

import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.HmsSensorState;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;

public class NBSwitchSensorInfo
{
    private long timestamp;

    /**
     * @return the timestamp
     */
    public long getTimestamp()
    {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp( long timestamp )
    {
        this.timestamp = timestamp;
    }

    /**
     * @return the fanSpeeds
     */
    public List<FanSpeed> getFanSpeeds()
    {
        return fanSpeeds;
    }

    /**
     * @param fanSpeeds the fanSpeeds to set
     */
    public void setFanSpeeds( List<FanSpeed> fanSpeeds )
    {
        this.fanSpeeds = fanSpeeds;
    }

    /**
     * @return the chassisTemps
     */
    public List<ChassisTemp> getChassisTemps()
    {
        return chassisTemps;
    }

    /**
     * @param chassisTemps the chassisTemps to set
     */
    public void setChassisTemps( List<ChassisTemp> chassisTemps )
    {
        this.chassisTemps = chassisTemps;
    }

    /**
     * @return the psuStatus
     */
    public List<PsuStatus> getPsuStatus()
    {
        return psuStatus;
    }

    /**
     * @param psuStatus the psuStatus to set
     */
    public void setPsuStatus( List<PsuStatus> psuStatus )
    {
        this.psuStatus = psuStatus;
    }

    private List<FanSpeed> fanSpeeds;

    private List<ChassisTemp> chassisTemps;

    private List<PsuStatus> psuStatus;

    public static class FanSpeed
    {
        private String fanName;

        private int fanId;

        private float value;

        private EventUnitType unit;

        private HmsSensorState status;

        /**
         * @return the fanName
         */
        public String getFanName()
        {
            return fanName;
        }

        /**
         * @param fanName the fanName to set
         */
        public void setFanName( String fanName )
        {
            this.fanName = fanName;
        }

        /**
         * @return the fanId
         */
        public int getFanId()
        {
            return fanId;
        }

        /**
         * @param fanId the fanId to set
         */
        public void setFanId( int fanId )
        {
            this.fanId = fanId;
        }

        /**
         * @return the value
         */
        public float getValue()
        {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue( float value )
        {
            this.value = value;
        }

        /**
         * @return the unit
         */
        public EventUnitType getUnit()
        {
            return unit;
        }

        /**
         * @param unit the unit to set
         */
        public void setUnit( EventUnitType unit )
        {
            this.unit = unit;
        }

        /**
         * @return the status
         */
        public HmsSensorState getStatus()
        {
            return status;
        }

        /**
         * @param status the status to set
         */
        public void setStatus( HmsSensorState status )
        {
            this.status = status;
        }
    };

    public static class ChassisTemp
    {
        private String tempName;

        private int tempId;

        private float value;

        private EventUnitType unit;

        private HmsSensorState status;

        /**
         * @return the tempName
         */
        public String getTempName()
        {
            return tempName;
        }

        /**
         * @param tempName the tempName to set
         */
        public void setTempName( String tempName )
        {
            this.tempName = tempName;
        }

        /**
         * @return the tempId
         */
        public int getTempId()
        {
            return tempId;
        }

        /**
         * @param tempId the tempId to set
         */
        public void setTempId( int tempId )
        {
            this.tempId = tempId;
        }

        /**
         * @return the value
         */
        public float getValue()
        {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue( float value )
        {
            this.value = value;
        }

        /**
         * @return the unit
         */
        public EventUnitType getUnit()
        {
            return unit;
        }

        /**
         * @param unit the unit to set
         */
        public void setUnit( EventUnitType unit )
        {
            this.unit = unit;
        }

        /**
         * @return the status
         */
        public HmsSensorState getStatus()
        {
            return status;
        }

        /**
         * @param status the status to set
         */
        public void setStatus( HmsSensorState status )
        {
            this.status = status;
        }
    };

    public static class PsuStatus
    {
        private String psuName;

        private int psuId;

        private HmsSensorState status;

        public String getPsuName()
        {
            return psuName;
        }

        public void setPsuName( String psuName )
        {
            this.psuName = psuName;
        }

        public int getPsuId()
        {
            return psuId;
        }

        public void setPsuId( int psuId )
        {
            this.psuId = psuId;
        }

        public HmsSensorState getStatus()
        {
            return status;
        }

        public void setStatus( HmsSensorState status )
        {
            this.status = status;
        }

        public ServerComponentEvent toServerComponentSensor()
        {
            ServerComponentEvent sensor = new ServerComponentEvent();
            sensor.setComponentId( getPsuName() );
            sensor.setEventId( getPsuName() );
            // sensor.setEventName(NodeEvent.SWITCH_POWERUNIT_STATUS);
            sensor.setDiscreteValue( getStatus().toString() );
            return sensor;
        }
    }
}
