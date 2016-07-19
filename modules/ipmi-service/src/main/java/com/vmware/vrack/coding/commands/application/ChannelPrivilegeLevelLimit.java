package com.vmware.vrack.coding.commands.application;

public enum ChannelPrivilegeLevelLimit
{
    CALLBACK( 1 ), USER( 2 ), OPERATOR( 3 ), ADMINISTRATOR( 4 ), OEM( 5 );
    private int value;

    private ChannelPrivilegeLevelLimit( int value )
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
