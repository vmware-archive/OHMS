/* ********************************************************************************
 * HmsOobAgentRestTemplate.java
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
package com.vmware.vrack.hms.rest.factory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.vmware.vrack.hms.common.exception.HmsException;

/**
 * Customized class of {@link RestTemplate} and is generic based on {@link HttpEntity} type. This class creates the
 * default instances of {@link HttpHeaders} and {@link HttpEntity} based on the constructor chosen. This helps avoiding
 * creating instance of these object from the clients every time {@link RestTemplate} object is created. <br/>
 * Note: The api's in this class will default to the Http/Https mode of communication as per the property -
 * hms.switch.default.scheme, if its required to have a communication irrespective of this value then use the api's in
 * the super class.
 *
 * @author spolepalli
 * @param <T>
 */
public class HmsOobAgentRestTemplate<T>
    extends RestTemplate
{

    private static final Logger LOG = LoggerFactory.getLogger( HmsOobAgentRestTemplate.class );

    private static final String CONTENT_TYPE = "Content-Type";

    private String contentType = MediaType.APPLICATION_JSON_VALUE;

    private HttpHeaders httpHeaders = new HttpHeaders();

    private HttpEntity<T> httpEntity = null;

    public HmsOobAgentRestTemplate()
    {
        httpHeaders.add( CONTENT_TYPE, this.contentType );
        this.httpEntity = new HttpEntity<T>( httpHeaders );
    }

    public HmsOobAgentRestTemplate( HttpHeaders httpHeaders )
        throws HmsException
    {
        this.httpEntity = new HttpEntity<T>( httpHeaders );
    }

    public HmsOobAgentRestTemplate( T entityBody, HttpHeaders httpHeaders )
        throws HmsException
    {
        if ( entityBody != null )
        {
            this.httpEntity = new HttpEntity<T>( entityBody, httpHeaders );
        }
        else
        {
            this.httpEntity = new HttpEntity<T>( httpHeaders );
        }
    }

    public HmsOobAgentRestTemplate( T entityBody )
        throws HmsException
    {
        httpHeaders.add( CONTENT_TYPE, this.contentType );
        this.httpEntity = new HttpEntity<T>( entityBody, httpHeaders );
    }

    public HmsOobAgentRestTemplate( T entityBody, String contentType )
        throws HmsException
    {
        this.contentType = contentType;
        httpHeaders.add( CONTENT_TYPE, this.contentType );

        if ( entityBody != null )
        {
            this.httpEntity = new HttpEntity<T>( entityBody, httpHeaders );
        }
        else
        {
            this.httpEntity = new HttpEntity<T>( httpHeaders );
        }
    }

    /**
     * Wrapper method for <br/>
     * super.exchange(uri, httpMethod, httpEntity, responseType)
     *
     * @param httpMethod
     * @param path
     * @param responseType
     * @return
     * @throws RestClientException
     * @throws HmsException
     */
    public <E> ResponseEntity<E> exchange( HttpMethod httpMethod, String path,
                                           ParameterizedTypeReference<E> responseType )
        throws RestClientException, HmsException
    {
        long currentTimeInMs = System.currentTimeMillis();
        URI uri = URIBuilder.getURI( path );
        ResponseEntity<E> response = super.exchange( uri, httpMethod, httpEntity, responseType );
        LOG.debug( "Time consumed for the rest call {} is {} ", uri.getPath(),
                   ( System.currentTimeMillis() - currentTimeInMs ) );

        return response;
    }

    /**
     * Wrapper method for <br/>
     * super.exchange(uri, httpMethod, httpEntity, responseType)
     *
     * @param httpMethod
     * @param path
     * @param query
     * @param responseType
     * @return
     * @throws RestClientException
     * @throws HmsException
     */
    public <E> ResponseEntity<E> exchange( HttpMethod httpMethod, String path, String query,
                                           ParameterizedTypeReference<E> responseType )
        throws RestClientException, HmsException
    {
        long currentTimeInMs = System.currentTimeMillis();
        URI uri = URIBuilder.getURI( path, query );
        ResponseEntity<E> response = super.exchange( uri, httpMethod, httpEntity, responseType );
        LOG.debug( "Time consumed for the rest call {} is {} ", uri.getPath(),
                   ( System.currentTimeMillis() - currentTimeInMs ) );

        return response;
    }

    /**
     * Wrapper method for <br/>
     * super.exchange(uri, httpMethod, httpEntity, responseType)
     *
     * @param httpMethod
     * @param path
     * @param responseType
     * @return
     * @throws RestClientException
     * @throws HmsException
     */
    public <E> ResponseEntity<E> exchange( HttpMethod httpMethod, String path, Class<E> responseType )
        throws RestClientException, HmsException
    {
        long currentTimeInMs = System.currentTimeMillis();
        URI uri = URIBuilder.getURI( path );
        ResponseEntity<E> response = super.exchange( uri, httpMethod, httpEntity, responseType );
        LOG.debug( "Time consumed for the rest call {} is {} ", uri.getPath(),
                   ( System.currentTimeMillis() - currentTimeInMs ) );
        return response;
    }

    /**
     * Wrapper method for <br/>
     * super.exchange(uri, httpMethod, httpEntity, responseType)
     *
     * @param httpMethod
     * @param path
     * @param responseType
     * @param decodeUrl
     * @return
     * @throws RestClientException
     * @throws HmsException
     */
    public <E> ResponseEntity<E> exchange( HttpMethod httpMethod, String path, Class<E> responseType,
                                           boolean decodeUrl )
        throws RestClientException, HmsException
    {
        long currentTimeInMs = System.currentTimeMillis();
        ResponseEntity<E> response = null;

        URI uri = URIBuilder.getURI( path );
        if ( decodeUrl )
        {
            try
            {
                String urlString = URLDecoder.decode( uri.toString(), "UTF-8" );
                response = super.exchange( urlString, httpMethod, httpEntity, responseType );
            }
            catch ( UnsupportedEncodingException e )
            {
                LOG.error( "Exception occured decoding the url {} with the exception {} ", uri, e );
                throw new HmsException( "Exception occured decoding the url", e );
            }
        }
        else
        {
            response = super.exchange( uri, httpMethod, httpEntity, responseType );
        }
        LOG.debug( "Time consumed for the rest call {} is {} ", uri.getPath(),
                   ( System.currentTimeMillis() - currentTimeInMs ) );

        return response;
    }

    /**
     * Wrapper method for <br/>
     * super.exchange(uri, httpMethod, httpEntity, responseType)
     *
     * @param httpMethod
     * @param path
     * @param query
     * @param responseType
     * @return
     * @throws RestClientException
     * @throws HmsException
     */
    public <E> ResponseEntity<E> exchange( HttpMethod httpMethod, String path, String query, Class<E> responseType )
        throws RestClientException, HmsException
    {
        long currentTimeInMs = System.currentTimeMillis();
        URI uri = URIBuilder.getURI( path, query );
        ResponseEntity<E> response = super.exchange( uri, httpMethod, httpEntity, responseType );
        LOG.debug( "Time consumed for the rest call {} is {} ", uri.getPath(),
                   ( System.currentTimeMillis() - currentTimeInMs ) );
        return response;
    }

    /**
     * Wrapper method for <br/>
     * super.getForEntity(uri, responseType)
     *
     * @param path
     * @param responseType
     * @return
     * @throws RestClientException
     * @throws HmsException
     */
    public <E> ResponseEntity<E> getForEntity( String path, Class<E> responseType )
        throws RestClientException, HmsException
    {
        long currentTimeInMs = System.currentTimeMillis();
        URI uri = URIBuilder.getURI( path );
        ResponseEntity<E> response = super.getForEntity( uri, responseType );
        LOG.debug( "Time consumed for the rest call {} is {} ", uri.getPath(),
                   ( System.currentTimeMillis() - currentTimeInMs ) );
        return response;
    }

    /**
     * Wrapper method for <br/>
     * super.getForEntity(uri, responseType)
     *
     * @param path
     * @param query
     * @param useDefaultScheme
     * @param responseType
     * @return
     * @throws RestClientException
     * @throws HmsException
     */
    public <E> ResponseEntity<E> getForEntity( String path, String query, Class<E> responseType )
        throws RestClientException, HmsException
    {
        long currentTimeInMs = System.currentTimeMillis();
        URI uri = URIBuilder.getURI( path, query );
        ResponseEntity<E> response = super.getForEntity( uri, responseType );
        LOG.debug( "Time consumed for the rest call {} is {} ", uri.getPath(),
                   ( System.currentTimeMillis() - currentTimeInMs ) );
        return response;
    }

    /**
     * Wrapper method for <br/>
     * super.put(uri, httpEntity)
     *
     * @param path
     * @param useDefaultScheme
     * @throws RestClientException
     * @throws HmsException
     */
    public void put( String path )
        throws RestClientException, HmsException
    {
        long currentTimeInMs = System.currentTimeMillis();
        URI uri = URIBuilder.getURI( path );
        super.put( uri, httpEntity );
        LOG.debug( "Time consumed for the rest call {} is {} ", uri.getPath(),
                   ( System.currentTimeMillis() - currentTimeInMs ) );
    }

    /**
     * Wrapper method for <br/>
     * super.getForEntity(uri, httpEntity)
     *
     * @param path
     * @param query
     * @throws RestClientException
     * @throws HmsException
     */
    public void put( String path, String query )
        throws RestClientException, HmsException
    {
        long currentTimeInMs = System.currentTimeMillis();
        URI uri = URIBuilder.getURI( path, query );
        super.put( uri, httpEntity );
        LOG.debug( "Time consumed for the rest call {} is {} ", uri.getPath(),
                   ( System.currentTimeMillis() - currentTimeInMs ) );
    }

    public HttpEntity<T> getHttpEntity()
    {
        return this.httpEntity;
    }

    public void setHttpEntity( HttpEntity<T> httpEntity )
    {
        this.httpEntity = httpEntity;
    }
}