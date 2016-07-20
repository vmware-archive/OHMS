/*******************************************************************************
 * Copyright (c) 2014 VMware, Inc. All rights reserved.
 ******************************************************************************/

package com.vmware.vrack.hms.common.switches.api;

import java.util.List;

public class SwitchOspfGlobalConfig {
	public enum OspfMode { ACTIVE, PASSIVE };
	
	public OspfMode getDefaultMode() { return defaultMode; }
	public String getRouterId() { return routerId; }
	public List<SwitchOspfNetworkConfig> getNetworks() { return networks; }
	public List<SwitchOspfInterfaceConfig> getInterfaces() { return interfaces; }
	
	public void setDefaultMode(OspfMode defaultMode) { this.defaultMode = defaultMode; }
	public void setRouterId(String routerId) { this.routerId = routerId; }
	public void setNetworks(List<SwitchOspfNetworkConfig> networks) { this.networks = networks; }
	public void setInterfaces(List<SwitchOspfInterfaceConfig> interfaces) { this.interfaces = interfaces; }
	
	private List<SwitchOspfNetworkConfig> networks;
	private List<SwitchOspfInterfaceConfig> interfaces;
	private OspfMode defaultMode;
	private String routerId;
}
