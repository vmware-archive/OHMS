/* ********************************************************************************
 * MemoryInfoHelper.java
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
package com.vmware.vrack.hms.boardservice.ib.api.cim;

import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.UnsignedInteger16;
import javax.cim.UnsignedInteger32;
import javax.cim.UnsignedInteger64;
import javax.security.auth.Subject;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import javax.wbem.client.PasswordCredential;
import javax.wbem.client.UserPrincipal;
import javax.wbem.client.WBEMClient;
import javax.wbem.client.WBEMClientConstants;
import javax.wbem.client.WBEMClientFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.memory.MemoryFormFactorMapper;
import com.vmware.vrack.hms.common.servernodes.api.memory.MemoryTypeMapper;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;

/**
 * Class to populate Physical Memory related fields in Server node
 * 
 * @author VMware, Inc.
 */
@Component
public class MemoryInfoHelper
{
    private static Logger logger = LoggerFactory.getLogger( MemoryInfoHelper.class );

    public WBEMClient cimClient;

    ServiceServerNode node;

    private static int cimPort;

    private static int cimClientTimeoutInMs;

    private static int cimConnectionRetry;

    private static int cimClientRetryFrequency;

    @Value( "${cim.port:5989}" )
    public void setCimPort( int cimPort )
    {
        MemoryInfoHelper.cimPort = cimPort;
    }

    @Value( "${cim.connection.timeout.ms:20000}" )
    public void setCimClientTimeoutInMs( int cimClientTimeoutInMs )
    {
        MemoryInfoHelper.cimClientTimeoutInMs = cimClientTimeoutInMs;
    }

    @Value( "${cim.connection.retry:3}" )
    public void setCimClientConnectionRetry( int cimConnectionRetry )
    {
        MemoryInfoHelper.cimConnectionRetry = cimConnectionRetry;
    }

    @Value( "${cim.connection.retry.frequency:60000}" )
    public void setCimClientRetryFrequency( int cimClientRetryFrequency )
    {
        MemoryInfoHelper.cimClientRetryFrequency = cimClientRetryFrequency;
    }

    @SuppressWarnings( "unused" )
    private MemoryInfoHelper()
    {

    }

    public MemoryInfoHelper( ServiceServerNode node )
    {
        this.node = node;
    }

    /**
     * Get the physical memory information using CIM. If the CIM client connection fails after number of retries through
     * an exception. If the CIM client connection success and physical memory information is available, return the
     * physicalMemoryList
     *
     * @return List<PhysicalMemory>
     * @throws Exception
     */
    public List<PhysicalMemory> getMemoryInfo()
        throws Exception
    {
        List<PhysicalMemory> physicalMemoryList = null;
        List<PhysicalMemory> fullPhysicalMemoryInventoryList = null;
        int counter = 0;
        boolean memSuccess = false;

        while ( counter < cimConnectionRetry && !memSuccess )
        {
            try
            {
                getClient();
                logger.debug( "CIM client connection success, Attempt number: {}", counter + 1 );
                physicalMemoryList = executeTask();
                if ( !CollectionUtils.isEmpty( physicalMemoryList ) )
                {
                    logger.debug( "Got physical memory list of size {} for node {}", physicalMemoryList.size(),
                                  nullSafeNodeId( node ) );
                }
                else
                {
                    logger.warn( "Got null physical memory list for node {}", node.getNodeID() );
                }
                memSuccess = true;
            }
            catch ( Exception e )
            {
                logger.error( "CIM client connection failed, couldn't get memory Information, retry attempt: {}",
                              counter + 1 );
                counter++;
                Thread.sleep( cimClientRetryFrequency ); // wait for 1 minute for each retries.
            }
        }
        if ( memSuccess )
        {
            try
            {
                fullPhysicalMemoryInventoryList = executeTask( physicalMemoryList );
                if ( !CollectionUtils.isEmpty( fullPhysicalMemoryInventoryList ) )
                {
                    logger.debug( "Got full memory Inventory using CIM of size {} for node {}",
                                  fullPhysicalMemoryInventoryList.size(), nullSafeNodeId( node ) );
                }
                else
                {
                    logger.warn( "Got null full memory Inventory using CIM for node {}", nullSafeNodeId( node ) );
                }
                return fullPhysicalMemoryInventoryList;
            }
            catch ( Exception e )
            {
                logger.error( "Could not get the physical memory information with the empty slot information using CIM client failed: ",
                              e );
                throw new HmsException( "Could not get the physical memory information with the empty slot information using CIM client failed: ",
                                        e );
            }
            finally
            {
                destroy();
            }
        }
        else
        {
            logger.error( "Could not get the physical memory inforamtion CIM client failed." );
            throw new HmsException( "Could not get the physical memory inforamtion CIM client failed." );
        }
    }

    /**
     * Get CIM client connection
     * 
     * @throws Exception
     */
    private void getClient()
        throws Exception
    {
        try
        {
            // TODO : we assume that we will get protocol and cim port number via node.
            URL cimomUrl = new URL( "https://" + node.getIbIpAddress() + ":" + cimPort );
            cimClient = WBEMClientFactory.getClient( WBEMClientConstants.PROTOCOL_CIMXML );
            final CIMObjectPath path = new CIMObjectPath( cimomUrl.getProtocol(), cimomUrl.getHost(),
                                                          String.valueOf( cimomUrl.getPort() ), null, null, null );
            final Subject subject = new Subject();
            subject.getPrincipals().add( new UserPrincipal( node.getOsUserName() ) );
            String password = node.getOsPassword();
            subject.getPrivateCredentials().add( new PasswordCredential( password ) );

            try
            {
                logger.debug( "Trying to create CIMClient object for node with IB IP address: {}",
                              node.getIbIpAddress() );
                cimClient.setProperty( WBEMClientConstants.PROP_TIMEOUT, String.valueOf( cimClientTimeoutInMs ) );
                cimClient.initialize( path, subject, Locale.getAvailableLocales() );
                logger.debug( "Initilization of CIMClient done for Node with IB IP address: {}",
                              node.getIbIpAddress() );
            }
            catch ( Exception e )
            {
                String err = "While initializing cimClient for Node with IB IP address: " + node.getIbIpAddress();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
        }
        catch ( Exception e )
        {
            // TODO: Log exception
            logger.error( "Error while creating connection to the cimClient: ", e );
            throw new HmsException( "Error while creating connection to the cimClient: ", e );
        }
    }

    /**
     * destroy CIM client connection
     * 
     * @throws Exception
     */
    private void destroy()
        throws Exception
    {
        if ( cimClient != null )
        {
            try
            {
                cimClient.close();
                cimClient = null;
            }
            catch ( Exception e )
            {
                logger.error( "While closing cimCLient: ", e );
            }
        }
    }

    /**
     * Method helps to get the memory Inventory using CIM class OMC_PhysicalMemory
     * 
     * @param physicalMemoryList
     * @return List<PhysicalMemory>
     * @throws Exception
     */
    private List<PhysicalMemory> executeTask()
        throws Exception
    {
        List<PhysicalMemory> physicalMemoryList = new ArrayList<>();
        try
        {
            List<CIMInstance> instancesList = new ArrayList<CIMInstance>();

            logger.debug( "Starting to get /root/cimv2:OMC_PhysicalMemory for node with IB IP address: {}",
                          node.getIbIpAddress() );
            // for Vendor and individual Memory Information and their sizes
            CloseableIterator<CIMInstance> storageIterator =
                cimClient.enumerateInstances( new CIMObjectPath( "/root/cimv2:OMC_PhysicalMemory" ), true, true, false,
                                              null );

            logger.debug( "Finished getting /root/cimv2:OMC_PhysicalMemory for node with IB IP address: {}",
                          node.getIbIpAddress() );

            try
            {
                while ( storageIterator.hasNext() )
                {
                    final CIMInstance instanceIterator = storageIterator.next();
                    instancesList.add( instanceIterator );
                }
                physicalMemoryList = getSystemMemory( cimClient, instancesList );
                return physicalMemoryList;
            }
            catch ( Exception e )
            {
                logger.error( "Error while getting system Memory: OMC_PhysicalMemory  ", e );
                throw new HmsException( e );

            }
        }
        catch ( WBEMException e )
        {
            logger.error( "Error while enumerating memory instances: OMC_PhysicalMemory ", e );
            throw new HmsException( e );
        }
    }

    /**
     * Method helps to get the full memory Inventory using CIM class OMC_MemorySlot
     * 
     * @param physicalMemoryList
     * @return List<PhysicalMemory>
     * @throws Exception
     */
    private List<PhysicalMemory> executeTask( List<PhysicalMemory> physicalMemoryList )
        throws Exception
    {
        try
        {
            List<CIMInstance> instancesList = new ArrayList<CIMInstance>();

            logger.debug( "Starting to get /root/cimv2:OMC_MemorySlot for node with IB IP address: {}",
                          node.getIbIpAddress() );

            CloseableIterator<CIMInstance> storageIterator =
                cimClient.enumerateInstances( new CIMObjectPath( "/root/cimv2:OMC_MemorySlot" ), true, true, false,
                                              null );

            logger.debug( "Finished getting /root/cimv2:OMC_MemorySlot for node with IB IP address: {}",
                          node.getIbIpAddress() );

            try
            {
                while ( storageIterator.hasNext() )
                {
                    final CIMInstance instanceIterator = storageIterator.next();
                    instancesList.add( instanceIterator );
                }
                return getSystemMemoryInventoryWithEmptySlot( cimClient, instancesList, physicalMemoryList );
            }
            catch ( Exception e )
            {
                logger.error( "Error while getting system Memory:OMC_MemorySlot ", e );
                throw new HmsException( e );
            }
        }
        catch ( WBEMException e )
        {
            logger.error( "Error while enumerating memory instances:OMC_MemorySlot ", e );
            throw new HmsException( e );
        }
    }

    /**
     * Helper method to get the full Physical Memory Inventory
     * 
     * @param client
     * @param instancesList
     * @param physicalMemoryList
     * @return List<PhysicalMemory>
     * @throws HmsException
     */
    private List<PhysicalMemory> getSystemMemoryInventoryWithEmptySlot( WBEMClient client,
                                                                        List<CIMInstance> instancesList,
                                                                        List<PhysicalMemory> physicalMemoryList )
        throws HmsException
    {

        if ( client != null && instancesList != null )
        {
            List<PhysicalMemory> fullPhysicalMemoryInventoryList = physicalMemoryList;
            List<PhysicalMemory> physicalMemoryListWithEmptySlot = new ArrayList<PhysicalMemory>();

            for ( int i = 0; i < instancesList.size(); i++ )
            {
                final CIMInstance instance = instancesList.get( i );

                String elementName = (String) instance.getProperty( "ElementName" ).getValue();
                String manufacturer = (String) instance.getProperty( "Manufacturer" ).getValue();
                String partNumber = (String) instance.getProperty( "PartNumber" ).getValue();
                String serialNumber = (String) instance.getProperty( "SerialNumber" ).getValue();
                String manufactureDate = (String) instance.getProperty( "ManufactureDate" ).getValue();
                String model = (String) instance.getProperty( "Model" ).getValue();

                PhysicalMemory memoryInfo = new PhysicalMemory();
                ComponentIdentifier componentIdentifier = new ComponentIdentifier();

                memoryInfo.setId( elementName );
                memoryInfo.setLocation( elementName );
                componentIdentifier.setManufacturer( manufacturer );
                componentIdentifier.setPartNumber( partNumber );
                componentIdentifier.setSerialNumber( serialNumber );
                componentIdentifier.setProduct( model );
                componentIdentifier.setManufacturingDate( manufactureDate );
                memoryInfo.setComponentIdentifier( componentIdentifier );
                memoryInfo.setFruOperationalStatus( FruOperationalStatus.NonOperational );

                physicalMemoryListWithEmptySlot.add( memoryInfo );
            }

            fullPhysicalMemoryInventoryList.addAll( getMissingPhysicalMemoryList( physicalMemoryList,
                                                                                  physicalMemoryListWithEmptySlot ) );

            Collections.sort( fullPhysicalMemoryInventoryList, new Comparator<PhysicalMemory>()
            {
                @Override
                public int compare( PhysicalMemory physicalMemory1, PhysicalMemory physicalMemory2 )
                {
                    return physicalMemory1.getId().compareTo( physicalMemory2.getId() );
                }
            } );

            return fullPhysicalMemoryInventoryList;
        }
        return null;
    }

    /**
     * Helper method to get the Physical Memory Inventory
     * 
     * @param client
     * @param instancesList
     * @return List<PhysicalMemory>
     * @throws HmsException
     */
    private List<PhysicalMemory> getSystemMemory( WBEMClient client, List<CIMInstance> instancesList )
        throws HmsException
    {
        if ( client != null && instancesList != null )
        {
            List<PhysicalMemory> memoryList = new ArrayList<PhysicalMemory>();

            for ( int i = 0; i < instancesList.size(); i++ )
            {
                final CIMInstance instance = instancesList.get( i );
                PhysicalMemory memoryInfo = new PhysicalMemory();

                String elementName = (String) instance.getProperty( "ElementName" ).getValue();
                String manufacturer = (String) instance.getProperty( "Manufacturer" ).getValue();
                String bankLabel = (String) instance.getProperty( "BankLabel" ).getValue();
                String partNumber = (String) instance.getProperty( "PartNumber" ).getValue();
                String serialNumber = (String) instance.getProperty( "SerialNumber" ).getValue();
                String manufactureDate = (String) instance.getProperty( "ManufactureDate" ).getValue();
                String model = (String) instance.getProperty( "Model" ).getValue();
                long maxMemorySpeedInHertz =
                    ( (UnsignedInteger32) instance.getProperty( "MaxMemorySpeed" ).getValue() ).longValue();
                BigInteger capacityInBytes =
                    ( (UnsignedInteger64) instance.getProperty( "Capacity" ).getValue() ).bigIntegerValue();
                int dataWidth = ( (UnsignedInteger16) instance.getProperty( "DataWidth" ).getValue() ).intValue();
                int totalWidth = ( (UnsignedInteger16) instance.getProperty( "TotalWidth" ).getValue() ).intValue();
                int formFactorCode = ( (UnsignedInteger16) instance.getProperty( "FormFactor" ).getValue() ).intValue();
                int memoryTypeCode = ( (UnsignedInteger16) instance.getProperty( "MemoryType" ).getValue() ).intValue();

                ComponentIdentifier componentIdentifier = new ComponentIdentifier();

                // Set the Retrieved Properties
                memoryInfo.setId( elementName );
                componentIdentifier.setManufacturer( manufacturer );
                memoryInfo.setLocation( elementName );
                componentIdentifier.setPartNumber( partNumber );
                componentIdentifier.setSerialNumber( serialNumber );
                componentIdentifier.setProduct( model );
                componentIdentifier.setManufacturingDate( manufactureDate );
                memoryInfo.setMemoryType( MemoryTypeMapper.getMemoryType( memoryTypeCode ) );
                memoryInfo.setCapacityInBytes( capacityInBytes );
                memoryInfo.setComponentIdentifier( componentIdentifier );
                memoryInfo.setFruOperationalStatus( FruOperationalStatus.Operational );

                if ( (Boolean) instance.getProperty( "IsSpeedInMhz" ).getValue() )
                    memoryInfo.setMaxMemorySpeedInHertz( maxMemorySpeedInHertz );
                // if(totalWidth > dataWidth)
                // memoryInfo.setEccEnabled(true);

                // Add the Memory info to memoryList only if it is of DIMM form factor
                // Ignore other memories as Flash memory etc.
                if ( "DIMM".equals( MemoryFormFactorMapper.getFormFactor( formFactorCode ) ) )
                {
                    memoryList.add( memoryInfo );
                }
            }
            return memoryList;
        }
        return null;
    }

    /**
     * Convert Physical Memory List into Map
     * 
     * @param physicalMemoryList
     * @return
     */
    private static Map<String, PhysicalMemory> convertListToMap( List<PhysicalMemory> physicalMemoryList )
    {
        Map<String, PhysicalMemory> physicalMemoryListMap = new HashMap<String, PhysicalMemory>();
        if ( physicalMemoryList == null || physicalMemoryList.isEmpty() )
            return physicalMemoryListMap;
        else
        {
            for ( PhysicalMemory physicalMemory : physicalMemoryList )
                physicalMemoryListMap.put( physicalMemory.getId(), physicalMemory );

            return physicalMemoryListMap;
        }
    }

    /**
     * Helps to Get the Missing Physical Memory List
     * 
     * @param physicalMemoryList
     * @param physicalMemoryListWithEmptySlot
     * @return
     */
    private List<PhysicalMemory> getMissingPhysicalMemoryList( List<PhysicalMemory> physicalMemoryList,
                                                               List<PhysicalMemory> physicalMemoryListWithEmptySlot )
    {

        Map<String, PhysicalMemory> physicalMemoryListMap = convertListToMap( physicalMemoryList );
        Map<String, PhysicalMemory> physicalMemoryListWithEmptySlotMap =
            convertListToMap( physicalMemoryListWithEmptySlot );
        Map<String, PhysicalMemory> emptyphysicalMemorySlotMap = new HashMap<String, PhysicalMemory>();

        for ( String key : physicalMemoryListWithEmptySlotMap.keySet() )
        {
            if ( physicalMemoryListMap.containsKey( key ) )
                continue;
            logger.debug( "Adding Missing or empty Physical Memory slot Information {}: to the emptyphysicalMemorySlotMap for host:{}",
                          physicalMemoryListWithEmptySlotMap.get( key ).getId(), node.getNodeID() );
            emptyphysicalMemorySlotMap.put( key, physicalMemoryListWithEmptySlotMap.get( key ) );
        }
        return new ArrayList<PhysicalMemory>( emptyphysicalMemorySlotMap.values() );
    }

    /**
     * Populates the Registered Memory Map. This map will be used to populate properties which are not populated through
     * OMC_Memory Instances.
     * 
     * @param client
     * @param registeredMemoryList
     * @return
     */
    private Map<String, Object> populateRegisteredMemoryMapWithFullInstance( WBEMClient client,
                                                                             List<CIMInstance> registeredMemoryList )
        throws HmsException
    {
        if ( client != null && registeredMemoryList != null )
        {
            Map<String, Object> registeredMemoryMap = new HashMap<String, Object>();

            for ( int index = 0; index < registeredMemoryList.size(); index++ )
            {
                final CIMInstance instance = registeredMemoryList.get( index );
                List<CIMObjectPath> associators =
                    getAssociatorNames( client, instance.getObjectPath(), null, "OMC_Memory" );

                if ( associators != null )
                {
                    for ( int j = 0; j < associators.size(); ++j )
                    {
                        // Get full instance
                        CIMInstance cimFullInstance = getInstance( client, associators.get( j ) );
                        String memoryDeviceId = (String) cimFullInstance.getProperty( "DeviceID" ).getValue();
                        registeredMemoryMap.put( memoryDeviceId, cimFullInstance );
                    }
                }
            }
            return registeredMemoryMap;
        }
        return null;
    }

    /**
     * Traverses an association from a given instance path to a given result class.
     *
     * @param pClient The client to use
     * @param pPath The instance path
     * @param pAssociationClass The class name of the association to traverse
     * @param pResultClass The class name of the CIM class to return
     * @return A List<CIMObjectPath> of the instance paths of the result class
     */
    private static List<CIMObjectPath> getAssociatorNames( WBEMClient pClient, CIMObjectPath pPath,
                                                           String pAssociationClass, String pResultClass )
        throws HmsException
    {
        try
        {
            final CloseableIterator<CIMObjectPath> associators =
                pClient.associatorNames( pPath, pAssociationClass, pResultClass, null, null );
            try
            {
                final List<CIMObjectPath> result = new ArrayList<CIMObjectPath>();
                while ( associators.hasNext() )
                {
                    final CIMObjectPath element = associators.next();
                    result.add( element );
                }
                return result;
            }
            finally
            {
                associators.close();
            }
        }
        catch ( final WBEMException e )
        {
            logger.error( "While getting associator names ", e );
            throw new HmsException( e );
        }
    }

    /**
     * Gives the full Instance
     * 
     * @param pClient
     * @param pPath
     * @return
     */
    private static CIMInstance getInstance( WBEMClient pClient, CIMObjectPath pPath )
        throws HmsException
    {
        try
        {
            final CIMInstance instance = pClient.getInstance( pPath, false, false, null );
            return instance;
        }
        catch ( final WBEMException e )
        {
            logger.error( "While getting full Memory Instances: ", e );
            throw new HmsException( e );
        }
    }

    /**
     * If node is null then returns null otherwise returns node ID.
     * 
     * @param node
     * @return
     */
    private String nullSafeNodeId( ServiceServerNode node )
    {
        if ( node == null )
        {
            return null;
        }
        else
        {
            return node.getNodeID();
        }
    }
}