/* ********************************************************************************
 * SwitchLinkedPort.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.api;

public class SwitchLinkedPort
{
    private String portName;

    private String mac; /* Used when LLDP messages are received */

    private String deviceName;

    public String getPortName()
    {
        return portName;
    }

    public String getMac()
    {
        return mac;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setPortName( String portName )
    {
        this.portName = portName;
    }

    public void setMac( String mac )
    {
        this.mac = mac;
    }

    public void setDeviceName( String deviceName )
    {
        this.deviceName = deviceName;
    }
}
