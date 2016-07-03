/* Copyright 2015 VMware, Inc. All rights reserved. */

package com.vmware.vrack.quanta.d51b.helper;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.ipmiservice.IpmiService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper Class for quanta ODM board D51B BMC sensor
 * @author VMware Inc.
 *
 */
public class BmcSensorHelper {

	private static Logger logger = Logger.getLogger(BmcSensorHelper.class);

    /**
     * method to get BMC sensor
     *
     * @param String componentID
     * @return
     * @throws HmsException
     */
	public static List<ServerComponentEvent> getBmcSensor(ServiceHmsNode serviceNode, IpmiService ipmiServiceExecutor) throws HmsException
	{
		if(serviceNode != null && serviceNode instanceof ServiceServerNode)
		{
			ServiceServerNode node = (ServiceServerNode) serviceNode;
			List<ServerComponentEvent> serverComponentSensor =  new ArrayList<>();
			ServerComponentEvent serverComponentSensorTemp =  null;

			try
			{

				try
				{
					boolean status = ipmiServiceExecutor.isHostAvailable(node);
					if (status != true) // Not able to authenticate to HOST
					{
						serverComponentSensorTemp = new ServerComponentEvent();

						serverComponentSensorTemp.setEventId("BMC AUTHENTICATION");
						serverComponentSensorTemp.setEventName(NodeEvent.BMC_AUTHENTICATION_FAILURE);
						serverComponentSensorTemp.setUnit(EventUnitType.DISCRETE);
						serverComponentSensorTemp.setComponentId(node.getManagementIp());
						serverComponentSensorTemp.setDiscreteValue("BMC_AUTHENTICATION_FAILURE");

						serverComponentSensor.add(serverComponentSensorTemp);
					}
					else // Authentication Success, Try Management Operations
					{
						try
						{
							ipmiServiceExecutor.getServerPowerStatus(node);
						}
						catch(Exception e)
						{
							logger.debug("error getting node power status for node "+node.getNodeID(),e);
							serverComponentSensorTemp = new ServerComponentEvent();

							serverComponentSensorTemp.setEventId("BMC MANAGEMENT STATUS");
							serverComponentSensorTemp.setEventName(NodeEvent.BMC_FAILURE);
							serverComponentSensorTemp.setUnit(EventUnitType.DISCRETE);
							serverComponentSensorTemp.setComponentId(node.getManagementIp());
							serverComponentSensorTemp.setDiscreteValue("BMC_MANAGEMENT_FAILURE");

							serverComponentSensor.add(serverComponentSensorTemp);
						}
					}
				}
				catch(Exception e) // Exception while connecting to host
				{
					logger.debug("error getting node connection "+node.getNodeID(),e);
					serverComponentSensorTemp = new ServerComponentEvent();

					serverComponentSensorTemp.setEventId("BMC STATUS");
					serverComponentSensorTemp.setEventName(NodeEvent.BMC_NOT_REACHABLE);
					serverComponentSensorTemp.setUnit(EventUnitType.DISCRETE);
					serverComponentSensorTemp.setComponentId(node.getManagementIp());
					serverComponentSensorTemp.setDiscreteValue("BMC_NOT_REACHABLE");

					serverComponentSensor.add(serverComponentSensorTemp);
				}

				return serverComponentSensor;
			}
			catch(Exception e)
			{
				logger.error("Cannot get BMC sensor Info", e);
				throw new HmsException("Unable to BMC CPU Sensor information", e);
			}
		}
		else
		{
			throw new HmsException("Node is Null or invalid");
		}
	}

}
