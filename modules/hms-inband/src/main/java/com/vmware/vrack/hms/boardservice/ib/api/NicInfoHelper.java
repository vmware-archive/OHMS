/* ********************************************************************************
 * NicInfoHelper.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.boardservice.ib.api;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVReader;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.vmware.vim.binding.vim.HostSystem;
import com.vmware.vim.binding.vim.host.HardwareInfo;
import com.vmware.vim.binding.vim.host.NetworkInfo;
import com.vmware.vim.binding.vim.host.NetworkSystem;
import com.vmware.vim.binding.vim.host.PciDevice;
import com.vmware.vim.binding.vim.host.PhysicalNic;
import com.vmware.vim.binding.vim.host.PhysicalNic.LinkSpeedDuplex;
import com.vmware.vrack.hms.boardservice.ib.InbandServiceImpl;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.SpeedInfo;
import com.vmware.vrack.hms.common.servernodes.api.SpeedUnit;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.servernodes.api.nic.NicStatisticsInfo;
import com.vmware.vrack.hms.common.servernodes.api.nic.NicStatus;
import com.vmware.vrack.hms.common.servernodes.api.nic.PortInfo;
import com.vmware.vrack.hms.common.util.EsxiSshUtil;
import com.vmware.vrack.hms.vsphere.HostProxy;

@Component
public class NicInfoHelper
{
    private static Logger logger = LoggerFactory.getLogger( NicInfoHelper.class );

    private static final String VENDOR = "vendor";

    private static final String DEVICEID = "deviceid";

    private static final String PRODUCT = "product";

    private static final String NIC_RECEIVED_PACKETS = "Packetsreceived";

    private static final String NIC_TRANSMITTED_PACKETS = "Packetssent";

    private static final String NIC_RECEIVE_PACKET_DROPS = "Receivepacketsdropped";

    private static final String NIC_TRANSMIT_PACKET_DROPS = "Transmitpacketsdropped";

    private static final String NIC_FIRMWARE_DRIVER_INFO = "DriverInfo";

    private static final String PORT_UP = " port up";

    private static final String PORT_DOWN = " port down";

    private static final String PORT = " port ";

    /**
     * Configurable Threshold value for NIC Packet Drop
     */
    private static float nicPacketDropThreshold;

    @Value( "${nic.packet.drop.threshold:1.0f}" )
    public void setNicPacketDropThreshold( float nicPacketDropThreshold )
    {
        NicInfoHelper.nicPacketDropThreshold = nicPacketDropThreshold;
    }

    /**
     * Returns List of EthernetController containing list of Nic Ports.
     *
     * @param hostProxy
     * @param nodeId
     * @return
     * @throws Exception
     */
    public static List<EthernetController> getNicInfo( HostProxy hostProxy, ServiceServerNode node )
        throws Exception
    {
        if ( hostProxy != null && node != null )
        {
            Map<String, List<PortInfo>> portMap = new HashMap<String, List<PortInfo>>();
            Map<String, String> mapfirmwareVersion = new HashMap<String, String>();
            Map<String, String> mapLinkSpeed = new HashMap<String, String>();
            String maxLinkSpeed;
            String firmwareVersion = null;
            // List of pciDeviceInfoMap, which we will be using to group Nics
            // and Vendor name and Product Name
            Map<String, Map<String, String>> pciDeviceInfoMap = new HashMap<>();
            NetworkSystem networkSystem = hostProxy.getNetworkSystemObj();
            if ( networkSystem != null )
            {
                NetworkInfo networkInfo = networkSystem.getNetworkInfo();
                if ( networkInfo != null )
                {
                    PhysicalNic[] physicalNics = networkInfo.getPnic();
                    if ( physicalNics != null )
                    {
                        for ( PhysicalNic pnic : physicalNics )
                        {
                            PortInfo info = new PortInfo();
                            info.setMacAddress( pnic.getMac() );
                            info.setDeviceName( pnic.getDevice() );
                            // info.setDriver(pnic.getDriver());
                            if ( pnic.getLinkSpeed() != null )
                            {
                                SpeedInfo speed =
                                    new SpeedInfo( (long) pnic.getLinkSpeed().getSpeedMb(), SpeedUnit.Mbps );
                                info.setLinkSpeedInMBps( speed );
                            }
                            info.setLinkStatus( ( pnic.getLinkSpeed() != null ) ? NicStatus.OK
                                            : NicStatus.DISCONNECTED );
                            LinkSpeedDuplex[] linkSpeeds = pnic.getValidLinkSpecification();
                            // maxLinkSpeed = new
                            // SpeedInfo(getMaxLinkSpeed(linkSpeeds),
                            // SpeedUnit.Mbps);
                            maxLinkSpeed = Long.toString( getMaxLinkSpeed( linkSpeeds ) );
                            Session session = null;
                            try
                            {
                                try
                                {
                                    logger.debug( "Trying to logon to ssh termninal for node:"
                                        + ( node != null ? node.getNodeID() : null ) );
                                    session = getSession( node );
                                }
                                catch ( HmsException | JSchException e )
                                {
                                    logger.error( "Cannot create ssh session Object for node : "
                                        + ( node != null ? node.getNodeID() : null ), e );
                                }
                                try
                                {
                                    firmwareVersion =
                                        getNicFirmwareVersion( pnic.getDevice(), session, node.getNodeID() );
                                }
                                catch ( Exception e )
                                {
                                    String err =
                                        String.format( "Unable to get firmware Info for nic [ %s ] of node [ %s ]",
                                                       pnic.getDevice(), node.getNodeID() );
                                    logger.debug( err, e );
                                }
                            }
                            finally
                            {
                                destroySession( session );
                            }
                            Map<String, String> pciInfo = null;
                            try
                            {
                                pciInfo = ( getPciDeviceInfo( pnic.getPci(), hostProxy ) );
                            }
                            catch ( HmsException e )
                            {
                                logger.error( "Error getting Pci Device Id for physical Nic: " + pnic.getDevice() );
                            }
                            String key = ( pciInfo != null ) ? pciInfo.get( DEVICEID ) : null;
                            // Prepare Map with Nics with same DeviceId under
                            // one ethernetController
                            if ( portMap.containsKey( key ) )
                            {
                                List<PortInfo> nicList = portMap.get( key );
                                nicList.add( info );
                            }
                            else
                            {
                                List<PortInfo> nicList = new ArrayList<PortInfo>();
                                nicList.add( info );
                                portMap.put( key, nicList );
                                mapfirmwareVersion.put( key, firmwareVersion );
                                mapLinkSpeed.put( key, maxLinkSpeed );
                            }
                            // put into pciDeviceMap
                            if ( !pciDeviceInfoMap.containsKey( key ) )
                            {
                                pciDeviceInfoMap.put( key, pciInfo );
                            }
                        }
                    }
                }
            }
            return prepareEthernetControllers( portMap, pciDeviceInfoMap, mapfirmwareVersion, mapLinkSpeed );
        }
        else
        {
            throw new Exception( "Can not get Nic Info because the Host Proxy object is NULL" );
        }
    }

    /**
     * Returns firmware version of NIC
     *
     * @param nicDeviceName
     * @param session
     * @return
     * @throws Exception
     */
    private static String getNicFirmwareVersion( String nicDeviceName, Session session, String nodeId )
        throws Exception
    {
        logger.debug( "Trying to get Nic Firmware data on node [ " + nodeId + " ] for Nic [ " + nicDeviceName + " ]" );
        String firmwareInfo = null;
        if ( nicDeviceName != null && !"".equals( nicDeviceName.trim() ) && session != null )
        {
            String command = String.format( Constants.GET_NIC_FIRMWARE_INFO_COMMAND, nicDeviceName.trim() );
            // Result will be received in csv format after executing following
            // command
            // AdvertisedAutoNegotiation,AdvertisedLinkModes,AutoNegotiation,CableType,CurrentMessageLevel,DriverInfo,LinkDetected,
            // LinkStatus,Name,PHYAddress,PauseAutonegotiate,PauseRX,PauseTX,SupportedPorts,SupportsAutoNegotiation,SupportsPause,
            // SupportsWakeon,Transceiver,Wakeon,
            // true,"10baseT/Half,10baseT/Full,100baseT/Half,100baseT/Full,1000baseT/Full,",true,Twisted
            // Pair,7,"0000:02:00.0,igb,""1.48, 0x800006e7"",5.0.5.1,",true,Up
            // ,vmnic0,1,true,false,false,"TP,",true,true,true,internal,"MagicPacket(tm),",
            String result = EsxiSshUtil.executeCommand( session, command );
            // if result String ends with '\n' We need to remove that extra
            // character,
            // else it will appear as last character while parsing
            if ( result.endsWith( "\n" ) )
            {
                result = result.substring( 0, result.length() - 1 );
            }
            logger.debug( "Nic details [ " + nicDeviceName + " ] as CSV: " + result );
            CSVReader csvReader = null;
            try
            {
                // convert String into InputStream
                InputStream is = new ByteArrayInputStream( result.getBytes() );
                // read it with BufferedReader
                BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
                csvReader = new CSVReader( br );
                // read all lines at once
                List<String[]> records = csvReader.readAll();
                Iterator<String[]> iterator = records.iterator();
                // skip header row
                String[] headerRow = iterator.next();
                List<String> headers = Arrays.asList( headerRow );
                // Calculate their indexes in the response array
                int driverInfoIndex = getIndexInArray( headers, NIC_FIRMWARE_DRIVER_INFO );
                while ( iterator.hasNext() )
                {
                    String[] record = iterator.next();
                    if ( record.length > driverInfoIndex )
                    {
                        // Again a csv, have to parse this one too
                        String driverInfoCSV = record[driverInfoIndex];
                        logger.debug( "NIC Driver info: Nic [ " + nicDeviceName + " ], Node [ " + nodeId
                            + " ], DriverInfo CSV: [ " + driverInfoCSV + " ]" );
                        InputStream iStream = new ByteArrayInputStream( driverInfoCSV.getBytes() );
                        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( iStream ) );
                        csvReader = new CSVReader( bufferedReader );
                        String[] nicDriverRecords = csvReader.readNext();
                        List<String> nicDriverInfo = Arrays.asList( nicDriverRecords );
                        if ( nicDriverInfo.size() > 2 )
                        {
                            firmwareInfo = nicDriverInfo.get( 2 );
                            logger.debug( "FirmwareInfo for nic [ " + nicDeviceName + " ] on node [ " + nodeId + " ] : "
                                + firmwareInfo );
                            return firmwareInfo;
                        }
                    }
                }
            }
            catch ( Exception e )
            {
                logger.error( "Error while parsing CSV from the result for Nic Driver Info for nic [ " + nicDeviceName
                    + " ] on node [ " + nodeId + " ] : ", e );
            }
            finally
            {
                if ( csvReader != null )
                {
                    csvReader.close();
                }
            }
            return firmwareInfo;
        }
        else
        {
            throw new HmsException( "Cannot get Nic firmware info on node [ " + nodeId + " ] for Nic [ " + nicDeviceName
                + "] and Session Object [" + session + " ]" );
        }
    }

    /**
     * PCI device Id for the pciHash received in physical nic is found in PciDevice Object under Hardware Info So trying
     * to get PCi device Id by iterating all PCI devices under HardwareInfo
     *
     * @param pciHash
     * @param hostProxy
     * @return
     * @throws HmsException
     */
    private static Map<String, String> getPciDeviceInfo( String pciHash, HostProxy hostProxy )
        throws HmsException
    {
        Map<String, String> pciDeviceInfo = new HashMap<>();
        if ( pciHash != null && !"".equals( pciHash ) && hostProxy != null )
        {
            HostSystem hostSystem = hostProxy.getHostSystem();
            if ( hostSystem != null )
            {
                HardwareInfo hardwareInfo = hostSystem.getHardware();
                if ( hardwareInfo != null )
                {
                    for ( PciDevice device : hardwareInfo.getPciDevice() )
                    {
                        String pciDeviceId = null;
                        if ( device.getId() != null && device.getId().equals( pciHash ) )
                        {
                            pciDeviceId =
                                Byte.toString( device.getBus() ).concat( ":" ).concat( Byte.toString( device.getSlot() ) );
                            String pciManufacturer = device.getVendorName();
                            String pciProductName = device.getDeviceName();
                            pciDeviceInfo.put( DEVICEID, pciDeviceId );
                            pciDeviceInfo.put( VENDOR, pciManufacturer );
                            pciDeviceInfo.put( PRODUCT, pciProductName );
                            return pciDeviceInfo;
                        }
                    }
                }
            }
            else
            {
                logger.error( "Host System Object was found null when trying to get PCI Device Id for Nic" );
                throw new HmsException( "Error while trying to get PCI Device info for Nic" );
            }
            return pciDeviceInfo;
        }
        else
        {
            logger.error( "Either pciHash or Host Proxy is Null. pciHash : " + pciHash );
            throw new HmsException( "Error while trying to get PCI Device info for Nic" );
        }
    }

    /**
     * Returns the Maximum link speed any Nic can support
     *
     * @param linkSpeedDuplexList
     * @return
     */
    public static long getMaxLinkSpeed( LinkSpeedDuplex[] linkSpeedDuplexList )
    {
        long maxSpeed = 0;
        if ( linkSpeedDuplexList != null )
        {
            for ( LinkSpeedDuplex linkSpeed : linkSpeedDuplexList )
            {
                if ( linkSpeed != null )
                {
                    if ( maxSpeed < linkSpeed.getSpeedMb() )
                    {
                        maxSpeed = linkSpeed.getSpeedMb();
                    }
                }
            }
        }
        return maxSpeed;
    }

    /**
     * Tries to create List of EthernetControllers, by grouping Nics with same PCI Device Id from list of NicInfos and
     * gets EthernetController Manufacturer and Product name
     *
     * @param nicMap
     * @param pciDeviceInfoMap
     * @return
     */
    private static List<EthernetController> prepareEthernetControllers( Map<String, List<PortInfo>> nicMap,
                                                                        Map<String, Map<String, String>> pciDeviceInfoMap,
                                                                        Map<String, String> firmwareVersionMap,
                                                                        Map<String, String> linkSpeedMap )
    {
        List<EthernetController> ethernetControllers = new ArrayList<>();
        if ( nicMap != null )
        {
            for ( String key : nicMap.keySet() )
            {
                EthernetController controller = new EthernetController();
                ComponentIdentifier componentIdentifier = new ComponentIdentifier();
                List<PortInfo> nicInfos = nicMap.get( key );
                controller.setPortInfos( nicInfos );
                // controller.setNumberOfPorts(nicInfos.size());
                controller.setSpeedInMbps( linkSpeedMap.get( key ) );
                controller.setFirmwareVersion( firmwareVersionMap.get( key ) );
                if ( pciDeviceInfoMap != null && pciDeviceInfoMap.containsKey( key )
                    && pciDeviceInfoMap.get( key ) != null )
                {
                    componentIdentifier.setManufacturer( pciDeviceInfoMap.get( key ).get( VENDOR ) );
                    componentIdentifier.setProduct( pciDeviceInfoMap.get( key ).get( PRODUCT ) );
                    controller.setComponentIdentifier( componentIdentifier );
                    controller.setPciDeviceId( pciDeviceInfoMap.get( key ).get( DEVICEID ) );
                }
                ethernetControllers.add( controller );
            }
        }
        return ethernetControllers;
    }

    /**
     * Get Nic Specific Sensor Data (as Nic Link Status Change, Nic Temperature above Threshold etc)
     *
     * @param serviceNode
     * @param component
     * @return
     * @throws HmsException
     */
    public static List<ServerComponentEvent> getNicSensor( ServiceHmsNode serviceNode, ServerComponent component,
                                                           InbandServiceImpl inbandServiceImpl )
                                                               throws HmsException
    {
        List<EthernetController> ethernetControllers = null;
        List<ServerComponentEvent> componentSensors = new ArrayList<ServerComponentEvent>();
        try
        {
            if ( inbandServiceImpl != null )
            {
                ethernetControllers = inbandServiceImpl.getNicInfo( serviceNode );
            }
            else
            {
                logger.error( "Error while getting NicInfo because InbandServiceImpl object was found "
                    + inbandServiceImpl );
                throw new HmsException( "Error while getting NicInfo because InbandServiceImpl object was found "
                    + inbandServiceImpl );
            }
        }
        catch ( HmsException e )
        {
            logger.error( "Unable to get EthernetControllers List while trying to get Nic Sensors for node ["
                + serviceNode != null ? serviceNode.getNodeID() : serviceNode + "]" );
            throw e;
        }
        if ( ethernetControllers != null )
        {
            Session session = null;
            ServiceServerNode node = null;
            try
            {
                node = (ServiceServerNode) serviceNode;
            }
            catch ( Exception e )
            {
                logger.error( "serviceNode is not an instance of ServiceServerNode: "
                    + ( node != null ? node.getNodeID() : node ) );
            }
            try
            {
                try
                {
                    logger.debug( "Trying to logon to ssh termninal for node:"
                        + ( node != null ? node.getNodeID() : node ) );
                    session = getSession( node );
                }
                catch ( HmsException | JSchException e )
                {
                    logger.error( "Cannot create ssh session Object for node : "
                        + ( node != null ? node.getNodeID() : node ), e );
                }
                for ( EthernetController controller : ethernetControllers )
                {
                    List<PortInfo> portInfos = controller.getPortInfos();
                    for ( PortInfo portInfo : portInfos )
                    {
                        try
                        {
                            // Currently generating Nic component Sensor based
                            // on Link Status
                            // If Link Status is DISCONNECTED, then it will
                            // generate Component Sensor Object
                            ServerComponentEvent componentSensor = new ServerComponentEvent();
                            String portStr = controller.getComponentIdentifier().getManufacturer() + " "
                                + controller.getComponentIdentifier().getProduct();
                            String componentId = portStr + PORT + portInfo.getDeviceName();
                            componentSensor.setComponentId( componentId );
                            componentSensor.setUnit( EventUnitType.DISCRETE );
                            if ( portInfo.getLinkStatus() == NicStatus.DISCONNECTED )
                            {
                                componentSensor.setDiscreteValue( Constants.NIC_PORT_DOWN_SENSOR_DISCRETE_VALUE );
                                componentSensor.setEventName( NodeEvent.NIC_PORT_DOWN );
                                componentSensor.setEventId( portStr + PORT_DOWN );
                                componentSensors.add( componentSensor );
                            }
                            else
                            {
                                componentSensor.setDiscreteValue( Constants.NIC_PORT_UP_SENSOR_DISCRETE_VALUE );
                                componentSensor.setEventName( NodeEvent.NIC_PORT_UP );
                                componentSensor.setEventId( portStr + PORT_UP );
                                componentSensors.add( componentSensor );
                            }
                            try
                            {
                                NicStatisticsInfo statisticsInfo =
                                    getNicStatistics( portInfo.getDeviceName(), session );
                                // Get List of Packet drop events (Receive +
                                // Transmit), if both have packet drop issues
                                List<ServerComponentEvent> packetDropEvents =
                                    getPacketDropEvents( componentId, statisticsInfo, portInfo );
                                componentSensors.addAll( packetDropEvents );
                            }
                            catch ( Exception e )
                            {
                                String err = "Exception while getting network packet drop info for nic [ "
                                    + portInfo.getDeviceName() + " ]";
                                logger.error( err );
                            }
                        }
                        catch ( Exception e )
                        {
                            logger.error( "Exception while getting Nic statistics data for [ "
                                + portInfo.getDeviceName() + " ] for node [ " + serviceNode != null
                                                ? serviceNode.getNodeID() : serviceNode + " ]" );
                        }
                    }
                }
            }
            finally
            {
                destroySession( session );
            }
        }
        else
        {
            logger.error( "Could Not find any Nic info on node [" + serviceNode != null ? serviceNode.getNodeID()
                            : serviceNode + "]" );
        }
        return componentSensors;
    }

    /**
     * Get Nic Statistics data for the specified data via esxi cli
     *
     * @param nicDeviceName
     * @param session
     * @return
     * @throws Exception
     */
    public static NicStatisticsInfo getNicStatistics( String nicDeviceName, Session session )
        throws Exception
    {
        logger.debug( "Trying to get Nic Statistics data for Nic: " + nicDeviceName );
        NicStatisticsInfo statisticsInfo = new NicStatisticsInfo();
        if ( nicDeviceName != null && !"".equals( nicDeviceName.trim() ) && session != null )
        {
            String command = String.format( Constants.GET_NIC_PACKET_DROP_INFO_COMMAND, nicDeviceName.trim() );
            // Result will be received in csv format after executing following
            // command
            // Bytesreceived,Bytessent,NICName,Packetsreceived,Packetssent,ReceiveCRCerrors,ReceiveFIFOerrors,Receiveframeerrors,
            // Receivelengtherrors,Receivemissederrors,Receiveovererrors,Receivepacketsdropped,Totalreceiveerrors,Totaltransmiterrors,
            // TransmitFIFOerrors,Transmitabortederrors,Transmitcarriererrors,Transmitheartbeaterrors,Transmitpacketsdropped,Transmitwindowerrors,
            // 1106636402154,1494164093163,vmnic0,1205726782,1376297918,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            String result = EsxiSshUtil.executeCommand( session, command );
            // if result String ends with '\n' We need to remove that extra
            // character,
            // else it will appear as last character while parsing
            if ( result.endsWith( "\n" ) )
            {
                result = result.substring( 0, result.length() - 1 );
            }
            logger.debug( result );
            CSVReader csvReader = null;
            try
            {
                // convert String into InputStream
                InputStream is = new ByteArrayInputStream( result.getBytes() );
                // read it with BufferedReader
                BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
                csvReader = new CSVReader( br );
                // read all lines at once
                List<String[]> records = csvReader.readAll();
                Iterator<String[]> iterator = records.iterator();
                // skip header row
                String[] headerRow = iterator.next();
                List<String> headers = Arrays.asList( headerRow );
                // Calculate their indexes in the response array
                int receivedPacketsIndex = getIndexInArray( headers, NIC_RECEIVED_PACKETS );
                int transmittedPacketsIndex = getIndexInArray( headers, NIC_TRANSMITTED_PACKETS );
                int receivePacketDropsIndex = getIndexInArray( headers, NIC_RECEIVE_PACKET_DROPS );
                int transmitPacketDropsIndex = getIndexInArray( headers, NIC_TRANSMIT_PACKET_DROPS );
                while ( iterator.hasNext() )
                {
                    // Nic Statistics records will contain total 21 fields
                    // Important ones are record[3]- packetsReceived, record[4]-
                    // transmittedPackets
                    // record[11]- receivePacketDrops, record[18]-
                    // transmitPacketDrops
                    String[] record = iterator.next();
                    if ( record.length > 19 )
                    {
                        statisticsInfo.setPacketsReceived( getLongValue( record[receivedPacketsIndex] ) );
                        statisticsInfo.setPacketsTransmitted( getLongValue( record[transmittedPacketsIndex] ) );
                        statisticsInfo.setReceivePacketDrops( getLongValue( record[receivePacketDropsIndex] ) );
                        statisticsInfo.setTransmitPacketDrops( getLongValue( record[transmitPacketDropsIndex] ) );
                    }
                }
            }
            catch ( Exception e )
            {
                logger.error( "Error while parsing CSV from the result for Nic: " + nicDeviceName, e );
            }
            finally
            {
                if ( csvReader != null )
                {
                    csvReader.close();
                }
            }
            return statisticsInfo;
        }
        else
        {
            throw new HmsException( "Cannot get Nic statistics info for Nic: " + nicDeviceName + " and Session Object: "
                + session );
        }
    }

    /**
     * Gets the array Index for the
     *
     * @param responseArray
     * @param inputString
     * @return
     */
    private static int getIndexInArray( List<String> responseArray, String inputString )
    {
        if ( responseArray != null && inputString != null && !"".equals( inputString.trim() ) )
        {
            for ( int i = 0; i < responseArray.size(); i++ )
            {
                String header = responseArray.get( i );
                if ( header != null && inputString.trim().equals( header.trim() ) )
                {
                    return i;
                }
            }
            throw new IllegalArgumentException( "Unable to get index for String [ " + inputString
                + " ] in response array" );
        }
        else
        {
            throw new IllegalArgumentException( "Unable to get index for String [ " + inputString
                + " ] in response array" );
        }
    }

    /**
     * Returns the parsed Long Value of String
     *
     * @param inputString
     * @return
     */
    private static Long getLongValue( String inputString )
    {
        if ( inputString != null && !"".equals( inputString.trim() ) )
        {
            try
            {
                Long longValue = new Long( inputString.trim() );
                return longValue;
            }
            catch ( Exception e )
            {
                return null;
            }
        }
        return null;
    }

    /**
     * Get ssh session object once for the node
     *
     * @param node
     * @return
     * @throws JSchException
     * @throws HmsException
     */
    public static Session getSession( ServiceServerNode node )
        throws JSchException, HmsException
    {
        if ( node != null && node.getIbIpAddress() != null && node.getOsUserName() != null
            && node.getOsPassword() != null )
        {
            Properties sessionConfig = new java.util.Properties();
            sessionConfig.put( "StrictHostKeyChecking", "no" );
            Session session = EsxiSshUtil.getSessionObject( node.getOsUserName(), node.getOsPassword(),
                                                            node.getIbIpAddress(), node.getSshPort(), sessionConfig );
            try
            {
                session.connect( 30000 );
                return session;
            }
            catch ( JSchException e )
            {
                logger.error( "Unable to create jsch CLI session: ", e );
                if ( session != null )
                {
                    session.disconnect();
                    session = null;
                }
                throw e;
            }
        }
        else
        {
            throw new HmsException( "Cannot Create SSh Session with host because one of the mandatory fields are NULL: "
                + ( node != null ? node.getIbIpAddress() : node ) );
        }
    }

    /**
     * Destroy ssh Session once completed
     *
     * @param session
     * @throws Exception
     */
    public static void destroySession( Session session )
    {
        try
        {
            if ( session != null )
                session.disconnect();
            session = null;
        }
        catch ( Exception e )
        {
            logger.error( "Unable to destroy SSH Session " + session );
        }
    }

    /**
     * Generate List of Packet Drop events
     *
     * @param statisticsInfo
     * @param controller
     * @param portInfo
     * @return
     */
    private static List<ServerComponentEvent> getPacketDropEvents( String componentId, NicStatisticsInfo statisticsInfo,
                                                                   PortInfo portInfo )
    {
        List<ServerComponentEvent> componentSensors = new ArrayList<ServerComponentEvent>();
        if ( statisticsInfo != null && portInfo != null )
        {
            Long rxPackets = statisticsInfo.getPacketsReceived();
            Long txPackets = statisticsInfo.getPacketsTransmitted();
            Long rxDropped = statisticsInfo.getReceivePacketDrops();
            Long txDropped = statisticsInfo.getTransmitPacketDrops();
            // Check if all are non null values and none of (received Packets +
            // receivePacket Drops) != 0, to prevent divide by zero exception
            if ( rxPackets != null && txPackets != null && rxDropped != null && txDropped != null )
            {
                float receiveDropPercentage = calculatePercentageDrop( rxDropped, rxPackets );
                float transmitDropPercentage = calculatePercentageDrop( txDropped, txPackets );
                ServerComponentEvent componentSensor = new ServerComponentEvent();
                componentSensor.setComponentId( componentId );
                componentSensor.setUnit( EventUnitType.PERCENT );
                if ( nicPacketDropThreshold < receiveDropPercentage )
                {
                    componentSensor.setValue( receiveDropPercentage );
                    componentSensor.setEventName( NodeEvent.NIC_PACKET_DROP_ABOVE_THRESHHOLD );
                    componentSensor.setEventId( "Receive Packet Drop Percentage" );
                    componentSensors.add( componentSensor );
                }
                if ( nicPacketDropThreshold < transmitDropPercentage )
                {
                    componentSensor.setValue( receiveDropPercentage );
                    componentSensor.setEventName( NodeEvent.NIC_PACKET_DROP_ABOVE_THRESHHOLD );
                    componentSensor.setEventId( "Transmit Packet Drop Percentage" );
                    componentSensors.add( componentSensor );
                }
            }
        }
        else
        {
            logger.debug( "Unable to generate Packet Drop Events for nic [ "
                + ( portInfo != null ? portInfo.getDeviceName() : null ) + " ]" );
        }
        return componentSensors;
    }

    /**
     * Calculate packet Drop percentage
     *
     * @param droppedPackets
     * @param successfulPackets
     * @return
     */
    private static float calculatePercentageDrop( long droppedPackets, long successfulPackets )
    {
        float dropPercentage = 0.0f;
        // To avoid divide by zero exception
        if ( droppedPackets + successfulPackets != 0 )
        {
            dropPercentage = ( (float) droppedPackets / ( (float) droppedPackets + (float) successfulPackets ) ) * 100;
        }
        return dropPercentage;
    }
}
