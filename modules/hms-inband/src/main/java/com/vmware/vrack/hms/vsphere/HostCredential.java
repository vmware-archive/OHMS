/* ********************************************************************************
 * HostCredential.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
