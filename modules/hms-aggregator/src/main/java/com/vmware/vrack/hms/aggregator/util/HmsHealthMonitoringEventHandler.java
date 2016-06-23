/* ********************************************************************************
 * HmsHealthMonitoringEventHandler.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 * Will start Hms health monitoring, when the HMS-IB context is completely loaded 9-4-2015 - Health Monitoring is not
 * being called as this functionality is not required.
 */
@Component
public class HmsHealthMonitoringEventHandler
    implements ApplicationListener<ContextRefreshedEvent>
{
    @Value( "${hms.monitoring.id:HMS_0}" )
    private String hmsID;

    @Override
    public void onApplicationEvent( ContextRefreshedEvent event )
    {
        ServerNode node = new ServerNode( hmsID, null, null, null );
        MonitoringUtil.startHMSHealthMonitoring( node );
    }
}
