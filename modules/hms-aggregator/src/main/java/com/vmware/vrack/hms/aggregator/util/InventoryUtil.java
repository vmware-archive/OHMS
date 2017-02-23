/* ********************************************************************************
 * InventoryUtil.java
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.configuration.HmsInventoryConfiguration;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.FileUtil;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;

public class InventoryUtil
{
    private static Logger logger = LoggerFactory.getLogger( InventoryUtil.class );

    public static final String NODE_ID = "nodeID";

    public static final String IB_IP_ADDRESS = "ibIpAddress";

    public static final String USERNAME = "osUserName";

    public static final String PASSWORD = "osPassword";

    private static boolean inventoryLoaded = false;

    /**
     * Initialize HMS inventory during Bootup. Will try to load inventory from defined file first If that file does not
     * exist, Will try OOB
     *
     * @param inventoryFilePath
     * @param hmsIpAddr
     * @param hmsPort
     * @param path
     * @param contentType
     * @return
     * @throws HmsException
     */
    public static boolean initializeInventory( String inventoryFilePath, String path )
        throws HmsException
    {

        inventoryLoaded = false;
        final Map<String, Object[]> nodes = initializeInventory( inventoryFilePath );

        if ( nodes == null )
        {
            logger.error( "Exception occured while initializing the inventory from the path: {}", inventoryFilePath );
            throw new HmsException( "Can't initialize the inventory from the file: " + inventoryFilePath );
        }
        // Refresh Node data and refresh Inband Data too
        MergeDataUtil.refreshNodeData( nodes, false );
        inventoryLoaded = true;
        MonitoringUtil.startMonitoringForAllNodes( nodes );

        logger.debug( "Available Inband NodeMap: {}", InventoryLoader.getInstance().getNodeMap() );
        return true;

    }

    /**
     * Reads HMS-inventory file from filesystem path provided as argument and returns it as Map.
     *
     * @param inventoryFilePath
     * @return
     * @throws HmsException
     */
    public static Map<String, Object[]> initializeInventory( String inventoryFilePath )
        throws HmsException
    {
        if ( StringUtils.isBlank( inventoryFilePath ) )
        {
            throw new HmsException( "In initializeInventory, inventory file is either null or blank." );
        }
        if ( FileUtil.isFileExists( inventoryFilePath ) )
        {
            logger.debug( "Reading from Hms. Inventory File found at {}.", inventoryFilePath );
            try
            {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue( new File( inventoryFilePath ), new TypeReference<Map<String, Object[]>>()
                {
                } );
            }
            catch ( IOException e )
            {
                logger.error( "In initializeInventory, error reading inventory file '{}'.", inventoryFilePath, e );
            }
        }
        else
        {
            logger.info( "In initializeInventory, inventory file '{}' does not exist.", inventoryFilePath );
        }
        return null;
    }

    /**
     * Get Inventory information via OOB
     *
     * @param hmsIpAddr
     * @param hmsPort
     * @param path
     * @param contentType
     * @return
     * @throws HMSRestException
     */
    public static ResponseEntity<HashMap<String, Object[]>> getInventoryOOB( String path, String contentType )
        throws HMSRestException
    {
        logger.debug( String.format( "Trying to get inventory OOB. Url fo r OOB operation is Path: %s, Content Type: %s ",
                                     path, contentType ) );

        ResponseEntity<HashMap<String, Object[]>> oobResponse = null;

        if ( path != null )
        {
            try
            {
                HmsOobAgentRestTemplate<Object> restTemplate = new HmsOobAgentRestTemplate<Object>( null, contentType );
                ParameterizedTypeReference<HashMap<String, Object[]>> typeRef =
                    new ParameterizedTypeReference<HashMap<String, Object[]>>()
                    {
                    };
                oobResponse = restTemplate.exchange( HttpMethod.GET, path, typeRef );
            }
            catch ( HttpStatusCodeException e )
            {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType( MediaType.APPLICATION_JSON );
                logger.error( "Http Status Code Exception when trying to get hms/nodes: " + e );
            }
            catch ( Exception e )
            {
                logger.error( "Error while trying to get nodes: " + e );
                throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                            "Exception while connecting to hms." + ( ( path != null ) ? path : "" ) );
            }
        }
        else
        {
            logger.error( String.format( "Unable to get inventory from OOB because some Final OOB Url cannot be constructed. "
                + "Url for OOB operation is Path: %s, Content Type: %s ", path, contentType ) );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while connecting to hms. Unable to create Url" );
        }
        return oobResponse;
    }

    /**
     * Put Inventory information to OOB
     *
     * @param hmsIpAddr
     * @param hmsPort
     * @param path
     * @param contentType
     * @param conf
     * @return
     * @throws HMSRestException
     */
    public static void putInventoryOOB( String path, String contentType, HmsInventoryConfiguration conf )
        throws HMSRestException
    {
        logger.debug( String.format( "Trying to put inventory OOB. Url for OOB operation is Path: %s, Content Type: %s ",
                                     path, contentType ) );

        if ( path != null )
        {
            try
            {
                ObjectMapper objectMapper = new ObjectMapper();
                OutputStream os = new ByteArrayOutputStream();
                objectMapper.writeValue( os, conf );
                String body = os.toString();
                HmsOobAgentRestTemplate<String> restTemplate = new HmsOobAgentRestTemplate<String>( body, contentType );
                restTemplate.put( path );
            }
            catch ( Exception e )
            {
                logger.error( "Error while trying to put hms inventory: ", e );
                throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                            "Exception while connecting to hms." + ( ( path != null ) ? path : "" ) );
            }
        }
        else
        {
            logger.error( String.format( "Unable to put inventory to OOB because some Final OOB Url cannot be constructed. "
                + "Url for OOB operation is Path: %s, Content Type: %s ", path, contentType ) );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while connecting to hms. Unable to create Url" );
        }
    }

    /**
     * Get Inventory information via OOB
     *
     * @param hmsIpAddr
     * @param hmsPort
     * @param path
     * @param contentType
     * @return
     * @throws HMSRestException
     */
    public static HashMap<String, List<HmsApi>> getOOBSupportedOperations( String path, String contentType )
        throws HMSRestException
    {
        logger.debug( String.format( "Trying to get inventory OOB. Url for OOB operation - Path: %s, Content Type: %s ",
                                     path, contentType ) );

        ResponseEntity<HashMap<String, List<HmsApi>>> oobResponse = null;

        if ( path != null )
        {
            try
            {
                HmsOobAgentRestTemplate<Object> restTemplate = new HmsOobAgentRestTemplate<Object>( null, contentType );
                ParameterizedTypeReference<HashMap<String, List<HmsApi>>> typeRef =
                    new ParameterizedTypeReference<HashMap<String, List<HmsApi>>>()
                    {
                    };
                oobResponse = restTemplate.exchange( HttpMethod.GET, path, typeRef );
            }
            catch ( HttpStatusCodeException e )
            {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType( MediaType.APPLICATION_JSON );
                logger.error( "Http Status Code Exception when trying to get oob operations: " + e );
            }
            catch ( Exception e )
            {
                logger.error( "Error while trying to get nodes: " + e );
                throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                            "Exception while connecting to hms."
                                                + ( ( path != null ) ? path.toString() : "" ) );
            }
        }
        else
        {
            logger.error( String.format( "Unable to get OOB supported operations because some Final OOB Url cannot be constructed. "
                + "Url for OOB operation is Path: %s, Content Type: %s ", path, contentType ) );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while connecting to hms. Unable to create Url" );
        }
        return oobResponse.getBody();
    }

    /**
     * Returns updated map of ServerInfos given a list of request Server Info
     *
     * @param hmsIbInventoryLocation
     * @param requestServerInfoList
     * @return
     * @throws HmsException
     */
    @SuppressWarnings( "unchecked" )
    public static Map<String, Object> getUpdatedServerInfos( Object[] hostsToBeUpdated,
                                                             List<ServerInfo> requestServerInfoList )
        throws HmsException
    {

        Map<String, Object> serverInfoMap = new HashMap<String, Object>();

        if ( hostsToBeUpdated != null && hostsToBeUpdated.length > 0 && requestServerInfoList != null )
        {
            for ( Object serverInfoObj : hostsToBeUpdated )
            {
                serverInfoMap.put( ( (Map<String, Object>) serverInfoObj ).get( NODE_ID ).toString(), serverInfoObj );
            }

            // Iterate through all ServerInfos in request, and get corresponding
            // node from the Map created in prev step, and update with latest
            // data
            for ( ServerInfo requestServerInfo : requestServerInfoList )
            {

                Map<String, Object> serverInfo =
                    (Map<String, Object>) ( serverInfoMap.get( requestServerInfo.getNodeId() ) );
                if ( serverInfo != null )
                {
                    // Update InbandIPAddress if passed in request
                    if ( StringUtils.isNotBlank( requestServerInfo.getInBandIpAddress() ) )
                    {
                        logger.debug( String.format( "Got IP address [%s]  to update for node [%s]",
                                                     requestServerInfo.getInBandIpAddress(),
                                                     requestServerInfo.getNodeId() ) );
                        serverInfo.put( IB_IP_ADDRESS, requestServerInfo.getInBandIpAddress() );
                    }

                    // Update OsUserName if passed in request
                    if ( StringUtils.isNotBlank( requestServerInfo.getInBandUserName() ) )
                    {
                        logger.debug( String.format( "Got OsUserName to update for node [%s]",
                                                     requestServerInfo.getNodeId() ) );
                        serverInfo.put( USERNAME, requestServerInfo.getInBandUserName() );
                    }

                    // Update OsPassword if passed in request
                    if ( StringUtils.isNotBlank( requestServerInfo.getInBandPassword() ) )
                    {
                        logger.debug( String.format( "Got OsPassword to update for node [%s]",
                                                     requestServerInfo.getNodeId() ) );
                        serverInfo.put( PASSWORD, requestServerInfo.getInBandPassword() );
                    }
                }
            }
        }
        else
        {
            logger.warn( String.format( "Not updating Inventory. The hosts in the inventory is [%s]. Server Info List from request is [%s] ",
                                        ( hostsToBeUpdated == null ? null : hostsToBeUpdated.length ),
                                        ( requestServerInfoList == null ? null : requestServerInfoList.size() ) ) );
        }

        return serverInfoMap;

    }

    /**
     * Returns a list of invalid hosts, given the list of hosts in inventory and list of hosts in the request
     *
     * @param inventoryHosts
     * @param requestServerInfoList
     * @return
     * @throws HmsException
     */
    @SuppressWarnings( "unchecked" )
    public static List<String> getInvalidHostsinRequest( Object[] inventoryHosts,
                                                         List<ServerInfo> requestServerInfoList )
        throws HmsException
    {

        Map<String, Object> serverInfoMap = new HashMap<String, Object>();
        List<String> invalidhostsFromRequest = new ArrayList<String>();

        if ( inventoryHosts != null && inventoryHosts.length > 0 && requestServerInfoList != null )
        {
            for ( Object serverInfoObj : inventoryHosts )
            {
                serverInfoMap.put( ( (Map<String, Object>) serverInfoObj ).get( NODE_ID ).toString(), serverInfoObj );
            }

            for ( ServerInfo serverInfo : requestServerInfoList )
            {
                if ( !serverInfoMap.containsKey( serverInfo.getNodeId() ) )
                {
                    invalidhostsFromRequest.add( serverInfo.getNodeId() );
                }
            }
        }

        return invalidhostsFromRequest;

    }

    /**
     * Creates the or update inventory file.
     *
     * @param inventoryFilePath the inventory file path
     * @param inventoryFileContent the inventory file content
     * @return true, if successful
     * @throws HmsException the hms exception
     */
    public static boolean createOrUpdateInventoryFile( final String inventoryFilePath,
                                                       final String inventoryFileContent )
        throws HmsException
    {
        return InventoryUtil.createOrUpdateInventoryFile( inventoryFilePath, inventoryFileContent, false );
    }

    /**
     * Creates the or update inventory file.
     *
     * @param inventoryFilePath the inventory file path
     * @param inventoryFileContent the inventory file content
     * @param createBackup the create backup
     * @return true, if successful
     * @throws HmsException the hms exception
     */
    public static boolean createOrUpdateInventoryFile( final String inventoryFilePath,
                                                       final String inventoryFileContent, final boolean createBackup )
        throws HmsException
    {
        boolean createdOrUpdated = FileUtil.createOrUpdateFile( inventoryFilePath, inventoryFileContent, createBackup );
        if ( !createdOrUpdated )
        {
            throw new HmsException( "Failed to create or update inventory file '" + inventoryFilePath + "'." );
        }
        else
        {
            return true;
        }
    }

    /**
     * Removes the server.
     *
     * @param inventoryFilePath the inventory file path
     * @param hostId the host id
     * @return true, if successful
     * @throws HmsException the hms exception
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonGenerationException
     */
    public static boolean removeServer( final String inventoryFilePath, final String hostId )
        throws HmsException, JsonGenerationException, JsonMappingException, IOException
    {

        // first read the inventory file.
        Map<String, Object[]> inventory = InventoryUtil.initializeInventory( inventoryFilePath );
        if ( inventory == null || !inventory.containsKey( Constants.HOSTS ) )
        {
            logger.error( "In removeServer, invalid inventory configuration in the file {}.", inventoryFilePath );
            return false;
        }

        // iterate through server and remove server entry from hosts list.
        ObjectMapper objectMapper = new ObjectMapper();
        Object[] hosts = inventory.get( Constants.HOSTS );
        if ( hosts != null && hosts.length > 0 )
        {
            List<ServerNode> serverNodesList = objectMapper.convertValue( hosts, new TypeReference<List<ServerNode>>()
            {
            } );
            ServerNode serverNode = null;
            for ( Iterator<ServerNode> iterator = serverNodesList.iterator(); iterator.hasNext(); )
            {
                serverNode = iterator.next();
                if ( StringUtils.equals( hostId, serverNode.getNodeID() ) )
                {
                    iterator.remove();
                    logger.debug( "In removeServer, removed hostId '{}' from the inventory '{}'.", hostId,
                                  inventoryFilePath );
                    break;
                }
            }
            inventory.put( Constants.HOSTS, serverNodesList.toArray() );
        }

        // save the data back to the file.
        String inventoryFileContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString( inventory );
        return InventoryUtil.createOrUpdateInventoryFile( inventoryFilePath, inventoryFileContent, true );
    }

    /**
     * Update inventory node map.
     *
     * @param inventoryFileAbsPath the inventory file abs path
     * @return true, if successful
     * @throws HmsException the hms exception
     */
    public static boolean updateInventoryNodeMap( final String inventoryFileAbsPath )
        throws HmsException
    {
        Map<String, Object[]> inventory = InventoryUtil.initializeInventory( inventoryFileAbsPath );
        if ( inventory == null || !inventory.containsKey( Constants.HOSTS ) )
        {
            logger.info( "In updateInventory, invalid inventory configuration in the file '{}'. "
                + "Inventory is either null or does not contain '{}'.", inventoryFileAbsPath, Constants.HOSTS );
            return false;
        }
        Object[] hosts = inventory.get( Constants.HOSTS );
        if ( hosts == null || hosts.length == 0 )
        {
            logger.info( "In updateInventory, invalid inventory configuration in the file '{}'. "
                + "Does not contain any host configuration(s).", inventoryFileAbsPath );
            return false;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ServerNode[] serverNodes = objectMapper.convertValue( hosts, new TypeReference<ServerNode[]>()
        {
        } );
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        ServerNode serverNode = null;
        for ( int index = 0; index < serverNodes.length; index++ )
        {
            serverNode = serverNodes[index];
            nodeMap.put( serverNode.getNodeID(), serverNode );
        }

        /*
         * TODO: Need to see the implications of updating nodeMap, if any.
         */
        InventoryLoader.getInstance().setNodeMap( nodeMap );
        return true;
    }

    /**
     * Method to identify if the oob agent's inventory is loaded already or not.
     *
     * @return
     * @throws RestClientException
     * @throws HmsException
     */
    public static boolean isOobAgentInventoryLoaded()
        throws RestClientException, HmsException
    {
        HmsOobAgentRestTemplate<Object> restTemplate = new HmsOobAgentRestTemplate<Object>();
        ResponseEntity<Boolean> response =
            restTemplate.exchange( HttpMethod.GET, Constants.HMS_OOB_INVENTORY_IS_LOADED, Boolean.class );
        if ( response != null && response.getBody() == true )
        {
            return true;
        }
        return false;
    }

    /**
     * Is used to check if inventory loading got completed
     * 
     * @return
     */
    public static boolean isInventoryLoaded()
    {
        return inventoryLoaded;
    }

    /**
     * This method is responsible for pushing the inventory on Out of band agent
     *
     * @param source
     * @throws HMSRestException
     */
    public static boolean refreshInventoryOnOutOfBand()
    {
        logger.debug( "push inventory to Out of band starts" );

        String hmsIbInventoryLocation = SpringContextHelper.getIbInventoryLocaiton();
        if ( hmsIbInventoryLocation != null )
        {
            try
            {
                Map<String, Object[]> inventoryMap = InventoryUtil.initializeInventory( hmsIbInventoryLocation );
                AggregatorUtil.populatePlainTextPasswords( inventoryMap );

                HmsOobAgentRestTemplate<Map<String, Object[]>> restTemplate =
                    new HmsOobAgentRestTemplate<Map<String, Object[]>>( inventoryMap );

                ResponseEntity<Object> response =
                    restTemplate.exchange( HttpMethod.PUT, Constants.HMS_OOB_INVENTORY_RELOAD, Object.class );

                if ( response != null && response.getStatusCode() == HttpStatus.OK )
                {
                    logger.info( "push inventory to Out of band ends successfully" );
                    return true;
                }

                String exceptionMsg =
                    String.format( "Unsuccessful response: {} from: %s, , can't complete inventory refresh on Out of band",
                                   response, Constants.HMS_OOB_INVENTORY_RELOAD );

                logger.error( exceptionMsg );

            }
            catch ( Throwable e )
            {
                logger.error( "Exception occured while loading the inventory on Hms oob agent: {}", e );
            }
        }
        else
        {
            String err =
                String.format( "invalid inventory location: %s, can't complete inventory refresh on Out of band",
                               hmsIbInventoryLocation );
            logger.error( err );
        }
        return false;
    }
}
