/* ********************************************************************************
 * HmsLocalRestServiceMgmtTest.java
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpMethod;

import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.aggregator.util.HMSDebuggerComponent;
import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

@RunWith( PowerMockRunner.class )
@PrepareForTest( { HmsLocalRestServiceMgmt.class, HMSDebuggerComponent.class, MonitoringUtil.class } )
public class HmsLocalRestServiceMgmtTest
{
    private static Logger logger = Logger.getLogger( HmsLocalRestServiceMgmtTest.class );

    @InjectMocks
    HmsLocalRestServiceMgmt hmsLocalRestServiceMgmt;

    @Mock
    HMSDebuggerComponent debuggerUtil;

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

        Map<String, SwitchNode> switchNodeMap = new HashMap<String, SwitchNode>();
        SwitchNode switchNode = new SwitchNode();
        switchNode.setIpAddress( "1.2.3.4" );
        switchNode.setOsName( "cumulus" );
        switchNode.setSwitchId( "S1" );
        switchNode.setPassword( "root123" );
        switchNodeMap.put( "S1", switchNode );

        // populating Nodemap before we could query its peripheral info
        InventoryLoader.getInstance().setNodeMap( nodeMap );

        // populating SwitchNodemap before we could query its peripheral info
        InventoryLoader.getInstance().setSwitchNodeMap( switchNodeMap );

        // Adding our test Implementation class to provide sample data.
        InBandServiceProvider.addBoardService( node.getServiceObject(), new InbandServiceTestImpl(), true );
    }

    @Test
    public void performLogArchivingForSwitchTest()
        throws Exception, IllegalArgumentException
    {

        // PowerMockito.method(HMSDebuggerComponent.class, parameterTypes)
        Mockito.doReturn( "logs archive directory" ).when( debuggerUtil ).archiveHmsDebugLogs( anyString(), anyString(),
                                                                                               anyString(), anyString(),
                                                                                               anyString(), anyString(),
                                                                                               anyString(), anyInt(),
                                                                                               Matchers.any( EventComponent.class ) );

        BaseResponse responseMock = new BaseResponse();
        responseMock.setStatusCode( 200 );
        responseMock.setStatusMessage( "Hms debug logs archive will be created shortly at logs archive directory" );

        // HmsLocalRestServiceMgmt hmsLocalServerRestService = new HmsLocalRestServiceMgmt();
        // when(debuggerUtil.archiveHmsDebugLogs(anyString(), anyString(), anyString(), anyString(), anyString(),
        // anyString(), anyString(), anyInt(), Matchers.any(EventComponent.class))).thenReturn("logs archive
        // directory");
        // Call to perform archiving for the switch
        BaseResponse response = hmsLocalRestServiceMgmt.performLogArchivingForSwitch( "S1", "100", HttpMethod.PUT );

        // check if the response is returned with a status code 200
        assertNotNull( response );
        assertEquals( response.getStatusCode(), responseMock.getStatusCode() );
    }

    @Test
    public void performLogArchivingTest()
        throws Exception, IllegalArgumentException
    {

        PowerMockito.mockStatic( HMSDebuggerComponent.class );
        BaseResponse responseMock = new BaseResponse();
        responseMock.setStatusCode( 200 );
        responseMock.setStatusMessage( "Hms debug logs archive will be created shortly at logs archive directory" );

        Mockito.doReturn( "logs archive directory" ).when( debuggerUtil ).archiveHmsDebugLogs( anyString(), anyString(),
                                                                                               anyString(), anyString(),
                                                                                               anyString(), anyString(),
                                                                                               anyString(), anyInt(),
                                                                                               Matchers.any( EventComponent.class ) );

        // HmsLocalRestServiceMgmt hmsLocalServerRestService = new HmsLocalRestServiceMgmt();
        when( debuggerUtil.archiveHmsDebugLogs( anyString(), anyString(), anyString(), anyString(), anyString(),
                                                anyString(), anyString(), anyInt(),
                                                Matchers.any( EventComponent.class ) ) ).thenReturn( "logs archive directory" );
        // Call to perform archiving for the host
        BaseResponse response = hmsLocalRestServiceMgmt.performLogArchiving( "N1", "100", HttpMethod.PUT );

        assertNotNull( response );
        assertEquals( response.getStatusCode(), responseMock.getStatusCode() );
    }

}
