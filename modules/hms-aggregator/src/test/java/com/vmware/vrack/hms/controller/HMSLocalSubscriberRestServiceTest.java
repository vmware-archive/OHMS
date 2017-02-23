/* ********************************************************************************
 * HMSLocalSubscriberRestServiceTest.java
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
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.common.events.BaseEventMonitoringSubscription;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscription;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

@RunWith( PowerMockRunner.class )
@ContextConfiguration( locations = { "/hms-aggregator-test.xml" } )
public class HMSLocalSubscriberRestServiceTest
{
    private static Logger logger = Logger.getLogger( HMSLocalSubscriberRestServiceTest.class );

    private MockMvc mockMvc;

    @Before
    public void initialize()
        throws HmsException
    {
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup( new HMSLocalSubscriberRestService() ).build();
        // this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();

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

    @Test
    public void nme_test()
        throws Exception
    {
        List<BaseEventMonitoringSubscription> subscriptions = new ArrayList<BaseEventMonitoringSubscription>();
        BaseEventMonitoringSubscription subscription = new BaseEventMonitoringSubscription();
        subscription.setNotificationEndpoint( "http://localhost:8080/response" );
        subscriptions.add( subscription );
        BaseEventMonitoringSubscription subscription2 = new BaseEventMonitoringSubscription();
        subscription2.setNotificationEndpoint( "http://localhost:8080/response2" );
        subscriptions.add( subscription2 );
        ObjectMapper mapper = new ObjectMapper();
        String subscriptionsAsString = mapper.writeValueAsString( subscriptions );

        MvcResult result =
            this.mockMvc.perform( post( ( "http://localhost:8080/events/register" ) ).content( subscriptionsAsString ).contentType( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() ).andReturn();
        assertNotNull( result.getResponse() );
        logger.info( "Response after non maskable endpoints registration: "
            + result.getResponse().getContentAsString() );
        assertTrue( result.getResponse().getContentAsString().contains( "NME request" ) );

        // Now trying to get endpoints registered for non-maskable events
        MvcResult nonmaskableEndpoints =
            this.mockMvc.perform( get( ( "http://localhost:8080/events/register" ) ) ).andExpect( status().isOk() ).andReturn();
        assertNotNull( nonmaskableEndpoints.getResponse() );
        logger.info( "Response that should return all endpoints: "
            + nonmaskableEndpoints.getResponse().getContentAsString() );
        assertTrue( nonmaskableEndpoints.getResponse().getContentAsString().contains( "http://localhost:8080/response" ) );
    }

    @Test
    public void subscribe_unsubscribe_test()
        throws Exception
    {
        List<EventMonitoringSubscription> subscriptions = new ArrayList<EventMonitoringSubscription>();
        EventMonitoringSubscription subscription = new EventMonitoringSubscription();
        subscription.setNotificationEndpoint( "http://localhost:8080/response" );
        subscription.setComponent( EventComponent.STORAGE );
        subscription.setNodeId( "N1" );
        subscription.setSubscriberId( "PRM" );
        subscriptions.add( subscription );

        EventMonitoringSubscription subscription2 = new EventMonitoringSubscription();
        subscription2.setNotificationEndpoint( "http://localhost:8080/response2" );
        subscription.setComponent( EventComponent.CPU );
        subscription.setNodeId( "N1" );
        subscription.setSubscriberId( "PRM" );
        subscriptions.add( subscription2 );

        ObjectMapper mapper = new ObjectMapper();
        String subscriptionsAsString = mapper.writeValueAsString( subscriptions );

        MvcResult result =
            this.mockMvc.perform( post( ( "http://localhost:8080/events/subscribe" ) ).content( subscriptionsAsString ).contentType( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() ).andReturn();
        assertNotNull( result.getResponse() );
        logger.info( "Response after event subscription: " + result.getResponse().getContentAsString() );
        assertTrue( result.getResponse().getContentAsString().contains( "subcription request" ) );

        // Now trying to get endpoints registered for non-maskable events
        MvcResult nonmaskableEndpoints =
            this.mockMvc.perform( get( ( "http://localhost:8080/events/PRM" ) ) ).andExpect( status().isOk() ).andReturn();
        assertNotNull( nonmaskableEndpoints.getResponse() );
        logger.info( "Response that should return all subscriptions by PRM: "
            + nonmaskableEndpoints.getResponse().getContentAsString() );
        assertTrue( nonmaskableEndpoints.getResponse().getContentAsString().contains( "http://localhost:8080/response" ) );

        // Now will try to unsubscribe Events

        List<EventMonitoringSubscription> unsubscriptions = new ArrayList<EventMonitoringSubscription>();
        EventMonitoringSubscription unsubscription = new EventMonitoringSubscription();
        unsubscription.setNotificationEndpoint( "http://localhost:8080/response" );
        unsubscription.setComponent( EventComponent.STORAGE );
        unsubscription.setNodeId( "N1" );
        unsubscription.setSubscriberId( "PRM" );
        unsubscriptions.add( unsubscription );
        String unsubscriptionsAsString = mapper.writeValueAsString( unsubscriptions );

        MvcResult unsubscriptionResult =
            this.mockMvc.perform( post( ( "http://localhost:8080/events/unsubscribe" ) ).content( unsubscriptionsAsString ).contentType( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() ).andReturn();
        assertNotNull( unsubscriptionResult.getResponse() );
        logger.info( "Response after unsubscription: " + unsubscriptionResult.getResponse().getContentAsString() );
        assertTrue( unsubscriptionResult.getResponse().getContentAsString().contains( "Operation completed successfully" ) );

    }
}
