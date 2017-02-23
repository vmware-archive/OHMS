/* ********************************************************************************
 * SwitchPortConfig.java
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
package com.vmware.vrack.hms.common.core.model.switches;

import java.util.List;

import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;

public class SwitchPortConfig
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

    private String name;

    private String speed;

    private int mtu;

    /*
     * 'type' can be set by VRM only for values SYNC, EXTERNAL, SERVER and UPLINK for relevant ports, the set will fail
     * on a port which is either of LOOPBACK or MANAGEMENT
     */
    private PortType type;

    private PortDuplexMode duplex;

    private PortAutoNegMode autoneg;

    private NodeAdminStatus adminStatus;

    private List<String> ipAddresses;

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName( String name )
    {
        this.name = name;
    }

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
     * @return the status
     */
    public NodeAdminStatus getAdminStatus()
    {
        return adminStatus;
    }

    /**
     * @param status the status to set
     */
    public void setAdminStatus( NodeAdminStatus status )
    {
        this.adminStatus = status;
    }

    /**
     * @return the ipAddresses
     */
    public List<String> getIpAddresses()
    {
        return ipAddresses;
    }

    /**
     * @param ipAddresses the ipAddresses to set
     */
    public void setIpAddresses( List<String> ipAddresses )
    {
        this.ipAddresses = ipAddresses;
    }
}
