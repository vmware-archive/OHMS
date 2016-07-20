package com.vmware.vrack.coding.commands.application;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;
import com.veraxsystems.vxipmi.common.TypeConverter;

/**
 * wrapper class for get Channel Access Command Request
 * 
 * @author Yagnesh Chawda
 */
public class GetChannelAccessCommandResponseData
    implements ResponseData
{
    private int channelNumber;

    private byte channelAccessCode;

    private byte channelPrivilegeLevelLimitCode;

    public int getChannelNumber()
    {
        return channelNumber;
    }

    public void setChannelNumber( int channelNumber )
    {
        this.channelNumber = channelNumber;
    }

    public byte getChannelAccessCode()
    {
        return channelAccessCode;
    }

    public void setChannelAccessCode( byte channelAccessCode )
    {
        this.channelAccessCode = channelAccessCode;
    }

    public byte getChannelPrivilegeLevelLimitCode()
    {
        return channelPrivilegeLevelLimitCode;
    }

    public void setChannelPrivilegeLevelLimitCode( byte channelPrivilegeLevelLimitCode )
    {
        this.channelPrivilegeLevelLimitCode = channelPrivilegeLevelLimitCode;
    }

    public boolean isAlertingEnabled()
    {
        if ( ( channelAccessCode & TypeConverter.intToByte( 0x20 ) ) == 0 )
        {
            return true;
        }
        return false;
    }

    public boolean isPerMessageAuthenticationEnabled()
    {
        if ( ( channelAccessCode & TypeConverter.intToByte( 0x10 ) ) == 0 )
        {
            return true;
        }
        return false;
    }

    public boolean isUserLevelAuthenticationEnabled()
    {
        if ( ( channelAccessCode & TypeConverter.intToByte( 0x08 ) ) == 0 )
        {
            return true;
        }
        return false;
    }

    public ChannelAccessModes getChannelAccessMode()
    {
        if ( ( channelAccessCode & TypeConverter.intToByte( 0x02 ) ) != 0 )
        {
            if ( ( channelAccessCode & TypeConverter.intToByte( 0x01 ) ) != 0 )
            {
                return ChannelAccessModes.Shared;
            }
            else if ( ( channelAccessCode & TypeConverter.intToByte( 0x01 ) ) == 0 )
            {
                return ChannelAccessModes.Always_Available;
            }
        }
        else if ( ( channelAccessCode & TypeConverter.intToByte( 0x01 ) ) != 0 )
        {
            return ChannelAccessModes.Pre_Boot_Only;
        }
        else if ( ( channelAccessCode & TypeConverter.intToByte( 0x01 ) ) == 0 )
        {
            return ChannelAccessModes.Disabled;
        }
        return null;
    }

    public ChannelPrivilegeLevelLimit getChannelPrivilegeLevelLimit()
    {
        if ( ( channelPrivilegeLevelLimitCode & TypeConverter.intToByte( 0x04 ) ) != 0 )
        {
            if ( ( channelPrivilegeLevelLimitCode & TypeConverter.intToByte( 0x01 ) ) != 0 )
            {
                return ChannelPrivilegeLevelLimit.OEM;
            }
            else if ( ( channelPrivilegeLevelLimitCode & TypeConverter.intToByte( 0x01 ) ) == 0 )
            {
                return ChannelPrivilegeLevelLimit.ADMINISTRATOR;
            }
        }
        else if ( ( channelAccessCode & TypeConverter.intToByte( 0x02 ) ) != 0 )
        {
            if ( ( channelPrivilegeLevelLimitCode & TypeConverter.intToByte( 0x01 ) ) != 0 )
            {
                return ChannelPrivilegeLevelLimit.OPERATOR;
            }
            else if ( ( channelPrivilegeLevelLimitCode & TypeConverter.intToByte( 0x01 ) ) == 0 )
            {
                return ChannelPrivilegeLevelLimit.USER;
            }
        }
        else if ( ( channelAccessCode & TypeConverter.intToByte( 0x01 ) ) != 0 )
        {
            return ChannelPrivilegeLevelLimit.CALLBACK;
        }
        return null;
    }
}
