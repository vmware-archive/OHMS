/* ********************************************************************************
 * SwitchBgpConfig.java
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
package com.vmware.vrack.hms.common.switches.api;

import java.util.List;

public class SwitchBgpConfig {
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public int getLocalAsn() {
		return localAsn;
	}
	public void setLocalAsn(int localAsn) {
		this.localAsn = localAsn;
	}
	public String getLocalIpAddress() {
		return localIpAddress;
	}
	public void setLocalIpAddress(String localIpAddress) {
		this.localIpAddress = localIpAddress;
	}
	public int getPeerAsn() {
		return peerAsn;
	}
	public void setPeerAsn(int peerAsn) {
		this.peerAsn = peerAsn;
	}
	public String getPeerIpAddress() {
		return peerIpAddress;
	}
	public void setPeerIpAddress(String peerIpAddress) {
		this.peerIpAddress = peerIpAddress;
	}
	public List<String> getExportedNetworks() {
		return exportedNetworks;
	}
	public void setExportedNetworks(List<String> exportedNetworks) {
		this.exportedNetworks = exportedNetworks;
	}
	
	private boolean enabled;
	private int localAsn;
	private String localIpAddress;
	private int peerAsn;
	private String peerIpAddress;
	private List<String> exportedNetworks;
}
