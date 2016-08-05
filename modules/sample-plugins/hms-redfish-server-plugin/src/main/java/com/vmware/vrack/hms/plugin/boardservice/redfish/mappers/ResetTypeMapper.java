/*
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
