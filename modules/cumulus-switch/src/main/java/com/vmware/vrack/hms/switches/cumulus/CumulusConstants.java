/* Copyright © 2014 VMware, Inc. All rights reserved. */
package com.vmware.vrack.hms.switches.cumulus;

/**
 * All constants for Cumulus
 *
 * This class provides all contsants/commands and snippets of output.
 */
public class CumulusConstants {
    /** Cumulus switch type */
    public static final String CUMULUS_SWITCH_TYPE = "cumulus";

    /** Cumulus firmware name */
    public static final String FIRMWARE_NAME = "ONIE";

    /** Discover switch command */
    public static final String CUMULUS_DISCOVER_COMMAND = "grep -ci cumulus-linux /etc/os-release";

    /** List all ports command */
    public static final String IP_LIST_PORTS_COMMAND = "ip -o link show";

    /** Change port status (up/down) command */
    public static final String CHANGE_PORT_STATUS_COMMAND = "echo '{password}' | sudo -S ip link set dev {portName} {status}";

    /** Reboot switch command */
    public static final String SWITCH_REBOOT_COMMAND = "echo '{password}' | sudo -S reboot";

    /** Upgrade switch command */
    public static final String SWITCH_UPGRADE_COMMAND = "echo '{password}' | sudo -S /bin/bash -c '" +
            "/usr/cumulus/bin/cl-img-install -f {package}\n" +
            "if [ $? -eq 0 ]; then\n" +
            "	/usr/cumulus/bin/cl-img-select -s\n" +
            "else\n" +
            "	exit -1\n" +
            "fi'";

    /** Check upgrade command */
    public static final String SWITCH_UPGRADE_CHECK_PKG_COMMAND =
            "wget --spider --tries 2 --connect-timeout 15 {package}";

    /** List specific port command */
    public static final String IP_LIST_PORT_COMMAND = "ip -o link show {portName}";

    /** List specific port command */
    public static final String IP_LINK_SHOW_COMMAND = "ip -s link show {portName}";

    /** get switch operating system command */
    public static final String GET_SWITCH_OS_COMMAND = "cat /etc/os-release";

    /** get the port state */
    public static final String GET_SWITCH_PORT_STATE = "ip -o link show {portName} | egrep -o state.* | cut -d' ' -f2";

    /** Get time of last boot command */
    public static final String LAST_BOOT_TIME_COMMAND = "date +%s; cat /proc/uptime | cut -d' ' -f1";

    /** Get hardware platform model and manufacturer command */
    public static final String HARDWARE_MODEL_MANUFACTURER_COMMAND = "platform-detect";

    /** Get serial #/chassis id command */
    public static final String GET_SERIAL_ID_COMMAND = "echo '{password}' | sudo -S /usr/cumulus/bin/decode-syseeprom -e";

    /** Get Part Number command */
    public static final String GET_PART_NUMBER_COMMAND = "echo '{password}' | sudo -S /usr/cumulus/bin/decode-syseeprom | egrep ^Part | cut -d' ' -f15-";

    /** Get Manufacture Date command */
    public static final String GET_MANUFACTURE_DATE_COMMAND = "echo '{password}' | sudo -S /usr/cumulus/bin/decode-syseeprom | egrep ^\"Manufacture Date\" | tr -s ' '| cut -d' ' -f5-";

    /** Get firmware version command */
    public static final String GET_FIRMWARE_VER_COMMAND = "echo '{password}' | sudo -S /usr/sbin/fw_printenv onie_version | cut -d= -f2";

    /** Cat ports.conf file */
    public static final String CAT_PORTS_CONF_FILE = "cat /etc/cumulus/ports.conf";

    /** ports.conf file */
    public static final String PORTS_CONF_FILE = "/etc/cumulus/ports.conf";

    /** IP address to reach closest Spine switch in the network */
    public static final String SPINE_SWITCH_ANYCAST_IP = "192.168.255.255";

    /** Show forwarding dabatase contents */
    public static final String BRIDGE_FDB_COMMAND = "/bin/bridge fdb show dev {portName}";

    /** Show lower layer details for a port */
    public static final String PORT_GET_LOW_LEVEL_DETAILS_COMMAND = "echo '{password}' | sudo -S /sbin/ethtool {portName}";

    /** Apply network configuration command */
    public static final String APPLY_NETWORK_CFG_COMMAND =
            "cp /etc/network/interfaces /tmp/interfaces.bak.`date +%s`;\n" +
                    "echo '{password}' | sudo -S cp -pf {file} /etc/network/interfaces;\n" +
                    "echo '{password}' | sudo -S service networking restart";

    /** Reload the interfaces */
    public static final String RELOAD_INTERFACES = "echo '{password}' | sudo -S touch /etc/network/interfaces.d/hms-port-dummy;\n" +
        "echo '{password}' | sudo -S ifreload -a";

    /** Change switch IP address command */
    public static final String CHANGE_SWITCH_IP_COMMAND =
            "if [ -n \"{address}\" ]; then\n" +
                    "	echo '{password}' | sudo -S sed -r -i.hms.bak '/^iface\\s+eth0\\b/,/^[^\\s]+/ s/address\\s+[0-9.]+\\b/address {address}/g' /etc/network/interfaces \n" +
                    "fi;\n" +
                    "if [ -n \"{netmask}\" ]; then\n" +
                    "	echo '{password}' | sudo -S sed -r -i.hms.bak '/^iface\\s+eth0\\b/,/^[^\\s]+/ s/netmask\\s+[0-9.]+\\b/netmask {netmask}/g' /etc/network/interfaces \n" +
                    "fi;\n" +
                    "if [ -n \"{gateway}\" ]; then\n" +
                    "	echo '{password}' | sudo -S sed -r -i.hms.bak '/^iface\\s+eth0\\b/,/^[^\\s]+/ s/gateway\\s+[0-9.]+\\b/gateway {gateway}/g' /etc/network/interfaces \n" +
                    "fi;\n"
                    ;

    /** List all bonds command */
    public static final String LIST_BONDS_COMMAND = "ls -1 /proc/net/bonding";

    /** List all bridges command */
    public static final String LIST_BRIDGES_COMMAND = "/sbin/brctl show";

    /** List specific bridge command */
    public static final String LIST_BRIDGE_COMMAND = "/sbin/brctl show {bridge}";

    /** List specific lacp group command */
    public static final String LIST_LACP_GROUP_COMMAND =
            "if [ ! -f \"/proc/net/bonding/{lacpGroupName}\" ]; then"							+ "\n" +
                    "	exit -1;" 																		+ "\n" +
                    "else"																				+ "\n" +
                    "	grep \"Slave Interface\" /proc/net/bonding/{lacpGroupName} | cut -d\":\" -f2" 	+ "\n" +
                    "fi;"																				+ "\n";

    public static final String SHOW_LACP_IP_ADDR =
            "ip -br addr show {lacpGroupName} | awk  '{print $3}'";

    /** Change password command */
    public static final String CHANGE_PASSWORD_COMMAND = "echo '{password}' | sudo -S /bin/bash -c '/usr/sbin/chpasswd << \"EOF\" \n" +
            "{username}:{newPassword}\n" +
            "EOF\n'";

    /** Enable Quagga Daemons command */
    public static final String ENABLE_QUAGGA_DAEMONS_COMMAND =
            "echo '{password}' | sudo -S sed -r -i.hms.bak 's/^(zebra|ospfd)\\s*=.*/\\1=yes/g' /etc/quagga/daemons;\n" +
                    "/usr/bin/vtysh -C;\n" +
                    "if [ $? -ne 0 ]; then\n" +
                    "	echo 'ERROR: Invalid OSPF configuration. Please fix the issue and try again.'\n" +
                    "	exit -1\n" +
                    "else\n" +
                    "	echo '{password}' | sudo -S /etc/init.d/quagga restart\n" +
                    "fi;";

    /** Disable Quagga Daemons command */
    public static final String DISABLE_QUAGGA_DAEMONS_COMMAND =
            "echo '{password}' | sudo -S sed -r -i.hms.bak 's/^(zebra|ospfd)\\s*=.*/\\1=no/g' /etc/quagga/daemons;\n" +
                    "/usr/bin/vtysh -C;\n" +
                    "if [ $? -ne 0 ]; then\n" +
                    "	echo 'ERROR: Invalid OSPF configuration. Please fix the issue and try again.'\n" +
                    "	exit -1\n" +
                    "else\n" +
                    "	echo '{password}' | sudo -S /etc/init.d/quagga restart\n" +
                    "fi;";

    /* List the state of the ospfd and zebra daemons */
    public static final String LIST_QUAGGA_DAEMONS_COMMAND =
            "echo '{password}' | egrep '^ospfd=|^zebra=' /etc/quagga/daemons | cut -d'=' -f2";

    /** Enable BGP command */
    public static final String ENABLE_BGP_COMMAND =
            "echo '{password}' | sudo -S sed -r -i.hms.bak 's/^(zebra|bgpd)\\s*=.*/\\1=yes/g' /etc/quagga/daemons;\n" +
                    "/usr/bin/vtysh -C;\n" +
                    "if [ $? -ne 0 ]; then\n" +
                    "	echo 'ERROR: Invalid BGP configuration. Please fix the issue and try again.'\n" +
                    "	exit -1\n" +
                    "else\n" +
                    "	echo '{password}' | sudo -S /etc/init.d/quagga restart\n" +
                    "fi;";

    public static final String CONFIGURE_BGP_COMMAND =
            "echo '{password}' | sudo -S /bin/bash -c \"" +
                    "cl-bgp as {localAsn} neighbor add {peerIp} remote-as {peerAsn};\n" +
                    "{exportNetworksCommands}\"";

    public static final String EXPORT_NETWORK_COMMAND =
            "cl-bgp as {localAsn} network add {network} ipv4 unicast;\n";

    /** Disable BGP command */
    public static final String DISABLE_BGP_COMMAND =
            "echo '{password}' | sudo -S sed -r -i.hms.bak 's/^(bgpd)\\s*=.*/\\1=no/g' /etc/quagga/daemons;\n" +
                    "/usr/bin/vtysh -C;\n" +
                    "if [ $? -ne 0 ]; then\n" +
                    "	echo 'ERROR: Invalid BGP configuration. Please fix the issue and try again.'\n" +
                    "	exit -1\n" +
                    "else\n" +
                    "	echo '{password}' | sudo -S /etc/init.d/quagga restart\n" +
                    "fi;";

    /** List all sensors command */
    public static final String GET_SENSOR_INFO_COMMAND = "/usr/sbin/smonctl -j";

    /** Set MTU command */
    public static final String SET_MTU_COMMAND =
            "echo '{password}' | sudo -S ip link set dev {name} mtu {mtu}";

    /** Set Speed, Auto Negotiation & duplex command */
    public static final String SET_SPEED_DUPLEX_AUTONEG_COMMAND =
            "echo '{password}' | sudo -S ethtool -s {name} speed {speed} duplex {duplex} autoneg {autoneg}";

    /* speed, duplex and autoneg can be specified individually to ethtool, or in any combination */
    public static final String SET_SPEED_DUPLEX_AUTONEG_PREAMBLE =
            "echo '{password}' | sudo -S ethtool -s {name}";

    public static final String SET_SPEED_ADDENDUM =
            "speed {speed}";

    public static final String SET_DUPLEX_ADDENDUM =
            "duplex {duplex}";

    public static final String SET_AUTONEG_ADDENDUM =
            "autoneg {autoneg}";

    /** Set IP address command */
    public static final String SET_IP_ADDRESS_COMMAND =
            "echo '{password}' | sudo -S ip addr add {ipAddress} dev {name}";

    public static final String SET_DEFAULT_ROUTE_CMD =
            "echo '{password}' | sudo -S ip route replace default via {gateway} dev {port}";

    public static final String DEL_DEFAULT_ROUTE_CMD =
            "echo '{password}' | sudo -S ip route delete default via {gateway}";

    /** Add source clause */
    public static final String ADD_SOURCE_CLAUSE_COMMAND =
            "CLEX=`grep ^source /etc/network/interfaces | grep hms- | wc -w`\n" +
                    "if [ $CLEX -eq 0 ]; then\n" +
                    "	echo '{password}' | sudo -S /bin/bash -c \"tee -a /etc/network/interfaces << 'EndDoc' > /dev/null\n" +
                    "\n" +
                    "# Source all HMS-related artifacts\n" +
                    "source /etc/network/interfaces.d/hms-*\n" +
                    "\n" +
                    "EndDoc\n\"\n" +
                    "fi;";

    /** Copy command */
    public static final String COPY_AS_ROOT_COMMAND =
            "echo '{password}' | sudo -S cp -pf {old} {new}";

    /** Delete LACP group command */
    public static final String DELETE_LACP_GROUP_COMMAND =
            "echo '{password}' | sudo -S /sbin/ifdown {lacpGroup};\n" +
                    "echo '{password}' | sudo -S rm -f {filename};"
                    ;

    /** Get all VLANs command */
    public static final String GET_ALL_VLANS_COMMAND =
            "/sbin/brctl show | sed -r '1 d; /^\\s+/ d; s/^(\\S+)\\s.*/\\1/g' | /usr/bin/xargs -r /sbin/ifquery -o json";

    /** Delete VLAN command */
    public static final String DELETE_VLAN_COMMAND =
            "echo '{password}' | sudo -S /sbin/ifdown --with-depends {vlan};\n" +
                    "echo '{password}' | sudo -S rm -f {filename};"
                    ;

    /** Bring up interfaces command */
    public static final String IFUP_COMMAND =
            "echo '{password}' | sudo -S /sbin/ifup --with-depends {interfaces}";

    /** Create VXLAN command */
    public static final String CREATE_VXLAN_COMMAND =
            "echo '{password}' | sudo -S sed -r -i -e 's/^(iface {vlan}.*)/" +
                    "\\1\\n\\t\\tpre-up ip link add {vxlan} type vxlan id {vni} svcnode {svcNodeIp}\\n" +
                    "\\t\\tpre-up \\/sbin\\/brctl addif {vlan} {vxlan}\\n" +
                    "\\t\\tpost-down ip link del dev {vxlan}/g' {filename};\n" +
                    "echo '{password}' | sudo -S /sbin/ifup --with-depends {vlan}";

    /** Delete VXLAN command */
    public static final String DELETE_VXLAN_COMMAND =
            "echo '{password}' | sudo -S sed -r -i -e '/^\\W+(pre-up|post-down)\\b.*\\b{vxlan}\\b/d' {filename};\n" +
                    "echo '{password}' | sudo -S /sbin/ifup --with-depends {vlan};\n" +
                    "echo '{password}' | sudo -S /sbin/ip link del dev {vxlan}";

    /** Get VXLAN command */
    public static final String GET_VXLAN_COMMAND =
            "sed -n -r -e '/\\s+pre-up\\s+ip\\s+link\\s+add.*type\\s+vxlan\\s+/p' {filename}";

    /** Enable MC-LAG command */
    public static final String ENABLE_MCLAG_COMMAND = IFUP_COMMAND;

    /** Disable MC-LAG command. */
    public static final String DISABLE_MCLAG_COMMAND =
            "echo '{password}' | sudo -S /bin/bash -c 'if [ -f \"{filename}\" ]; then\n" +
                    "	rm -f {filename}\n" +
                    "	ifreload -a\n" +
                    "fi;'";

    /** Temporary (yet persistent) directory on ToR switch */
    public static final String PERSISTENT_TMP_DIR = "/tmp";

    /** File containing all persistent interfaces related settings */
    public static final String INTERFACES_FILE = "/etc/network/interfaces";
    /** File containing all configuration details for Quagga */
    public static final String CUMULUS_QUAGGA_CONFIG_FILE = "/etc/quagga/Quagga.conf";

    /** Persistence Directory setup command */
    public static final String SETUP_PERSISTENCE_DIRECTORY =
            "if [ -d /mnt/persist ]; then\n" +
                    "	echo '{password}' | sudo -S /bin/bash -c \"mkdir -p /mnt/persist/etc/network/interfaces.d\n" +
                    "		cp -pfu /etc/network/interfaces /mnt/persist/etc/network\n" +
                    "		rm -f /mnt/persist/etc/network/interfaces.d/hms-*\n" +
                    "		cp -pfu /etc/network/interfaces.d/hms-* /mnt/persist/etc/network/interfaces.d\n" +
                    "		cp -pfu /etc/{passwd,shadow,hosts,ntp.conf} /mnt/persist/etc\"\n" +
                    "fi;\n";

    public static final String SET_NTP_SERVER_COMMAND =
            "echo '{password}' | sudo -S /bin/bash -c \"" +
                    "	sed -i.hms.bak -r '0,/^#*\\s*server/ s/^((#*\\s*)server)/server {timeServer}\\n\\1/g' /etc/ntp.conf;" +
                    "	service ntp restart\"";

    /** Default connection polling interval */
    public static final int DEFAULT_CONNECTION_POLLING_INTERVAL = 5000;
    /** Default connection Timeout */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 20000;

    /** Management port name */
    public static final String MANAGEMENT_PORT_NAME = "eth0";

    /** Introduction/Preable about the network interfaces file */
    public static final String PREAMBLE_STANZA =
            "# This file describes the network interfaces available on your system\n" +
                    "# and how to activate them. For more information, see interfaces(5).\n" +
                    "#\n" +
                    "# IMPORTANT!!!!!!!\n" +
                    "#\n" +
                    "# Interface definitions are processed in an order dependent fashion as follows:\n" +
                    "#\n" +
                    "# - Loopbacks, ports, and bonds such as lo, swp12, bond0\n" +
                    "#   - note that ports that are part of a bond do not have other configuration\n" +
                    "# - Port and bond sub-interfaces (vlans), such as swp1.900 or bond0.100\n" +
                    "# - Bridges, like br0 and br2.\n" +
                    "#\n" +
                    "# When defining more than 10 bridges using VLAN sub-interfaces, the\n" +
                    "# subinterfaces should be declared in conjunction with their associated bridge.\n" +
                    "#\n"
                    ;

    /** Comment details about HMS name/version */
    public static final String HMS_SIGNATURE_STANZA =
            "###################################################################################\n" +
                    "# THIS SECTION RESERVED FOR HMS USE. PLEASE DO NOT MODIFY.\n" +
                    "# Configuration name: {hms.configuration.name}\n" +
                    "# Configuration version: {hms.configuration.version}\n" +
                    "# Configuration applied: {hms.configuration.applied}\n" +
                    "# {hms.configuration.signature}\n" +
                    "###################################################################################\n"
                    ;

    /** Loopback stanza details */
    public static final String LOOPBACK_STANZA =
            "# Loopback network interface\n" +
                    "auto lo\n" +
                    "iface lo inet loopback\n"
                    ;

    /** Management Port details */
    public static final String MANAGEMENT_PORT_STANZA =
            "# Management port network interface\n" +
                    "auto eth0\n" +
                    "iface eth0 inet static\n" +
                    "{ipv4}" +
                    "{gateway}"
                    ;

    /** Switch port details */
    public static final String SWITCH_PORT_STANZA =
            "auto {name}\n" +
                    "iface {name} inet {mode}\n" +
                    "{ipv4}" +
                    "{gateway}" +
                    "{mtu}" +
                    "{others}"
                    ;

    /** MTU details */
    public static final String MTU_LINE =
            "       mtu {mtu}\n"
            ;

    /** Speed, Auto Negotation and Duplex mode details */
    public static final String AUTONEG_DUPLEX_LINES =
            "       link-duplex {duplex}\n" +
                    "       link-autoneg {autoneg}\n"
                    ;

    /** Speed, Auto Negotation and Duplex mode details */
    public static final String SPEED_LINE_ADDENDUM =
            "       link-speed {speed}\n"
            ;

    /** IPV4 details */
    public static final String IPV4_LINE =
            "		address {address}\n" +
                    "		netmask {netmask}\n"
                    ;

    /** IPV4 Address details */
    public static final String IPV4_ADDRESS_LINE =
            "		address {address}\n"
            ;

    /** STP details */
    public static final String STP_LINE =
            "		bridge-stp on\n" +
                    "		mstpctl-forcevers {protocol}\n"
                    ;

    /** Gateway detail */
    public static final String GATEWAY_LINE =
            "		gateway {gateway}\n"
            ;

    /** LACP group detail */
    public static final String LACP_GROUP_STANZA =
            "auto {name}\n" +
                    "iface {name}\n" +
                    "{ipv4}" +
                    "{gateway}" +
                    "		bond-slaves {interfaces}\n" +
                    "		bond-mode {lacpMode}\n" +
                    "		bond-miimon 100\n" +
                    "		bond-use-carrier 1\n" +
                    "		bond-lacp-rate 0\n" +
                    "		bond-min-links 1\n" +
                    "		bond-xmit-hash-policy layer3+4\n"
                    ;

    /** VRR detail */
    public static final String VRR_LINE =
            "		post-up /usr/cumulus/bin/cl-vrr {name} {controlInterface} {sharedMac} {sharedIp}\n";

    /** VLAN detail */
    public static final String VLAN_STANZA =
            "auto {name}\n" +
                    "iface {name} inet {mode}\n" +
                    "		bridge-ports {interfaces}\n" +
                    "		bridge-ageing 300\n" +
                    "{stp}" +
                    "{ipv4}" +
                    "{vrr}"
                    ;

    /** VLAN subinterface details */
    public static final String VLAN_SUBINTERFACE_STANZA =
            "auto {subifname}.{id}\n" +
                    "iface {subifname}.{id} inet manual\n" +
                    "		up ip link set up dev {subifname}.{id}\n" +
                    "		down ip link set down dev {subifname}.{id}\n"
                    ;

    /** CLAG detail */
    public static final String CLAG_STANZA =
            "auto {name}\n" +
                    "iface {name}\n" +
                    "{ipv4}" +
                    "	clagd-enable {enabled}\n" +
                    "	clagd-priority 8192\n" +
                    "	clagd-peer-ip {peerIp}\n" +
                    "	clagd-sys-mac {sharedMac}\n"
                    ;

    /** VLAN CL25 detail */
    public static final String VLAN_CL25_STANZA =
            "auto {name}\n" +
                    "iface {name}\n" +
                    "	bridge-vlan-aware yes\n" +
                    "	bridge-ports {interfaces}\n" +
                    "	bridge-vids {id}\n" +
                    "	bridge-pvid 1\n" +
                    "	bridge-stp on\n";

    /** VLAN CL25 create command */
    public static final String VLAN_CL25_CREATE_COMMAND =
            "echo '{password}' | sudo -S sed -r -i -e 's/(bridge-ports.*)/\\1 {interfaces}/g' -e 's/(bridge-vids.*)/\\1 {id}/g' {filename}";

    /** VLAN CL25 subinterface detail */
    public static final String VLAN_CL25_SUBINTERFACE_STANZA =
            "auto {subifname}\n" +
                    "iface {subifname}\n" +
                    "	bridge-vids {id}\n" +
                    "	bridge-pvid 1\n";

    public static final Integer DEFAULT_MTU = 1500;
}
