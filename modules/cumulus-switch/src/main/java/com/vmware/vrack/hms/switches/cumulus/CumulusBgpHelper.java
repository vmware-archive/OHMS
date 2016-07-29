/* ********************************************************************************
 * CumulusBgpHelper.java
 *
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.switches.cumulus;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.SshExecResult;

/**
 * Implements BGP (Border Gateway Protocol) - a routing protocol.
 * 
 * This class serves as the base BGP for a cumulus switch, and will be used to configure, enable or disable bgp.
 */
public class CumulusBgpHelper {
	/**
     * Bgp Helper class.
     * 
     * Sets the Bgp service provided for the CumulusTorSwitchService object created.
     *
     * @param service CumulusTorSwitchService object
     */
	public CumulusBgpHelper(CumulusTorSwitchService service) {
		this.service = service;
	}
	
	/**
     * Configure BGP (enable or disable).
     * 
     * Enable or Disable BGP configuration for the cumulus switch session.
     *
     * @param switchNode information about the switch (including ip, username/password, protocol etc).
     * @param bgp BGP configuration
     * @exception HmsException if BGP configuration parameter is null
     */
	public boolean configureBgp(SwitchNode switchNode, SwitchBgpConfig bgp) throws HmsException {
		if (bgp == null)
			throw new HmsException ("BGP configuration parameter cannot be null.");
		else if (bgp.isEnabled())
			return enableBgp(switchNode, bgp);
		else
			return disableBgp(switchNode, bgp);
	}
	
	/**
     * Get the BGP configuration/protocol.
     * 
     * Get the BGP configuration for the cumulus switch session.
     *
     * @param switchNode information about the switch (including ip, username/password, protocol etc).
     */
	public SwitchBgpConfig getBgpConfiguration(SwitchNode switchNode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
     * Enable BGP.
     * 
     * Enable BGP configuration for the cumulus switch session.
     *
     * @param switchNode information about the switch (including ip, username/password, protocol etc).
     * @param bgp BGP configuration
     * @exception HmsException if the CumulusTorSwitchSession failed to enable the BGP on the switch. 
     */
	private boolean enableBgp(SwitchNode switchNode, SwitchBgpConfig bgp) throws HmsException {
		if (bgp.getExportedNetworks() == null || bgp.getExportedNetworks().isEmpty()) {
			throw new HmsException ("Invalid list of networks exported");
		}
		
		/* 1. Start BGP daemon via Quagga */
		String startDaemonCommand = CumulusConstants.ENABLE_BGP_COMMAND
				.replaceAll("\\{password\\}", CumulusUtil.qr(switchNode.getPassword()));

		CumulusTorSwitchSession session = (CumulusTorSwitchSession) CumulusUtil.getSession(switchNode);
		SshExecResult result = session.executeEnhanced(startDaemonCommand);
		result.log(logger, Level.DEBUG);
		result.logIfError(logger);
		
		if (result.getExitCode() != 0) {
			throw new HmsException ("Failed to enable BGP on switch " + switchNode.getSwitchId());
		}
			
		/* 2. Configure BGP parameters */
		StringBuilder exportNetworksCommands = new StringBuilder("");
		for (String network : bgp.getExportedNetworks()) {
			String command = CumulusConstants.EXPORT_NETWORK_COMMAND
					.replaceAll("\\{network\\}", network)
					.replaceAll("\\{localAsn\\}", Integer.toString(bgp.getLocalAsn()));
			
			exportNetworksCommands.append(command);
		}
		
		String configureBgpCommand = CumulusConstants.CONFIGURE_BGP_COMMAND
				.replaceAll("\\{password\\}", CumulusUtil.qr(switchNode.getPassword()))
				.replaceAll("\\{localAsn\\}", Integer.toString(bgp.getLocalAsn()))
				.replaceAll("\\{peerAsn\\}", Integer.toString(bgp.getPeerAsn()))
				.replaceAll("\\{peerIp\\}", bgp.getPeerIpAddress())
				.replaceAll("\\{exportNetworksCommands\\}", exportNetworksCommands.toString());
		
		SshExecResult result2 = session.executeEnhanced(configureBgpCommand);
		result2.log(logger, Level.DEBUG);
		result2.logIfError(logger);
		
		if (result2.getExitCode() != 0) {
			throw new HmsException ("Failed to configure BGP on switch " + switchNode.getSwitchId());
		}
			
		CumulusUtil.configurePersistenceDirectory(switchNode);
		
		return true;
	}
	
	/**
     * Disable BGP.
     * 
     * Disable BGP configuration for the cumulus switch session.
     *
     * @param switchNode information about the switch (including ip, username/password, protocol etc).
     * @param bgp BGP configuration
     * @exception HmsException if CumulusTorSwitchSession failed to disable BGP on the switch
     */
	private boolean disableBgp(SwitchNode switchNode, SwitchBgpConfig bgp) throws HmsException {
		/* Stop BGP daemon via Quagga */
		String startDaemonCommand = CumulusConstants.DISABLE_BGP_COMMAND
				.replaceAll("\\{password\\}", CumulusUtil.qr(switchNode.getPassword()));

		CumulusTorSwitchSession session = (CumulusTorSwitchSession) CumulusUtil.getSession(switchNode);
		SshExecResult result = session.executeEnhanced(startDaemonCommand);
		result.log(logger, Level.DEBUG);
		result.logIfError(logger);
		
		if (result.getExitCode() != 0) {
			throw new HmsException ("Failed to disable BGP on switch " + switchNode.getSwitchId());
		}
			
		CumulusUtil.configurePersistenceDirectory(switchNode);
		return true;
	}
	
	@SuppressWarnings("unused")
	/** Instantiated Cumulus Switch Service object */
	private CumulusTorSwitchService service;
	private static Logger logger = Logger.getLogger(CumulusBgpHelper.class);
}
