/* ********************************************************************************
 * SwitchLinkedPort.java
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
