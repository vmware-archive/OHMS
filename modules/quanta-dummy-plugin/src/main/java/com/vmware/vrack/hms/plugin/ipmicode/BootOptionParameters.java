package com.vmware.vrack.hms.plugin.ipmicode;

public class BootOptionParameters {
	
    public static final String SET_SYSTEM_BOOT_OPTIONS_COMMAND = "08";

    public static final byte BOOT_FLAG_SELECTOR = 0x05;

    public static final String GET_SYSTEM_BOOT_OPTIONS_COMMAND = "09";
	
    public static final String GET_BOOT_OPTIONS_PARAMETERS = "05 00 00";

}
