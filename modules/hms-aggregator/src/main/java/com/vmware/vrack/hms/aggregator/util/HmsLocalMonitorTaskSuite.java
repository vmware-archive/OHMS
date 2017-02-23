/* ********************************************************************************
 * HmsLocalMonitorTaskSuite.java
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
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.Header;
import com.vmware.vrack.common.event.enums.EventCatalog;
import com.vmware.vrack.hms.aggregator.EventGeneratorTask;
import com.vmware.vrack.hms.aggregator.HostDataAggregator;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.MonitorResponseCallback;
import com.vmware.vrack.hms.common.monitoring.MonitorTaskSuite;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodePowerStatus;
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

            /*
             * Check Server's AdminStatus. If the AdminStatus is DECOMMISSIONED, then stop monitoring for the server
             */
            if ( response.getNode().getAdminStatus() == NodeAdminStatus.DECOMISSION )
            {
                logger.info( "In executeTask, Server '{}' AdminStatus is '{}'. Stopping monitoring for the server.",
                             response.getNode().getNodeID(), NodeAdminStatus.DECOMISSION.toString() );
                boolean serverRemoved = MonitoringUtil.getMonitoredNodes().remove( response.getNode().getNodeID() );
                if ( serverRemoved )
                {
                    logger.debug( "In executeTask, removed Server '{}' from Monitored Nodes of MonitoringUtil." );
                }
                else
                {
                    logger.warn( "In executeTask, failed to remove Server '{}' from Monitored Nodes of "
                        + "MonitoringUtil." );
                }
                break;
            }

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
                    boolean oobMonitoring = false;
                    boolean ibMonitoring = false;
                    ServerNode serverNode = (ServerNode) response.getNode();
                    ServerNodePowerStatus serverNodePowerStatus = new ServerNodePowerStatus();
                    for ( ServerComponent component : components )
                    {
                        logger.debug( "Monitoring node: {} TimeStamp: {} Server Component: {}.",
                                      response.getHms_node_id(), new Date(), component );
                        switch ( component )
                        {
                            case BMC:
                                // Make sure that Node OOB availability before making the OOB event monitoring
                                try
                                {
                                    if ( !this.isStopMonitoring() )
                                    {

                                        HmsLocalMonitorTask monitor =
                                            new HmsLocalMonitorTask( response, component, true, ibMonitoring );
                                        monitor.executeTask();
                                    }
                                }
                                catch ( Exception e )
                                {
                                    logger.error( "Error getting sensor information for node id: {} and Server "
                                        + "component: {}.", response.getHms_node_id(), component, e );
                                }
                                // If the BMC events size is zero, which means
                                // No Error in OOB communication, continue to
                                // monitor the OOB server component events
                                if ( response.getEvents() != null && response.getEvents().size() == 0 )
                                {
                                    oobMonitoring = true;
                                }
                                // Check if the host or node is power off or on before we start the IB event
                                // monitoring...
                                try
                                {
                                    if ( oobMonitoring )
                                    {
                                        HostDataAggregator aggregator = new HostDataAggregator();
                                        serverNodePowerStatus =
                                            aggregator.getServerNodePowerStatus( serverNode.getNodeID() );
                                    }
                                }
                                catch ( Exception e )
                                {
                                    logger.error( "Error getting Server Node Power Status using OOB for node: {} ",
                                                  serverNode.getNodeID(), e );
                                }
                                if ( serverNodePowerStatus.isPowered() && serverNodePowerStatus.isDiscoverable() )
                                {
                                    ibMonitoring = true;
                                }
                                break;
                            default:
                                try
                                {
                                    if ( !this.isStopMonitoring() )
                                    {
                                        HmsLocalMonitorTask monitor =
                                            new HmsLocalMonitorTask( response, component, oobMonitoring, ibMonitoring );
                                        monitor.executeTask();
                                    }
                                }
                                catch ( Exception e )
                                {
                                    logger.error( "Error getting sensor information for node id: {} and Server "
                                        + "component: {}.", response.getHms_node_id(), component, e );
                                }
                                break;
                        }
                    }
                }

                // Monitor Switch Events
                if ( response.getNode() instanceof HMSSwitchNode )
                {
                    boolean switchMonitoring = false;
                    HMSSwitchNode switchNode = (HMSSwitchNode) response.getNode();
                    for ( SwitchComponentEnum component : switchComponents )
                    {
                        // Always SWITCH component case will execute first to enable or disable switchMonitoring flag
                        switch ( component )
                        {
                            case SWITCH:
                                EventGeneratorTask eventGenerator = new EventGeneratorTask();
                                List<Event> switchEvents =
                                    eventGenerator.getAggregatedSwitchEvents( switchNode.getNodeID(), component );

                                if ( switchEvents != null && switchEvents.size() > 0 )
                                {
                                    for ( Event event : switchEvents )
                                    {
                                        Header header = event.getHeader();
                                        if ( header.getEventName() == EventCatalog.MANAGEMENT_SWITCH_UP
                                            || header.getEventName() == EventCatalog.TOR_SWITCH_UP
                                            || header.getEventName() == EventCatalog.SPINE_SWITCH_UP )
                                        {
                                            switchMonitoring = true;
                                        }
                                    }
                                }
                                try
                                {
                                    logger.debug( "Monitoring Switch node: " + response.getHms_node_id()
                                        + " TimeStamp : " + new Date() );

                                    if ( !this.isStopMonitoring() )
                                    {

                                        HmsLocalSwitchMonitorTask hmsLocalSwitchMonitorTask =
                                            new HmsLocalSwitchMonitorTask( response, component, true );
                                        hmsLocalSwitchMonitorTask.executeTask();
                                    }
                                }
                                catch ( Exception e )
                                {
                                    logger.error( "Error getting sensor information for switch node id: {} and Switch component: {}",
                                                  response.getHms_node_id(), component, e );
                                }
                                break;
                            default:
                                try
                                {
                                    logger.debug( "Monitoring Switch node: " + response.getHms_node_id()
                                        + " TimeStamp : " + new Date() );

                                    if ( !this.isStopMonitoring() )
                                    {

                                        HmsLocalSwitchMonitorTask hmsLocalSwitchMonitorTask =
                                            new HmsLocalSwitchMonitorTask( response, component, switchMonitoring );
                                        hmsLocalSwitchMonitorTask.executeTask();
                                    }
                                }
                                catch ( Exception e )
                                {
                                    logger.error( "Error getting sensor information for switch node id: {}  and Switch component: {}",
                                                  response.getHms_node_id(), component, e );
                                }
                                break;
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
