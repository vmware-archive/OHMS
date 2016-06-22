/* ********************************************************************************
 * OperationNotSupportedOOBException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.exception;

public class OperationNotSupportedOOBException
    extends HmsException
{
    private static final long serialVersionUID = 1415748198934677711L;

    public OperationNotSupportedOOBException()
    {
    }

    public OperationNotSupportedOOBException( String message )
    {
        super( message );
    }

    public OperationNotSupportedOOBException( Throwable cause )
    {
        super( cause );
    }

    public OperationNotSupportedOOBException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public OperationNotSupportedOOBException( String message, Throwable cause, boolean enableSuppression,
                                              boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
