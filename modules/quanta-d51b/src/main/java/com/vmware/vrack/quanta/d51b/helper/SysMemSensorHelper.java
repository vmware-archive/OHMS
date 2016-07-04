/* Copyright 2015 VMware, Inc. All rights reserved. */

package com.vmware.vrack.quanta.d51b.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.ipmiservice.IpmiService;
import com.vmware.vrack.quanta.d51b.parsers.BoardSensorNumber;
import com.vmware.vrack.quanta.d51b.parsers.ServerComponentSensorParsers;

/**
 * Helper Class for quanta ODM board D51B Memory sensor
 * @author VMware Inc.
 *
 */
public class SysMemSensorHelper {

	private static Logger logger = Logger.getLogger(SysMemSensorHelper.class);

    /**
     * method to get Memory sensor
     * @param String componentID
     * @return List<ServerComponentSensor>
     * @throws HmsException
     */
	public static List<ServerComponentEvent> getSysMemSensor(ServiceHmsNode serviceNode, IpmiService ipmiServiceExecutor) throws HmsException
	{
	    if(serviceNode != null && serviceNode instanceof ServiceServerNode)
		{
			ServiceServerNode node = (ServiceServerNode) serviceNode;
			List<ServerComponentEvent> serverComponentSensor =  new ArrayList<>();
			List<Integer> listSensorNumber = new ArrayList<Integer>();
			List<Map<String, String>> sensorData = new ArrayList<>();
			ServerComponentSensorParsers serverComponentSensorParsers = new ServerComponentSensorParsers();
			List<Map<String, String>> memorySensorData = new ArrayList<Map<String, String>>();
			String sensorReading;

			try
			{

				for (BoardSensorNumber boardSensorNumber: BoardSensorNumber.values())
				{
					if ((BoardSensorNumber.memorySensor(boardSensorNumber.getCode())) != BoardSensorNumber.Invalid)
					{
						listSensorNumber.add(boardSensorNumber.getCode());
					}
				}
				sensorData = ipmiServiceExecutor.getSensorData(node, listSensorNumber);

				for (int i=0; i<sensorData.size(); i++)
				{
					Map<String,String> data = sensorData.get(i);

					sensorReading = data.get("reading");

					//If the Threshold based sensor reading is 0.0 means Sensor "Not Available"/"Not Present"
					if (sensorReading.equals("0.0"))
					{
						continue;
					}
					memorySensorData.add(data);
				}

				serverComponentSensor = serverComponentSensorParsers.getServerComponentSensor(memorySensorData);

				for (int i=0; i<serverComponentSensor.size(); i++)
				{
					if (serverComponentSensor.get(i).getDiscreteValue() != null)
					{
						//Uncorrectable ECC / uncorrectable memory error
						if (serverComponentSensor.get(i).getDiscreteValue().contains("UncorrectableECC"))
						{
							serverComponentSensor.get(i).setEventName(NodeEvent.MEMORY_ECC_ERROR);
						}
					}
				}

				return serverComponentSensor;
			}
			catch(Exception e)
			{
				logger.error("Cannot get Sys Mem sensor Info", e);
				throw new HmsException("Unable to get System Memory Sensor information", e);
			}
		}
		else
		{
			throw new HmsException("Node is Null or invalid");
		}
	}

}
