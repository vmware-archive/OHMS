package com.vmware.vrack.coding.commands.chassis;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;

/**
 * Wrapper class for Chassis Identify Command Response
 * 
 * @author Yagnesh Chawda
 */
public class ChassisIdentifyCommandResponseData
    implements ResponseData
{
    private Boolean forceChassisIdentifySupport;

    public Boolean getForceChassisIdentifySupport()
    {
        return forceChassisIdentifySupport;
    }

    public void setForceChassisIdentifySupport( Boolean forceChassisIdentifySupport )
    {
        this.forceChassisIdentifySupport = forceChassisIdentifySupport;
    }
}
