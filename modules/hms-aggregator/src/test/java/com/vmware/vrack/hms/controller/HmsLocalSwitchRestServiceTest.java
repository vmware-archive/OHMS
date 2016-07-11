/* ********************************************************************************
 * HmsLocalSwitchRestServiceTest.java
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
package com.vmware.vrack.hms.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.switches.HmsAggregatorDummyDataProvider;

@RunWith( PowerMockRunner.class )
@ContextConfiguration( locations = { "/hms-aggregator-test.xml" } )
@PrepareForTest( { HmsSwitchRestService.class } )
public class HmsLocalSwitchRestServiceTest
{
    private static Logger logger = Logger.getLogger( HmsLocalSwitchRestServiceTest.class );

    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void initialize()
        throws HmsException
    {
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup( new HmsSwitchRestService() ).build();
        // this.mockMvc =
        // MockMvcBuilders.webAppContextSetup(applicationContext).build();
        Map<String, SwitchNode> switchNodeMap = new HashMap<String, SwitchNode>();
        SwitchNode node = new SwitchNode( "S1", "ssh", "10.28.197.242", 22, "cumulus", "root123" );
        switchNodeMap.put( "S1", node );
        // populating Nodemap before we could query its peripheral info
        InventoryLoader.getInstance().setSwitchNodeMap( switchNodeMap );
    }

    @Test( expected = NullPointerException.class )
    public void testGetSwitchInfo()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( get( "http://localhost:8080/napi/switches/S1" ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testGetSwitchBgpConfig()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( get( "http://localhost:8080/napi/switches/S1/bgp" ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testCreateOrUpdateSwitchBgpConfig()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( put( "http://localhost:8080/napi/switches/S1/bgp" ).content( mapper.writeValueAsString( HmsAggregatorDummyDataProvider.getNBSwitchBgpConfig() ).getBytes() ).accept( MediaType.APPLICATION_JSON ).contentType( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testDeleteSwitchBgpConfig()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( delete( "http://localhost:8080/napi/switches/S1/bgp" ).accept( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testGetSwitchOspfv2Config()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( get( "http://localhost:8080/napi/switches/S1/ospfv2" ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testCreateOrUpdateSwitchOspfv2Config()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( put( "http://localhost:8080/napi/switches/S1/ospfv2" ).content( mapper.writeValueAsString( HmsAggregatorDummyDataProvider.getNBSwitchOspfv2Config() ).getBytes() ).accept( MediaType.APPLICATION_JSON ).contentType( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testDeleteSwitchOspfv2Config()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( delete( "http://localhost:8080/napi/switches/S1/ospfv2" ).accept( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testGetSwitchMcLagConfig()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( get( "http://localhost:8080/napi/switches/S1/mclag" ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testDeleteSwitchMcLagConfig()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( delete( "http://localhost:8080/napi/switches/S1/mclag" ).accept( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testGetSwitchLagConfig()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( get( "http://localhost:8080/napi/switches/S1/lags/bd-test" ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testCreateOrUpdateSwitchLagConfig()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( put( "http://localhost:8080/napi/switches/S1/lags/" ).content( mapper.writeValueAsString( HmsAggregatorDummyDataProvider.getNBSwitchLagConfig() ).getBytes() ).accept( MediaType.APPLICATION_JSON ).contentType( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testDeleteSwitchLagConfig()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( delete( "http://localhost:8080/napi/switches/S1/lags/bd-test" ).accept( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testGetSwitchAllLagsConfigs()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( get( "http://localhost:8080/napi/switches/S1/lags" ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testGetSwitchVlanConfig()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( get( "http://localhost:8080/napi/switches/S1/vlans/2011" ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testCreateOrUpdateSwitchVlanConfig()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( put( "http://localhost:8080/napi/switches/S1/vlans" ).content( mapper.writeValueAsString( HmsAggregatorDummyDataProvider.getNBSwitchVlanConfig() ).getBytes() ).accept( MediaType.APPLICATION_JSON ).contentType( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testDeleteSwitchVlanConfig()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( delete( "http://localhost:8080/napi/switches/S1/vlans/2011" ).accept( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testGetSwitchAllVlansConfigs()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( get( "http://localhost:8080/napi/switches/S1/vlans" ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testConfigureIpv4DefaultRoute()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( put( "http://localhost:8080/napi/switches/S1/ipv4defaultroute?gateway=1.1.1.1&port=swp1" ).accept( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testDeleteIpv4DefaultRoute()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( delete( "http://localhost:8080/napi/switches/S1/ipv4defaultroute" ).accept( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testGetSwitchPortInfo()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( get( "http://localhost:8080/napi/switches/S1/ports/swp1" ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testUpdateSwitchPortConfig()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( put( "http://localhost:8080/napi/switches/S1/ports/swp1" ).content( mapper.writeValueAsString( HmsAggregatorDummyDataProvider.getNBSwitchPortConfig() ).getBytes() ).accept( MediaType.APPLICATION_JSON ).contentType( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testApplyBulkConfigs()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( put( "http://localhost:8080/napi/switches/S1/bulkconfigs" ).content( mapper.writeValueAsString( HmsAggregatorDummyDataProvider.getNBSwitchBulkConfigList() ).getBytes() ).accept( MediaType.APPLICATION_JSON ).contentType( MediaType.APPLICATION_JSON ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testGetSwitchAllPortInfos()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( get( "http://localhost:8080/napi/switches/S1/ports" ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }

    @Test( expected = NullPointerException.class )
    public void testGetAllSwitchInfos()
        throws Throwable
    {
        try
        {
            this.mockMvc.perform( get( "http://localhost:8080/napi/switches/" ) ).andReturn();
        }
        catch ( NestedServletException e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof NullPointerException );
            throw e.getCause();
        }
        throw new Exception( "failed" );
    }
}
