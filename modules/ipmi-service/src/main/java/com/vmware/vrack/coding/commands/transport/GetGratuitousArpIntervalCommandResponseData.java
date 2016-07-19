package com.vmware.vrack.coding.commands.transport;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;

/**
 * Wrapper class for Get Gratuitous ARP Interval
 * 
 * @author Yagnesh Chawda
 */
public class GetGratuitousArpIntervalCommandResponseData
    implements ResponseData
{
    private byte parameterRevision;

    private byte gratuitousArpIntervalCode;

    public double getGratuitousArpIntervalInSeconds()
    {
        double interval = ( gratuitousArpIntervalCode & 0xFF ) / 2;
        return interval;
    }

    public byte getParameterRevision()
    {
        return parameterRevision;
    }

    public void setParameterRevision( byte parameterRevision )
    {
        this.parameterRevision = parameterRevision;
    }

    public byte getGratuitousArpIntervalCode()
    {
        return gratuitousArpIntervalCode;
    }

    public void setGratuitousArpIntervalCode( byte gratuitousArpIntervalCode )
    {
        this.gratuitousArpIntervalCode = gratuitousArpIntervalCode;
    }
}
