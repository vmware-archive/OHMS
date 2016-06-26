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
