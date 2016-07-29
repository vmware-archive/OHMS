package com.vmware.vrack.coding.commands.chassis;

/**
 * Enum for Bios Boot Type
 * 
 * @author Yagnesh Chawda
 */
public enum BiosBootType
{
    Legacy( (byte) 0x00 ), EFI( (byte) 0x20 );
    private byte code;

    private BiosBootType( byte code )
    {
        this.code = code;
    }

    public byte getCode()
    {
        return this.code;
    }

    public static BiosBootType getBiosBootType( byte inputCode )
    {
        if ( ( inputCode & EFI.getCode() ) != 0 )
        {
            return EFI;
        }
        else
        {
            return Legacy;
        }
    }
}
