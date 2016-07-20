/* Copyright © 2014 VMware, Inc. All rights reserved. */
package com.vmware.vrack.hms.switches.cumulus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchSession;

/**
 * Provides functionality on the cumulus ports configuration.
 * 
 * This class provides functionality on the ports configuration such as get port speed and reload.
 */
public class CumulusPortsConf {
	
	/**
	 * Get the cumulus port configuration instance.
	 * 
	 * Using the switch node, return the cumulus port configuration object. 
	 * 
	 * @param switchNode switch node object
	 * @return the port configuration on the switch node
	 */
	public static synchronized CumulusPortsConf getInstance(SwitchNode switchNode) {
		if (!nodeCache.containsKey(switchNode)) {
			SwitchSession session = CumulusUtil.getSession(switchNode);
			CumulusPortsConf portsConf = new CumulusPortsConf(session);
			nodeCache.put(switchNode, portsConf);
		}
		return nodeCache.get(switchNode);
	}

	/**
	 * Constructor for the class - reload the switch session
	 * 
	 * @param torSwitchSession switch session object
	 */
	private CumulusPortsConf(SwitchSession torSwitchSession) {
		reload(torSwitchSession);
	}
	
	/**
	 * Used to reload the switch session object
	 * 
	 * Using the switch session object, get the ports configuration properties from the file - and set the properties.
	 * 
	 * @param torSwitchSession - switch session object
	 */
	private void reload(SwitchSession torSwitchSession) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			torSwitchSession.download(baos, CumulusConstants.PORTS_CONF_FILE);
			
			/* Read intermediate/raw properties from the file */
			Properties rawPortSpeedProperties = new Properties();
			rawPortSpeedProperties.load(new ByteArrayInputStream(baos.toByteArray()));
			
			for (String key : rawPortSpeedProperties.stringPropertyNames()) {
				portsConfProperties.setProperty("swp" + key, rawPortSpeedProperties.getProperty(key));
			}
		} catch (Exception e) {
			logger.error("Error encountered while scanning ports.conf file", e);
		}
	}
	
	/**
	 * Get the port speed
	 * 
	 * From the Properties object created from the properties file, get the port speed.
	 * 
	 * @param portName - port name to get the port speed for the switch session
	 * @return String of the port speed
	 */
	public String getPortSpeed (String portName) {
		return portsConfProperties.getProperty(portName);
	}

	/** Properties object that contains all port configuration details from the ports.conf file  */
	private Properties portsConfProperties = new Properties();
    private static Logger logger = Logger.getLogger(CumulusPortsConf.class);
    private static Map<SwitchNode,CumulusPortsConf> nodeCache = new HashMap<SwitchNode,CumulusPortsConf>();
}
