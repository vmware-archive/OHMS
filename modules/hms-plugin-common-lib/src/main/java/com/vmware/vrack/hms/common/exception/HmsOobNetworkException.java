/* ********************************************************************************
 * HmsOobNetworkException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.exception;

public class HmsOobNetworkException
    extends HmsException
{
    private static final long serialVersionUID = 1L;

    private HmsOobNetworkErrorCode errorCode;

    /**
     * @return the errorCode
     */
    public HmsOobNetworkErrorCode getErrorCode()
    {
        return errorCode;
    }

    public HmsOobNetworkException( HmsOobNetworkErrorCode errorCode )
    {
        this.errorCode = errorCode;
    }

    public HmsOobNetworkException( String message, HmsOobNetworkErrorCode errorCode )
    {
        super( message );
        this.errorCode = errorCode;
    }

    public HmsOobNetworkException( Throwable cause, HmsOobNetworkErrorCode errorCode )
    {
        super( cause );
        this.errorCode = errorCode;
    }

    public HmsOobNetworkException( String message, Throwable cause, HmsOobNetworkErrorCode errorCode )
    {
        super( message + ", Exception: " + cause.getLocalizedMessage(), cause );
        this.errorCode = errorCode;
    }

    public HmsOobNetworkException( String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace, HmsOobNetworkErrorCode errorCode )
    {
        super( message + ", Exception: " + cause.getLocalizedMessage(), cause, enableSuppression, writableStackTrace );
        this.errorCode = errorCode;
    }
}
