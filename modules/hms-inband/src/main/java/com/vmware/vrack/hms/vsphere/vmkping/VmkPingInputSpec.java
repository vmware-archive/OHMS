/* ********************************************************************************
 * VmkPingInputSpec.java
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

import java.io.Serializable;

/**
 * Author: Tao Ma Date: 3/3/14
 */
public class VmkPingInputSpec
    implements Serializable
{
    /*
     * Re-factor hard code string 2014-08-07
     */
    private static final String NETSTACK = ", netstack=";

    private static final String WAIT = ", wait=";

    private static final String TTL = ", ttl=";

    private static final String SIZE = ", size=";

    private static final String NEXTHOP = ", nexthop=";

    private static final String IPV6 = ", ipv6=";

    private static final String IPV4 = ", ipv4=";

    private static final String INTERVAL = ", interval=";

    private static final String NIC = ", nic=";

    private static final String HOST = ", host=";

    private static final String DF = ", df=";

    private static final String DEBUG = ", debug=";

    private static final String VMK_PING_INPUT_SPEC_COUNT = "VmkPingInputSpec [count=";

    /**
     * Specify the number of packets to send. Optional.
     */
    private Integer count = 1;

    /**
     * VMKPing debug mode. Optional.
     */
    private Boolean debug;

    /**
     * Set DF bit on IPv4 packets. Optional.
     */
    private Boolean df = true;

    /**
     * Specify the host to send packets to.
     */
    @VmkPingField( required = true )
    private String host;

    /**
     * Specify the outgoing interface, i.e. device. Optional.
     */
    @VmkPingField( name = "interface" )
    private String nic;

    /**
     * Set the interval for sending packets in seconds. Optional.
     */
    private Integer interval = 1;

    /**
     * Ping with ICMPv4 echo requests. Optional.
     */
    private Boolean ipv4;

    /**
     * Ping with ICMPv6 echo requests. Optional.
     */
    private Boolean ipv6;

    /**
     * Set stack name for vmkping execution, 'vxlan' by default
     */
    @VmkPingField( minVersion = "5.5" )
    private String netstack;

    /**
     * Override the system's default route selection, in dotted quad notation. (IPv4 only. Requires interface option).
     * Gateway. Optional.
     */
    private String nexthop;

    /**
     * Set the payload size of the packets to send, i.e. MTU. Optional.
     */
    private Integer size = 1500 - 28;

    /**
     * Set IPv4 Time To Live or IPv6 Hop Limit.
     */
    private Integer ttl;

    /**
     * Set the timeout to wait if no responses are received in seconds.
     */
    private Integer wait = 3;

    /**
     * @return the count
     */
    public Integer getCount()
    {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount( Integer count )
    {
        this.count = count;
    }

    /**
     * @return the debug
     */
    public Boolean getDebug()
    {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug( Boolean debug )
    {
        this.debug = debug;
    }

    /**
     * @return the df
     */
    public Boolean getDf()
    {
        return df;
    }

    /**
     * @param df the df to set
     */
    public void setDf( Boolean df )
    {
        this.df = df;
    }

    /**
     * @return the host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost( String host )
    {
        this.host = host;
    }

    /**
     * @return the nic
     */
    public String getNic()
    {
        return nic;
    }

    /**
     * @param nic the nic to set
     */
    public void setNic( String nic )
    {
        this.nic = nic;
    }

    /**
     * @return the interval
     */
    public Integer getInterval()
    {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval( Integer interval )
    {
        this.interval = interval;
    }

    /**
     * @return the ipv4
     */
    public Boolean getIpv4()
    {
        return ipv4;
    }

    /**
     * @param ipv4 the ipv4 to set
     */
    public void setIpv4( Boolean ipv4 )
    {
        this.ipv4 = ipv4;
    }

    /**
     * @return the ipv6
     */
    public Boolean getIpv6()
    {
        return ipv6;
    }

    /**
     * @param ipv6 the ipv6 to set
     */
    public void setIpv6( Boolean ipv6 )
    {
        this.ipv6 = ipv6;
    }

    /**
     * @return the nexthop
     */
    public String getNexthop()
    {
        return nexthop;
    }

    /**
     * @param nexthop the nexthop to set
     */
    public void setNexthop( String nexthop )
    {
        this.nexthop = nexthop;
    }

    /**
     * @return the size
     */
    public Integer getSize()
    {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize( Integer size )
    {
        this.size = size;
    }

    /**
     * @return the ttl
     */
    public Integer getTtl()
    {
        return ttl;
    }

    /**
     * @param ttl ttl count to set
     */
    public void setTtl( Integer ttl )
    {
        this.ttl = ttl;
    }

    /**
     * @return the wait
     */
    public Integer getWait()
    {
        return wait;
    }

    /**
     * @param wait the wait to set
     */
    public void setWait( Integer wait )
    {
        this.wait = wait;
    }

    public String getNetstack()
    {
        return netstack;
    }

    public void setNetstack( String netstack )
    {
        this.netstack = netstack;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( VMK_PING_INPUT_SPEC_COUNT );
        builder.append( count );
        builder.append( DEBUG );
        builder.append( debug );
        builder.append( DF );
        builder.append( df );
        builder.append( HOST );
        builder.append( host );
        builder.append( NIC );
        builder.append( nic );
        builder.append( INTERVAL );
        builder.append( interval );
        builder.append( IPV4 );
        builder.append( ipv4 );
        builder.append( IPV6 );
        builder.append( ipv6 );
        builder.append( NEXTHOP );
        builder.append( nexthop );
        builder.append( SIZE );
        builder.append( size );
        builder.append( TTL );
        builder.append( ttl );
        builder.append( WAIT );
        builder.append( wait );
        builder.append( NETSTACK );
        builder.append( netstack );
        builder.append( "]" );
        return builder.toString();
    }
}
