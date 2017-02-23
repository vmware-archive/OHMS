/* ********************************************************************************
 * HttpUtilTest.java
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.resource.AboutResponse;
import com.vmware.vrack.hms.common.rest.model.HmsServiceState;
import com.vmware.vrack.hms.common.service.ServiceState;

/*
 * These tests need to run against HMS Aggregator. Hence, all these are ignored.
 */
@Ignore
public class HttpUtilTest
{

    @BeforeClass
    public static void setUpBeforeClass()
        throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass()
        throws Exception
    {
    }

    @Before
    public void setUp()
        throws Exception
    {
    }

    @After
    public void tearDown()
        throws Exception
    {
    }

    @Test
    public void testExecuteGet()
    {
        /*
         * Test for: public static HttpResponse executeGet(final String url);
         */
        HttpResponse httpResponse = HttpUtil.executeGet( "http://localhost:8080/hms-aggregator/api/1.0/hms/state" );
        assertNotNull( httpResponse );
        assertTrue( httpResponse instanceof HttpResponse );
        assertTrue( httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK );
    }

    @Test
    public void testExecuteGetWithValueType()
    {
        /*
         * Test for: public static <T> T executeGet(final String url, final Class<T> valueType);
         */
        HmsServiceState hmsServiceState =
            HttpUtil.executeGet( "http://localhost:8080/hms-aggregator/api/1.0/hms/state", HmsServiceState.class );
        assertNotNull( hmsServiceState );
        assertTrue( hmsServiceState instanceof HmsServiceState );
    }

    @Test
    public void testExecuteGetWithTypeRef()
    {
        /*
         * Test for: public static <T> T executeGet(final String url, final TypeReference<T> typeRef);
         */
        TypeReference<Map<String, AboutResponse>> typeRef = new TypeReference<Map<String, AboutResponse>>()
        {
        };
        Map<String, AboutResponse> response =
            HttpUtil.executeGet( "http://localhost:8080/hms-aggregator/api/1.0/hms/about", typeRef );
        assertNotNull( response );
    }

    @Test
    public void testExecutePost()
        throws ParseException, IOException
    {
        /*
         * Test for: public static <T> HttpResponse executePost(final String url)
         */
        HttpResponse httpResponse =
            HttpUtil.executePost( "http://localhost:8080/hms-aggregator/api/1.0/hms/state?serviceState="
                + ServiceState.RUNNING.toString() );
        assertNotNull( httpResponse );
        assertTrue( httpResponse instanceof HttpResponse );
        assertTrue( httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK );
    }

    @Test
    public void testExecutePostRequestBody()
    {
        /*
         * Test for: public static <T> HttpResponse executePost(final String url, final Object requestBody)
         */
        HttpResponse httpResponse =
            HttpUtil.executePost( "http://localhost:8080/hms-aggregator/api/1.0/hms/refreshinventory", "{}" );
        assertNotNull( httpResponse );
        assertTrue( httpResponse instanceof HttpResponse );
        assertTrue( httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR );
    }

    @Test
    public void testExecutePostRequestBodyAndValueTypeResponse()
    {
        /*
         * Test for: public static <T> T executePost(final String url, final Object requestBody, final Class<T>
         * valueType);
         */
        BaseResponse baseResponse =
            HttpUtil.executePost( "http://localhost:8080/hms-aggregator/api/1.0/hms/refreshinventory", "{}",
                                  BaseResponse.class );
        /*
         * In this case, /refreshinventory API will return 500 (INTERNAL_SERVER_ERROR). HttpUtil will parse response
         * body, only if response status code is 200(OK). Hence, response is null expected.
         */
        assertNull( baseResponse );
    }

    @Test
    public void testExecutePostWithoutRequestBodyAndTypeRefResponse()
    {
        /*
         * Test for: public static <T> T executePost(final String url, final TypeReference<T> typeRef)
         */
        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>()
        {
        };

        /*
         * As OOB Agent is not running, this call will return 500. Hence, response will be null;
         */
        Map<String, Object> sshKeyMap =
            HttpUtil.executePost( "http://localhost:8080/hms-aggregator/api/1.0/hms/sshkeys/create", typeRef );
        assertNull( sshKeyMap );
    }

    @Test
    public void testExecutePostWithValueTypeResponse()
        throws ParseException, IOException
    {
        /*
         * Test for: public static <T> T executePost(final String url, final Class<T> valueType)
         */
        BaseResponse baseResponse =
            HttpUtil.executePost( "http://localhost:8080/hms-aggregator/api/1.0/hms/state?serviceState="
                + ServiceState.RUNNING.toString(), BaseResponse.class );
        assertNotNull( baseResponse );
        assertTrue( baseResponse.getStatusCode() == HttpStatus.SC_NOT_MODIFIED );
    }

    @Test
    public void testExecutePostWithBasicAuth()
    {
        final String url = "https://api.github.com/authorizations";
        HttpResponse httpResponse = HttpUtil.executePostWithBasicAuth( url, "userName", "password" );
        assertNotNull( httpResponse );
        StatusLine statusLine = httpResponse.getStatusLine();
        assertNotNull( statusLine );
        assertTrue( statusLine.getStatusCode() == HttpStatus.SC_UNAUTHORIZED );
    }
}
