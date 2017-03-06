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
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.jcraft.jsch.Session;
import com.vmware.vrack.hms.aggregator.HostDataAggregator;
import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodePowerStatus;
import com.vmware.vrack.hms.common.util.EsxiSshUtil;
import com.vmware.vrack.hms.inventory.HmsDataCache;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

@RunWith( PowerMockRunner.class )
@ContextConfiguration( locations = { "/hms-aggregator-test.xml" } )
@PrepareForTest( { MonitoringUtil.class, HostDataAggregator.class, HMSLocalServerRestService.class, HmsDataCache.class,
    EsxiSshUtil.class } )
public class HMSLocalServerRestServiceTest
{
    private static Logger logger = Logger.getLogger( HMSLocalServerRestServiceTest.class );

    private MockMvc mockMvc;

    @Mock
    HostDataAggregator aggregator;

    @Mock
    HmsDataCache hmsDataCache;

    @Mock
    ServiceServerNode serviceServerNode;

    @Mock
    Session session;

    @InjectMocks
    HMSLocalServerRestService hmsLocalServerRestService;

    @Before
    public void initialize()
        throws HmsException
    {
        // Setup Spring test in standalone mode
        // this.mockMvc = MockMvcBuilders.standaloneSetup(new
        // HMSLocalServerRestService()).build();
        // this.mockMvc =
        // MockMvcBuilders.webAppContextSetup(applicationContext).build();

        // this must be called for the @Mock annotations above to be processed
        // and for the mock service to be injected into the controller under
        // test.
        MockitoAnnotations.initMocks( this );

        this.mockMvc = MockMvcBuilders.standaloneSetup( hmsLocalServerRestService ).build();

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

    @Test
    public void getPortName()
        throws Throwable
    {
        PowerMockito.mockStatic( EsxiSshUtil.class );

        MvcResult result;
        String portName = "sw p1";
        String resultOfCommand = "sw p1\n" + "sw p1\n";

        Mockito.when( EsxiSshUtil.getSessionObject( anyString(), anyString(), anyString(), any( Integer.class ),
                                                    any( Properties.class ) ) ).thenReturn( session );
        Mockito.doNothing().when( session ).connect( any( Integer.class ) );
        Mockito.when( EsxiSshUtil.executeCommand( any( Session.class ), anyString() ) ).thenReturn( resultOfCommand );

        result =
            this.mockMvc.perform( get( "http://localhost:8080/host/N1/portname" ) ).andExpect( status().isOk() ).andReturn();

        assertNotNull( result.getResponse() );
        String resultAsString = result.getResponse().getContentAsString();
        assertTrue( resultAsString.equals( portName ) );
    }

    @Test( expected = HmsException.class )
    public void getPortName_withDifferentPorts()
        throws Throwable
    {
        PowerMockito.mockStatic( EsxiSshUtil.class );

        MvcResult result;
        String resultOfCommand = "sw p1\n" + "sw p2\n";

        Mockito.when( EsxiSshUtil.getSessionObject( anyString(), anyString(), anyString(), any( Integer.class ),
                                                    any( Properties.class ) ) ).thenReturn( session );
        Mockito.doNothing().when( session ).connect( any( Integer.class ) );
        Mockito.when( EsxiSshUtil.executeCommand( any( Session.class ), anyString() ) ).thenReturn( resultOfCommand );
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1/portname" ) ).andExpect( status().isOk() ).andReturn();
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

    @Test( expected = HmsException.class )
    public void getPortName_withSinglePort()
        throws Throwable
    {
        PowerMockito.mockStatic( EsxiSshUtil.class );

        MvcResult result;
        String resultOfCommand = "sw p1\n";

        Mockito.when( EsxiSshUtil.getSessionObject( anyString(), anyString(), anyString(), any( Integer.class ),
                                                    any( Properties.class ) ) ).thenReturn( session );
        Mockito.doNothing().when( session ).connect( any( Integer.class ) );
        Mockito.when( EsxiSshUtil.executeCommand( any( Session.class ), anyString() ) ).thenReturn( resultOfCommand );
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/host/N1/portname" ) ).andExpect( status().isOk() ).andReturn();
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

    // @Test(expected = HMSRestException.class)
    @Test
    public void getHostInfo_nodeInNodeMap()
        throws Throwable
    {
        PowerMockito.mockStatic( MonitoringUtil.class );

        ServerNode serverNode = InventoryLoader.getInstance().getNode( "N1" );
        when( MonitoringUtil.getServerNodeOOB( anyString() ) ).thenReturn( serverNode );
        when( aggregator.getServerInfo( anyString() ) ).thenReturn( serverNode.getServerInfo( serverNode ) );

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

        /*
         * Map<String, ServerInfo> serverInfoMap = new HashMap<String, ServerInfo>(); ServerInfo serverInfo = new
         * ServerInfo(); serverInfo.setAdminStatus(NodeAdminStatus.OPERATIONAL.toString());
         * serverInfo.setOperationalStatus("true"); serverInfo.setPowered(true); serverInfo.setPowered(true);
         * serverInfo.setNodeId("N1"); serverInfoMap.put("N1", serverInfo);
         * when(hmsDataCache.getServerInfoMap()).thenReturn(serverInfoMap);
         */

        ServerNodePowerStatus powerstatus = new ServerNodePowerStatus();
        powerstatus.setDiscoverable( true );
        powerstatus.setPowered( true );
        powerstatus.setOperationalStatus( "true" );
        when( MonitoringUtil.getServerNodePowerStatusOOB( anyString() ) ).thenReturn( powerstatus );

        when( aggregator.getAndUpdateServerNodePowerStatus( anyString(),
                                                            any( ServerInfo.class ) ) ).thenReturn( powerstatus );

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
        ResponseEntity responseEntityMock = mock( ResponseEntity.class );
        HmsOobAgentRestTemplate restTemplateMock = mock( HmsOobAgentRestTemplate.class );
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplateMock );
        when( restTemplateMock.exchange( any( HttpMethod.class ), anyString(), anyString(),
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

        ResponseEntity responseEntityMock = mock( ResponseEntity.class );
        HmsOobAgentRestTemplate restTemplateMock = mock( HmsOobAgentRestTemplate.class );
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplateMock );
        when( restTemplateMock.exchange( any( HttpMethod.class ), anyString(), anyString(),
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
        ResponseEntity responseEntityMock = mock( ResponseEntity.class );
        HmsOobAgentRestTemplate restTemplateMock = mock( HmsOobAgentRestTemplate.class );
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplateMock );
        when( restTemplateMock.exchange( any( HttpMethod.class ), anyString(), anyString(),
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
