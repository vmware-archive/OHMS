package com.vmware.vrack.coding.commands.chassis;

/**
 * Enum for Validity of SystemBootOptions
 * 
 * @author Yagnesh Chawda
 */
public enum BootOptionsValidity
{
    NextBootOnly( (byte) 0x00 ), Persistent( (byte) 0x01 );
    private byte code;

    private BootOptionsValidity( byte code )
    {
        this.code = code;
    }

    public byte getCode()
    {
        return this.code;
    }

    public static BootOptionsValidity getBootOptionsValidity( byte inputCode )
    {
        if ( ( inputCode & Persistent.getCode() ) != 0 )
        {
            return Persistent;
        }
        else
        {
            return NextBootOnly;
        }
    }
}
