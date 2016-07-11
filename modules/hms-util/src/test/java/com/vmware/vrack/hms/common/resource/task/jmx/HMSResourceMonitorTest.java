/* ********************************************************************************
 * HMSResourceMonitorTest.java
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
package com.vmware.vrack.hms.common.resource.task.jmx;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.vmware.vrack.hms.common.boardvendorservice.api.BoardServiceTest;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

public class HMSResourceMonitorTest
{
    private static Logger logger = Logger.getLogger( HMSResourceMonitorTest.class );

    @Test
    public void test()
    {
        logger.info( "Testing HMSResourceMonitorTest" );
        try
        {
            ServerNode node = new ServerNode();
            node.setManagementIp( "xx.xx.xx.xx" );
            node.setManagementUserName( "testuser" );
            node.setManagementUserPassword( "testpass" );
            IComponentEventInfoProvider boardService = new BoardServiceTest();
            MonitoringTaskResponse response = new MonitoringTaskResponse( node, ServerComponent.MEMORY, boardService );
            HMSResourceMonitor hmsResourceMonitor = new HMSResourceMonitor( response, ServerComponent.MEMORY );
            hmsResourceMonitor.call();
            assertNotNull( hmsResourceMonitor.response );
        }
        catch ( Exception e )
        {
            logger.info( "Testing HMSResourceMonitorTest Failed" );
            e.printStackTrace();
        }
    }
}
