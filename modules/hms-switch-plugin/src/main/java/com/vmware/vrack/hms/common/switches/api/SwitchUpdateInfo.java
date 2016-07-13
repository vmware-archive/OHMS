/* ********************************************************************************
 * SwitchUpdateInfo.java
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

public class SwitchUpdateInfo
{
    private String ipAddress;

    private String netmask;

    private String gateway;

    private String timeServer;

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress( String ipAddress )
    {
        this.ipAddress = ipAddress;
    }

    public void setManagementIpAddress( String ipAddress )
    {
        this.ipAddress = ipAddress;
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

    public String getTimeServer()
    {
        return timeServer;
    }

    public void setTimeServer( String timeServer )
    {
        this.timeServer = timeServer;
    }
}
