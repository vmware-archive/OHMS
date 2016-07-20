/*******************************************************************************
 * Copyright (c) 2014 VMware, Inc. All rights reserved.
 ******************************************************************************/

package com.vmware.vrack.hms.common.switches.api;

public class SwitchOspfInterfaceConfig {
	public enum InterfaceMode { ACTIVE, PASSIVE };
	
	public InterfaceMode getMode() { return mode; }
	public String getName() { return name; }
	
	public void setMode(InterfaceMode mode) { this.mode = mode; }
	public void setName(String name) { this.name = name; }
	
	private InterfaceMode mode;
	private String name;
}
