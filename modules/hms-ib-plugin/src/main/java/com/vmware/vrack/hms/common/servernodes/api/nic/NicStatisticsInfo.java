/* ********************************************************************************
 * NicStatisticsInfo.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api.nic;

/**
 * Nic Statistics info. e.g. Transmitted Packet Info, Received packet info etc.
 * 
 * @author VMware Inc.
 */
public class NicStatisticsInfo
{
    private Long packetsReceived;

    private Long packetsTransmitted;

    private Long receivePacketDrops;

    private Long transmitPacketDrops;

    public Long getPacketsReceived()
    {
        return packetsReceived;
    }

    public void setPacketsReceived( Long packetsReceived )
    {
        this.packetsReceived = packetsReceived;
    }

    public Long getPacketsTransmitted()
    {
        return packetsTransmitted;
    }

    public void setPacketsTransmitted( Long packetsTransmitted )
    {
        this.packetsTransmitted = packetsTransmitted;
    }

    public Long getReceivePacketDrops()
    {
        return receivePacketDrops;
    }

    public void setReceivePacketDrops( Long receivePacketDrops )
    {
        this.receivePacketDrops = receivePacketDrops;
    }

    public Long getTransmitPacketDrops()
    {
        return transmitPacketDrops;
    }

    public void setTransmitPacketDrops( Long transmitPacketDrops )
    {
        this.transmitPacketDrops = transmitPacketDrops;
    }
}
