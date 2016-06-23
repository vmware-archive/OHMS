/* ********************************************************************************
 * HMSResourceMonitorTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
