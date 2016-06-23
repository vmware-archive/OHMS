/* ********************************************************************************
 * NicStatisticsInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
