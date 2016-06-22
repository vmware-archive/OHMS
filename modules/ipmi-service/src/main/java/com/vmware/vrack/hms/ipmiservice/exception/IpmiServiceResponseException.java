/* ********************************************************************************
 * IpmiServiceResponseException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
