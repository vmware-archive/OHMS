/* ********************************************************************************
 * MaintenanceInterceptorTest.java
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
package com.vmware.vrack.hms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.web.servlet.HandlerMapping;

import com.vmware.vrack.hms.common.exception.HMSRestException;

@RunWith( Theories.class )
@PrepareForTest( { MaintenanceInterceptor.class } )
public class MaintenanceInterceptorTest
{

    MaintenanceInterceptor interceptor = new MaintenanceInterceptor();

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Object handler;

    public static class I1
    {
        String remoteAdd;

        String url;

        public I1( String remoteAdd, String url )
        {
            this.remoteAdd = remoteAdd;
            this.url = url;
        }
    }

    public static class I2
    {
        String remoteAdd;

        String url;

        public I2( String remoteAdd, String url )
        {
            this.remoteAdd = remoteAdd;
            this.url = url;
        }
    }

    public static class I3
    {
        String remoteAdd;

        String url;

        public I3( String remoteAdd, String url )
        {
            this.remoteAdd = remoteAdd;
            this.url = url;
        }
    }

    @DataPoint
    public static I1 input1 = new I1( "127.0.0.1", "/hms-aggregator/api/1.0/hms/nodes" );

    @DataPoint
    public static I1 input2 = new I1( "::1", "/hms-aggregator/api/1.0/hms/nodes" );

    @DataPoint
    public static I1 input3 = new I1( "0:0:0:0:0:0:0:1", "/hms-aggregator/api/1.0/hms/nodes" );

    @DataPoint
    public static I2 input4 = new I2( "192.168.100.2", "/hms-aggregator/api/1.0/hms/about" );

    @DataPoint
    public static I2 input5 = new I2( "192.168.100.2", "/hms-aggregator/api/1.0/hms/upgrade" );

    @DataPoint
    public static I3 input6 = new I3( "192.168.100.2", "/hms-aggregator/api/1.0/hms/nodes" );

    @Before
    public void init()
    {
        MockitoAnnotations.initMocks( this );
        interceptor.setLocalIpRegex( "127.0.0.1|::1|0:0:0:0:0:0:0:1" );
        interceptor.setPrivateIpEligibleApisRegEx( ".*/hms-aggregator/api/1.0/hms/about.*|.*/hms-aggregator/api/1.0/hms/upgrade.*" );
        Mockito.when( request.getMethod() ).thenReturn( "GET" );
        Mockito.when( request.getAttribute( HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE ) ).thenReturn( "" );
        Mockito.when( request.getAttribute( HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE ) ).thenReturn( "" );
    }

    @Test
    @Theory
    public void testIfTheRequestIsFromValidSource( I1 input )
        throws Exception
    {
        Mockito.when( request.getRequestURL() ).thenReturn( new StringBuffer( input.url ) );
        Mockito.when( request.getRemoteAddr() ).thenReturn( input.remoteAdd );
        interceptor.preHandle( request, response, handler );
    }

    @Test
    @Theory
    public void testIfTheRequestIsFromValidSource( I2 input )
        throws Exception
    {
        Mockito.when( request.getRequestURL() ).thenReturn( new StringBuffer( input.url ) );
        Mockito.when( request.getRemoteAddr() ).thenReturn( input.remoteAdd );
        interceptor.preHandle( request, response, handler );
    }

    @Test( expected = HMSRestException.class )
    @Theory
    public void testIfTheRequestIsFromValidSource( I3 input )
        throws Exception
    {
        Mockito.when( request.getRequestURL() ).thenReturn( new StringBuffer( input.url ) );
        Mockito.when( request.getRemoteAddr() ).thenReturn( input.remoteAdd );
        interceptor.preHandle( request, response, handler );
    }
}
