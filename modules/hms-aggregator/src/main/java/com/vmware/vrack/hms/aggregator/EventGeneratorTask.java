/* ********************************************************************************
 * EventGeneratorTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

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
public class EventGeneratorTask
    implements IEventAggregatorTask
{
    private static Logger logger = Logger.getLogger( EventGeneratorTask.class );

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
            if ( InventoryLoader.getInstance().getOOBSupportedServerComponents( serverNode.getNodeID() ) != null
                && InventoryLoader.getInstance().getOOBSupportedServerComponents( serverNode.getNodeID() ).contains( component.getComponentSensorAPI() ) )
                return MonitoringUtil.getOnDemandEventsOOB( serverNode.getNodeID(), component );
        }
        catch ( Exception e )
        {
            logger.debug( "Error getting OOB Events for component " + component, e );
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
            logger.debug( "Error getting IB Events for component " + component, e );
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
    public List<Event> getAggregatedEvents( ServerNode node, ServerComponent component )
        throws HMSRestException
    {
        List<Event> aggregatedEvents = new ArrayList<Event>();
        List<Event> tempEvents = getIBEvents( node, component );
        if ( tempEvents != null )
        {
            aggregatedEvents.addAll( tempEvents );
        }
        tempEvents = getOOBEvents( node, component );
        if ( tempEvents != null )
        {
            aggregatedEvents.addAll( tempEvents );
        }
        try
        {
            aggregatedEvents =
                EventFilterService.filterOrMassageEvents( node.getNodeID(), component, aggregatedEvents );
        }
        catch ( Exception e )
        {
            // Ignore
            logger.warn( "Error filtering events for Node:" + node.getNodeID() + ", for Component:" + component, e );
        }
        // TODO: Get Host UP/DOWN events too in EventFilterService
        // Generate the Host Up or Down event
        if ( component == ServerComponent.SERVER )
        {
            HostUpDownEventAggregator hostUpDownEventAggregator = new HostUpDownEventAggregator();
            List<Event> hostUpDownEvents = hostUpDownEventAggregator.getHostUpDownEvent( node );
            if ( hostUpDownEvents != null )
            {
                aggregatedEvents.addAll( hostUpDownEvents );
            }
        }
        if ( aggregatedEvents != null )
        {
            // Add FRU ID to Events
            try
            {
                List<Event> events =
                    FruIdEventsHelperUtil.addFruIDtoEvents( aggregatedEvents, node.getNodeID(), component );
                if ( events != null )
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
        if ( aggregatedEvents != null )
            SpringContextHelper.getApplicationContext().publishEvent( new FruEventStateChangeMessage( aggregatedEvents,
                                                                                                      component ) );
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
            if ( managementSwitchUpDownEvents != null )
            {
                if ( aggregatedSwitchEvents == null )
                    aggregatedSwitchEvents = new ArrayList<Event>();
                aggregatedSwitchEvents.addAll( managementSwitchUpDownEvents );
            }
        }
        try
        {
            aggregatedSwitchEvents =
                EventFilterService.filterOrMassageSwitchEvents( switchId, component, aggregatedSwitchEvents );
        }
        catch ( Exception e )
        {
            logger.warn( "Error filtering events for Switch Node:" + switchId + ", for Component:" + component, e );
        }
        if ( aggregatedSwitchEvents != null )
        {
            try
            {
                List<Event> switchEventListWithFruID =
                    FruIdEventsHelperUtil.addFruIDtoSwitchEventsHelper( aggregatedSwitchEvents, switchId );
                if ( switchEventListWithFruID != null )
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
        if ( aggregatedSwitchEvents != null )
            SpringContextHelper.getApplicationContext().publishEvent( new FruEventStateChangeMessage( aggregatedSwitchEvents,
                                                                                                      component ) );
        return aggregatedSwitchEvents;
    }
}
