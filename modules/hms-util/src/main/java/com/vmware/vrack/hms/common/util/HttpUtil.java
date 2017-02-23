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
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vmware.vrack.hms.common.RequestMethod;
import com.vmware.vrack.hms.common.StatusCode;

/**
 * The Class HttpUtil.
 */
public class HttpUtil
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( HttpUtil.class );

    /** The Constant RESP_CODE. */
    public static final String RESP_CODE = "responseCode";

    /** The Constant RESP_BODY. */
    public static final String RESP_BODY = "responseBody";

    /**
     * Executes HTTP request and returns Httpresponse Object Takes Http url, Request method and RequestBody as input
     * params.
     *
     * @param url the url
     * @param requestMethod the request method
     * @param requestBody the request body
     * @return the com.vmware.vrack.hms.common. http response
     */
    public static com.vmware.vrack.hms.common.HttpResponse executeRequest( String url, RequestMethod requestMethod,
                                                                           String requestBody )
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
                return new com.vmware.vrack.hms.common.HttpResponse( responseCode, buffer.toString() );
            }
            catch ( IOException e )
            {
                logger.error( "Error creating connection to URL : " + url, e );
                return new com.vmware.vrack.hms.common.HttpResponse( StatusCode.FAILED.getValue(),
                                                                     StatusCode.FAILED.toString() );
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
            return new com.vmware.vrack.hms.common.HttpResponse( StatusCode.FAILED.getValue(),
                                                                 StatusCode.FAILED.toString() );
        }
    }

    /**
     * Builds a Url String on which Http operations will be performed. Takes list<String> as input
     *
     * @param list the list
     * @return the string
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
        return result.substring( 0, result.length() - 1 );
    }

    /**
     * Gets the response handler.
     *
     * @param <T> the generic type
     * @param valueType the value type
     * @return the response handler
     */
    private static <T> ResponseHandler<T> getResponseHandler( final Class<T> valueType )
    {
        ResponseHandler<T> responseHandler = new ResponseHandler<T>()
        {
            private Logger logger = LoggerFactory.getLogger( ResponseHandler.class );

            @Override
            public T handleResponse( final HttpResponse httpResponse )
                throws IOException
            {
                StatusLine statusLine = httpResponse.getStatusLine();
                int httpStatusCode = statusLine.getStatusCode();
                String httpStatusReasonPhrase = statusLine.getReasonPhrase();
                logger.info( "In getResponseHandler, HTTP Status: {} ({}).", httpStatusCode, httpStatusReasonPhrase );
                HttpEntity httpEntity = httpResponse.getEntity();
                if ( ( httpStatusCode == HttpStatus.SC_OK || httpStatusCode == HttpStatus.SC_ACCEPTED )
                    && httpEntity != null )
                {
                    String responseBody = EntityUtils.toString( httpEntity );
                    if ( StringUtils.isNotBlank( responseBody ) )
                    {
                        return HmsGenericUtil.parseStringAsValueType( responseBody, valueType );
                    }
                    logger.warn( "In getResponseHandler, response body is null." );
                }
                else
                {
                    logger.warn( "In getResponseHandler, as HTTP response status is {}, not parsing response body.",
                                 httpStatusCode );
                }
                return null;
            }
        };
        return responseHandler;
    }

    /**
     * Retuns a generic ResponseHandler with handleResponse method, processing response body, only is HTTP status is OK.
     * If HTTP response status is other than OK, null response body will be returned.
     *
     * @param <T> the generic type
     * @param typeRef the type ref
     * @return the response handler
     */
    private static <T> ResponseHandler<T> getResponseHandler( final TypeReference<T> typeRef )
    {
        ResponseHandler<T> responseHandler = new ResponseHandler<T>()
        {
            private Logger logger = LoggerFactory.getLogger( ResponseHandler.class );

            @Override
            public T handleResponse( final HttpResponse httpResponse )
                throws IOException
            {
                StatusLine statusLine = httpResponse.getStatusLine();
                int httpStatusCode = statusLine.getStatusCode();
                String httpStatusReasonPhrase = statusLine.getReasonPhrase();
                logger.info( "In getResponseHandler, HTTP Status: {} ({}).", httpStatusCode, httpStatusReasonPhrase );
                HttpEntity httpEntity = httpResponse.getEntity();
                if ( ( httpStatusCode == HttpStatus.SC_OK || httpStatusCode == HttpStatus.SC_ACCEPTED )
                    && httpEntity != null )
                {
                    String responseBody = EntityUtils.toString( httpEntity );
                    if ( StringUtils.isNotBlank( responseBody ) )
                    {
                        return HmsGenericUtil.parseStringAsTypeReference( responseBody, typeRef );
                    }
                    logger.warn( "In getResponseHandler, response body is null." );
                }
                else
                {
                    logger.warn( "In getResponseHandler, as HTTP response status is {}, not parsing response body.",
                                 httpStatusCode );
                }
                return null;
            }
        };
        return responseHandler;
    }

    /**
     * Invokes GET on the given URL and parses response body, if any, as the given Class type and returns the same. If
     * the API does not return any response, then caller MUST pass Void as the return type.
     *
     * @param <T> the generic type
     * @param url the url
     * @param valueType the value type
     * @return the t
     */
    public static <T> T executeGet( final String url, final Class<T> valueType )
    {
        if ( StringUtils.isBlank( url ) || valueType == null )
        {
            logger.warn( "In executeGet, either url is null or blank or value type is null." );
            return null;
        }
        logger.info( "In executeGet, Invoking GET on {}. Response Type: {}", url, valueType.getSimpleName() );
        return HttpUtil.executeGet( url, HttpUtil.getResponseHandler( valueType ), HttpUtil.getHttpClient() );
    }

    /**
     * Execute get.
     *
     * @param <T> the generic type
     * @param url the url
     * @param typeRef the type ref
     * @return the t
     */
    public static <T> T executeGet( final String url, final TypeReference<T> typeRef )
    {
        if ( StringUtils.isBlank( url ) || typeRef == null )
        {
            logger.warn( "In executeGet, either url is null or blank or type reference is null." );
            return null;
        }
        return HttpUtil.executeGet( url, HttpUtil.getResponseHandler( typeRef ), HttpUtil.getHttpClient() );
    }

    /**
     * Execute get.
     *
     * @param url the url
     * @return the http response
     */
    public static HttpResponse executeGet( final String url )
    {
        if ( StringUtils.isBlank( url ) )
        {
            logger.warn( "In executeGet, url is either null or blank." );
            return null;
        }
        return HttpUtil.executeGet( url, HttpUtil.getHttpClient() );
    }

    /**
     * Execute get.
     *
     * @param url the url
     * @param httpClient the http client
     * @return the http response
     */
    private static HttpResponse executeGet( final String url, final HttpClient httpClient )
    {
        if ( StringUtils.isBlank( url ) )
        {
            logger.warn( "In executeGet, url is either null or blank." );
            return null;
        }
        if ( httpClient == null )
        {
            logger.warn( "In executeGet, HttpClient is null." );
            return null;
        }
        HttpGet httpGet = new HttpGet( url );
        try
        {
            return httpClient.execute( httpGet );
        }
        catch ( IOException e )
        {
            logger.error( "In executeGet, error while invoking GET on {}.", url, e );
            return null;
        }
    }

    /**
     * Execute get.
     *
     * @param <T> the generic type
     * @param url the url
     * @param responseHandler the response handler
     * @param httpClient the http client
     * @return the t
     */
    private static <T> T executeGet( final String url, final ResponseHandler<T> responseHandler,
                                     final HttpClient httpClient )
    {
        if ( StringUtils.isBlank( url ) )
        {
            logger.warn( "In executeGet, url is either null or blank." );
            return null;
        }
        if ( httpClient == null )
        {
            logger.warn( "In executeGet, HttpClient is null." );
            return null;
        }
        HttpGet httpGet = new HttpGet( url );
        try
        {
            return httpClient.execute( httpGet, responseHandler );
        }
        catch ( IOException e )
        {
            logger.error( "In executeGet, error while invoking GET on {}.", url, e );
            return null;
        }
    }

    /**
     * Invokes POST on the given URL with the given request body. If successful response (HTTP Status Code: 200) is
     * received from the server, response body will be converted to asked type. Otherwise, null will be returned. If the
     * API does not return any response body, then caller MUST pass Void as valueType.
     *
     * @param <T> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param valueType the value type
     * @return the t
     */
    public static <T> T executePost( final String url, final Object requestBody, final Class<T> valueType )
    {

        // check that url is not null and blank
        if ( StringUtils.isBlank( url ) )
        {
            logger.warn( "In executePost, URL is either null or blank.", url );
            return null;
        }

        // check that request body is not null.
        if ( requestBody == null )
        {
            logger.warn( "In executePost, request body is null." );
            return null;
        }

        // check that response type is not null.
        if ( valueType == null )
        {
            logger.warn( "In executePost, response type is null." );
            return null;
        }
        HttpEntity httpEntity = HttpUtil.getStringEntity( requestBody );
        if ( httpEntity == null )
        {
            logger.warn( "In executePost, failed to serialize request body as JSON payload." );
            return null;
        }
        return HttpUtil.executePost( url, httpEntity, HttpUtil.getResponseHandler( valueType ) );
    }

    /**
     * Invokes POST on the given URL with the given request body. If successful response (HTTP Status Code: 200) is
     * received from the server, response body will be converted to asked type. Otherwise, null will be returned. If the
     * API does not return any response body, then caller MUST pass Void as valueType.
     *
     * @param <T> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param typeRef the type ref
     * @return the t
     */
    public static <T> T executePost( final String url, final Object requestBody, final TypeReference<T> typeRef )
    {

        // check that url is not null and blank
        if ( StringUtils.isBlank( url ) )
        {
            logger.warn( "In executePost, URL is either null or blank.", url );
            return null;
        }

        // check that request body is not null.
        if ( requestBody == null )
        {
            logger.warn( "In executePost, request body is null." );
            return null;
        }

        // check that response type is not null.
        if ( typeRef == null )
        {
            logger.warn( "In executeGet, response type is null." );
            return null;
        }
        HttpEntity httpEntity = HttpUtil.getStringEntity( requestBody );
        if ( httpEntity == null )
        {
            return null;
        }
        return HttpUtil.executePost( url, httpEntity, HttpUtil.getResponseHandler( typeRef ) );
    }

    /**
     * Invokes POST on the given URL. If successful response (HTTP Status Code: 200) is received from the server,
     * response body will be converted to asked type. Otherwise, null will be returned. If the API does not return any
     * response body, then caller MUST pass Void as valueType.
     *
     * @param <T> the generic type
     * @param url the url
     * @param valueType the value type
     * @return the t
     */
    public static <T> T executePost( final String url, final Class<T> valueType )
    {

        // check that url is not null and blank
        if ( StringUtils.isBlank( url ) )
        {
            logger.warn( "In executePost, URL is either null or blank.", url );
            return null;
        }

        // check that response type is not null.
        if ( valueType == null )
        {
            logger.warn( "In executeGet, response type is null." );
            return null;
        }
        return HttpUtil.executePost( url, null, HttpUtil.getResponseHandler( valueType ) );
    }

    /**
     * Invokes POST on the given URL. If successful response (HTTP Status Code: 200) is received from the server,
     * response body will be converted to asked type. Otherwise, null will be returned. If the API does not return any
     * response body, then caller MUST pass Void as valueType.
     *
     * @param <T> the generic type
     * @param url the url
     * @param typeRef the type ref
     * @return the t
     */
    public static <T> T executePost( final String url, final TypeReference<T> typeRef )
    {

        // check that url is not null and blank
        if ( StringUtils.isBlank( url ) )
        {
            logger.warn( "In executePost, URL is either null or blank.", url );
            return null;
        }

        // check that response type is not null.
        if ( typeRef == null )
        {
            logger.warn( "In executeGet, response type is null." );
            return null;
        }
        return HttpUtil.executePost( url, null, HttpUtil.getResponseHandler( typeRef ) );
    }

    /**
     * Execute post.
     *
     * @param <T> the generic type
     * @param url the url
     * @param httpEntity the http entity
     * @param responseHandler the response handler
     * @return the t
     */
    private static <T> T executePost( final String url, final HttpEntity httpEntity,
                                      final ResponseHandler<T> responseHandler )
    {

        // check that url is not null
        if ( StringUtils.isBlank( url ) )
        {
            logger.error( "In executePost, URL is either null or blank." );
            return null;
        }

        // check that ResponseHandler is not null
        if ( responseHandler == null )
        {
            logger.error( "In executePost, ResponseHandler is null." );
            return null;
        }

        HttpClient httpClient = HttpUtil.getHttpClient();
        HttpPost httpPost = new HttpPost( url );
        httpPost.setHeader( HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON );
        if ( httpEntity != null )
        {
            httpPost.setEntity( httpEntity );
            httpPost.setHeader( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON );
        }
        try
        {
            return httpClient.execute( httpPost, responseHandler );
        }
        catch ( IOException e )
        {
            logger.error( "In executePost, error while invoking POST on {}.", url, e );
            return null;
        }
    }

    /**
     * Execute post.
     *
     * @param <T> the generic type
     * @param url the url
     * @return the http response
     */
    public static HttpResponse executePost( final String url )
    {
        // check that url is not null
        if ( StringUtils.isBlank( url ) )
        {
            logger.error( "In executePost, URL is either null or blank." );
            return null;
        }

        HttpResponse httpResponse = null;
        HttpClient httpClient = HttpUtil.getHttpClient();
        HttpPost httpPost = new HttpPost( url );

        // set headers
        httpPost.setHeader( HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON );

        // create a httpContext
        HttpContext httpContext = new BasicHttpContext();

        try
        {
            httpResponse = httpClient.execute( httpPost, httpContext );
            StatusLine statusLine = httpResponse.getStatusLine();
            HttpUriRequest httpUriRequest = (HttpUriRequest) httpContext.getAttribute( HttpCoreContext.HTTP_REQUEST );
            logger.debug( "In executePost, Response {} ({}) received for {} on {}.", statusLine.getStatusCode(),
                          statusLine.getReasonPhrase(), httpUriRequest.getMethod(),
                          httpUriRequest.getURI().toString() );
            return httpResponse;
        }
        catch ( IOException e )
        {
            logger.error( "In executePost, error while invoking POST on {}.", url, e );
            return httpResponse;
        }
    }

    /**
     * Execute post.
     *
     * @param <T> the generic type
     * @param url the url
     * @param requestBody the request body
     * @return the http response
     */
    public static HttpResponse executePost( final String url, final Object requestBody )
    {
        // check that url is not null
        if ( StringUtils.isBlank( url ) )
        {
            logger.error( "In executePost, URL is either null or blank." );
            return null;
        }
        // check that request body is not null.
        if ( requestBody == null )
        {
            logger.warn( "In executePost, request body is null." );
            return null;
        }
        HttpClient httpClient = HttpUtil.getHttpClient();
        HttpPost httpPost = new HttpPost( url );
        HttpEntity httpEntity = HttpUtil.getStringEntity( requestBody );
        if ( httpEntity == null )
        {
            logger.warn( "In executePost, failed to serialize request body as JSON payload." );
            return null;
        }
        httpPost.setEntity( httpEntity );
        httpPost.setHeader( HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON );
        httpPost.setHeader( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON );
        try
        {
            return httpClient.execute( httpPost );
        }
        catch ( IOException e )
        {
            logger.error( "In executePost, error while invoking POST on {}.", url, e );
            return null;
        }
    }

    /**
     * Invokes POST on the given URL using the given username and password for BASIC auth credentials. HttpResponse is
     * returned.
     *
     * @param url the url
     * @param userName the user name
     * @param password the password
     * @return the http response
     */
    public static HttpResponse executePostWithBasicAuth( final String url, final String userName,
                                                         final String password )
    {
        // check that url is not null
        if ( StringUtils.isBlank( url ) )
        {
            logger.warn( "In executePostWithBasicAuth, URL is either null or blank." );
            return null;
        }
        HttpClient httpClient = HttpUtil.getHttpClientWithBasicAuth( userName, password );
        if ( httpClient == null )
        {
            logger.warn( "In executePostWithBasicAuth, unable to get HttpClient with BasicAuth." );
            return null;
        }
        try
        {
            return httpClient.execute( new HttpPost( url ) );
        }
        catch ( IOException e )
        {
            logger.error( "In executePostWithBasicAuth, error invoking POST on '{}'", url, e );
            return null;
        }
    }

    /**
     * Invokes POST on the given URL using the given username and password for BASIC auth credentials. If either 200(OK)
     * or 201(ACCEPTED) response is received for the invoked API, response body is extracted and converted to the given
     * TypeReference type object using Jackson.
     *
     * @param <T> the generic type
     * @param url the url
     * @param userName the user name
     * @param password the password
     * @param typeRef the type ref
     * @return the t
     */
    public static <T> T executePostWithBasicAuth( final String url, final String userName, final String password,
                                                  final TypeReference<T> typeRef )
    {
        // check that url is not null
        if ( StringUtils.isBlank( url ) )
        {
            logger.warn( "In executePostWithBasicAuth, URL is either null or blank." );
            return null;
        }
        ResponseHandler<T> responseHandler = HttpUtil.getResponseHandler( typeRef );
        HttpClient httpClient = HttpUtil.getHttpClientWithBasicAuth( userName, password );
        if ( httpClient == null )
        {
            logger.warn( "In executePostWithBasicAuth, unable to get HttpClient with BasicAuth." );
            return null;
        }
        try
        {
            return httpClient.execute( new HttpPost( url ), responseHandler );
        }
        catch ( IOException e )
        {
            logger.error( "In executePostWithBasicAuth, error invoking POST on '{}'", url, e );
            return null;
        }
    }

    /**
     * Invokes POST on the given URL using the given username and password for BASIC auth credentials. If either 200(OK)
     * or 201(ACCEPTED) response is received for the invoked API, response body is extracted and converted to the given
     * Class type object using Jackson.
     *
     * @param <T> the generic type
     * @param url the url
     * @param userName the user name
     * @param password the password
     * @param valueType the value type
     * @return the t
     */
    public static <T> T executePostWithBasicAuth( final String url, final String userName, final String password,
                                                  final Class<T> valueType )
    {
        // check that url is not null
        if ( StringUtils.isBlank( url ) )
        {
            logger.warn( "In executePostWithBasicAuth, URL is either null or blank." );
            return null;
        }
        ResponseHandler<T> responseHandler = HttpUtil.getResponseHandler( valueType );
        HttpClient httpClient = HttpUtil.getHttpClientWithBasicAuth( userName, password );
        if ( httpClient == null )
        {
            logger.warn( "In executePostWithBasicAuth, unable to get HttpClient with BasicAuth." );
            return null;
        }
        try
        {
            return httpClient.execute( new HttpPost( url ), responseHandler );
        }
        catch ( IOException e )
        {
            logger.error( "In executePostWithBasicAuth, error invoking POST on '{}'", url, e );
            return null;
        }
    }

    /**
     * Returns HttpClient with basic auth.
     *
     * @param userName the user name
     * @param password the password
     * @return the http client with basic auth
     */
    private static HttpClient getHttpClientWithBasicAuth( final String userName, final String password )
    {
        // check that userName is not null
        if ( StringUtils.isBlank( userName ) )
        {
            logger.warn( "In getHttpClientWithBasicAuth, userName is either null or blank." );
            return null;
        }
        // check that password is not null
        if ( StringUtils.isBlank( password ) )
        {
            logger.warn( "In getHttpClientWithBasicAuth, password is either null or blank." );
            return null;
        }
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials( userName, password );
        credentialsProvider.setCredentials( AuthScope.ANY, usernamePasswordCredentials );
        return HttpClientBuilder.create().setDefaultCredentialsProvider( credentialsProvider ).build();
    }

    /**
     * Returns the default HttpClient object.
     *
     * @return the http client
     */
    public static HttpClient getHttpClient()
    {
        return HttpClients.createDefault();
    }

    /**
     * Gets the string entity.
     *
     * @param requestBody the request body
     * @return the string entity
     */
    private static StringEntity getStringEntity( final Object requestBody )
    {
        // check that request body is not null.
        if ( requestBody == null )
        {
            logger.warn( "In getStringEntity, request body is null." );
            return null;
        }
        final String payload = HmsGenericUtil.getPayload( requestBody );
        if ( payload == null )
        {
            logger.error( "In getStringEntity, unable to convert request body to json string." );
            return null;
        }
        return new StringEntity( payload, StandardCharsets.UTF_8 );
    }

    /**
     * Execute put.
     *
     * @param url the url
     * @return the http response
     */
    public static HttpResponse executePut( final String url )
    {
        // check that url is not null
        if ( StringUtils.isBlank( url ) )
        {
            logger.error( "In executePut, URL is either null or blank." );
            return null;
        }
        return HttpUtil.executePut( url, HttpUtil.getHttpClient(), null );
    }

    /**
     * Execute put.
     *
     * @param apiUrl the api url
     * @param requestBody the request body
     * @return the http response
     */
    public static HttpResponse executePut( final String apiUrl, final String requestBody )
    {
        if ( StringUtils.isBlank( apiUrl ) )
        {
            logger.error( "In executePut, URL: '{}' is either null or blank.", apiUrl );
            return null;
        }
        if ( StringUtils.isBlank( requestBody ) )
        {
            logger.error( "In executePut, requestBody: '{}' is either null or blank.", requestBody );
            return null;
        }
        HttpPut httpPut = new HttpPut( apiUrl );
        httpPut.setHeader( HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN );
        httpPut.setHeader( HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN );
        HttpEntity httpEntity = new StringEntity( requestBody, StandardCharsets.UTF_8 );
        httpPut.setEntity( httpEntity );
        try
        {
            HttpClient httpClient = HttpUtil.getHttpClient();
            return httpClient.execute( httpPut );
        }
        catch ( IOException e )
        {
            logger.error( "In executePut, error invoking PUT on '{}'", apiUrl, e );
            return null;
        }
    }

    /**
     * Execute put.
     *
     * @param url the url
     * @param httpClient the http client
     * @param httpEntity the http entity
     * @return the http response
     */
    private static HttpResponse executePut( final String url, final HttpClient httpClient, final HttpEntity httpEntity )
    {
        // check that url is not null
        if ( StringUtils.isBlank( url ) )
        {
            logger.error( "In executePut, URL is either null or blank." );
            return null;
        }
        if ( httpClient == null )
        {
            logger.error( "In executePut, HttpClient is null." );
            return null;
        }
        HttpPut httpPut = new HttpPut( url );
        httpPut.setHeader( HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON );
        if ( httpEntity != null )
        {
            httpPut.setHeader( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON );
            httpPut.setEntity( httpEntity );
        }
        try
        {
            return httpClient.execute( httpPut );
        }
        catch ( IOException e )
        {
            logger.error( "In executePut, error invoking PUT on '{}'", url, e );
            return null;
        }
    }
}