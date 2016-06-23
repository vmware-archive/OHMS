/* ********************************************************************************
 * MonitorResponseCallbackTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.monitoring;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.vmware.vrack.common.event.Body;
import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.Header;
import com.vmware.vrack.common.event.enums.EventCatalog;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.common.event.enums.EventSeverity;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscription;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.util.CommonProperties;

public class MonitorResponseCallbackTest
{
    private static Logger logger = Logger.getLogger( MonitorResponseCallbackTest.class );

    @Test
    public void test()
    {
        logger.info( "Test MonitorResponseCallbackTest" );
        try
        {
            MonitorResponseCallback monitorResponseCallback = new MonitorResponseCallback();
            ServerNode node = new ServerNode();
            node.setManagementIp( "xx.xx.xx.xx" );
            node.setManagementUserName( "test" );
            node.setManagementUserPassword( "test" );
            HmsNode hmsnode = node;
            List<EventMonitoringSubscription> subscribers = new ArrayList<EventMonitoringSubscription>();
            EventMonitoringSubscription eventMonitoringSubscription;
            eventMonitoringSubscription = new EventMonitoringSubscription();
            eventMonitoringSubscription.setNodeId( "testnode" );
            eventMonitoringSubscription.setSubscriberId( "123" );
            eventMonitoringSubscription.setComponent( EventComponent.CPU );
            subscribers.add( eventMonitoringSubscription );
            List<Event> events = new ArrayList<Event>();
            Event event = new Event();
            Body body = new Body();
            Header header = new Header();
            header.setAgent( "HMS" );
            header.setEventName( EventCatalog.CPU_INITIALIZATION_ERROR );
            header.setSeverity( EventSeverity.CRITICAL );
            event.setBody( body );
            event.setHeader( header );
            events.add( event );
            CommonProperties commonProperties = new CommonProperties();
            commonProperties.setPrmBasicAuthUser( "test" );
            commonProperties.setPrmBasicAuthPass( "test" );
            List<ServerComponent> components = new ArrayList<ServerComponent>()
            {
                {
                    add( ServerComponent.CPU );
                }
            };
            monitorResponseCallback.callbackEventSubcribers( hmsnode, components );
            List<SwitchComponentEnum> switchComponents = new ArrayList<SwitchComponentEnum>()
            {
                {
                    add( SwitchComponentEnum.SWITCH );
                }
            };
            monitorResponseCallback.callbackSwitchEventSubcribers( hmsnode, switchComponents );
            monitorResponseCallback.postCallBackRequest( subscribers, events );
            monitorResponseCallback.callbackEventSubcribersUsingEvents( hmsnode, events );
        }
        catch ( Exception e )
        {
            logger.info( "Test MonitorResponseCallbackTest Failed!" );
            e.printStackTrace();
        }
    }
}
