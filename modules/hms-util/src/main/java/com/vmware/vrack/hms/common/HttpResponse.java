/* ********************************************************************************
 * HttpResponse.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common;

public class HttpResponse
{
    private int responseCode;

    private String responseBody;

    private HttpResponse()
    {
    }

    public HttpResponse( int responseCode, String responseBody )
    {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
    }

    public int getResponseCode()
    {
        return responseCode;
    }

    public void setResponseCode( int responseCode )
    {
        this.responseCode = responseCode;
    }

    public String getResponseBody()
    {
        return responseBody;
    }

    public void setResponseBody( String responseBody )
    {
        this.responseBody = responseBody;
    }
}
