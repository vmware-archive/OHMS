/* ********************************************************************************
 * HmsInventoryTest.java
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
package com.vmware.vrack.hms.common.configuration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.util.HmsGenericUtil;

/**
 * The Class HmsInventoryTest.
 */
public class HmsInventoryTest
{

    /** The props. */
    private Properties props = null;

    /** The hms ib inventory location property. */
    private final String HMS_IB_INVENTORY_LOCATION_PROPERTY = "hms.ib.inventory.location";

    /** The property file. */
    private final String PROPERTY_FILE = "src/test/resources/test-config.properties";

    /**
     * Sets the up before class.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setUpBeforeClass()
        throws Exception
    {
    }

    /**
     * Tear down after class.
     *
     * @throws Exception the exception
     */
    @AfterClass
    public static void tearDownAfterClass()
        throws Exception
    {
    }

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp()
        throws Exception
    {
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown()
        throws Exception
    {
    }

    /**
     * Instantiates a new hms inventory test.
     *
     * @throws FileNotFoundException the file not found exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public HmsInventoryTest()
        throws FileNotFoundException, IOException
    {
        props = new Properties();
        props.load( new FileInputStream( PROPERTY_FILE ) );
        assertTrue( props.keySet().size() > 0 );
    }

    /**
     * Test load inventory.
     */
    @Test
    public void testLoadInventory()
    {
        HmsInventory hmsInventory = HmsInventory.getInstance();
        assertTrue( hmsInventory.loadInventory( props.getProperty( HMS_IB_INVENTORY_LOCATION_PROPERTY ) ) );
    }

    /**
     * Test add host.
     */
    @Test
    public void testAddHost()
    {
        HmsInventory hmsInventory = HmsInventory.getInstance();
        assertTrue( hmsInventory.loadInventory( props.getProperty( HMS_IB_INVENTORY_LOCATION_PROPERTY ) ) );

        final String nodeId = this.getNodeId( this.getDateTimeInMillis() );

        // add server
        assertTrue( hmsInventory.addHost( getServerNode( nodeId ) ) );

        List<ServerNode> hosts = hmsInventory.getHosts();
        assertNotNull( hosts );
        List<String> nodeIds = this.getAllNodeIds( hosts );
        assertTrue( nodeIds.contains( nodeId ) );
    }

    /**
     * Test remove server.
     */
    @Test
    public void testRemoveServer()
    {
        HmsInventory hmsInventory = HmsInventory.getInstance();
        assertTrue( hmsInventory.loadInventory( props.getProperty( HMS_IB_INVENTORY_LOCATION_PROPERTY ) ) );

        final String nodeId = this.getNodeId( this.getDateTimeInMillis() );

        // add server
        assertTrue( hmsInventory.addHost( getServerNode( nodeId ) ) );

        // validate that the new server is part of the inventory
        List<ServerNode> hosts = hmsInventory.getHosts();
        assertNotNull( hosts );
        assertTrue( hosts.size() > 0 );

        List<String> nodeIds = this.getAllNodeIds( hosts );
        assertTrue( nodeIds.contains( nodeId ) );

        // remove server
        assertTrue( hmsInventory.removeHost( nodeId ) );
        hosts = hmsInventory.getHosts();
        assertNotNull( hosts );

        nodeIds = this.getAllNodeIds( hosts );
        assertFalse( nodeIds.contains( nodeId ) );
    }

    @Test
    @Ignore
    public void testSaveInventory()
    {
        HmsInventory hmsInventory = HmsInventory.getInstance();
        assertTrue( hmsInventory.loadInventory( props.getProperty( HMS_IB_INVENTORY_LOCATION_PROPERTY ) ) );

        final String nodeId = this.getNodeId( this.getDateTimeInMillis() );

        // add server
        assertTrue( hmsInventory.addHost( getServerNode( nodeId ) ) );
        assertTrue( hmsInventory.saveInventory( props.getProperty( HMS_IB_INVENTORY_LOCATION_PROPERTY ), false ) );
        HmsGenericUtil.deleteLatestInventoryBackup();
    }

    /**
     * Gets the all node ids.
     *
     * @param hosts the hosts
     * @return the all node ids
     */
    private List<String> getAllNodeIds( List<ServerNode> hosts )
    {

        List<String> nodeIds = new ArrayList<String>();
        if ( hosts != null )
        {
            for ( ServerNode serverNode : hosts )
            {
                nodeIds.add( serverNode.getNodeID() );
            }
        }
        return nodeIds;
    }

    /**
     * Gets the server node.
     *
     * @param nodeId the node id
     * @return the server node
     */
    private ServerNode getServerNode( final String nodeId )
    {
        ServerNode serverNode = new ServerNode();
        serverNode.setNodeID( nodeId );
        return serverNode;
    }

    /**
     * Gets the node id.
     *
     * @param dateTimeInMillis the date time in millis
     * @return the node id
     */
    private String getNodeId( final String dateTimeInMillis )
    {
        return String.format( "N%s", dateTimeInMillis );
    }

    /**
     * Gets the date time in millis.
     *
     * @return the date time in millis
     */
    private String getDateTimeInMillis()
    {
        Calendar cal = Calendar.getInstance();
        return Long.toString( cal.getTimeInMillis() );
    }
}
