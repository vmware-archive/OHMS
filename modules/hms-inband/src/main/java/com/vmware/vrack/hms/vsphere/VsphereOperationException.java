/* ********************************************************************************
 * VsphereOperationException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.vsphere;

/**
 * Author: Tao Ma Date: 3/6/14
 */
public class VsphereOperationException
    extends VsphereException
{
    public VsphereOperationException()
    {
        super();
    }

    public VsphereOperationException( String message )
    {
        super( message );
    }

    public VsphereOperationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public VsphereOperationException( Throwable cause )
    {
        super( cause );
    }

    protected VsphereOperationException( String message, Throwable cause, boolean enableSuppression,
                                         boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
