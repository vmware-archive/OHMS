/* ********************************************************************************
 * MonitorTaskSuite.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.monitoring;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.switchnodes.api.HMSSwitchNode;

/**
 * <code>MonitorTaskSuite.</code><br>
 *
 * @author VMware, Inc.
 */
public class MonitorTaskSuite
    implements Callable<MonitoringTaskResponse>
{
    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( MonitorTaskSuite.class );

    /**
     * The <code>monitorFrequency</code> field.<br>
     */
    private Long monitorFrequency = 20000L;

    /**
     * The <code>response</code> field.<br>
     */
    protected MonitoringTaskResponse response;

    /** The stop monitoring. */
    private boolean stopMonitoring = false;

    /**
     * Gets the monitor_frequency.
     *
     * @return the monitor_frequency
     */
    public Long getMonitorFrequency()
    {
        return monitorFrequency;
    }

    /**
     * Sets the monitorFrequency.
     *
     * @param monitorFrequency the new monitorFrequency
     */
    public void setMonitorFrequency( Long monitorFrequency )
    {
        this.monitorFrequency = monitorFrequency;
    }

    /**
     * Instantiates a new monitor task suite.
     *
     * @param response the response
     */
    public MonitorTaskSuite( MonitoringTaskResponse response )
    {
        super();
        this.response = response;
    }

    /**
     * Instantiates a new monitor task suite.
     *
     * @param response the response
     * @param monitorFrequency the monitorFrequency
     */
    public MonitorTaskSuite( MonitoringTaskResponse response, Long monitorFrequency )
    {
        super();
        this.response = response;
        this.monitorFrequency = monitorFrequency;
    }

    /**
     * Retrieves Sensor data using specified Provide to get Sensor info for all the components in a node.
     *
     * @return the monitoring task response
     * @throws HmsException the hms exception
     */
    public MonitoringTaskResponse executeTask()
        throws HmsException
    {
        MonitorResponseCallback updateSubscriber = new MonitorResponseCallback();
        while ( ( !Thread.currentThread().isInterrupted() ) && ( !stopMonitoring ) )
        {
            try
            {
                Thread.sleep( monitorFrequency );
                List<ServerComponent> components = response.getComponentList();
                List<SwitchComponentEnum> switchComponents = response.getSwitchComponentList();
                // Monitor Server nodes
                if ( response.getNode() instanceof ServerNode )
                {
                    if ( !response.node.isNodeOperational() )
                    {
                        continue;
                    }
                    for ( ServerComponent component : components )
                    {
                        try
                        {
                            if ( !stopMonitoring )
                            {
                                logger.debug( "Monitoring node: {};  TimeStamp: {}.", response.getHms_node_id(),
                                              new Date() );
                                MonitorTask monitor = new MonitorTask( response, component );
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
                // Monitor Switch nodes
                if ( response.getNode() instanceof HMSSwitchNode )
                {
                    for ( SwitchComponentEnum component : switchComponents )
                    {
                        try
                        {
                            if ( !stopMonitoring )
                            {
                                logger.debug( "Monitoring Switch node: {} TimeStamp : {} ", response.getHms_node_id(),
                                              new Date() );
                                MonitorSwitchTask monitor = new MonitorSwitchTask( response, component );
                                monitor.executeTask();
                            }
                        }
                        catch ( Exception e )
                        {
                            logger.error( "Error getting sensor information for switch node id: {}",
                                          response.getHms_node_id(), e );
                        }
                    }
                }
                if ( response.getNode() instanceof ServerNode )
                {
                    updateSubscriber.callbackEventSubcribers( response.node, components );
                }
                if ( response.getNode() instanceof HMSSwitchNode )
                {
                    updateSubscriber.callbackSwitchEventSubcribers( response.node, switchComponents );
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

    /**
     * Call.
     *
     * @return Returns MonitoringTaskResponse
     * @throws Exception the exception
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public MonitoringTaskResponse call()
        throws Exception
    {
        return executeTask();
    }

    /**
     * Checks if is stop monitoring.
     *
     * @return true, if is stop monitoring
     */
    public boolean isStopMonitoring()
    {
        return stopMonitoring;
    }

    /**
     * Sets the stop monitoring.
     *
     * @param stopMonitoring the new stop monitoring
     */
    public void setStopMonitoring( boolean stopMonitoring )
    {
        this.stopMonitoring = stopMonitoring;
    }
}
