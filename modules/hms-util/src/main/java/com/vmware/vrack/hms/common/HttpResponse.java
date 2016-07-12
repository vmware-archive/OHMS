/* ********************************************************************************
 * HttpResponse.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
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
