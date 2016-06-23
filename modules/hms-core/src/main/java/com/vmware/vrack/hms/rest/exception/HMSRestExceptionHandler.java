/* ********************************************************************************
 * HMSRestExceptionHandler.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.rest.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.notification.BaseResponse;

@Provider
public class HMSRestExceptionHandler
    implements ExceptionMapper<HMSRestException>
{
    @Override
    public Response toResponse( HMSRestException exception )
    {
        return Response.status( exception.getResponseErrorCode() ).entity( new BaseResponse( exception.getResponseErrorCode(),
                                                                                             exception.getMessage(),
                                                                                             exception.getReason() ) ).type( MediaType.APPLICATION_JSON ).build();
    }
}
