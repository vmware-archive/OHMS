/* ********************************************************************************
 * NBSwitchPortConfig.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model.switches;

public class NBSwitchPortConfig
{
    public enum PortType
    {
        SYNC, EXTERNAL, UPLINK, LOOPBACK, MANAGEMENT, SERVER
    }

    public enum PortDuplexMode
    {
        FULL, HALF
    };

    public enum PortAutoNegMode
    {
        ON, OFF
    };

    private String speed;

    private int mtu;

    /*
     * 'type' can be set by VRM only for values SYNC, EXTERNAL, SERVER and UPLINK for relevant ports, the set will fail
     * on a port which is either of LOOPBACK or MANAGEMENT
     */
    private PortType type;

    private PortDuplexMode duplex;

    private PortAutoNegMode autoneg;

    private NBSwitchNetworkPrefix ipAddress;

    /**
     * @return the speed
     */
    public String getSpeed()
    {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed( String speed )
    {
        this.speed = speed;
    }

    /**
     * @return the mtu
     */
    public int getMtu()
    {
        return mtu;
    }

    /**
     * @param mtu the mtu to set
     */
    public void setMtu( int mtu )
    {
        this.mtu = mtu;
    }

    /**
     * @return the type
     */
    public PortType getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType( PortType type )
    {
        this.type = type;
    }

    /**
     * @return the duplex
     */
    public PortDuplexMode getDuplex()
    {
        return duplex;
    }

    /**
     * @param duplex the duplex to set
     */
    public void setDuplex( PortDuplexMode duplex )
    {
        this.duplex = duplex;
    }

    /**
     * @return the autoneg
     */
    public PortAutoNegMode getAutoneg()
    {
        return autoneg;
    }

    /**
     * @param autoneg the autoneg to set
     */
    public void setAutoneg( PortAutoNegMode autoneg )
    {
        this.autoneg = autoneg;
    }

    /**
     * @return the ipAddress
     */
    public NBSwitchNetworkPrefix getIpAddress()
    {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress( NBSwitchNetworkPrefix ipAddress )
    {
        this.ipAddress = ipAddress;
    }
}
