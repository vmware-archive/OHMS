/* ********************************************************************************
 * InventoryUtilTest.java
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
package com.vmware.vrack.hms.aggregator.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.TestUtil;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.FileUtil;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;

/**
 * The Class InventoryUtilTest.
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( { SpringContextHelper.class, InventoryUtil.class } )
public class InventoryUtilTest
{

    /** The node id. */
    private static String nodeId;

    /** The node map. */
    private Map<String, ServerNode> nodeMap;

    /**
     * Initialize.
     *
     * @throws HmsException the hms exception
     */
    @Before
    public void initialize()
        throws HmsException
    {
        nodeId = "N" + TestUtil.getDateTimeStamp();
        ServerNode serverNode = TestUtil.getServerNode( nodeId );
        nodeMap = TestUtil.getNodeMap( serverNode );
        InventoryLoader.getInstance().setNodeMap( nodeMap );
    }

    /**
     * Gets the OOB supported operations_ oob unavailable.
     *
     * @return the OOB supported operations_ oob unavailable
     * @throws HMSRestException the HMS rest exception
     */
    @Test( expected = HmsException.class )
    public void getOOBSupportedOperations_OOBUnavailable()
        throws HMSRestException
    {
        HashMap<String, List<HmsApi>> oobOperations =
            InventoryUtil.getOOBSupportedOperations( "/samplePath", "application/json" );
        assertNull( oobOperations );
    }

    /**
     * Gets the inventory oo b_ oob unavailable.
     *
     * @return the inventory oo b_ oob unavailable
     * @throws HMSRestException the HMS rest exception
     */
    @Test( expected = HmsException.class )
    public void getInventoryOOB_OOBUnavailable()
        throws HMSRestException
    {
        ResponseEntity<HashMap<String, Object[]>> inventory =
            InventoryUtil.getInventoryOOB( "/samplePath", "application/json" );
        assertNull( inventory );
    }

    /**
     * Test create inventory file.
     *
     * @throws HmsException the hms exception
     */
    @Test
    public void testCreateInventoryFile()
        throws HmsException
    {
        Map<String, Object[]> inventory = new HashMap<String, Object[]>();
        String nodeId = "N" + TestUtil.getDateTimeStamp();
        ServerNode serverNode = TestUtil.getServerNode( nodeId );
        List<ServerNode> serverNodes = new ArrayList<ServerNode>();
        serverNodes.add( serverNode );
        inventory.put( Constants.HOSTS, serverNodes.toArray( new ServerNode[serverNodes.size()] ) );
        String tempDir = TestUtil.getTemporaryDirectory();
        String invFileName = FilenameUtils.concat( tempDir, TestUtil.getDateTimeStamp() + ".json" );
        String invContent = TestUtil.getValueAsString( inventory );
        boolean createdOrUpdated = InventoryUtil.createOrUpdateInventoryFile( invFileName, invContent );
        assertTrue( createdOrUpdated );
        assertTrue( FileUtil.deleteDirectory( tempDir ) );
    }

    /**
     * Test update inventory file.
     *
     * @throws HmsException the hms exception
     */
    @Test
    public void testUpdateInventoryFile()
        throws HmsException
    {
        this.updateInventory( false );
    }

    /**
     * Test update inventory file create backup.
     *
     * @throws HmsException the hms exception
     */
    @Test
    public void testUpdateInventoryFileCreateBackup()
        throws HmsException
    {
        this.updateInventory( true );
    }

    /**
     * Update inventory.
     *
     * @param createBackup the create backup
     * @throws HmsException the hms exception
     */
    private void updateInventory( boolean createBackup )
        throws HmsException
    {
        Map<String, Object[]> inventory = new HashMap<String, Object[]>();
        String nodeId = "N" + TestUtil.getDateTimeStamp();
        ServerNode serverNode = TestUtil.getServerNode( nodeId );
        List<ServerNode> serverNodes = new ArrayList<ServerNode>();
        serverNodes.add( serverNode );
        inventory.put( Constants.HOSTS, serverNodes.toArray( new ServerNode[serverNodes.size()] ) );
        String tempDir = TestUtil.getTemporaryDirectory();
        String invFileName = FilenameUtils.concat( tempDir, TestUtil.getDateTimeStamp() + ".json" );
        String invContent = TestUtil.getValueAsString( inventory );
        boolean createdOrUpdated = InventoryUtil.createOrUpdateInventoryFile( invFileName, invContent );
        assertTrue( createdOrUpdated );

        String nodeId2 = "N1" + TestUtil.getDateTimeStamp();
        ServerNode serverNode2 = TestUtil.getServerNode( nodeId2 );
        serverNodes.add( serverNode2 );
        inventory.put( Constants.HOSTS, serverNodes.toArray( new ServerNode[serverNodes.size()] ) );
        invContent = TestUtil.getValueAsString( inventory );
        if ( createBackup )
        {
            createdOrUpdated = InventoryUtil.createOrUpdateInventoryFile( invFileName, invContent, true );
        }
        else
        {
            createdOrUpdated = InventoryUtil.createOrUpdateInventoryFile( invFileName, invContent );
        }
        assertTrue( createdOrUpdated );
        assertTrue( FileUtil.deleteDirectory( tempDir ) );
    }

    @Test
    public void testRemoveServerWithInvalidInventoryFile_1()
        throws HmsException, JsonGenerationException, JsonMappingException, IOException
    {
        String nodeId = "N1" + TestUtil.getDateTimeStamp();
        ServerNode serverNode = TestUtil.getServerNode( nodeId );
        this.nodeMap.put( nodeId, serverNode );
        String tempDir = TestUtil.getTemporaryDirectory();
        String invFileName = FilenameUtils.concat( tempDir, TestUtil.getDateTimeStamp() + ".json" );
        String invContent = TestUtil.getValueAsString( nodeMap );
        boolean createdOrUpdated = InventoryUtil.createOrUpdateInventoryFile( invFileName, invContent );
        assertTrue( createdOrUpdated );
        boolean removed = InventoryUtil.removeServer( invFileName, nodeId );
        assertFalse( removed );
        assertTrue( FileUtil.deleteDirectory( tempDir ) );
    }

    @Test
    public void testRemoveServerWithInvalidInventoryFile_2()
        throws HmsException, JsonGenerationException, JsonMappingException, IOException
    {
        Map<String, Object[]> inventory = new HashMap<String, Object[]>();
        String nodeId = "N" + TestUtil.getDateTimeStamp();
        ServerNode serverNode = TestUtil.getServerNode( nodeId );
        List<ServerNode> serverNodes = new ArrayList<ServerNode>();
        serverNodes.add( serverNode );
        inventory.put( Constants.SWITCHES, serverNodes.toArray( new ServerNode[serverNodes.size()] ) );
        String tempDir = TestUtil.getTemporaryDirectory();
        String invFileName = FilenameUtils.concat( tempDir, TestUtil.getDateTimeStamp() + ".json" );
        String invContent = TestUtil.getValueAsString( inventory );
        boolean createdOrUpdated = InventoryUtil.createOrUpdateInventoryFile( invFileName, invContent );
        assertTrue( createdOrUpdated );
        boolean removed = InventoryUtil.removeServer( invFileName, nodeId );
        assertFalse( removed );
        assertTrue( FileUtil.deleteDirectory( tempDir ) );
    }

    @Test
    public void testRemoveServer()
        throws HmsException, JsonGenerationException, JsonMappingException, IOException
    {
        Map<String, Object[]> inventory = new HashMap<String, Object[]>();

        // add node_1
        String nodeId = "N" + TestUtil.getDateTimeStamp();
        ServerNode serverNode = TestUtil.getServerNode( nodeId );
        List<ServerNode> serverNodes = new ArrayList<ServerNode>();
        serverNodes.add( serverNode );

        // add node_2
        String nodeId2 = "N2" + TestUtil.getDateTimeStamp();
        ServerNode serverNode2 = TestUtil.getServerNode( nodeId2 );
        serverNodes.add( serverNode2 );

        // save inventory
        inventory.put( Constants.HOSTS, serverNodes.toArray( new ServerNode[serverNodes.size()] ) );
        String tempDir = TestUtil.getTemporaryDirectory();
        String invFileName = FilenameUtils.concat( tempDir, TestUtil.getDateTimeStamp() + ".json" );
        String invContent = TestUtil.getValueAsString( inventory );
        boolean createdOrUpdated = InventoryUtil.createOrUpdateInventoryFile( invFileName, invContent );
        assertTrue( createdOrUpdated );

        // remove node_1 from inventory
        boolean removed = InventoryUtil.removeServer( invFileName, nodeId );
        assertTrue( removed );

        // Check that hosts does not contain removed node_1.
        Map<String, Object[]> invConfig = InventoryUtil.initializeInventory( invFileName );
        assertNotNull( invConfig );
        assertTrue( invConfig.containsKey( Constants.HOSTS ) );
        Object[] hosts = invConfig.get( Constants.HOSTS );
        assertNotNull( hosts );
        assertTrue( hosts.length == 1 );

        ObjectMapper objectMapper = new ObjectMapper();
        ServerNode[] serverNodes1 = objectMapper.convertValue( hosts, new TypeReference<ServerNode[]>()
        {
        } );
        assertNotNull( serverNodes1 );
        assertTrue( serverNodes1.length == 1 );
        ServerNode serverNode3 = serverNodes1[0];
        assertTrue( StringUtils.equals( nodeId2, serverNode3.getNodeID() ) );
        assertFalse( StringUtils.equals( nodeId, serverNode3.getNodeID() ) );

        assertTrue( FileUtil.deleteDirectory( tempDir ) );
    }

    @Test
    public void testRefreshInventoryOnOutOfBand()
        throws Exception
    {

        PowerMockito.mockStatic( SpringContextHelper.class );
        when( SpringContextHelper.getIbInventoryLocaiton() ).thenReturn( "dummy.json" );
        ResponseEntity<Object> response = new ResponseEntity<>( HttpStatus.OK );
        HmsOobAgentRestTemplate<?> restTemplateMock = mock( HmsOobAgentRestTemplate.class );
        whenNew( HmsOobAgentRestTemplate.class ).withAnyArguments().thenReturn( restTemplateMock );
        when( restTemplateMock.exchange( HttpMethod.PUT, Constants.HMS_OOB_INVENTORY_RELOAD,
                                         Object.class ) ).thenReturn( response );

        boolean status = InventoryUtil.refreshInventoryOnOutOfBand();
        assertTrue( status );
    }
}
