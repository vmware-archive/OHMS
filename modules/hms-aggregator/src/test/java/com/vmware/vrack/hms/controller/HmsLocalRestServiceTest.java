/* ********************************************************************************
 * HmsLocalRestServiceTest.java
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vmware.vrack.hms.aggregator.util.InventoryUtil;
import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.AboutResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.inventory.InventoryLoader;

@RunWith( PowerMockRunner.class )
@PrepareForTest( { HMSLocalRestService.class, MonitoringUtil.class, InventoryUtil.class } )
public class HmsLocalRestServiceTest
{
    @Before
    public void initialize()
        throws HmsException
    {
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setIbIpAddress( "10.28.197.28" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );
        nodeMap.put( "N1", node );
        InventoryLoader.getInstance().setNodeMap( nodeMap );
        // InBandServiceProvider.addBoardService(node.getServiceObject(), new InbandServiceTestImpl(), true);
    }

    @Test
    public void generateHMSToken_test()
    {
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    @Test
    public void getMirrorHostInfo_nodeInNodeMap()
        throws Exception
    {
        URI uriMock = mock( URI.class );
        ResponseEntity responseEntityMock = mock( ResponseEntity.class );
        whenNew( URI.class ).withArguments( anyString(), anyString(), anyString(), anyInt(), anyString(), anyString(),
                                            anyString() ).thenReturn( uriMock );
        RestTemplate restTemplateMock = mock( RestTemplate.class );
        whenNew( RestTemplate.class ).withNoArguments().thenReturn( restTemplateMock );
        when( restTemplateMock.exchange( any( URI.class ), any( HttpMethod.class ), any( HttpEntity.class ),
                                         any( Class.class ) ) ).thenReturn( responseEntityMock );
        HMSLocalRestService hmsLocalRestService = new HMSLocalRestService();
        HttpServletRequest requestMock = mock( HttpServletRequest.class );
        HttpServletResponse responseMock = mock( HttpServletResponse.class );
        when( requestMock.getServletPath() ).thenReturn( "/test" );
        when( requestMock.getPathInfo() ).thenReturn( "/test" );
        when( requestMock.getQueryString() ).thenReturn( "test" );
        when( requestMock.getMethod() ).thenReturn( "GET" );
        ResponseEntity response =
            hmsLocalRestService.getMirrorHostInfo( null, HttpMethod.GET, requestMock, responseMock );
        assertSame( responseEntityMock, response );
    }

    @Test( expected = HmsException.class )
    public void getAboutResponse_OobUnavailable()
        throws Exception
    {
        HMSLocalRestService hmsLocalRestService = new HMSLocalRestService();
        Map<String, AboutResponse> response = hmsLocalRestService.getAboutResponse();
        assertNotNull( response );
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    @Test( expected = HmsException.class )
    public void getAboutResponse_OobAvailable()
        throws Exception
    {
        URI uriMock = mock( URI.class );
        ResponseEntity responseEntityMock = mock( ResponseEntity.class );
        whenNew( URI.class ).withArguments( anyString(), anyString(), anyString(), anyInt(), anyString(), anyString(),
                                            anyString() ).thenReturn( uriMock );
        RestTemplate restTemplateMock = mock( RestTemplate.class );
        whenNew( RestTemplate.class ).withNoArguments().thenReturn( restTemplateMock );
        when( restTemplateMock.exchange( any( URI.class ), any( HttpMethod.class ), any( HttpEntity.class ),
                                         any( Class.class ) ) ).thenReturn( responseEntityMock );
        when( responseEntityMock.getStatusCode() ).thenReturn( HttpStatus.OK );
        HMSLocalRestService hmsLocalRestService = new HMSLocalRestService();
        Map<String, AboutResponse> response = hmsLocalRestService.getAboutResponse();
        assertNotNull( response );
    }

    @Test
    @Ignore
    public void initializeInventory_test()
        throws HmsException
    {
        /*
         * mockStatic(InventoryUtil.class); when(InventoryUtil.initializeInventory(anyString())).thenReturn(true);
         * when(InventoryUtil.getOOBSupportedOperations(anyString(), anyInt(), anyString(), anyString())).thenReturn(new
         * HashMap<String, List<HmsApi>>()); HMSLocalRestService restService = new HMSLocalRestService(); BaseResponse
         * response = restService.initializeInventory(); assertNotNull(response); assertTrue(200 ==
         * response.getStatusCode());
         */
    }
}
