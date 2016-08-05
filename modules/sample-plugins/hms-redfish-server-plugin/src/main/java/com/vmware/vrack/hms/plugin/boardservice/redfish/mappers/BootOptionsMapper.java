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
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceSelector;
import com.vmware.vrack.hms.common.resource.chassis.BootOptionsValidity;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource.Boot;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource.Boot.BootSourceState;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource.Boot.BootSourceType;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class BootOptionsMapper
{
    private final static Set<BootDeviceSelector> SUPPORTED_BOOT_DEVICE_SELECTORS = new HashSet<>(
        asList( BootDeviceSelector.Default_Hard_Disk, BootDeviceSelector.No_Override, BootDeviceSelector.PXE )
    );

    public Boot map( SystemBootOptions data )
        throws OperationNotSupportedOOBException
    {
        BootDeviceSelector deviceSelector = data.getBootDeviceSelector();
        if ( deviceSelector != null && !SUPPORTED_BOOT_DEVICE_SELECTORS.contains( deviceSelector ) )
        {
            throw new OperationNotSupportedOOBException( "Device selector: " + deviceSelector + " is not supported" );
        }

        Boot bootOptions = new Boot();

        if ( deviceSelector != null )
        {
            bootOptions.setBootSourceOverrideTarget( mapBootSourceTarget( deviceSelector ) );
        }

        if ( data.getBootOptionsValidity() != null )
        {
            bootOptions.setBootSourceOverrideEnabled( mapBootSourceValidity( data.getBootOptionsValidity() ) );
        }

        return bootOptions;
    }

    private BootSourceType mapBootSourceTarget( BootDeviceSelector deviceSelector )
        throws OperationNotSupportedOOBException
    {
        switch ( deviceSelector )
        {
            case No_Override:
                return BootSourceType.None;
            case Default_Hard_Disk:
                return BootSourceType.Hdd;
            case PXE:
                return BootSourceType.Pxe;
            default:
                throw new OperationNotSupportedOOBException(
                    "Setting boot source target to: " + deviceSelector + " is not supported"
                );
        }
    }

    private BootSourceState mapBootSourceValidity( BootOptionsValidity bootOptionsValidity )
        throws OperationNotSupportedOOBException
    {
        switch ( bootOptionsValidity )
        {
            case NextBootOnly:
                return BootSourceState.Once;
            case Persistent:
                return BootSourceState.Continuous;
            default:
                throw new OperationNotSupportedOOBException(
                    "Setting boot source validity to: " + bootOptionsValidity + " is not supported"
                );
        }
    }
}
