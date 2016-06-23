/* ********************************************************************************
 * HmsOperationNotSupportedException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.exception;

public class HmsOperationNotSupportedException
    extends HmsException
{
    public HmsOperationNotSupportedException()
    {
        super();
    }

    public HmsOperationNotSupportedException( String message, Throwable cause, boolean enableSuppression,
                                              boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }

    public HmsOperationNotSupportedException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public HmsOperationNotSupportedException( String message )
    {
        super( message );
    }

    public HmsOperationNotSupportedException( Throwable cause )
    {
        super( cause );
    }

    private static final long serialVersionUID = -612852538193067922L;
}
