/* ********************************************************************************
 * HmsCacheEventTest.java
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

package com.vmware.vrack.hms.inventory;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vmware.vrack.common.event.Body;
import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.Header;
import com.vmware.vrack.common.event.enums.EventCatalog;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.common.event.enums.EventSeverity;
import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.rest.model.CpuInfo;
import com.vmware.vrack.hms.common.rest.model.FruComponent;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;

/**
 * Unit Test case for HMS cache event massager and listener
 *
 * @author VMware Inc
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/hms-aggregator-test.xml" } )
public class HmsCacheEventTest
    implements ApplicationEventPublisherAware
{

    private ApplicationEventPublisher eventPublisher;

    /**
     * Test Server Cache Event Publisher
     */
    @Test
    public void serverCacheEventTest()
    {

        HmsDataCache cache = new HmsDataCache();

        ServerCacheUpdateListener listener = new ServerCacheUpdateListener();
        listener.setHmsDataCache( cache );

        StaticApplicationContext context = new StaticApplicationContext();
        context.addApplicationListener( listener );

        context.refresh();

        // Adding dummy Server Information
        ServerInfo serverInfo = new ServerInfo();
        ComponentIdentifier serverComponentIdentifier = new ComponentIdentifier();
        serverComponentIdentifier.setManufacturer( "Testware" );
        serverComponentIdentifier.setProduct( "VM360" );
        serverComponentIdentifier.setPartNumber( "JFD32254" );
        serverComponentIdentifier.setSerialNumber( "32355567" );
        serverInfo.setComponentIdentifier( serverComponentIdentifier );
        serverInfo.setFruId( "53543454" );
        serverInfo.setInBandIpAddress( "127.0.0.1" );
        serverInfo.setLocation( "2U" );
        serverInfo.setManagementIpAddress( "127.0.0.1" );
        serverInfo.setNodeId( "TestNode" );
        serverInfo.setOperationalStatus( FruOperationalStatus.Operational );

        ServerDataChangeMessage event = new ServerDataChangeMessage( serverInfo, ServerComponent.SERVER );

        context.publishEvent( event );

        assertNotNull( listener.getHmsDataCache().getServerInfoMap().containsKey( "TestNode" ) );
        assertNotNull( listener.getHmsDataCache().getServerInfoMap().get( "TestNode" ).getNodeId() );

        context.close();
        context.destroy();

    }

    /**
     * Test Switch Cache Event Publisher
     */
    @Test
    public void switchCacheEventTest()
    {

        HmsDataCache cache = new HmsDataCache();

        SwitchCacheUpdateListener listener = new SwitchCacheUpdateListener();
        listener.setHmsDataCache( cache );

        StaticApplicationContext context = new StaticApplicationContext();
        context.addApplicationListener( listener );

        context.refresh();

        // Adding dummy Switch Information
        NBSwitchInfo switchInfo = new NBSwitchInfo();
        ComponentIdentifier switchComponentIdentifier = new ComponentIdentifier();
        switchComponentIdentifier.setManufacturer( "Testware" );
        switchComponentIdentifier.setProduct( "VM-Switch" );
        switchComponentIdentifier.setPartNumber( "5435GFFGF" );
        switchComponentIdentifier.setSerialNumber( "6546547" );
        switchInfo.setComponentIdentifier( switchComponentIdentifier );
        switchInfo.setFirmwareName( "firmwareTest" );
        switchInfo.setFirmwareVersion( "dsd2321" );
        switchInfo.setLocation( "3U" );
        switchInfo.setFruId( "443254576" );
        switchInfo.setOperationalStatus( FruOperationalStatus.Operational );
        switchInfo.setSwitchId( "TestSwitch" );
        // switchInfo.setRole(SwitchRoleType.MANAGEMENT);

        SwitchDataChangeMessage event = new SwitchDataChangeMessage( switchInfo, SwitchComponentEnum.SWITCH );

        context.publishEvent( event );

        assertNotNull( listener.getHmsDataCache().getServerInfoMap().containsKey( "TestSwitch" ) );
        assertNotNull( listener.getHmsDataCache().getSwitchInfoMap().get( "TestSwitch" ).getSwitchId() );

        context.close();
        context.destroy();

    }

    /**
     * Test Server FRU Component Cache Event Publisher
     */
    @Test
    public void fruCacheEventTest()
    {

        HmsDataCache cache = new HmsDataCache();

        FruCacheUpdateListener listener = new FruCacheUpdateListener();
        listener.setHmsDataCache( cache );

        StaticApplicationContext context = new StaticApplicationContext();
        context.addApplicationListener( listener );

        context.refresh();

        // Adding dummy CPU Information
        List<CpuInfo> listCpu = new ArrayList<CpuInfo>();
        CpuInfo cpu1 = new CpuInfo();
        ComponentIdentifier cpuIdentifier1 = new ComponentIdentifier();
        cpuIdentifier1.setManufacturer( "INTEL" );
        cpuIdentifier1.setProduct( "Intel Xeon Processor" );
        cpu1.setComponentIdentifier( cpuIdentifier1 );
        cpu1.setId( "1" );
        cpu1.setCpuFrequencyInHertz( 2600 );
        cpu1.setNumOfCores( 4 );
        listCpu.add( cpu1 );

        FruDataChangeMessage event =
            new FruDataChangeMessage( (List<FruComponent>) (List<?>) listCpu, "TestNode", ServerComponent.CPU );
        context.publishEvent( event );

        assertNotNull( listener.getHmsDataCache().getServerInfoMap().containsKey( "TestNode" ) );
        // assertNotNull(listener.getHmsDataCache().getServerInfoMap().get("TestNode").getCpuInfo().get(0).getComponentIdentifier().getManufacturer());

        context.close();
        context.destroy();

    }

    /**
     * Test HMS Event Publisher to refresh the cache on Event
     */
    @Test
    public void fruHmsEventCacheEventTest()
    {

        HmsDataCache cache = new HmsDataCache();

        FruEventDataUpdateListener listener = new FruEventDataUpdateListener();
        listener.setHmsDataCache( cache );

        StaticApplicationContext context = new StaticApplicationContext();
        context.addApplicationListener( listener );

        context.refresh();

        List<Event> events = new ArrayList<Event>();
        Event event = new Event();
        Body body = new Body();
        Header header = new Header();
        Map<EventComponent, String> componentIdentifier = new HashMap<EventComponent, String>();
        componentIdentifier.put( EventComponent.SERVER, "N9" );

        // Adding dummy Event
        body.setDescription( "CPU for rack EVO:RACK node N5 and CPU processor 1 has shutdown due to POST Failure." );
        header.setAgent( "HMS" );
        header.setEventName( EventCatalog.CPU_POST_FAILURE );
        header.setSeverity( EventSeverity.CRITICAL );
        header.setVersion( "1.0" );
        header.addComponentIdentifier( componentIdentifier );

        event.setBody( body );
        event.setHeader( header );

        events.add( event );

        FruEventStateChangeMessage eventMessage = new FruEventStateChangeMessage( events, ServerComponent.CPU );
        context.publishEvent( eventMessage );

        assertNotNull( listener.getHmsDataCache().getServerInfoMap().containsKey( "TestNode" ) );

        context.close();
        context.destroy();

    }

    /**
     * @param applicationEventPublisher
     */
    @Override
    @Autowired
    public void setApplicationEventPublisher( ApplicationEventPublisher applicationEventPublisher )
    {
        this.eventPublisher = applicationEventPublisher;

    }

}
