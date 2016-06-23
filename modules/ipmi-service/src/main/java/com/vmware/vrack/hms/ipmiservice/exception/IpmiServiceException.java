/* ********************************************************************************
 * IpmiServiceException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.ipmiservice.exception;

/**
 * IPMI service Exception
 * 
 * @author VMware Inc.
 */
public class IpmiServiceException
    extends Exception
{
    private static final long serialVersionUID = 1415748195510407722L;

    public IpmiServiceException()
    {
    }

    public IpmiServiceException( String message )
    {
        super( message );
    }

    public IpmiServiceException( Throwable cause )
    {
        super( cause );
    }

    public IpmiServiceException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public IpmiServiceException( String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
