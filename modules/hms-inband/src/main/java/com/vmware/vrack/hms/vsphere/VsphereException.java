/* ********************************************************************************
 * VsphereException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.vsphere;

/**
 * Author: Tao Ma Date: 3/6/14
 */
public class VsphereException
    extends RuntimeException
{
    public VsphereException()
    {
        super();
    }

    public VsphereException( String message )
    {
        super( message );
    }

    public VsphereException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public VsphereException( Throwable cause )
    {
        super( cause );
    }

    protected VsphereException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
