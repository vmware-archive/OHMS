/* ********************************************************************************
 * HmsLocalHealthMonitorTaskSuite.java
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
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.MonitorResponseCallback;
import com.vmware.vrack.hms.common.monitoring.MonitorTaskSuite;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;

/**
 * <code>HmsLocalHealthMonitorTaskSuite</code><br>
 *
 * @author VMware, Inc.
 */
public class HmsLocalHealthMonitorTaskSuite
    extends MonitorTaskSuite
{
    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( HmsLocalHealthMonitorTaskSuite.class );

    /**
     * Instantiates a new hms local health monitor task suite.
     *
     * @param response the response
     */
    public HmsLocalHealthMonitorTaskSuite( MonitoringTaskResponse response )
    {
        super( response );
    }

    /**
     * Instantiates a new hms local health monitor task suite.
     *
     * @param response the response
     * @param frequency the frequency
     */
    public HmsLocalHealthMonitorTaskSuite( MonitoringTaskResponse response, Long frequency )
    {
        super( response, frequency );
    }

    /**
     * Execute HMS health Monitoring and the broadcast the events.
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
                Thread.sleep( this.getMonitorFrequency() );
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
                try
                {
                    logger.debug( "Monitoring HMS health: {} TimeStamp: {}.", response.getHms_node_id(), new Date() );
                    HmsLocalHealthMonitorTask monitor = new HmsLocalHealthMonitorTask( response, ServerComponent.HMS );
                    monitor.executeTask();
                }
                catch ( Exception e )
                {
                    logger.error( "Error executing HMS Health Monitor Task.", e );
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
