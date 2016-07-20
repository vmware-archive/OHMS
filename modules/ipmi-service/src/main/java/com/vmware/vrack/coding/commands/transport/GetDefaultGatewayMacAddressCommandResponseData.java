package com.vmware.vrack.coding.commands.transport;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;

/**
 * Wrapper Class for Get Default Gateway Mac Address Response
 * 
 * @author Yagnesh Chawda
 */
public class GetDefaultGatewayMacAddressCommandResponseData
    implements ResponseData
{
    private byte parameterRevision;

    private byte[] defaultGatewayMacAddress;

    // returns the mac address in format FF-FF-FF-FF-FF-FF
    public String getDefaultGatewayMacAddressAsString()
    {
        StringBuilder macAddressAsString = new StringBuilder();
        if ( defaultGatewayMacAddress != null )
        {
            for ( int i = 0; i < defaultGatewayMacAddress.length; i++ )
            {
                macAddressAsString.append( String.format( "%02X%s", defaultGatewayMacAddress[i],
                                                          ( i < defaultGatewayMacAddress.length - 1 ) ? "-" : "" ) );
            }
        }
        return macAddressAsString.toString();
    }

    public byte getParameterRevision()
    {
        return parameterRevision;
    }

    public void setParameterRevision( byte parameterRevision )
    {
        this.parameterRevision = parameterRevision;
    }

    public byte[] getDefaultGatewayMacAddress()
    {
        return defaultGatewayMacAddress;
    }

    public void setDefaultGatewayMacAddress( byte[] defaultGatewayMacAddress )
    {
        this.defaultGatewayMacAddress = defaultGatewayMacAddress;
    }
}
