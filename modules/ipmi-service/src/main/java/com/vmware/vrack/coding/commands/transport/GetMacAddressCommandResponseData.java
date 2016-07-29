package com.vmware.vrack.coding.commands.transport;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;

/**
 * Wrapper Class for Get Mac Address Response
 * 
 * @author Yagnesh Chawda
 */
public class GetMacAddressCommandResponseData
    implements ResponseData
{
    private byte parameterRevision;

    private byte[] macAddress;

    public byte getParameterRevision()
    {
        return parameterRevision;
    }

    public void setParameterRevision( byte parameterRevision )
    {
        this.parameterRevision = parameterRevision;
    }

    // returns the mac address in format FF-FF-FF-FF-FF-FF
    public String getMacAddressAsString()
    {
        StringBuilder macAddressAsString = new StringBuilder();
        if ( macAddress != null )
        {
            for ( int i = 0; i < macAddress.length; i++ )
            {
                macAddressAsString.append( String.format( "%02X%s", macAddress[i],
                                                          ( i < macAddress.length - 1 ) ? "-" : "" ) );
            }
        }
        return macAddressAsString.toString();
    }

    public byte[] getMacAddressAsByteArray()
    {
        return macAddress;
    }

    public void setMacAddress( byte[] macAddress )
    {
        this.macAddress = macAddress;
    }
}
