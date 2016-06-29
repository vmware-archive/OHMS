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
