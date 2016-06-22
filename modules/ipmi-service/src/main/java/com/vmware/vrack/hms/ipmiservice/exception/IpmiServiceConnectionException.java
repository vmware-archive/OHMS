/* ********************************************************************************
 * IpmiServiceConnectionException.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.ipmiservice.exception;

/**
 * IPMI service connection Exception indicating that connecting to the remote machine failed.
 * 
 * @author Vmware Inc.,
 */
public class IpmiServiceConnectionException
    extends IpmiServiceException
{
    private static final long serialVersionUID = 1415748195510407721L;

    public IpmiServiceConnectionException()
    {
        super();
    }

    public IpmiServiceConnectionException( String message )
    {
        super( message );
    }
}
