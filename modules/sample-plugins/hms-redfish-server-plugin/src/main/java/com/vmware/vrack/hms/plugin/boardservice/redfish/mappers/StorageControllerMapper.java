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
