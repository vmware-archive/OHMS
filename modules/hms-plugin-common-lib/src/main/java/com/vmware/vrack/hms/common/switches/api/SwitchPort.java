/* ********************************************************************************
 * SwitchPort.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The abstract SwitchPort represents a generic Top of Rack switch port. Although it provides the basic functionality in
 * most cases, you may extend this class if needed.
 *
 * @author VMware, Inc.
 */
public class SwitchPort
{
    public enum PortStatus
    {
        UP, DOWN
    };

    public enum PortType
    {
        LOOPBACK, MANAGEMENT, SERVER, SYNC, EXTERNAL, UPLINK
    };

    public enum PortDuplexMode
    {
        FULL, HALF
    };

    public enum PortAutoNegMode
    {
        ON, OFF
    };

    public int getIfNumber()
    {
        return ifNumber;
    }

    public void setIfNumber( int ifNumber )
    {
        this.ifNumber = ifNumber;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getSpeed()
    {
        return speed;
    }

    public void setSpeed( String speed )
    {
        this.speed = speed;
    }

    public PortDuplexMode getDuplex()
    {
        return duplex;
    }

    public void setDuplex( PortDuplexMode duplex )
    {
        this.duplex = duplex;
    }

    public PortAutoNegMode getAutoneg()
    {
        return autoneg;
    }

    public void setAutoneg( PortAutoNegMode autoneg )
    {
        this.autoneg = autoneg;
    }

    public String getFlags()
    {
        return flags;
    }

    public void setFlags( String flags )
    {
        this.flags = flags;
    }

    public int getMtu()
    {
        return mtu;
    }

    public void setMtu( int mtu )
    {
        this.mtu = mtu;
    }

    public PortStatus getStatus()
    {
        return status;
    }

    public void setStatus( PortStatus status )
    {
        this.status = status;
    }

    public PortType getType()
    {
        return type;
    }

    public void setType( PortType type )
    {
        this.type = type;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public void setMacAddress( String macAddress )
    {
        this.macAddress = macAddress;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress( String ipAddress )
    {
        this.ipAddress = ipAddress;
    }

    public SwitchPortStatistics getStatistics()
    {
        return statistics;
    }

    public void setStatistics( SwitchPortStatistics statistics )
    {
        this.statistics = statistics;
    }

    public Set<String> getLinkedMacAddresses()
    {
        return linkedMacAddresses;
    }

    public void setLinkedMacAddresses( Set<String> linkedMacAddresses )
    {
        this.linkedMacAddresses = linkedMacAddresses;
    }

    public SwitchLinkedPort getLinkedPort()
    {
        return linkedPort;
    }

    public void setLinkedPort( SwitchLinkedPort linkedPort )
    {
        this.linkedPort = linkedPort;
    }

    // TODO: Ugly. Do we really need this code?
    /*
     * +++rsen: Not touching this deprecated method to account for new fields
     */
    @JsonIgnore
    public Map<String, String> getObjMap()
    {
        Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put( "number", Integer.toString( ifNumber ) );
        valueMap.put( "name", name );
        valueMap.put( "speed", speed );
        valueMap.put( "flags", flags );
        valueMap.put( "status", status.toString() );
        valueMap.put( "type", type.toString() );
        valueMap.put( "macAddress", macAddress );
        return valueMap;
    }

    protected int ifNumber;

    protected String name;

    protected String speed;

    protected String flags;

    protected int mtu;

    protected PortDuplexMode duplex;

    protected PortAutoNegMode autoneg;

    protected PortStatus status;

    protected PortType type;

    protected String macAddress;

    protected String ipAddress;

    protected SwitchPortStatistics statistics = new SwitchPortStatistics();

    protected Set<String> linkedMacAddresses;

    protected SwitchLinkedPort linkedPort; /*
                                            * We wont allow multiple remote NICs to connect to one port of ToR switch
                                            */

    @SuppressWarnings( "unused" )
    private static Logger logger = Logger.getLogger( SwitchPort.class );
}
