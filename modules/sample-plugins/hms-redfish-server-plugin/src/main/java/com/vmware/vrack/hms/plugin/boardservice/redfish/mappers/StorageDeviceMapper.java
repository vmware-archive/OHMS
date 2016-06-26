package com.vmware.vrack.hms.plugin.boardservice.redfish.mappers;

import com.vmware.vrack.hms.common.StatusEnum;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.SimpleStorageResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.Status;

import java.math.BigInteger;

public class StorageDeviceMapper
{
    public HddInfo mapDevice( SimpleStorageResource.Device device )
    {
        HddInfo hddInfo = new HddInfo();
        hddInfo.setName( device.getName() );
        hddInfo.setType( device.getModel() );
        hddInfo.setState( mapDiskStatus( device.getStatus() ) );
        hddInfo.setDiskCapacityInMB( bytesToMB( device.getCapacityBytes() ) );

        return hddInfo;
    }

    private StatusEnum mapDiskStatus( Status status )
    {
        switch ( status.getState() )
        {
            case Enabled:
                return StatusEnum.OK;
            case Offline:
                return StatusEnum.OFF;
            case Disabled:
                return StatusEnum.QUIESCED;
            case Absent:
            case InTest:
            case Starting:
            default:
                return StatusEnum.UNKNOWN;
        }
    }

    private long bytesToMB( BigInteger bytes )
    {
        return bytes.divide( BigInteger.valueOf( 1000 * 1000 ) ).longValue();
    }
}
