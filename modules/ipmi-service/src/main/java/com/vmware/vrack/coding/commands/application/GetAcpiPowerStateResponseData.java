package com.vmware.vrack.coding.commands.application;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;
import com.veraxsystems.vxipmi.common.TypeConverter;

/**
 * wrapper class for Get ACPI Power State
 * 
 * @author Yagnesh Chawda
 */
public class GetAcpiPowerStateResponseData
    implements ResponseData
{
    private byte systemAcpiPowerStateCode;

    private byte deviceAcpiPowerStateCode;

    public byte getSystemAcpiPowerStateCode()
    {
        return systemAcpiPowerStateCode;
    }

    public void setSystemAcpiPowerStateCode( byte systemAcpiPowerStateCode )
    {
        this.systemAcpiPowerStateCode = systemAcpiPowerStateCode;
    }

    public byte getDeviceAcpiPowerStateCode()
    {
        return deviceAcpiPowerStateCode;
    }

    public void setDeviceAcpiPowerStateCode( byte deviceAcpiPowerStateCode )
    {
        this.deviceAcpiPowerStateCode = deviceAcpiPowerStateCode;
    }

    // Returns the SystemPowerState
    public SystemPowerState getSystemAcpiPowerState()
    {
        if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x00 ) )
        {
            return SystemPowerState.S0_G0;
        }
        else if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x01 ) )
        {
            return SystemPowerState.S1;
        }
        else if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x02 ) )
        {
            return SystemPowerState.S2;
        }
        else if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x03 ) )
        {
            return SystemPowerState.S3;
        }
        else if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x04 ) )
        {
            return SystemPowerState.S4;
        }
        else if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x05 ) )
        {
            return SystemPowerState.S5_G2;
        }
        else if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x06 ) )
        {
            return SystemPowerState.S4_S5;
        }
        else if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x07 ) )
        {
            return SystemPowerState.G3;
        }
        else if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x08 ) )
        {
            return SystemPowerState.Sleeping;
        }
        else if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x09 ) )
        {
            return SystemPowerState.G1_sleeping;
        }
        else if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x0A ) )
        {
            return SystemPowerState.Override;
        }
        else if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x20 ) )
        {
            return SystemPowerState.Legacy_On;
        }
        else if ( systemAcpiPowerStateCode == TypeConverter.intToByte( 0x21 ) )
        {
            return SystemPowerState.Legacy_Off;
        }
        else
        {
            return SystemPowerState.Unknown;
        }
    }

    // returns the DevicePowerState
    public DevicePowerState getDeviceAcpiPowerState()
    {
        if ( deviceAcpiPowerStateCode == TypeConverter.intToByte( 0x00 ) )
        {
            return DevicePowerState.D0;
        }
        else if ( deviceAcpiPowerStateCode == TypeConverter.intToByte( 0x01 ) )
        {
            return DevicePowerState.D1;
        }
        else if ( deviceAcpiPowerStateCode == TypeConverter.intToByte( 0x02 ) )
        {
            return DevicePowerState.D2;
        }
        else if ( deviceAcpiPowerStateCode == TypeConverter.intToByte( 0x03 ) )
        {
            return DevicePowerState.D3;
        }
        else
        {
            return DevicePowerState.Unknown;
        }
    }
}
