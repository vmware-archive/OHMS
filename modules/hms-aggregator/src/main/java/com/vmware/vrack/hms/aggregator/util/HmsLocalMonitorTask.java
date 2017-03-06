/* ********************************************************************************
 * HmsLocalMonitorTask.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.aggregator.EventGeneratorTask;
import com.vmware.vrack.hms.aggregator.IEventAggregatorTask;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.MonitorTask;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
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
    private static Logger logger = LoggerFactory.getLogger( HmsLocalMonitorTask.class );

    /**
     * Flag for the Out-Of-Band Monitoring
     */
    private boolean oobMonitoring;

    /**
     * Flag for the In-Band Monitoring
     */
    private boolean ibMonitoring;

    public HmsLocalMonitorTask()
    {
        super();
    }

    public HmsLocalMonitorTask( MonitoringTaskResponse response, ServerComponent component )
    {
        super( response, component );
    }

    public HmsLocalMonitorTask( MonitoringTaskResponse response, ServerComponent component, boolean oobMonitoring,
                                boolean ibMonitoring )
    {
        super( response, component );
        this.oobMonitoring = oobMonitoring;
        this.ibMonitoring = ibMonitoring;
    }

    public HmsLocalMonitorTask( MonitoringTaskResponse response )
    {
        super( response );
    }

    /**
     * Retrieves Sensor data using specified Provide to get Component Sensor List. HMS-aggregator always give preference
     * of getting events data from HMS-core for each components. if HMS-core, can NOT provide hat info, in that case
     * HMS-aggregator falls back and tries to get the sensor info from InBandApi.
     */
    @SuppressWarnings( "deprecation" )
    @Override
    public MonitoringTaskResponse executeTask()
        throws HmsException
    {
        try
        {
            if ( node != null && node instanceof ServerNode && component != null && component != ServerComponent.HMS )
            {

                /*
                 * Check NodeAdminStatus and Stop Monitoring, if the AdminStatus is DECOMMISSION.
                 */
                if ( node.getAdminStatus() == NodeAdminStatus.DECOMISSION )
                {
                    logger.info( "In executeTask, Node '{}' AdminStauts is '{}'. "
                        + "Events not aggregated for '{}' component of the Node.", node.getNodeID(),
                                 NodeAdminStatus.DECOMISSION, component );
                    return response;
                }

                if ( node.isNodeOperational() )
                {

                    ServerNode serverNode = (ServerNode) node;
                    // MonitoringUtil.poppulateEventsOrComponentSensors(component,
                    // response);
                    IEventAggregatorTask eventAggregatorTask = new EventGeneratorTask();

                    List<Event> events =
                        eventAggregatorTask.getAggregatedEvents( serverNode, component, oobMonitoring, ibMonitoring );
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
                    throw new HmsException( String.format( "Either not a ServerNode or not a ServerComponent."
                        + " Server Node: '%s', Component: '%s'.", node.getNodeID(), component ) );
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
