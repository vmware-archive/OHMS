/* ********************************************************************************
 * NetTopElement.java
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
