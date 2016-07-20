/* ********************************************************************************
 * BaseResponse.java
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
