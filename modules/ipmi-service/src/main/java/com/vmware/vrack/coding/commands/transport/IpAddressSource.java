package com.vmware.vrack.coding.commands.transport;

public enum IpAddressSource
{
    UNSPECIFIED( 0 ),
    STATIC_ADDRESS( 1 ),
    OBTAINED_BY_BMC_RUNNING_DHCP( 2 ),
    ADDRESS_LOADED_BY_BIOS( 3 ),
    OBTAINED_BY_BMC_VIA_OTHER_ASSIGNMENT_PROTOCOLS( 4 );
    private int value;

    private IpAddressSource( int value )
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
