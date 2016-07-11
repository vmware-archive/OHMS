/* ********************************************************************************
 * IpmiServiceResponseException.java
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
package com.vmware.vrack.hms.ipmiservice.exception;

import com.veraxsystems.vxipmi.coding.payload.CompletionCode;
import com.vmware.vrack.hms.ipmiservice.IpmiServiceCompletionCode;

/**
 * IPMI service response Exception
 * 
 * @author VMware Inc.
 */
public class IpmiServiceResponseException
    extends IpmiServiceException
{
    private static final long serialVersionUID = 1L;

    private IpmiServiceCompletionCode ipmiServiceCompletionCode;

    public IpmiServiceResponseException( CompletionCode completionCode )
    {
        setCompletionCode( completionCode.getCode() );
    }

    public void setCompletionCode( int code )
    {
        ipmiServiceCompletionCode = IpmiServiceCompletionCode.parseInt( code );
    }

    public IpmiServiceCompletionCode getCompletionCode()
    {
        return ipmiServiceCompletionCode;
    }

    public String getMessage()
    {
        return ipmiServiceCompletionCode.getMessage();
    }

    public IpmiServiceResponseException( String message )
    {
        super( message );
    }
}
