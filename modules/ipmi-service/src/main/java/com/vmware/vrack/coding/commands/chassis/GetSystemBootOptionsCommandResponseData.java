package com.vmware.vrack.coding.commands.chassis;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;
import com.veraxsystems.vxipmi.common.TypeConverter;

/**
 * Wrapper class for Get Boot Options Parameter -> Get Boot Flags Parameter
 * 
 * @author Yagnesh Chawda
 */
public class GetSystemBootOptionsCommandResponseData
    implements ResponseData
{
    // The Sample response of this command,
    /*
     * [0]=>01 , Parameter revision [1]=>05 , Parameter selector(which we sent) From here, is the actual meaningful data
     * [2]=>00 , Tells about if the change in boot preference is temporary or permanent [3]=>04 , Boot Device
     * Selector,This Bit is important to us [4]=>00 , [5]=>00 , [6]=>00 , Device instance selector, This Bit determines
     * which instance to choose, internal or external
     */
    private byte[] bootFlagCode = new byte[7];

    public byte[] getBootFlagCode()
    {
        return bootFlagCode;
    }

    public void setBootFlagCode( byte[] bootFlagCode )
    {
        this.bootFlagCode = bootFlagCode;
    }

    // Returns if the BootFlags are valid
    public boolean isBootFlagsValid()
    {
        byte data1 = bootFlagCode[2];
        if ( ( data1 & TypeConverter.intToByte( 0x80 ) ) != 0 )
        {
            return true;
        }
        return false;
    }

    // If the boot flags are temporary or persistent
    public BootOptionsValidity getBootOptionsValidity()
    {
        byte data1 = bootFlagCode[2];
        if ( ( data1 & TypeConverter.intToByte( 0x40 ) ) == 0 )
        {
            return BootOptionsValidity.NextBootOnly;
        }
        else
        {
            return BootOptionsValidity.Persistent;
        }
    }

    // Returns if the Bios Boot Type is Legacy or EFI
    public BiosBootType getBiosBootType()
    {
        byte data1 = bootFlagCode[2];
        if ( ( data1 & TypeConverter.intToByte( 0x20 ) ) == 0 )
        {
            return BiosBootType.Legacy;
        }
        else
        {
            return BiosBootType.EFI;
        }
    }

    // If the boot type is External or Internal
    public BootDeviceType getBootDeviceType()
    {
        byte deviceInstanceSelector = bootFlagCode[6];
        if ( ( deviceInstanceSelector & TypeConverter.intToByte( 0x10 ) ) != 0 )
        {
            return BootDeviceType.Internal;
        }
        else
        {
            return BootDeviceType.External;
        }
    }

    // Returns the Boot Device Selector.
    public BootDeviceSelector getBootDeviceSelector()
    {
        return BootDeviceSelector.getBootDeviceSelector( bootFlagCode[3] );
    }

    public int getBootDeviceInstanceNumber()
    {
        int instanceNumber = ( TypeConverter.intToByte( 0x0F ) & bootFlagCode[6] );
        return instanceNumber;
    }
}
