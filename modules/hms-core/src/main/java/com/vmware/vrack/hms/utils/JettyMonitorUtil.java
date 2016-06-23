/* ********************************************************************************
 * JettyMonitorUtil.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.utils;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.ConnectorStatistics;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;
import com.vmware.vrack.hms.common.util.Constants;

public class JettyMonitorUtil
{
    private static ConnectorStatistics stats =
        ServerNodeConnector.getInstance().getServer().getBean( ConnectorStatistics.class );

    private static Handler statHandler = ServerNodeConnector.getInstance().getServer().getHandler();

    private static final String HMS_AGENT_STATUS_DESC_SEPARATOR = " -";

    @SuppressWarnings( "deprecation" )
    public static ServerComponentEvent getServerStartedDuration()
    {
        ServerComponentEvent sensor = new ServerComponentEvent();
        sensor.setComponentId( "HMS_OOB_AGENT_RESTHANDLER" );
        sensor.setUnit( EventUnitType.DISCRETE );
        if ( stats != null )
        {
            long milliseconds = stats.getStartedMillis();
            int seconds = (int) ( milliseconds / 1000 ) % 60;
            int minutes = (int) ( ( milliseconds / ( 1000 * 60 ) ) % 60 );
            int hours = (int) ( ( milliseconds / ( 1000 * 60 * 60 ) ) % 24 );
            sensor.setDiscreteValue( hours + " hrs " + minutes + " mins " + seconds + " secs" );
        }
        sensor.setEventName( NodeEvent.HMS_OOB_AGENT_RESTHANDLER_STARTED_DURATION );
        return sensor;
    }

    public static ServerComponentEvent getServerState()
    {
        ServerComponentEvent sensor = new ServerComponentEvent();
        sensor.setComponentId( EventComponent.RACK.name() );
        sensor.setUnit( EventUnitType.DISCRETE );
        String eventText = null;
        if ( stats != null && AbstractLifeCycle.STARTED.equals( stats.getState() ) )
        {
            sensor.setEventName( NodeEvent.HMS_AGENT_UP );
            eventText = NodeEvent.HMS_AGENT_UP.getEventID().getEventText();
        }
        else
        {
            sensor.setEventName( NodeEvent.HMS_AGENT_DOWN );
            eventText = NodeEvent.HMS_AGENT_DOWN.getEventID().getEventText();
        }
        sensor.setEventId( Constants.HMS_OOBAGENT_STATUS );
        String descreteValue = StringUtils.substringBefore( eventText, HMS_AGENT_STATUS_DESC_SEPARATOR );
        sensor.setDiscreteValue( descreteValue );
        return sensor;
    }

    @SuppressWarnings( "deprecation" )
    public static ServerComponentEvent getServerMeanResponseTime()
    {
        ServerComponentEvent sensor = new ServerComponentEvent();
        sensor.setComponentId( "HMS_OOB_AGENT_RESTHANDLER" );
        sensor.setUnit( EventUnitType.DISCRETE );
        if ( statHandler != null && statHandler instanceof StatisticsHandler )
        {
            sensor.setDiscreteValue( String.valueOf( ( (StatisticsHandler) statHandler ).getDispatchedTimeMean() ) );
        }
        sensor.setEventName( NodeEvent.HMS_OOB_AGENT_RESTHANDLER_MEAN_RESPONSETIME );
        return sensor;
    }

    @SuppressWarnings( "deprecation" )
    public static ServerComponentEvent getIncomingMessagesCount()
    {
        ServerComponentEvent sensor = new ServerComponentEvent();
        sensor.setComponentId( "HMS_OOB_AGENT_RESTHANDLER" );
        sensor.setUnit( EventUnitType.OTHER );
        if ( statHandler != null && statHandler instanceof StatisticsHandler )
        {
            sensor.setValue( ( (StatisticsHandler) statHandler ).getRequests() );
        }
        sensor.setEventName( NodeEvent.HMS_OOB_AGENT_RESTHANDLER_MESSAGE_IN_COUNT );
        return sensor;
    }

    @SuppressWarnings( "deprecation" )
    public static ServerComponentEvent getOutgoingMessagesCount()
    {
        ServerComponentEvent sensor = new ServerComponentEvent();
        sensor.setComponentId( "HMS_OOB_AGENT_RESTHANDLER" );
        sensor.setUnit( EventUnitType.OTHER );
        if ( statHandler != null && statHandler instanceof StatisticsHandler )
        {
            StatisticsHandler handler = (StatisticsHandler) statHandler;
            int responseCount = handler.getResponses1xx() + handler.getResponses2xx() + handler.getResponses3xx()
                + handler.getResponses4xx() + handler.getResponses5xx();
            sensor.setValue( responseCount );
        }
        sensor.setEventName( NodeEvent.HMS_OOB_AGENT_RESTHANDLER_MESSAGE_OUT_COUNT );
        return sensor;
    }

    public static int getActiveRequestsCount()
    {
        int count = -1;
        if ( statHandler != null && statHandler instanceof StatisticsHandler )
        {
            StatisticsHandler handler = (StatisticsHandler) statHandler;
            count = handler.getRequestsActive();
        }
        return count;
    }
}
