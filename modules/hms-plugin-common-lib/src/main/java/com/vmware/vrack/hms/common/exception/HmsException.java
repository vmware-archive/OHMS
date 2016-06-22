/* ********************************************************************************
 * HmsException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.exception;

public class HmsException
    extends Exception
{
    private static final long serialVersionUID = 1415748195510407711L;

    public HmsException()
    {
    }

    public HmsException( String message )
    {
        super( message );
    }

    public HmsException( Throwable cause )
    {
        super( cause );
    }

    public HmsException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public HmsException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
