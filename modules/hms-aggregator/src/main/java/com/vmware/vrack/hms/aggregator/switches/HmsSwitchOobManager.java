/* ********************************************************************************
 * HmsSwitchOobManager.java
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
package com.vmware.vrack.hms.aggregator.switches;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.exception.HMSRestException;

@Component
public class HmsSwitchOobManager
{
    @Autowired
    RestTemplate restTemplate;

    @Value( "${hms.switch.host}" )
    private String hmsIpAddr;

    @Value( "${hms.switch.port}" )
    private int hmsPort;

    @Value( "${hms.switch.username}" )
    private String hmsOobUsername;

    @Value( "${hms.switch.password}" )
    private String hmsOobPassword;

    private static final String HMS_API_PREFIX = "/api/1.0/hms/switches";

    private static Logger logger = LoggerFactory.getLogger( HmsSwitchOobManager.class );

    /**
     * Works only for HTTP GET
     *
     * @param metricURI
     * @param metricClazz
     * @return
     * @throws HMSRestException
     */
    public <U> List<U> parseGetListResponse( String metricURI, Class<U> metricClazz )
        throws HMSRestException
    {
        ResponseEntity<String> hmsJsonDataResp = null;
        URI uri = null;
        try
        {
            try
            {
                uri = new URI( "http", null, hmsIpAddr, hmsPort, HMS_API_PREFIX + metricURI, null, null );
            }
            catch ( URISyntaxException e )
            {
                throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                            "Exception while parsing uri. URI: " + HMS_API_PREFIX + metricURI );
            }
            hmsJsonDataResp = restTemplate.getForEntity( uri.toString(), String.class );
            HttpStatus statusCode = hmsJsonDataResp.getStatusCode();
            if ( statusCode != HttpStatus.OK )
            {
                return null;
            }
        }
        catch ( Exception e )
        {
            logger.error( String.format( "ERROR calling HMS OOB url: %s. ErrorMessage: %s", metricURI,
                                         e.getMessage() ) );
            return null;
        }
        String hmsJsonData = hmsJsonDataResp.getBody();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.readValue( hmsJsonData,
                                     mapper.getTypeFactory().constructCollectionType( List.class, metricClazz ) );
        }
        catch ( Exception e )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while parsing message body. Body: " + hmsJsonData );
        }
    }

    /**
     * Works for all methods but cannot parse a response containing a list of objects
     *
     * @param method
     * @param metricURI
     * @param payload
     * @param metricClazz
     * @return
     * @throws HMSRestException
     */
    private <U> U parseResponse( HttpMethod method, Object payload, String metricURI, TypeReference<U> metricClazz )
        throws HMSRestException
    {
        ResponseEntity<String> hmsJsonDataResp = null;
        String jsonPayload = null;
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            jsonPayload = mapper.writeValueAsString( payload );
        }
        catch ( Exception e )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Error in converting object into JSON String. Reason: " + e.getMessage() );
        }
        try
        {
            // URL input fields should already encoded,
            URI requestURL = null;
            try
            {
                requestURL = new URI( "http", null, hmsIpAddr, hmsPort, HMS_API_PREFIX + metricURI, null, null );
            }
            catch ( URISyntaxException e )
            {
                throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                            "Exception while parsing uri. URI: " + HMS_API_PREFIX + metricURI );
            }
            if ( method != null && method != HttpMethod.GET )
            {
                HttpHeaders headers = new HttpHeaders();
                headers.add( "Content-Type", "application/json" );
                HttpEntity<Object> entity = new HttpEntity<Object>( jsonPayload, headers );
                hmsJsonDataResp = restTemplate.exchange( URLDecoder.decode( requestURL.toString(), "UTF-8" ), method,
                                                         entity, String.class );
            }
            else
            {
                hmsJsonDataResp = restTemplate.getForEntity( requestURL, String.class );
            }
            HttpStatus statusCode = hmsJsonDataResp.getStatusCode();
            if ( statusCode != HttpStatus.OK )
            {
                return null;
            }
        }
        catch ( Exception e )
        {
            logger.error( String.format( "ERROR calling HMS OOB url: %s. ErrorMessage: %s", metricURI,
                                         e.getMessage() ) );
            return null;
        }
        String hmsJsonData = hmsJsonDataResp.getBody();
        if ( hmsJsonData == null )
            return null;
        try
        {
            return mapper.readValue( hmsJsonData, metricClazz );
        }
        catch ( Exception e )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while parsing message body. Body: " + hmsJsonData );
        }
    }

    /**
     * Toned down version to get simple objects based on uri from HMS OOB agent
     *
     * @param metricClazz
     * @param metricURI
     * @return
     * @throws HMSRestException
     */
    public <U> U parseGetResponse( TypeReference<U> metricClazz, String metricURI )
        throws HMSRestException
    {
        return parseResponse( HttpMethod.GET, null, metricURI, metricClazz );
    }

    /**
     * Toned down version to PUT objects based on uri and get simple response from HMS OOB agent
     *
     * @param metricClazz
     * @param object
     * @param metricURI
     * @return
     * @throws HMSRestException
     */
    public <U> U parsePutResponse( TypeReference<U> metricClazz, Object object, String metricURI )
        throws HMSRestException
    {
        return parseResponse( HttpMethod.PUT, object, metricURI, metricClazz );
    }

    /**
     * Toned down version to delete objects based on uri from HMS OOB agent
     *
     * @param metricClazz
     * @param metricURI
     * @return
     * @throws HMSRestException
     */
    public <U> U parseDeleteResponse( TypeReference<U> metricClazz, String metricURI )
        throws HMSRestException
    {
        return parseResponse( HttpMethod.DELETE, null, metricURI, metricClazz );
    }
}
