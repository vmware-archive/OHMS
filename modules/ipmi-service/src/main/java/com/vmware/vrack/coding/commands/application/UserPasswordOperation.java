package com.vmware.vrack.coding.commands.application;

public enum UserPasswordOperation
{
    Disable_User( (byte) 0x00 ), Enable_User( (byte) 0x01 ), Set_Password( (byte) 0x02 ), Test_Password( (byte) 0x03 );
    private byte value;

    private UserPasswordOperation( byte value )
    {
        this.value = value;
    }

    public byte getValue()
    {
        return value;
    }
}
