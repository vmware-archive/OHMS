/* ********************************************************************************
 * HmsLocalMonitorTaskSuite.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.MonitorResponseCallback;
import com.vmware.vrack.hms.common.monitoring.MonitorTaskSuite;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.switchnodes.api.HMSSwitchNode;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

/**
 * <code>HmsLocalMonitorTaskSuite</code><br>
 *
 * @author VMware, Inc.
 */
public class HmsLocalMonitorTaskSuite
    extends MonitorTaskSuite
{
    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( HmsLocalMonitorTaskSuite.class );

    /**
     * Instantiates a new hms local monitor task suite.
     *
     * @param response the response
     */
    public HmsLocalMonitorTaskSuite( MonitoringTaskResponse response )
    {
        super( response );
    }

    /**
     * Instantiates a new hms local monitor task suite.
     *
     * @param response the response
     * @param frequency the frequency
     */
    public HmsLocalMonitorTaskSuite( MonitoringTaskResponse response, Long frequency )
    {
        super( response, frequency );
    }

    /**
     * Retrieves Hms health events from HMS-OOB at regular intervals and broadcasts them to subscribers.
     *
     * @return the monitoring task response
     * @throws HmsException the hms exception
     */
    @Override
    public MonitoringTaskResponse executeTask()
        throws HmsException
    {
        MonitorResponseCallback updateSubscriber = new MonitorResponseCallback();
        while ( ( !Thread.currentThread().isInterrupted() ) && ( !this.isStopMonitoring() ) )
        {
            try
            {
                Thread.sleep( getMonitorFrequency() );
                List<Event> events = response.getEvents();
                if ( events != null )
                {
                    events.clear();
                }
                else
                {
                    events = new ArrayList<Event>();
                    response.setEvents( events );
                }
                List<ServerComponent> components = response.getComponentList();
                List<SwitchComponentEnum> switchComponents = response.getSwitchComponentList();
                /*
                 * Get InBandServiceImpl/SensorInforProvider for current node. We are getting this every time from
                 * InBandServiceProvider, because it can change in the runtime.
                 */
                IComponentEventInfoProvider sensorInfoProvider = null;
                if ( response.getNode() instanceof ServerNode )
                {
                    sensorInfoProvider = InBandServiceProvider.getBoardService( response.getNode().getServiceObject() );
                    // can not monitor In-band if node is not operational
                    // OOB can still be monitored
                    if ( sensorInfoProvider != null && response.getNode().isNodeOperational() )
                    {
                        response.setSensorInfoProvider( sensorInfoProvider );
                    }
                }
                // Monitor Server Events
                if ( response.getNode() instanceof ServerNode )
                {
                    for ( ServerComponent component : components )
                    {
                        try
                        {
                            logger.debug( "Monitoring node: {} TimeStamp: {}.", response.getHms_node_id(), new Date() );
                            if ( !this.isStopMonitoring() )
                            {
                                HmsLocalMonitorTask monitor = new HmsLocalMonitorTask( response, component );
                                monitor.executeTask();
                            }
                        }
                        catch ( Exception e )
                        {
                            logger.error( "Error getting sensor information for node id: {}.",
                                          response.getHms_node_id(), e );
                        }
                    }
                }
                // Monitor Switch Events
                if ( response.getNode() instanceof HMSSwitchNode )
                {
                    for ( SwitchComponentEnum component : switchComponents )
                    {
                        try
                        {
                            logger.debug( "Monitoring Switch node: " + response.getHms_node_id() + " TimeStamp : "
                                + new Date() );
                            if ( !this.isStopMonitoring() )
                            {
                                HmsLocalSwitchMonitorTask hmsLocalSwitchMonitorTask =
                                    new HmsLocalSwitchMonitorTask( response, component );
                                hmsLocalSwitchMonitorTask.executeTask();
                            }
                        }
                        catch ( Exception e )
                        {
                            logger.error( "Error getting sensor information for switch node id: {}",
                                          response.getHms_node_id(), e );
                        }
                    }
                }
                try
                {
                    if ( response.getEvents() != null )
                    {
                        updateSubscriber.callbackEventSubcribersUsingEvents( response.getNode(), response.getEvents() );
                    }
                }
                catch ( Exception e )
                {
                    logger.error( "Error Broadcasting Events from Component Sensor for node id: {}.",
                                  response.getHms_node_id(), e );
                }
            }
            catch ( Exception e )
            {
                logger.error( "Error getting sensor information for node id: {}.", response.getHms_node_id(), e );
                throw new HmsException( "Error Initiating monitoring for HMS Node: " + response.getHms_node_id(), e );
            }
        }
        return response;
    }
}
