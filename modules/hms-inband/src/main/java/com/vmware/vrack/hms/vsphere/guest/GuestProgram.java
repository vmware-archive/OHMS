/* ********************************************************************************
 * GuestProgram.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.vsphere.guest;

/**
 * Created by Jeffrey Wang on 4/22/14.
 */
public class GuestProgram
{
    private String path;

    private String arguments;

    public GuestProgram()
    {
    }

    public GuestProgram( String path, String arguments )
    {
        this.path = path;
        this.arguments = arguments;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath( final String path )
    {
        this.path = path;
    }

    public String getArguments()
    {
        return arguments;
    }

    public void setArguments( final String arguments )
    {
        this.arguments = arguments;
    }
}
