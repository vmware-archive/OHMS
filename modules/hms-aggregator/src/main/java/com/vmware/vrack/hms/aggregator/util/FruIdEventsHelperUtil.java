/* ********************************************************************************
 * FruIdEventsHelperUtil.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.aggregator.HostDataAggregator;
import com.vmware.vrack.hms.aggregator.ServerComponentAggregator;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.rest.model.CpuInfo;
import com.vmware.vrack.hms.common.rest.model.EthernetController;
import com.vmware.vrack.hms.common.rest.model.MemoryInfo;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.rest.model.StorageController;
import com.vmware.vrack.hms.common.rest.model.StorageInfo;
import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.inventory.InventoryLoader;

/**
 * Helper class to Integrate/Add the FRU ID with the Events
 */
public class FruIdEventsHelperUtil
{
    private static Logger logger = Logger.getLogger( FruIdEventsHelperUtil.class );

    /**
     * Helper Method to Integrate/Add the FRU ID with the CPU Events Header
     *
     * @param aggregatedEvents
     * @param nodeID
     * @param serverComponent
     * @return List<Event>
     * @throws HmsException
     */
    public static List<Event> addFruIDtoEvents( List<Event> aggregatedEvents, String nodeID,
                                                ServerComponent serverComponent )
        throws HmsException
    {
        ServerInfoHelperUtil serverInfoHelperUtil = new ServerInfoHelperUtil();
        ServerInfo serverInfo = new ServerInfo();
        List<Event> events = new ArrayList<Event>();
        try
        {
            ServerNode serverNode = InventoryLoader.getInstance().getNodeMap().get( nodeID );
            switch ( serverComponent )
            {
                case CPU:
                    if ( serverNode.getCpuInfo() == null )
                    {
                        ServerComponentAggregator aggregator = new ServerComponentAggregator();
                        aggregator.setServerComponentInfo( serverNode, ServerComponent.CPU );
                    }
                    events =
                        addFruIDtoCpuEvents( aggregatedEvents, serverInfoHelperUtil.convertToFruCpuInfo( serverNode ) );
                    return events;
                case MEMORY:
                    if ( serverNode.getPhysicalMemoryInfo() == null )
                    {
                        ServerComponentAggregator aggregator = new ServerComponentAggregator();
                        aggregator.setServerComponentInfo( serverNode, ServerComponent.MEMORY );
                    }
                    events =
                        addFruIDtoMemoryEvents( aggregatedEvents,
                                                serverInfoHelperUtil.convertToFruMemoryInfo( serverNode ) );
                    return events;
                case STORAGE:
                    if ( serverNode.getHddInfo() == null )
                    {
                        ServerComponentAggregator aggregator = new ServerComponentAggregator();
                        aggregator.setServerComponentInfo( serverNode, ServerComponent.STORAGE );
                    }
                    events =
                        addFruIDtoStorageEvents( aggregatedEvents,
                                                 serverInfoHelperUtil.convertFruStorageInfo( serverNode ) );
                    return events;
                case STORAGE_CONTROLLER:
                    if ( serverNode.getStorageControllerInfo() == null )
                    {
                        ServerComponentAggregator aggregator = new ServerComponentAggregator();
                        aggregator.setServerComponentInfo( serverNode, ServerComponent.STORAGE_CONTROLLER );
                    }
                    events =
                        addFruIDtoStorageControllerEvents( aggregatedEvents,
                                                           serverInfoHelperUtil.convertToFruStorageControllerInfo( serverNode ) );
                    return events;
                case NIC:
                    if ( serverNode.getEthernetControllerList() == null )
                    {
                        ServerComponentAggregator aggregator = new ServerComponentAggregator();
                        aggregator.setServerComponentInfo( serverNode, ServerComponent.NIC );
                    }
                    events =
                        addFruIDtoEthernetControllerEvents( aggregatedEvents,
                                                            serverInfoHelperUtil.convertToFruNICInfo( serverNode ) );
                    return events;
                case SYSTEM:
                case BMC:
                case SERVER:
                    serverInfo = serverInfoHelperUtil.convertServerNodeToServerInfo( serverNode );
                    String serverFruID = serverInfo.getFruId();
                    events = addFruIDtoHostEvents( aggregatedEvents, serverFruID );
                    return events;
            }
            return aggregatedEvents;
        }
        catch ( Exception e )
        {
            throw new HmsException( "HMS aggregator error in adding Fru ID to Events for node " + nodeID, e );
        }
    }

    /**
     * Helper Method to Integrate/Add the FRU ID with the Switch Events
     *
     * @param aggregatedSwitchEvents
     * @param switchID
     * @return List<Event>
     * @throws HmsException
     */
    public static List<Event> addFruIDtoSwitchEventsHelper( List<Event> aggregatedSwitchEvents, String switchID )
        throws HmsException
    {
        try
        {
            if ( aggregatedSwitchEvents.size() != 0 )
            {
                List<Event> events = new ArrayList<Event>();
                HostDataAggregator aggregator = new HostDataAggregator();
                SwitchInfo switchInfo = aggregator.getSwitchNodeOOBData( switchID );
                if ( switchInfo == null )
                    return aggregatedSwitchEvents;
                events = addFruIDtoSwitchEvents( aggregatedSwitchEvents, switchInfo.getFruId() );
                return events;
            }
            return aggregatedSwitchEvents;
        }
        catch ( Exception e )
        {
            throw new HmsException( "HMS aggregator error in adding Fru ID to Events for node " + switchID, e );
        }
    }

    /**
     * Add FRU ID to CPU aggregated Events
     *
     * @param aggregatedEvents
     * @param cpuInfoList
     * @return List<Event>
     */
    public static List<Event> addFruIDtoCpuEvents( List<Event> aggregatedEvents, List<CpuInfo> cpuInfoList )
    {
        List<Event> events = new ArrayList<Event>();
        CpuInfo cpuInfo = new CpuInfo();
        try
        {
            for ( int i = 0; i < aggregatedEvents.size(); i++ )
            {
                Event event = aggregatedEvents.get( i );
                Map<EventComponent, String> eventComponentIdentifier = event.getHeader().getComponentIdentifier();
                Map<String, String> eventBody = event.getBody().getData();
                String componentId = eventComponentIdentifier.get( EventComponent.CPU );
                for ( int j = 0; j < cpuInfoList.size(); j++ )
                {
                    if ( componentId.contains( cpuInfoList.get( j ).getLocation() ) )
                    {
                        cpuInfo = cpuInfoList.get( j );
                        eventBody.put( "fruID", cpuInfo.getFruId() );
                        break;
                    }
                }
                events.add( event );
            }
            return events;
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator error in adding Fru ID to CPU Events", e );
        }
        return null;
    }

    /**
     * Add FRU ID to Memory aggregated Events
     *
     * @param aggregatedEvents
     * @param memoryInfoList
     * @return List<Event>
     */
    public static List<Event> addFruIDtoMemoryEvents( List<Event> aggregatedEvents, List<MemoryInfo> memoryInfoList )
    {
        List<Event> events = new ArrayList<Event>();
        MemoryInfo memoryInfo = new MemoryInfo();
        try
        {
            for ( int i = 0; i < aggregatedEvents.size(); i++ )
            {
                Event event = aggregatedEvents.get( i );
                Map<EventComponent, String> eventComponentIdentifier = event.getHeader().getComponentIdentifier();
                Map<String, String> eventBody = event.getBody().getData();
                String componentId = eventComponentIdentifier.get( EventComponent.MEMORY );
                for ( int j = 0; j < memoryInfoList.size(); j++ )
                {
                    if ( componentId.contains( memoryInfoList.get( j ).getLocation() ) )
                    {
                        memoryInfo = memoryInfoList.get( j );
                        eventBody.put( "fruID", memoryInfo.getFruId() );
                        break;
                    }
                }
                events.add( event );
            }
            return events;
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator error in adding Fru ID to Memory Events", e );
        }
        return null;
    }

    /**
     * Add FRU ID to Storage/HDD aggregated Events
     *
     * @param aggregatedEvents
     * @param storageInfoList
     * @return List<Event>
     */
    public static List<Event> addFruIDtoStorageEvents( List<Event> aggregatedEvents, List<StorageInfo> storageInfoList )
    {
        List<Event> events = new ArrayList<Event>();
        StorageInfo storageInfo = new StorageInfo();
        try
        {
            for ( int i = 0; i < aggregatedEvents.size(); i++ )
            {
                Event event = aggregatedEvents.get( i );
                Map<EventComponent, String> eventComponentIdentifier = event.getHeader().getComponentIdentifier();
                Map<String, String> eventBody = event.getBody().getData();
                String componentId = eventComponentIdentifier.get( EventComponent.STORAGE );
                for ( int j = 0; j < storageInfoList.size(); j++ )
                {
                    // String deviceProductLocation = storageInfoList.get(j).getLocation() + " "
                    // + storageInfoList.get(j).getComponentIdentifier().getProduct();
                    if ( componentId.equals( storageInfoList.get( j ).getId() ) )
                    {
                        storageInfo = storageInfoList.get( j );
                        eventBody.put( "fruID", storageInfo.getFruId() );
                        break;
                    }
                }
                events.add( event );
            }
            return events;
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator error in adding Fru ID to Storage or HDD Events", e );
        }
        return null;
    }

    /**
     * Add FRU ID to Storage Controller Events
     *
     * @param aggregatedEvents
     * @param storageControllerList
     * @return List<Event>
     */
    public static List<Event> addFruIDtoStorageControllerEvents( List<Event> aggregatedEvents,
                                                                 List<StorageController> storageControllerList )
    {
        List<Event> events = new ArrayList<Event>();
        StorageController storageController = new StorageController();
        try
        {
            for ( int i = 0; i < aggregatedEvents.size(); i++ )
            {
                Event event = aggregatedEvents.get( i );
                Map<EventComponent, String> eventComponentIdentifier = event.getHeader().getComponentIdentifier();
                Map<String, String> eventBody = event.getBody().getData();
                String componentId = eventComponentIdentifier.get( EventComponent.STORAGE_CONTROLLER );
                for ( int j = 0; j < storageControllerList.size(); j++ )
                {
                    String deviceNameProduct =
                        storageControllerList.get( j ).getDeviceName() + " "
                            + storageControllerList.get( j ).getComponentIdentifier().getProduct();
                    if ( componentId.equals( deviceNameProduct ) )
                    {
                        storageController = storageControllerList.get( j );
                        eventBody.put( "fruID", storageController.getFruId() );
                        break;
                    }
                }
                events.add( event );
            }
            return events;
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator error in adding Fru ID to Storage Controller Events", e );
        }
        return null;
    }

    /**
     * Add FRU ID to Ethernet Controller aggregated Events
     *
     * @param aggregatedEvents
     * @param ethernetControllerList
     * @return List<Event>
     */
    public static List<Event> addFruIDtoEthernetControllerEvents( List<Event> aggregatedEvents,
                                                                  List<EthernetController> ethernetControllerList )
    {
        List<Event> events = new ArrayList<Event>();
        EthernetController ethernetController = new EthernetController();
        try
        {
            for ( int i = 0; i < aggregatedEvents.size(); i++ )
            {
                Event event = aggregatedEvents.get( i );
                Map<EventComponent, String> eventComponentIdentifier = event.getHeader().getComponentIdentifier();
                Map<String, String> eventBody = event.getBody().getData();
                String componentId = eventComponentIdentifier.get( EventComponent.NIC );
                boolean foundEthernetControllerFru = false;
                for ( int j = 0; j < ethernetControllerList.size() && foundEthernetControllerFru == false; j++ )
                {
                    for ( int k = 0; k < ethernetControllerList.get( j ).getPortInfos().size(); k++ )
                    {
                        if ( componentId.contains( ethernetControllerList.get( j ).getPortInfos().get( k ).getDeviceName() ) )
                        {
                            ethernetController = ethernetControllerList.get( j );
                            eventBody.put( "fruID", ethernetController.getFruId() );
                            foundEthernetControllerFru = true;
                            break;
                        }
                    }
                }
                events.add( event );
            }
            return events;
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator error in adding Fru ID to Ethernet Controller or NIC Events", e );
        }
        return null;
    }

    /**
     * Add FRU ID to Host/System/BMC related aggregated Events
     *
     * @param aggregatedEvents
     * @param serverFruID
     * @return List<Event>
     */
    public static List<Event> addFruIDtoHostEvents( List<Event> aggregatedEvents, String serverFruID )
    {
        List<Event> events = new ArrayList<Event>();
        try
        {
            for ( int i = 0; i < aggregatedEvents.size(); i++ )
            {
                Event event = aggregatedEvents.get( i );
                Map<String, String> eventBody = event.getBody().getData();
                eventBody.put( "fruID", serverFruID );
                events.add( event );
            }
            return events;
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator error in adding Fru ID to Host or BMC or SYSTEM Events", e );
        }
        return null;
    }

    /**
     * Add FRU ID to Switch related aggregated Events
     *
     * @param aggregatedEvents
     * @param switchFruID
     * @return List<Event>
     */
    public static List<Event> addFruIDtoSwitchEvents( List<Event> aggregatedEvents, String switchFruID )
    {
        List<Event> events = new ArrayList<Event>();
        try
        {
            for ( int i = 0; i < aggregatedEvents.size(); i++ )
            {
                Event event = aggregatedEvents.get( i );
                Map<String, String> eventBody = event.getBody().getData();
                eventBody.put( "fruID", switchFruID );
                events.add( event );
            }
            return events;
        }
        catch ( Exception e )
        {
            logger.error( "HMS aggregator error in adding Fru ID to Switch Events", e );
        }
        return null;
    }
}
