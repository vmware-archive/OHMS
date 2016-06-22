/* ********************************************************************************
 * NBSwitchLagConfig.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model.switches;

import java.util.Set;

public class NBSwitchLagConfig
{
    private String name;

    private String mode;

    private Integer mtu;

    private NBSwitchNetworkPrefix ipAddress;

    private Set<String> ports;

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
     * @return the mode
     */
    public String getMode()
    {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode( String mode )
    {
        this.mode = mode;
    }

    /**
     * @return the mtu
     */
    public Integer getMtu()
    {
        return mtu;
    }

    /**
     * @param mtu the mtu to set
     */
    public void setMtu( Integer mtu )
    {
        this.mtu = mtu;
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

    /**
     * @return the ports
     */
    public Set<String> getPorts()
    {
        return ports;
    }

    /**
     * @param ports the ports to set
     */
    public void setPorts( Set<String> ports )
    {
        this.ports = ports;
    }
}
