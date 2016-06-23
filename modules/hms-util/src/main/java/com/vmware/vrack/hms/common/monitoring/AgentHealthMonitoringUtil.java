/* ********************************************************************************
 * AgentHealthMonitoringUtil.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.monitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.util.NetworkInterfaceUtil;

public class AgentHealthMonitoringUtil
{
    private static OperatingSystemMXBean osMBean;

    private static ThreadMXBean threadMBean;

    private static Logger logger = Logger.getLogger( AgentHealthMonitoringUtil.class );

    public static ServerComponentEvent getHMSMemoryUsage()
    {
        try
        {
            long MegaBytes = 1024L * 1024L;
            MemoryUsage mu = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            long memoryUsage = mu.getCommitted() / MegaBytes;
            ServerComponentEvent sensor = new ServerComponentEvent();
            sensor.setComponentId( "HMS_AGENT_MEMORY_USAGE" );
            sensor.setUnit( EventUnitType.MEGABYTES );
            sensor.setEventName( NodeEvent.HMS_AGENT_MEMORY_STATUS );
            sensor.setValue( memoryUsage );
            return sensor;
        }
        catch ( Exception e )
        {
            logger.debug( "Error getting Memory Usage", e );
        }
        return null;
    }

    public static ServerComponentEvent getCPUUsage()
    {
        try
        {
            osMBean = ManagementFactory.newPlatformMXBeanProxy( ManagementFactory.getPlatformMBeanServer(),
                                                                ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME,
                                                                OperatingSystemMXBean.class );
            double cpuUsage = osMBean.getSystemLoadAverage();
            ServerComponentEvent sensor = new ServerComponentEvent();
            sensor.setComponentId( "HMS_AGENT_CPU_USAGE" );
            sensor.setUnit( EventUnitType.PERCENT );
            sensor.setEventName( NodeEvent.HMS_AGENT_CPU_STATUS );
            sensor.setValue( (float) cpuUsage * 100 );
            return sensor;
        }
        catch ( Exception e )
        {
            logger.debug( "Error getting CPU Usage", e );
        }
        return null;
    }

    public static ServerComponentEvent getThreadCount()
    {
        try
        {
            threadMBean =
                ManagementFactory.newPlatformMXBeanProxy( ManagementFactory.getPlatformMBeanServer(),
                                                          ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class );
            ServerComponentEvent sensor = new ServerComponentEvent();
            sensor.setComponentId( "HMS_AGENT_THREAD_USAGE" );
            sensor.setUnit( EventUnitType.COUNT );
            sensor.setEventName( NodeEvent.HMS_AGENT_THREAD_COUNT );
            sensor.setValue( threadMBean.getThreadCount() );
            return sensor;
        }
        catch ( Exception e )
        {
            logger.debug( "Error getting Thread Count", e );
        }
        return null;
    }

    public static ServerComponentEvent getNetworkStatus( String networkInterface )
    {
        ServerComponentEvent sensor = new ServerComponentEvent();
        sensor.setComponentId( "HMS_AGENT_NETWORK_STATUS" );
        sensor.setUnit( EventUnitType.DISCRETE );
        if ( NetworkInterfaceUtil.isNetworkInterfaceUp( networkInterface ) )
            sensor.setDiscreteValue( "AVAILABLE" );
        else
            sensor.setDiscreteValue( "NOT AVAILABLE" );
        sensor.setEventName( null );
        return sensor;
    }
}
