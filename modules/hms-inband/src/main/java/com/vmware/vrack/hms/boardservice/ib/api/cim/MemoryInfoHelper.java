/* ********************************************************************************
 * MemoryInfoHelper.java
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
package com.vmware.vrack.hms.boardservice.ib.api.cim;

import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
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

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.memory.MemoryFormFactorMapper;
import com.vmware.vrack.hms.common.servernodes.api.memory.MemoryTypeMapper;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;

/**
 * Class to populate Physical Memory related fields in Server node
 * 
 * @author Yagnesh Chawda
 */
@Component
public class MemoryInfoHelper
{
    private static Logger logger = LoggerFactory.getLogger( MemoryInfoHelper.class );

    public WBEMClient cimClient;

    ServiceServerNode node;

    private static int cimPort;

    private static int cimClientTimeoutInMs;

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

    private MemoryInfoHelper()
    {
    }

    public MemoryInfoHelper( ServiceServerNode node )
    {
        this.node = node;
    }

    public List<PhysicalMemory> getMemoryInfo()
        throws Exception
    {
        List<PhysicalMemory> physicalMemories = null;
        if ( cimClient != null )
            executeTask();
        else
        {
            getClient();
            physicalMemories = executeTask();
            destroy();
        }
        return physicalMemories;
    }

    public void getClient()
    {
        // if(node.isDiscoverable() && node.isPowered())
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
                subject.getPrivateCredentials().add( new PasswordCredential( node.getOsPassword() ) );
                try
                {
                    logger.debug( "Trying to create CIMClient object for node with IB IP address:"
                        + node.getIbIpAddress() );
                    cimClient.setProperty( WBEMClientConstants.PROP_TIMEOUT, String.valueOf( cimClientTimeoutInMs ) );
                    cimClient.initialize( path, subject, Locale.getAvailableLocales() );
                    logger.debug( "Initilization of CIMClient done for Node with IB IP address:"
                        + node.getIbIpAddress() );
                }
                catch ( Exception e )
                {
                    logger.error( "While initializing cimClient for Node with IB IP address:" + node.getIbIpAddress(),
                                  e );
                }
            }
            catch ( Exception e )
            {
                // TODO: Log exception
                logger.error( "While creating cimClient: ", e );
            }
        }
    }

    public void destroy()
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

    public List<PhysicalMemory> executeTask()
        throws Exception
    {
        List<PhysicalMemory> physicalMemories = new ArrayList<>();
        try
        {
            // System.out.println("In Memory Info.");
            List<CIMInstance> instancesList = new ArrayList<CIMInstance>();
            List<CIMInstance> registeredMemoryList = new ArrayList<CIMInstance>();
            /*
             * //for Memory enabled State Info, total Installed Memory CloseableIterator<CIMInstance>
             * registeredMemoryIterator = cimClient.enumerateInstances(new
             * CIMObjectPath("/root/interop:OMC_RegisteredSystemMemoryProfile"), true, true, false, null);
             */
            logger.debug( "Starting to get /root/cimv2:OMC_Memory for node with IB IP address:"
                + node.getIbIpAddress() );
            // for Vendor and individual Memory Information and their sizes
            CloseableIterator<CIMInstance> storageIterator =
                cimClient.enumerateInstances( new CIMObjectPath( "/root/cimv2:OMC_Memory" ), true, true, false, null );
            logger.debug( "Finished getting /root/cimv2:OMC_Memory for node with IB IP address:"
                + node.getIbIpAddress() );
            try
            {
                /*
                 * while(registeredMemoryIterator.hasNext()) { final CIMInstance instanceIterator =
                 * registeredMemoryIterator.next(); registeredMemoryList.add(instanceIterator); }
                 */
                while ( storageIterator.hasNext() )
                {
                    final CIMInstance instanceIterator = storageIterator.next();
                    instancesList.add( instanceIterator );
                }
                /*
                 * Map<String,Object> registeredMemoryMap = populateRegisteredMemoryMapWithFullInstance(cimClient,
                 * registeredMemoryList);
                 */
                // physicalMemories = getSystemMemory(cimClient, instancesList, registeredMemoryMap);
                physicalMemories = getSystemMemory( cimClient, instancesList );
                return physicalMemories;
            }
            catch ( Exception e )
            {
                logger.error( "While getting system Memory ", e );
                throw new HmsException( e );
            }
        }
        catch ( WBEMException e )
        {
            logger.error( "While enumerating memory instances: ", e );
            throw new HmsException( e );
        }
    }

    /*
     * private List<PhysicalMemory> getSystemMemory(WBEMClient client, List<CIMInstance> instancesList, Map<String,
     * Object> registeredMemoryMap) throws HmsException
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
                List<CIMObjectPath> associators =
                    getAssociatorNames( client, instance.getObjectPath(), null, "OMC_PhysicalMemory" );
                // systemMemoryInfo.setNoOfMemoryDevicesInstalled(associators.size());
                // If associators is not null then check for Memory Stick related Info
                if ( associators != null )
                {
                    for ( int j = 0; j < associators.size(); ++j )
                    {
                        // Get full instance
                        CIMInstance cimFullInstance = getInstance( client, associators.get( j ) );
                        PhysicalMemory memoryInfo = new PhysicalMemory();
                        String elementName = (String) cimFullInstance.getProperty( "ElementName" ).getValue();
                        String manufacturer = (String) cimFullInstance.getProperty( "Manufacturer" ).getValue();
                        String bankLabel = (String) cimFullInstance.getProperty( "BankLabel" ).getValue();
                        String partNumber = (String) cimFullInstance.getProperty( "PartNumber" ).getValue();
                        String serialNumber = (String) cimFullInstance.getProperty( "SerialNumber" ).getValue();
                        String manufactureDate = (String) cimFullInstance.getProperty( "ManufactureDate" ).getValue();
                        String model = (String) cimFullInstance.getProperty( "Model" ).getValue();
                        long maxMemorySpeedInHertz =
                            ( (UnsignedInteger32) cimFullInstance.getProperty( "MaxMemorySpeed" ).getValue() ).longValue();
                        BigInteger capacityInBytes =
                            ( (UnsignedInteger64) cimFullInstance.getProperty( "Capacity" ).getValue() ).bigIntegerValue();
                        int dataWidth =
                            ( (UnsignedInteger16) cimFullInstance.getProperty( "DataWidth" ).getValue() ).intValue();
                        int totalWidth =
                            ( (UnsignedInteger16) cimFullInstance.getProperty( "TotalWidth" ).getValue() ).intValue();
                        int formFactorCode =
                            ( (UnsignedInteger16) cimFullInstance.getProperty( "FormFactor" ).getValue() ).intValue();
                        int memoryTypeCode =
                            ( (UnsignedInteger16) cimFullInstance.getProperty( "MemoryType" ).getValue() ).intValue();
                        ComponentIdentifier componentIdentifier = new ComponentIdentifier();
                        // Set the Retrieved Properties
                        // memoryInfo.setId(bankLabel);
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
                        if ( (Boolean) cimFullInstance.getProperty( "IsSpeedInMhz" ).getValue() )
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
                }
            }
            return memoryList;
        }
        return null;
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
}
