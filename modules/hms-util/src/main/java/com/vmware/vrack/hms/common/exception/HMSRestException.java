/* ********************************************************************************
 * HMSRestException.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
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
