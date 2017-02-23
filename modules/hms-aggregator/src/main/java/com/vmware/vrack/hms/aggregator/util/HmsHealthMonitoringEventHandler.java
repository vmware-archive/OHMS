/* ********************************************************************************
 * HmsHealthMonitoringEventHandler.java
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

    @Value( "${hms.health.monitor.frequency:30000}" )
    private Long frequency;

    @Override
    public void onApplicationEvent( ContextRefreshedEvent event )
    {
        ServerNode node = new ServerNode( hmsID, null, null, null );
        MonitoringUtil.startHMSHealthMonitoring( node, frequency );
    }
}
