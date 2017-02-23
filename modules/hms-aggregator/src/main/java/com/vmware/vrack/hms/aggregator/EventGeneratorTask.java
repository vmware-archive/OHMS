/* ********************************************************************************
 * EventGeneratorTask.java
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
package com.vmware.vrack.hms.aggregator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.aggregator.switches.ManagementSwitchUpDownEventHelper;
import com.vmware.vrack.hms.aggregator.util.FruIdEventsHelperUtil;
import com.vmware.vrack.hms.aggregator.util.HostUpDownEventAggregator;
import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.aggregator.util.SpringContextHelper;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.EventFilterService;
import com.vmware.vrack.hms.inventory.FruEventStateChangeMessage;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

/**
 * @author sgakhar Purpose of this class is to Aggregate component events fetched from OOB and IB agents. Source for
 *         InBand events is the InBandBoardService instantiated during bootup of InBandAgent. Source OOB events is the
 *         OutOfBandAgnet. Example Use case for CPU event: getAggregatedEvents(NODE_N1,CPU) getOOBEvents is called it
 *         returns CPU events from OOB Agent [CPU_TEMP_ABOVE_THRESHOLD,CPU_STATUS etc...] getIBEvents is called it
 *         returns CPU events from IB Agent [], which might be an empty array or null both IB and OOB event arrays are
 *         aggregated then to return [CPU_TEMP_ABOVE_THRESHOLD,CPU_STATUS etc...]. Example Use case for HDD:
 *         getAggregatedEvents(NODE_N1,HDD) getOOBEvents is called it returns HDD events from OOB Agent
 *         [HDD_STATUS,HDD_FAILURE] getIBEvents is called it returns HDD events(SMART based) from IB Agent
 *         [HDD_EXCESIVE_WRITE_ERRORS,HDD_HEALTH_STATUS_CRITICAL ,HDD_TEMP_ABOVE_THRESHOLD etc..] both IB and OOB event
 *         arrays are aggregated then to return [HDD_STATUS,HDD_FAILURE,HDD_EXCESIVE_WRITE_ERRORS
 *         ,HDD_HEALTH_STATUS_CRITICAL,HDD_TEMP_ABOVE_THRESHOLD].
 */
@SuppressWarnings( "deprecation" )
public class EventGeneratorTask
    implements IEventAggregatorTask
{

    private static Logger logger = LoggerFactory.getLogger( EventGeneratorTask.class );

    /**
     * Get OOB Events for given node and Component
     *
     * @param node_id
     * @param component
     * @return List<Event>
     */
    private List<Event> getOOBEvents( ServerNode serverNode, ServerComponent component )
    {
        try
        {
            if ( serverNode != null )
            {
                if ( InventoryLoader.getInstance().getOOBSupportedServerComponents( serverNode.getNodeID() ) != null
                    && InventoryLoader.getInstance().getOOBSupportedServerComponents( serverNode.getNodeID() ).contains( component.getComponentSensorAPI() ) )
                    return MonitoringUtil.getOnDemandEventsOOB( serverNode.getNodeID(), component );
            }
            else
            {
                logger.warn( "Not calling OOB for On Demand Events as Server Node is null, returning null" );
            }
        }
        catch ( Exception e )
        {
            logger.error( String.format( "Error getting OOB Events for component : %s for node : %s", component,
                                         serverNode.getNodeID() ),
                          e );
        }
        return null;

    }

    /**
     * Get IB Events for given node and Component
     *
     * @param node_id
     * @param component
     * @return List<Event>
     */
    private List<Event> getIBEvents( ServerNode serverNode, ServerComponent component )
    {
        try
        {
            if ( serverNode != null )
            {
                IComponentEventInfoProvider boardService =
                    InBandServiceProvider.getBoardService( serverNode.getServiceObject() );

                if ( boardService.getSupportedHmsApi( serverNode.getServiceObject() ) != null
                    && boardService.getSupportedHmsApi( serverNode.getServiceObject() ).contains( component.getComponentSensorAPI() ) )
                {
                    serverNode.addComponentSensorData( component,
                                                       boardService.getComponentEventList( serverNode.getServiceObject(),
                                                                                           component ) );
                    return EventMonitoringSubscriptionHolder.getEventList( serverNode, component );
                }
            }
        }
        catch ( Exception e )
        {
            logger.error( String.format( "Error getting IB Events for component : %s for node : %s ", component,
                                         serverNode.getNodeID() ),
                          e );
        }
        return null;
    }

    /**
     * Returns Aggregation of inband and out of band events
     *
     * @param node
     * @param component
     * @return List<Event>
     */
    @Override
    public List<Event> getAggregatedEvents( ServerNode node, ServerComponent component, boolean oobMonitoring,
                                            boolean ibMonitoring )
        throws HMSRestException
    {

        List<Event> aggregatedEvents = new ArrayList<Event>();
        List<Event> tempEvents;

        // If oobMonitoring is true
        if ( oobMonitoring )
        {
            logger.debug( "Getting Events from HMS OOB agent for Server component : {} Node : {} ", component,
                          node.getNodeID() );
            tempEvents = getOOBEvents( node, component );
            if ( tempEvents != null && tempEvents.size() > 0 )
            {
                logger.debug( "Got Events from HMS OOB agent for Server component : {} Node : {} ", component,
                              node.getNodeID() );
                aggregatedEvents.addAll( tempEvents );
            }
        }
        else
        {
            logger.error( "Out-Of-Band Monitoring is not enabled for Server component : {} as Node : {} OOB communication is failed.",
                          component, node.getNodeID() );
        }

        // If ibMonitoring is true
        if ( ibMonitoring )
        {
            logger.debug( "Getting Events from HMS IB agent for Server component : {} Node : {} ", component,
                          node.getNodeID() );
            tempEvents = getIBEvents( node, component );
            if ( tempEvents != null && tempEvents.size() > 0 )
            {
                logger.debug( "Got Events from HMS IB agent for Server component : {} Node : {} ", component,
                              node.getNodeID() );
                aggregatedEvents.addAll( tempEvents );
            }
        }
        else
        {
            logger.error( "Ignore this log if Server Component is BMC. In-Band Monitoring is not enabled for Server component : {} as Node : {} is not operational",
                          component, node.getNodeID() );
        }

        try
        {
            if ( aggregatedEvents != null && aggregatedEvents.size() > 0 )
            {
                aggregatedEvents =
                    EventFilterService.filterOrMassageEvents( node.getNodeID(), component, aggregatedEvents );
            }
        }
        catch ( Exception e )
        {
            // Ignore
            logger.warn( "Error filtering events for Node :" + node.getNodeID() + ", for Component :" + component, e );
        }

        // TODO: Get Host UP/DOWN events too in EventFilterService
        // Generate the Host Up or Down event
        if ( component == ServerComponent.SERVER )
        {
            // If oobMonitoring is true
            if ( oobMonitoring )
            {
                HostUpDownEventAggregator hostUpDownEventAggregator = new HostUpDownEventAggregator();
                List<Event> hostUpDownEvents = hostUpDownEventAggregator.getHostUpDownEvent( node );
                if ( hostUpDownEvents != null && hostUpDownEvents.size() > 0 )
                {
                    logger.debug( "Got Events for HMS Server node UP or DOWN for Server component : {} Node : {} ",
                                  component, node.getNodeID() );
                    aggregatedEvents.addAll( hostUpDownEvents );
                }
            }
        }

        if ( aggregatedEvents != null && aggregatedEvents.size() > 0 )
        {
            // Add FRU ID to Events
            try
            {
                List<Event> events =
                    FruIdEventsHelperUtil.addFruIDtoEvents( aggregatedEvents, node.getNodeID(), component );
                if ( events != null && events.size() > 0 )
                {
                    aggregatedEvents = new ArrayList<Event>();
                    aggregatedEvents.addAll( events );
                }
            }
            catch ( Exception e )
            {
                logger.error( "HMS Get Aggregated Events error in adding FRU ID to Events", e );
            }
        }

        // Publish event and refresh the HMS In memory Cache on HMS event
        if ( aggregatedEvents != null && aggregatedEvents.size() > 0 )
        {
            SpringContextHelper.getApplicationContext().publishEvent( new FruEventStateChangeMessage( aggregatedEvents,
                                                                                                      component ) );
        }

        return aggregatedEvents;
    }

    /**
     * Get OOB switch Events for given switch node and component
     *
     * @param switchId
     * @param component
     * @return List<Event>
     */
    private List<Event> getOOBSwitchEvents( String switchId, SwitchComponentEnum component )
    {
        try
        {
            return MonitoringUtil.getOnDemandSwitchEventsOOB( switchId, component );
        }
        catch ( Exception e )
        {
            logger.debug( "Error getting OOB Switch Events for component " + component, e );
        }
        return null;

    }

    /**
     * Get switch Aggregated events
     *
     * @param switchId
     * @param component
     * @return List<Event>
     * @throws HMSRestException
     */
    @Override
    public List<Event> getAggregatedSwitchEvents( String switchId, SwitchComponentEnum component,
                                                  boolean switchMonitoring )
        throws HMSRestException
    {

        List<Event> aggregatedSwitchEvents = new ArrayList<Event>();

        // If the switchMonitoring is enabled go head and generate the switch events.
        if ( switchMonitoring )
        {
            aggregatedSwitchEvents = getOOBSwitchEvents( switchId, component );

            // Generate the MANAGEMENT switch Up or Down event.
            if ( component == SwitchComponentEnum.SWITCH
                && StringUtils.equalsIgnoreCase( switchId, Constants.HMS_MANAGEMENT_SWITCH_ID ) )
            {
                List<Event> managementSwitchUpDownEvents =
                    ManagementSwitchUpDownEventHelper.getManagementSwitchUpDownEvent( switchId );
                if ( managementSwitchUpDownEvents != null && managementSwitchUpDownEvents.size() > 0 )
                {
                    if ( aggregatedSwitchEvents == null )
                        aggregatedSwitchEvents = new ArrayList<Event>();
                    aggregatedSwitchEvents.addAll( managementSwitchUpDownEvents );
                }
            }
        }
        else
        {
            logger.error( "Switch Monitoring is not enabled for Switch component: {} as the Switch Node: {} is down or not reachable.",
                          component, switchId );
        }

        // Filtering or Massage switch events
        try
        {
            if ( aggregatedSwitchEvents != null && aggregatedSwitchEvents.size() > 0 )
            {
                aggregatedSwitchEvents =
                    EventFilterService.filterOrMassageSwitchEvents( switchId, component, aggregatedSwitchEvents );
            }
        }
        catch ( Exception e )
        {
            logger.warn( "Error filtering events for Switch Node: {} for Component: {}", switchId, component, e );
        }

        // Add FRU ID to switch Events
        if ( aggregatedSwitchEvents != null && aggregatedSwitchEvents.size() > 0 )
        {
            try
            {
                List<Event> switchEventListWithFruID =
                    FruIdEventsHelperUtil.addFruIDtoSwitchEventsHelper( aggregatedSwitchEvents, switchId );
                if ( switchEventListWithFruID != null && switchEventListWithFruID.size() > 0 )
                {
                    aggregatedSwitchEvents = new ArrayList<Event>();
                    aggregatedSwitchEvents.addAll( switchEventListWithFruID );
                }
            }
            catch ( HmsException e )
            {
                logger.error( "HMS Get Aggregated Switch Events error in adding FRU ID to Events", e );
            }
        }

        // Publish event to refresh the HMS In memory data/Cache on switch event
        if ( aggregatedSwitchEvents != null && aggregatedSwitchEvents.size() > 0 )
        {
            SpringContextHelper.getApplicationContext().publishEvent( new FruEventStateChangeMessage( aggregatedSwitchEvents,
                                                                                                      component ) );
        }

        return aggregatedSwitchEvents;
    }

    @Override
    public List<Event> getAggregatedSwitchEvents( String switchId, SwitchComponentEnum component )
        throws HMSRestException
    {

        List<Event> aggregatedSwitchEvents = getOOBSwitchEvents( switchId, component );

        // Generate the MANAGEMENT switch Up or Down event.
        if ( component == SwitchComponentEnum.SWITCH
            && StringUtils.equalsIgnoreCase( switchId, Constants.HMS_MANAGEMENT_SWITCH_ID ) )
        {
            List<Event> managementSwitchUpDownEvents =
                ManagementSwitchUpDownEventHelper.getManagementSwitchUpDownEvent( switchId );
            if ( managementSwitchUpDownEvents != null && managementSwitchUpDownEvents.size() > 0 )
            {
                if ( aggregatedSwitchEvents == null )
                    aggregatedSwitchEvents = new ArrayList<Event>();
                aggregatedSwitchEvents.addAll( managementSwitchUpDownEvents );
            }
        }

        // Add FRU ID to switch Events
        if ( aggregatedSwitchEvents != null && aggregatedSwitchEvents.size() > 0 )
        {
            try
            {
                List<Event> switchEventListWithFruID =
                    FruIdEventsHelperUtil.addFruIDtoSwitchEventsHelper( aggregatedSwitchEvents, switchId );
                if ( switchEventListWithFruID != null && switchEventListWithFruID.size() > 0 )
                {
                    aggregatedSwitchEvents = new ArrayList<Event>();
                    aggregatedSwitchEvents.addAll( switchEventListWithFruID );
                }
            }
            catch ( HmsException e )
            {
                logger.error( "HMS Get Aggregated Switch Events error in adding FRU ID to Events", e );
            }
        }

        return aggregatedSwitchEvents;
    }

    @Override
    public List<Event> getAggregatedEvents( ServerNode node, ServerComponent component )
        throws HMSRestException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> processEvents( ServerNode node, ServerComponent component )
        throws HMSRestException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
