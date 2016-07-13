/* ********************************************************************************
 * HmsLocalSwitchMonitorTask.java
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

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.aggregator.EventGeneratorTask;
import com.vmware.vrack.hms.aggregator.IEventAggregatorTask;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.MonitorSwitchTask;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.switchnodes.api.HMSSwitchNode;

/**
 * HMS Aggregator Switch Specific Monitor Task
 *
 * @author VMware Inc.,
 */
public class HmsLocalSwitchMonitorTask
    extends MonitorSwitchTask
{
    private static Logger logger = Logger.getLogger( HmsLocalSwitchMonitorTask.class );

    public HmsLocalSwitchMonitorTask()
    {
        super();
    }

    public HmsLocalSwitchMonitorTask( MonitoringTaskResponse response, SwitchComponentEnum component )
    {
        super( response, component );
    }

    public HmsLocalSwitchMonitorTask( MonitoringTaskResponse response )
    {
        super( response );
    }

    /**
     * Retrieves Sensor data using specified Provide to get Switch Component Sensor List. HMS-local always give
     * preference of getting events data from HMS OOB agent for each components. if HMS OOB agent, can NOT provide that
     * info, in that case HMS-local falls back and tries to get the sensor info from InBandApi. But for Switch all the
     * Events data coming from the HMS OOB agent
     */
    @Override
    public MonitoringTaskResponse executeTask()
        throws HmsException
    {
        try
        {
            if ( node != null && node instanceof HMSSwitchNode && component != null )
            {
                if ( node.getNodeID() != null && node.getNodeID() != "" )
                {
                    HMSSwitchNode switchNode = (HMSSwitchNode) node;
                    IEventAggregatorTask eventAggregatorTask = new EventGeneratorTask();
                    List<Event> events =
                        eventAggregatorTask.getAggregatedSwitchEvents( switchNode.getNodeID(), component );
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
                    logger.error( "Can't get the Switch Node to Monitor" );
                    throw new HmsException( "Can't get the Switch Node to Monitor" + node.getNodeID() );
                }
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while getting Sensor information for Switch Node:" + node.getNodeID(), e );
            throw new HmsException( "Error while getting Sensor information for Switch Node:" + node.getNodeID(), e );
        }
        return response;
    }
}
