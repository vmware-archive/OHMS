/* ********************************************************************************
 * InventoryUtil.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.inventory.InventoryLoader;

public class InventoryUtil
{
    public static Logger logger = Logger.getLogger( InventoryUtil.class );

    /**
     * Check if the file exist in FileSystem
     *
     * @param path
     * @return
     */
    public static boolean isFileExists( String path )
    {
        try
        {
            if ( path != null && !"".equals( path.trim() ) )
            {
                File f = new File( path );
                if ( f.exists() )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                logger.error( "File location is incorrect. Path: " + path );
                return false;
            }
        }
        catch ( Exception t )
        {
            logger.error( "Error while confirming file's existence at path:" + path );
            return false;
        }
    }

    /**
     * Write inventory file with the content defined
     *
     * @param inventoryFilePath
     * @param content
     * @return
     * @throws HmsException
     */
    public static boolean writeFile( String inventoryFilePath, String content )
        throws HmsException
    {
        if ( inventoryFilePath != null && content != null )
        {
            try
            {
                File file = new File( inventoryFilePath );
                if ( file.createNewFile() )
                {
                    logger.debug( "Inventory file created at: " + inventoryFilePath );
                    Writer writer = null;
                    try
                    {
                        writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( inventoryFilePath ),
                                                                             "utf-8" ) );
                        writer.write( content );
                    }
                    catch ( IOException ex )
                    {
                        String err = "Unable to write inventory at " + inventoryFilePath;
                        logger.error( err, ex );
                        throw new HmsException( err, ex );
                    }
                    finally
                    {
                        try
                        {
                            writer.close();
                        }
                        catch ( Exception ex )
                        {
                            logger.error( "Unable to close writer.", ex );
                        }
                    }
                    return true;
                }
                else
                {
                    String err = "Failed to write inventory file at " + inventoryFilePath;
                    logger.error( err );
                    throw new HmsException( err );
                }
            }
            catch ( IOException e )
            {
                String err = "Unable to create and write inventory data at " + inventoryFilePath;
                logger.error( err, e );
                throw new HmsException( err );
            }
        }
        else
        {
            String err = "Failed to create inventory file at " + inventoryFilePath + "and content [" + content + "]";
            logger.error( err );
            throw new HmsException( err );
        }
    }

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
    public static boolean initializeInventory( String inventoryFilePath, String hmsIpAddr, Integer hmsPort,
                                               String path )
                                                   throws HmsException
    {
        Map<String, Object[]> nodes = null;
        try
        {
            // Try to read from the inventory file stored locally first.
            nodes = initializeInventory( inventoryFilePath );
        }
        catch ( Exception e )
        {
            // ignore as we will attempt to read nodes info from HMS-OOB
        }
        if ( nodes == null )
        {
            // If nodes info are not available locally, try to read it from HMS-OOB and save it locally in inventoryFile
            ObjectMapper mapper = new ObjectMapper();
            logger.debug( "Cannot find inventory file at: " + inventoryFilePath + " Now Trying with OOB" );
            try
            {
                ResponseEntity<HashMap<String, Object[]>> oobResponse =
                    getInventoryOOB( hmsIpAddr, hmsPort, path, "application/json" );
                nodes = oobResponse.getBody();
                try
                {
                    writeFile( inventoryFilePath, mapper.writerWithDefaultPrettyPrinter().writeValueAsString( nodes ) );
                }
                catch ( IOException e )
                {
                    String err = "Unable to write inventory data to file: " + inventoryFilePath;
                    logger.error( err, e );
                    throw new HmsException( err, e );
                }
                catch ( HmsException e )
                {
                    String err = "Unable to write inventory data to file: " + inventoryFilePath;
                    logger.error( err, e );
                    throw new HmsException( err, e );
                }
            }
            catch ( HmsException e )
            {
                String err = "Unable to get Hms Inventory data via OOB.";
                logger.error( err, e );
                throw new HmsException( err, e );
            }
        }
        // Refresh Node data and refresh Inband Data too
        MergeDataUtil.refreshNodeData( nodes, false );
        MonitoringUtil.startMonitoringForAllNodes( nodes );
        logger.debug( "Available Inband NodeMap : " + InventoryLoader.getInstance().getNodeMap() );
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
        if ( inventoryFilePath == null || "".equals( inventoryFilePath.trim() ) )
        {
            throw new HmsException( "Unable to create file at location: " + inventoryFilePath );
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object[]> nodes = null;
        if ( isFileExists( inventoryFilePath ) )
        {
            logger.debug( "Reading from Hms. Inventory File found at " + inventoryFilePath );
            try
            {
                nodes = mapper.readValue( new File( inventoryFilePath ), new TypeReference<Map<String, Object[]>>()
                {
                } );
            }
            catch ( IOException e )
            {
                String err = "Exception occured while trying to read inventory File at:" + inventoryFilePath;
                logger.error( err, e );
                return null;
            }
            return nodes;
        }
        else
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
    public static ResponseEntity<HashMap<String, Object[]>> getInventoryOOB( String hmsIpAddr, Integer hmsPort,
                                                                             String path, String contentType )
                                                                                 throws HMSRestException
    {
        logger.debug( String.format( "Trying to get inventory OOB. Url for OOB operation is IpAddress: %s port: %s, Path: %s, Content Type: %s ",
                                     hmsIpAddr, hmsPort, path, contentType ) );
        ResponseEntity<HashMap<String, Object[]>> oobResponse = null;
        URI uri = null;
        if ( hmsIpAddr != null && hmsPort != null && path != null )
        {
            try
            {
                uri = new URI( "http", null, hmsIpAddr, hmsPort, path, null, null );
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                if ( contentType != null )
                {
                    headers.add( "Content-Type", contentType );
                }
                HttpEntity<Object> entity = new HttpEntity<Object>( headers );
                ParameterizedTypeReference<HashMap<String, Object[]>> typeRef =
                    new ParameterizedTypeReference<HashMap<String, Object[]>>()
                    {
                    };
                oobResponse = restTemplate.exchange( uri, HttpMethod.GET, entity, typeRef );
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
                                            "Exception while connecting to hms."
                                                + ( ( uri != null ) ? uri.toString() : "" ) );
            }
        }
        else
        {
            logger.error( String.format( "Unable to get inventory from OOB because some Final OOB Url cannot be constructed. "
                + "Url for OOB operation is IpAddress: %s port: %s, Path: %s, Content Type: %s ", hmsIpAddr, hmsPort,
                                         path, contentType ) );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while connecting to hms. Unable to create Url" );
        }
        return oobResponse;
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
    public static HashMap<String, List<HmsApi>> getOOBSupportedOperations( String hmsIpAddr, Integer hmsPort,
                                                                           String path, String contentType )
                                                                               throws HMSRestException
    {
        logger.debug( String.format( "Trying to get inventory OOB. Url for OOB operation is IpAddress: %s port: %s, Path: %s, Content Type: %s ",
                                     hmsIpAddr, hmsPort, path, contentType ) );
        ResponseEntity<HashMap<String, List<HmsApi>>> oobResponse = null;
        URI uri = null;
        if ( hmsIpAddr != null && hmsPort != null && path != null )
        {
            try
            {
                uri = new URI( "http", null, hmsIpAddr, hmsPort, path, null, null );
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                if ( contentType != null )
                {
                    headers.add( "Content-Type", contentType );
                }
                HttpEntity<Object> entity = new HttpEntity<Object>( headers );
                ParameterizedTypeReference<HashMap<String, List<HmsApi>>> typeRef =
                    new ParameterizedTypeReference<HashMap<String, List<HmsApi>>>()
                    {
                    };
                oobResponse = restTemplate.exchange( uri, HttpMethod.GET, entity, typeRef );
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
                                                + ( ( uri != null ) ? uri.toString() : "" ) );
            }
        }
        else
        {
            logger.error( String.format( "Unable to get OOB supported operations because some Final OOB Url cannot be constructed. "
                + "Url for OOB operation is IpAddress: %s port: %s, Path: %s, Content Type: %s ", hmsIpAddr, hmsPort,
                                         path, contentType ) );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while connecting to hms. Unable to create Url" );
        }
        return oobResponse.getBody();
    }
}
