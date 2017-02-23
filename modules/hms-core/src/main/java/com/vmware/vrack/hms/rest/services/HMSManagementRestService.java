/* ********************************************************************************
 * HMSManagementRestService.java
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
package com.vmware.vrack.hms.rest.services;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.configuration.HmsInventoryConfiguration;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.notification.NodeDiscoveryResponse;
import com.vmware.vrack.hms.common.rest.model.DhcpLease;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.FileUtil;
import com.vmware.vrack.hms.common.util.HmsGenericUtil;
import com.vmware.vrack.hms.common.util.ThreadStackLogger;
import com.vmware.vrack.hms.common.util.ZipUtil;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;
import com.vmware.vrack.hms.node.switches.SwitchNodeConnector;
import com.vmware.vrack.hms.utils.DhcpLeaseUtil;
import com.vmware.vrack.hms.utils.NodeDiscoveryUtil;
import com.vmware.vrack.hms.utils.OobUtil;

@Path( "/" )
public class HMSManagementRestService
{

    private static final String QUOTE = "\"";

    public static final String GRANT_EXECUTE_RIGHTS = "chmod +x %s";

    public final String TIMESTAMP_FORMAT = "yyyy-MM-dd_hh-mm-ss";

    private static Logger logger = LoggerFactory.getLogger( HMSManagementRestService.class );

    private ServerNodeConnector serverConnector = ServerNodeConnector.getInstance();

    private SwitchNodeConnector switchConnector = SwitchNodeConnector.getInstance();

    @GET
    @Path( "/nodes" )
    @Produces( "application/json" )
    public Map<String, Object[]> getHMSNodes()
        throws HMSRestException
    {

        Map<String, Object[]> nodes = new HashMap<String, Object[]>();
        try
        {

            Object[] hosts = serverConnector.getNodeMap().values().toArray();
            Object[] switches = switchConnector.switchNodeMap.values().toArray();

            List<ServerNode> copyHosts = new ArrayList<ServerNode>();
            List<SwitchNode> copySwitches = new ArrayList<SwitchNode>();

            if ( hosts != null && hosts.length > 0 )
            {
                for ( Object obj : hosts )
                {
                    ServerNode serverNode = (ServerNode) obj;
                    ServerNode copy = (ServerNode) HmsGenericUtil.maskPassword( serverNode );
                    copyHosts.add( copy );
                }
            }

            if ( switches != null && switches.length > 0 )
            {
                for ( Object obj : switches )
                {
                    SwitchNode switchNode = (SwitchNode) obj;
                    SwitchNode copy = (SwitchNode) HmsGenericUtil.maskPassword( switchNode );
                    copySwitches.add( copy );
                }
            }

            nodes.put( Constants.HOSTS, copyHosts.toArray() );
            nodes.put( Constants.SWITCHES, copySwitches.toArray() );

        }
        catch ( Exception e )
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                        "Error while fetching HMS nodes" );
        }
        return nodes;
    }

    /**
     * Returns node discovery status for nodes present in inventory. discoveryStatus will be "RUNNING", if any of the
     * node's discovery is in "RUNNING" status. If none of the nodes are in "RUNNING" status, it means discovery for all
     * nodes has been completed, and discoveryStatus in the response will be "SUCCESS".
     *
     * @return
     * @throws HMSRestException
     */
    @GET
    @Path( "/discover" )
    @Produces( "application/json" )
    public NodeDiscoveryResponse discoverNodes()
        throws HMSRestException
    {

        return NodeDiscoveryUtil.getNodeDiscoveryStatus();
    }

    @POST
    @Path( "/handshake" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( "application/json" )
    public BaseResponse handshake()
        throws HMSRestException
    {

        BaseResponse response = new BaseResponse();
        response.setStatusCode( Status.ACCEPTED.getStatusCode() );
        response.setStatusMessage( "Handshake request accepted." );

        return response;
    }

    @POST
    @Path( "/handshake/{aggregator_ip}/{source}" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( "application/json" )
    public BaseResponse handshake( @PathParam( "aggregator_ip" ) String aggregatorIp,
                                   @PathParam( "source" ) String source )
        throws HMSRestException
    {

        if ( StringUtils.isNotBlank( aggregatorIp ) )
        {
            logger.debug( "received aggregator Ip: " + aggregatorIp + ":: source: " + source );
            try
            {
                aggregatorIp = aggregatorIp.trim();

                boolean handShakeStatus = isHandshakeCompleted();

                // Updates Aggregator Future ip only one time, if the primary
                // and secondary ip's are different then its considered that
                // handshake is already completed, by default both will be 192.168.100.40
                if ( !handShakeStatus )
                {
                    // update proxy config file with the new ip
                    updateProxyConfiguration( aggregatorIp );
                }
                else
                {
                    logger.info( "Have not updated the aggregatorIp as handhake already completed: {}", aggregatorIp );
                }
            }
            catch ( HmsException e )
            {
                logger.error( "Exception occured during the handshake: {}", e );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage(), e );
            }
        }
        else
        {
            logger.error( "aggregator ip can't be blank" );
            throw new HMSRestException( Status.BAD_REQUEST.getStatusCode(), "Aggregator Ip is blank",
                                        "Invalid input provided" );
        }
        BaseResponse response = new BaseResponse();
        response.setStatusCode( Status.ACCEPTED.getStatusCode() );
        response.setStatusMessage( "Handshake request accepted." );

        return response;
    }

    /**
     * Method to identify if the handshake is already completed
     *
     * @return
     */
    private boolean isHandshakeCompleted()
    {
        String proxyConfigFile = HmsConfigHolder.getHMSConfigProperty( HmsConfigHolder.HMS_PROXY_CONFIG_FILE );

        String primaryIp = OobUtil.getProperty( proxyConfigFile, HmsConfigHolder.HMS_PROXY_PROPS,
                                                HmsConfigHolder.HMS_OOB_ALLOWED_PRIMARY_IP );
        String secondaryIp = OobUtil.getProperty( proxyConfigFile, HmsConfigHolder.HMS_PROXY_PROPS,
                                                  HmsConfigHolder.HMS_OOB_ALLOWED_SECONDARY_IP );
        logger.debug( "Primary Ip: {}, Secondary Ip: {}", primaryIp, secondaryIp );

        if ( StringUtils.isNotBlank( primaryIp ) && StringUtils.isNotBlank( secondaryIp )
            && !primaryIp.trim().equals( secondaryIp.trim() ) )
        {
            return true;
        }
        return false;
    }

    /**
     * Updates proxy configuration details
     *
     * @param aggregatorIp
     * @throws HmsException
     */
    private void updateProxyConfiguration( String aggregatorIp )
        throws HmsException
    {
        String proxyConfigFile = HmsConfigHolder.getHMSConfigProperty( HmsConfigHolder.HMS_PROXY_CONFIG_FILE );
        String defaultAggIp = HmsConfigHolder.getHMSConfigProperty( HmsConfigHolder.HMS_AGGREGATOR_DEFAULT_IP );

        java.nio.file.Path path = Paths.get( proxyConfigFile );
        if ( Files.exists( path ) )
        {
            // Updates only if the new ip is not the default ip: 192.168.100.40
            logger.debug( "defaultAggIp: {}, aggregatorIp: {}", defaultAggIp, aggregatorIp );
            if ( !defaultAggIp.equals( aggregatorIp ) )
            {
                aggregatorIp = QUOTE + aggregatorIp + QUOTE;
                HmsConfigHolder.setProperty( proxyConfigFile, HmsConfigHolder.HMS_PROXY_PROPS,
                                             HmsConfigHolder.HMS_OOB_ALLOWED_PRIMARY_IP, aggregatorIp );
                HmsConfigHolder.setProperty( proxyConfigFile, HmsConfigHolder.HMS_PROXY_PROPS,
                                             HmsConfigHolder.HMS_OOB_ALLOWED_SECONDARY_IP, aggregatorIp );
                logger.debug( "Updated the hanshaked ip details, aggregatorIp: {}", aggregatorIp );
            }
        }
    }

    /**
     * OOB End-Point to return map of node id and the HMS API supported by corresponding OOB Board Plugin
     *
     * @return
     * @throws HMSRestException
     */
    @GET
    @Path( "/service/operations" )
    @Produces( "application/json" )
    public Map<String, List<HmsApi>> getSupportedOOBOperations()
        throws HMSRestException
    {

        return BoardServiceProvider.getOperationSupported();
    }

    /**
     * Return debug information on all HMS threads including stack trace and other useful information.
     */
    @GET
    @Path( "/debug/threads" )
    @Produces( "application/json" )
    public Map<String, String> executeThreadStackLogger()
        throws HMSRestException
    {

        ThreadStackLogger threadStackLogger = new ThreadStackLogger( logger );
        Map<String, String> map = new HashMap<String, String>();

        map.put( "timestamp", Long.toString( System.currentTimeMillis() ) );
        map.put( "threads", threadStackLogger.log() );
        return map;
    }

    /**
     * Forces HMS to reload the inventory. If any new servers are found, then Board Service will be loaded for them.
     * This method is deprecated, please use InventoryRestService.reload()
     *
     * @param inventory
     * @return
     * @throws HmsException
     */
    @PUT
    @Path( "/refreshinventory" )
    @Produces( MediaType.APPLICATION_JSON )
    @Deprecated
    public Response refreshInventory( final String inventory )
        throws HmsException
    {
        BaseResponse baseResponse = new BaseResponse();
        String message = null;

        // Check that inventory is not null or blank.
        if ( StringUtils.isBlank( inventory ) )
        {
            message = "Invalid inventory. Inventory is either null or blank.";
            baseResponse = HmsGenericUtil.getBaseResponse( Status.BAD_REQUEST, message );
            return Response.status( Status.BAD_REQUEST ).entity( baseResponse ).build();
        }

        // Parse Inventory as HmsInventoryConfiguration
        HmsInventoryConfiguration hic = HmsInventoryConfiguration.getHmsInventoryConfiguration( inventory );
        if ( hic == null )
        {
            message = "Invalid inventory. Parsing inventory as HmsInventoryConfiguration failed.";
            baseResponse = HmsGenericUtil.getBaseResponse( Status.BAD_REQUEST, message );
            return Response.status( Status.BAD_REQUEST ).entity( baseResponse ).build();
        }

        // If inventory file exists, update it.
        if ( HmsConfigHolder.isHmsInventoryFileExists() )
        {
            String invConfFile = HmsConfigHolder.getInventoryConfigFileName();
            boolean inventorySaved = HmsInventoryConfiguration.store( invConfFile, true, hic );
            if ( inventorySaved )
            {
                logger.debug( "In refreshInventory, inventory saved to '{}'.", invConfFile );
            }
            else
            {
                message = String.format( "Failed to save inventory to '%s'.", invConfFile );
                logger.error( "In refreshInventory, {}", message );
                baseResponse = HmsGenericUtil.getBaseResponse( Status.INTERNAL_SERVER_ERROR, message );
                return Response.status( Status.INTERNAL_SERVER_ERROR ).entity( baseResponse ).build();
            }
        }

        try
        {

            // Reloading Server Nodes from Inventory
            ServerNodeConnector.getInstance().parseRackInventoryConfig( hic, false );

        }
        catch ( Exception e )
        {
            message = "Error reloading HMS Inventory while reloading Server Nodes";
            logger.error( message, e );
            baseResponse = HmsGenericUtil.getBaseResponse( Status.INTERNAL_SERVER_ERROR, message );
            return Response.status( Status.INTERNAL_SERVER_ERROR ).entity( baseResponse ).build();
        }

        try
        {
            // Reloading Switch Nodes from Inventory
            SwitchNodeConnector.getInstance().reloadRackInventoryConfig( hic );

        }
        catch ( Exception e )
        {
            message = "Error reloading HMS Inventory while reloading Switch Nodes";
            logger.error( message, e );
            baseResponse = HmsGenericUtil.getBaseResponse( Status.INTERNAL_SERVER_ERROR, message );
            return Response.status( Status.INTERNAL_SERVER_ERROR ).entity( baseResponse ).build();
        }
        message = "Inventory refresh successful.";
        logger.info( "In refreshInventory, {}.", message );
        baseResponse = HmsGenericUtil.getBaseResponse( Status.OK, message );
        return Response.status( Status.OK ).entity( baseResponse ).build();
    }

    /**
     * Zip and return the hms OOB log files.
     * 
     * @param fileName
     * @return
     */
    @GET
    @Path( "/hmslogs" )
    @Produces( MediaType.APPLICATION_OCTET_STREAM )
    public Response getHmsLogs()
        throws HMSRestException
    {
        // String tempDir = FileUtil.getTemporaryFolderPath();
        String tempDir = HmsConfigHolder.getHMSConfigProperty( HmsConfigHolder.HMS_TEMPORARY_LOG_DIR );
        File tempDirHandle = new File( tempDir );

        if ( !tempDirHandle.exists() && !tempDirHandle.isDirectory() )
        {
            if ( !FileUtil.createDirectory( tempDir ) )
            {
                logger.error( "Unable to create directory for creating temporary OOB logs" );
                ResponseBuilder response = Response.status( Status.INTERNAL_SERVER_ERROR );
                return response.build();
            }
        }

        String tempZipFileName = System.currentTimeMillis() + "-hmsLog.zip";

        // Create a random zip file name
        String outputZipFile = tempDir + tempZipFileName;

        String hmslogPath = HmsConfigHolder.getHMSConfigProperty( HmsConfigHolder.HMS_LOG_FILE_PATH );
        String hmslog1Path = HmsConfigHolder.getHMSConfigProperty( HmsConfigHolder.HMS_LOG_1_FILE_PATH );

        // Put some validations here such as invalid file name or missing file name
        if ( hmslogPath == null || hmslogPath.isEmpty() )
        {
            ResponseBuilder response = Response.status( Status.NOT_FOUND );
            return response.build();
        }

        logger.debug( "Hms Log file path:" + hmslogPath );
        logger.debug( "Hms Log 1 file path:" + hmslog1Path );

        boolean zipForLogCreated = ZipUtil.zipFiles( outputZipFile, hmslogPath, hmslog1Path );

        if ( zipForLogCreated )
        {
            // Prepare a file object with file to return
            File hmsLog = new File( outputZipFile );

            ResponseBuilder response = Response.ok( hmsLog );
            response.header( "Content-Disposition", "attachment; filename=\"" + "hmslog.zip" + "\"" );

            return response.build();
        }
        else
        {
            ResponseBuilder response = Response.status( Status.INTERNAL_SERVER_ERROR );
            return response.build();
        }
    }

    /**
     * Deletes the Temp Log file
     *
     * @param fileName
     * @return
     */
    @DELETE
    @Path( "/hmslogs" )
    @Produces( "application/json" )
    public BaseResponse deleteTemporaryHmsLogFile()
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();

        String tempDir = HmsConfigHolder.getHMSConfigProperty( HmsConfigHolder.HMS_TEMPORARY_LOG_DIR );

        if ( tempDir != null )
        {
            File logArchiveName = new File( tempDir );
            if ( logArchiveName.exists() && logArchiveName.isDirectory() && logArchiveName.canWrite() )
            {
                for ( File archive : logArchiveName.listFiles() )
                {
                    try
                    {
                        if ( isFileDeleteable( archive ) )
                        {
                            archive.delete();
                        }
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Unable to delete log archive [ " + archive.getName() + " ] from filesystem.",
                                      e );
                    }

                }
                response.setStatusCode( Status.OK.getStatusCode() );
                response.setStatusMessage( "HMS temporary Log File deleted" );
                return response;
            }
            else
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            "Error deleting Old OOB temporary log files" );
            }
        }
        else
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                        "Error deleting Old OOB temporary log files" );
        }
    }

    /**
     * Checks if the particular hms debug log file was created within 10 mins with current timestamp. If yes, then it
     * can be deleted.
     *
     * @param logArchiveName
     * @return
     * @throws HmsException
     */
    public boolean isFileDeleteable( File logArchiveName )
        throws HmsException
    {
        if ( logArchiveName != null )
        {
            try
            {
                Date parsedDate = new Date( logArchiveName.lastModified() );
                Date currentDate = new Date();
                long duration = currentDate.getTime() - parsedDate.getTime();
                long logClearThreshold = 60000;
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes( duration );

                String logClearDurationInMilliSeconds =
                    HmsConfigHolder.getHMSConfigProperty( HmsConfigHolder.HMS_TEMPORARY_LOG_FILE_CLEAR_DURATION );

                try
                {
                    logClearThreshold = Long.parseLong( logClearDurationInMilliSeconds );

                }
                catch ( Exception e )
                {
                    logger.debug( "Exception occured while getting log clear threshold" + e );
                }

                if ( ( diffInMinutes - TimeUnit.MILLISECONDS.toMinutes( logClearThreshold ) ) > 0 )
                {
                    return true;
                }

            }
            catch ( Exception e )
            {
                String err = "Exception occured when trying to get file duration since it was created for  file [ "
                    + logArchiveName + " ]";
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return false;
        }
        else
        {
            String err =
                "Cannot get Date from log Archive name [ " + logArchiveName + " ], because either it is null or empty.";
            logger.error( err );
            throw new HmsException( err );
        }
    }

    @GET
    @Path( "/newhosts" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getNewHosts()
    {
        BaseResponse baseResponse = null;
        String message = null;
        List<DhcpLease> activeDhcpLeases = DhcpLeaseUtil.getActiveDhcpLeases();
        if ( CollectionUtils.isEmpty( activeDhcpLeases ) )
        {
            message = "Failed to get active DHCP leases.";
            logger.error( "In getNewHosts, {}", message );
            baseResponse = HmsGenericUtil.getBaseResponse( Status.INTERNAL_SERVER_ERROR, message );
            return Response.status( Status.INTERNAL_SERVER_ERROR ).entity( baseResponse ).build();
        }

        // Get the Servers in the HMS Inventory
        Collection<HmsNode> serversList = serverConnector.getServers();
        if ( CollectionUtils.isEmpty( serversList ) )
        {
            message = "HMS Inventory Servers List is either null or empty.";
            logger.warn( "In getNewHosts, {} Inventory is not updated from Aggregator yet.", message );
            baseResponse = HmsGenericUtil.getBaseResponse( Status.INTERNAL_SERVER_ERROR, message );
            return Response.status( Status.INTERNAL_SERVER_ERROR ).entity( baseResponse ).build();
        }

        // Get the Management IPs of the Servers already in Inventory
        Set<String> managementIpsInInventory = new HashSet<String>();
        for ( Iterator<HmsNode> serverIterator = serversList.iterator(); serverIterator.hasNext(); )
        {
            HmsNode hmsNode = (HmsNode) serverIterator.next();
            ServerNode serverNode = (ServerNode) hmsNode;
            managementIpsInInventory.add( serverNode.getManagementIp() );
        }

        /*
         * <pre> Iterate through the DHCP Active Leases and 1. Remove a lease if the Lease IP is already in HMS
         * Inventory. 2. Remove a lease if the Lease IP is not reachable even though it is NOT in inventory. After the
         * above two actions, activeDhcpLeases list will contain list of DHCP leases, whose IP is NOT in HMS Inventory
         * and the IPs are reachable. </pre>
         */
        for ( Iterator<DhcpLease> dhcpLeasesIterator = activeDhcpLeases.iterator(); dhcpLeasesIterator.hasNext(); )
        {
            DhcpLease dhcpLease = (DhcpLease) dhcpLeasesIterator.next();
            String dhcpIpAddress = dhcpLease.getIpAddress();
            // Check if DHCP Lease IP is in HMS Inventory
            if ( managementIpsInInventory.contains( dhcpIpAddress ) )
            {
                logger.debug( "In getNewHosts, BMC IP '{}' is in HMS Inventory. Removed it from the New hosts list.",
                              dhcpIpAddress );
                /*
                 * Remove this DHCP Lease from the list of active DHCP Leases list, as this lease's IP is in HMS
                 * Inventory.
                 */
                dhcpLeasesIterator.remove();
            }
            else
            {
                /*
                 * DHCP Lease IP is NOT in HMS Inventory. Check If the IP is reachable. If not reachable, then remove it
                 * from activeDhcpLeases list. Typically, this condition can arise, if server is decommissioned and DHCP
                 * server is yet to mark the lease as free.
                 */
                if ( !HmsGenericUtil.isHostReachable( dhcpIpAddress ) )
                {
                    logger.debug( "In getNewHosts, BMC IP '{}' is NOT in HMS Inventory and is NOT reachable. "
                        + "Removed it from the New hosts list.", dhcpIpAddress );
                    dhcpLeasesIterator.remove();
                }
                else
                {
                    logger.debug( "In getNewHosts, BMC IP '{}' is NOT in HMS Inventory and is reachable.",
                                  dhcpIpAddress );
                }
            }
        }
        return Response.status( Status.OK ).entity( activeDhcpLeases ).build();
    }
}