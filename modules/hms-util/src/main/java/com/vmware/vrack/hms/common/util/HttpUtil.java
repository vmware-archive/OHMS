/* ********************************************************************************
 * HttpUtil.java
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
package com.vmware.vrack.hms.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.HttpResponse;
import com.vmware.vrack.hms.common.RequestMethod;
import com.vmware.vrack.hms.common.StatusCode;

public class HttpUtil
{
    private static Logger logger = Logger.getLogger( HttpUtil.class );

    public static final String RESP_CODE = "responseCode";

    public static final String RESP_BODY = "responseBody";

    /**
     * Executes HTTP request and returns Httpresponse Object Takes Http url, Request method and RequestBody as input
     * params
     * 
     * @param url
     * @param requestMethod
     * @param requestBody
     * @return
     */
    public static HttpResponse executeRequest( String url, RequestMethod requestMethod, String requestBody )
    {
        HttpURLConnection connection = null;
        if ( url != null && requestMethod != null && requestBody != null )
        {
            URL urlObj;
            try
            {
                urlObj = new URL( url );
                connection = (HttpURLConnection) urlObj.openConnection();
                switch ( requestMethod )
                {
                    case POST:
                        connection.setRequestMethod( "POST" );
                        break;
                    case GET:
                    default:
                        connection.setRequestMethod( "GET" );
                }
                connection.setDoOutput( true );
                connection.setRequestProperty( "Content-Type", "application/json" );
                if ( requestBody != null )
                {
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write( requestBody.getBytes() );
                    outputStream.flush();
                }
                BufferedReader responseBuffer =
                    new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
                String output;
                StringBuffer buffer = new StringBuffer();
                while ( ( output = responseBuffer.readLine() ) != null )
                {
                    buffer.append( output );
                }
                responseBuffer.close();
                int responseCode = connection.getResponseCode();
                return new HttpResponse( responseCode, buffer.toString() );
            }
            catch ( IOException e )
            {
                logger.error( "Error creating connection to URL : " + url, e );
                return new HttpResponse( StatusCode.FAILED.getValue(), StatusCode.FAILED.toString() );
            }
            finally
            {
                if ( connection != null )
                {
                    connection.disconnect();
                }
            }
        }
        else
        {
            return new HttpResponse( StatusCode.FAILED.getValue(), StatusCode.FAILED.toString() );
        }
    }

    /**
     * Asynchronously executes Request and does not wait for the response
     * 
     * @param url
     * @param requestMethod
     * @param requestBody
     */
    public static void executeRequestAsync( String url, RequestMethod requestMethod, String requestBody )
    {
        HttpURLConnection connection = null;
        if ( url != null && requestMethod != null && requestBody != null )
        {
            URL urlObj;
            try
            {
                urlObj = new URL( url );
                connection = (HttpURLConnection) urlObj.openConnection();
                switch ( requestMethod )
                {
                    case POST:
                        connection.setRequestMethod( "POST" );
                        break;
                    case GET:
                    default:
                        connection.setRequestMethod( "GET" );
                }
                connection.setDoOutput( true );
                connection.setRequestProperty( "Content-Type", "application/json" );
                if ( requestBody != null )
                {
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write( requestBody.getBytes() );
                    outputStream.flush();
                }
                connection.getResponseCode();
            }
            catch ( IOException e )
            {
                logger.error( "Error creating connection to URL : " + url, e );
            }
            finally
            {
                if ( connection != null )
                {
                    connection.disconnect();
                }
            }
        }
    }

    /**
     * Builds a Url String on which Http operations will be performed. Takes list<String> as input
     * 
     * @param list
     * @return
     */
    public static String buildUrl( List<String> list )
    {
        StringBuilder result = new StringBuilder();
        if ( list != null && !list.isEmpty() )
            for ( int i = 0; i < list.size(); i++ )
            {
                String uri = list.get( i );
                if ( uri != null && !"".equals( uri ) )
                {
                    if ( uri.startsWith( "/" ) )
                    {
                        uri = uri.substring( 1, uri.length() );
                    }
                    if ( uri.endsWith( "/" ) )
                    {
                        result.append( uri );
                    }
                    else
                    {
                        result.append( uri + "/" );
                    }
                }
            }
        String resultString = result.substring( 0, result.length() - 1 );
        return resultString;
    }
}
