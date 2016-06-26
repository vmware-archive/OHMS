package com.vmware.vrack.hms.plugin.boardservice.redfish.mappers;

import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.SimpleStorageResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.Status;

public class StorageControllerMapper
{
    public StorageControllerInfo map( SimpleStorageResource simpleStorage )
    {
        StorageControllerInfo storageControllerInfo = new StorageControllerInfo();
        storageControllerInfo.setDeviceName( simpleStorage.getName() );
        storageControllerInfo.setNumOfStorageDevicesConnected( simpleStorage.getDevices().size() );
        storageControllerInfo.setFruOperationalStatus( mapStatus( simpleStorage.getStatus() ) );
        return storageControllerInfo;
    }

    private FruOperationalStatus mapStatus( Status status )
    {
        if ( status.getHealth().equals( "OK" ) && status.getState() == Status.State.Enabled )
        {
            return FruOperationalStatus.Operational;
        }
        return FruOperationalStatus.NonOperational;
    }
}
