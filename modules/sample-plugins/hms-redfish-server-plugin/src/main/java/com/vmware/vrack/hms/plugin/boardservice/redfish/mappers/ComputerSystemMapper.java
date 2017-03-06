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

import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.BiosBootType;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceSelector;
import com.vmware.vrack.hms.common.resource.chassis.BootOptionsValidity;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource;

/**
 * Class responsible for mapping Redfish ComputerSystem properties to HMS-specific data model objects
 */
public final class ComputerSystemMapper
{
    /**
     * Performs mapping parts of Redfish ComputerSystem data into HMS ServerNodeInfo
     * Omitting properties below, as they are relative to NB REST service that exposes them
     * nodeInfo.setoDataId( computerSystem.getOdataId() );
     * nodeInfo.setoDataContext( computerSystem.getOdataContext() );
     * Omitting unavailable properties
     * fruInfo.setManufacturingDate( ? );
     *
     * @param computerSystem Redfish ComputerSystem Resource
     * @return ServerNodeInfo
     */
    public ServerNodeInfo mapNodeInfo( ComputerSystemResource computerSystem )
    {
        ServerNodeInfo nodeInfo = new ServerNodeInfo();
        nodeInfo.setId( computerSystem.getId() );
        nodeInfo.setComponent( ServerComponent.SERVER );

        ComponentIdentifier fruInfo = new ComponentIdentifier();

        fruInfo.setProduct( computerSystem.getModel() );
        fruInfo.setDescription( computerSystem.getDescription() );
        fruInfo.setManufacturer( computerSystem.getManufacturer() );
        fruInfo.setPartNumber( computerSystem.getPartNumber() );
        fruInfo.setSerialNumber( computerSystem.getSerialNumber() );

        nodeInfo.setComponentIdentifier( fruInfo );
        return nodeInfo;
    }

    /**
     * Performs mapping parts of Redfish ComputerSystem data into HMS SystemBootOptions
     *
     * @param computerSystem Redfish ComputerSystem Resource
     * @return SystemBootOptions
     */
    public SystemBootOptions mapBootOptions( ComputerSystemResource computerSystem )
    {
        SystemBootOptions bootOptions = new SystemBootOptions();
        ComputerSystemResource.Boot boot = computerSystem.getBoot();

        if ( boot.getBootSourceOverrideEnabled() != null )
        {
            switch ( boot.getBootSourceOverrideEnabled() )
            {
                case Once:
                    bootOptions.setBootOptionsValidity( BootOptionsValidity.NextBootOnly );
                    break;
                case Continuous:
                    bootOptions.setBootOptionsValidity( BootOptionsValidity.Persistent );
                    break;
                case Disabled:
                default:
                    break;
            }
        }

        if ( boot.getBootSourceOverrideTarget() != null )
        {
            switch ( boot.getBootSourceOverrideTarget() )
            {
                case None:
                    bootOptions.setBootDeviceSelector( BootDeviceSelector.No_Override );
                    break;
                case Pxe:
                    bootOptions.setBootDeviceSelector( BootDeviceSelector.PXE );
                    break;
                case Hdd:
                    bootOptions.setBootDeviceSelector( BootDeviceSelector.Default_Hard_Disk );
                    break;
                case BiosSetup:
                case Utilities:
                case Diags:
                case UefiTarget:
                case Floppy:
                case Cd:
                case Usb:
                default:
                    break;
            }
        }

        bootOptions.setBiosBootType( BiosBootType.Legacy );
        bootOptions.setBootFlagsValid( true );

        return bootOptions;
    }
}
