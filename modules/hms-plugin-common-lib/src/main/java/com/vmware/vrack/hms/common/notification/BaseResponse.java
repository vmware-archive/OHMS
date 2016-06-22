/* ********************************************************************************
 * BaseResponse.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.notification;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude( JsonInclude.Include.NON_NULL )
public class BaseResponse
{
    private Integer statusCode;

    private String statusMessage;

    private String errorMessage;

    public BaseResponse()
    {
        super();
    }

    public BaseResponse( Integer statusCode, String statusMessage, String errorMessage )
    {
        super();
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.errorMessage = errorMessage;
    }

    public Integer getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode( Integer statusCode )
    {
        this.statusCode = statusCode;
    }

    public String getStatusMessage()
    {
        return statusMessage;
    }

    public void setStatusMessage( String statusMessage )
    {
        this.statusMessage = statusMessage;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage( String errorMessage )
    {
        this.errorMessage = errorMessage;
    }
}
