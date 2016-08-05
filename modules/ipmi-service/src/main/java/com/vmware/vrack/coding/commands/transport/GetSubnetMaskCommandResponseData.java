package com.vmware.vrack.coding.commands.transport;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;

/**
 * Wrapper class for Get Subnet Mask Command
 * 
 * @author Yagnesh Chawda
 */
public class GetSubnetMaskCommandResponseData
    implements ResponseData
{
    private byte parameterRevision;

    private byte[] subnetMask;

    public String getSubnetMaskAsString()
    {
        StringBuilder subnetMaskAsString = new StringBuilder();
        if ( subnetMask != null )
        {
            for ( int i = 0; i < subnetMask.length; i++ )
            {
                subnetMaskAsString.append( ( subnetMask[i] & 0xFF ) + ( ( i < subnetMask.length - 1 ) ? "." : "" ) );
            }
        }
        return subnetMaskAsString.toString();
    }

    public byte getParameterRevision()
    {
        return parameterRevision;
    }

    public void setParameterRevision( byte parameterRevision )
    {
        this.parameterRevision = parameterRevision;
    }

    public byte[] getSubnetMask()
    {
        return subnetMask;
    }

    public void setSubnetMask( byte[] subnetMask )
    {
        this.subnetMask = subnetMask;
    }
}
