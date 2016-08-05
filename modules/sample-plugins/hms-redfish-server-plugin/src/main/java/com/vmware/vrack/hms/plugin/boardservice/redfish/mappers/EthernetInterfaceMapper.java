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

import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.SpeedInfo;
import com.vmware.vrack.hms.common.servernodes.api.SpeedUnit;
import com.vmware.vrack.hms.common.servernodes.api.nic.NicStatus;
import com.vmware.vrack.hms.common.servernodes.api.nic.PortInfo;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.EthernetInterfaceResource;

import java.util.ArrayList;
import java.util.List;

public final class EthernetInterfaceMapper
{
    public EthernetController mapEthernetController( EthernetInterfaceResource ethernetInterface )
    {
        EthernetController controller = new EthernetController();

        controller.setComponent( ServerComponent.NIC );

        // controller.setFirmwareVersion( ? );
        // controller.setPciDeviceId( ? );

        List<PortInfo> portInfos = new ArrayList<>();
        PortInfo info = new PortInfo();

        info.setMacAddress( ethernetInterface.getMacAddress() );
        // info.setDeviceName( ? );

        if ( ethernetInterface.getInterfaceEnabled() != null )
        {
            info.setLinkStatus( ethernetInterface.getInterfaceEnabled() ? NicStatus.OK : NicStatus.DISCONNECTED );
        }

        if ( ethernetInterface.getSpeedMbps() != null )
        {
            SpeedInfo speedInfo = new SpeedInfo();
            speedInfo.setSpeed( ethernetInterface.getSpeedMbps().longValue() );
            speedInfo.setUnit( SpeedUnit.Mbps );
            info.setLinkSpeedInMBps( speedInfo );

            controller.setSpeedInMbps( ethernetInterface.getSpeedMbps().toString() );
        }

        portInfos.add( info );
        controller.setPortInfos( portInfos );

        return controller;
    }

    public String mapManagementMacAddress( List<EthernetInterfaceResource> nics )
    {
        for ( EthernetInterfaceResource nic : nics )
        {
            // TODO assuming single management port available, Redfish allows multiple
            return nic.getMacAddress();
        }
        return null;
    }
}
