/* ********************************************************************************
 * IpAllocation.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.vsphere;

/**
 * Author: Tao Ma Date: 2/27/14
 */
public class IpAllocation
{
    /*
     * Re-factor hard code string 2014-08-07
     */
    private final static String IP_ALLOCATION_STR = "IpAllocation{";

    private final static StringBuilder sb = new StringBuilder( IP_ALLOCATION_STR );

    private final static String DHCP_ENABLED_STR = "dhcpEnabled=";

    private final static String NTP_STR = ", ntp='";

    private final static String ADDRESS_STR = ", address='";

    private final static String NETMASK_STR = ", netmask='";

    private final static String GATEWAY_STR = ", gateway='";

    private boolean dhcpEnabled;

    private String address;

    private String netmask;

    private String gateway;

    private String ntp;

    public IpAllocation()
    {
    }

    public IpAllocation( boolean dhcpEnabled )
    {
        this( dhcpEnabled, null, null, null );
    }

    public IpAllocation( String address, String netmask, String gateway )
    {
        this( false, address, netmask, gateway );
    }

    private IpAllocation( boolean dhcpEnabled, String address, String netmask, String gateway )
    {
        this.dhcpEnabled = dhcpEnabled;
        this.address = address;
        this.netmask = netmask;
        this.gateway = gateway;
    }

    public boolean isDhcpEnabled()
    {
        return dhcpEnabled;
    }

    public void setDhcpEnabled( boolean dhcpEnabled )
    {
        this.dhcpEnabled = dhcpEnabled;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress( String address )
    {
        this.address = address;
    }

    public String getNetmask()
    {
        return netmask;
    }

    public void setNetmask( String netmask )
    {
        this.netmask = netmask;
    }

    public String getGateway()
    {
        return gateway;
    }

    public void setGateway( String gateway )
    {
        this.gateway = gateway;
    }

    public String getNtp()
    {
        return ntp;
    }

    public void setNtp( String ntp )
    {
        this.ntp = ntp;
    }

    @Override
    public String toString()
    {
        sb.append( DHCP_ENABLED_STR ).append( dhcpEnabled );
        sb.append( ADDRESS_STR ).append( address ).append( '\'' );
        sb.append( NETMASK_STR ).append( netmask ).append( '\'' );
        sb.append( GATEWAY_STR ).append( gateway ).append( '\'' );
        sb.append( NTP_STR ).append( ntp ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
