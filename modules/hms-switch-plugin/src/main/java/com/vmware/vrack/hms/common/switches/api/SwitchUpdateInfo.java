/*******************************************************************************
 * Copyright (c) 2015 VMware, Inc. All rights reserved.
 ******************************************************************************/

package com.vmware.vrack.hms.common.switches.api;

public class SwitchUpdateInfo {
	private String ipAddress;
	private String netmask;
	private String gateway;
	private String timeServer;
	
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public void setManagementIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getNetmask() {
		return netmask;
	}
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	public String getTimeServer() {
		return timeServer;
	}
	public void setTimeServer(String timeServer) {
		this.timeServer = timeServer;
	}
}
