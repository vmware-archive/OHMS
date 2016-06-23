package com.vmware.vrack.coding.commands.transport;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;

/**
 * Wrapper class for Get Default Address IP Address Command
 * 
 * @author Yagnesh Chawda
 */
public class GetDefaultGatewayIpAddressCommandResponseData
    implements ResponseData
{
    private byte parameterRevision;

    private byte[] defaultGatewayIpAddress;

    public String getDefaultGatewayIpAddressAsString()
    {
        StringBuilder ipAddressAsString = new StringBuilder();
        if ( defaultGatewayIpAddress != null )
        {
            for ( int i = 0; i < defaultGatewayIpAddress.length; i++ )
            {
                ipAddressAsString.append( ( defaultGatewayIpAddress[i] & 0xFF )
                    + ( ( i < defaultGatewayIpAddress.length - 1 ) ? "." : "" ) );
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

    public byte[] getDefaultGatewayIpAddress()
    {
        return defaultGatewayIpAddress;
    }

    public void setDefaultGatewayIpAddress( byte[] defaultGatewayIpAddress )
    {
        this.defaultGatewayIpAddress = defaultGatewayIpAddress;
    }
}
