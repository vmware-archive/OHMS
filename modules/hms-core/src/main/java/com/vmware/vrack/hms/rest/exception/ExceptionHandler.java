/* ********************************************************************************
 * ExceptionHandler.java
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
package com.vmware.vrack.hms.rest.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.notification.BaseResponse;

/**
 * Exception Handler for Exceptions other than HmsRestException. Like Json Mapping Exception.
 * 
 * @author Yagnesh Chawda
 */
@Provider
public class ExceptionHandler
    implements ExceptionMapper<Throwable>
{
    private Logger logger = Logger.getLogger( ExceptionHandler.class );

    @Override
    public Response toResponse( Throwable exception )
    {
        logger.error( "Returning Bad Request status due to unhandled exception.", exception );
        return Response.status( Status.BAD_REQUEST.getStatusCode() ).entity( new BaseResponse( Status.BAD_REQUEST.getStatusCode(),
                                                                                               "Bad Request",
                                                                                               exception.getMessage() ) ).type( MediaType.APPLICATION_JSON ).build();
    }
}
