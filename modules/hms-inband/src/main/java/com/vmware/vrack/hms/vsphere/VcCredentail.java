/* ********************************************************************************
 * VcCredentail.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.vsphere;

/**
 * Created by Jeffrey Wang on 7/26/14.
 */
public class VcCredentail
{
    private String vcIp;

    private String vcPrivateIp;

    private String vcUsername;

    private String vcPassword;

    private String vcVmName;

    public VcCredentail()
    {
    }

    public String getVcIp()
    {
        return vcIp;
    }

    public void setVcIp( final String vcIp )
    {
        this.vcIp = vcIp;
    }

    public String getVcPrivateIp()
    {
        return vcPrivateIp;
    }

    public void setVcPrivateIp( final String vcPrivateIp )
    {
        this.vcPrivateIp = vcPrivateIp;
    }

    public String getVcUsername()
    {
        return vcUsername;
    }

    public void setVcUsername( final String vcUsername )
    {
        this.vcUsername = vcUsername;
    }

    public String getVcPassword()
    {
        return vcPassword;
    }

    public void setVcPassword( final String vcPassword )
    {
        this.vcPassword = vcPassword;
    }

    public String getVcVmName()
    {
        return vcVmName;
    }

    public void setVcVmName( final String vcVmName )
    {
        this.vcVmName = vcVmName;
    }
}
