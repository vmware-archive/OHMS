/* ********************************************************************************
 * HMSOOBHealthSensor.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.utils;

import java.util.ArrayList;
import java.util.List;

import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.AgentHealthMonitoringUtil;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;

public class HMSOOBHealthSensor
    implements IComponentEventInfoProvider
{
    public List<ServerComponentEvent> getComponentEventList( ServiceHmsNode serviceNode, ServerComponent component )
        throws HmsException
    {
        ArrayList<ServerComponentEvent> sensorComponnets = new ArrayList<ServerComponentEvent>();
        if ( component.equals( ServerComponent.HMS ) )
        {
            sensorComponnets.add( AgentHealthMonitoringUtil.getCPUUsage() );
            sensorComponnets.add( AgentHealthMonitoringUtil.getHMSMemoryUsage() );
            sensorComponnets.add( AgentHealthMonitoringUtil.getThreadCount() );
            sensorComponnets.add( JettyMonitorUtil.getServerStartedDuration() );
            sensorComponnets.add( JettyMonitorUtil.getOutgoingMessagesCount() );
            sensorComponnets.add( JettyMonitorUtil.getIncomingMessagesCount() );
            sensorComponnets.add( JettyMonitorUtil.getServerMeanResponseTime() );
            sensorComponnets.add( JettyMonitorUtil.getServerState() );
            return sensorComponnets;
        }
        // TODO Auto-generated method stub
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
}
