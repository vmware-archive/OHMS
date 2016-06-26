package com.vmware.vrack.hms.plugin.boardservice.redfish.mappers;

import com.vmware.vrack.hms.common.exception.OperationNotSupportedOOBException;
import com.vmware.vrack.hms.common.resource.PowerOperationAction;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource.Actions.ResetType;

public class ResetTypeMapper
{
    public ResetType map( PowerOperationAction action )
        throws OperationNotSupportedOOBException
    {
        switch ( action )
        {
            case POWERDOWN:
                return ResetType.ForceOff;
            case POWERUP:
                return ResetType.On;
            case COLDRESET:
                throw new OperationNotSupportedOOBException( action + " is not supported" );
            case HARDRESET:
                // this is not obvious, will all redfish implementations do hard reset for GracefulRestart?
                return ResetType.GracefulRestart;
            case POWERCYCLE:
                return ResetType.ForceRestart;
            default:
                throw new OperationNotSupportedOOBException( action + " is not supported" );
        }
    }
}
