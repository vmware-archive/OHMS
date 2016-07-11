/* ********************************************************************************
 * AgentHealthMonitoringUtilTest.java
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
package com.vmware.vrack.hms.common.monitoring;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.vmware.vrack.hms.common.monitoring.AgentHealthMonitoringUtil;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;

public class AgentHealthMonitoringUtilTest
{
    private static Logger logger = Logger.getLogger( AgentHealthMonitoringUtilTest.class );

    @Test
    public void test()
    {
        logger.info( "Test hmsutil AgentHealthMonitoringUtilTest" );
        try
        {
            List<ServerComponentEvent> serverComponentEvent = new ArrayList<>();
            serverComponentEvent.add( AgentHealthMonitoringUtil.getCPUUsage() );
            serverComponentEvent.add( AgentHealthMonitoringUtil.getHMSMemoryUsage() );
            serverComponentEvent.add( AgentHealthMonitoringUtil.getNetworkStatus( "vmnic0" ) );
            serverComponentEvent.add( AgentHealthMonitoringUtil.getThreadCount() );
            serverComponentEvent.get( 0 ).setValue( 10 );
            serverComponentEvent.get( 1 ).setValue( 120 );
            serverComponentEvent.get( 2 ).setDiscreteValue( "AVAILABLE" );
            serverComponentEvent.get( 3 ).setValue( 5 );
            assertNotNull( serverComponentEvent );
            for ( int i = 0; i < serverComponentEvent.size(); i++ )
            {
                assertNotNull( serverComponentEvent.get( i ).getComponentId() );
                assertNotNull( serverComponentEvent.get( i ).getUnit() );
                assertNotNull( serverComponentEvent.get( i ).getValue() );
            }
            assertNotNull( serverComponentEvent.get( 0 ).getEventName() );
            assertNotNull( serverComponentEvent.get( 1 ).getEventName() );
            assertNull( serverComponentEvent.get( 2 ).getEventName() );
            assertNotNull( serverComponentEvent.get( 3 ).getEventName() );
            assertEquals( "AVAILABLE", serverComponentEvent.get( 2 ).getDiscreteValue() );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsuitl AgentHealthMonitoringUtilTest Failed!" );
            e.printStackTrace();
        }
    }
}
