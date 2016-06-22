/* ********************************************************************************
 * HmsLocalMonitorTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.aggregator.EventGeneratorTask;
import com.vmware.vrack.hms.aggregator.IEventAggregatorTask;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.MonitorTask;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 * Hms Local Specific Monitor Task
 * 
 * @author Yagnesh Chawda
 */
public class HmsLocalMonitorTask
    extends MonitorTask
{
    private static Logger logger = Logger.getLogger( HmsLocalMonitorTask.class );

    public HmsLocalMonitorTask()
    {
        super();
    }

    public HmsLocalMonitorTask( MonitoringTaskResponse response, ServerComponent component )
    {
        super( response, component );
    }

    public HmsLocalMonitorTask( MonitoringTaskResponse response )
    {
        super( response );
    }

    /**
     * Retrieves Sensor data using specified Provide to get Component Sensor List. HMS-local always give preference of
     * getting events data from HMS-core for each components. if HMS-core, can NOT provide hat info, in that case
     * HMS-local fallsback and tries to get the sensor info from InBandApi.
     */
    @Override
    public MonitoringTaskResponse executeTask()
        throws HmsException
    {
        try
        {
            if ( node != null && node instanceof ServerNode && component != null && component != ServerComponent.HMS )
            {
                if ( node.isNodeOperational() )
                {
                    ServerNode serverNode = (ServerNode) node;
                    // MonitoringUtil.poppulateEventsOrComponentSensors(component, response);
                    IEventAggregatorTask eventAggregatorTask = new EventGeneratorTask();
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
                    logger.error( node.getAdminStatus().getMessage( node.getNodeID() ) );
                    throw new HmsException( node.getAdminStatus().getMessage( node.getNodeID() ) );
                }
            }
            else
            {
                if ( component != ServerComponent.HMS )
                {
                    String err = "Some of mandatory items are NULL. serverNode:" + node + ", component:" + component;
                    logger.error( err );
                    throw new HmsException( err );
                }
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
