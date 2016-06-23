/* ********************************************************************************
 * NetTopElement.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.topology;

import java.util.List;

public class NetTopElement
{
    private String deviceId; // Device ID to which this port belongs to

    private String portName; // Port name as evident to outside world

    private String macAddress; // MAC address of the port

    private List<String> vlans; // All the VLANs associated with this port

    private NetTopElement connectedElement; // Others end of the connection

    public String getDeviceId()
    {
        return deviceId;
    }

    public String getPortName()
    {
        return portName;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public List<String> getVlans()
    {
        return vlans;
    }

    public NetTopElement getConnectedElement()
    {
        return connectedElement;
    }

    public void setDeviceId( String deviceId )
    {
        this.deviceId = deviceId;
    }

    public void setPortName( String portName )
    {
        this.portName = portName;
    }

    public void setMacAddress( String macAddress )
    {
        this.macAddress = macAddress;
    }

    public void setVlans( List<String> vlans )
    {
        this.vlans = vlans;
    }

    public void setConnectedElement( NetTopElement connectedElement )
    {
        this.connectedElement = connectedElement;
    }
}
