/* Copyright © 2014 VMware, Inc. All rights reserved. */
package com.vmware.vrack.hms.switches.cumulus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsObjectNotFoundException;
import com.vmware.vrack.hms.common.exception.HmsOperationNotSupportedException;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.switches.api.SwitchVxlan;
import com.vmware.vrack.hms.common.util.SshExecResult;

/**
 * Provides functionality for vxlan apis.
 * 
 * This class provides functionality for vxlans such as get vxlan, matching vxlans for specified vlan, create vxlan, update vxlan etc. 
 */
public class CumulusVxlanHelper {
	
	/**
	 * Base constructor for the VxlanHelper
	 * 
	 * Constructor for class Cumulus Vxlans 
	 * @param service
	 */
	public CumulusVxlanHelper(CumulusTorSwitchService service) {
		this.service = service;
	}

	/**
	 * Get vxlans for provided switch node
	 * 
	 * Attempts to get vxlans (execute command to get vxlans) that maps to a specific vlan name. 
	 * 	Calls function getSwitchVxlansMatchingVlan
	 * 
	 * @param switchNode object
	 * @return List of vxlans 
	 */
	public List<SwitchVxlan> getSwitchVxlans(SwitchNode switchNode) {
		return getSwitchVxlansMatchingVlan(switchNode, null);
	}
	
	/**
	 * Get Switch vxlans matching vlan
	 * 
	 * Currently disabled functionality. Should return the vxlans that match to the provided vlan name for the switch node object.
	 * 
	 * @param switchNode object
	 * @param vlanName name for which the vxlans will be associated to.
	 * @return List of vxlans
	 */
	public List<SwitchVxlan> getSwitchVxlansMatchingVlan(SwitchNode switchNode, String vlanName) {
		/* Disabling Vxlan get because this functionality is not being utilized currently. */
		return new ArrayList<SwitchVxlan>();
	}
	
	/**
	 * Original (unused) get switch vxlan matching vlan
	 * 
	 * Older implementation - to get the vxlans that are associated with the provided vlan.
	 * 
	 * @param switchNode object
	 * @param vlanName name for which the vxlans are associated with
	 * @return List of vxlans
	 */
	@SuppressWarnings("unused")
	private List<SwitchVxlan> ORIGINAL_getSwitchVxlansMatchingVlan(SwitchNode switchNode, String vlanName) {
		List<SwitchVlan> allVlans = service.getSwitchVlansBulk(switchNode);
		List<SwitchVxlan> allVxlans = new ArrayList<SwitchVxlan>();
		
		CumulusTorSwitchSession switchSession = (CumulusTorSwitchSession) CumulusUtil.getSession(switchNode);
		
		for (SwitchVlan vlan : allVlans) {
			if (vlanName != null && !vlanName.trim().equals(vlan.getName().trim()))
				continue;

			String filename = CumulusUtil.getVlanFilename(vlan.getName());
			String command = CumulusConstants.GET_VXLAN_COMMAND
					.replaceAll("\\{filename\\}", filename);
			
			try {
				SshExecResult result = switchSession.executeEnhanced(command);
				result.logIfError(logger);
				
				String resultOut = new String (result.getStdout());
				String[] resultLines = resultOut.split("\n");
				
				for (String line : resultLines) {
					Pattern p = Pattern.compile("\\s+pre-up\\s+ip\\s+link\\s+add\\s(.*)\\s+type\\s+vxlan\\s+id\\s+(.*)\\s+svcnode\\s+(.*)\\s*");
					Matcher m = p.matcher(line);
					if (m.matches()) {
						SwitchVxlan vxlan = new SwitchVxlan();
						vxlan.setName(m.group(1));
						vxlan.setVni(m.group(2));
						vxlan.setVlanName(vlan.getName());
						allVxlans.add(vxlan);
					}
				}
			} catch (HmsException e) {
				logger.warn("Error trying to get VXLANs associated with VLAN " + vlanName, e);
			}
		}
		
		return allVxlans;
	}
	
	/**
	 * Create vxlan
	 * 
	 * Create vxlan for switch node based on a vxlan provided. Currently not supported. 
	 * 
	 * @param switchNode object
	 * @param vxlan object that will be used to create a vxlan.
	 * @return True if creation was successful; false if creation of vxlan was unsuccessful
	 * @throws HmsException Create Vxlan Functionality is not supported
	 */
	public boolean createVxlan(SwitchNode switchNode, SwitchVxlan vxlan)
			throws HmsException {
		throw new HmsOperationNotSupportedException("Create VXLAN functionality is currently not supported on this switch type.");
	}
	
	/**
	 * Original Create Vxlan 
	 * 
	 * Older implementation - to create a vxlan based on the provided vxlan object. Determine if that vxlan exists, remove and recreate vxlan. 
	 * 	
	 * @param switchNode object
	 * @param vxlan to be created
	 * @return  True if creation was successful; false if creation of vxlan was unsuccessful
	 * @throws HmsException if Vxlan did not exist, VNI parameter invalid and Vlan name invalid
	 */
	@SuppressWarnings("unused")
	private boolean ORIGINAL_createVxlan(SwitchNode switchNode, SwitchVxlan vxlan)
			throws HmsException {
	
		/* Validate input */
		if (vxlan == null || vxlan.getName() == null || vxlan.getName().trim().equals("")) {
			throw new HmsException ("Invalid VXLAN object and/or name.");
		}
		
		if (vxlan.getVni() == null || vxlan.getVni().trim().equals("")) {
			throw new HmsException ("Invalid VNI input parameter.");
		}
		
		if (vxlan.getVlanName() == null || vxlan.getVlanName().trim().equals("")) {
			throw new HmsException ("Invalid VLAN name input parameter.");
		}
		
		String vlanName = vxlan.getVlanName().trim();
		SwitchVlan vlan = service.getSwitchVlan(switchNode, vlanName);
		if (vlan == null) {
			throw new HmsObjectNotFoundException("VLAN " + vlanName + " could not be found.");
		}
		
		/* Check if VxLAN already exists and remove it */
		String vxlanName = vxlan.getName().trim();
		List<SwitchVxlan> list = getSwitchVxlansMatchingVlan(switchNode, vlanName);
		if (list != null) {
			for (SwitchVxlan vx : list) {
				if (vx.getName().equals(vxlanName)) {
					deleteVxlan(switchNode, vxlanName, vlanName);
					break;
				}
			}
		}
		
		logger.debug ("Creating VXLAN tunnel " + vxlanName + " associated with VLAN " + vlanName + " on switch " + switchNode.getSwitchId());

		String filename = CumulusUtil.getVlanFilename(vlanName);
		String command = CumulusConstants.CREATE_VXLAN_COMMAND
				.replaceAll("\\{password\\}", CumulusUtil.qr(switchNode.getPassword()))
				.replaceAll("\\{vxlan\\}", vxlanName)
				.replaceAll("\\{vni\\}", vxlan.getVni())
				.replaceAll("\\{vlan\\}", vlanName)
				.replaceAll("\\{svcNodeIp\\}", CumulusConstants.SPINE_SWITCH_ANYCAST_IP)
				.replaceAll("\\{filename\\}", filename);

		CumulusTorSwitchSession switchSession = (CumulusTorSwitchSession) CumulusUtil.getSession(switchNode);
		SshExecResult result = switchSession.executeEnhanced(command);
		result.logIfError(logger);
		
		CumulusUtil.configurePersistenceDirectory(switchNode);
		
		return true;
	}
	
	/**
	 * Delete Vxlan
	 * 
	 * Delete vxlan for switch node based on the vxlan provided. Currently not supported.
	 * 
	 * @param switchNode object
	 * @param vxlanName name of vxlan to be deleted
	 * @param vlanName vlan object for which the vxlan is associated to.
	 * @return True if deletion was successful; False if deletion was unsuccessful
	 * @throws HmsException Delete Vxlan Functionality is not supported
	 */
	public boolean deleteVxlan(SwitchNode switchNode, String vxlanName, String vlanName)
			throws HmsException {
		throw new HmsOperationNotSupportedException("Delete VXLAN functionality is currently not supported on this switch type.");
	}

	/**
	 * Original implementation of Delete
	 * 
	 * Older implementation - delete a vxlan based on the vxlan name provided, that is associated with the provided vlan.
	 * 
	 * @param switchNode object
	 * @param vxlanName name of vxlan to be deleted
	 * @param vlanName vlan that the vxlan is associated to
	 * @return True if deletion was successful; False if deletion was unsuccessful
	 * @throws HmsException Delete of Vxlan if invalid vxlan object or vlan name
	 */
	@SuppressWarnings("unused")
	private boolean ORIGINAL_deleteVxlan(SwitchNode switchNode, String vxlanName, String vlanName) throws HmsException {
		/* Validate input */
		if (vxlanName == null || vxlanName.trim().equals("")) {
			throw new HmsException ("Invalid VXLAN object and/or name.");
		}
		
		if (vlanName == null || vlanName.trim().equals("")) {
			throw new HmsException ("Invalid VLAN name input parameter.");
		}
		
		vxlanName = vxlanName.trim();
		vlanName = vlanName.trim();
		
		SwitchVlan vlan = service.getSwitchVlan(switchNode, vlanName);
		if (vlan == null) {
			throw new HmsObjectNotFoundException("VLAN " + vlanName + " could not be found.");
		}
		
		logger.debug ("Deleting VXLAN tunnel " + vxlanName + " associated with VLAN " + vlanName + " on switch " + switchNode.getSwitchId());

		String filename = CumulusUtil.getVlanFilename(vlanName);
		String command = CumulusConstants.DELETE_VXLAN_COMMAND
				.replaceAll("\\{password\\}", CumulusUtil.qr(switchNode.getPassword()))
				.replaceAll("\\{vxlan\\}", vxlanName)
				.replaceAll("\\{vlan\\}", vlanName)
				.replaceAll("\\{filename\\}", filename);

		CumulusTorSwitchSession switchSession = (CumulusTorSwitchSession) CumulusUtil.getSession(switchNode);
		SshExecResult result = switchSession.executeEnhanced(command);
		result.logIfError(logger);
		
		CumulusUtil.configurePersistenceDirectory(switchNode);
		
		return true;
	}
	
	/** Variable used to represent the Cumulus Switch session */
	private CumulusTorSwitchService service;
    private static Logger logger = Logger.getLogger(CumulusVxlanHelper.class);
}
