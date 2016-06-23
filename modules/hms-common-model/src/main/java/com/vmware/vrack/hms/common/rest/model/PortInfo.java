/* ********************************************************************************
 * PortInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.servernodes.api.SpeedInfo;
import com.vmware.vrack.hms.common.servernodes.api.nic.NicStatus;

/**
 * Class for Ethernet controllers Port Properties
 *
 * @author VMware Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class PortInfo
{
    private NicStatus linkStatus;

    private SpeedInfo linkSpeedInMBps;

    private String macAddress;

    private String deviceName;

    public NicStatus getLinkStatus()
    {
        return linkStatus;
    }

    public void setLinkStatus( NicStatus linkStatus )
    {
        this.linkStatus = linkStatus;
    }

    public SpeedInfo getLinkSpeedInMBps()
    {
        return linkSpeedInMBps;
    }

    public void setLinkSpeedInMBps( SpeedInfo linkSpeedInMBps )
    {
        this.linkSpeedInMBps = linkSpeedInMBps;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public void setMacAddress( String macAddress )
    {
        this.macAddress = macAddress;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceName( String deviceName )
    {
        this.deviceName = deviceName;
    }

    /**
     * Get the Physical Ethernet Controller FRU Port Information. Wrapper method to get the PortInfo object for the node
     *
     * @param ethernetControllerPortInfo
     * @return PortInfo
     */
    public PortInfo getPortInfo( com.vmware.vrack.hms.common.servernodes.api.nic.PortInfo ethernetControllerPortInfo )
    {
        PortInfo portInfo = new PortInfo();
        portInfo.setMacAddress( ethernetControllerPortInfo.getMacAddress() );
        portInfo.setLinkSpeedInMBps( ethernetControllerPortInfo.getLinkSpeedInMBps() );
        portInfo.setLinkStatus( ethernetControllerPortInfo.getLinkStatus() );
        portInfo.setDeviceName( ethernetControllerPortInfo.getDeviceName() );
        return portInfo;
    }
}
