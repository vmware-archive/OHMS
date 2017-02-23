/* ********************************************************************************
 * SubscriberRestService.java
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
package com.vmware.vrack.hms.rest.services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.common.events.BaseEventMonitoringSubscription;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscription;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.util.HmsMessages;

@Path( "/events" )
public class SubscriberRestService
{

    private static Logger logger = Logger.getLogger( SubscriberRestService.class );

    /**
     * Method to be called to get list of subscriber's notificationEndpoint, who registered for Non-Maskable Events
     * notifications.
     * 
     * @return
     * @throws HMSRestException
     */
    @GET
    @Path( "/register" )
    @Produces( "application/json" )
    public List<BaseEventMonitoringSubscription> getNmeEvents()
        throws HMSRestException
    {

        List<BaseEventMonitoringSubscription> baseEventMonitoringSubscriptions =
            new ArrayList<BaseEventMonitoringSubscription>();

        for ( BaseEventMonitoringSubscription baseEventMonitoringSubscription : EventMonitoringSubscriptionHolder.getNmeMonitoringSubscriptionMap().values() )
        {
            baseEventMonitoringSubscriptions.add( baseEventMonitoringSubscription );
        }

        if ( baseEventMonitoringSubscriptions.isEmpty() )
        {
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), HmsMessages.SUCCESS_MSG,
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
    @POST
    @Path( "/register" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( "application/json" )
    public BaseResponse nme( List<BaseEventMonitoringSubscription> baseEventMonitoringSubscriptions )
        throws HMSRestException
    {

        BaseResponse response = new BaseResponse();

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
    @POST
    @Path( "/subscribe" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( "application/json" )
    public BaseResponse subscribe( List<EventMonitoringSubscription> eventRegistrations )
        throws HMSRestException
    {

        BaseResponse response = new BaseResponse();

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
    @POST
    @Path( "/unsubscribe" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( "application/json" )
    public BaseResponse unsubscribe( List<EventMonitoringSubscription> eventRegistrations )
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
    @GET
    @Path( "/{subscriber_id}" )
    @Produces( "application/json" )
    public List<EventMonitoringSubscription> getEventSubscriptionDetails( @PathParam( "subscriber_id" ) String subscriberId )
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
     * Returns Event notifications on demand basis.
     * 
     * @return
     * @throws HMSRestException
     */
    @GET
    @Path( "/" )
    @Produces( "application/json" )
    public List<Event> getAvailableEvents()
        throws HMSRestException
    {

        throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                    "Feature not implemented yet" );

    }

}
