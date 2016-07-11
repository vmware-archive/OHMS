/* ********************************************************************************
 * HmsOobNetworkException.java
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
