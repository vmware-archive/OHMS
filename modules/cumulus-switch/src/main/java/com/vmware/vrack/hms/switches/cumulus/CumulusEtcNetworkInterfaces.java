/* Copyright © 2014 VMware, Inc. All rights reserved. */
package com.vmware.vrack.hms.switches.cumulus;

import com.vmware.vrack.hms.common.switches.api.SwitchNetworkConfiguration;
import com.vmware.vrack.hms.common.switches.api.SwitchNetworkConfiguration.Ipv4;
import com.vmware.vrack.hms.common.switches.api.SwitchNetworkConfiguration.Port;
import com.vmware.vrack.hms.common.switches.api.SwitchNetworkConfiguration.Routes.Clag;
import com.vmware.vrack.hms.common.switches.api.SwitchNetworkConfiguration.Routes.Vrr;
import com.vmware.vrack.hms.common.switches.api.SwitchNetworkConfiguration.Vlan;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Parses the Network Configuration for Cumulus Switch.
 * 
 * This class serves to parse and pull together the Cumulus network interface details.
 */
@Deprecated
public class CumulusEtcNetworkInterfaces {

	/** 
	 * CumulusEtcNetworkInterfaces empty constructor.
	 * 
	 * Empty constructor for the CumulusEtcNetworkInterface class.
	 */
	private CumulusEtcNetworkInterfaces() {
	}
	
	/**
	 * Parses the network configuration details for the cumulus switch instance.
	 * 
	 * Utilizing the SwitchNetworkConfiguration object from the switch instance: 
	 * 	1. parses the details 
	 * 	2. structures the output using the CumulusConstants with the network interface details.
	 * 
	 * @param networkConfiguration Contains details on the network configuration (ie ports, lacpgroups, vlans etc)
	 * @return CumulusEtcNetworkInterfaces A verbose output of all the network configuration details
	 */
	public static CumulusEtcNetworkInterfaces parseNetworkConfiguration(SwitchNetworkConfiguration networkConfiguration) {
		CumulusEtcNetworkInterfaces ret = new CumulusEtcNetworkInterfaces();
		Map<String, List<Port>> lacpGroupPortMap = networkConfiguration.getLacpGroupPortMap();
		Map<String, Ipv4> portIpv4Map = networkConfiguration.getPortIpv4Map();

		/* Start with the preamble */
		ret.contents.add(CumulusConstants.PREAMBLE_STANZA);
		
		/* Insert HMS signature */
		ret.contents.add(
			CumulusConstants.HMS_SIGNATURE_STANZA
				.replaceAll("\\{hms.configuration.name\\}", networkConfiguration.getName())
				.replaceAll("\\{hms.configuration.version\\}", networkConfiguration.getVersion())
				.replaceAll("\\{hms.configuration.applied\\}", new Date(System.currentTimeMillis()).toString())
				.replaceAll("\\{hms.configuration.signature\\}", UUID.randomUUID().toString())
		);

		/* Insert loopback port */
		ret.contents.add(CumulusConstants.LOOPBACK_STANZA);
		
		/* Insert management port */
		ret.contents.add(CumulusConstants.MANAGEMENT_PORT_STANZA
				.replaceAll("\\{ipv4\\}", CumulusConstants.IPV4_LINE)
				.replaceAll("\\{gateway\\}", CumulusConstants.GATEWAY_LINE)
				.replaceAll("\\{address\\}", networkConfiguration.getManagement().getIpv4().getAddress())
				.replaceAll("\\{netmask\\}", networkConfiguration.getManagement().getIpv4().getNetmask())
				.replaceAll("\\{gateway\\}", networkConfiguration.getManagement().getIpv4().getGateway())
		);
		
		/* Insert switch ports (if they have static IP address or MTU setting) */
		List<Port> portList = networkConfiguration.getPorts();
		if (portList != null) {
			for (Port p : portList) {
				String mode = "manual";
				String address = "";
				String gateway = "";
				String mtu = "";
				boolean writeStanza = false;
				
				if (p.getMtu() != null) {
					writeStanza = true;
					mtu = CumulusConstants.MTU_LINE
							.replaceAll("\\{mtu\\}", p.getMtu().toString())
							.replaceAll("\\{name\\}", p.getName());
				}
				
				if (portIpv4Map.containsKey(p.getName())) {
					Ipv4 ipv4 = portIpv4Map.get(p.getName());
					writeStanza = true;
					mode = "static";
					address = CumulusConstants.IPV4_LINE
							.replaceAll("\\{address\\}", ipv4.getAddress())
							.replaceAll("\\{netmask\\}", ipv4.getNetmask());
				}
				
				if (writeStanza) {
					ret.contents.add(
							CumulusConstants.SWITCH_PORT_STANZA
								.replaceAll("\\{name\\}", p.getName())
								.replaceAll("\\{mode\\}", mode)
								.replaceAll("\\{ipv4\\}", address)
								.replaceAll("\\{gateway\\}", gateway)
								.replaceAll("\\{mtu\\}", mtu)
					);
				}
			}
		}
		
		/* Insert LACP groups, i.e. bonds */
		for (String lg : lacpGroupPortMap.keySet()) {
			List<Port> interfaceList = lacpGroupPortMap.get(lg);
			StringBuilder interfaceListStr = new StringBuilder();
			String mode = "manual";
			String address = "";
			String gateway = "";
			
			for (Port p : interfaceList) {
				interfaceListStr.append(p.getName()).append(" ");
			}
			
			if (portIpv4Map.containsKey(lg)) {
				Ipv4 ipv4 = portIpv4Map.get(lg);
				mode = "static";
				address = CumulusConstants.IPV4_LINE
					.replaceAll("\\{address\\}", ipv4.getAddress()
					.replaceAll("\\{netmask\\}", ipv4.getNetmask()));
			}
			
			ret.contents.add(
				CumulusConstants.LACP_GROUP_STANZA
					.replaceAll("\\{name\\}", lg)
					.replaceAll("\\{interfaces\\}", interfaceListStr.toString())
					.replaceAll("\\{mode\\}", mode)
					.replaceAll("\\{lacpMode\\}", "802.3ad")
					.replaceAll("\\{ipv4\\}", address)
					.replaceAll("\\{gateway\\}", gateway)
			);
		}
		
		/* Insert VLANs and VRRs */
		for (Vlan v : networkConfiguration.getVlans()) {
			StringBuilder interfaceListStr = new StringBuilder();
            StringBuilder ipv4Str = new StringBuilder("");
			StringBuilder vrrStr = new StringBuilder("");
            StringBuilder stpStr = new StringBuilder("");
			
			for (String port : v.getTaggedPorts()) {
				ret.contents.add(CumulusConstants.VLAN_SUBINTERFACE_STANZA
					.replaceAll("\\{id\\}", Integer.toString(v.getVlanId()))
					.replaceAll("\\{subifname\\}", port));
				
				interfaceListStr.append(port + "." + v.getVlanId()).append(" ");
			}
			
			for (String port : v.getUntaggedPorts()) {
				interfaceListStr.append(port).append(" ");
			}

            if (portIpv4Map.containsKey(v.getName())) {
                Ipv4 ipv4 = portIpv4Map.get(v.getName());
                ipv4Str.append(CumulusConstants.IPV4_LINE
                        .replaceAll("\\{address\\}", ipv4.getAddress())
                        .replaceAll("\\{netmask\\}", ipv4.getNetmask()));
            }

			if (v.getStp() != null && v.getStp().getEnabled()) {
                stpStr.append(CumulusConstants.STP_LINE
                        .replaceAll("\\{protocol\\}", v.getStp().getProtocol()));
            }

			if (networkConfiguration.getRoutes() != null) {
				for (Vrr vrr : networkConfiguration.getRoutes().getVrr()) {
					if (vrr.getInterface().equalsIgnoreCase(v.getName())) {
						vrrStr.append(CumulusConstants.VRR_LINE
								.replaceAll("\\{controlInterface\\}", vrr.getControlInterface())
								.replaceAll("\\{sharedMac\\}", vrr.getSharedMac())
								.replaceAll("\\{sharedIp\\}", vrr.getSharedIp()));
						break;
					}
				}
			}
			
			ret.contents.add(CumulusConstants.VLAN_STANZA
                .replaceAll("\\{ipv4\\}", ipv4Str.toString())
                .replaceAll("\\{stp\\}", stpStr.toString())
				.replaceAll("\\{vrr\\}", vrrStr.toString())
				.replaceAll("\\{name\\}", v.getName())
				.replaceAll("\\{interfaces\\}", interfaceListStr.toString()));
		}
		
		/* Insert CLAG stanza */
		if (networkConfiguration.getRoutes() != null) {
			for (Clag c : networkConfiguration.getRoutes().getClag()) {
				ret.contents.add(CumulusConstants.CLAG_STANZA
					.replaceAll("\\{name\\}", c.getInterface())
					.replaceAll("\\{peerIp\\}", c.getPeerIp())
					.replaceAll("\\{sharedMac\\}", c.getSharedMac()));
			}
		}
		
		return ret;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (String c : contents) {
			sb.append(c).append("\n");
		}
		return sb.toString();
	}
	
	public InputStream getInputStream() {
		byte[] contentBytes = toString().getBytes();
		return new ByteArrayInputStream(contentBytes);
	}
	
	/** List of Strings containing all Network Config details - vlans, ports, lacpgroup details. */
	private List<String> contents = new ArrayList<String>();
    @SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(CumulusEtcNetworkInterfaces.class);
}
