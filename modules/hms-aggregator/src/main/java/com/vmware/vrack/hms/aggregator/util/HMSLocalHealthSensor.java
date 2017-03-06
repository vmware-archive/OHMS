/* ********************************************************************************
 * HMSLocalHealthSensor.java
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

import java.util.ArrayList;
import java.util.List;

import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.AgentHealthMonitoringUtil;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;

public class HMSLocalHealthSensor
    implements IComponentEventInfoProvider
{

    /**
     * Get ServerComponentSensor for given node and Component
     * 
     * @param seeviceNode
     * @param component
     * @return List<ServerComponentSensor>
     */
    public List<ServerComponentEvent> getComponentEventList( ServiceHmsNode serviceNode, ServerComponent component )
        throws HmsException
    {

        ArrayList<ServerComponentEvent> sensorComponnets = new ArrayList<ServerComponentEvent>();
        if ( component.equals( ServerComponent.HMS ) )
        {
            sensorComponnets.add( AgentHealthMonitoringUtil.getCPUUsage() );
            sensorComponnets.add( AgentHealthMonitoringUtil.getHMSMemoryUsage() );
            sensorComponnets.add( AgentHealthMonitoringUtil.getThreadCount() );
            /*
             * ServerComponentEvent serverCompEvent = isHMSOOBAgentRunning(); if(serverCompEvent != null) {
             * sensorComponnets.add(serverCompEvent); }
             */

            return sensorComponnets;
        }
        return null;
    }

    public List<HmsApi> getSupportedHmsApi( ServiceHmsNode serviceNode )
        throws HmsException
    {
        List<HmsApi> supportedAPI = new ArrayList<HmsApi>();

        supportedAPI.add( HmsApi.HMS_INFO );
        supportedAPI.add( HmsApi.HMS_HEALTH_INFO );
        return supportedAPI;
    }

    /**
     * Verify if OOB AGENT is responding
     */
    public static ServerComponentEvent isHMSOOBAgentRunning()
    {

        if ( !MonitoringUtil.isHMSOOBAvailable() )
        {
            ServerComponentEvent sensor = new ServerComponentEvent();

            sensor.setComponentId( "HMS_OOBAGENT_REACHABLE_STATUS" );
            sensor.setUnit( EventUnitType.DISCRETE );
            sensor.setDiscreteValue( "NOT REACHABLE" );
            sensor.setEventName( NodeEvent.HMS_AGENT_NON_RESPONSIVE );
            return sensor;
        }

        return null;

    }

}
