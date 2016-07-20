/* Copyright © 2014 VMware, Inc. All rights reserved. */
package com.vmware.vrack.hms.switches.cumulus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.HmsSensorState;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo.ChassisTemp;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo.FanSpeed;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo.PsuStatus;
import com.vmware.vrack.hms.common.util.JsonUtils;
import com.vmware.vrack.hms.common.util.SshExecResult;

/**
 * Provides functionality for sensor apis.
 * 
 * This class provides functionality on sensor information such as get Sensor Info and get component sensor list.
 */
public class CumulusSensorHelper {
	
	/**
	 * Constructor for class CumulusSensorHelper
	 * 
	 * Cumulus Sensor helper class constructor; sets the service for the CumulusSensorHelper object.
	 * 
	 * @param service CumulusTorSwitchService object
	 */
	public CumulusSensorHelper(CumulusTorSwitchService service) {
		this.service = service;
	}

	/**
	 * Gets the switch sensor info from the switch node
	 * 
	 * Using the switch session, get a list of all sensor items. 
	 *  Iterate through the items ( chassis temp, fan speeds, and PSU status ), and set the corresponding values.
	 * 
	 * @param switchNode switch node object
	 * @return Return object containing all sensor information (chassis temp list, fan speed list, and psu statuses)
	 * @throws HmsException get switch sensor info failed because sensor data not available.
	 */
	public SwitchSensorInfo getSwitchSensorInfo(SwitchNode switchNode) throws HmsException {
		String command = CumulusConstants.GET_SENSOR_INFO_COMMAND;
		CumulusTorSwitchSession switchSession = (CumulusTorSwitchSession) CumulusUtil.getSession(switchNode);
		
		SshExecResult result = switchSession.executeEnhanced(command);
		if (result == null || result.getExitCode() != 0) {
			logger.error("Error while fetching switch sensor data for switch " + switchNode.getSwitchId());
			return null;
		}
		
		SwitchSensorInfo sensorInfo = new SwitchSensorInfo();
		
		/* Output from smonctl command is going to be a json list of sensor items. */
		try {
			List<ChassisTemp> chassisTemps = null;
			List<PsuStatus> psuStatusList = null;
			List<FanSpeed> fanSpeeds = null;
			
			@SuppressWarnings("rawtypes")
			List<Map> sensorList = JsonUtils.getBeanCollectionFromJsonString(new String(result.getStdout()), Map.class);
			@SuppressWarnings("rawtypes")
			Iterator<Map> it = sensorList.iterator();

			/* Iterate through all sensor items */
			while (it.hasNext()) {
				Map<?,?> sensorItem = it.next();
				
				/* Set the timestamp value of when the sensor reading was taken. */
				sensorInfo.setTimestamp(((Integer) sensorItem.get("log_time")).longValue());
				
				String type = (String) sensorItem.get("type");
				switch (type) {
				case "temp":
					if (chassisTemps == null) {
						chassisTemps = new ArrayList<ChassisTemp>();
					}
					
					ChassisTemp chassisTemp = new ChassisTemp();
					String tempName = (String) sensorItem.get("name");
					chassisTemp.setTempName(tempName);
					chassisTemp.setTempId(Integer.valueOf(tempName.substring(4)));
					chassisTemp.setUnit(EventUnitType.DEGREES_CELSIUS);
					chassisTemp.setStatus("OK".equals(sensorItem.get("state")) ? HmsSensorState.Ok : HmsSensorState.Invalid);
					
					/* Value parameter may not have the same prefix as the name */
					String vkName = tempName.toLowerCase() + "_input";
					if (sensorItem.containsKey(vkName)) {
						chassisTemp.setValue(((Double) sensorItem.get(vkName)).floatValue());
					} else {
						for (Object o : sensorItem.keySet()) {
							if (((String) o).endsWith("_input")) {
								vkName = (String) o;
								chassisTemp.setValue(((Double) sensorItem.get(vkName)).floatValue());
								break;
							}
						}
					}
					
					chassisTemps.add(chassisTemp);
					break;
					
				case "power":
					if (psuStatusList == null) {
						psuStatusList = new ArrayList<PsuStatus>();
					}
					
					PsuStatus psuStatus = new PsuStatus();
					String psuName = (String) sensorItem.get("name");
					psuStatus.setPsuName(psuName);
					psuStatus.setPsuId(Integer.valueOf(psuName.substring(3)));
					psuStatus.setStatus("OK".equals(sensorItem.get("state")) ? HmsSensorState.Ok : HmsSensorState.Invalid);
					
					psuStatusList.add(psuStatus);
					break;
					
				case "fan":
					if (fanSpeeds == null) {
						fanSpeeds = new ArrayList<FanSpeed>();
					}
					
					FanSpeed fanSpeed = new FanSpeed();
					String fanName = (String) sensorItem.get("name");
					fanSpeed.setFanName(fanName);
					fanSpeed.setFanId(Integer.valueOf(fanName.substring(3)));
					fanSpeed.setUnit(EventUnitType.RPM);
					fanSpeed.setStatus("OK".equals(sensorItem.get("state")) ? HmsSensorState.Ok : HmsSensorState.Invalid);

					/* Value parameter may not have the same prefix as the name */
					vkName = fanName.toLowerCase() + "_input";
					if (sensorItem.containsKey(vkName)) {
						fanSpeed.setValue((Integer) sensorItem.get(vkName));
					} else {
						for (Object o : sensorItem.keySet()) {
							if (((String) o).endsWith("_input")) {
								vkName = (String) o;
								fanSpeed.setValue((Integer) sensorItem.get(vkName));
								break;
							}
						}
					}
					
					fanSpeeds.add(fanSpeed);
					break;
					
				case "board":
					/* Board sensor information currently not needed. */
					break;
					
				default:
					logger.warn("Skipping unknown sensor type " + type);
					break;
				}
			}
			
			sensorInfo.setChassisTemps(chassisTemps);
			sensorInfo.setFanSpeeds(fanSpeeds);
			sensorInfo.setPsuStatus(psuStatusList);
			
		} catch (Exception e) {
			logger.error("Exception received while fetching switch sensor data", e);
		}
		
		return (sensorInfo);
	}
	
	/**
	 * Get the entire sensor list (component sensor list)
	 * 
	 * Using the service node (chassis, fanspeed, psu status), get the list of those sensors.
	 *  Converts all types into list of Server Component Sensor type
	 * 
	 * @param serviceNode service node corresponding to a switch node instance
	 * @param component server component 
	 * @return List of ServerComponentEvents. This includes all list of components for the Switch: Fan Speeds, Chassis Temps, and Psu Status
	 * @throws HmsException if request for fan speed, chassis temp, or psu status failed
	 */
	public List<ServerComponentEvent> getComponentSensorList(ServiceHmsNode serviceNode, ServerComponent component)
			throws HmsException {
		logger.debug("Gathering component sensor information on switch " + serviceNode.getNodeID());
		
		List<ServerComponentEvent> list = new ArrayList<ServerComponentEvent>();
		SwitchSensorInfo sensorInfo = getSwitchSensorInfo(new SwitchNode(serviceNode));

		/* Convert fan speeds into component sensors. */
		if (sensorInfo.getFanSpeeds() != null) {
			for (FanSpeed fanSpeed : sensorInfo.getFanSpeeds()) {
				list.add(fanSpeed.toServerComponentSensor());
			}
		}

		/* Convert chassis temps into component sensors. */
		if (sensorInfo.getChassisTemps() != null) {
			for (ChassisTemp temp : sensorInfo.getChassisTemps()) {
				list.add(temp.toServerComponentSensor());
			}
		}

		/* Convert PSU status into component sensors. */
		if (sensorInfo.getPsuStatus() != null) {
			for (PsuStatus psu : sensorInfo.getPsuStatus()) {
				list.add(psu.toServerComponentSensor());
			}
		}
		
		return list;
	}
	
	@SuppressWarnings("unused")
	/** Instantiated Cumulus Switch Service object */
	private CumulusTorSwitchService service;
    private static Logger logger = Logger.getLogger(CumulusSensorHelper.class);
}
