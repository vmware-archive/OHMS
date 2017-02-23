/* ********************************************************************************
 * HMSRestExceptionHandler.java
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

        return Response.status( exception.getResponseErrorCode() ).entity(

                                                                           new BaseResponse( exception.getResponseErrorCode(),
                                                                                             exception.getMessage(),
                                                                                             exception.getReason() ) ).type( MediaType.APPLICATION_JSON ).build();
    }

}
