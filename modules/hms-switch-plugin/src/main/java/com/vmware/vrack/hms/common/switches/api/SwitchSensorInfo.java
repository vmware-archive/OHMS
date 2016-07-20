/*******************************************************************************
 * Copyright (c) 2014 VMware, Inc. All rights reserved.
 ******************************************************************************/

package com.vmware.vrack.hms.common.switches.api;

import java.util.List;

import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.HmsSensorState;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;

public class SwitchSensorInfo {
    private long timestamp;
    private List<FanSpeed> fanSpeeds;
    private List<ChassisTemp> chassisTemps;
    private List<PsuStatus> psuStatus;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<FanSpeed> getFanSpeeds() {
        return fanSpeeds;
    }

    public void setFanSpeeds(List<FanSpeed> fanSpeeds) {
        this.fanSpeeds = fanSpeeds;
    }

    public List<ChassisTemp> getChassisTemps() {
        return chassisTemps;
    }

    public void setChassisTemps(List<ChassisTemp> chassisTemps) {
        this.chassisTemps = chassisTemps;
    }

    public List<PsuStatus> getPsuStatus() {
        return psuStatus;
    }

    public void setPsuStatus(List<PsuStatus> psuStatus) {
        this.psuStatus = psuStatus;
    }

    public static class FanSpeed {
        private String fanName;
        private int fanId;
        private float value;
        private EventUnitType unit;
        private HmsSensorState status;

        public String getFanName() {
            return fanName;
        }
        public void setFanName(String fanName) {
            this.fanName = fanName;
        }
        public int getFanId() {
            return fanId;
        }
        public void setFanId(int fanId) {
            this.fanId = fanId;
        }
        public float getValue() {
            return value;
        }
        public void setValue(float value) {
            this.value = value;
        }
        public EventUnitType getUnit() {
            return unit;
        }
        public void setUnit(EventUnitType unit) {
            this.unit = unit;
        }
        public HmsSensorState getStatus() {
            return status;
        }
        public void setStatus(HmsSensorState status) {
            this.status = status;
        }

        public ServerComponentEvent toServerComponentSensor() {
            ServerComponentEvent sensor = new ServerComponentEvent();
            sensor.setComponentId(getFanName());
            sensor.setEventId(getFanName());
            // sensor.setEventName(NodeEvent.SWITCH_FAN_SPEED);
            sensor.setUnit(getUnit());
            sensor.setValue(getValue());
            return sensor;
        }
    }

    public static class ChassisTemp {
        private String tempName;
        private int tempId;
        private float value;
        private EventUnitType unit;
        private HmsSensorState status;

        public String getTempName() {
            return tempName;
        }
        public void setTempName(String tempName) {
            this.tempName = tempName;
        }
        public int getTempId() {
            return tempId;
        }
        public void setTempId(int tempId) {
            this.tempId = tempId;
        }
        public float getValue() {
            return value;
        }
        public void setValue(float value) {
            this.value = value;
        }
        public EventUnitType getUnit() {
            return unit;
        }
        public void setUnit(EventUnitType unit) {
            this.unit = unit;
        }
        public HmsSensorState getStatus() {
            return status;
        }
        public void setStatus(HmsSensorState status) {
            this.status = status;
        }
        public ServerComponentEvent toServerComponentSensor() {
            ServerComponentEvent sensor = new ServerComponentEvent();
            sensor.setComponentId(getTempName());
            sensor.setEventId(getTempName());
            // sensor.setEventName(NodeEvent.SWITCH_CHASSIS_TEMPERATURE);
            sensor.setUnit(getUnit());
            sensor.setValue(getValue());
            return sensor;
        }
    }

    public static class PsuStatus {
        private String psuName;
        private int psuId;
        private HmsSensorState status;

        public String getPsuName() {
            return psuName;
        }
        public void setPsuName(String psuName) {
            this.psuName = psuName;
        }
        public int getPsuId() {
            return psuId;
        }
        public void setPsuId(int psuId) {
            this.psuId = psuId;
        }
        public HmsSensorState getStatus() {
            return status;
        }
        public void setStatus(HmsSensorState status) {
            this.status = status;
        }
        public ServerComponentEvent toServerComponentSensor() {
            ServerComponentEvent sensor = new ServerComponentEvent();
            sensor.setComponentId(getPsuName());
            sensor.setEventId(getPsuName());
            // sensor.setEventName(NodeEvent.SWITCH_POWERUNIT_STATUS);
            sensor.setDiscreteValue(getStatus().toString());
            return sensor;
        }
    }
}
