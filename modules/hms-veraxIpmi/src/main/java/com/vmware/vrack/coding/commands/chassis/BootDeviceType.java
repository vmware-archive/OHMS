package com.vmware.vrack.coding.commands.chassis;

/**
 * Enum for Boot Device Type
 * 
 * @author Yagnesh Chawda
 */
public enum BootDeviceType
{
    Internal( 1 ), External( 0 );
    private int code;

    private BootDeviceType( int code )
    {
        this.code = code;
    }

    public int getCode()
    {
        return this.code;
    }

    public static BootDeviceType getBootDeviceType( int code )
    {
        if ( code == 1 )
        {
            return Internal;
        }
        else
        {
            return External;
        }
    }
}
