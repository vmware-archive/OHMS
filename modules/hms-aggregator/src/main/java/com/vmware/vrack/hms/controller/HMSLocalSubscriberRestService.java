/* ********************************************************************************
 * HMSLocalSubscriberRestService.java
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
package com.vmware.vrack.hms.controller;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.common.events.BaseEventMonitoringSubscription;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscription;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.util.HmsMessages;
import com.vmware.vrack.hms.common.util.NetworkInterfaceUtil;

/**
 * Events Subscription Rest Endpoints for Hms-Local. Subscribers will Subscribe only to Hms-local. Hms-local will take
 * care of the events notification by passing the request to hms-core
 *
 * @author Yagnesh Chawda
 */
@Controller
@RequestMapping( "/events" )
public class HMSLocalSubscriberRestService
{
    private static Logger logger = Logger.getLogger( HMSLocalSubscriberRestService.class );

    @Value( "${hms.switch.host}" )
    private String hmsIpAddr;

    @Value( "${hms.switch.port}" )
    private int hmsPort;

    @Value( "${hms.local.port}" )
    private String hmsLocalPort;

    @Value( "${hms.local.context}" )
    private String hmsLocalContext;

    @Value( "${hms.local.protocol}" )
    private String hmsLocalProtocol;

    /**
     * Network interface name to which hms OOB will send data to hms IB
     */
    @Value( "${hms.network.interface.1}" )
    private String hmsIbNetworkInterface;

    private static MonitoringUtil monitoringUtil;

    @Autowired
    public void setMonitoringUtil( MonitoringUtil monitoringUtil )
    {
        HMSLocalSubscriberRestService.monitoringUtil = monitoringUtil;
    }

    /**
     * Gets the Hms-local Ip address by defined Network Interface Name
     */
    public String getHmsLocalIP()
        throws SocketException
    {
        return NetworkInterfaceUtil.getByInterfaceName( hmsIbNetworkInterface );
    }

    /**
     * Method to be called to get list of subscriber's notification Endpoints, who registered for Non-Maskable Events
     * notifications.
     *
     * @return
     * @throws HMSRestException
     */
    @RequestMapping( value = "/register", method = RequestMethod.GET )
    @ResponseBody
    public List<BaseEventMonitoringSubscription> getNmeEvents()
        throws HMSRestException
    {
        List<BaseEventMonitoringSubscription> baseEventMonitoringSubscriptions =
            new ArrayList<BaseEventMonitoringSubscription>();
        for ( BaseEventMonitoringSubscription subscription : EventMonitoringSubscriptionHolder.getNmeMonitoringSubscriptionMap().values() )
        {
            baseEventMonitoringSubscriptions.add( subscription );
        }
        if ( baseEventMonitoringSubscriptions.isEmpty() )
        {
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), HmsMessages.NO_DATA_FOUND,
                                        "No subscription for Non maskable events so far." );
        }
        return baseEventMonitoringSubscriptions;
    }

    /**
     * Method to be called to register for Non-Maskable Events notifications. Calling system will send is URL to be
     * called by HMS, when Non-maskable events are generated at HMS end for any Node/Switch.
     *
     * @param baseEventMonitoringSubscriptions
     * @return
     * @throws HMSRestException
     */
    @RequestMapping( value = "/register", method = RequestMethod.POST )
    @ResponseBody
    public BaseResponse nme( @RequestBody String eventRegistrationJson, HttpMethod method, HttpServletRequest request )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        ObjectMapper mapper = new ObjectMapper();
        List<BaseEventMonitoringSubscription> baseEventMonitoringSubscriptions = null;
        try
        {
            baseEventMonitoringSubscriptions =
                mapper.readValue( eventRegistrationJson, new TypeReference<List<BaseEventMonitoringSubscription>>()
                {
                } );
        }
        catch ( Exception e )
        {
            logger.error( e.getMessage() );
            response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
            response.setErrorMessage( HmsMessages.NME_SUBSCRIPTION_ERROR
                + " Please check the field names in Json provided. " + "Error Message: " + e.getMessage() );
            return response;
        }
        List<BaseEventMonitoringSubscription> failedRegistrations = new ArrayList<BaseEventMonitoringSubscription>();
        if ( baseEventMonitoringSubscriptions != null && !baseEventMonitoringSubscriptions.isEmpty() )
        {
            response.setStatusCode( Status.ACCEPTED.getStatusCode() );
            for ( int i = 0; i < baseEventMonitoringSubscriptions.size(); i++ )
            {
                // while registering for the events if something goes for one event, other events will still continue
                BaseEventMonitoringSubscription baseEventMonitoringSubscription =
                    baseEventMonitoringSubscriptions.get( i );
                try
                {
                    EventMonitoringSubscriptionHolder.getInstance().addNmeMonitoringSubscription( baseEventMonitoringSubscription );
                }
                catch ( Exception e )
                {
                    failedRegistrations.add( baseEventMonitoringSubscription );
                    logger.error( e.getMessage(), e );
                    response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
                    response.setErrorMessage( HmsMessages.NME_SUBSCRIPTION_ERROR );
                }
            }
            logger.debug( "Subscribed NME Events. Final NME subscriptions: "
                + EventMonitoringSubscriptionHolder.getNmeMonitoringSubscriptionMap() );
            if ( failedRegistrations.isEmpty() )
            {
                response.setStatusMessage( HmsMessages.NME_SUBSCRIPTION_SUCCESS );
            }
            else
            {
                response.setStatusMessage( HmsMessages.NME_SUBSCRIPTION_SUCCESS_WITH_ERRORS );
            }
        }
        return response;
    }

    /**
     * Method to be called for Subscribing any Events by Subscriber for any Nodes, on a particular component instances
     * Subscriber can register for multiple nodes/component's instances in one single request though.
     *
     * @param eventRegistrations
     * @return
     * @throws HMSRestException
     */
    @RequestMapping( value = "/subscribe", method = RequestMethod.POST )
    @ResponseBody
    public BaseResponse subscribe( @RequestBody String eventRegistrationJson )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        ObjectMapper mapper = new ObjectMapper();
        List<EventMonitoringSubscription> eventRegistrations = null;
        try
        {
            eventRegistrations =
                mapper.readValue( eventRegistrationJson, new TypeReference<List<EventMonitoringSubscription>>()
                {
                } );
        }
        catch ( Exception e )
        {
            logger.error( e.getMessage() );
            response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
            response.setErrorMessage( HmsMessages.SUBSCRIPTION_ERROR + " Please check the fieldNames in Json provided. "
                + "Error Message: " + e.getMessage() );
            return response;
        }
        List<EventMonitoringSubscription> failedRegistrations = new ArrayList<EventMonitoringSubscription>();
        if ( eventRegistrations != null && !eventRegistrations.isEmpty() )
        {
            response.setStatusCode( Status.ACCEPTED.getStatusCode() );
            for ( int i = 0; i < eventRegistrations.size(); i++ )
            {
                // while registering for the events if something goes for one event, other events will still continue
                EventMonitoringSubscription eventMonitoringSubscription = eventRegistrations.get( i );
                try
                {
                    EventMonitoringSubscriptionHolder.getInstance().addEventMonitoringSubscription( eventMonitoringSubscription );
                }
                catch ( Exception e )
                {
                    failedRegistrations.add( eventMonitoringSubscription );
                    logger.error( e.getMessage() );
                    response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
                    response.setErrorMessage( HmsMessages.SUBSCRIPTION_ERROR );
                }
            }
            logger.debug( "Registered Events. Final subscribed events: "
                + EventMonitoringSubscriptionHolder.getEventMonitoringSubscriptionMap().keySet() );
            if ( failedRegistrations.isEmpty() )
            {
                response.setStatusMessage( HmsMessages.SUBSCRIPTION_SUCCESS );
            }
            else
            {
                response.setStatusMessage( HmsMessages.SUBSCRIPTION_SUCCESS_WITH_ERRORS );
            }
        }
        return response;
    }

    /**
     * Method to unsubscribe to any particular Event that might have been subscribed by a particular subscriber on a
     * particular component's instnace.
     *
     * @param eventRegistrations
     * @return
     * @throws HMSRestException
     */
    @RequestMapping( value = "/unsubscribe", method = RequestMethod.POST )
    @ResponseBody
    public BaseResponse unsubscribe( @RequestBody List<EventMonitoringSubscription> eventRegistrations )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        List<EventMonitoringSubscription> failedRegistrations = new ArrayList<EventMonitoringSubscription>();
        if ( eventRegistrations != null && !eventRegistrations.isEmpty() )
        {
            response.setStatusCode( Status.ACCEPTED.getStatusCode() );
            for ( int i = 0; i < eventRegistrations.size(); i++ )
            {
                // while unregistering for the events, if something goes wrong for one event, other events will still
                // continue
                EventMonitoringSubscription eventMonitoringSubscription = eventRegistrations.get( i );
                try
                {
                    EventMonitoringSubscriptionHolder.getInstance().removeEventMonitoringSubscription( eventMonitoringSubscription );
                }
                catch ( Exception e )
                {
                    failedRegistrations.add( eventMonitoringSubscription );
                    logger.error( e.getMessage(), e );
                    response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
                    response.setErrorMessage( HmsMessages.UNSUBSCRIPTION_ERROR );
                }
            }
            logger.debug( "Unsubscribed Events. Final subscriptions: "
                + EventMonitoringSubscriptionHolder.getEventMonitoringSubscriptionMap().keySet() );
            if ( failedRegistrations.isEmpty() )
            {
                response.setStatusMessage( HmsMessages.SUCCESS_MSG );
            }
            else
            {
                response.setStatusMessage( HmsMessages.ONE_OR_MORE_ITEMS_IN_REQ_FAILED );
            }
        }
        return response;
    }

    /**
     * Method to be called to get the list of Events subscribed by any particular subscriber.
     *
     * @param app_id
     * @return
     * @throws HMSRestException
     */
    @RequestMapping( value = "/{subscriber_id}", method = RequestMethod.GET )
    @ResponseBody
    public List<EventMonitoringSubscription> getEventSubscriptionDetails( @PathVariable( "subscriber_id" ) String subscriberId)
        throws HMSRestException
    {
        List<EventMonitoringSubscription> eventRegistrations = new ArrayList<EventMonitoringSubscription>();
        for ( EventMonitoringSubscription eventMonitoringSubscription : EventMonitoringSubscriptionHolder.getEventMonitoringSubscriptionMap().values() )
        {
            String subsId = eventMonitoringSubscription.getSubscriberId();
            if ( subscriberId.trim().equals( subsId ) )
            {
                eventRegistrations.add( eventMonitoringSubscription );
            }
        }
        if ( eventRegistrations.isEmpty() )
        {
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), HmsMessages.FAILED_MSG,
                                        "Can't find subscriber with id:" + subscriberId );
        }
        return eventRegistrations;
    }

    /**
     * Subscribe to Hms-core for non maskable events during hms-local bootup. To be notified at specified url in
     * hms-local
     */
    public void registerWithHmsCore()
    {
        logger.debug( "Preparing to register with Hms-core. HmsLocalSubscriberRestService Instance: " + this );
        monitoringUtil.registerWithHmsCore();
    }
}
