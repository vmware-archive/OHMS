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
 * Helper Class for quanta ODM board D51B FAN sensor
 * @author VMware Inc.
 *
 */
public class FanSensorHelper {

	private static Logger logger = Logger.getLogger(FanSensorHelper.class);

    /**
     * method to get FAN sensor
     * @param ServiceHmsNode serviceNode
     * @return List<ServerComponentSensor>
     * @throws HmsException
     */
	public static List<ServerComponentEvent> getFanSensor(ServiceHmsNode serviceNode, IpmiService ipmiServiceExecutor) throws HmsException
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
					if ((BoardSensorNumber.sensorTypeFan(boardSensorNumber.getCode())) != BoardSensorNumber.Invalid)
					{
						listSensorNumber.add(boardSensorNumber.getCode());
					}
				}
				sensorData = ipmiServiceExecutor.getSensorData(node, listSensorNumber);
				serverComponentSensor = serverComponentSensorParsers.getServerComponentSensor(sensorData);

				for (int i=0; i<serverComponentSensor.size(); i++)
				{

					if (serverComponentSensor.get(i).getDiscreteValue() != null)
					{
						//Event: Critical non-recoverable FAN failure
						if (serverComponentSensor.get(i).getDiscreteValue().contains("BelowLowerNonRecoverable")
								|| serverComponentSensor.get(i).getDiscreteValue().contains("AboveUpperNonRecoverable"))
						{
							serverComponentSensor.get(i).setEventName(NodeEvent.FAN_STATUS_NON_RECOVERABLE);
						}
					}
				}

				return serverComponentSensor;
			}
			catch(Exception e)
			{
				logger.error("Cannot get Fan sensor Info", e);
				throw new HmsException("Unable to get Fan Sensor information", e);
			}
		}
		else
		{
			throw new HmsException("Node is Null or invalid");
		}
	}

}


