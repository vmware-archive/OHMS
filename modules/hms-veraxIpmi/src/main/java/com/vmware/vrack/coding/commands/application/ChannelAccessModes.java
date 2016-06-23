package com.vmware.vrack.coding.commands.application;

/**
 * Wrapper class for Channel Access Modes
 * 
 * @author Yagnesh Chawda
 */
public enum ChannelAccessModes
{
    Disabled( 0 ), Pre_Boot_Only( 1 ), Always_Available( 2 ), Shared( 3 );
    private int value;

    private ChannelAccessModes( int value )
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }
}
