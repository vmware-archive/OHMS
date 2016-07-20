package com.vmware.vrack.hms.switches.cumulus.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides basic implementation for the object Bridge. Extends Switch Port implementation.
 * 
 * Created by sankala on 12/5/14.
 */
public class Bridge extends ConfigBlock {

	/** Set of variables used for the Config Block object */
    public List<SwitchPort> members = new ArrayList<>();

    public static String format = "auto %s\n" +
            "iface %s\n" +
            "    bridge-vlan-aware yes\n" +
            "    bridge-stp on";
    
    private static String bridge_ports_format = "\n    bridge-ports %s";

    /**
     * Get the string of switch port Bridge details.
     * 
     * @return string with details for the bridge
     */
    public String getString() {
        String swpStr = "";
        for(SwitchPort member : members) {
        	if (member.getIpv4DefaultRoute() == null)
        		swpStr += member.name + " ";
        }
        String retString = String.format( format, name, name);
        if (swpStr != "") {
        	retString += String.format(bridge_ports_format, swpStr);
        }
        if ( pvid != "" ) retString += "\n    bridge-pvid " + pvid;
        if ( vlans != null && vlans.size() > 0 ) retString += "\n    bridge-vids " + Configuration.joinCollection(vlans, " ");
        if ( otherConfig != "" ) retString += "\n" + otherConfig;
        return retString;
    }

    static Pattern bridgePattern = Pattern.compile("\\s*(bridge-vlan-aware|bridge-ports|bridge-vids|bridge-pvid|bridge-access) (.*)$");

    /**
     * Add another configuration detail for the bridge
     * 
     * @param aLine
     * @param configuration details 
     * @return void
     */
    public void addOtherConfig(String aLine, Configuration configuration) {
        Matcher matcher = bridgePattern.matcher(aLine);
        if ( matcher.matches() ) {
            if ( matcher.group(1).equals("bridge-ports") ) {
                String[] memberSwpIds = matcher.group(2).split(" ");
                for(String memberSwpId : memberSwpIds) {
                    SwitchPort swp = (SwitchPort) configuration.getConfigBlock(memberSwpId);
                    if ( swp == null ) {
                        if ( memberSwpId.startsWith("peer") ) {
                            swp = new SwitchPort();
                            swp.peer = true;
                            swp.name = memberSwpId;
                        } else {
                            continue;
                        }
                    }
                    swp.setParentBridge( this );
                    members.add( swp );
                }
            } else if ( matcher.group(1).equals("bridge-vids") ) {
                String[] vlanIds = matcher.group(2).split(" ");
                if ( vlans == null ) vlans = new ArrayList<>();
                vlans.addAll(Arrays.asList(vlanIds));
            } else if ( matcher.group(1).equals("bridge-pvid") ) {
                pvid = matcher.group(2);
            }
        } else if (aLine.indexOf("bridge-stp") > -1) {
            //ignore. We need bridge-stp to be on always.
        } else
            super.addOtherConfig(aLine, configuration);

        //brige-ports definition can occur before pvid and vids. So doing it every step.. - can be optimized.
        for(SwitchPort member : members) {
            member.setParentBridge( this );
        }
    }
}
