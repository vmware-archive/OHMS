/* ********************************************************************************
 * VmkPingSummary.java
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

package com.vmware.vrack.hms.vsphere.vmkping;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.io.Serializable;

/**
 * Author: Tao Ma Date: 3/3/14
 */
public class VmkPingSummary
    implements Serializable
{
    /*
     * Re-factor hard code string 2014-08-07
     */
    private static final String ROUND_TRIP_MIN_AVG_MAX_S_S_S_MS = "round-trip min/avg/max = %s/%s/%s ms\n";

    private static final String S_PACKETS_TRANSMITTED_S_PACKETS_RECEIVED_S_PACKET_LOSS =
        "%s packets transmitted, %s packets received, %s packet loss\n";

    private static final String S_PING_STATISTICS = "--- %s ping statistics ---\n";

    private static final long serialVersionUID = 1L;

    @XStreamAlias( "HostAddr" )
    private String hostAddr;

    @XStreamOmitField
    private String hostName;

    @XStreamAlias( "Transmitted" )
    private int transmitted;

    @XStreamAlias( "Recieved" )
    private int received;

    @XStreamAlias( "Duplicated" )
    private int duplicated;

    @XStreamAlias( "PacketLost" )
    private int packetLost;

    @XStreamAlias( "RoundtripMinMS" )
    private int roundtripMin;

    @XStreamAlias( "RoundtripAvgMS" )
    private int roundtripAvg;

    @XStreamAlias( "RoundtripMaxMS" )
    private int roundtripMax;

    /**
     * @return the hostAddr
     */
    public String getHostAddr()
    {
        return hostAddr;
    }

    /**
     * @param hostAddr the hostAddr to set
     */
    public void setHostAddr( String hostAddr )
    {
        this.hostAddr = hostAddr;
    }

    /**
     * @return the hostName
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public void setHostName( String hostName )
    {
        this.hostName = hostName;
    }

    /**
     * @return the transmitted
     */
    public int getTransmitted()
    {
        return transmitted;
    }

    /**
     * @param transmitted the transmitted to set
     */
    public void setTransmitted( int transmitted )
    {
        this.transmitted = transmitted;
    }

    /**
     * @return the received
     */
    public int getReceived()
    {
        return received;
    }

    /**
     * @param received the received to set
     */
    public void setReceived( int received )
    {
        this.received = received;
    }

    /**
     * @return the duplicated
     */
    public int getDuplicated()
    {
        return duplicated;
    }

    /**
     * @param duplicated the duplicated to set
     */
    public void setDuplicated( int duplicated )
    {
        this.duplicated = duplicated;
    }

    /**
     * @return the packetLost
     */
    public int getPacketLost()
    {
        return packetLost;
    }

    /**
     * @param packetLost the packetLost to set
     */
    public void setPacketLost( int packetLost )
    {
        this.packetLost = packetLost;
    }

    /**
     * @return the roundtripMin
     */
    public int getRoundtripMin()
    {
        return roundtripMin;
    }

    /**
     * @param roundtripMin the roundtripMin to set
     */
    public void setRoundtripMin( int roundtripMin )
    {
        this.roundtripMin = roundtripMin;
    }

    /**
     * @return the roundtripAvg
     */
    public int getRoundtripAvg()
    {
        return roundtripAvg;
    }

    /**
     * @param roundtripAvg the roundtripAvg to set
     */
    public void setRoundtripAvg( int roundtripAvg )
    {
        this.roundtripAvg = roundtripAvg;
    }

    /**
     * @return the roundtripMax
     */
    public int getRoundtripMax()
    {
        return roundtripMax;
    }

    /**
     * @param roundtripMax the roundtripMax to set
     */
    public void setRoundtripMax( int roundtripMax )
    {
        this.roundtripMax = roundtripMax;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( String.format( S_PING_STATISTICS, hostAddr ) );
        builder.append( String.format( S_PACKETS_TRANSMITTED_S_PACKETS_RECEIVED_S_PACKET_LOSS, transmitted, received,
                                       packetLost + "%" ) );
        builder.append( String.format( ROUND_TRIP_MIN_AVG_MAX_S_S_S_MS, roundtripMin, roundtripAvg, roundtripMax ) );
        return builder.toString();
    }
}
