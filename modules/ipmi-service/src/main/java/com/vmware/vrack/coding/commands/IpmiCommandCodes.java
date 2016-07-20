package com.vmware.vrack.coding.commands;

/**
 * Contains IPMI command bytes for various IPMI functions
 * 
 * @author Yagnesh Chawda
 */
public final class IpmiCommandCodes
{
    /**
     * IPMI code for Cold Reset command
     */
    public static final byte COLD_RESET = 0x02;

    /**
     * IPMI code for Get ACPI Power State Command
     */
    public static final byte GET_ACPI_POWER_STATE = 0x07;

    /**
     * IPMI code for Get self Test Results
     */
    public static final byte GET_SELF_TEST_RESULTS = 0x04;

    /**
     * IPMI code for Get LAN configuration Parameters
     */
    public static final byte GET_LAN_CONFIG_PARAMETERS = 0x02;

    /**
     * IPMI code for Get Channel Info Command
     */
    public static final byte GET_CHANNEL_INFO = 0x42;

    /**
     * IPMI code for Get User Name Command
     */
    public static final byte GET_USER_NAME = 0x46;

    /**
     * IPMI command for Get User Access Command
     */
    public static final byte GET_USER_ACCESS = 0x44;

    /**
     * Ipmi command for Set User Password Command
     */
    public static final byte SET_USER_PASSWORD = 0x47;

    /**
     * Ipmi command for Get Channel Access
     */
    public static final byte GET_CHANNEL_ACCESS = 0x41;

    /**
     * Ipmi command for Get System Boot Options
     */
    public static final byte GET_SYSTEM_BOOT_OPTIONS = 0x09;

    /**
     * Ipmi command for Set System Boot Options
     */
    public static final byte SET_SYSTEM_BOOT_OPTIONS = 0x08;

    /**
     * Ipmi command for Chassis Identify
     */
    public static final byte CHASSIS_IDENTIFY = 0x04;
}
