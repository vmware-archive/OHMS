/* ********************************************************************************
 * HMSRestException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.exception;

import java.io.Serializable;

public class HMSRestException
    extends HmsException
    implements Serializable
{
    private static final long serialVersionUID = 614622508173467361L;

    private int responseErrorCode;

    private String reason;

    public HMSRestException( int responseErrorCode, String errorMessage, String reason )
    {
        super( errorMessage );
        this.reason = reason;
        this.responseErrorCode = responseErrorCode;
    }

    public int getResponseErrorCode()
    {
        return responseErrorCode;
    }

    @SuppressWarnings( "unused" )
    private void setResponseErrorCode( int responseCode )
    {
        this.responseErrorCode = responseCode;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason( String reason )
    {
        this.reason = reason;
    }
}
