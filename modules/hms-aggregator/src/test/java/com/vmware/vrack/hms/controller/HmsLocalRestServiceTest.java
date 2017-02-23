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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vmware.vrack.hms.aggregator.util.InventoryUtil;
import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.resource.AboutResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;

@RunWith( PowerMockRunner.class )
@PrepareForTest( { HMSLocalRestService.class, MonitoringUtil.class, InventoryUtil.class } )
public class HmsLocalRestServiceTest
{
    @Mock
    InventoryUtil inventoryUtil;

    HMSLocalRestService hmsLocalRestServiceMock = mock( HMSLocalRestService.class );

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
        // InBandServiceProvider.addBoardService(node.getServiceObject(), new
        // InbandServiceTestImpl(), true);
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
        ResponseEntity responseEntityMock = mock( ResponseEntity.class );
        HmsOobAgentRestTemplate restTemplateMock = mock( HmsOobAgentRestTemplate.class );
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplateMock );
        when( restTemplateMock.exchange( any( HttpMethod.class ), anyString(), anyString(),
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
        ResponseEntity responseEntityMock = mock( ResponseEntity.class );

        HmsOobAgentRestTemplate restTemplateMock = mock( HmsOobAgentRestTemplate.class );
        whenNew( HmsOobAgentRestTemplate.class ).withNoArguments().thenReturn( restTemplateMock );

        when( restTemplateMock.exchange( any( HttpMethod.class ), anyString(),
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
         * mockStatic(InventoryUtil.class); when(InventoryUtil.initializeInventory (anyString())).thenReturn(true);
         * when(InventoryUtil.getOOBSupportedOperations(anyString(), anyInt(), anyString(), anyString())).thenReturn(new
         * HashMap<String, List<HmsApi>>()); HMSLocalRestService restService = new HMSLocalRestService(); BaseResponse
         * response = restService.initializeInventory(); assertNotNull(response); assertTrue(200 ==
         * response.getStatusCode());
         */
    }

    @Test
    public void updateInventoryTest_validHosts()
        throws Exception
    {
        List<String> invalidHosts = new ArrayList<String>();

        String requestBody =
            "{\"serverNodeList\":[{\"nodeId\":\"N5\",\"inBandIpAddress\":\"192.168.2.10\",\"inBandUserName\":\"read\"}]}";
        PowerMockito.mockStatic( InventoryUtil.class );
        Map<String, Object[]> nodeMap = new HashMap<String, Object[]>();
        ServerNode node = new ServerNode();
        node.setNodeID( "N5" );
        node.setIbIpAddress( "10.28.197.28" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );

        Object[] serverNodes = new Object[] { node };

        nodeMap.put( "hosts", serverNodes );

        when( InventoryUtil.initializeInventory( anyString() ) ).thenReturn( nodeMap );

        Map<String, Object> updatedServerInfoMap = new HashMap<String, Object>();
        ServerNode updatedNode1 = new ServerNode();
        updatedNode1.setNodeID( "N5" );
        updatedNode1.setIbIpAddress( "192.168.1.100" );
        updatedNode1.setOsUserName( "root" );
        updatedNode1.setOsPassword( "root123" );

        updatedServerInfoMap.put( "N5", updatedNode1 );

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatusCode( 200 );
        baseResponse.setStatusMessage( "Successfully updated the HMS inventory" );

        ResponseEntity responseMock = new ResponseEntity<Object>( baseResponse, HttpStatus.OK );

        when( InventoryUtil.getUpdatedServerInfos( any( Object[].class ),
                                                   anyList() ) ).thenReturn( updatedServerInfoMap );
        when( hmsLocalRestServiceMock.updateInventory( anyString() ) ).thenCallRealMethod();
        when( hmsLocalRestServiceMock.refreshInventory( anyString(),
                                                        any( HttpMethod.class ) ) ).thenReturn( new ResponseEntity<BaseResponse>( baseResponse,
                                                                                                                                  HttpStatus.OK ) );
        ResponseEntity response = hmsLocalRestServiceMock.updateInventory( requestBody );

        assertNotNull( response );
        assertEquals( responseMock.getStatusCode(), response.getStatusCode() );
    }

    @Test
    public void updateInventoryTest_inValidHosts()
        throws Exception
    {

        List<String> invalidHosts = new ArrayList<String>();
        invalidHosts.add( "N6" );
        String requestBody =
            "{\"serverNodeList\":[{\"nodeId\":\"N6\",\"inBandIpAddress\":\"192.168.2.10\",\"inBandUserName\":\"read\"}]}";
        PowerMockito.mockStatic( InventoryUtil.class );
        Map<String, Object[]> nodeMap = new HashMap<String, Object[]>();
        ServerNode node = new ServerNode();
        node.setNodeID( "N5" );
        node.setIbIpAddress( "10.28.197.28" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );

        Object[] serverNodes = new Object[] { node };

        nodeMap.put( "hosts", serverNodes );

        when( InventoryUtil.initializeInventory( anyString() ) ).thenReturn( nodeMap );

        Map<String, Object> updatedServerInfoMap = new HashMap<String, Object>();
        ServerNode updatedNode1 = new ServerNode();
        updatedNode1.setNodeID( "N6" );
        updatedNode1.setIbIpAddress( "192.168.2.10" );
        updatedNode1.setOsUserName( "read" );
        updatedNode1.setOsPassword( "root123" );

        updatedServerInfoMap.put( "N6", updatedNode1 );

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatusMessage( String.format( "Unable to update the HMS inventory, for invalid hosts %s",
                                                      invalidHosts ) );
        baseResponse.setStatusCode( HttpStatus.BAD_REQUEST.value() );
        ResponseEntity responseMock = new ResponseEntity<Object>( baseResponse, HttpStatus.BAD_REQUEST );

        when( InventoryUtil.getUpdatedServerInfos( any( Object[].class ),
                                                   anyList() ) ).thenReturn( updatedServerInfoMap );

        when( hmsLocalRestServiceMock.updateInventory( anyString() ) ).thenCallRealMethod();
        when( hmsLocalRestServiceMock.refreshInventory( anyString(),
                                                        any( HttpMethod.class ) ) ).thenReturn( new ResponseEntity<BaseResponse>( baseResponse,
                                                                                                                                  HttpStatus.OK ) );
        when( InventoryUtil.getInvalidHostsinRequest( any( Object[].class ), anyList() ) ).thenReturn( invalidHosts );
        ResponseEntity response = hmsLocalRestServiceMock.updateInventory( requestBody );

        assertNotNull( response );
        assertEquals( responseMock.getStatusCode(), response.getStatusCode() );
    }

    @Test
    public void testRemoveServer()
    {

    }

}
