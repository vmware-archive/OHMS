/* ********************************************************************************
 * CpuInfoOperationalStatusHelper.java
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.UnsignedInteger16;
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
import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUStatusEnum;

@Component
public class CpuInfoOperationalStatusHelper
{

    private static Logger logger = LoggerFactory.getLogger( CpuInfoOperationalStatusHelper.class );

    public WBEMClient cimClient;

    ServiceServerNode node;

    private static int cimPort;

    private static int cimClientTimeoutInMs;

    @Value( "${cim.port:5989}" )
    public void setCimPort( int cimPort )
    {
        CpuInfoOperationalStatusHelper.cimPort = cimPort;
    }

    @Value( "${cim.connection.timeout.ms:20000}" )
    public void setCimClientTimeoutInMs( int cimClientTimeoutInMs )
    {
        CpuInfoOperationalStatusHelper.cimClientTimeoutInMs = cimClientTimeoutInMs;
    }

    @SuppressWarnings( "unused" )
    private CpuInfoOperationalStatusHelper()
    {

    }

    public CpuInfoOperationalStatusHelper( ServiceServerNode node )
    {
        this.node = node;
    }

    /**
     * Get the CPU status and Health status information using CIM.
     *
     * @return List<CPUInfo>
     * @throws Exception
     */
    public List<CPUInfo> getCpuOperationalStatus( List<CPUInfo> cpuInfoList )
        throws Exception
    {
        try
        {
            getClient();
            logger.info( "CIM client connection success for OMC_Processor" );
            return executeTask( cpuInfoList );
        }
        catch ( Exception e )
        {
            logger.error( "CIM client connection failed, couldn't get the CPU information with operational status", e );
            throw new HmsException( "Could not get the CPU information with operational status using CIM client... failed: ",
                                    e );
        }
        finally
        {
            destroy();
            logger.info( "CIM client connection destroy successful" );
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
            URL cimomUrl = new URL( "https://" + node.getIbIpAddress() + ":" + cimPort );
            cimClient = WBEMClientFactory.getClient( WBEMClientConstants.PROTOCOL_CIMXML );
            final CIMObjectPath path = new CIMObjectPath( cimomUrl.getProtocol(), cimomUrl.getHost(),
                                                          String.valueOf( cimomUrl.getPort() ), null, null, null );
            final Subject subject = new Subject();
            subject.getPrincipals().add( new UserPrincipal( node.getOsUserName() ) );
            subject.getPrivateCredentials().add( new PasswordCredential( node.getOsPassword()) );

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
            logger.error( "Error while creating connection to the cimClient: ", e );
            throw new HmsException( "Error while creating connection to the cimClient: ", e );
        }
    }

    /**
     * Destroy CIM client connection
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
                logger.error( "Error while closing the cimClient connection: ", e );
            }
        }
    }

    /**
     * Method helps to get the processor Information using CIM class OMC_Processor
     *
     * @return List<CPUInfo>
     * @throws Exception
     */
    private List<CPUInfo> executeTask( List<CPUInfo> cpuInfoList )
        throws Exception
    {
        try
        {
            List<CIMInstance> instancesList = new ArrayList<CIMInstance>();

            logger.debug( "Starting to get /root/cimv2:OMC_Processor for node with IB IP address: {}",
                          node.getIbIpAddress() );

            CloseableIterator<CIMInstance> storageIterator =
                cimClient.enumerateInstances( new CIMObjectPath( "/root/cimv2:OMC_Processor" ), true, true, false,
                                              null );

            logger.debug( "Finished getting /root/cimv2:OMC_Processor for node with IB IP address: {}",
                          node.getIbIpAddress() );

            try
            {
                while ( storageIterator.hasNext() )
                {
                    final CIMInstance instanceIterator = storageIterator.next();
                    instancesList.add( instanceIterator );
                }
                return getProcessorInformation( cimClient, instancesList, cpuInfoList );
            }
            catch ( Exception e )
            {
                logger.error( "Error while getting processor Information: OMC_Processor ", e );
                throw new HmsException( e );
            }
        }
        catch ( WBEMException e )
        {
            logger.error( "Error while enumerating processor Information: OMC_Processor ", e );
            throw new HmsException( e );
        }
    }

    /**
     * Helper method to get the processor Information operational status
     *
     * @param client
     * @param instancesList
     * @return FruOperationalStatus
     * @throws HmsException
     */
    public List<CPUInfo> getProcessorInformation( WBEMClient client, List<CIMInstance> instancesList,
                                                  List<CPUInfo> cpuInfoList )
        throws HmsException
    {

        CPUStatusEnum operationalCpuStatus = null;
        CPUStatusEnum operationalHealthStatus = null;
        FruOperationalStatus fruOperationalStatus = null;

        if ( client != null && instancesList != null )
        {

            for ( int i = 0; i < instancesList.size(); i++ )
            {
                final CIMInstance instance = instancesList.get( i );

                String elementName = (String) instance.getProperty( "ElementName" ).getValue();
                int cpuStatus = ( (UnsignedInteger16) instance.getProperty( "CPUStatus" ).getValue() ).intValue();
                operationalCpuStatus = getOperationalStatusOfProcessor( cpuStatus );
                int healthState = ( (UnsignedInteger16) instance.getProperty( "HealthState" ).getValue() ).intValue();
                operationalHealthStatus = getOperationalStatusOfProcessor( healthState );

                if ( operationalCpuStatus != null && operationalHealthStatus != null )
                {

                    if ( CPUInfo.getCpuOperationalState( operationalHealthStatus ).equals( FruOperationalStatus.Operational )
                        && CPUInfo.getCpuOperationalState( operationalCpuStatus ).equals( FruOperationalStatus.Operational ) )
                    {
                        fruOperationalStatus = FruOperationalStatus.Operational;
                    }
                    else if ( CPUInfo.getCpuOperationalState( operationalHealthStatus ).equals( FruOperationalStatus.NonOperational )
                        && CPUInfo.getCpuOperationalState( operationalCpuStatus ).equals( FruOperationalStatus.NonOperational ) )
                    {
                        fruOperationalStatus = FruOperationalStatus.NonOperational;
                    }
                    else if ( CPUInfo.getCpuOperationalState( operationalHealthStatus ).equals( FruOperationalStatus.UnKnown )
                        && CPUInfo.getCpuOperationalState( operationalCpuStatus ).equals( FruOperationalStatus.UnKnown ) )
                    {
                        fruOperationalStatus = FruOperationalStatus.UnKnown;
                    }

                    cpuInfoList.get( i ).setFruOperationalStatus( fruOperationalStatus );
                    // cpuInfoList.get(i).setLocation(elementName);
                }
            }
        }
        return cpuInfoList;
    }

    private CPUStatusEnum getOperationalStatusOfProcessor( int status )
    {

        logger.debug( "CPU health status is: {}", CPUStatusCIMEnum.getCpuHealthState( status ) );

        switch ( CPUStatusCIMEnum.getCpuHealthState( status ) )
        {
            case Ok:
            case CPU_Enabled:
            case CPU_Is_Idle:
                return CPUStatusEnum.OK;
            case Degraded:
            case Minor_Failure:
                return CPUStatusEnum.DEGRADED;
            case CPU_Disabled_By_BIOS:
            case CPU_Disabled_By_User:
                return CPUStatusEnum.DISABLED;
            case Major_Failure:
            case Critical_Failure:
                return CPUStatusEnum.FAILURE;
            case Non_Recoverable_Error:
                return CPUStatusEnum.UNCORRECTABLE_ERROR;
            case Unknown:
            case Other:
                return CPUStatusEnum.UNKNOWN;
            default:
                logger.debug( "Couldn't map the CPU health status" );
                return CPUStatusEnum.UNKNOWN;
        }
    }

}
