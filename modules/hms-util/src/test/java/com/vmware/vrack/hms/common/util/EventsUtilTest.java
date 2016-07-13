/* ********************************************************************************
 * EventsUtilTest.java
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
package com.vmware.vrack.hms.common.util;

import static org.junit.Assert.*;

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
import com.vmware.vrack.hms.common.events.EventMonitoringSubscription;

public class EventsUtilTest
{
    private static Logger logger = Logger.getLogger( EventsUtilTest.class );

    @Test
    public void test()
    {
        logger.info( "Testing EventsUtilTest" );
        try
        {
            CommonProperties commonProperties = new CommonProperties();
            commonProperties.setPrmBasicAuthUser( "test" );
            commonProperties.setPrmBasicAuthPass( "test" );
            List<Event> events = new ArrayList<Event>();
            Event event = new Event();
            EventMonitoringSubscription eventMonitoringSubscription;
            Body body = new Body();
            Header header = new Header();
            Boolean status;
            body.setDescription( "CPU for rack EVO:RACK node DellNode and CPU processor 1 has shutdown due to thermal error" );
            header.setAgent( "HMS" );
            header.setEventName( EventCatalog.CPU_THERMAL_TRIP );
            header.setSeverity( EventSeverity.CRITICAL );
            event.setBody( body );
            event.setHeader( header );
            events.add( event );
            eventMonitoringSubscription = new EventMonitoringSubscription();
            eventMonitoringSubscription.setNodeId( "testnode" );
            eventMonitoringSubscription.setSubscriberId( "123" );
            eventMonitoringSubscription.setComponent( EventComponent.CPU );
            status = EventsUtil.broadcastEvents( events, eventMonitoringSubscription );
            assertTrue( status );
            status = EventsUtil.broadcastNmeEvents( events );
            assertTrue( status );
            status = EventsUtil.broadcastSubscribedEvents( events );
            assertTrue( status );
        }
        catch ( Exception e )
        {
            logger.info( "Test EventsUtilTest Failed" );
            e.printStackTrace();
        }
    }
}
