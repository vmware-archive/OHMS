/* ********************************************************************************
 * HmsResponseTimeoutException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.exception;

public class HmsResponseTimeoutException
    extends HmsException
{
    private static final long serialVersionUID = 459231249867630187L;

    public HmsResponseTimeoutException()
    {
    }

    public HmsResponseTimeoutException( String message )
    {
        super( message );
    }

    public HmsResponseTimeoutException( Throwable cause )
    {
        super( cause );
    }

    public HmsResponseTimeoutException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
