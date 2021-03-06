/* ********************************************************************************
 * UpgradeRestService.java
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.ServiceManager;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.resource.UpgradeStatusCode;
import com.vmware.vrack.hms.common.rest.model.OobUpgradeSpec;
import com.vmware.vrack.hms.common.rest.model.RollbackSpec;
import com.vmware.vrack.hms.common.rest.model.UpgradeStatus;
import com.vmware.vrack.hms.common.service.ServiceState;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.FileUtil;
import com.vmware.vrack.hms.common.util.HmsGenericUtil;
import com.vmware.vrack.hms.common.util.HmsUpgradeUtil;
import com.vmware.vrack.hms.common.util.ProcessUtil;
import com.vmware.vrack.hms.utils.UpgradeUtil;

/**
 * <code>UpgradeRestService</code> <br>
 *
 * @author VMware, Inc.
 */
@Path( "/upgrade" )
@Produces( MediaType.APPLICATION_JSON )
@Consumes( MediaType.APPLICATION_JSON )
public class UpgradeRestService
{

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private static final String RESTART_PROXY_SCRIPT = "hms_oob_restart_lighttpd_wrapper.sh";

    /**
     * Upgrade.
     *
     * @param upgradeSpec the upgrade spec
     * @return the base response
     * @throws HMSRestException the HMS rest exception
     */
    @POST
    public Response upgrade( OobUpgradeSpec upgradeSpec )
        throws HMSRestException
    {

        String message = null;

        // validate upgrade request.
        Response validationResponse = UpgradeUtil.validateUpgradeRequest( upgradeSpec );
        if ( validationResponse != null )
        {
            // delete upgrade scripts and upgrade bundle files
            if ( upgradeSpec != null )
            {
                UpgradeUtil.deleteUpgradeFiles( upgradeSpec.getId() );
            }
            return validationResponse;
        }

        final String upgradeId = upgradeSpec.getId();
        final String upgradeDir = UpgradeUtil.getUpgradeDir( upgradeId );

        UpgradeStatus status = new UpgradeStatus();
        status.setId( upgradeId );

        boolean scriptsExecutable = FileUtil.setFilesExecutable( upgradeDir, "sh" );
        if ( !scriptsExecutable )
        {

            // delete upgrade scripts and upgrade bundle files
            UpgradeUtil.deleteUpgradeFiles( upgradeId );

            message = String.format( "Failed to grant execute rights to upgrade scripts at '%s'.", upgradeDir );
            logger.error( message );

            status.setStatusCode( UpgradeStatusCode.HMS_OOB_UPGRADE_INTERNAL_ERROR );
            status.setStatusMessage( UpgradeStatusCode.HMS_OOB_UPGRADE_INTERNAL_ERROR.getStatusMessage() );
            status.setMoreInfo( message );
            return Response.status( Status.INTERNAL_SERVER_ERROR ).entity( status ).build();
        }

        /*
         * 1. Put Service under maintenance 2. Drain/Post all events in queue 3. Shut off monitoring threads
         */
        boolean serviceInMaintenance = ServiceManager.putServiceInMaintenance();
        if ( serviceInMaintenance )
        {

            // Invoke upgrade-hms-oob script
            boolean upgradeInitiated = UpgradeUtil.initiateUpgrade( upgradeId, upgradeSpec.getFileName() );

            if ( upgradeInitiated )
            {

                if ( ( ServiceManager.getServiceState().equals( ServiceState.NORMAL_MAINTENANCE ) )
                    && ( ServiceManager.getActiveRequests() == 1 ) )
                {

                    message =
                        "Upgrade initiated after Out-of-band agent in " + ServiceState.NORMAL_MAINTENANCE.toString();

                }
                else if ( ( ServiceManager.getServiceState().equals( ServiceState.FORCE_MAINTENANCE ) )
                    && ( ServiceManager.getActiveRequests() > 1 ) )
                {

                    message =
                        String.format( "Upgrade initiated after Out-of-band agent " + "in %s with %s active requests.",
                                       ServiceState.FORCE_MAINTENANCE.toString(), ServiceManager.getActiveRequests() );
                }

                logger.info( message );

                // save upgradeStatus to json file.
                status.setStatusCode( UpgradeStatusCode.HMS_OOB_UPGRADE_INITIATED );

                final String hmsUpgradeDir = UpgradeUtil.getHmsUpgradeDir();
                String upgradeStatusFileAbsPath = String.format( "%1$s/%2$s.json", hmsUpgradeDir, upgradeId );
                boolean saved = HmsUpgradeUtil.saveUpgradeStatus( upgradeStatusFileAbsPath, status );
                if ( saved )
                {
                    logger.info( "Saved upgrade status to '{}'", upgradeStatusFileAbsPath );
                }
                else
                {
                    logger.warn( "Unable to save upgrade status to '{}'.", upgradeStatusFileAbsPath );
                }

                status.setStatusMessage( UpgradeStatusCode.HMS_OOB_UPGRADE_INITIATED.getStatusMessage() );
                status.setMoreInfo( message );
                return Response.status( Status.ACCEPTED ).entity( status ).build();

            }
            else
            {

                // delete upgrade scripts and upgrade bundle files
                UpgradeUtil.deleteUpgradeFiles( upgradeId );

                message = "Executing Out-of-band Agent upgrade script failed.";
                logger.error( message );

                /*
                 * put back service in running state and reset activeRequests and restart monitoring.
                 */
                ServiceManager.putServiceInRunning();

                status.setStatusCode( UpgradeStatusCode.HMS_OOB_UPGRADE_INTERNAL_ERROR );
                status.setStatusMessage( UpgradeStatusCode.HMS_OOB_UPGRADE_INTERNAL_ERROR.getStatusMessage() );
                status.setMoreInfo( message );
                return Response.status( Status.INTERNAL_SERVER_ERROR ).entity( status ).build();
            }
        }
        else
        {

            // delete upgrade scripts and upgrade bundle files
            UpgradeUtil.deleteUpgradeFiles( upgradeId );

            message = "Setting Out-of-band Agent Service in MAINTENANCE failed.";
            logger.error( message );
            status.setStatusCode( UpgradeStatusCode.HMS_OOB_UPGRADE_INTERNAL_ERROR );
            status.setStatusMessage( UpgradeStatusCode.HMS_OOB_UPGRADE_INTERNAL_ERROR.getStatusMessage() );
            status.setMoreInfo( message );
            return Response.status( Status.INTERNAL_SERVER_ERROR ).entity( status ).build();
        }
    }

    /**
     * Rollback.
     *
     * @param rollbackSpec the rollback spec
     * @return the response
     * @throws HMSRestException the HMS rest exception
     */
    @POST
    @Path( "/rollback" )
    public Response rollback( RollbackSpec rollbackSpec )
        throws HMSRestException
    {

        // validate rollback upgrade request
        UpgradeUtil.validateRollbackRequest( rollbackSpec );

        boolean rollbackInitiated = UpgradeUtil.rollbackUpgrade( rollbackSpec );

        if ( rollbackInitiated )
        {
            // respond as rollback request accepted
            BaseResponse response =
                new BaseResponse( Status.ACCEPTED.getStatusCode(), Status.ACCEPTED.getReasonPhrase(),
                                  "Rollback of upgrade initiated." );
            return Response.accepted().entity( response ).build();

        }
        else
        {

            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                        Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                        "Initiating rollback of upgrade failed." );
        }
    }

    /**
     * REST API for uploading a file to server.
     * <p>
     * The expected form data fields are: <br>
     * <i>fileName</i> Absolute path of the fileName.<br>
     * <i>fileContent</i> The file data <br>
     * to. *
     *
     * @param multipartFormDataInput the multi-part form data input
     * @return the response
     * @throws HMSRestException the HMS rest exception
     */
    @POST
    @Path( "/upload" )
    @Consumes( MediaType.MULTIPART_FORM_DATA )
    public Response uploadFile( MultipartFormDataInput multipartFormDataInput )
        throws HMSRestException
    {

        Map<String, List<InputPart>> formDataMap = multipartFormDataInput.getFormDataMap();

        String upgradeId = UpgradeUtil.getUpgradeId( formDataMap );
        if ( upgradeId == null )
        {

            logger.error( "Form data does not contain 'upgradeId' input." );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                        "Error while saving uploaded file. Form data does not contain 'upgradeId' input.",
                                        Status.INTERNAL_SERVER_ERROR.toString() );
        }
        String fileName = UpgradeUtil.getFileName( formDataMap );
        if ( fileName == null )
        {

            logger.error( "Form data does not contain 'fileName' input." );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                        "Error while saving uploaded file. Form data does not contain 'fileName' input.",
                                        Status.INTERNAL_SERVER_ERROR.toString() );
        }

        final String upgradeDir = UpgradeUtil.getUpgradeDir( upgradeId );
        final String destFileNameAbsPath = FilenameUtils.concat( upgradeDir, fileName );

        byte[] bytes = UpgradeUtil.getFileContent( formDataMap );
        if ( bytes == null )
        {

            logger.error( "Form data either does not contain 'fileContent' input or "
                + "there was an error converting it into bytes." );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                        "Form data either does not contain 'fileContent' input or "
                                            + "there was an error converting it into bytes.",
                                        Status.INTERNAL_SERVER_ERROR.toString() );
        }

        try
        {

            File file = new File( destFileNameAbsPath );
            String dirAbsPath = file.getParent();
            File dir = new File( dirAbsPath );
            if ( !dir.exists() )
            {
                dir.mkdirs();
            }

            if ( !file.exists() )
            {

                file.createNewFile();

            }
            else
            {

                file.delete();
                file.createNewFile();
            }

            FileOutputStream fop = new FileOutputStream( file );
            fop.write( bytes );
            fop.flush();
            fop.close();

            return Response.ok().build();

        }
        catch ( IOException e )
        {

            logger.error( "Error while saving uploaded file as '{}'.", fileName, e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                        "Error while saving uploaded file." + e.getMessage(),
                                        Status.INTERNAL_SERVER_ERROR.toString() );
        }
    }

    /**
     * Gets the upgrade status.
     *
     * @param upgradeId the upgrade id
     * @return the upgrade status
     * @throws HMSRestException the HMS rest exception
     */
    @GET
    @Path( "/monitor/{upgradeId}" )
    public Response getUpgradeStatus( @PathParam( "upgradeId" ) String upgradeId )
        throws HMSRestException
    {

        UpgradeStatus upgradeStatus = new UpgradeStatus();
        upgradeStatus.setId( upgradeId );
        String statusMessage = null;

        String hmsParentDir = HmsConfigHolder.getHMSConfigProperty( "hms.parent.dir" );
        String upgradeStatusFileName = upgradeId + ".json";

        logger.info( "Looking for upgrade status file '{}' at '{}' directory and its sub-directories.",
                     upgradeStatusFileName, hmsParentDir );
        File[] upgradeSatusFiles = FileUtil.findFiles( hmsParentDir, upgradeStatusFileName, true );

        if ( ( upgradeSatusFiles == null ) || ( upgradeSatusFiles != null && upgradeSatusFiles.length != 1 ) )
        {

            statusMessage = String.format(
                                           "No upgrade status file '%1s' found for the given upgrade id '%2s' at '%3s' "
                                               + "directory and its sub-directories.",
                                           upgradeStatusFileName, upgradeId, hmsParentDir );
            logger.info( statusMessage );
            upgradeStatus.setStatusMessage( statusMessage );
            return Response.status( Status.BAD_REQUEST ).entity( upgradeStatus ).build();

        }
        else
        {

            File upgradeSatusFile = upgradeSatusFiles[0];
            String upgradeStatusFileNameAbsPath = upgradeSatusFile.getAbsolutePath();
            logger.info( "Found upgrade status file - '{}'. ", upgradeStatusFileNameAbsPath );

            UpgradeStatus status = HmsUpgradeUtil.loadUpgradeStatus( upgradeSatusFile );
            if ( status == null )
            {

                statusMessage =
                    String.format( "Unable to load UpgradeStatus from the file - '{}'", upgradeStatusFileNameAbsPath );
                logger.error( statusMessage );
                upgradeStatus.setStatusMessage( statusMessage );
                return Response.status( Status.INTERNAL_SERVER_ERROR ).entity( upgradeStatus ).build();

            }
            else
            {

                // additional check of upgrade id
                if ( status.getId().equals( upgradeId ) )
                {

                    status.setStatusMessage( status.getStatusCode().getStatusMessage() );
                    return Response.ok().entity( status ).build();

                }
                else
                {

                    return Response.status( Status.INTERNAL_SERVER_ERROR ).entity( upgradeStatus ).build();
                }
            }
        }
    }

    /**
     * Delete backup.
     *
     * @param upgradeId the upgrade id
     * @return the response
     */
    @DELETE
    @Path( "/backup/{upgradeId}" )
    public Response deleteBackup( @PathParam( "upgradeId" ) String upgradeId )
    {

        if ( StringUtils.isBlank( upgradeId ) )
        {
            return Response.status( Status.BAD_REQUEST ).build();
        }
        UpgradeUtil.deleteUpgradeFiles( upgradeId );

        String hmsParentDir = HmsConfigHolder.getHMSConfigProperty( "hms.parent.dir" );
        String hmsBackupDir = String.format( "%1$s/hms_backup_%2$s", hmsParentDir, upgradeId );

        File backupDir = new File( hmsBackupDir );
        if ( backupDir.exists() && backupDir.isDirectory() )
        {

            if ( FileUtil.deleteDirectory( backupDir ) )
            {

                logger.info( "Deleted HMS Backup - '{}'. ", hmsBackupDir );
                return Response.status( Status.OK ).build();

            }
            else
            {

                logger.info( "Faield to delete HMS Backup - '{}'. ", hmsBackupDir );
                return Response.status( Status.INTERNAL_SERVER_ERROR ).build();
            }
        }
        else
        {

            logger.info( "HMS Backup Directory - '{}' either does not exist or not a directory "
                + "for the upgradeId - '{}'.", hmsBackupDir, upgradeId );
            return Response.status( Status.BAD_REQUEST ).build();
        }
    }

    /**
     * This is the API specifically only for Upgrades. Responsible for restart of Lighttpd by invoking the script
     * hms_oob_restart_lighttpd_wrapper.sh. This script will be coped to Mgmt switch as part of upgrade file upload from
     * Hms Aggregator. UpgradeId is the upgrade folder created at runtime while upgrade is initiated.
     *
     * @param upgradeId
     * @return
     * @throws HMSRestException
     */
    @POST
    @Path( "/proxy/restart/{upgradeId}" )
    public Response restartProxy( @PathParam( "upgradeId" ) String upgradeId )
        throws HMSRestException
    {

        if ( StringUtils.isNotBlank( upgradeId ) )
        {
            String upgradeBaseDir = HmsConfigHolder.getHMSConfigProperty( Constants.HMS_UPGRADE_DIR );
            String scriptLocation =
                upgradeBaseDir + Constants.FILE_SEPERATOR + upgradeId + Constants.FILE_SEPERATOR + RESTART_PROXY_SCRIPT;
            logger.debug( "starts executing the script: {}", scriptLocation );

            List<String> cmdWithArgs = new ArrayList<String>();
            cmdWithArgs.add( 0, scriptLocation );

            int exitCode = ProcessUtil.executeCommand( cmdWithArgs );
            if ( exitCode != 0 )
            {
                logger.error( "unsuccessful on executing the script, exit value {}", exitCode );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                            "unsuccessful on executing the script", "exit code: " + exitCode );
            }
            logger.debug( "successfully completed executing the script: {}", scriptLocation );

        }
        else
        {
            logger.error( "upgrade token can't be blank" );
            throw new HMSRestException( Status.BAD_REQUEST.getStatusCode(), "upgrade token is blank",
                                        "Invalid input provided" );
        }

        BaseResponse baseResponse = HmsGenericUtil.getBaseResponse( Status.ACCEPTED, "restart proxy initiated." );
        return Response.accepted().entity( baseResponse ).build();
    }

    // TODO-remove this later RTP3
    /**
     * Downloads the inventory file. Basically during upgrade Hms-oob will be backed-up initially. This method will
     * download the inventory file from the backed-up location
     *
     * @return
     * @throws HMSRestException
     */
    @GET
    @Path( "/download/inventory/{upgradeId}" )
    public File downloadInventoryFile( @PathParam( "upgradeId" ) String upgradeId )
        throws HMSRestException
    {
        logger.debug( "download inventory starts" );

        String hmsParentDir = HmsConfigHolder.getHMSConfigProperty( "hms.parent.dir" );
        String hmsBackupDir = String.format( "%1$s/hms_backup_%2$s", hmsParentDir, upgradeId );

        final String fileAbsLocation =
            FilenameUtils.concat( hmsBackupDir,
                                  HmsConfigHolder.getHMSConfigProperty( "hms.inventory.configuration.file" ) );

        File inventoryFile = new File( fileAbsLocation );
        if ( !inventoryFile.exists() )
        {
            String msg = String.format( "The inventory file doesn't exists at the location: %s", fileAbsLocation );
            logger.error( msg );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), msg, "inventory file not found" );
        }
        logger.debug( "returning the downloadable file" );
        return inventoryFile;
    }
}