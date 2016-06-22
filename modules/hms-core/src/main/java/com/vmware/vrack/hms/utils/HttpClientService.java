/* ********************************************************************************
 * HttpClientService.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.HmsConfigHolder;

/**
 * @author ambi
 */
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
        this.ignoreSSLWithBasicAuth = withSSLIgnore();
        this.defaulClient = new DefaultHttpClient();
        String prmBasicAuthUser =
            HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS, "prm.basic.auth.user" );
        String prmBasicAuthPass =
            HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS, "prm.basic.auth.password" );
        Credentials credentials = new UsernamePasswordCredentials( prmBasicAuthUser, prmBasicAuthPass );
        ignoreSSLWithBasicAuth.getCredentialsProvider().setCredentials( AuthScope.ANY, credentials );
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

    // Returns HttpClient with a TrustManager that trusts all certificates.
    private DefaultHttpClient withSSLIgnore()
        throws KeyManagementException, NoSuchAlgorithmException
    {
        PoolingClientConnectionManager conMan = new PoolingClientConnectionManager();
        conMan.setMaxTotal( 5 );
        DefaultHttpClient base = new DefaultHttpClient( conMan );
        SSLContext sslContext = SSLContext.getInstance( "TLS" );
        sslContext.init( null, new TrustManager[] { new X509TrustManager()
        {
            public X509Certificate[] getAcceptedIssuers()
            {
                return null;
            }

            public void checkClientTrusted( X509Certificate[] certs, String authType )
            {
            }

            public void checkServerTrusted( X509Certificate[] certs, String authType )
            {
                ;
            }
        } }, new SecureRandom() );
        SSLSocketFactory sf = new SSLSocketFactory( sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );
        ClientConnectionManager ccm = base.getConnectionManager();
        SchemeRegistry schemeRegistry = ccm.getSchemeRegistry();
        // Registering default http/https schemes.
        schemeRegistry.register( new Scheme( "https", 443, sf ) );
        DefaultHttpClient httpClient = new DefaultHttpClient( ccm, base.getParams() );
        return httpClient;
    }

    private String getResponseAsString( HttpResponse response )
        throws IOException
    {
        HttpEntity entity = response.getEntity();
        String resp = EntityUtils.toString( entity );
        return resp;
    }
}
