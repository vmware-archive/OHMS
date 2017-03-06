/* ********************************************************************************
 * HealthMonitorEventAggregatorTaskTest.java
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

import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.vmware.vrack.common.event.Body;
import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.aggregator.util.InventoryUtil;
import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.AgentHealthMonitoringUtil;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.controller.InbandServiceTestImpl;

@RunWith( PowerMockRunner.class )
@PrepareForTest( { MonitoringUtil.class, AgentHealthMonitoringUtil.class } )
@SuppressWarnings( "deprecation" )
public class HealthMonitorEventAggregatorTaskTest
{

    @Mock
    InventoryUtil inventoryUtil;

    @Test
    public void testProcessEvents()
        throws HmsException
    {

        PowerMockito.mockStatic( MonitoringUtil.class );
        PowerMockito.mockStatic( AgentHealthMonitoringUtil.class );

        List<Event> eventsLst = new ArrayList<Event>();
        Event event = new Event();
        Body body = new Body();
        Map<String, String> data = new HashMap<String, String>();
        data.put( Constants.HMS_INV_FROM_AGG_AVAILABILITY_STATUS, new String( "true" ) );
        body.setData( data );
        event.setBody( body );
        eventsLst.add( event );
        when( MonitoringUtil.getHealthMonitorEventsOOB() ).thenReturn( eventsLst );

        InbandServiceTestImpl inbandSvc = new InbandServiceTestImpl();
        List<ServerComponentEvent> eventLst = inbandSvc.getComponentEventList( null, ServerComponent.CPU );
        when( AgentHealthMonitoringUtil.getCPUUsage() ).thenReturn( eventLst.get( 0 ) );
        eventLst = inbandSvc.getComponentEventList( null, ServerComponent.MEMORY );
        when( AgentHealthMonitoringUtil.getHMSMemoryUsage() ).thenReturn( eventLst.get( 0 ) );

        ServerComponentEvent sensor = new ServerComponentEvent();
        sensor.setComponentId( "HMS_AGENT_THREAD_USAGE" );
        sensor.setUnit( EventUnitType.COUNT );
        sensor.setEventName( NodeEvent.HMS_AGENT_THREAD_COUNT );
        sensor.setValue( 1 );
        when( AgentHealthMonitoringUtil.getThreadCount() ).thenReturn( sensor );

        HealthMonitorEventAggregatorTask task = new HealthMonitorEventAggregatorTask();
        ServerNode serverNode = new ServerNode();
        List<Event> events = task.processEvents( serverNode, ServerComponent.HMS );
        Assert.assertNotNull( events );
        Event eventObj = events.get( events.size() - 1 );
        Map<String, String> map = eventObj.getBody().getData();
        String value = map.get( Constants.HMS_INV_FROM_AGG_AVAILABILITY_STATUS );
        Assert.assertEquals( "true", value );
    }
}
