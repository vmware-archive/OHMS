/* ********************************************************************************
 * InventoryUtilTest.java
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
package com.vmware.vrack.hms.aggregator.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.inventory.InventoryLoader;

@RunWith( PowerMockRunner.class )
@PrepareForTest( { InventoryUtil.class } )
@Ignore
public class InventoryUtilTest
{
    @Before
    public void initialize()
        throws HmsException
    {
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setIbIpAddress( "1.2.3.4" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );
        nodeMap.put( "N1", node );
        // populating Nodemap before we could query its peripheral info
        InventoryLoader.getInstance().setNodeMap( nodeMap );
        // Adding our test Implementation class to provide sample data.
        // InBandServiceProvider.addBoardService(node.getServiceObject(), new InbandServiceTestImpl(), true);
    }

    @Test( )
    public void isFileExist_invalidFile()
        throws Exception
    {
        boolean status = InventoryUtil.isFileExists( null );
        assertFalse( status );
    }

    @Test
    public void isFileExist_validFile()
        throws Exception
    {
        boolean status = InventoryUtil.isFileExists( "testLocation" );
        assertFalse( status );
        File fileMock = mock( File.class );
        whenNew( File.class ).withArguments( anyString() ).thenReturn( fileMock );
        when( fileMock.exists() ).thenReturn( true );
        status = InventoryUtil.isFileExists( "testLocaltion" );
        assertTrue( status );
    }

    @Test( expected = HmsException.class )
    public void getOOBSupportedOperations_OOBUnavailable()
        throws HMSRestException
    {
        HashMap<String, List<HmsApi>> oobOperations =
            InventoryUtil.getOOBSupportedOperations( "0.0.0.0", 8448, "/samplePath", "application/json" );
        assertNull( oobOperations );
    }

    @Test( expected = HmsException.class )
    public void getInventoryOOB_OOBUnavailable()
        throws HMSRestException
    {
        ResponseEntity<HashMap<String, Object[]>> inventory =
            InventoryUtil.getInventoryOOB( "0.0.0.0", 8448, "/samplePath", "application/json" );
        assertNull( inventory );
    }

    @Test
    @Ignore
    public void initializeInventory_withFileNameOnly()
        throws Exception
    {
        /*
         * Map<String, Object[]> inventory = null; try { inventory = InventoryUtil.initializeInventory(null); } catch
         * (Exception e) { assertTrue(e instanceof HmsException); assertNull(inventory); } File fileMock =
         * mock(File.class); whenNew(File.class).withArguments(anyString()).thenReturn(fileMock);
         * when(fileMock.exists()).thenReturn(true); ObjectMapper objectMapperMock = mock(ObjectMapper.class);
         * whenNew(ObjectMapper.class).withNoArguments().thenReturn(objectMapperMock);
         * when(objectMapperMock.readValue(any(File.class), any(TypeReference.class))).thenReturn(new HashMap<String,
         * Object[]>()); inventory = InventoryUtil.initializeInventory("testPath"); assertNotNull(inventory);
         */
    }

    @Test
    @Ignore
    public void initializeInventory_fromMockedOOB()
        throws Exception
    {
        /*
         * boolean status = false; try { status = InventoryUtil.initializeInventory("testInventoryPath", "0.0.0.0",
         * 8448, "/testPath"); } catch (Exception e) { assertTrue(e instanceof HmsException); assertFalse(status); }
         * File fileMock = mock(File.class); whenNew(File.class).withArguments(anyString()).thenReturn(fileMock);
         * when(fileMock.exists()).thenReturn(true); ResponseEntity<HashMap<String,Object[]>> responseEntity = new
         * ResponseEntity<HashMap<String,Object[]>>(HttpStatus.OK); mockStatic(InventoryUtil.class);
         * when(InventoryUtil.getInventoryOOB(anyString(), anyInt(), anyString(),
         * anyString())).thenReturn(responseEntity); when(InventoryUtil.initializeInventory(anyString(), anyString(),
         * anyInt(), anyString())).thenCallRealMethod(); when(InventoryUtil.writeFile(anyString(),
         * anyString())).thenReturn(true); when(InventoryUtil.initializeInventory(anyString())).thenCallRealMethod();
         * ObjectWriter objectWriter = mock(ObjectWriter.class); ObjectMapper objectMapperMock =
         * mock(ObjectMapper.class); whenNew(ObjectMapper.class).withNoArguments().thenReturn(objectMapperMock);
         * when(objectMapperMock.readValue(any(File.class), any(TypeReference.class))).thenReturn(new HashMap<String,
         * Object[]>()); when(objectMapperMock.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
         * when(objectWriter.writeValueAsString(anyString())).thenReturn("OK"); status =
         * InventoryUtil.initializeInventory("testInventoryPath", "0.0.0.0", 8448, "/testPath"); assertTrue(status);
         */
    }
}
