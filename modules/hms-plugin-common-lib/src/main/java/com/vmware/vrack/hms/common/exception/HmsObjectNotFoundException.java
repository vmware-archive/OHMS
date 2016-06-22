/* ********************************************************************************
 * HmsObjectNotFoundException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.exception;

public class HmsObjectNotFoundException
    extends HmsException
{
    private static final long serialVersionUID = 1087536995722226753L;

    public HmsObjectNotFoundException()
    {
    }

    public HmsObjectNotFoundException( String message )
    {
        super( message );
    }

    public HmsObjectNotFoundException( Throwable cause )
    {
        super( cause );
    }

    public HmsObjectNotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public HmsObjectNotFoundException( String message, Throwable cause, boolean enableSuppression,
                                       boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
