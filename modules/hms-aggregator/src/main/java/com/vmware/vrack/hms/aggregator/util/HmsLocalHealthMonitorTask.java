/* ********************************************************************************
 * HmsLocalHealthMonitorTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.aggregator.HealthMonitorEventAggregatorTask;
import com.vmware.vrack.hms.aggregator.IEventAggregatorTask;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.MonitorTask;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 * Health Monitor Task for HMS Agent
 * 
 * @author Yagnesh Chawda
 */
public class HmsLocalHealthMonitorTask
    extends MonitorTask
{
    private static Logger logger = Logger.getLogger( HmsLocalHealthMonitorTask.class );

    public HmsLocalHealthMonitorTask()
    {
        super();
    }

    public HmsLocalHealthMonitorTask( MonitoringTaskResponse response, ServerComponent component )
    {
        super( response, component );
    }

    public HmsLocalHealthMonitorTask( MonitoringTaskResponse response )
    {
        super( response );
    }

    /**
     * Retrieves HMS health event data from HMS-OOB agent
     */
    @Override
    public MonitoringTaskResponse executeTask()
        throws HmsException
    {
        try
        {
            if ( node != null && node instanceof ServerNode && component != null )
            {
                ServerNode serverNode = (ServerNode) node;
                IEventAggregatorTask eventAggregatorTask = new HealthMonitorEventAggregatorTask();
                List<Event> events = eventAggregatorTask.getAggregatedEvents( serverNode, component );
                List<Event> monitoringTaskRespEvents = response.getEvents();
                if ( monitoringTaskRespEvents == null )
                {
                    monitoringTaskRespEvents = new ArrayList<Event>();
                    response.setEvents( monitoringTaskRespEvents );
                }
                monitoringTaskRespEvents.addAll( events );
            }
            else
            {
                logger.error( "Some of mandatory items are NULL. serverNode:" + node + ", component:" + component );
                throw new HmsException( "Error while getting Sensor information for Node:" + node.getNodeID() );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while getting Sensor information for Node:" + node.getNodeID(), e );
            throw new HmsException( "Error while getting Sensor information for Node:" + node.getNodeID(), e );
        }
        return response;
    }
}
