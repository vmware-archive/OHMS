package com.vmware.vrack.coding.commands;

/**
 * Contains all the IPMI Command parameters if a command requires one For example, In Get Lan Configuration Parameters,
 * we need to pass parameters along with the command, like for Mac Address we need to pass the parameter that specifies
 * to get MAC address.
 *
 * @author Yagnesh Chawda
 */
public class IpmiCommandParameters
{
    public static final byte LAN_CH_1 = 0x01;

    public static final byte MAC_ADDRESS_PARAMETER = 0x05;

    public static final byte SUBNET_MASK_PARAMETER = 0x06;

    public static final byte IP_ADDRESS_PARAMETER = 0x03;

    public static final byte IP_ADDRESS_SOURCE_PARAMETER = 0x04;

    public static final byte GRATUITOUS_ARP_INTERVAL_COMMAND = 0x0B;

    public static final byte DEFAULT_GATEWAY_IP_ADDRESS_PARAMETER = 0x0C;

    public static final byte DEFAULT_GATEWAY_MAC_ADDRESS_PARAMETER = 0x0D;

    public static final byte DEFAULT_SET_SELECTOR_PARAMETER = 0x00;

    public static final byte DEFAULT_BLOCK_SELECTOR_PARAMETER = 0x00;

    public static final byte MAC_ADDRESS_SET_SELECTOR_PARAMETER = 0x00;

    public static final byte MAC_ADDRESS_BLOCK_SELECTOR_PARAMETER = 0x00;

    // MAC Address parameters are {channel number, MAC address parameter, Set Selector, Block Selector}
    public static final byte[] GET_MAC_ADDRESS_COMMAND_PARAM =
        { LAN_CH_1, MAC_ADDRESS_PARAMETER, MAC_ADDRESS_SET_SELECTOR_PARAMETER, MAC_ADDRESS_BLOCK_SELECTOR_PARAMETER };

    // Parameter to get the current channel number using the GET_CHANNEL_INFO command
    public static final byte GET_CHANNEL_INFO_CURRENT_CHANNEL_PARAMETER = 0xE;

    // Cold Reset does not require any parameter
    public static final byte[] COLD_RESET_PARAM = null;

    // Get Self Test Results does not require any Parameter
    public static final byte[] GET_SELF_TEST_RESULTS_PARAM = null;

    // Get ACPI Power State does not require any parameter
    public static final byte[] GET_ACPI_POWER_STATE_PARAM = null;

    // Get User Access Command requires parameters in format {Channel number, User ID}
    // Here the user id is user-1 which is reserved, and have user id as 0x01
    // This command can be useful for querying number of users on the channel
    public static final byte[] GET_USER_ACCESS_COMMAND_FOR_USER_1_PARAM = { LAN_CH_1, 0x01 };

    // IP Address parameters are {channel number, IP address parameter, Set Selector, Block Selector}
    public static final byte[] GET_IP_ADDRESS_COMMAND_PARAM =
        { LAN_CH_1, IP_ADDRESS_PARAMETER, DEFAULT_SET_SELECTOR_PARAMETER, DEFAULT_BLOCK_SELECTOR_PARAMETER };

    // Default Gateway IP Address parameters are {channel number, Default Gateway IP address parameter, Set Selector,
    // Block Selector}
    public static final byte[] GET_DEFAULT_GATEWAY_IP_ADDRESS_COMMAND_PARAM = { LAN_CH_1,
        DEFAULT_GATEWAY_IP_ADDRESS_PARAMETER, DEFAULT_SET_SELECTOR_PARAMETER, DEFAULT_BLOCK_SELECTOR_PARAMETER };

    // Default Gateway MAC Address parameters are {channel number, Default Gateway MAC address parameter, Set Selector,
    // Block Selector}
    public static final byte[] GET_DEFAULT_GATEWAY_MAC_ADDRESS_COMMAND_PARAM = { LAN_CH_1,
        DEFAULT_GATEWAY_MAC_ADDRESS_PARAMETER, DEFAULT_SET_SELECTOR_PARAMETER, DEFAULT_BLOCK_SELECTOR_PARAMETER };

    // Gratuitous ARP Interval parameters are {channel number, Gratuitous ARP Interval , Set Selector, Block Selector}
    public static final byte[] GET_GRATUITOUS_ARP_INTERVAL_COMMAND_PARAM =
        { LAN_CH_1, GRATUITOUS_ARP_INTERVAL_COMMAND, DEFAULT_SET_SELECTOR_PARAMETER, DEFAULT_BLOCK_SELECTOR_PARAMETER };

    // Subnet Mask parameters are {channel number, Subnet Mask, Set Selector, Block Selector}
    public static final byte[] GET_SUBNET_MASK_COMMAND_PARAM =
        { LAN_CH_1, SUBNET_MASK_PARAMETER, DEFAULT_SET_SELECTOR_PARAMETER, DEFAULT_BLOCK_SELECTOR_PARAMETER };

    // IP Address Source parameters are {channel number,IP address Source parameter, Set Selector, Block Selector}
    public static final byte[] GET_IP_ADDRESS_SOURCE_COMMAND_PARAM =
        { LAN_CH_1, IP_ADDRESS_SOURCE_PARAMETER, DEFAULT_SET_SELECTOR_PARAMETER, DEFAULT_BLOCK_SELECTOR_PARAMETER };
}
