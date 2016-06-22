/* ********************************************************************************
 * HddInfoHelper.java
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.vmware.vim.binding.vim.HostSystem;
import com.vmware.vim.binding.vim.host.ConfigManager;
import com.vmware.vim.binding.vim.host.PlugStoreTopology;
import com.vmware.vim.binding.vim.host.PlugStoreTopology.Path;
import com.vmware.vim.binding.vim.host.ScsiDisk;
import com.vmware.vim.binding.vim.host.ScsiLun;
import com.vmware.vim.binding.vim.host.StorageSystem;
import com.vmware.vim.binding.vmodl.ManagedObjectReference;
import com.vmware.vrack.hms.boardservice.ib.InbandServiceImpl;
import com.vmware.vrack.hms.common.StatusEnum;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddSMARTData;
import com.vmware.vrack.hms.common.util.EsxiSshUtil;
import com.vmware.vrack.hms.vsphere.VsphereClient;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Yagnesh Chawda
 */
public class HddInfoHelper
{
    private static Logger logger = LoggerFactory.getLogger( HddInfoHelper.class );

    private static final String UNAVAILABLE = "unavailable";

    public static List<HddInfo> getHddInfo( HostSystem hostSystem, VsphereClient client, ServiceServerNode node )
        throws Exception
    {
        if ( hostSystem != null && hostSystem.getConfigManager() != null )
        {
            List<HddInfo> hddInfos = new ArrayList<>();
            ConfigManager configManager = hostSystem.getConfigManager();
            ManagedObjectReference ssmor = configManager.getStorageSystem();
            if ( ssmor != null )
            {
                StorageSystem ss = client.createStub( StorageSystem.class, ssmor );
                if ( ss != null && ss.getStorageDeviceInfo() != null )
                {
                    Map<String, Path> plugStoreTopologyPathMap = new HashMap<>();
                    PlugStoreTopology plugStoreTopology = ss.getStorageDeviceInfo().getPlugStoreTopology();
                    if ( plugStoreTopology != null )
                    {
                        for ( Path path : plugStoreTopology.getPath() )
                        {
                            String key = path.getDevice();
                            if ( key != null )
                            {
                                plugStoreTopologyPathMap.put( key, path );
                            }
                        }
                    }
                    for ( ScsiLun lun : ss.getStorageDeviceInfo().getScsiLun() )
                    {
                        HddInfo hddInfo = new HddInfo();
                        ComponentIdentifier hddComponentIdentifier = new ComponentIdentifier();
                        ScsiDisk sd = null;
                        // Because we are getting some data which are NOT truely speaking Drives in this listing being
                        // it can be some caching storage devices.
                        // So filtering them out.
                        if ( "disk".equalsIgnoreCase( lun.getDeviceType() ) )
                        {
                            if ( lun instanceof ScsiDisk )
                            {
                                sd = (ScsiDisk) lun;
                                if ( sd.getSsd() )
                                {
                                    hddInfo.setType( "SSD" );
                                }
                                else
                                {
                                    hddInfo.setType( "HDD" );
                                }
                                if ( sd.getCapacity() != null )
                                {
                                    long size = sd.getCapacity().getBlock() * sd.getCapacity().getBlockSize();
                                    long sizeInMB = ( size / ( 1024 * 1024 ) );
                                    hddInfo.setDiskCapacityInMB( sizeInMB );
                                }
                                if ( sd.getOperationalState() != null && sd.getOperationalState().length > 0 )
                                {
                                    hddInfo.setState( StatusEnum.getHddState( sd.getOperationalState()[( sd.getOperationalState().length )
                                        - 1] ) );
                                }
                                if ( sd.getUuid() != null )
                                {
                                    for ( String pathKey : plugStoreTopologyPathMap.keySet() )
                                    {
                                        if ( pathKey.contains( sd.getUuid() ) )
                                        {
                                            Path path = plugStoreTopologyPathMap.get( pathKey );
                                            if ( path != null && path.getTargetNumber() != null )
                                            {
                                                hddInfo.setLocation( String.valueOf( path.getTargetNumber() ) );
                                                hddInfo.setId( String.valueOf( path.getTargetNumber() ) );
                                            }
                                        }
                                    }
                                }
                                // Set Canonical Name here. Will be required to map Smart Data to particular Hdd
                                hddInfo.setName( sd.getCanonicalName() );
                                // setting revision from scsiLun as firmware for HDD.
                                hddInfo.setFirmwareInfo( sd.getRevision() );
                                // Setting the manufacturer, product model
                                hddComponentIdentifier.setManufacturer( sd.getVendor() );
                                hddComponentIdentifier.setProduct( sd.getModel() );
                                if ( !sd.getSerialNumber().equals( UNAVAILABLE ) && sd.getSerialNumber() != null
                                    && !sd.getSerialNumber().equals( "" ) )
                                    hddComponentIdentifier.setSerialNumber( sd.getSerialNumber() );
                                hddInfo.setComponentIdentifier( hddComponentIdentifier );
                                hddInfos.add( hddInfo );
                            }
                        }
                    }
                }
                else
                {
                    throw new HmsException( "No HDD information found." );
                }
            }
            return hddInfos;
        }
        else
        {
            throw new Exception( "Can not get Hdd Info because the Host Proxy Object is NULL" );
        }
    }

    /**
     * Get HDD Specific SMART Data
     *
     * @param diskName
     * @param session
     * @return
     * @throws Exception
     */
    public static List<HddSMARTData> getHddSmartDataInfo( String diskName, Session session )
        throws Exception
    {
        logger.debug( "Trying to get Smart data for disk: " + diskName );
        List<HddSMARTData> smartData = new ArrayList<HddSMARTData>();
        if ( diskName != null && !"".equals( diskName.trim() ) && session != null )
        {
            String command = String.format( Constants.GET_HDD_SMART_DATA_COMMAND, diskName.trim() );
            // Result will be received in csv format after executing following command
            String result = EsxiSshUtil.executeCommand( session, command );
            // if result String ends with '\n' We need to remove that extra character,
            // else it will appear as last character in Build Version while parsing
            if ( result.endsWith( "\n" ) )
            {
                result = result.substring( 0, result.length() - 1 );
            }
            logger.debug( result );
            // convert String into InputStream
            InputStream is = new ByteArrayInputStream( result.getBytes() );
            // read it with BufferedReader
            BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
            CSVReader csvReader = new CSVReader( br );
            // read all lines at once
            List<String[]> records = csvReader.readAll();
            Iterator<String[]> iterator = records.iterator();
            // skip header row
            iterator.next();
            while ( iterator.hasNext() )
            {
                String[] record = iterator.next();
                HddSMARTData data = new HddSMARTData();
                // Set name of the Hdd Smart parameter
                data.setParameter( record[0] );
                if ( data.getParameter() != null )
                {
                    data.setThreshold( record[1] );
                    data.setValue( record[2] );
                    data.setWorst( record[3] );
                    smartData.add( data );
                }
            }
            csvReader.close();
            logger.debug( smartData.toString() );
            return smartData;
        }
        else
        {
            throw new HmsException( "Cannot get HDD SMART Data for Disk: " + diskName + " and Session Object: "
                + session );
        }
    }

    /**
     * Get ssh Session Object once for the node
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
     * Get HDD Specific Sensor Data (as Health Status,Media Wearout Indicator etc)
     *
     * @param serviceNode
     * @param component
     * @param inbandServiceImpl
     * @return
     * @throws HmsException
     */
    public static List<ServerComponentEvent> getHddSensor( ServiceHmsNode serviceNode, ServerComponent component,
                                                           InbandServiceImpl inbandServiceImpl )
                                                               throws HmsException
    {
        List<HddInfo> hddInfos = null;
        List<ServerComponentEvent> componentSensors = new ArrayList<ServerComponentEvent>();
        try
        {
            if ( inbandServiceImpl != null )
            {
                hddInfos = inbandServiceImpl.getHddInfo( serviceNode );
            }
            else
            {
                String err =
                    "Error while getting HddInfo because InbandServiceImpl object was found " + inbandServiceImpl;
                logger.error( err );
                throw new HmsException( err );
            }
        }
        catch ( HmsException e )
        {
            logger.error( "Unable to get HddInfo List while trying to get Hdd Sensors for node [" + serviceNode != null
                            ? serviceNode.getNodeID() : serviceNode + "]" );
            throw e;
        }
        componentSensors = getHddOperationalStateEvents( hddInfos, serviceNode );
        if ( hddInfos != null )
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
                for ( HddInfo hddInfo : hddInfos )
                {
                    try
                    {
                        if ( hddInfo != null )
                        {
                            List<HddSMARTData> hddSmartDatas = getHddSmartDataInfo( hddInfo.getName(), session );
                            // if (hddInfo)
                            for ( HddSMARTData hddSmartData : hddSmartDatas )
                            {
                                try
                                {
                                    if ( hddSmartData != null )
                                    {
                                        // For all properties of hddSmartData data in this if condition, generate
                                        // ServerComponentSensor if values are not in the limits.
                                        String parameter = hddSmartData.getParameter();
                                        ServerComponentEvent componentSensor = null;
                                        if ( parameter != null && !"".equals( parameter )
                                            && hddSmartData.getValue() != null )
                                        {
                                            componentSensor = new ServerComponentEvent();
                                            boolean addComponentSensor = false;
                                            if ( hddInfo.getType().equals( "HDD" ) )
                                            {
                                                switch ( parameter )
                                                {
                                                    // Generate ServerComponentSensor if required
                                                    case Constants.HEALTH_STATUS:
                                                        if ( !"".equals( hddSmartData.getValue() )
                                                            && !"N/A".equalsIgnoreCase( hddSmartData.getValue() )
                                                            && !"OK".equalsIgnoreCase( hddSmartData.getValue() ) )
                                                        {
                                                            addComponentSensor = true;
                                                            componentSensor.setEventName( NodeEvent.HDD_HEALTH_CRITICAL );
                                                            componentSensor.setDiscreteValue( hddSmartData.getValue() );
                                                        }
                                                        break;
                                                    case Constants.MEDIA_WEAROUT_INDICATOR:
                                                        if ( isSmartDataBeyondThreshold( hddSmartData, false ) )
                                                        {
                                                            addComponentSensor = true;
                                                            componentSensor.setEventName( NodeEvent.HDD_WEAROUT_ABOVE_THRESHOLD );
                                                            componentSensor.setDiscreteValue( hddSmartData.getValue() );
                                                        }
                                                        break;
                                                    case Constants.WRITE_ERROR_COUNT:
                                                    case Constants.WRITE_SECTORS_TOT_COUNT:
                                                        if ( isSmartDataBeyondThreshold( hddSmartData, false ) )
                                                        {
                                                            addComponentSensor = true;
                                                            componentSensor.setEventName( NodeEvent.HDD_WRITE_ERROR );
                                                            componentSensor.setDiscreteValue( hddSmartData.getValue() );
                                                        }
                                                        break;
                                                    case Constants.READ_ERROR_COUNT:
                                                    case Constants.READ_SECTORS_TOT_COUNT:
                                                    case Constants.RAW_READ_ERROR_RATE:
                                                        if ( isSmartDataBeyondThreshold( hddSmartData, false ) )
                                                        {
                                                            addComponentSensor = true;
                                                            componentSensor.setEventName( NodeEvent.HDD_READ_ERROR );
                                                            componentSensor.setDiscreteValue( hddSmartData.getValue() );
                                                        }
                                                        break;
                                                    case Constants.DRIVE_TEMPERATURE:
                                                    case Constants.DRIVER_RATED_MAX_TEMPERATURE:
                                                        if ( isSmartDataBeyondThreshold( hddSmartData, false ) )
                                                        {
                                                            addComponentSensor = true;
                                                            componentSensor.setEventName( NodeEvent.HDD_TEMP_ABOVE_THRESHOLD );
                                                            componentSensor.setValue( new Float( hddSmartData.getValue() ) );
                                                        }
                                                        break;
                                                    case Constants.REALLOCATED_SECTORS_COUNT:
                                                    case Constants.INITIAL_BAD_BLOCK_COUNT:
                                                        if ( isSmartDataBeyondThreshold( hddSmartData, false ) )
                                                        {
                                                            addComponentSensor = true;
                                                            componentSensor.setEventName( NodeEvent.HDD_HEALTH_CRITICAL );
                                                            componentSensor.setDiscreteValue( hddSmartData.getValue() );
                                                        }
                                                        break;
                                                    case Constants.POWER_ON_HOURS:
                                                    case Constants.POWER_CYCLE_COUNT:
                                                        break;
                                                }
                                            }
                                            else if ( hddInfo.getType().equals( "SSD" ) )
                                            {
                                                switch ( parameter )
                                                {
                                                    case Constants.MEDIA_WEAROUT_INDICATOR:
                                                        if ( isSmartDataBeyondThreshold( hddSmartData, false ) )
                                                        {
                                                            addComponentSensor = true;
                                                            componentSensor.setEventName( NodeEvent.SSD_WEAROUT_ABOVE_THRESHOLD );
                                                            componentSensor.setDiscreteValue( hddSmartData.getValue() );
                                                        }
                                                        break;
                                                    case Constants.WRITE_ERROR_COUNT:
                                                    case Constants.WRITE_SECTORS_TOT_COUNT:
                                                        if ( isSmartDataBeyondThreshold( hddSmartData, false ) )
                                                        {
                                                            addComponentSensor = true;
                                                            componentSensor.setEventName( NodeEvent.SSD_WRITE_ERROR );
                                                            componentSensor.setDiscreteValue( hddSmartData.getValue() );
                                                        }
                                                        break;
                                                    case Constants.READ_ERROR_COUNT:
                                                    case Constants.READ_SECTORS_TOT_COUNT:
                                                    case Constants.RAW_READ_ERROR_RATE:
                                                        if ( isSmartDataBeyondThreshold( hddSmartData, false ) )
                                                        {
                                                            addComponentSensor = true;
                                                            componentSensor.setEventName( NodeEvent.SSD_READ_ERROR );
                                                            componentSensor.setDiscreteValue( hddSmartData.getValue() );
                                                        }
                                                        break;
                                                    case Constants.DRIVE_TEMPERATURE:
                                                    case Constants.DRIVER_RATED_MAX_TEMPERATURE:
                                                        if ( isSmartDataBeyondThreshold( hddSmartData, false ) )
                                                        {
                                                            addComponentSensor = true;
                                                            componentSensor.setEventName( NodeEvent.SSD_TEMP_ABOVE_THRESHOLD );
                                                            componentSensor.setValue( new Float( hddSmartData.getValue() ) );
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                            if ( addComponentSensor )
                                            {
                                                componentSensor.setEventId( parameter );
                                                // componentID = hddInfo.getLocation() + " " +
                                                // hddInfo.getComponentIdentifier().getProduct();
                                                // componentSensor.setComponentId(componentID);
                                                componentSensor.setComponentId( hddInfo.getId() );
                                                componentSensors.add( componentSensor );
                                            }
                                        }
                                        else
                                        {
                                            logger.warn( "One of the SMART Paramter is NULL for Storage is for node ["
                                                + serviceNode != null ? serviceNode.getNodeID() : serviceNode + "]" );
                                        }
                                    }
                                }
                                catch ( Exception e )
                                {
                                    logger.error( "Exception while getting Storage Smart Data for [ "
                                        + hddInfo.getName() + " ] for node [ " + serviceNode != null
                                                        ? serviceNode.getNodeID()
                                                        : serviceNode + " ] for SMART parameter:"
                                                            + hddSmartData.getParameter() );
                                }
                            }
                        }
                        else
                        {
                            logger.warn( "One of Storage Info is Null for node [" + serviceNode != null
                                            ? serviceNode.getNodeID() : serviceNode + "]" );
                        }
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Exception while getting Storage Smart Data for [ " + hddInfo.getName()
                            + " ] for node [ " + serviceNode != null ? serviceNode.getNodeID() : serviceNode + " ]" );
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
            logger.error( "Could Not find any Storage info on node [" + serviceNode != null ? serviceNode.getNodeID()
                            : serviceNode + "]" );
        }
        return componentSensors;
    }

    /**
     * Get HDD Operational State Events
     *
     * @param hddInfoList
     * @param serviceNode
     * @return List<ServerComponentEvent>
     * @throws HmsException
     */
    public static List<ServerComponentEvent> getHddOperationalStateEvents( List<HddInfo> hddInfoList,
                                                                           ServiceHmsNode serviceNode )
                                                                               throws HmsException
    {
        if ( serviceNode != null && serviceNode instanceof ServiceServerNode )
        {
            List<ServerComponentEvent> serverComponentEventList = new ArrayList<ServerComponentEvent>();
            if ( hddInfoList != null )
            {
                ServiceServerNode node = null;
                if ( serviceNode instanceof ServiceServerNode )
                    node = (ServiceServerNode) serviceNode;
                try
                {
                    for ( HddInfo hddInfo : hddInfoList )
                    {
                        try
                        {
                            if ( hddInfo != null )
                            {
                                ServerComponentEvent serverComponentEvent = new ServerComponentEvent();
                                boolean addevent = false;
                                if ( hddInfo.getType().equals( "HDD" ) )
                                {
                                    switch ( hddInfo.getState() )
                                    {
                                        case OK:
                                        case DEGRADED:
                                            serverComponentEvent.setEventName( NodeEvent.HDD_UP );
                                            serverComponentEvent.setDiscreteValue( FruOperationalStatus.Operational.toString() );
                                            addevent = true;
                                            break;
                                        case ERROR:
                                        case OFF:
                                        case QUIESCED:
                                        case LOSTCOMMUNICATION:
                                        case TIMEOUT:
                                            serverComponentEvent.setEventName( NodeEvent.HDD_DOWN );
                                            serverComponentEvent.setDiscreteValue( FruOperationalStatus.NonOperational.toString() );
                                            addevent = true;
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                else if ( hddInfo.getType().equals( "SSD" ) )
                                {
                                    switch ( hddInfo.getState() )
                                    {
                                        case OK:
                                        case DEGRADED:
                                            serverComponentEvent.setEventName( NodeEvent.SSD_UP );
                                            serverComponentEvent.setDiscreteValue( FruOperationalStatus.Operational.toString() );
                                            addevent = true;
                                            break;
                                        case ERROR:
                                        case OFF:
                                        case QUIESCED:
                                        case LOSTCOMMUNICATION:
                                        case TIMEOUT:
                                            serverComponentEvent.setEventName( NodeEvent.SSD_DOWN );
                                            serverComponentEvent.setDiscreteValue( FruOperationalStatus.NonOperational.toString() );
                                            addevent = true;
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                if ( addevent )
                                {
                                    serverComponentEvent.setEventId( "Storage Device Operationl Status" );
                                    // componentID = hddInfo.getLocation() + " " +
                                    // hddInfo.getComponentIdentifier().getProduct();
                                    // serverComponentEvent.setComponentId(componentID);
                                    serverComponentEvent.setComponentId( hddInfo.getId() );
                                    serverComponentEvent.setUnit( EventUnitType.DISCRETE );
                                    serverComponentEventList.add( serverComponentEvent );
                                }
                            }
                            else
                            {
                                logger.warn( "One of Storage Device Info is Null for node [" + serviceNode != null
                                                ? serviceNode.getNodeID() : serviceNode + "]" );
                            }
                        }
                        catch ( Exception e )
                        {
                            logger.error( "Exception while getting operationsal status event for the Storage for node [ "
                                + serviceNode != null ? serviceNode.getNodeID() : serviceNode + " ]" );
                        }
                    }
                }
                catch ( Exception e )
                {
                    logger.error( "Exception while getting Server Component Storage Opearional state Events for node : "
                        + ( node != null ? node.getNodeID() : node ) );
                }
            }
            else
            {
                logger.error( "Could Not find any Storage device info on node [" + serviceNode != null
                                ? serviceNode.getNodeID() : serviceNode + "]" );
            }
            return serverComponentEventList;
        }
        else
        {
            String err = "Node is NOT Server Node" + "Node: " + serviceNode;
            logger.error( err );
            throw new HmsException( err );
        }
    }

    /**
     * Return true if Smart data is in concerning / warning state
     *
     * @param hddSMARTData
     * @return
     */
    public static boolean isSmartDataBeyondThreshold( HddSMARTData hddSMARTData,
                                                      boolean isValueGreaterThanThresholdConcerning )
    {
        boolean isDataConcerning = false;
        if ( hddSMARTData != null )
        {
            String thresholdString = hddSMARTData.getThreshold();
            String valueString = hddSMARTData.getValue();
            if ( valueString != null )
            {
                if ( thresholdString != null )
                {
                    Integer value = null;
                    Integer threshold = null;
                    try
                    {
                        value = Integer.parseInt( valueString );
                        threshold = Integer.parseInt( thresholdString );
                    }
                    catch ( Exception e )
                    {
                        return false;
                    }
                    // Check if value lower than threshold is concerning or the opposite of it.
                    // If isValueLowerTheBetter is true,
                    // it means that if value exceeds the defined threshold, the data is concerning.
                    if ( isValueGreaterThanThresholdConcerning )
                    {
                        if ( value >= threshold )
                        {
                            isDataConcerning = true;
                        }
                    }
                    else
                    {
                        if ( value <= threshold )
                        {
                            isDataConcerning = true;
                        }
                    }
                }
            }
        }
        return isDataConcerning;
    }
}
