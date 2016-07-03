/* Copyright 2015 VMware, Inc. All rights reserved. */

package com.vmware.vrack.quanta.d51b.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.ipmiservice.IpmiService;
import com.vmware.vrack.quanta.d51b.parsers.BoardSensorNumber;
import com.vmware.vrack.quanta.d51b.parsers.ServerComponentSensorParsers;

/**
 * Helper Class for quanta ODM board D51B System sensor
 * @author VMware Inc.
 *
 */
public class SystemSensorHelper {

	private static Logger logger = Logger.getLogger(SystemSensorHelper.class);

    /**
     * method to get System Sensor sensor
     * @param ServiceHmsNode serviceNode
     * @return List<ServerComponentSensor>
     * @throws HmsException
     */
	public static List<ServerComponentEvent> getSystemSensor(ServiceHmsNode serviceNode, IpmiService ipmiServiceExecutor) throws HmsException
	{
	    if(serviceNode != null && serviceNode instanceof ServiceServerNode)
		{
			ServiceServerNode node = (ServiceServerNode) serviceNode;
			List<ServerComponentEvent> serverComponentSensor =  new ArrayList<>();
			List<Integer> listSensorNumber = new ArrayList<Integer>();
			List<Map<String, String>> sensorData = new ArrayList<>();
			ServerComponentSensorParsers serverComponentSensorParsers = new ServerComponentSensorParsers();

			try
			{

				for (BoardSensorNumber boardSensorNumber: BoardSensorNumber.values())
				{
					if ((BoardSensorNumber.sensorTypeSystem(boardSensorNumber.getCode())) != BoardSensorNumber.Invalid)
					{
						listSensorNumber.add(boardSensorNumber.getCode());
					}
				}
				sensorData = ipmiServiceExecutor.getSensorData(node, listSensorNumber);
				serverComponentSensor = serverComponentSensorParsers.getServerComponentSensor(sensorData);
				return serverComponentSensor;
			}
			catch(Exception e)
			{
				logger.error("Cannot get system sensor Info", e);
				throw new HmsException("Unable to get System Sensor information", e);
			}
		}
		else
		{
			throw new HmsException("Node is Null or invalid");
		}
	}

}
