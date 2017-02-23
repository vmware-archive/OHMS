/* ********************************************************************************
 * HostCredential.java
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

package com.vmware.vrack.hms.vsphere;

/**
 * Created by Jeffrey Wang on 3/26/14.
 */
public class HostCredential
{
    private String ipAddress;

    private String hostname;

    private String username;

    private String password;

    public HostCredential()
    {
    }

    public HostCredential( String ipAddress, String hostname, String username, String password )
    {
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.username = username;
        this.password = password;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public String getHostname()
    {
        return hostname;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setIpAddress( String ipAddress )
    {
        this.ipAddress = ipAddress;
    }

    public void setHostname( String hostname )
    {
        this.hostname = hostname;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }
}