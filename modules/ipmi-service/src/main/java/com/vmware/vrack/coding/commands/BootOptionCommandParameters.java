package com.vmware.vrack.coding.commands;

/**
 * Class for Boot Options Command related Parameters.
 * 
 * @author Yagnesh Chawda
 */
public class BootOptionCommandParameters
{
    // IPMI Boot Selector 5 Boot Flags Selector
    public static final byte BOOT_FLAGS_SELECTOR = (byte) 0x05;

    // IPMI Command for Get Boot Flags
    public static final byte[] GET_BOOT_FLAGS = { 0x05, 0x00, 0x00 };
}
