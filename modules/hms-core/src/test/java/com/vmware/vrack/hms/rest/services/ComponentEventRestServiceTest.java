/* ********************************************************************************
 * ComponentEventRestServiceTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.rest.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.ConnectorStatistics;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.vmware.vrack.common.event.Body;
import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.Header;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;
import com.vmware.vrack.hms.testplugin.BoardService_TEST;

public class ComponentEventRestServiceTest
{
    private static Logger logger = Logger.getLogger( ComponentEventRestServiceTest.class );

    private ConnectorStatistics connectorStatistics;

    private Server server;

    /**
     * Insert test node in NodeMap
     * 
     * @param node
     */
    public static void insertNodeInNodeMap( ServerNode node )
    {
        ServerNodeConnector.getInstance().nodeMap.put( "N1", node );
    }

    /**
     * Insert test board service for test node
     * 
     * @throws Exception
     */
    public static void addBoardServiceForNode()
        throws Exception
    {
        ServerNode node = (ServerNode) ServerNodeConnector.getInstance().nodeMap.get( "N1" );
        try
        {
            BoardServiceProvider.addBoardServiceClass( node.getServiceObject(), BoardService_TEST.class, true );
        }
        catch ( HmsException e )
        {
            logger.error( "Unable to add boardservice for node: " + node.getNodeID() );
        }
    }

    /**
     * Remove test node from NodeMap
     */
    public static void removeNodeFromNodeMap()
    {
        ServerNodeConnector.getInstance().nodeMap.remove( "N1" );
    }

    /**
     * Remove BoardService for test node
     */
    public static void removeBoardServiceForNode()
    {
        try
        {
            BoardServiceProvider.removeBoardServiceClass( getServerNode().getServiceObject() );
        }
        catch ( HmsException e )
        {
            logger.error( "Unable to clear boardservice for node: " + getServerNode().getNodeID() );
        }
    }

    @BeforeClass
    public static void clearNodeMapAndBoardService()
    {
        removeNodeFromNodeMap();
        removeBoardServiceForNode();
    }

    @AfterClass
    public static void cleanUp()
    {
        removeNodeFromNodeMap();
        removeBoardServiceForNode();
    }

    public static ServerNode getServerNode()
    {
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setBoardProductName( "S2600GZ" );
        node.setBoardVendor( "Intel" );
        node.setIbIpAddress( "10.28.197.28" );
        node.setManagementIp( "10.28.197.208" );
        return node;
    }

    @Test( expected = HMSRestException.class )
    public void getComponentEvents_nodeNotInNodeMap()
        throws HMSRestException
    {
        clearNodeMapAndBoardService();
        ComponentEventRestService restService = new ComponentEventRestService();
        restService.getComponnetEvents( "N1", EventComponent.CPU );
    }

    @Test( expected = HMSRestException.class )
    public void getComponentEvents_nodeInNodeMap_noBoardService()
        throws HMSRestException
    {
        insertNodeInNodeMap( getServerNode() );
        removeBoardServiceForNode();
        ComponentEventRestService restService = new ComponentEventRestService();
        restService.getComponnetEvents( "N1", EventComponent.CPU );
    }

    @Test
    public void getComponentEvents_nodeInNodeMap_boardServiceFound_CPU()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ComponentEventRestService restService = new ComponentEventRestService();
        List<Event> cpuEvents = restService.getComponnetEvents( "N1", EventComponent.CPU );
        assertNotNull( cpuEvents );
        assertTrue( cpuEvents.size() > 0 );
        assertNotNull( cpuEvents.get( 0 ) );
    }

    @Test
    public void getComponentEvents_nodeInNodeMap_boardServiceFound_HDD()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ComponentEventRestService restService = new ComponentEventRestService();
        List<Event> hddEvents = restService.getComponnetEvents( "N1", EventComponent.STORAGE );
        assertNotNull( hddEvents );
        assertTrue( hddEvents.size() > 0 );
        assertNotNull( hddEvents.get( 0 ) );
    }

    @Test
    public void getComponentNmeEvents_nodeInNodeMap_boardServiceFound_HDD()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ComponentEventRestService restService = new ComponentEventRestService();
        List<Event> hddEvents = restService.getComponnetNmeEvents( "N1" );
        assertNotNull( hddEvents );
        assertTrue( hddEvents.size() > 0 );
        assertNotNull( hddEvents.get( 0 ) );
    }

    @Test
    public void getComponentEvents_monitor_HMS_On_STARTED()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        server = initMocks( AbstractLifeCycle.STARTED );
        ComponentEventRestService restService = new ComponentEventRestService();
        List<Event> hmsEvents = restService.getComponentEvents();
        assertNotNull( hmsEvents );
        assertTrue( hmsEvents.size() > 0 );
        Event event = hmsEvents.get( 0 );
        assertNotNull( event );
        Header header = event.getHeader();
        assertEquals( "HMS", header.getAgent() );
        assertEquals( "HMS_AGENT_UP", header.getEventName().name() );
        assertEquals( "NORMAL", header.getEventType().name() );
        assertEquals( "[MANAGEMENT, SOFTWARE]", header.getEventCategoryList().toString() );
        assertEquals( "INFORMATIONAL", header.getSeverity().name() );
        // assertEquals("?", header.getComponentIdentifier());
        Body body = event.getBody();
        Map<String, String> data = body.getData();
        assertEquals( "DISCRETE", data.get( "unit" ) );
        assertEquals( "HMS_OOBAGENT_STATUS", data.get( "eventId" ) );
        assertEquals( "HMS Agent is up", data.get( "value" ) );
        // assertEquals("Hms Agent is Up", data.get("value"));
        assertEquals( "HMS_AGENT_UP", data.get( "eventName" ) );
        assertEquals( "HMS Agent is up - rack {RACK_NAME}", body.getDescription() );
        resetMocks( server );
    }

    @Test
    public void getComponentEvents_monitor_HMS_On_NOT_STARTED()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        server = initMocks( AbstractLifeCycle.FAILED );
        ComponentEventRestService restService = new ComponentEventRestService();
        List<Event> hmsEvents = restService.getComponentEvents();
        assertNotNull( hmsEvents );
        assertTrue( hmsEvents.size() > 0 );
        Event event = hmsEvents.get( 0 );
        assertNotNull( event );
        Header header = event.getHeader();
        assertEquals( "HMS", header.getAgent() );
        assertEquals( "HMS_AGENT_DOWN", header.getEventName().name() );
        assertEquals( "NORMAL", header.getEventType().name() );
        assertEquals( "[MANAGEMENT, SOFTWARE]", header.getEventCategoryList().toString() );
        assertEquals( "CRITICAL", header.getSeverity().name() );
        // assertEquals("?", header.getComponentIdentifier());
        Body body = event.getBody();
        Map<String, String> data = body.getData();
        assertEquals( "DISCRETE", data.get( "unit" ) );
        assertEquals( "HMS_OOBAGENT_STATUS", data.get( "eventId" ) );
        assertEquals( "HMS Agent is down", data.get( "value" ) );
        // assertEquals("Hms Agent is Down", data.get("value"));
        assertEquals( "HMS_AGENT_DOWN", data.get( "eventName" ) );
        assertEquals( "HMS Agent is down - rack {RACK_NAME}", body.getDescription() );
        resetMocks( server );
    }

    private Server initMocks( String status )
    {
        ServerNodeConnector serverNodeConnector = ServerNodeConnector.getInstance();
        Server server = Mockito.mock( Server.class );
        serverNodeConnector.setServer( server );
        connectorStatistics = Mockito.mock( ConnectorStatistics.class );
        Mockito.when( serverNodeConnector.getServer().getBean( ConnectorStatistics.class ) ).thenReturn( connectorStatistics );
        Mockito.when( connectorStatistics.getState() ).thenReturn( status );
        return server;
    }

    private void resetMocks( Server server )
    {
        Mockito.reset( server );
        Mockito.reset( connectorStatistics );
    }
}
