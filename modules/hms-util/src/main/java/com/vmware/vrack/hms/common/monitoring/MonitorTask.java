/* ********************************************************************************
 * MonitorTask.java
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
package com.vmware.vrack.hms.common.monitoring;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsOperationNotSupportedException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.util.EventsUtil;

/**
 * The Class MonitorTask.
 */
public class MonitorTask
    implements Callable<MonitoringTaskResponse>
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( MonitorTask.class );

    /**
     * The node.
     *
     * @ServerNode serverNode for which monitoring is to be done
     */
    public HmsNode node;

    /**
     * The response.
     *
     * @MonitoringTaskResponse response contains sensorProvide and references to nodes and components
     */
    public MonitoringTaskResponse response;

    /**
     * The component.
     *
     * @ServerComponent component for node HMSNode to be monitored
     */
    public ServerComponent component;

    /**
     * Instantiates a new monitor task.
     */
    public MonitorTask()
    {
        super();
    }

    /**
     * Instantiates a new monitor task.
     *
     * @param response the response
     * @param component the component
     */
    public MonitorTask( MonitoringTaskResponse response, ServerComponent component )
    {
        this.response = response;
        node = response.node;
        this.component = component;
    }

    /**
     * Instantiates a new monitor task.
     *
     * @param response the response
     */
    public MonitorTask( MonitoringTaskResponse response )
    {
        this.response = response;
        node = response.node;
        component = response.getComponentList().get( 0 );
    }

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    public MonitoringTaskResponse call()
        throws Exception
    {
        return executeTask();
    }

    /**
     * Retrieves Sensor data using specified Provide to get Component Sensor List.
     *
     * @return the monitoring task response
     * @throws HmsException the hms exception
     */
    public MonitoringTaskResponse executeTask()
        throws HmsException
    {
        String message = null;
        try
        {
            if ( node != null && component != null && node.isNodeOperational() )
            {
                if ( EventsUtil.isComponentServerApiSupported( response.getSensorInfoProvider(), component,
                                                               node.getServiceObject() ) )
                {
                    List<ServerComponentEvent> events =
                        response.getSensorInfoProvider().getComponentEventList( node.getServiceObject(), component );
                    node.addComponentSensorData( component, events );
                }
                else
                {
                    message = String.format( "Operation is not supported for Component %s of Node %s", component,
                                             node.getNodeID() );
                    logger.error( message );
                    throw new HmsOperationNotSupportedException( message );
                }
            }
        }
        catch ( Exception e )
        {
            message = String.format( "Error while getting Sensor information for Node '%s'.", node.getNodeID() );
            throw new HmsException( message, e );
        }
        return response;
    }
}