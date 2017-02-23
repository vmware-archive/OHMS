/* ********************************************************************************
 * HmsAggregatorSwitchTest.java
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
package com.vmware.vrack.hms.switches;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.aggregator.switches.HmsSwitchManager;
import com.vmware.vrack.hms.aggregator.switches.HmsSwitchOobManager;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;

@RunWith( PowerMockRunner.class )
@PowerMockRunnerDelegate( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/hms-spring-aggregator-test.xml" } )
@PrepareForTest( { HmsSwitchOobManager.class, HmsSwitchManager.class } )
public class HmsAggregatorSwitchTest
{

    private static final String hmsSwitchOobUrlBase = "/api/1.0/hms/switches/";

    @Autowired
    @Spy
    private HmsSwitchManager hmsSwitchManager = new HmsSwitchManager();

    @Autowired
    private ApplicationContext context;

    @Autowired
    private HmsSwitchOobManager hmsSwitchOobManager;

    @Mock
    HmsOobAgentRestTemplate<?> restTemplate;

    @SuppressWarnings( "unchecked" )
    @Before
    public void init()
        throws Exception
    {

        MockitoAnnotations.initMocks( this );
        ObjectMapper mapper = new ObjectMapper();

        ReflectionTestUtils.setField( hmsSwitchManager, "hmsSwitchOobManager", hmsSwitchOobManager );
        ReflectionTestUtils.setField( hmsSwitchManager, "context", context );

        String path = hmsSwitchOobUrlBase + "S1/ports";
        Mockito.when( restTemplate.getForEntity( eq( path ),
                                                 any( Class.class ) ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchPortNameList() ),
                                                                                                          HttpStatus.OK ) );

        path = hmsSwitchOobUrlBase + "S1/vlans";
        Mockito.when( restTemplate.getForEntity( eq( path ),
                                                 any( Class.class ) ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchVlanNameList() ),
                                                                                                          HttpStatus.OK ) );

        path = hmsSwitchOobUrlBase + "S1/vlansbulk";
        Mockito.when( restTemplate.getForEntity( eq( path ),
                                                 any( Class.class ) ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchVlanList() ),
                                                                                                          HttpStatus.OK ) );

        path = hmsSwitchOobUrlBase + "S1/vlans/2011";
        Mockito.when( restTemplate.getForEntity( eq( path ),
                                                 any( Class.class ) ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchVlan() ),
                                                                                                          HttpStatus.OK ) );

        path = hmsSwitchOobUrlBase + "S1/lacpgroups";
        Mockito.when( restTemplate.getForEntity( eq( path ),
                                                 any( Class.class ) ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchLacpGroupNameList() ),
                                                                                                          HttpStatus.OK ) );

        path = hmsSwitchOobUrlBase + "S1/lacpgroups/bd-test";
        Mockito.when( restTemplate.getForEntity( eq( path ),
                                                 any( Class.class ) ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchLacpGroup() ),
                                                                                                          HttpStatus.OK ) );

        path = hmsSwitchOobUrlBase + "S1/ospf";
        Mockito.when( restTemplate.getForEntity( eq( path ),
                                                 any( Class.class ) ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchOspf() ),
                                                                                                          HttpStatus.OK ) );

        path = hmsSwitchOobUrlBase + "S1/portsbulk";
        Mockito.when( restTemplate.getForEntity( eq( path ),
                                                 any( Class.class ) ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchPortList() ),
                                                                                                          HttpStatus.OK ) );

        path = hmsSwitchOobUrlBase + "S1/ports/swp1";
        Mockito.when( restTemplate.getForEntity( eq( path ),
                                                 any( Class.class ) ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchPort() ),
                                                                                                          HttpStatus.OK ) );

        path = hmsSwitchOobUrlBase + "S1";
        Mockito.when( restTemplate.getForEntity( eq( path ),
                                                 any( Class.class ) ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchInfo() ),
                                                                                                          HttpStatus.OK ) );

        path = hmsSwitchOobUrlBase;
        Mockito.when( restTemplate.getForEntity( eq( path ),
                                                 any( Class.class ) ) ).thenReturn( new ResponseEntity<String>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchIdList() ),
                                                                                                                HttpStatus.OK ) );

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatusMessage( String.format( "Operation succeeded" ) );
        baseResponse.setStatusCode( HttpStatus.OK.value() );

        ResponseEntity<String> mockedHmsResponse =
            new ResponseEntity<String>( mapper.writeValueAsString( baseResponse ), HttpStatus.OK );

        Mockito.when( restTemplate.exchange( any( HttpMethod.class ), anyString(),
                                             any( Class.class ) ) ).thenReturn( mockedHmsResponse );

        Mockito.when( restTemplate.exchange( any( HttpMethod.class ), anyString(), any( Class.class ),
                                             any( Boolean.class ) ) ).thenReturn( mockedHmsResponse );
        // Mockito.when(restTemplate.exchange(any(HttpMethod.class), anyString(),
        // any(Class.class)))).thenReturn(mockedHmsResponse);
    }

    @Test
    public void testSpringDeps()
    {
        assertNotNull( hmsSwitchManager );
        assertNotNull( context );
        assertNotNull( hmsSwitchOobManager );
        assertNotNull( restTemplate );
    }

    @Test
    public void testGetSwitchBgpConfig()
        throws Exception
    {
        // assertNotNull(hmsSwitchManager.getSwitchBgpConfig("S1"));
    }

    @Test
    public void testCreateOrUpdateSwitchBgpConfig()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );

        hmsSwitchManager.createOrUpdateSwitchBgpConfig( "S1", HmsAggregatorDummyDataProvider.getNBSwitchBgpConfig() );
    }

    @Test
    public void testDeleteSwitchBgpConfig()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );

        hmsSwitchManager.deleteSwitchBgpConfig( "S1" );
    }

    @Test
    public void testGetSwitchOspfv2Config()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        assertNotNull( hmsSwitchManager.getSwitchOspfv2Config( "S1" ) );
    }

    @Test
    public void testCreateOrUpdateSwitchOspfv2Config()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        hmsSwitchManager.createOrUpdateSwitchOspfv2Config( "S1",
                                                           HmsAggregatorDummyDataProvider.getNBSwitchOspfv2Config() );
    }

    @Test
    public void testDeleteSwitchOspfv2Config()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );

        hmsSwitchManager.deleteSwitchOspfv2Config( "S1" );
    }

    @Test
    public void testGetSwitchMcLagConfig()
        throws Exception
    {
        // assertNotNull(hmsSwitchManager.getSwitchMcLagConfig("S1"));
    }

    @Test
    public void testDeleteSwitchMcLagConfig()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        hmsSwitchManager.deleteSwitchMcLagConfig( "S1" );
    }

    @Test
    public void testGetSwitchLagConfig()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        assertNotNull( hmsSwitchManager.getSwitchLagConfig( "S1", "bd-test" ) );
    }

    @Test
    public void testCreateOrUpdateSwitchLagConfig()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        hmsSwitchManager.createOrUpdateSwitchLagConfig( "S1", HmsAggregatorDummyDataProvider.getNBSwitchLagConfig() );
    }

    @Test
    public void testDeleteSwitchLagConfig()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        hmsSwitchManager.deleteSwitchLagConfig( "S1", "bd-test" );
    }

    @Test
    public void testGetSwitchAllLagsConfigs()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        assertNotNull( hmsSwitchManager.getSwitchAllLagsConfigs( "S1" ) );
    }

    @Test
    public void testGetSwitchVlanConfig()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        assertNotNull( hmsSwitchManager.getSwitchVlanConfig( "S1", "2011" ) );
    }

    @Test
    public void testCreateOrUpdateSwitchVlanConfig()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        hmsSwitchManager.createOrUpdateSwitchVlanConfig( "S1", HmsAggregatorDummyDataProvider.getNBSwitchVlanConfig() );
    }

    @Test
    public void testDeleteSwitchVlanConfig()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        hmsSwitchManager.deleteSwitchVlanConfig( "S1", "2011" );
    }

    @Test
    public void testGetSwitchAllVlansConfigs()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        assertNotNull( hmsSwitchManager.getSwitchAllVlansConfigs( "S1" ) );
    }

    @Test
    public void testConfigureIpv4DefaultRoute()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        hmsSwitchManager.configureIpv4DefaultRoute( "S1", "192.168.100.40", "swp1" );
    }

    @Test
    public void testDeleteIpv4DefaultRoute()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        hmsSwitchManager.deleteIpv4DefaultRoute( "S1" );
    }

    @Test
    public void testGetSwitchPortInfo()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        assertNotNull( hmsSwitchManager.getSwitchPortInfo( "S1", "swp1" ) );
    }

    @Test
    public void testUpdateSwitchPortConfig()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        hmsSwitchManager.updateSwitchPortConfig( "S1", "S1", HmsAggregatorDummyDataProvider.getNBSwitchPortConfig() );
    }

    @Test
    public void testApplyBulkConfigs()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        hmsSwitchManager.applyBulkConfigs( "S1", HmsAggregatorDummyDataProvider.getNBSwitchBulkConfigList() );
    }

    @Test
    public void testGetSwitchAllPortInfos()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        assertNotNull( hmsSwitchManager.getSwitchAllPortInfos( "S1" ) );
    }

    @Test
    public void testGetSwitchInfo()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        assertNotNull( hmsSwitchManager.getSwitchInfo( "S1" ) );
    }

    @Test
    public void testGetAllSwitchInfos()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        assertNotNull( hmsSwitchManager.getAllSwitchInfos() );
    }

    @Test
    public void testGetAllSwitchIds()
        throws Exception
    {
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplate );
        assertNotNull( hmsSwitchManager.getAllSwitchIds() );
    }

}
