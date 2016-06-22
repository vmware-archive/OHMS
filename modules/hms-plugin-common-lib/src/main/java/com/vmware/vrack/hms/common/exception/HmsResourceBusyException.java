/* ********************************************************************************
 * HmsResourceBusyException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.exception;

public class HmsResourceBusyException
    extends HmsException
{
    static final long serialVersionUID = 4701639302322786248L;

    public HmsResourceBusyException()
    {
    }

    public HmsResourceBusyException( String message )
    {
        super( message );
    }

    public HmsResourceBusyException( Throwable cause )
    {
        super( cause );
    }

    public HmsResourceBusyException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
