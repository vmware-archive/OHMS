/* ********************************************************************************
 * FruIdEventsHelperUtilTest.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.util;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.vmware.vrack.common.event.Body;
import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.Header;
import com.vmware.vrack.common.event.enums.EventCatalog;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.common.event.enums.EventSeverity;
import com.vmware.vrack.hms.common.rest.model.CpuInfo;
import com.vmware.vrack.hms.common.rest.model.EthernetController;
import com.vmware.vrack.hms.common.rest.model.MemoryInfo;
import com.vmware.vrack.hms.common.rest.model.PortInfo;
import com.vmware.vrack.hms.common.rest.model.StorageController;
import com.vmware.vrack.hms.common.rest.model.StorageInfo;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;

/**
 * Unit Test cases to Integrate/Add the FRU ID to Events.
 *
 * @author VMware Inc.
 */
public class FruIdEventsHelperUtilTest
{
    private static Logger logger = Logger.getLogger( FruIdEventsHelperUtilTest.class );

    /**
     * Unit test to Integrate FRU ID with CPU Events
     */
    @Test
    public void addFruIDtoCpuEventsTest()
    {
        List<Event> events = new ArrayList<Event>();
        List<Event> cpuEventsFruID = new ArrayList<Event>();
        Event event = new Event();
        Body body = new Body();
        Header header = new Header();
        try
        {
            // Construct CPU Event for Testing
            body.setDescription( "CPU for rack EVO:RACK node N5 and CPU processor 0 has shutdown due to POST Failure." );
            Map<String, String> data = new HashMap<String, String>();
            data.put( "unit", EventUnitType.DISCRETE.toString() );
            data.put( "eventName", "CPU_POST_FAILURE" );
            body.setData( data );
            Map<EventComponent, String> compIdentifier = new HashMap<EventComponent, String>();
            compIdentifier.put( EventComponent.CPU, "0" );
            compIdentifier.put( EventComponent.SERVER, "N5" );
            header.addComponentIdentifier( compIdentifier );
            header.setAgent( "HMS" );
            header.setEventName( EventCatalog.CPU_POST_FAILURE );
            header.setSeverity( EventSeverity.CRITICAL );
            header.setVersion( "1.0" );
            event.setBody( body );
            event.setHeader( header );
            events.add( event );
            // Construct CPU FRU Information
            List<CpuInfo> cpuInfoList = new ArrayList<CpuInfo>();
            CpuInfo cpuInfo = new CpuInfo();
            ComponentIdentifier cpuComponentIdentifier = new ComponentIdentifier();
            cpuComponentIdentifier.setManufacturer( "intel" );
            cpuComponentIdentifier.setProduct( "Intel(R) Xeon(R) CPU E5-2650 v2 @ 2.60GHz" );
            cpuInfo.setComponentIdentifier( cpuComponentIdentifier );
            cpuInfo.setCpuFrequencyInHertz( 2593 );
            cpuInfo.setHostId( "N5" );
            cpuInfo.setNumOfCores( 8 );
            cpuInfo.setLocation( "0" );
            cpuInfo.setFruId( "-9458916751211679985" );
            cpuInfoList.add( cpuInfo );
            cpuEventsFruID = FruIdEventsHelperUtil.addFruIDtoCpuEvents( events, cpuInfoList );
            Map<String, String> eventData = cpuEventsFruID.get( 0 ).getBody().getData();
            assertNotNull( eventData.get( "fruID" ) );
        }
        catch ( Exception e )
        {
            logger.info( "Integrate FRU ID with CPU Events Test Failed" );
        }
    }

    /**
     * Unit test to Integrate FRU ID with Memory Events
     */
    @Test
    public void addFruIDtoMemoryEventsTest()
    {
        List<Event> events = new ArrayList<Event>();
        List<Event> memoryEventsFruID = new ArrayList<Event>();
        Event event = new Event();
        Body body = new Body();
        Header header = new Header();
        try
        {
            // Construct MEMORY Event for testing
            body.setDescription( "Memory temperature for rack EVO:RACK node N5 and Memory ChannelA_Dimm0 has reached its maximum safe operating temperature. Current Memory temperature is 95.0 degrees C." );
            Map<String, String> data = new HashMap<String, String>();
            data.put( "unit", EventUnitType.DEGREES_CELSIUS.toString() );
            data.put( "eventName", "MEMORY_TEMP_ABOVE_THRESHOLD" );
            body.setData( data );
            Map<EventComponent, String> compIdentifier = new HashMap<EventComponent, String>();
            compIdentifier.put( EventComponent.MEMORY, "ChannelA_Dimm0" );
            compIdentifier.put( EventComponent.SERVER, "N5" );
            header.addComponentIdentifier( compIdentifier );
            header.setAgent( "HMS" );
            header.setEventName( EventCatalog.DIMM_TEMPERATURE_ABOVE_UPPER_THRESHOLD );
            header.setSeverity( EventSeverity.WARNING );
            header.setVersion( "1.0" );
            event.setBody( body );
            event.setHeader( header );
            events.add( event );
            // Construct MEMORY FRU Information
            List<MemoryInfo> memoryInfoList = new ArrayList<MemoryInfo>();
            MemoryInfo memoryInfo = new MemoryInfo();
            ComponentIdentifier memoryComponentIdentifier = new ComponentIdentifier();
            memoryComponentIdentifier.setManufacturer( "Samsung" );
            memoryComponentIdentifier.setSerialNumber( "14289B0C" );
            memoryComponentIdentifier.setPartNumber( "M393B2G70QH0-YK0" );
            memoryInfo.setComponentIdentifier( memoryComponentIdentifier );
            memoryInfo.setMemorySpeedInHertz( 1333L );
            memoryInfo.setHostId( "N5" );
            memoryInfo.setLocation( "ChannelA_Dimm0" );
            memoryInfo.setFruId( "179086051211679985" );
            memoryInfoList.add( memoryInfo );
            memoryEventsFruID = FruIdEventsHelperUtil.addFruIDtoMemoryEvents( events, memoryInfoList );
            Map<String, String> eventData = memoryEventsFruID.get( 0 ).getBody().getData();
            assertNotNull( eventData.get( "fruID" ) );
        }
        catch ( Exception e )
        {
            logger.info( "Integrate FRU ID with Memory Events Test Failed" );
        }
    }

    /**
     * Unit test to Integrate FRU ID with Storage/HDD Events
     */
    @Test
    public void addFruIDtoStorageEventsTest()
    {
        List<Event> events = new ArrayList<Event>();
        List<Event> storageEventsFruID = new ArrayList<Event>();
        Event event = new Event();
        Body body = new Body();
        Header header = new Header();
        try
        {
            // Construct Storage/HDD Event for Testing
            body.setDescription( "Storage drive for rack EVO:RACK node N3 and HDD HDD_0 drive slot is full: DrivePresence" );
            Map<String, String> data = new HashMap<String, String>();
            data.put( "unit", "DISCRETE" );
            data.put( "eventName", "HDD_SLOT_FULL" );
            body.setData( data );
            Map<EventComponent, String> compIdentifier = new HashMap<EventComponent, String>();
            compIdentifier.put( EventComponent.STORAGE, "0" );
            compIdentifier.put( EventComponent.SERVER, "N5" );
            header.addComponentIdentifier( compIdentifier );
            header.setAgent( "HMS" );
            header.setEventName( EventCatalog.HDD_UP );
            header.setSeverity( EventSeverity.INFORMATIONAL );
            header.setVersion( "1.0" );
            event.setBody( body );
            event.setHeader( header );
            events.add( event );
            // Construct Storage/HDD FRU Information
            List<StorageInfo> storageInfoList = new ArrayList<StorageInfo>();
            StorageInfo storageInfo = new StorageInfo();
            ComponentIdentifier cpuComponentIdentifier = new ComponentIdentifier();
            cpuComponentIdentifier.setManufacturer( "ATA" );
            cpuComponentIdentifier.setProduct( "32G MLC SATADOM" );
            storageInfo.setComponentIdentifier( cpuComponentIdentifier );
            storageInfo.setDiskCapacityInMB( 29579 );
            storageInfo.setHostId( "N5" );
            storageInfo.setFruId( "-9080809091211679985" );
            storageInfo.setDiskType( "SSD" );
            storageInfo.setLocation( "0" );
            storageInfo.setId( "0" );
            storageInfoList.add( storageInfo );
            storageEventsFruID = FruIdEventsHelperUtil.addFruIDtoStorageEvents( events, storageInfoList );
            Map<String, String> eventData = storageEventsFruID.get( 0 ).getBody().getData();
            assertNotNull( eventData.get( "fruID" ) );
        }
        catch ( Exception e )
        {
            logger.info( "Integrate FRU ID with Storage or HDD Events Test Failed" );
        }
    }

    /**
     * Unit test to Integrate FRU ID with Storage Controller Events
     */
    @Test
    public void addFruIDtoStorageControllerEventsTest()
    {
        List<Event> events = new ArrayList<Event>();
        List<Event> storageControllerEventsFruID = new ArrayList<Event>();
        Event event = new Event();
        Body body = new Body();
        Header header = new Header();
        try
        {
            // Construct Storage Controllers Event for Testing
            body.setDescription( "Storage Adapter for the rack EVO:RACK node N3, Patsburg 4-Port SATA Storage Control Unit operational status is down" );
            Map<String, String> data = new HashMap<String, String>();
            data.put( "unit", "DISCRETE" );
            data.put( "eventName", "STORAGE_CONTROLLER_DOWN" );
            body.setData( data );
            Map<EventComponent, String> compIdentifier = new HashMap<EventComponent, String>();
            compIdentifier.put( EventComponent.STORAGE_CONTROLLER, "vmhba0 Patsburg 4-Port SATA Storage Control Unit" );
            compIdentifier.put( EventComponent.SERVER, "N5" );
            header.addComponentIdentifier( compIdentifier );
            header.setAgent( "HMS" );
            header.setEventName( EventCatalog.STORAGE_CONTROLLER_DOWN );
            header.setSeverity( EventSeverity.ERROR );
            header.setVersion( "1.0" );
            event.setBody( body );
            event.setHeader( header );
            events.add( event );
            // Construct Storage Controller FRU Information
            List<StorageController> StorageControllerList = new ArrayList<StorageController>();
            StorageController storageControllerInfo = new StorageController();
            ComponentIdentifier componentIdentifier = new ComponentIdentifier();
            componentIdentifier.setManufacturer( "Intel Corporation" );
            componentIdentifier.setProduct( "Patsburg 4-Port SATA Storage Control Unit" );
            storageControllerInfo.setComponentIdentifier( componentIdentifier );
            storageControllerInfo.setDeviceName( "vmhba0" );
            storageControllerInfo.setNumOfStorageDevicesConnected( 1 );
            storageControllerInfo.setDriver( "ahci" );
            storageControllerInfo.setFirmwareVersion( "23fh.56" );
            storageControllerInfo.setFruId( "944039473-157926983" );
            storageControllerInfo.setOperationalStatus( "offline" );
            StorageControllerList.add( storageControllerInfo );
            storageControllerEventsFruID =
                FruIdEventsHelperUtil.addFruIDtoStorageControllerEvents( events, StorageControllerList );
            Map<String, String> eventData = storageControllerEventsFruID.get( 0 ).getBody().getData();
            assertNotNull( eventData.get( "fruID" ) );
        }
        catch ( Exception e )
        {
            logger.info( "Integrate FRU ID with Storage Controller Events Test Failed" );
        }
    }

    /**
     * Unit test to Integrate FRU ID with Ethernet Controller or NIC Events
     */
    @Test
    public void addFruIDtoEthernetControllerEventsTest()
    {
        List<Event> events = new ArrayList<Event>();
        List<Event> ethernetControllerEventsFruID = new ArrayList<Event>();
        Event event = new Event();
        Body body = new Body();
        Header header = new Header();
        try
        {
            // Construct Ethernet Controller or NIC Event for Testing
            body.setDescription( "NIC for rack EVO:RACK node N5 Ethernet Controller {EC} and NIC vmnic2 Link status is down" );
            Map<String, String> data = new HashMap<String, String>();
            data.put( "unit", "DISCRETE" );
            data.put( "eventName", "NIC_LINK_DOWN" );
            body.setData( data );
            Map<EventComponent, String> compIdentifier = new HashMap<EventComponent, String>();
            compIdentifier.put( EventComponent.NIC, "vmnic0" );
            compIdentifier.put( EventComponent.SERVER, "N5" );
            header.addComponentIdentifier( compIdentifier );
            header.setAgent( "HMS" );
            header.setEventName( EventCatalog.NIC_LINK_DOWN );
            header.setSeverity( EventSeverity.ERROR );
            header.setVersion( "1.0" );
            event.setBody( body );
            event.setHeader( header );
            events.add( event );
            // Construct Ethernet Controller FRU Information
            List<EthernetController> ethernetControllerInfoList = new ArrayList<EthernetController>();
            List<PortInfo> portInfoList = new ArrayList<PortInfo>();
            PortInfo portInfo = new PortInfo();
            EthernetController ethernetController = new EthernetController();
            ComponentIdentifier ethernetControllerComponentIdentifier = new ComponentIdentifier();
            ethernetControllerComponentIdentifier.setManufacturer( "Intel Corporation" );
            ethernetControllerComponentIdentifier.setProduct( "I350 Gigabit Network Connection" );
            ethernetController.setComponentIdentifier( ethernetControllerComponentIdentifier );
            ethernetController.setFruId( "32023948731211679985" );
            ethernetController.setHostId( "N5" );
            ethernetController.setSpeedInMbps( "1000" );
            ethernetController.setFirmwareVersion( "1.61, 0x80000919" );
            portInfo.setMacAddress( "c4:54:44:72:c5:d4" );
            portInfo.setDeviceName( "vmnic0" );
            portInfoList.add( portInfo );
            ethernetController.setPortInfos( portInfoList );
            ethernetControllerInfoList.add( ethernetController );
            ethernetControllerEventsFruID =
                FruIdEventsHelperUtil.addFruIDtoEthernetControllerEvents( events, ethernetControllerInfoList );
            Map<String, String> eventData = ethernetControllerEventsFruID.get( 0 ).getBody().getData();
            assertNotNull( eventData.get( "fruID" ) );
        }
        catch ( Exception e )
        {
            logger.info( "Integrate FRU ID with Ethernet Controller Events Test Failed" );
        }
    }

    /**
     * Unit test to Integrate FRU ID with Host Events
     */
    @Test
    public void addFruIDtoHostEventsTest()
    {
        List<Event> events = new ArrayList<Event>();
        List<Event> hostEventsFruID = new ArrayList<Event>();
        Event event = new Event();
        Body body = new Body();
        Header header = new Header();
        try
        {
            // Construct SYSTEM Event for Testing
            body.setDescription( "System Status for rack EVO:RACK node N5 System PCIe Slot1 health StateDeasserted" );
            Map<String, String> data = new HashMap<String, String>();
            data.put( "unit", "DISCRETE" );
            data.put( "eventName", "SYSTEM_STATUS" );
            body.setData( data );
            Map<EventComponent, String> compIdentifier = new HashMap<EventComponent, String>();
            compIdentifier.put( EventComponent.SYSTEM, "PCIe Slot1" );
            compIdentifier.put( EventComponent.SERVER, "N5" );
            header.addComponentIdentifier( compIdentifier );
            header.setAgent( "HMS" );
            // header.setEventName(EventCatalog.SYSTEM_STATUS);
            header.setSeverity( EventSeverity.INFORMATIONAL );
            header.setVersion( "1.0" );
            event.setBody( body );
            event.setHeader( header );
            events.add( event );
            String serverFruID = "1676845885";
            hostEventsFruID = FruIdEventsHelperUtil.addFruIDtoHostEvents( events, serverFruID );
            Map<String, String> eventData = hostEventsFruID.get( 0 ).getBody().getData();
            assertNotNull( eventData.get( "fruID" ) );
        }
        catch ( Exception e )
        {
            logger.info( "Integrate FRU ID with Host Events Test Failed" );
        }
    }
}
