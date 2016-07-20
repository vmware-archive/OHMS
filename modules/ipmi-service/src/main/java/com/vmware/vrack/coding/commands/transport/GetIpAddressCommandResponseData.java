package com.vmware.vrack.coding.commands.transport;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;

/**
 * Wrapper class for Get IP Address Command
 * 
 * @author Yagnesh Chawda
 */
public class GetIpAddressCommandResponseData
    implements ResponseData
{
    private byte parameterRevision;

    private byte[] ipAddress;

    public String getIpAddressAsString()
    {
        StringBuilder ipAddressAsString = new StringBuilder();
        if ( ipAddress != null )
        {
            for ( int i = 0; i < ipAddress.length; i++ )
            {
                ipAddressAsString.append( ( ipAddress[i] & 0xFF ) + ( ( i < ipAddress.length - 1 ) ? "." : "" ) );
            }
        }
        return ipAddressAsString.toString();
    }

    public byte getParameterRevision()
    {
        return parameterRevision;
    }

    public void setParameterRevision( byte parameterRevision )
    {
        this.parameterRevision = parameterRevision;
    }

    public byte[] getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress( byte[] ipAddress )
    {
        this.ipAddress = ipAddress;
    }
}
