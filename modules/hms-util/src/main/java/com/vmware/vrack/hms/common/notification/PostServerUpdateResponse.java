/* ********************************************************************************
 * PostServerUpdateResponse.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
