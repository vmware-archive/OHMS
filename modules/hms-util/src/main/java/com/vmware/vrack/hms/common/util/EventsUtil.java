/* ********************************************************************************
 * EventsUtil.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.Header;
import com.vmware.vrack.common.event.enums.EventCatalog;
import com.vmware.vrack.hms.common.RequestMethod;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentSwitchEventInfoProvider;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.events.BaseEventMonitoringSubscription;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;

/**
 * Utility class to broadcast non maskable events as well as subscribed events to the subscribers
 *
 * @author Yagnesh Chawda
 */
public class EventsUtil
{
    private static Logger logger = Logger.getLogger( EventsUtil.class );

    /**
     * Broadcast events to the subscribers, can be either maskable as well as non-maskable
     *
     * @param events
     * @param subscription
     */
    public static boolean broadcastEvents( List<Event> events, BaseEventMonitoringSubscription subscription )
    {
        if ( events != null && !events.isEmpty() && subscription != null )
        {
            try
            {
                // First we need to prepare client with the username and password,
                // otherwise it will fail during connection establishment
                // Here we are assuming that the username and password are already populated in CommonProperties class.
                HttpClientService.getInstance().prepareClients( CommonProperties.getPrmBasicAuthUser(),
                                                                CommonProperties.getPrmBasicAuthPass() );
                if ( subscription.getRequestMethod() != null && subscription.getNotificationEndpoint() != null )
                {
                    String url = subscription.getNotificationEndpoint();
                    RequestMethod method = subscription.getRequestMethod();
                    ObjectMapper mapper = new ObjectMapper();
                    String requestBody;
                    try
                    {
                        requestBody = mapper.writeValueAsString( events );
                        logger.debug( "Data before sending Notifications method: " + method + ":: url: " + url
                            + ":: requestBody: " + requestBody );
                        switch ( method )
                        {
                            case PUT:
                                HttpClientService.getInstance().postJson( url, requestBody, true, true );
                                break;
                            case POST:
                            default:
                                HttpClientService.getInstance().postJson( url, requestBody, true, true );
                                break;
                        }
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Exception occured while sending notifications to url: " + url + e );
                    }
                }
            }
            catch ( KeyManagementException | NoSuchAlgorithmException | IllegalArgumentException e )
            {
                logger.error( String.format( "Error initializing prm client, error message: %s", e.getMessage() ), e );
            }
            return true;
        }
        return false;
    }

    /**
     * Broadcast Non Maskable Events to everyone
     *
     * @param events
     */
    public static boolean broadcastNmeEvents( List<Event> events )
    {
        if ( events != null )
        {
            Collection<BaseEventMonitoringSubscription> nmeCollection =
                EventMonitoringSubscriptionHolder.getNmeMonitoringSubscriptionMap().values();
            for ( BaseEventMonitoringSubscription nmeSubscription : nmeCollection )
            {
                broadcastEvents( events, nmeSubscription );
            }
            return true;
        }
        return false;
    }

    /**
     * Broadcast subscribed events notification to respective subscribers
     *
     * @param events
     * @return
     */
    public static boolean broadcastSubscribedEvents( List<Event> events )
    {
        if ( events != null )
        {
            try
            {
                Map<BaseEventMonitoringSubscription, List<Event>> filteredEvents =
                    EventMonitoringSubscriptionHolder.getFilteredEvents( events );
                for ( BaseEventMonitoringSubscription subscription : filteredEvents.keySet() )
                {
                    // event if it fails for one endpoint it will still continue for the rest.
                    try
                    {
                        broadcastEvents( filteredEvents.get( subscription ), subscription );
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Error Broadcasting events notifications to: "
                            + subscription.getNotificationEndpoint() + "with Events List: "
                            + filteredEvents.get( subscription ) );
                    }
                }
            }
            catch ( HmsException e )
            {
                logger.error( "Error broadcasting subscribed events list" );
            }
            return true;
        }
        return false;
    }

    /**
     * Verifies if the ServerComponent is supported by BoardService before calling actual method for that component
     *
     * @param componentEventInfoProvider
     * @param serverComponent
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    public static boolean isComponentServerApiSupported( IComponentEventInfoProvider componentEventInfoProvider,
                                                         ServerComponent serverComponent,
                                                         ServiceHmsNode serviceHmsNode )
                                                             throws HmsException
    {
        if ( componentEventInfoProvider != null )
        {
            // HACK: Yags: Condition needs to be cleaned up as part of switch cleanup stories.
            if ( serverComponent != null && serverComponent.getComponentSensorAPI() == null )
            {
                return true;
            }
            List<HmsApi> hmsApis = componentEventInfoProvider.getSupportedHmsApi( serviceHmsNode );
            if ( hmsApis != null && serverComponent != null )
            {
                return hmsApis.contains( serverComponent.getComponentSensorAPI() );
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Verifies if the SwitchComponentEnum is supported by BoardService before calling actual method for that component
     *
     * @param switchComponentEventInfoProvider
     * @param switchComponent
     * @param serviceHmsNode
     * @return boolean
     * @throws HmsException
     */
    public static boolean isComponentSwitchApiSupported( IComponentSwitchEventInfoProvider switchComponentEventInfoProvider,
                                                         SwitchComponentEnum switchComponent,
                                                         ServiceHmsNode serviceHmsNode )
                                                             throws HmsException
    {
        if ( switchComponentEventInfoProvider != null )
        {
            if ( switchComponent != null && switchComponent.getComponentSensorAPI() == null )
            {
                return true;
            }
            List<HmsApi> hmsApis = switchComponentEventInfoProvider.getSupportedHmsSwitchApi( serviceHmsNode );
            if ( hmsApis != null && switchComponent != null )
            {
                return hmsApis.contains( switchComponent.getComponentSensorAPI() );
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Identifies if the event belongs to HMS
     *
     * @param eventName
     * @return
     */
    public static boolean isHmsSupportedEvent( EventCatalog eventName )
    {
        if ( eventName == null )
        {
            return false;
        }
        return ( EventCatalog.HMS_AGENT_UP.equals( eventName ) || EventCatalog.HMS_AGENT_DOWN.equals( eventName ) );
    }

    /**
     * Identifies if the event belongs to HMS
     *
     * @param event
     * @return
     */
    public static boolean isHmsSupportedEvent( Event event )
    {
        Header eventHeader = event.getHeader();
        EventCatalog eventCatalog = null;
        if ( eventHeader != null )
        {
            eventCatalog = eventHeader.getEventName();
        }
        return isHmsSupportedEvent( eventCatalog );
    }
}
