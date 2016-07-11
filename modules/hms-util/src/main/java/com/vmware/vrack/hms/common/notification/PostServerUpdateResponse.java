/* ********************************************************************************
 * PostServerUpdateResponse.java
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
package com.vmware.vrack.hms.common.notification;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.monitoring.ITaskResponseLifecycleHandler;
import com.vmware.vrack.hms.common.util.HttpClientService;

@JsonInclude( JsonInclude.Include.NON_NULL )
public class PostServerUpdateResponse
    implements ITaskResponseLifecycleHandler
{
    private static Logger logger = Logger.getLogger( PostServerUpdateResponse.class );

    @Override
    public void init( TaskResponse taskResponse )
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTaskComplete( TaskResponse taskResponse )
    {
        try
        {
            if ( taskResponse.callbackEndpoint != null )
            {
                ObjectMapper mapper = new ObjectMapper();
                String requestBody = mapper.writeValueAsString( taskResponse );
                HttpClientService.getInstance().post( taskResponse.callbackEndpoint, requestBody, true, true );
            }
        }
        catch ( Exception e )
        {
            logger.error( "error completing task response", e );
        }
    }
}
