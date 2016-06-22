/* ********************************************************************************
 * StorageControllerInfoHelper.java
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.vmware.vim.binding.vim.HostSystem;
import com.vmware.vim.binding.vim.host.ConfigManager;
import com.vmware.vim.binding.vim.host.HardwareInfo;
import com.vmware.vim.binding.vim.host.HostBusAdapter;
import com.vmware.vim.binding.vim.host.PciDevice;
import com.vmware.vim.binding.vim.host.StorageSystem;
import com.vmware.vim.binding.vmodl.ManagedObjectReference;
import com.vmware.vrack.hms.boardservice.ib.InbandServiceImpl;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;
import com.vmware.vrack.hms.common.util.EsxiSshUtil;
import com.vmware.vrack.hms.vsphere.HostProxy;
import com.vmware.vrack.hms.vsphere.VsphereClient;

/**
 * Helper class get the Storage controller Information using the vSphere API's and esxicli interface
 * 
 * @author VMware Inc.
 */
public class StorageControllerInfoHelper
{
    private static Logger logger = LoggerFactory.getLogger( StorageControllerInfoHelper.class );

    private static final String VENDOR = "vendor";

    private static final String DEVICEID = "deviceid";

    private static final String PRODUCT = "product";

    /**
     * Helper method to get the Storage controller Information using the vSphere API's and esxicli interface Using
     * HostBusAdapter data object to fetch the information
     *
     * @param hostSystem
     * @param client
     * @param hostProxy
     * @param node
     * @return List<StorageControllerInfo>
     */
    public static List<StorageControllerInfo> getStorageControllerInfo( HostSystem hostSystem, VsphereClient client,
                                                                        HostProxy hostProxy, ServiceServerNode node )
                                                                            throws Exception
    {
        if ( hostSystem != null && hostSystem.getConfigManager() != null )
        {
            List<StorageControllerInfo> storageControllerInfoList = new ArrayList<>();
            ConfigManager configManager = hostSystem.getConfigManager();
            ManagedObjectReference ssmor = configManager.getStorageSystem();
            if ( ssmor != null )
            {
                StorageSystem ss = client.createStub( StorageSystem.class, ssmor );
                if ( ss != null && ss.getStorageDeviceInfo() != null )
                {
                    HostBusAdapter[] hostBusAdapterList = ss.getStorageDeviceInfo().getHostBusAdapter();
                    // Call the get physical Host Bus Adapter
                    HostBusAdapter[] physicalHostBusAdapterList = getPhysicalHostBusAdapter( hostBusAdapterList );
                    if ( physicalHostBusAdapterList != null )
                    {
                        for ( int i = 0; i < physicalHostBusAdapterList.length; i++ )
                        {
                            StorageControllerInfo storageControllerInfo = new StorageControllerInfo();
                            ComponentIdentifier storageControllerComponentIdentifier = new ComponentIdentifier();
                            Map<String, String> pciInfo = null;
                            Session session = null;
                            storageControllerInfo.setDeviceName( physicalHostBusAdapterList[i].getDevice() );
                            storageControllerInfo.setDriver( physicalHostBusAdapterList[i].getDriver() );
                            switch ( physicalHostBusAdapterList[i].getStatus() )
                            {
                                case Constants.OFFLINE:
                                    storageControllerInfo.setFruOperationalStatus( FruOperationalStatus.NonOperational );
                                    break;
                                case Constants.ONLINE:
                                case Constants.UNBOUND:
                                case Constants.UNKNOWN:
                                    storageControllerInfo.setFruOperationalStatus( FruOperationalStatus.Operational );
                                    break;
                                default:
                                    break;
                            }
                            try
                            {
                                // Call the Peripheral Component Interconnect (PCI)
                                // device information for the controller
                                pciInfo = ( getPciDeviceInfo( physicalHostBusAdapterList[i].getPci(), hostProxy ) );
                            }
                            catch ( HmsException e )
                            {
                                logger.error( "Error getting Pci Device Id for Storage controller: "
                                    + physicalHostBusAdapterList[i].getDevice() );
                            }
                            storageControllerInfo.setPciDeviceId( pciInfo.get( DEVICEID ) );
                            storageControllerComponentIdentifier.setManufacturer( pciInfo.get( VENDOR ) );
                            storageControllerComponentIdentifier.setProduct( pciInfo.get( PRODUCT ) );
                            storageControllerInfo.setComponentIdentifier( storageControllerComponentIdentifier );
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
                                    int numOfDevices =
                                        getDevicesConnectedToController( physicalHostBusAdapterList[i].getDevice(),
                                                                         session, node.getNodeID() );
                                    storageControllerInfo.setNumOfStorageDevicesConnected( numOfDevices );
                                }
                                catch ( Exception e )
                                {
                                    String err =
                                        String.format( "Unable to get number of storage devices connected to storage controller [ %s ] of node [ %s ]",
                                                       physicalHostBusAdapterList[i].getDevice(), node.getNodeID() );
                                    logger.debug( err, e );
                                }
                            }
                            finally
                            {
                                destroySession( session );
                            }
                            storageControllerInfoList.add( storageControllerInfo );
                        }
                    }
                }
                else
                {
                    throw new HmsException( "No Storage Controller information found." );
                }
            }
            return storageControllerInfoList;
        }
        else
        {
            throw new Exception( "Can not get Storage Controller Info because the Host Proxy Object is NULL" );
        }
    }

    /**
     * Get the Physical Host Bus Adapter (Storage Controller)
     *
     * @param hostBusAdapterList
     * @return HostBusAdapter[]
     */
    private static HostBusAdapter[] getPhysicalHostBusAdapter( HostBusAdapter[] hostBusAdapterList )
    {
        HostBusAdapter[] hostBusAdapterArray = null;
        try
        {
            // Sort HostBusAdapter based on the device
            List<HostBusAdapter> hostBusAdapterArrayList =
                new ArrayList<HostBusAdapter>( Arrays.asList( hostBusAdapterList ) );
            Collections.sort( hostBusAdapterArrayList, new Comparator<HostBusAdapter>()
            {
                @Override
                public int compare( HostBusAdapter hostBusAdapter0, HostBusAdapter hostBusAdapter1 )
                {
                    return hostBusAdapter0.device.compareTo( hostBusAdapter1.device );
                }
            } );
            // Filter HostBusAdapter object based on the PCI ID....to get only
            // Physical Host Bus adapter
            SortedSet<HostBusAdapter> hostBusAdapterSortedSet =
                new TreeSet<HostBusAdapter>( new Comparator<HostBusAdapter>()
                {
                    @Override
                    public int compare( HostBusAdapter hostBusAdapter0, HostBusAdapter hostBusAdapter1 )
                    {
                        return hostBusAdapter0.getPci().compareTo( hostBusAdapter1.getPci() );
                    }
                } );
            Iterator<HostBusAdapter> iterator = hostBusAdapterArrayList.iterator();
            while ( iterator.hasNext() )
            {
                hostBusAdapterSortedSet.add( iterator.next() );
            }
            hostBusAdapterArrayList.clear();
            hostBusAdapterArrayList.addAll( hostBusAdapterSortedSet );
            hostBusAdapterArray = hostBusAdapterArrayList.toArray( new HostBusAdapter[hostBusAdapterArrayList.size()] );
            return hostBusAdapterArray;
        }
        catch ( Exception e )
        {
            logger.error( "Error while getting the Physical Host Bus Adapter", e );
        }
        return null;
    }

    /**
     * Get the Peripheral Component Interconnect (PCI) device information
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
                            // The device ID might be a negative value as
                            // vSphere Server uses an unsignedshort integer to
                            // represent a PCI device ID
                            // If the PCI ID is greater than 32767, the Server
                            // will convert the ID to its two's complement for
                            // the WSDL representation.
                            int pciDeviceid = device.getDeviceId();
                            // If it's 2's complement value...convert into
                            // decimal
                            if ( pciDeviceid < 0 )
                                pciDeviceid = pciDeviceid + 65536;
                            pciDeviceId = String.valueOf( pciDeviceid );
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
                logger.error( "Host System Object was found null when trying to get PCI Device Id for Storage controller" );
                throw new HmsException( "Error while trying to get PCI Device info for Storage Controller" );
            }
            return pciDeviceInfo;
        }
        else
        {
            logger.error( "Either pciHash or Host Proxy is Null. pciHash : " + pciHash );
            throw new HmsException( "Error while trying to get PCI Device info for Storage Controller" );
        }
    }

    /**
     * Get SSH Session Object for the node
     *
     * @param node
     * @return SSH session
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
     * Destroy SSH Session once completed
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
     * Returns Number of storage devices connected to Storage controller/Host Bus Adapter
     *
     * @param hbaDevice
     * @param session
     * @param nodeId
     * @return number of storage devices connected to Storage controller
     * @throws Exception
     */
    private static int getDevicesConnectedToController( String hbaDevice, Session session, String nodeId )
        throws Exception
    {
        logger.debug( "Getting number of storage devices connected to storage controller hbaDevice [ " + hbaDevice
            + " ] for node [ " + nodeId + " ]" );
        int numOfDevices = 0;
        if ( hbaDevice != null && session != null )
        {
            String command = Constants.GET_STORAGE_DEVICE_CONNECTED;
            String result = EsxiSshUtil.executeCommand( session, command );
            // if result String ends with '\n' We need to remove that extra
            // character,
            // else it will appear as last character while parsing
            if ( result.endsWith( "\n" ) )
            {
                result = result.substring( 0, result.length() - 1 );
            }
            logger.debug( "Storage Controller connected devices details as CSV: " + result );
            CSVReader csvReader = null;
            try
            {
                // Covert String into InputStream
                InputStream is = new ByteArrayInputStream( result.getBytes() );
                // Reading it with BufferedReader
                BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
                csvReader = new CSVReader( br );
                // Reading all lines at once
                List<String[]> records = csvReader.readAll();
                // Parse to get the number of devices connected a storage
                // controller for HBA device
                for ( int i = 0; i < records.size(); i++ )
                {
                    String[] record = records.get( i );
                    for ( int j = 0; j < record.length; j++ )
                    {
                        if ( record[j].contains( hbaDevice ) )
                        {
                            ++numOfDevices;
                        }
                    }
                }
            }
            catch ( Exception e )
            {
                logger.error( "Error while parsing CSV from the result for number of storage devices connected to storage controller "
                    + "HBA device [ " + hbaDevice + " ] on node [ " + nodeId + " ] : ", e );
            }
            finally
            {
                if ( csvReader != null )
                {
                    csvReader.close();
                }
            }
            return numOfDevices;
        }
        else
        {
            throw new HmsException( "Cannot get number of storage devices connected on node [ " + nodeId
                + " ] for storage controller HBA device " + "[ " + hbaDevice + "] and Session Object [" + session
                + " ]" );
        }
    }

    /**
     * Fill the ServerComponentEvent object List to generate the Server Component Storage controller operational status
     * event.
     *
     * @param serviceNode
     * @param component
     * @param inbandServiceImpl
     * @return List<ServerComponentEvent>
     * @throws HmsException
     */
    public static List<ServerComponentEvent> getServerComponentStorageControllerEvent( ServiceHmsNode serviceNode,
                                                                                       ServerComponent component,
                                                                                       InbandServiceImpl inbandServiceImpl )
                                                                                           throws HmsException
    {
        if ( serviceNode != null && serviceNode instanceof ServiceServerNode )
        {
            List<StorageControllerInfo> storageControllerInfoList = null;
            List<ServerComponentEvent> serverComponentStorageControllerEvent = new ArrayList<ServerComponentEvent>();
            try
            {
                if ( inbandServiceImpl != null )
                {
                    storageControllerInfoList = inbandServiceImpl.getStorageControllerInfo( serviceNode );
                }
                else
                {
                    String err = "Error while getting StorageControllerInfo because InbandServiceImpl object was found "
                        + inbandServiceImpl;
                    logger.error( err );
                    throw new HmsException( err );
                }
            }
            catch ( HmsException e )
            {
                logger.error( "Unable to get StorageControllerInfo List, while trying to Server Component Storage Controller Event for node ["
                    + serviceNode != null ? serviceNode.getNodeID() : serviceNode + "]" );
                throw e;
            }
            if ( storageControllerInfoList != null )
            {
                ServiceServerNode node = null;
                if ( serviceNode instanceof ServiceServerNode )
                    node = (ServiceServerNode) serviceNode;
                try
                {
                    for ( StorageControllerInfo storageControllerInfo : storageControllerInfoList )
                    {
                        try
                        {
                            if ( storageControllerInfo != null )
                            {
                                ServerComponentEvent serverComponentEvent = new ServerComponentEvent();
                                String componentID = null;
                                switch ( storageControllerInfo.getFruOperationalStatus() )
                                {
                                    case NonOperational:
                                        serverComponentEvent.setEventName( NodeEvent.STORAGE_CONTROLLER_DOWN );
                                        serverComponentEvent.setDiscreteValue( FruOperationalStatus.NonOperational.toString() );
                                        serverComponentEvent.setEventId( "Storage Controller Operationl Status" );
                                        componentID = storageControllerInfo.getDeviceName() + " "
                                            + storageControllerInfo.getComponentIdentifier().getProduct();
                                        serverComponentEvent.setComponentId( componentID );
                                        serverComponentEvent.setUnit( EventUnitType.DISCRETE );
                                        serverComponentStorageControllerEvent.add( serverComponentEvent );
                                        break;
                                    case Operational:
                                        serverComponentEvent.setEventName( NodeEvent.STORAGE_CONTROLLER_UP );
                                        serverComponentEvent.setDiscreteValue( FruOperationalStatus.Operational.toString() );
                                        serverComponentEvent.setEventId( "Storage Controller Operationl Status" );
                                        componentID = storageControllerInfo.getDeviceName() + " "
                                            + storageControllerInfo.getComponentIdentifier().getProduct();
                                        serverComponentEvent.setComponentId( componentID );
                                        serverComponentEvent.setUnit( EventUnitType.DISCRETE );
                                        serverComponentStorageControllerEvent.add( serverComponentEvent );
                                        break;
                                    default:
                                        break;
                                }
                            }
                            else
                            {
                                logger.warn( "One of Storage Controller Info is Null for node [" + serviceNode != null
                                                ? serviceNode.getNodeID() : serviceNode + "]" );
                            }
                        }
                        catch ( Exception e )
                        {
                            logger.error( "Exception while getting operationsal status event for the Storage Controller device name [ "
                                + storageControllerInfo.getDeviceName() + " ] for node [ " + serviceNode != null
                                                ? serviceNode.getNodeID() : serviceNode + " ]" );
                        }
                    }
                }
                catch ( Exception e )
                {
                    logger.error( "Exception while getting Server Component Storage Controller Events for node : "
                        + ( node != null ? node.getNodeID() : node ) );
                }
            }
            else
            {
                logger.error( "Could Not find any Storage Controller info on node [" + serviceNode != null
                                ? serviceNode.getNodeID() : serviceNode + "]" );
            }
            return serverComponentStorageControllerEvent;
        }
        else
        {
            String err = "Node is NOT Server Node" + "Node: " + serviceNode;
            logger.error( err );
            throw new HmsException( err );
        }
    }
}
