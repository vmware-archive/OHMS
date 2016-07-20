package com.vmware.vrack.hms.switches.cumulus.model;

import java.util.List;

/**
 * Provides basic implementation for the object ConfigBlock.
 * 
 * Created by sankala on 12/5/14.
 */
public class ConfigBlock {
	/** Set of variables for the Config Block object. Such as name, pvid, vlans, portConfig details, and switch port details */
    public Configuration parentConfig;
    public String name;
    public String pvid = "";
    public List<String> vlans;
    //Trailing text of iface swp10 (inet static) ...
    public String portConfig = "";
    //lines after switch port declaration. - May be we can improve it later.
    public String otherConfig = "";

    /**
     * Get string for Config block - returns empty string.
     * 
     * @return empty string
     */
    public String getString() { return ""; }

    /**
     * Add other configuration details; notes new addition of configuration.
     * 
     * @param aLine
     * @param configuration
     */
    public void addOtherConfig(String aLine, Configuration configuration) {
        otherConfig += aLine + "\n";
    }
}
