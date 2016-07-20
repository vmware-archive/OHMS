/* ********************************************************************************
 * VmkPingTrace.java
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
@XStreamAlias( "Trace" )
public class VmkPingTrace
    implements Serializable
{
    /*
     * Re-factor hard code string 2014-08-07
     */
    private static final String S_BYTES_FROM_S_ICMP_SEQ_S_TTL_S_TIME_S_MS =
        "%s bytes from %s: icmp_seq=%s ttl=%s time=%s ms\n";

    private static final long serialVersionUID = 1L;

    @XStreamAlias( "ReceivedBytes" )
    private int receivedBytes;

    @XStreamAlias( "Host" )
    private String hostAddr;

    @XStreamOmitField
    private String hostName;

    @XStreamAlias( "ICMPSeq" )
    private int icmpSeq;

    @XStreamAlias( "TTL" )
    private int ttl;

    @XStreamAlias( "RoundtripTimeMS" )
    private int roundtripTime;

    @XStreamAlias( "Dup" )
    private boolean dup;

    @XStreamAlias( "Detail" )
    private String detail;

    /**
     * @return the receivedBytes
     */
    public int getReceivedBytes()
    {
        return receivedBytes;
    }

    /**
     * @param receivedBytes the receivedBytes to set
     */
    public void setReceivedBytes( int receivedBytes )
    {
        this.receivedBytes = receivedBytes;
    }

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
     * @return the icmpSeq
     */
    public int getIcmpSeq()
    {
        return icmpSeq;
    }

    /**
     * @param icmpSeq the icmpSeq to set
     */
    public void setIcmpSeq( int icmpSeq )
    {
        this.icmpSeq = icmpSeq;
    }

    /**
     * @return the ttl
     */
    public int getTtl()
    {
        return ttl;
    }

    /**
     * @param ttl the ttl to set
     */
    public void setTtl( int ttl )
    {
        this.ttl = ttl;
    }

    /**
     * @return the roundtripTime
     */
    public int getRoundtripTime()
    {
        return roundtripTime;
    }

    /**
     * @param roundtripTime the roundtripTime to set
     */
    public void setRoundtripTime( int roundtripTime )
    {
        this.roundtripTime = roundtripTime;
    }

    /**
     * @return the dup
     */
    public boolean isDup()
    {
        return dup;
    }

    /**
     * @param dup the dup to set
     */
    public void setDup( boolean dup )
    {
        this.dup = dup;
    }

    /**
     * @return the detail
     */
    public String getDetail()
    {
        return detail;
    }

    /**
     * @param detail the detail to set
     */
    public void setDetail( String detail )
    {
        this.detail = detail;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format( S_BYTES_FROM_S_ICMP_SEQ_S_TTL_S_TIME_S_MS, receivedBytes, hostAddr, icmpSeq, ttl,
                              roundtripTime );
    }
}
