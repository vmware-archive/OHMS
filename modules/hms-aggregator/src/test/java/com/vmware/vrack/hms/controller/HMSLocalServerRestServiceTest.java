/* ********************************************************************************
 * HMSLocalServerRestServiceTest.java
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.vmware.vrack.hms.aggregator.HostDataAggregator;
import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodePowerStatus;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

@RunWith( PowerMockRunner.class )
@ContextConfiguration( locations = { "/hms-aggregator-test.xml" } )
@PrepareForTest( { MonitoringUtil.class, HostDataAggregator.class, HMSLocalServerRestService.class } )
public class HMSLocalServerRestServiceTest
{
    private static Logger logger = Logger.getLogger( HMSLocalServerRestServiceTest.class );

    private MockMvc mockMvc;

    @Before
    public void initialize()
        throws HmsException
    {
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup( new HMSLocalServerRestService() ).build();
        // this.mockMvc =
        // MockMvcBuilders.webAppContextSetup(applicationContext).build();
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setIbIpAddress( "10.28.197.28" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );
        nodeMap.put( "N1", node );
        // populating Nodemap before we could query its peripheral info
        InventoryLoader.getInstance().setNodeMap( nodeMap );
        // Adding our test Implementation class to provide sample data.
        InBandServiceProvider.addBoardService( node.getServiceObject(), new InbandServiceTestImpl(), true );
    }

    @Test( expected = HMSRestException.class )
    public void getHddInfo_nodeNotInInventory()
        throws Throwable
    {
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        InventoryLoader.getInstance().setNodeMap( nodeMap );
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1/storageinfo" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "HDD" ) );
    }

    @Test( expected = HMSRestException.class )
    public void getHddInfo()
        throws Throwable
    {
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1/storageinfo" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
    }

    @Test( expected = HMSRestException.class )
    public void getStorageControllerInfo_nodeNotInInventory()
        throws Throwable
    {
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        InventoryLoader.getInstance().setNodeMap( nodeMap );
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1/storagecontrollerinfo" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "Intel Corporation" ) );
    }

    @Test( expected = HMSRestException.class )
    public void getStorageControllerInfo()
        throws Throwable
    {
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1/storagecontrollerinfo" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
    }

    @Test( expected = HMSRestException.class )
    public void getCpuInfo_nodeNotInInventory()
        throws Throwable
    {
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        InventoryLoader.getInstance().setNodeMap( nodeMap );
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1/cpuinfo" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "INTEL" ) );
    }

    @Test( expected = HMSRestException.class )
    public void getCpuInfo()
        throws Throwable
    {
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1/cpuinfo" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "INTEL" ) );
    }

    @Test( expected = HMSRestException.class )
    public void getMemoryInfo_nodeNotInInvnetory()
        throws Throwable
    {
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        InventoryLoader.getInstance().setNodeMap( nodeMap );
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1/memoryinfo" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "DIMM" ) );
    }

    @Test( expected = HMSRestException.class )
    public void getMemoryInfo()
        throws Throwable
    {
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1/memoryinfo" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "DIMM" ) );
    }

    @Test( expected = HMSRestException.class )
    public void getNicInfo_nodeNotInInventory()
        throws Throwable
    {
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        InventoryLoader.getInstance().setNodeMap( nodeMap );
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1/nicinfo" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "Ethernet" ) );
    }

    @Test( expected = HMSRestException.class )
    public void getNicInfo()
        throws Throwable
    {
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1/nicinfo" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "Ethernet" ) );
    }

    @Test( expected = HMSRestException.class )
    public void getHostInfo_nodeInNodeMap()
        throws Throwable
    {
        PowerMockito.mockStatic( MonitoringUtil.class );
        when( MonitoringUtil.getServerNodeOOB( anyString() ) ).thenReturn( InventoryLoader.getInstance().getNode( "N1" ) );
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "N1" ) );
    }

    @Test
    public void getPowerStatus_nodeInNodeMap()
        throws Exception
    {
        PowerMockito.mockStatic( MonitoringUtil.class );
        when( MonitoringUtil.getServerNodeOOB( anyString() ) ).thenReturn( InventoryLoader.getInstance().getNode( "N1" ) );
        ServerNodePowerStatus powerstatus = new ServerNodePowerStatus();
        powerstatus.setDiscoverable( true );
        powerstatus.setPowered( true );
        powerstatus.setOperationalStatus( "true" );
        when( MonitoringUtil.getServerNodePowerStatusOOB( anyString() ) ).thenReturn( powerstatus );
        MvcResult result =
            this.mockMvc.perform( get( "http://localhost:8080/host/N1/powerstatus" ) ).andExpect( status().isOk() ).andReturn();
        assertNotNull( result.getResponse() );
        logger.debug( "Power Response: " + result.getResponse().getContentAsString() );
        assertTrue( result.getResponse().getContentAsString().contains( "discoverable" ) );
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
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
        HMSLocalServerRestService hmsLocalServerRestService = new HMSLocalServerRestService();
        HttpServletRequest requestMock = mock( HttpServletRequest.class );
        HttpServletResponse responseMock = mock( HttpServletResponse.class );
        when( requestMock.getServletPath() ).thenReturn( "/test" );
        when( requestMock.getPathInfo() ).thenReturn( "/test" );
        when( requestMock.getQueryString() ).thenReturn( "test" );
        when( requestMock.getMethod() ).thenReturn( "GET" );
        ResponseEntity response =
            hmsLocalServerRestService.getMirrorHostInfo( null, HttpMethod.GET, requestMock, responseMock );
        assertSame( responseEntityMock, response );
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    @Test
    public void putMirrorHostInfo_nodeInNodeMap()
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
        HMSLocalServerRestService hmsLocalServerRestService = new HMSLocalServerRestService();
        HttpServletRequest requestMock = mock( HttpServletRequest.class );
        HttpServletResponse responseMock = mock( HttpServletResponse.class );
        when( requestMock.getServletPath() ).thenReturn( "/test" );
        when( requestMock.getPathInfo() ).thenReturn( "/test" );
        when( requestMock.getQueryString() ).thenReturn( "test" );
        when( requestMock.getMethod() ).thenReturn( "PUT" );
        ResponseEntity response =
            hmsLocalServerRestService.putMirrorHostInfo( null, HttpMethod.PUT, requestMock, responseMock );
        assertSame( responseEntityMock, response );
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    @Test
    public void updateNode_nodeInNodeMap()
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
        HMSLocalServerRestService hmsLocalServerRestService = new HMSLocalServerRestService();
        HttpServletRequest requestMock = mock( HttpServletRequest.class );
        HttpServletResponse responseMock = mock( HttpServletResponse.class );
        when( requestMock.getServletPath() ).thenReturn( "/test" );
        when( requestMock.getPathInfo() ).thenReturn( "/test" );
        when( requestMock.getQueryString() ).thenReturn( "test" );
        when( requestMock.getMethod() ).thenReturn( "PUT" );
        ResponseEntity response =
            hmsLocalServerRestService.updateNode( "N1", "power_up", null, HttpMethod.PUT, requestMock, responseMock );
        assertSame( responseEntityMock, response );
    }

    @Test
    public void getAllServerNodes_nodeInNodeMap()
        throws Exception
    {
        HMSLocalServerRestService hmsLocalServerRestService = new HMSLocalServerRestService();
        HttpServletRequest requestMock = mock( HttpServletRequest.class );
        HttpServletResponse responseMock = mock( HttpServletResponse.class );
        when( requestMock.getServletPath() ).thenReturn( "/test" );
        when( requestMock.getPathInfo() ).thenReturn( "/test" );
        when( requestMock.getQueryString() ).thenReturn( "test" );
        when( requestMock.getMethod() ).thenReturn( "GET" );
        Map<String, ServerNode> response =
            hmsLocalServerRestService.getAllServerNodes( null, HttpMethod.GET, requestMock, responseMock );
        assertNotNull( response );
        assertNotNull( response.get( "N1" ) );
    }
}
