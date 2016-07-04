/* Copyright 2015 VMware, Inc. All rights reserved. */

package com.vmware.vrack.quanta.d51b.helper;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.ipmiservice.IpmiService;
import com.vmware.vrack.quanta.d51b.parsers.BoardSensorNumber;
import com.vmware.vrack.quanta.d51b.parsers.ServerComponentSensorParsers;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper Class for quanta ODM board D51B CPU sensor
 * @author VMware Inc.
 *
 */
public class CpuSensorHelper {

	private static Logger logger = Logger.getLogger(CpuSensorHelper.class);

    /**
     * method to get cpu sensor
     * @param String componentID
     * @return
     * @throws HmsException
     */
	public static List<ServerComponentEvent> getCpuSensor(ServiceHmsNode serviceNode, IpmiService ipmiServiceExecutor) throws HmsException
	{
	    if(serviceNode != null && serviceNode instanceof ServiceServerNode)
		{
			ServiceServerNode node = (ServiceServerNode) serviceNode;
			List<ServerComponentEvent> serverComponentSensor =  new ArrayList<>();
			List<Integer> listSensorNumber = new ArrayList<Integer>();
			List<Map<String, String>> sensorData = new ArrayList<>();
			ServerComponentSensorParsers serverComponentSensorParsers = new ServerComponentSensorParsers();
			List<Map<String, String>> cpuSensorData = new ArrayList<Map<String, String>>();

			try
			{
				for (BoardSensorNumber boardSensorNumber: BoardSensorNumber.values())
				{
					if ((BoardSensorNumber.processorSensor(boardSensorNumber.getCode())) != BoardSensorNumber.Invalid)
					{
						listSensorNumber.add(boardSensorNumber.getCode());
					}
				}
				sensorData = ipmiServiceExecutor.getSensorData(node, listSensorNumber);
				
				for (int i = 0; i < sensorData.size(); i++) {
                    Map<String, String> data = sensorData.get(i);

                    String sensorReading = data.get("reading");

                    // If the Threshold based sensor reading is 0.0 means Sensor "Not valid/Not Available"/"Not Present"
                    if (sensorReading != null && !sensorReading.equals("") && sensorReading.equals("0.0"))
                        continue;
                    cpuSensorData.add(data);
                }
                
				serverComponentSensor = serverComponentSensorParsers.getServerComponentSensor(cpuSensorData);

				for (int i=0; i<serverComponentSensor.size(); i++)
				{

					if (serverComponentSensor.get(i).getDiscreteValue() != null)
					{
						//Processor Thermal Trip
						if (serverComponentSensor.get(i).getDiscreteValue().contains("ProcessorThermalTrip"))
						{
							serverComponentSensor.get(i).setEventName(NodeEvent.CPU_THERMAL_TRIP);
						}

						//Processor Startup/Initialization failure (CPU didn’t start)
						if (serverComponentSensor.get(i).getDiscreteValue().contains("Frb3ProcessorStartupFailure"))
						{
							serverComponentSensor.get(i).setEventName(NodeEvent.CPU_INIT_ERROR);
						}

						//Hang in POST failure - Processor POST failure
						if (serverComponentSensor.get(i).getDiscreteValue().contains("Frb2HangInPostFailure"))
						{
							serverComponentSensor.get(i).setEventName(NodeEvent.CPU_POST_FAILURE);
						}

						//Event: MCERR - Processor machine check error
						if (serverComponentSensor.get(i).getDiscreteValue().contains("MachineCheckException"))
						{
							serverComponentSensor.get(i).setEventName(NodeEvent.CPU_MACHINE_CHECK_ERROR);
						}

						//Event: IERR (Internal Error) and MCERR (machine check error) - Processor Catastrophic error
						if (serverComponentSensor.get(i).getDiscreteValue().contains("Ierr")
								|| serverComponentSensor.get(i).getDiscreteValue().contains("MachineCheckException"))
						{
							serverComponentSensor.get(i).setEventName(NodeEvent.CPU_CAT_ERROR);
						}

						//Event: CPU temperature is below the lower operating threshold.
						if (serverComponentSensor.get(i).getDiscreteValue().contains("BelowLowerNonRecoverable")
								|| serverComponentSensor.get(i).getDiscreteValue().contains("BelowLowerNonCritical")
								|| serverComponentSensor.get(i).getDiscreteValue().contains("BelowLowerCritical"))
						{
							serverComponentSensor.get(i).setEventName(NodeEvent.CPU_TEMP_BELOW_THRESHHOLD);
						}
					}
				}

				return serverComponentSensor;
			}
			catch(Exception e)
			{
				logger.error("Cannot get cpu sensor Info", e);
				throw new HmsException("Unable to get CPU Sensor information", e);
			}
		}
		else
		{
			throw new HmsException("Node is Null or invalid");
		}
	}

}

