package com.vmware.vrack.coding.commands.transport;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;
import com.veraxsystems.vxipmi.common.TypeConverter;

/**
 * Wrapper class for Get IP Address Source Command
 * 
 * @author Yagnesh Chawda
 */
public class GetIpAddressSourceCommandResponseData
    implements ResponseData
{
    private byte parameterRevision;

    private byte ipAddressSourceCode;

    public byte getParameterRevision()
    {
        return parameterRevision;
    }

    public void setParameterRevision( byte parameterRevision )
    {
        this.parameterRevision = parameterRevision;
    }

    public byte getIpAddressSourceCode()
    {
        return ipAddressSourceCode;
    }

    public void setIpAddressSourceCode( byte ipAddressSourceCode )
    {
        this.ipAddressSourceCode = ipAddressSourceCode;
    }

    public IpAddressSource getIpAddressSource()
    {
        if ( ipAddressSourceCode <= TypeConverter.intToByte( 0x04 ) )
        {
            if ( ( ipAddressSourceCode & TypeConverter.intToByte( 0x04 ) ) != 0 )
            {
                return IpAddressSource.OBTAINED_BY_BMC_VIA_OTHER_ASSIGNMENT_PROTOCOLS;
            }
            else if ( ( ipAddressSourceCode & TypeConverter.intToByte( 0x02 ) ) != 0 )
            {
                if ( ( ipAddressSourceCode & TypeConverter.intToByte( 0x01 ) ) != 0 )
                {
                    return IpAddressSource.ADDRESS_LOADED_BY_BIOS;
                }
                else if ( ( ipAddressSourceCode & TypeConverter.intToByte( 0x01 ) ) == 0 )
                {
                    return IpAddressSource.OBTAINED_BY_BMC_RUNNING_DHCP;
                }
            }
            else if ( ( ipAddressSourceCode & TypeConverter.intToByte( 0x01 ) ) != 0 )
            {
                return IpAddressSource.STATIC_ADDRESS;
            }
            else if ( ( ipAddressSourceCode & TypeConverter.intToByte( 0x01 ) ) == 0 )
            {
                return IpAddressSource.UNSPECIFIED;
            }
        }
        return null;
    }
}
