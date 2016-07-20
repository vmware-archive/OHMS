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
