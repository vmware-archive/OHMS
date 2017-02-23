/* ********************************************************************************
 * HostDataAggregatorTest.java
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
package com.vmware.vrack.hms.aggregator;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;

import com.vmware.vrack.hms.aggregator.util.AggregatorUtil;
import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.boardservice.ib.api.ESXIInfoHelper;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.esxinfo.HostNameInfo;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.inventory.InventoryLoader;

@RunWith( PowerMockRunner.class )
@PrepareForTest( { MonitoringUtil.class, AggregatorUtil.class, ESXIInfoHelper.class } )
public class HostDataAggregatorTest
{

    @Mock
    ApplicationContext context;

    String nodeId = "N0";

    @Test
    public void testGetServerInfoOnBmcIsReachable()
        throws HmsException, IllegalArgumentException, IllegalAccessException
    {
        HostDataAggregator agg = new HostDataAggregator();

        PowerMockito.mockStatic( MonitoringUtil.class );
        PowerMockito.mockStatic( AggregatorUtil.class );

        String path = Constants.HMS_OOB_HOST_INFO_ENDPOINT;
        path = path.replace( "{host_id}", nodeId );

        ServerNode serverNode = new ServerNode();
        serverNode.setPowered( true );
        serverNode.setDiscoverable( true );

        populateResourcesForNode( serverNode );

        when( MonitoringUtil.getServerNodeOOB( path ) ).thenReturn( serverNode );
        when( AggregatorUtil.isComponentAvilableOOB( any( ServerNode.class ),
                                                     any( ServerComponent.class ) ) ).thenReturn( true );
        MemberModifier.field( HostDataAggregator.class, "context" ).set( agg, context );

        ServerInfo serverInfo = agg.getServerInfo( nodeId );
        Assert.assertEquals( FruOperationalStatus.Operational, serverInfo.getOperationalStatus() );
        Assert.assertEquals( 1, serverInfo.getCpuInfo().size() );
    }

    @Test
    public void testGetServerInfoOnBmcIsNotReachableButEsxiIs()
        throws Exception
    {
        HostDataAggregator agg = new HostDataAggregator();

        PowerMockito.mockStatic( MonitoringUtil.class );
        PowerMockito.mockStatic( ESXIInfoHelper.class );
        PowerMockito.spy( AggregatorUtil.class );

        String path = Constants.HMS_OOB_HOST_INFO_ENDPOINT;
        path = path.replace( "{host_id}", nodeId );

        ServerNode serverNode = new ServerNode();
        serverNode.setPowered( true );
        serverNode.setDiscoverable( false );
        serverNode.setNodeID( nodeId );

        populateResourcesForNode( serverNode );
        when( MonitoringUtil.getServerNodeOOB( path ) ).thenReturn( serverNode );
        PowerMockito.doReturn( true ).when( AggregatorUtil.class, "isComponentAvilableOOB", any( ServerNode.class ),
                                            any( ServerComponent.class ) );
        PowerMockito.doReturn( true ).when( AggregatorUtil.class, "isEsxiHostReachable", any( ServerNode.class ),
                                            anyInt(), anyInt() );

        HostNameInfo hostNameInfo = new HostNameInfo();
        hostNameInfo.setHostName( "testName" );
        when( ESXIInfoHelper.getEsxiHostNameInfo( any( ServiceServerNode.class ) ) ).thenReturn( hostNameInfo );

        MemberModifier.field( HostDataAggregator.class, "context" ).set( agg, context );

        InventoryLoader inventoryLoader = InventoryLoader.getInstance();
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        nodeMap.put( nodeId, serverNode );
        inventoryLoader.setNodeMap( nodeMap );

        ServerInfo serverInfo = agg.getServerInfo( nodeId );
        Assert.assertEquals( FruOperationalStatus.Operational, serverInfo.getOperationalStatus() );
        Assert.assertEquals( 1, serverInfo.getCpuInfo().size() );
    }

    @Test
    public void testGetServerInfoOnBmcAndEsxiAreNotReachable()
        throws Exception
    {
        HostDataAggregator agg = new HostDataAggregator();

        PowerMockito.mockStatic( MonitoringUtil.class );
        PowerMockito.mockStatic( ESXIInfoHelper.class );
        PowerMockito.spy( AggregatorUtil.class );

        String path = Constants.HMS_OOB_HOST_INFO_ENDPOINT;
        path = path.replace( "{host_id}", nodeId );

        ServerNode serverNode = new ServerNode();
        serverNode.setPowered( true );
        serverNode.setDiscoverable( false );
        serverNode.setNodeID( nodeId );

        when( MonitoringUtil.getServerNodeOOB( path ) ).thenReturn( serverNode );
        PowerMockito.doReturn( true ).when( AggregatorUtil.class, "isComponentAvilableOOB", any( ServerNode.class ),
                                            any( ServerComponent.class ) );
        PowerMockito.doReturn( false ).when( AggregatorUtil.class, "isEsxiHostReachable", any( ServerNode.class ),
                                             anyInt(), anyInt() );

        HostNameInfo hostNameInfo = new HostNameInfo();
        when( ESXIInfoHelper.getEsxiHostNameInfo( any( ServiceServerNode.class ) ) ).thenReturn( hostNameInfo );

        MemberModifier.field( HostDataAggregator.class, "context" ).set( agg, context );

        InventoryLoader inventoryLoader = InventoryLoader.getInstance();
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        nodeMap.put( nodeId, serverNode );
        inventoryLoader.setNodeMap( nodeMap );

        ServerInfo serverInfo = agg.getServerInfo( nodeId );
        Assert.assertEquals( FruOperationalStatus.NonOperational, serverInfo.getOperationalStatus() );
        Assert.assertEquals( 0, serverInfo.getCpuInfo().size() );
    }

    private void populateResourcesForNode( ServerNode serverNode )
    {
        ArrayList<CPUInfo> arrayList = new ArrayList<CPUInfo>();
        CPUInfo cpuInfo = new CPUInfo();
        cpuInfo.setId( "1" );
        cpuInfo.setMaxClockFrequency( 100l );
        cpuInfo.setTotalCpuCores( 2 );
        arrayList.add( cpuInfo );
        serverNode.setCpuInfo( arrayList );
    }

}
