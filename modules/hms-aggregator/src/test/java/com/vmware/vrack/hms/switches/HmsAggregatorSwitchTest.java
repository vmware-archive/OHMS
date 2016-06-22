/* ********************************************************************************
 * HmsAggregatorSwitchTest.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.switches;

import static org.junit.Assert.assertNotNull;

import java.net.URI;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.aggregator.switches.HmsSwitchManager;
import com.vmware.vrack.hms.aggregator.switches.HmsSwitchOobManager;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.BaseResponse;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/hms-spring-aggregator-test.xml" } )
public class HmsAggregatorSwitchTest
{
    private static final String hmsSwitchOobUrlBase = "http://localhost:8448/api/1.0/hms/switches/";

    @Autowired
    @Spy
    private HmsSwitchManager hmsSwitchManager;

    @Autowired
    @Spy
    private ApplicationContext context;

    @Autowired
    @Spy
    private HmsSwitchOobManager hmsSwitchOobManager;

    @Mock
    RestTemplate restTemplate;

    @Before
    public void init()
        throws Exception
    {
        MockitoAnnotations.initMocks( this );
        ObjectMapper mapper = new ObjectMapper();
        ReflectionTestUtils.setField( hmsSwitchOobManager, "restTemplate", restTemplate );
        ReflectionTestUtils.setField( hmsSwitchManager, "hmsSwitchOobManager", hmsSwitchOobManager );
        ReflectionTestUtils.setField( hmsSwitchManager, "context", context );
        Mockito.when( restTemplate.getForEntity( new URI( hmsSwitchOobUrlBase + "S1/ports" ).toString(),
                                                 String.class ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchPortNameList() ),
                                                                                                    HttpStatus.OK ) );
        Mockito.when( restTemplate.getForEntity( new URI( hmsSwitchOobUrlBase + "S1/vlans" ).toString(),
                                                 String.class ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchVlanNameList() ),
                                                                                                    HttpStatus.OK ) );
        Mockito.when( restTemplate.getForEntity( new URI( hmsSwitchOobUrlBase + "S1/vlansbulk" ).toString(),
                                                 String.class ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchVlanList() ),
                                                                                                    HttpStatus.OK ) );
        Mockito.when( restTemplate.getForEntity( new URI( hmsSwitchOobUrlBase + "S1/vlans/2011" ),
                                                 String.class ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchVlan() ),
                                                                                                    HttpStatus.OK ) );
        Mockito.when( restTemplate.getForEntity( new URI( hmsSwitchOobUrlBase + "S1/lacpgroups" ).toString(),
                                                 String.class ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchLacpGroupNameList() ),
                                                                                                    HttpStatus.OK ) );
        Mockito.when( restTemplate.getForEntity( new URI( hmsSwitchOobUrlBase + "S1/lacpgroups/bd-test" ),
                                                 String.class ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchLacpGroup() ),
                                                                                                    HttpStatus.OK ) );
        Mockito.when( restTemplate.getForEntity( new URI( hmsSwitchOobUrlBase + "S1/ospf" ),
                                                 String.class ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchOspf() ),
                                                                                                    HttpStatus.OK ) );
        Mockito.when( restTemplate.getForEntity( new URI( hmsSwitchOobUrlBase + "S1/portsbulk" ).toString(),
                                                 String.class ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchPortList() ),
                                                                                                    HttpStatus.OK ) );
        Mockito.when( restTemplate.getForEntity( new URI( hmsSwitchOobUrlBase + "S1/ports/swp1" ),
                                                 String.class ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchPort() ),
                                                                                                    HttpStatus.OK ) );
        Mockito.when( restTemplate.getForEntity( new URI( hmsSwitchOobUrlBase + "S1" ),
                                                 String.class ) ).thenReturn( new ResponseEntity<>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchInfo() ),
                                                                                                    HttpStatus.OK ) );
        Mockito.when( restTemplate.getForEntity( new URI( hmsSwitchOobUrlBase ),
                                                 String.class ) ).thenReturn( new ResponseEntity<String>( mapper.writeValueAsString( HmsOobAgentDummyDataProvider.getSwitchIdList() ),
                                                                                                          HttpStatus.OK ) );
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatusMessage( String.format( "Operation succeeded" ) );
        baseResponse.setStatusCode( HttpStatus.OK.value() );
        ResponseEntity<String> mockedHmsResponse =
            new ResponseEntity<String>( mapper.writeValueAsString( baseResponse ), HttpStatus.OK );
        Mockito.when( restTemplate.exchange( Mockito.<URI>any(), Mockito.<HttpMethod>eq( HttpMethod.PUT ),
                                             Mockito.<HttpEntity<?>>any(),
                                             Mockito.<Class<String>>any() ) ).thenReturn( mockedHmsResponse );
        Mockito.when( restTemplate.exchange( Mockito.<String>any(), Mockito.<HttpMethod>eq( HttpMethod.PUT ),
                                             Mockito.<HttpEntity<?>>any(),
                                             Mockito.<Class<String>>any() ) ).thenReturn( mockedHmsResponse );
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
        throws HmsException
    {
        // assertNotNull(hmsSwitchManager.getSwitchBgpConfig("S1"));
    }

    @Test
    public void testCreateOrUpdateSwitchBgpConfig()
        throws HmsException
    {
        hmsSwitchManager.createOrUpdateSwitchBgpConfig( "S1", HmsAggregatorDummyDataProvider.getNBSwitchBgpConfig() );
    }

    @Test
    public void testDeleteSwitchBgpConfig()
        throws HmsException
    {
        hmsSwitchManager.deleteSwitchBgpConfig( "S1" );
    }

    @Test
    public void testGetSwitchOspfv2Config()
        throws HmsException
    {
        assertNotNull( hmsSwitchManager.getSwitchOspfv2Config( "S1" ) );
    }

    @Test
    public void testCreateOrUpdateSwitchOspfv2Config()
        throws HmsException
    {
        hmsSwitchManager.createOrUpdateSwitchOspfv2Config( "S1",
                                                           HmsAggregatorDummyDataProvider.getNBSwitchOspfv2Config() );
    }

    @Test
    public void testDeleteSwitchOspfv2Config()
        throws HmsException
    {
        hmsSwitchManager.deleteSwitchOspfv2Config( "S1" );
    }

    @Test
    public void testGetSwitchMcLagConfig()
        throws HmsException
    {
        // assertNotNull(hmsSwitchManager.getSwitchMcLagConfig("S1"));
    }

    @Test
    public void testDeleteSwitchMcLagConfig()
        throws HmsException
    {
        hmsSwitchManager.deleteSwitchMcLagConfig( "S1" );
    }

    @Test
    public void testGetSwitchLagConfig()
        throws HmsException
    {
        assertNotNull( hmsSwitchManager.getSwitchLagConfig( "S1", "bd-test" ) );
    }

    @Test
    public void testCreateOrUpdateSwitchLagConfig()
        throws HmsException
    {
        hmsSwitchManager.createOrUpdateSwitchLagConfig( "S1", HmsAggregatorDummyDataProvider.getNBSwitchLagConfig() );
    }

    @Test
    public void testDeleteSwitchLagConfig()
        throws HmsException
    {
        hmsSwitchManager.deleteSwitchLagConfig( "S1", "bd-test" );
    }

    @Test
    public void testGetSwitchAllLagsConfigs()
        throws HmsException
    {
        assertNotNull( hmsSwitchManager.getSwitchAllLagsConfigs( "S1" ) );
    }

    @Test
    public void testGetSwitchVlanConfig()
        throws HmsException
    {
        assertNotNull( hmsSwitchManager.getSwitchVlanConfig( "S1", "2011" ) );
    }

    @Test
    public void testCreateOrUpdateSwitchVlanConfig()
        throws HmsException
    {
        hmsSwitchManager.createOrUpdateSwitchVlanConfig( "S1", HmsAggregatorDummyDataProvider.getNBSwitchVlanConfig() );
    }

    @Test
    public void testDeleteSwitchVlanConfig()
        throws HmsException
    {
        hmsSwitchManager.deleteSwitchVlanConfig( "S1", "2011" );
    }

    @Test
    public void testGetSwitchAllVlansConfigs()
        throws HmsException
    {
        assertNotNull( hmsSwitchManager.getSwitchAllVlansConfigs( "S1" ) );
    }

    @Test
    public void testConfigureIpv4DefaultRoute()
        throws HmsException
    {
        hmsSwitchManager.configureIpv4DefaultRoute( "S1", "192.168.100.40", "swp1" );
    }

    @Test
    public void testDeleteIpv4DefaultRoute()
        throws HmsException
    {
        hmsSwitchManager.deleteIpv4DefaultRoute( "S1" );
    }

    @Test
    public void testGetSwitchPortInfo()
        throws HmsException
    {
        assertNotNull( hmsSwitchManager.getSwitchPortInfo( "S1", "swp1" ) );
    }

    @Test
    @Ignore
    public void testUpdateSwitchPortConfig()
        throws HmsException
    {
        hmsSwitchManager.updateSwitchPortConfig( "S1", "S1", HmsAggregatorDummyDataProvider.getNBSwitchPortConfig() );
    }

    @Test
    public void testApplyBulkConfigs()
        throws HmsException
    {
        hmsSwitchManager.applyBulkConfigs( "S1", HmsAggregatorDummyDataProvider.getNBSwitchBulkConfigList() );
    }

    @Test
    public void testGetSwitchAllPortInfos()
        throws HmsException
    {
        assertNotNull( hmsSwitchManager.getSwitchAllPortInfos( "S1" ) );
    }

    @Test
    public void testGetSwitchInfo()
        throws HmsException
    {
        assertNotNull( hmsSwitchManager.getSwitchInfo( "S1" ) );
    }

    @Test
    @Ignore
    public void testGetAllSwitchInfos()
        throws HmsException
    {
        assertNotNull( hmsSwitchManager.getAllSwitchInfos() );
    }

    @Test
    @Ignore
    public void testGetAllSwitchIds()
        throws HmsException
    {
        assertNotNull( hmsSwitchManager.getAllSwitchIds() );
    }
}
