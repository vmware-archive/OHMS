/* ********************************************************************************
 * HttpClientService.java
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
package com.vmware.vrack.hms.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.HmsConfigHolder;

/**
 * @author ambi
 */
@Deprecated
public class HttpClientService
{
    private static Logger logger = Logger.getLogger( HttpClientService.class );

    public final static int HTTP_GET = 0;

    public final static int HTTP_POST = 1;

    private final static String HTTP_METHOD[] = { "GET", "POST" };

    private DefaultHttpClient ignoreSSLWithBasicAuth;

    private DefaultHttpClient defaulClient;

    private static HttpClientService prmClientService;

    public static HttpClientService getInstance()
    {
        if ( prmClientService == null )
        {
            prmClientService = new HttpClientService();
            try
            {
                prmClientService.prepareClients();
            }
            catch ( KeyManagementException | NoSuchAlgorithmException e )
            {
                logger.error( String.format( "Error initializing prm client, error message: %s", e.getMessage() ), e );
            }
        }
        return prmClientService;
    }

    // Preparing PRM clients with trust managers and basic auth.
    public void prepareClients()
        throws KeyManagementException, NoSuchAlgorithmException
    {
        // this.ignoreSSLWithBasicAuth = withSSLIgnore();
        this.defaulClient = new DefaultHttpClient();

        String prmBasicAuthUser =
            HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS, "prm.basic.auth.user" );
        String prmBasicAuthPass =
            HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS, "prm.basic.auth.password" );
        Credentials credentials = new UsernamePasswordCredentials( prmBasicAuthUser, prmBasicAuthPass );
        // ignoreSSLWithBasicAuth.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        defaulClient.getCredentialsProvider().setCredentials( AuthScope.ANY, credentials );
    }

    public String get( String url, Boolean ignoreSSLCert, Boolean asyncWithIgnoreResponse )
        throws IOException, URISyntaxException
    {
        return invokeHTTPMethod( HTTP_POST, url, null, ignoreSSLCert, asyncWithIgnoreResponse );
    }

    public String post( String url, String requestBody, Boolean ignoreSSLCert, Boolean asyncWithIgnoreResponse )
        throws IOException, URISyntaxException
    {
        return invokeHTTPMethod( HTTP_POST, url, requestBody, ignoreSSLCert, asyncWithIgnoreResponse );
    }

    public String postJson( String url, String json, Boolean ignoreSSLCert, Boolean asyncWithIgnoreResponse )
        throws IOException, URISyntaxException
    {
        logger.debug( "Sending Json POST request to : " + url );
        return invokeJsonPost( url, json, ignoreSSLCert, asyncWithIgnoreResponse );
    }

    public String invokeHTTPMethod( int httpMethod, String url, String requestBody, Boolean ignoreSSLCert,
                                    Boolean asyncWithIgnoreResponse )
        throws IOException, URISyntaxException
    {
        HttpUriRequest httpMessage = null;
        switch ( httpMethod )
        {
            case HTTP_GET:
                httpMessage = new HttpGet( url );
                break;
            case HTTP_POST:
                httpMessage = new HttpPost( url );
                ( (HttpPost) httpMessage ).setEntity( new StringEntity( requestBody ) );
                break;
        }
        HttpResponse response = null;
        if ( ignoreSSLCert )
        {
            response = ignoreSSLWithBasicAuth.execute( httpMessage );
        }
        else
        {
            response = defaulClient.execute( httpMessage );
        }

        if ( asyncWithIgnoreResponse )
        {
            EntityUtils.consume( response.getEntity() );
            return null;
        }

        return getResponseAsString( response );
    }

    private String invokeJsonPost( String url, String json, Boolean ignoreSSLCert, Boolean asyncWithIgnoreResponse )
        throws IOException, URISyntaxException
    {
        HttpUriRequest httpMessage = null;
        httpMessage = new HttpPost( url );
        httpMessage.addHeader( "content-type", "application/json" );
        ( (HttpPost) httpMessage ).setEntity( new StringEntity( json ) );
        HttpResponse response = null;
        if ( ignoreSSLCert )
        {
            response = ignoreSSLWithBasicAuth.execute( httpMessage );
        }
        else
        {
            response = defaulClient.execute( httpMessage );
        }

        if ( asyncWithIgnoreResponse )
        {
            EntityUtils.consume( response.getEntity() );
            return null;
        }

        return getResponseAsString( response );
    }

    private String getResponseAsString( HttpResponse response )
        throws IOException
    {
        HttpEntity entity = response.getEntity();
        String resp = EntityUtils.toString( entity );
        return resp;
    }
}