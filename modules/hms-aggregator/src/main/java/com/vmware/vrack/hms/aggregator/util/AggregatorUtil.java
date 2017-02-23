/* ********************************************************************************
 * AggregatorUtil.java
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
package com.vmware.vrack.hms.aggregator.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.boardvendorservice.api.ib.IInbandService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.rest.model.SetNodePassword;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.bios.BiosInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.esxinfo.HostNameInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.HmsGenericUtil;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

/**
 * The Class AggregatorUtil.
 *
 * @author sgakhar Provides utility functions to be used while aggregating OOB and IB data
 */
public class AggregatorUtil
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( AggregatorUtil.class );

    private static final String HMS_AGENT_REMOVE_SERVER_API_URL = "/api/1.0/hms/host/{hostId}";

    private static final String HMS_AGENT_SET_BMC_PASSWORD_API_URL = "/api/1.0/hms/host/{hostId}/setpassword";

    /**
     * Verifies if the server component is available OOB.
     *
     * @param node the node
     * @param component the component
     * @return true, if is component avilable oob
     */
    public static boolean isComponentAvilableOOB( ServerNode node, ServerComponent component )
    {
        if ( component.getComponentInfoAPI() != null )
            InventoryLoader.getInstance().isServerComponentAvailableOOB( node.getNodeID(),
                                                                         component.getComponentInfoAPI() );
        return false;
    }

    /**
     * Returns InbandService instance for the specified node.
     *
     * @param node the node
     * @return the in band service
     * @throws HmsException the hms exception
     */
    private static IInbandService getInBandService( ServerNode node )
        throws HmsException
    {
        IInbandService service = InBandServiceProvider.getBoardService( node.getServiceObject() );
        return service;
    }

    /**
     * Agregate node in band basic info.
     *
     * @param node the node
     * @param inBandNode the in band node
     */
    private static void agregateNodeInBandBasicInfo( ServerNode node, ServerNode inBandNode )
    {
        try
        {
            MergeDataUtil.mergeInbandData( node, inBandNode, false );
        }
        catch ( HmsException e )
        {
            logger.error( "error while aggregating inband node info(ip, user, password) in oob server node object", e );
        }
    }

    /**
     * Sets Server Component Info got using InBand Service in node object.
     *
     * @param node the node
     * @param component the component
     * @return the server component ib
     * @throws HmsException the hms exception
     */
    @SuppressWarnings( { "deprecation" } )
    public static void getServerComponentIB( ServerNode node, ServerComponent component )
        throws HmsException
    {

        IInbandService service = getInBandService( node );
        ServerNode inBandNode = InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() );
        ServiceHmsNode serviceNode = inBandNode.getServiceObject();
        agregateNodeInBandBasicInfo( node, inBandNode );
        switch ( component )
        {
            case CPU:
                List<CPUInfo> cpuInfo = null;
                cpuInfo = service.getCpuInfo( serviceNode );
                node.setCpuInfo( cpuInfo );
                InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setCpuInfo( cpuInfo );
                break;
            case STORAGE:
                List<HddInfo> hddInfo = null;
                hddInfo = service.getHddInfo( serviceNode );
                node.setHddInfo( hddInfo );
                InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setHddInfo( hddInfo );
                break;
            case STORAGE_CONTROLLER:
                List<StorageControllerInfo> storageControllerInfo = null;
                storageControllerInfo = service.getStorageControllerInfo( serviceNode );
                node.setStorageControllerInfo( storageControllerInfo );
                InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setStorageControllerInfo( storageControllerInfo );
                break;
            case MEMORY:
                List<PhysicalMemory> memoryInfo = null;
                memoryInfo = service.getSystemMemoryInfo( serviceNode );
                node.setPhysicalMemoryInfo( memoryInfo );
                InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setPhysicalMemoryInfo( memoryInfo );
                break;
            case NIC:
                List<EthernetController> nicInfo = null;
                nicInfo = service.getNicInfo( serviceNode );

                /*
                 * Need to be reviewed while reviewing topology code. //TODO: get Additional NIC info of Switch name ,
                 * Port and Mac Details from Switch topology try { NicDataUtil.getAdditionalNicInfo(nicInfo,
                 * node.getNodeID()); } catch(Exception e) { logger.error(
                 * "Exception while getting extra information for nic via NetTopologyElements: " ); }
                 */

                node.setEthernetControllerList( nicInfo );
                InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setEthernetControllerList( nicInfo );
                break;
            case BIOS:
                BiosInfo biosInfo = null;
                biosInfo = service.getBiosInfo( serviceNode );
                if ( biosInfo != null )
                {
                    node.setBiosVersion( biosInfo.getBiosVersion() );
                    node.setBiosReleaseDate( biosInfo.getBiosReleaseDate() );
                    InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setBiosVersion( biosInfo.getBiosVersion() );
                    InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setBiosReleaseDate( biosInfo.getBiosReleaseDate() );
                }

                break;
        }

    }

    /**
     * Sets Server Component Info got using OOB Service in node object Gets Server Component using InBand Service.
     *
     * @param node the node
     * @param component the component
     * @return the server component oob
     * @throws HmsException the hms exception
     */
    @SuppressWarnings( "deprecation" )
    public static void getServerComponentOOB( ServerNode node, ServerComponent component )
        throws HmsException
    {
        String path;

        switch ( component )
        {
            case CPU:
                List<CPUInfo> cpuInfo = null;
                path = Constants.HMS_OOB_CPU_INFO_ENDPOINT.replace( "{host_id}", node.getNodeID() );
                cpuInfo = MonitoringUtil.<CPUInfo>getServerComponentOOB( path );
                node.setCpuInfo( cpuInfo );
                break;
            case STORAGE:
                List<HddInfo> hddInfo = null;
                path = Constants.HMS_OOB_HDD_INFO_ENDPOINT.replace( "{host_id}", node.getNodeID() );
                hddInfo = MonitoringUtil.<HddInfo>getServerComponentOOB( path );
                node.setHddInfo( hddInfo );
                break;
            case MEMORY:
                List<PhysicalMemory> memoryInfo = null;
                path = Constants.HMS_OOB_MEMORY_INFO_ENDPOINT.replace( "{host_id}", node.getNodeID() );
                memoryInfo = MonitoringUtil.<PhysicalMemory>getServerComponentOOB( path );
                node.setPhysicalMemoryInfo( memoryInfo );
                break;
            case NIC:
                List<EthernetController> nicInfo = null;
                path = Constants.HMS_OOB_NIC_INFO_ENDPOINT.replace( "{host_id}", node.getNodeID() );
                nicInfo = MonitoringUtil.<EthernetController>getServerComponentOOB( path );
                node.setEthernetControllerList( nicInfo );
                break;
            case STORAGE_CONTROLLER:
                List<StorageControllerInfo> storageControllerInfo = null;
                path = Constants.HMS_OOB_STORAGE_CONTROLLER_INFO_ENDPOINT.replace( "{host_id}", node.getNodeID() );
                storageControllerInfo = MonitoringUtil.<StorageControllerInfo>getServerComponentOOB( path );
                node.setStorageControllerInfo( storageControllerInfo );
                break;
        }
    }

    /**
     * Gets the response entity.
     *
     * @param <T> the generic type
     * @param responseBody the response body
     * @param httpStatus the http status
     * @return the response entity
     */
    public static <T> ResponseEntity<T> getResponseEntity( T responseBody, HttpStatus httpStatus )
    {
        if ( responseBody == null )
        {
            return new ResponseEntity<T>( httpStatus );
        }
        else
        {
            return new ResponseEntity<T>( responseBody, httpStatus );
        }
    }

    /**
     * Gets the response entity.
     *
     * @param <T> the generic type
     * @param httpStatus the http status
     * @return the response entity
     */
    public static <T> ResponseEntity<T> getResponseEntity( HttpStatus httpStatus )
    {
        return AggregatorUtil.getResponseEntity( null, httpStatus );
    }

    /**
     * Invokes HMS Agent API for removing sever from it's inventory.
     *
     * @param hostId the host id
     */
    public static ResponseEntity<BaseResponse> removeServer( final String hostId )
    {
        if ( StringUtils.isBlank( hostId ) )
        {
            logger.error( "In removeServer, hostId is either null or blank." );
            return null;
        }
        ResponseEntity<BaseResponse> hmsAgentResponse = null;
        String oobApiUrl = HMS_AGENT_REMOVE_SERVER_API_URL.replace( "{hostId}", hostId );
        HttpMethod httpMethod = HttpMethod.DELETE;
        try
        {
            HmsOobAgentRestTemplate<BaseResponse> restTemplate =
                new HmsOobAgentRestTemplate<BaseResponse>( null, MediaType.APPLICATION_JSON_VALUE );
            hmsAgentResponse = restTemplate.exchange( httpMethod, oobApiUrl, null, BaseResponse.class );
            if ( hmsAgentResponse != null )
            {
                HttpStatus httpStatus = hmsAgentResponse.getStatusCode();
                logger.debug( "In removeServer, HMS Agent returned {}({}) for {} on {}.", httpStatus.value(),
                              httpStatus.getReasonPhrase(), httpMethod, oobApiUrl );
            }
        }
        catch ( RestClientException | HmsException e )
        {
            logger.error( "In removeServer, error while invoking {} on {}.", httpMethod, oobApiUrl, e );
        }
        return hmsAgentResponse;
    }

    /**
     * Change BMC password
     * 
     * @param nodeId -the host whose IPMI password needs to be reset
     * @param newPassword - the password to be reset to
     */
    public static ResponseEntity<BaseResponse> changeHostIpmiPassword( String nodeId, String newPassword )
        throws HMSRestException, RestClientException
    {

        ServerNode serverNode = InventoryLoader.getInstance().getNode( nodeId );
        SetNodePassword setNodeIPMIPassword = new SetNodePassword();

        setNodeIPMIPassword.setUsername( serverNode.getManagementUserName() );
        setNodeIPMIPassword.setCurrentPassword( serverNode.getManagementUserPassword() );
        setNodeIPMIPassword.setNewPassword( newPassword );

        ResponseEntity<BaseResponse> oobResponse = null;
        String oobApiUrl = HMS_AGENT_SET_BMC_PASSWORD_API_URL.replace( "{hostId}", nodeId );
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON.toString() );
        headers.add( "Accept", MediaType.APPLICATION_JSON.toString() );

        try
        {
            HmsOobAgentRestTemplate<SetNodePassword> restTemplate =
                new HmsOobAgentRestTemplate<SetNodePassword>( setNodeIPMIPassword, headers );

            oobResponse = restTemplate.exchange( HttpMethod.PUT, oobApiUrl, BaseResponse.class );
            if ( oobResponse != null )
            {
                if ( oobResponse.getStatusCode() == HttpStatus.OK )
                {
                    logger.debug( "IPMI password successfully reset for host {} ", nodeId );

                }
                else
                {
                    logger.debug( "Error resetting IPMI password for host {}. Status Code: {}, Response Body: {}",
                                  nodeId, oobResponse.getStatusCode(), oobResponse.getBody().toString() );
                }
            }
            else
            {
                logger.debug( "Received null response :Reset the IPMI password for host {}", nodeId );
            }
        }
        catch ( HmsException e )
        {
            logger.error( "Error while resetting IPMI pasword for Node: {}.", nodeId, e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return oobResponse;
    }

    /**
     * Converts the encrypted passwords to plain-text
     *
     * @param inventoryMap
     * @return
     * @throws HMSRestException
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static void populatePlainTextPasswords( Map<String, Object[]> inventoryMap )
        throws HMSRestException
    {

        if ( inventoryMap != null )
        {
            Object[] hosts = inventoryMap.get( Constants.HOSTS );
            Object[] switches = inventoryMap.get( Constants.SWITCHES );

            if ( hosts == null || switches == null )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                            "hosts/switches are null in the provided input file",
                                            "invalid inventory file" );
            }
            ObjectMapper mapper = new ObjectMapper();
            ServerNode[] serverNodes = mapper.convertValue( hosts, new TypeReference<ServerNode[]>()
            {
            } );

            SwitchNode[] switchNodes = mapper.convertValue( switches, new TypeReference<SwitchNode[]>()
            {
            } );

            decryptPassword( serverNodes );
            decryptPassword( switchNodes );

            Object[] serverNodeObj = mapper.convertValue( serverNodes, new TypeReference<Object[]>()
            {
            } );

            Object[] switchNodeObj = mapper.convertValue( switchNodes, new TypeReference<Object[]>()
            {
            } );
            inventoryMap.put( Constants.HOSTS, serverNodeObj );
            inventoryMap.put( Constants.SWITCHES, switchNodeObj );
        }
    }

    /**
     * @param switchNodes
     */
    private static void decryptPassword( SwitchNode[] switchNodes )
    {
        if ( switchNodes != null && switchNodes.length > 0 )
        {
            for ( SwitchNode switchNode : switchNodes )
            {
                switchNode.setPassword( switchNode.getPassword() );
            }
        }
    }

    /**
     * @param serverNodes
     */
    private static void decryptPassword( ServerNode[] serverNodes )
    {
        if ( serverNodes != null && serverNodes.length > 0 )
        {
            for ( ServerNode serverNode : serverNodes )
            {
                serverNode.setOsPassword( serverNode.getOsPassword() );
                serverNode.setManagementUserPassword( serverNode.getManagementUserPassword() );
            }
        }
    }

    /**
     * Method to identify if the ESXI host is reachable or not.
     *
     * @param serverNode
     * @param sshRetryCount
     * @param sshRetryDelay
     * @return
     * @throws HmsException
     */
    public static boolean isEsxiHostReachable( ServerNode serverNode, int sshRetryCount, int sshRetryDelay )
        throws HmsException
    {

        logger.debug( "SSH to the host: {}, sshRetryCount: {}, sshRetryDelay: {}", serverNode.getIbIpAddress(),
                      sshRetryCount, sshRetryDelay );
        IInbandService inbandService = getInBandService( serverNode );

        for ( int i = 0; i < sshRetryCount; i++ )
        {
            try
            {
                HostNameInfo hostNameInfo = inbandService.getHostName( serverNode.getServiceObject() );

                if ( hostNameInfo != null && StringUtils.isNotBlank( hostNameInfo.getHostName() ) )
                {
                    logger.debug( "Node found to be reachable for the nodeId: {} & ipAddress: {}",
                                  serverNode.getNodeID(), serverNode.getIbIpAddress() );

                    return true;
                }
            }
            catch ( HmsException e )
            {
                logger.error( "Exception occured finding reachability status for the nodeId: {} & ipAddress: {} & retryIndex: {}",
                              serverNode.getNodeID(), serverNode.getIbIpAddress(), i );

                if ( i < sshRetryCount - 1 )
                {
                    HmsGenericUtil.sleepThread( false, sshRetryDelay );
                }
            }
        }

        logger.warn( "Node found to be not reachable for the nodeId: {} & ipAddress: {}", serverNode.getNodeID(),
                     serverNode.getIbIpAddress() );

        return false;
    }
}
