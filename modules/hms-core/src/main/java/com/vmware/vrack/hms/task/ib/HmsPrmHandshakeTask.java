/* ********************************************************************************
 * HmsPrmHandshakeTask.java
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
package com.vmware.vrack.hms.task.ib;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.HttpResponse;
import com.vmware.vrack.hms.common.RequestMethod;
import com.vmware.vrack.hms.common.notification.EventRequester;
import com.vmware.vrack.hms.common.notification.PRMHandshakeRequest;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.util.HttpUtil;

// This class is no longer valid
@Deprecated
public class HmsPrmHandshakeTask
    extends IBTask
{
    public TaskResponse response;

    private static Logger logger = Logger.getLogger( HmsPrmHandshakeTask.class );

    HttpURLConnection connection = null;

    URL urlObj = null;

    public HmsPrmHandshakeTask( TaskResponse response )
    {
        this.response = response;
    }

    @Override
    public void executeTask()
    {
        Properties hmsProperties = HmsConfigHolder.getProperties( HmsConfigHolder.HMS_CONFIG_PROPS );
        String PRM_BASE_URL = hmsProperties.getProperty( "PRM_BASE_URL" );
        String PRM_BASE_URL_PORT = hmsProperties.getProperty( "PRM_BASE_URL_PORT" );
        String PRM_HANDSHAKE_URL = hmsProperties.getProperty( "PRM_HANDSHAKE_URL" );
        String HMS_BASE_URL = hmsProperties.getProperty( "HMS_BASE_URL" );
        String HMS_BASE_URL_PORT = hmsProperties.getProperty( "HMS_BASE_URL_PORT" );
        String HMS_APP_TYPE = hmsProperties.getProperty( "HMS_APP_TYPE" );

        try
        {
            String prmHandshakeUrl = PRM_BASE_URL + ":" + PRM_BASE_URL_PORT + PRM_HANDSHAKE_URL;
            String hmsBaseUrl = HMS_BASE_URL + ":" + HMS_BASE_URL_PORT;

            PRMHandshakeRequest prmHandshakeRequest = new PRMHandshakeRequest();
            EventRequester requester = new EventRequester();
            requester.setBaseUrl( hmsBaseUrl );
            requester.setAppType( HMS_APP_TYPE );
            prmHandshakeRequest.setRequester( requester );

            ObjectMapper mapper = new ObjectMapper();

            String handshakeMessage = mapper.writeValueAsString( prmHandshakeRequest );
            HttpResponse response = HttpUtil.executeRequest( prmHandshakeUrl, RequestMethod.POST, handshakeMessage );
            if ( response.getResponseCode() == 200 )
            {
                logger.debug( response.getResponseCode() + "    " + response.getResponseBody() );
            }

        }
        catch ( IOException e )
        {
            logger.error( "while sending Hms prm handshake request: ", e );
        }
    }

    @Override
    public TaskResponse call()
        throws Exception
    {
        this.executeTask();
        return response;
    }

    @Override
    public void destroy()
        throws Exception
    {
        if ( connection != null )
        {
            connection.disconnect();
        }
    }

}
