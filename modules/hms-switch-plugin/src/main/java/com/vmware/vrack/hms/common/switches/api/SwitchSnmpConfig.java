/* ********************************************************************************
 * SwitchSnmpConfig.java
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

import java.util.List;

public class SwitchSnmpConfig
{
    /* if enabled is true, turn on SNMP on switch; otherwise, disable it */
    private Boolean enabled;

    /* (MANDATORY) IPv4 Address of the SNMP Manager which can typically be a Zenoss or Nagios server */
    private String serverIp;

    /* (OPTIONAL) SNMP server port (default = 162) */
    private Integer serverPort;

    /*
     * (MANDATORY) Users that we will connect as, keeping it a list is just being defensive in case mulitple users may
     * want to connect to the server for different purpose. Typically there will be 1 user in the list during
     * configuration
     */
    private List<SwitchSnmpUser> users;

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled( Boolean enabled )
    {
        this.enabled = enabled;
    }

    public String getServerIp()
    {
        return serverIp;
    }

    public void setServerIp( String serverIp )
    {
        this.serverIp = serverIp;
    }

    public Integer getServerPort()
    {
        return serverPort;
    }

    public void setServerPort( Integer serverPort )
    {
        this.serverPort = serverPort;
    }

    public List<SwitchSnmpUser> getUsers()
    {
        return users;
    }

    public void setUsers( List<SwitchSnmpUser> users )
    {
        this.users = users;
    }
}
