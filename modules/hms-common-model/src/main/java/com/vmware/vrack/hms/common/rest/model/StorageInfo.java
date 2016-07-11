/* ********************************************************************************
 * StorageInfo.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.StatusEnum;
import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;

/**
 * Class for Storage related properties
 *
 * @author VMware Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class StorageInfo
    extends FruComponent
{
    private String id;

    private long diskCapacityInMB;

    private String diskType;

    private String firmwareVersion;

    private String operationalStatus;

    private String hostId;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getDiskType()
    {
        return diskType;
    }

    public void setDiskType( String diskType )
    {
        this.diskType = diskType;
    }

    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }

    public void setFirmwareVersion( String firmwareVersion )
    {
        this.firmwareVersion = firmwareVersion;
    }

    public long getDiskCapacityInMB()
    {
        return diskCapacityInMB;
    }

    public void setDiskCapacityInMB( long diskCapacityInMB )
    {
        this.diskCapacityInMB = diskCapacityInMB;
    }

    public String getOperationalStatus()
    {
        return operationalStatus;
    }

    public void setOperationalStatus( String operationalStatus )
    {
        this.operationalStatus = operationalStatus;
    }

    public String getHostId()
    {
        return hostId;
    }

    public void setHostId( String hostId )
    {
        this.hostId = hostId;
    }

    /**
     * Get the Physical Storage Device or HDD FRU Information Wrapper method to get the StorageInfo object for the node
     *
     * @param serverNodeHddInfo
     * @param nodeID
     * @return StorageInfo
     */
    public StorageInfo getStorageInfo( HddInfo serverNodeHddInfo, String nodeID )
    {
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setId( serverNodeHddInfo.getId() );
        storageInfo.setDiskCapacityInMB( serverNodeHddInfo.getDiskCapacityInMB() );
        storageInfo.setDiskType( serverNodeHddInfo.getType() );
        storageInfo.setFirmwareVersion( serverNodeHddInfo.getFirmwareInfo() );
        storageInfo.setOperationalStatus( getStorageOperationaState( serverNodeHddInfo.getState() ).name() );
        storageInfo.setLocation( serverNodeHddInfo.getLocation() );
        storageInfo.setHostId( nodeID );
        if ( serverNodeHddInfo.getComponentIdentifier() != null )
        {
            storageInfo.setComponentIdentifier( serverNodeHddInfo.getComponentIdentifier() );
        }
        return storageInfo;
    }

    /**
     * Get the operational status of the storage device
     *
     * @param state
     * @return FruOperationalStatus
     */
    public static FruOperationalStatus getStorageOperationaState( StatusEnum state )
    {
        if ( state != null )
        {
            switch ( state )
            {
                case OK:
                case DEGRADED:
                    return FruOperationalStatus.Operational;
                case ERROR:
                case OFF:
                case QUIESCED:
                case LOSTCOMMUNICATION:
                case TIMEOUT:
                    return FruOperationalStatus.NonOperational;
                case UNKNOWNSTATE:
                    return FruOperationalStatus.UnKnown;
                default:
                    throw new IllegalArgumentException( "Invalid state: " + state );
            }
        }
        else
        {
            throw new IllegalArgumentException( "Invalid state: " + state );
        }
    }
}
