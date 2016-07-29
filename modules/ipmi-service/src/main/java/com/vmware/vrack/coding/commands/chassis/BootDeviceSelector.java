package com.vmware.vrack.coding.commands.chassis;

import com.veraxsystems.vxipmi.common.TypeConverter;

/**
 * Enum for Boot Device Selector in Get System Boot Options
 * 
 * @author Yagnesh Chawda
 */
public enum BootDeviceSelector
{
    No_Override( (byte) 0x00 ),
    Force_PXE( (byte) 0x04 ),
    Force_Default_Hard_Disk( (byte) 0x08 ),
    Force_Default_Hard_Disk_Safe_Mode( (byte) 0x0C ),
    Force_Diagnostic_Partition( (byte) 0x10 ),
    Force_Default_CD_DVD( (byte) 0x14 ),
    Force_Into_BIOS_Setup( (byte) 0x18 ),
    Force_Boot_From_Remotely_Connected_Removable_Media( (byte) 0x1C ),
    Force_Boot_From_Primary_Remote_Media( (byte) 0x24 ),
    Force_Boot_From_Remotely_Connected_CD_DVD( (byte) 0x20 ),
    Force_Boot_From_Remotely_Connected_Hard_Disk( (byte) 0x2C ),
    Force_Boot_From_Primary_Removable_Media( (byte) 0x3C );
    private byte code;

    private final static byte MASK_BITS = 0x3C;

    private BootDeviceSelector( byte code )
    {
        this.code = code;
    }

    public byte getCode()
    {
        return this.code;
    }

    public static BootDeviceSelector getBootDeviceSelector( byte inputCode )
    {
        if ( ( TypeConverter.intToByte( MASK_BITS ) & inputCode ) == Force_PXE.getCode() )
        {
            return Force_PXE;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS ) & inputCode ) == Force_Default_Hard_Disk.getCode() )
        {
            return Force_Default_Hard_Disk;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS ) & inputCode ) == Force_Default_Hard_Disk_Safe_Mode.getCode() )
        {
            return Force_Default_Hard_Disk_Safe_Mode;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS ) & inputCode ) == Force_Diagnostic_Partition.getCode() )
        {
            return Force_Diagnostic_Partition;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS ) & inputCode ) == Force_Default_CD_DVD.getCode() )
        {
            return Force_Default_CD_DVD;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS ) & inputCode ) == Force_Into_BIOS_Setup.getCode() )
        {
            return Force_Into_BIOS_Setup;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS )
            & inputCode ) == Force_Boot_From_Remotely_Connected_Removable_Media.getCode() )
        {
            return Force_Boot_From_Remotely_Connected_Removable_Media;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS )
            & inputCode ) == Force_Boot_From_Primary_Remote_Media.getCode() )
        {
            return Force_Boot_From_Primary_Remote_Media;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS )
            & inputCode ) == Force_Boot_From_Remotely_Connected_CD_DVD.getCode() )
        {
            return Force_Boot_From_Remotely_Connected_CD_DVD;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS )
            & inputCode ) == Force_Boot_From_Remotely_Connected_Hard_Disk.getCode() )
        {
            return Force_Boot_From_Remotely_Connected_Hard_Disk;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS )
            & inputCode ) == Force_Boot_From_Primary_Removable_Media.getCode() )
        {
            return Force_Boot_From_Primary_Removable_Media;
        }
        else
        {
            return No_Override;
        }
    }
}
