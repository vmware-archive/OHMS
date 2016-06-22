/* ********************************************************************************
 * UpgradeUtil.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.resource.UpgradeStatusCode;
import com.vmware.vrack.hms.common.rest.model.OobUpgradeSpec;
import com.vmware.vrack.hms.common.rest.model.RollbackSpec;
import com.vmware.vrack.hms.common.rest.model.UpgradeStatus;
import com.vmware.vrack.hms.common.upgrade.api.ChecksumMethod;
import com.vmware.vrack.hms.common.util.FileUtil;
import com.vmware.vrack.hms.common.util.ProcessUtil;

/**
 * <code>UpgradeUtil</code> is a utility class for initiating an upgrade and rollback of an upgrade.<br>
 *
 * @author VMware, Inc.
 */
public class UpgradeUtil
{
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger( UpgradeUtil.class );

    /** The Constant HMS_PARENT_DIR. */
    private static final String HMS_PARENT_DIR = "hms.parent.dir";

    /** The Constant HMS_DIR. */
    private static final String HMS_DIR = "hms.dir";

    /** The Constant UPGRADE_SCRIPT. */
    private static final String UPGRADE_SCRIPT = "hms.upgrade.script";

    /** The Constant ROLLABACK_SCRIPT. */
    private static final String ROLLABACK_SCRIPT = "hms.rollback.script";

    /**
     * private constructor for not allowing to create an instance of this class.
     */
    private UpgradeUtil()
    {
        throw new AssertionError();
    }

    /**
     * Initiate upgrade.
     *
     * @param upgradeSpec the upgrade spec
     * @return true, if successful
     */
    public static boolean initiateUpgrade( final OobUpgradeSpec upgradeSpec )
    {
        String upgradeScript = HmsConfigHolder.getHMSConfigProperty( UPGRADE_SCRIPT );
        if ( upgradeScript != null && upgradeSpec != null )
        {
            try
            {
                List<String> cmdWithArgs = new ArrayList<String>();
                // upgrade script abs path
                String upgradeScriptAbsPath = upgradeSpec.getScriptsLocation() + File.separator + upgradeScript;
                cmdWithArgs.add( 0, upgradeScriptAbsPath );
                // HMS_TOKEN
                cmdWithArgs.add( 1, upgradeSpec.getId() );
                // HMS_SCRIPT_DIR
                cmdWithArgs.add( 2, upgradeSpec.getScriptsLocation() );
                // HMS_OOB_BINARY_LOCATION
                cmdWithArgs.add( 3, upgradeSpec.getLocation() );
                // HMS_OOB_BINARY_FILENAME
                cmdWithArgs.add( 4, upgradeSpec.getFileName() );
                // HMS_OOB_BINARY_CHECKSUM_FILENAME
                cmdWithArgs.add( 5, upgradeSpec.getFileName() + ".md5" );
                int exitValue = ProcessUtil.getCommandExitValue( cmdWithArgs );
                if ( exitValue == 0 )
                {
                    return true;
                }
            }
            catch ( Exception e )
            {
                logger.debug( "Error while initiating upgrade using upgrade script {} at {}.", upgradeScript,
                              upgradeSpec.getScriptsLocation(), e );
            }
        }
        return false;
    }

    /**
     * Rollback upgrade.
     *
     * @param rollbackSpec the rollback spec
     * @return true, if successful
     */
    public static boolean rollbackUpgrade( RollbackSpec rollbackSpec )
    {
        String rollbackScript = HmsConfigHolder.getHMSConfigProperty( ROLLABACK_SCRIPT );
        String hmsParentDir = HmsConfigHolder.getHMSConfigProperty( HMS_PARENT_DIR );
        String hmsDir = HmsConfigHolder.getHMSConfigProperty( HMS_DIR );
        if ( rollbackScript != null && hmsDir != null && rollbackSpec != null )
        {
            List<String> cmdWithArgs = new ArrayList<String>();
            String rollbackScriptAbsPath = rollbackSpec.getScriptsLocation() + File.separator + rollbackScript;
            cmdWithArgs.add( 0, rollbackScriptAbsPath );
            // HMS_TOKEN
            cmdWithArgs.add( 1, rollbackSpec.getId() );
            // HMS_SCRIPT_DIR
            cmdWithArgs.add( 2, rollbackSpec.getScriptsLocation() );
            // HMS_OOB_BACKUP_DIR_FULLPATH
            String backupDirAbsPath = hmsParentDir + File.separator + "hms_backup_" + rollbackSpec.getId();
            cmdWithArgs.add( 3, backupDirAbsPath );
            // HMS_DIR_FULLPATH
            cmdWithArgs.add( 4, hmsDir );
            int exitValue = ProcessUtil.getCommandExitValue( cmdWithArgs );
            if ( exitValue == 0 )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate upgrade spec.
     *
     * @param upgradeSpec the upgrade spec
     * @return the response
     * @throws HMSRestException the HMS rest exception
     */
    public static Response validateUpgradeRequest( OobUpgradeSpec upgradeSpec )
        throws HMSRestException
    {
        String message = null;
        if ( upgradeSpec != null )
        {
            logger.info( "Validating upgrade request: {}", upgradeSpec.toString() );
            if ( StringUtils.isBlank( upgradeSpec.getChecksum() ) )
            {
                message = "'checksum' is a mandatory parameter for HMS Upgrade.";
                logger.error( message );
                return UpgradeUtil.getUpgradeStatusResponse( upgradeSpec, Status.BAD_REQUEST, message );
            }
            else if ( StringUtils.isBlank( upgradeSpec.getFileName() ) )
            {
                message = "'fileName' is a mandatory parameter for HMS Upgrade.";
                logger.error( message );
                return UpgradeUtil.getUpgradeStatusResponse( upgradeSpec, Status.BAD_REQUEST, message );
            }
            else if ( StringUtils.isBlank( upgradeSpec.getId() ) )
            {
                message = "'id' is a mandatory parameter for HMS Upgrade.";
                logger.error( message );
                return UpgradeUtil.getUpgradeStatusResponse( upgradeSpec, Status.BAD_REQUEST, message );
            }
            else if ( StringUtils.isBlank( upgradeSpec.getLocation() ) )
            {
                message = "'location' is a mandatory parameter for HMS Upgrade.";
                logger.error( message );
                return UpgradeUtil.getUpgradeStatusResponse( upgradeSpec, Status.BAD_REQUEST, message );
            }
            else if ( StringUtils.isBlank( upgradeSpec.getScriptsLocation() ) )
            {
                message = "'scriptsLocation' is a mandatory parameter for HMS Upgrade.";
                logger.error( message );
                return UpgradeUtil.getUpgradeStatusResponse( upgradeSpec, Status.BAD_REQUEST, message );
            }
        }
        else
        {
            message = "HMS upgrade request should have the mandatory parameters - "
                + "[scriptsLocation, id, location, fileName, checksum] ";
            logger.error( message );
            return UpgradeUtil.getUpgradeStatusResponse( upgradeSpec, Status.BAD_REQUEST, message );
        }
        // check that upgrade binary file exists
        String fileName = FilenameUtils.concat( upgradeSpec.getLocation(), upgradeSpec.getFileName() );
        if ( !FileUtil.isFileExists( fileName ) )
        {
            message = String.format( "Upgrade binary '%s' not found at '%s'.", upgradeSpec.getFileName(),
                                     upgradeSpec.getLocation() );
            logger.error( message );
            return UpgradeUtil.getUpgradeStatusResponse( upgradeSpec, Status.FORBIDDEN, message );
        }
        // validate upgrade binary checksum
        String upgradeBinaryChecksum = FileUtil.getFileChecksum( fileName, ChecksumMethod.SHA1 );
        if ( upgradeBinaryChecksum == null || !upgradeBinaryChecksum.equals( upgradeSpec.getChecksum() ) )
        {
            message = String.format(
                                     "Upgrade binary checksum validation failed. "
                                         + "[Binary Checksum - %s, Upgrade Spec Checksum - %s ]",
                                     upgradeBinaryChecksum, upgradeSpec.getChecksum() );
            logger.error( message );
            return UpgradeUtil.getUpgradeStatusResponse( upgradeSpec, Status.FORBIDDEN, message );
        }
        // check that upgrade script file exists
        String upgradeScript = FilenameUtils.concat( upgradeSpec.getScriptsLocation(),
                                                     HmsConfigHolder.getHMSConfigProperty( UPGRADE_SCRIPT ) );
        if ( !FileUtil.isFileExists( upgradeScript ) )
        {
            message =
                String.format( "Upgrade script '%s' not found at '%s'.", upgradeScript, upgradeSpec.getLocation() );
            logger.error( message );
            return UpgradeUtil.getUpgradeStatusResponse( upgradeSpec, Status.FORBIDDEN, message );
        }
        logger.info( "Valid upgrade request: {}", upgradeSpec.toString() );
        return null;
    }

    /**
     * Gets the upgrade status response.
     *
     * @param upgradeSpec the upgrade spec
     * @param status the status
     * @param message the message
     * @return the upgrade status response
     */
    private static Response getUpgradeStatusResponse( final OobUpgradeSpec upgradeSpec, final Status status,
                                                      final String message )
    {
        UpgradeStatus upgradeStatus = new UpgradeStatus();
        if ( upgradeSpec != null && StringUtils.isNotBlank( upgradeSpec.getId() ) )
        {
            upgradeStatus.setId( upgradeSpec.getId() );
        }
        if ( status.equals( Status.BAD_REQUEST ) )
        {
            upgradeStatus.setStatusCode( UpgradeStatusCode.HMS_OOB_UPGRADE_INVALID_REQUEST );
            upgradeStatus.setStatusMessage( UpgradeStatusCode.HMS_OOB_UPGRADE_INVALID_REQUEST.getStatusMessage() );
        }
        else if ( status.equals( Status.FORBIDDEN ) )
        {
            upgradeStatus.setStatusCode( UpgradeStatusCode.HMS_OOB_UPGRADE_FORBIDDEN );
            upgradeStatus.setStatusMessage( UpgradeStatusCode.HMS_OOB_UPGRADE_FORBIDDEN.getStatusMessage() );
        }
        upgradeStatus.setMoreInfo( message );
        return Response.status( status ).entity( upgradeStatus ).build();
    }

    /**
     * Validates rollback request.
     *
     * @param rollbackSpec the rollback spec
     * @throws HMSRestException the HMS rest exception
     */
    public static void validateRollbackRequest( RollbackSpec rollbackSpec )
        throws HMSRestException
    {
        String message = null;
        if ( rollbackSpec != null )
        {
            if ( StringUtils.isBlank( rollbackSpec.getScriptsLocation() ) )
            {
                message = "'scriptsLocation' is a mandatory parameter for HMS Upgrade rollback.";
                throw new HMSRestException( Status.BAD_REQUEST.getStatusCode(), Status.BAD_REQUEST.getReasonPhrase(),
                                            message );
            }
            else if ( StringUtils.isBlank( rollbackSpec.getId() ) )
            {
                message = "'id' is a mandatory parameter for HMS Upgrade rollback.";
                throw new HMSRestException( Status.BAD_REQUEST.getStatusCode(), Status.BAD_REQUEST.getReasonPhrase(),
                                            message );
            }
        }
        else
        {
            message =
                "HMS rollback upgrade request should have the mandatory parameters - " + "[scriptsLocation, id ] ";
            throw new HMSRestException( Status.BAD_REQUEST.getStatusCode(), Status.BAD_REQUEST.getReasonPhrase(),
                                        message );
        }
        // before rollback check if rollback script and backup directory exists.
        String rollbackScript = FilenameUtils.concat( rollbackSpec.getScriptsLocation(),
                                                      HmsConfigHolder.getHMSConfigProperty( ROLLABACK_SCRIPT ) );
        // check that upgrade binary file exists
        if ( !FileUtil.isFileExists( rollbackScript ) )
        {
            logger.debug( "Upgrade rollback script {} not found at {}.",
                          HmsConfigHolder.getHMSConfigProperty( ROLLABACK_SCRIPT ), rollbackSpec.getScriptsLocation() );
            message = String.format( "Upgrade rollback Script %s not found.", rollbackScript );
            throw new HMSRestException( Status.FORBIDDEN.getStatusCode(), Status.FORBIDDEN.getReasonPhrase(), message );
        }
        String backupDir = String.format( "%s/hms_backup_%s", HmsConfigHolder.getHMSConfigProperty( HMS_PARENT_DIR ),
                                          rollbackSpec.getId() );
        if ( !FileUtil.isDirExists( backupDir ) )
        {
            message = String.format( "Backup directory %s does not exist.", backupDir );
            logger.debug( message );
            throw new HMSRestException( Status.FORBIDDEN.getStatusCode(), Status.FORBIDDEN.getReasonPhrase(), message );
        }
    }

    /**
     * Gets the file name.
     *
     * @param formDataMap the form data map
     * @return the file name
     */
    public static String getFileName( final Map<String, List<InputPart>> formDataMap )
    {
        if ( formDataMap == null )
        {
            return null;
        }
        List<InputPart> inputParts = formDataMap.get( "fileName" );
        if ( ( inputParts == null ) || ( inputParts != null && inputParts.size() == 0 ) )
        {
            logger.debug( "Form data does not contain 'fileName' input." );
            return null;
        }
        InputPart fileNameInputPart = inputParts.get( 0 );
        if ( fileNameInputPart != null )
        {
            try
            {
                return fileNameInputPart.getBodyAsString();
            }
            catch ( IOException e )
            {
                logger.error( "Error while reading fileName input from the form data.", e );
            }
        }
        else
        {
            logger.debug( "Form data does not contain 'fileName' input." );
            return null;
        }
        return null;
    }

    /**
     * Gets the file content.
     *
     * @param formDataMap the form data map
     * @return the file content
     */
    public static byte[] getFileContent( final Map<String, List<InputPart>> formDataMap )
    {
        if ( formDataMap == null )
        {
            return null;
        }
        List<InputPart> inputParts = formDataMap.get( "fileContent" );
        if ( ( inputParts == null ) || ( inputParts != null && inputParts.size() == 0 ) )
        {
            logger.debug( "Form data does not contain 'fileContent' input." );
            return null;
        }
        InputPart fileContentInputPart = inputParts.get( 0 );
        if ( fileContentInputPart != null )
        {
            InputStream inputStream = null;
            try
            {
                inputStream = fileContentInputPart.getBody( InputStream.class, null );
            }
            catch ( IOException e )
            {
                logger.error( "Error while getting 'fileContent' as InputStream.", e );
                return null;
            }
            try
            {
                return IOUtils.toByteArray( inputStream );
            }
            catch ( IOException e )
            {
                logger.error( "Error while converting 'fileContent' InputStream to bytes", e );
                return null;
            }
        }
        else
        {
            logger.debug( "Form data does not contain 'fileContent' input." );
            return null;
        }
    }

    /**
     * Delete upgrade files.
     *
     * @param upgradeSpec the upgrade spec
     */
    public static void deleteUpgradeFiles( final OobUpgradeSpec upgradeSpec )
    {
        if ( upgradeSpec == null )
        {
            return;
        }
        String scriptsLocation = upgradeSpec.getScriptsLocation();
        if ( scriptsLocation != null )
        {
            if ( !FileUtil.deleteDirectory( scriptsLocation ) )
            {
                logger.error( "Failed to delete HMS Upgrade Scripts Location - '{}'", scriptsLocation );
            }
            else
            {
                logger.info( "Deleted HMS Upgrade Scripts Location - '{}'", scriptsLocation );
            }
        }
        String location = upgradeSpec.getLocation();
        if ( !StringUtils.equals( scriptsLocation, location ) )
        {
            if ( !FileUtil.deleteDirectory( scriptsLocation ) )
            {
                logger.error( "Failed to delete HMS Upgrade Bundle Location - '{}'", location );
            }
            else
            {
                logger.info( "Deleted HMS Upgrade Bundle Location - '{}'", location );
            }
        }
    }
}
