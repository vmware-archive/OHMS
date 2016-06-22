/* ********************************************************************************
 * MonitorTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.monitoring;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsOperationNotSupportedException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.util.EventsUtil;

public class MonitorTask
    implements Callable<MonitoringTaskResponse>
{
    /**
     * @ServerNode serverNode for which monitoring is to be done
     */
    public HmsNode node;

    /**
     * @MonitoringTaskResponse response contains sensorProvide and references to nodes and components
     */
    public MonitoringTaskResponse response;

    /**
     * @ServerComponent component for node HMSNode to be monitored
     */
    public ServerComponent component;

    private static Logger logger = Logger.getLogger( MonitorTask.class );

    public MonitorTask()
    {
        super();
    }

    public MonitorTask( MonitoringTaskResponse response, ServerComponent component )
    {
        this.response = response;
        node = response.node;
        this.component = component;
    }

    public MonitorTask( MonitoringTaskResponse response )
    {
        this.response = response;
        node = response.node;
        component = response.getComponentList().get( 0 );
    }

    public MonitoringTaskResponse call()
        throws Exception
    {
        return executeTask();
    }

    /**
     * Retrieves Sensor data using specified Provide to get Component Sensor List
     */
    public MonitoringTaskResponse executeTask()
        throws HmsException
    {
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
                    String error = String.format( "Operation is not supported for Component %s of Node %s", component,
                                                  node.getNodeID() );
                    logger.error( error );
                    throw new HmsOperationNotSupportedException( error );
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
