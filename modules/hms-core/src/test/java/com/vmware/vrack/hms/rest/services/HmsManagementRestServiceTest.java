/* ********************************************************************************
 * HmsManagementRestServiceTest.java
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
package com.vmware.vrack.hms.rest.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.Response;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.ZipUtil;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;
import com.vmware.vrack.hms.node.switches.SwitchNodeConnector;
import com.vmware.vrack.hms.utils.OobUtil;

/**
 * Test class for {@link HMSManagementRestService}
 *
 * @author spolepalli
 */

@RunWith( PowerMockRunner.class )
@FixMethodOrder( MethodSorters.NAME_ASCENDING )
@PrepareForTest( { HMSManagementRestService.class, ZipUtil.class, HmsConfigHolder.class, OobUtil.class } )
public class HmsManagementRestServiceTest
{

    @Test
    public void testGetHMSNodes()
        throws HMSRestException
    {

        ServerNodeConnector.getInstance().setNodeMap( new ConcurrentHashMap<String, HmsNode>() );
        ComponentEventRestServiceTest.insertNodeInNodeMap( ComponentEventRestServiceTest.getServerNode() );

        SwitchNode switchNode = new SwitchNode( "Arista", "SSH", "10.28.197.248", 22, "lanier", "l@ni3r2o14" );
        ConcurrentHashMap<String, SwitchNode> switchMap = new ConcurrentHashMap<String, SwitchNode>();
        switchMap.put( "S1", switchNode );
        SwitchNodeConnector.getInstance().switchNodeMap = switchMap;

        HMSManagementRestService service = new HMSManagementRestService();
        Map<String, Object[]> hmsNodes = service.getHMSNodes();

        // Test for masked objects;
        Object[] serverNodes = hmsNodes.get( Constants.HOSTS );
        for ( Object serverNode : serverNodes )
        {
            ServerNode servNode = (ServerNode) serverNode;
            assertEquals( servNode.getNodeID(), "N1" );
            assertEquals( servNode.getBoardProductName(), "S2600GZ" );
            assertEquals( servNode.getBoardVendor(), "Intel" );
            assertEquals( servNode.getIbIpAddress(), "10.28.197.28" );
            assertEquals( servNode.getManagementIp(), "10.28.197.208" );
            assertEquals( servNode.getOsUserName(), "testuser" );
            assertEquals( servNode.getOsPassword(), "****" );
        }

        Object[] switches = hmsNodes.get( Constants.SWITCHES );
        for ( Object obj : switches )
        {
            SwitchNode sNode = (SwitchNode) obj;
            assertEquals( sNode.getSwitchId(), "Arista" );
            assertEquals( sNode.getProtocol(), "SSH" );
            assertEquals( sNode.getPort(), new Integer( 22 ) );
            assertEquals( sNode.getUsername(), "lanier" );
            assertEquals( sNode.getPassword(), "****" );
        }

        // Test for original objects
        Map<String, HmsNode> nodeMap = ServerNodeConnector.getInstance().getNodeMap();
        ServerNode servNode = (ServerNode) nodeMap.get( "N1" );
        assertEquals( servNode.getNodeID(), "N1" );
        assertEquals( servNode.getBoardProductName(), "S2600GZ" );
        assertEquals( servNode.getBoardVendor(), "Intel" );
        assertEquals( servNode.getIbIpAddress(), "10.28.197.28" );
        assertEquals( servNode.getManagementIp(), "10.28.197.208" );
        assertEquals( servNode.getOsUserName(), "testuser" );
        assertEquals( servNode.getOsPassword(), "ospassword" );

        Map<String, SwitchNode> switchNodeMap = SwitchNodeConnector.getInstance().switchNodeMap;
        SwitchNode sNode = (SwitchNode) switchNodeMap.get( "S1" );
        assertEquals( sNode.getSwitchId(), "Arista" );
        assertEquals( sNode.getProtocol(), "SSH" );
        assertEquals( sNode.getPort(), new Integer( 22 ) );
        assertEquals( sNode.getUsername(), "lanier" );
        assertEquals( sNode.getPassword(), "l@ni3r2o14" );
    }

    @Test
    public void getHMSLogsTest_fileNotExists()
        throws Exception
    {
        File fileMock = mock( File.class );
        whenNew( File.class ).withArguments( anyString() ).thenReturn( fileMock );
        when( fileMock.canWrite() ).thenReturn( true );
        when( fileMock.getParentFile() ).thenReturn( fileMock );
        when( fileMock.getParentFile().exists() ).thenReturn( true );
        when( fileMock.getParentFile().mkdirs() ).thenReturn( true );
        when( fileMock.createNewFile() ).thenReturn( true );
        PowerMockito.mockStatic( ZipUtil.class );
        when( ZipUtil.zipFiles( any( String.class ), any( String.class ) ) ).thenReturn( true );
        HMSManagementRestService service = new HMSManagementRestService();

        Response response = service.getHmsLogs();
        assertNotNull( response );
        assertEquals( response.getStatus(), 404 );
    }

    @Test
    @Ignore
    public void getHMSLogsTest()
        throws Exception
    {
        HMSManagementRestService service = new HMSManagementRestService();
        PowerMockito.mockStatic( ZipUtil.class );
        when( ZipUtil.zipFiles( any( String.class ), any( String.class ) ) ).thenReturn( true );
        // HmsConfigHolder.initializeHmsAppProperties();
        Response response = service.getHmsLogs();
        assertNotNull( response );
        assertEquals( response.getStatus(), 200 );
    }

    @Test
    public void testHandshakeOnSuccessCase()
        throws Exception
    {
        PowerMockito.mockStatic( HmsConfigHolder.class );
        when( HmsConfigHolder.getProperty( any( String.class ), any( String.class ) ) ).thenReturn( "false" );

        PowerMockito.mockStatic( OobUtil.class );
        when( OobUtil.getProperty( any( String.class ), any( String.class ),
                                   any( String.class ) ) ).thenReturn( "192.168.100.40" ).thenReturn( "192.168.100.108" );

        HMSManagementRestService service = new HMSManagementRestService();
        BaseResponse response = service.handshake( "192.168.100.108", "VRM" );

        assertNotNull( response );
        assertEquals( response.getStatusCode().intValue(), 202 );
    }

    @Test( expected = HMSRestException.class )
    public void testHandshakeOnInvalidInput()
        throws Exception
    {
        HMSManagementRestService service = new HMSManagementRestService();
        service.handshake( "", "VRM" );
    }
}
