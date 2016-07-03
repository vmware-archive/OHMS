/* Copyright 2015 VMware, Inc. All rights reserved. */

package com.vmware.vrack.quanta.d51b.parsers;

import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerComponentSensorParsers
{
    private static Logger logger = Logger.getLogger(ServerComponentSensorParsers.class);

    public NodeEvent getNodeSensor(String sensorName)
    {
        switch(sensorName)
        {
        //CPU Sensor
        case "Temp_CPU0":
        case "Temp_CPU1":
        case "Temp_VR_CPU0":
        case "Temp_VR_CPU1":
            return NodeEvent.CPU_TEMP_ABOVE_THRESHHOLD;
        case "CPU_0":
        case "CPU_1":
            return NodeEvent.CPU_FAILURE;
        case "Temp_PCH":
            return NodeEvent.PCH_TEMP_ABOVE_THRESHOLD;

            //Memory sensor
        case "Temp_DIMM_AB":
        case "Temp_DIMM_CD":
        case "Temp_DIMM_EF":
        case "Temp_DIMM_GH":
            case "Temp_VR_DIMM_AB":
            case "Temp_VR_DIMM_CD":
            case "Temp_VR_DIMM_EF":
            case "Temp_VR_DIMM_GH":
            return NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD;
        case "Memory Error":
            return NodeEvent.MEMORY_STATUS;
        case "DIMM_HOT":
            return NodeEvent.MEMORY_FAILURE;

            //FAN Sensor
        case "Fan_SYS0_1":
        case "Fan_SYS0_2":
        case "Fan_SYS1_1":
        case "Fan_SYS1_2":
        case "Fan_SYS2_1":
        case "Fan_SYS2_2":
        case "Fan_SYS3_1":
        case "Fan_SYS3_2":
        case "Fan_SYS4_1":
        case "Fan_SYS4_2":
        case "Fan_SYS5_1":
        case "Fan_SYS5_2":
            return NodeEvent.FAN_SPEED_THRESHHOLD;

            //HDD Sensor
        case "HDD0":
        case "HDD1":
        case "HDD2":
        case "HDD3":
        case "HDD4":
        case "HDD5":
        case "HDD6":
        case "HDD7":
        case "HDD8":
        case "HDD9":
            return NodeEvent.HDD_FAILURE;

            //Power Unit Sensor
        case "Power Unit":
            return NodeEvent.POWER_UNIT_STATUS;

            //Power Supply Sensor
        case "PSU1 Status":
        case "PSU2 Status":
            return NodeEvent.POWER_UNIT_STATUS_FAILURE;

            //System Sensor
        case "PCIE ERROR":
            return NodeEvent.SYSTEM_PCIE_ERROR;
        case "POST Error":
            return NodeEvent.SYSTEM_POST_ERROR;

        default:
            return null;
        }
    }

    public NodeEvent getNodeSensorInformational(String sensorName)
    {
        switch(sensorName)
        {
        //CPU Sensor
        case "Temp_CPU0":
        case "Temp_CPU1":
        case "Temp_VR_CPU0":
        case "Temp_VR_CPU1":
               return NodeEvent.CPU_TEMPERATURE;
        case "Temp_PCH":
            return NodeEvent.CPU_TEMPERATURE;
        case "CPU_0":
        case "CPU_1":
        case "CPU_DIMM_VRHOT":
            return NodeEvent.CPU_STATUS;

            //HDD Sensor
        case "HDD0":
        case "HDD1":
        case "HDD2":
        case "HDD3":
        case "HDD4":
        case "HDD5":
        case "HDD6":
        case "HDD7":
        case "HDD8":
        case "HDD9":
            return NodeEvent.HDD_STATUS;

            //Power Unit Sensor
        case "Power Unit":
            return NodeEvent.POWER_UNIT_STATUS;

            //Power Supply Sensor
        case "PSU1 Status":
        case "PSU2 Status":
            return NodeEvent.POWER_UNIT_STATUS;

            //FAN Sensor
        case "Fan_SYS0_1":
        case "Fan_SYS0_2":
        case "Fan_SYS1_1":
        case "Fan_SYS1_2":
        case "Fan_SYS2_1":
        case "Fan_SYS2_2":
        case "Fan_SYS3_1":
        case "Fan_SYS3_2":
        case "Fan_SYS4_1":
        case "Fan_SYS4_2":
        case "Fan_SYS5_1":
        case "Fan_SYS5_2":
            return NodeEvent.FAN_SPEED;

            //Memory sensor
        case "Temp_DIMM_AB":
        case "Temp_DIMM_CD":
        case "Temp_DIMM_EF":
        case "Temp_DIMM_GH":
        case "Temp_VR_DIMM_AB":
        case "Temp_VR_DIMM_CD":
        case "Temp_VR_DIMM_EF":
        case "Temp_VR_DIMM_GH":
            return NodeEvent.MEMORY_TEMPERATURE;
        case "Memory Error":
        case "DIMM_HOT":
            return NodeEvent.MEMORY_STATUS;

            //System Sensor
        case "PCIE ERROR":
            return NodeEvent.SYSTEM_STATUS;
        case "POST Error":
            return NodeEvent.SYSTEM_STATUS;

        default:
            return null;
        }
    }
    public EventUnitType getSensorUnitType(String sensorName)
    {
        switch(sensorName)
        {
        //CPU sensor
        case "Temp_CPU0":
        case "Temp_CPU1":
        case "Temp_VR_CPU0":
        case "Temp_VR_CPU1":
            return EventUnitType.DEGREES_CELSIUS;
        case "Temp_PCH":
            return EventUnitType.DEGREES_CELSIUS;
        case "CPU_0":
        case "CPU_1":
        case "CPU_DIMM_VRHOT":
            return EventUnitType.DISCRETE;

            //Memory Sensor
        case "Temp_DIMM_AB":
        case "Temp_DIMM_CD":
        case "Temp_DIMM_EF":
        case "Temp_DIMM_GH":
        case "Temp_VR_DIMM_AB":
        case "Temp_VR_DIMM_CD":
        case "Temp_VR_DIMM_EF":
        case "Temp_VR_DIMM_GH":
            return EventUnitType.DEGREES_CELSIUS;
        case "Memory Error":
        case "DIMM_HOT":
            return EventUnitType.DISCRETE;

            //FAN Sensor
        case "Fan_SYS0_1":
        case "Fan_SYS0_2":
        case "Fan_SYS1_1":
        case "Fan_SYS1_2":
        case "Fan_SYS2_1":
        case "Fan_SYS2_2":
        case "Fan_SYS3_1":
        case "Fan_SYS3_2":
        case "Fan_SYS4_1":
        case "Fan_SYS4_2":
        case "Fan_SYS5_1":
        case "Fan_SYS5_2":
            return EventUnitType.RPM;

            //HDD Sensor
        case "HDD0":
        case "HDD1":
        case "HDD2":
        case "HDD3":
        case "HDD4":
        case "HDD5":
        case "HDD6":
        case "HDD7":
        case "HDD8":
        case "HDD9":
            return EventUnitType.DISCRETE;

            //Power Unit Sensor
        case "Power Unit":
            return EventUnitType.DISCRETE;
            //Power Supply
        case "PSU1 Status":
        case "PSU2 Status":
            return EventUnitType.DISCRETE;
            //System Sensor
        case "PCIE ERROR":
            return EventUnitType.DISCRETE;
        case "POST Error":
            return EventUnitType.DISCRETE;

        default:
            return null;
        }
    }

    public String getComponentId(String sensorName)
    {
        switch(sensorName)
        {
        //CPU Sensor component ID
        case "Temp_CPU0":
        case "Temp_VR_CPU0":
            return "0";
        case "CPU_0":
            return "Processor 0";
        case "Temp_CPU1":
        case "Temp_VR_CPU1":
            return "1";
        case "CPU_1":
            return "Processor 1";
        case "Temp_PCH":
            return "Platform controller hub";
        case "CPU_DIMM_VRHOT":
            return "Processor Hot";

            //FAN Sensor component ID
        case "Fan_SYS0_1":
            return "FAN1-1";
        case "Fan_SYS0_2":
            return "FAN1-2";
        case "Fan_SYS1_1":
            return "FAN2-1";
        case "Fan_SYS1_2":
            return "FAN2-2";
        case "Fan_SYS2_1":
            return "FAN3-1";
        case "Fan_SYS2_2":
            return "FAN3-2";
        case "Fan_SYS3_1":
            return "FAN4-1";
        case "Fan_SYS3_2":
            return "FAN4-2";
        case "Fan_SYS4_1":
            return "FAN5-1";
        case "Fan_SYS4_2":
            return "FAN5-2";
        case "Fan_SYS5_1":
            return "FAN6-1";
        case "Fan_SYS5_2":
            return "FAN6-2";

            //HDD Sensor component ID
        case "HDD0":
            return "HDD_0";
        case "HDD1":
            return "HDD_1";
        case "HDD2":
            return "HDD_2";
        case "HDD3":
            return "HDD_3";
        case "HDD4":
            return "HDD_4";
        case "HDD5":
            return "HDD_5";
        case "HDD6":
            return "HDD_6";
        case "HDD7":
            return "HDD_7";
        case "HDD8":
            return "HDD_8";
        case "HDD9":
            return "HDD_9";

            //Power Unit Sensor component ID
        case "Power Unit":
            return "POWER_UNIT";
        case "PSU1 Status":
            return "Power Supply 0";
        case "PSU2 Status":
            return "Power Supply 1";

            //Memory Sensor component ID
        case "Memory Error":
            return "Memory Error";
        case "Temp_DIMM_AB":
            return "Temp_DIMM_AB";
        case "Temp_DIMM_CD":
            return "Temp_DIMM_CD";
        case "Temp_DIMM_EF":
            return "Temp_DIMM_EF";
        case "Temp_DIMM_GH":
            return "Temp_DIMM_GH";
        case "Temp_VR_DIMM_AB":
            return "Temp_VR_DIMM_AB";
        case "Temp_VR_DIMM_CD":
            return "Temp_VR_DIMM_CD";
        case "Temp_VR_DIMM_EF":
            return "Temp_VR_DIMM_EF";
        case "Temp_VR_DIMM_GH":
            return "Temp_VR_DIMM_GH";

            //System Sensor
        case "PCIE ERROR":
            return "PCIE ERROR";
        case "POST Error":
            return "POST Error";

        default:
            return null;
        }
    }

    public List<ServerComponentEvent> getServerComponentSensor (List<Map<String, String>> sensorData) throws Exception
    {
        List<ServerComponentEvent> serverComponentSensor =  new ArrayList<>();
        ServerComponentEvent serverComponentSensorTemp =  null;
        String state = null;
        String[] listState = null;

        try
        {
            for (int i=0; i<sensorData.size(); i++)
            {
                Map<String,String> data = sensorData.get(i);
                serverComponentSensorTemp = new ServerComponentEvent();

                if(data.containsKey("State"))
                {
                    state = data.get("State");
                }

                listState = null;
                listState = FailureCodeEventTrigger.getStates(state);

                for (int j=0; j < listState.length; j++)
                {
                    if (listState[j].equals(SuccessCodeSensorState.StateDeasserted.toString())
                            || listState[j].equals(FailureCodeEventTrigger.Invalid.toString())
                            || listState[j].equals(SuccessCodeSensorState.Ok.toString())
                            || listState[j].equals(SuccessCodeSensorState.DrivePresence.toString())
                            || listState[j].equals(SuccessCodeSensorState.ConsistencyOrParityCheckInProgress.toString())
                            || listState[j].equals(SuccessCodeSensorState.RebuildRemapInProgress.toString())
                            || listState[j].equals(SuccessCodeSensorState.PowerSupplyPresenceDetected.toString())
                            || listState[j].equals(SuccessCodeSensorState.ProcessorPresenceDetected.toString())
                            || listState[j].equals(SuccessCodeSensorState.TerminatorPresenceDetected.toString()))
                    {
                        serverComponentSensorTemp = new ServerComponentEvent();

                        if (data.containsKey("name"))
                        {
                            serverComponentSensorTemp.setEventId(data.get("name"));
                        }
                        serverComponentSensorTemp.setEventName(getNodeSensorInformational(data.get("name")));
                        serverComponentSensorTemp.setUnit(getSensorUnitType(data.get("name")));
                        serverComponentSensorTemp.setComponentId(getComponentId(data.get("name")));

                        if (getSensorUnitType(data.get("name")) != EventUnitType.DISCRETE)
                        {
                            if(data.containsKey("reading"))
                            {
                                serverComponentSensorTemp.setValue(Float.parseFloat(data.get("reading")));
                            }
                        }
                        if(data.containsKey("State"))
                        {
                            serverComponentSensorTemp.setDiscreteValue(data.get("State"));
                        }
                    }
                    else
                    {
                        serverComponentSensorTemp = new ServerComponentEvent();

                        if (data.containsKey("name"))
                        {
                            serverComponentSensorTemp.setEventId(data.get("name"));
                        }
                        serverComponentSensorTemp.setEventName(getNodeSensor(data.get("name")));
                        serverComponentSensorTemp.setUnit(getSensorUnitType(data.get("name")));
                        serverComponentSensorTemp.setComponentId(getComponentId(data.get("name")));

                        if (getSensorUnitType(data.get("name")) != EventUnitType.DISCRETE)
                        {
                            if(data.containsKey("reading"))
                            {
                                serverComponentSensorTemp.setValue(Float.parseFloat(data.get("reading")));
                            }
                        }
                        if(data.containsKey("State"))
                        {
                            serverComponentSensorTemp.setDiscreteValue(data.get("State"));
                        }
                        break;
                    }
                }
                serverComponentSensor.add(serverComponentSensorTemp);
            }
        }
        catch(Exception e)
        {
            logger.error("Cannot get getServerComponentSensor data", e);
            throw e;
        }
        return serverComponentSensor;
    }
}
