/* ********************************************************************************
 * UpgradeUtil.java
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
import org.apache.commons.lang3.ArrayUtils;
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
import com.vmware.vrack.hms.common.util.Constants;
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

    /** The Constant HMS_UPGRADE_DIR. */
    private static final String HMS_UPGRADE_DIR = "hms.upgrade.dir";

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
    public static boolean initiateUpgrade( final String upgradeId, final String upgradeBinaryFileName )
    {

        String upgradeScript = HmsConfigHolder.getHMSConfigProperty( UPGRADE_SCRIPT );
        final String upgradeDir = UpgradeUtil.getUpgradeDir( upgradeId );

        if ( upgradeId != null && upgradeBinaryFileName != null && upgradeScript != null )
        {

            // upgrade script abs path
            String upgradeScriptAbsPath = FilenameUtils.concat( upgradeDir, upgradeScript );

            try
            {

                List<String> cmdWithArgs = new ArrayList<String>();
                cmdWithArgs.add( 0, upgradeScriptAbsPath );

                // HMS_TOKEN
                cmdWithArgs.add( 1, upgradeId );

                // HMS_SCRIPT_DIR
                cmdWithArgs.add( 2, upgradeDir );

                // HMS_OOB_BINARY_LOCATION
                cmdWithArgs.add( 3, upgradeDir );

                // HMS_OOB_BINARY_FILENAME
                cmdWithArgs.add( 4, upgradeBinaryFileName );

                // HMS_OOB_BINARY_CHECKSUM_FILENAME
                cmdWithArgs.add( 5, upgradeBinaryFileName + ".md5" );

                int exitValue = ProcessUtil.getCommandExitValue( cmdWithArgs );
                if ( exitValue == 0 )
                {
                    return true;
                }
            }
            catch ( Exception e )
            {
                logger.error( "Error while initiating upgrade using script '{}'.", upgradeScriptAbsPath, e );
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

        if ( rollbackSpec == null )
        {
            logger.debug( "RollbackSpec is null." );
            return false;
        }

        final String upgradeId = rollbackSpec.getId();
        if ( StringUtils.isBlank( upgradeId ) )
        {
            logger.debug( "RollbackSpec id is either null or blank." );
            return false;
        }

        final String upgradeDir = UpgradeUtil.getUpgradeDir( upgradeId );
        final String rollbackScript = HmsConfigHolder.getHMSConfigProperty( ROLLABACK_SCRIPT );
        final String hmsParentDir = HmsConfigHolder.getHMSConfigProperty( HMS_PARENT_DIR );
        final String hmsDir = HmsConfigHolder.getHMSConfigProperty( HMS_DIR );

        if ( rollbackScript != null && hmsParentDir != null && hmsDir != null )
        {

            final String rollbackScriptAbsPath = FilenameUtils.concat( upgradeDir, rollbackScript );
            List<String> cmdWithArgs = new ArrayList<String>();

            // SCRIPT TO BE EXECUTED
            cmdWithArgs.add( 0, rollbackScriptAbsPath );

            // HMS_TOKEN
            cmdWithArgs.add( 1, upgradeId );

            // HMS_SCRIPT_DIR
            cmdWithArgs.add( 2, upgradeDir );

            // HMS_OOB_BACKUP_DIR_FULLPATH
            String backupDirAbsPath = FilenameUtils.concat( hmsParentDir, "hms_backup_" + upgradeId );
            cmdWithArgs.add( 3, backupDirAbsPath );

            // HMS_DIR_FULLPATH
            cmdWithArgs.add( 4, hmsDir );

            int exitValue = ProcessUtil.getCommandExitValue( cmdWithArgs );
            if ( exitValue == 0 )
            {
                return true;
            }
            else
            {
                logger.debug( "Error while executing rollback script. Script execution returned: {}", exitValue );
                return false;
            }
        }
        else
        {
            logger.info( "Either HMS directory or HMS parent direcotry or rollbackScript not configured."
                + " [ hmsDir: {}; hmsParentDir: {}; rollbackScript: {}.", hmsDir, hmsParentDir, rollbackScript );
            return false;
        }
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
        String upgradeId = null;

        if ( upgradeSpec == null )
        {
            message = "HMS upgrade request should have the mandatory parameters - [ id, fileName, checksum ].";
            logger.error( message );
            return UpgradeUtil.getUpgradeStatusResponse( null, Status.BAD_REQUEST, message );
        }
        logger.debug( "Validating upgrade request: {}", upgradeSpec.toString() );

        // check the upgrade id is not null or blank.
        if ( StringUtils.isBlank( upgradeSpec.getId() ) )
        {
            message = "'id' is a mandatory parameter for HMS Upgrade.";
            logger.error( message );
            return UpgradeUtil.getUpgradeStatusResponse( null, Status.BAD_REQUEST, message );
        }
        upgradeId = upgradeSpec.getId();

        // check the upgrade fileName is not null or blank.
        if ( StringUtils.isBlank( upgradeSpec.getFileName() ) )
        {
            message = "'fileName' is a mandatory parameter for HMS Upgrade.";
            logger.error( message );
            return UpgradeUtil.getUpgradeStatusResponse( upgradeId, Status.BAD_REQUEST, message );
        }

        // check the upgrade binary checksum is not null or blank.
        if ( StringUtils.isBlank( upgradeSpec.getChecksum() ) )
        {
            message = "'checksum' is a mandatory parameter for HMS Upgrade.";
            logger.error( message );
            return UpgradeUtil.getUpgradeStatusResponse( upgradeId, Status.BAD_REQUEST, message );
        }

        Response response = UpgradeUtil.validateUpgradeBinaryChecksum( upgradeSpec );
        if ( response != null )
        {
            return response;
        }

        response = UpgradeUtil.validateUpgradeScripts( upgradeId );
        if ( response != null )
        {
            return response;
        }
        logger.info( "Valid upgrade request: {}", upgradeSpec.toString() );
        return null;
    }

    /**
     * Validate upgrade binary checksum.
     *
     * @param upgradeSpec the upgrade spec
     * @return the response
     */
    public static Response validateUpgradeBinaryChecksum( final OobUpgradeSpec upgradeSpec )
    {

        String message = null;
        if ( upgradeSpec == null || StringUtils.isBlank( upgradeSpec.getId() )
            || StringUtils.isBlank( upgradeSpec.getFileName() ) || StringUtils.isBlank( upgradeSpec.getChecksum() ) )
        {
            message = "HMS upgrade spec paramaters can't be null or blank - [ id , fileName, checksum ].";
            logger.error( message );
            return UpgradeUtil.getUpgradeStatusResponse( null, Status.BAD_REQUEST, message );
        }

        final String upgradeId = upgradeSpec.getId();
        final String upgradeDir = UpgradeUtil.getUpgradeDir( upgradeId );

        // check that upgrade binary file exists
        final String fileName = FilenameUtils.concat( upgradeDir, upgradeSpec.getFileName() );
        if ( !FileUtil.isFileExists( fileName ) )
        {
            message = String.format( "Upgrade binary '%s' not found at '%s'.", upgradeSpec.getFileName(), upgradeDir );
            logger.error( message );
            return UpgradeUtil.getUpgradeStatusResponse( upgradeId, Status.FORBIDDEN, message );
        }

        // validate upgrade binary checksum
        final String upgradeBinaryChecksum = FileUtil.getFileChecksum( fileName, ChecksumMethod.SHA1 );
        if ( upgradeBinaryChecksum == null || !upgradeBinaryChecksum.equals( upgradeSpec.getChecksum() ) )
        {
            message = String.format(
                                     "Upgrade binary checksum validation failed. "
                                         + "[Binary Checksum - %s, Upgrade Spec Checksum - %s ]",
                                     upgradeBinaryChecksum, upgradeSpec.getChecksum() );
            logger.error( message );
            return UpgradeUtil.getUpgradeStatusResponse( upgradeId, Status.FORBIDDEN, message );
        }
        return null;
    }

    /**
     * Validate upgrade scripts.
     *
     * @param upgradeId the upgrade id
     * @return the response
     */
    public static Response validateUpgradeScripts( final String upgradeId )
    {

        String message = null;
        final String upgradeDir = UpgradeUtil.getUpgradeDir( upgradeId );

        File[] upgradeScriptFiles = FileUtil.findFiles( upgradeDir, "hms_oob_.*.sh", false );
        if ( upgradeScriptFiles == null )
        {
            message = "HMS upgrade scripts not found at '" + upgradeDir + "'";
            logger.error( message );
            return UpgradeUtil.getUpgradeStatusResponse( upgradeId, Status.FORBIDDEN, message );
        }

        String[] upgradeScriptFileNames = new String[upgradeScriptFiles.length];
        for ( int index = 0; index < upgradeScriptFileNames.length; index++ )
        {
            upgradeScriptFileNames[index] = upgradeScriptFiles[index].getName();
        }

        for ( String upgradeScript : Constants.HMS_UPGRADE_OOB_SCRIPTS )
        {
            if ( !ArrayUtils.contains( upgradeScriptFileNames, upgradeScript ) )
            {
                message = String.format( "HMS upgrade script '%s' not found at '%s'.", upgradeScript, upgradeDir );
                logger.error( message );
                return UpgradeUtil.getUpgradeStatusResponse( upgradeId, Status.FORBIDDEN, message );
            }
        }
        return null;
    }

    /**
     * Gets the upgrade status response.
     *
     * @param upgradeId the upgrade id
     * @param status the status
     * @param message the message
     * @return the upgrade status response
     */
    private static Response getUpgradeStatusResponse( final String upgradeId, final Status status,
                                                      final String message )
    {

        UpgradeStatus upgradeStatus = new UpgradeStatus();

        if ( StringUtils.isNoneBlank( upgradeId ) )
        {
            upgradeStatus.setId( upgradeId );
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
        if ( rollbackSpec == null )
        {
            message = "HMS rollback upgrade request should have the mandatory parameters - [ id ].";
            throw new HMSRestException( Status.BAD_REQUEST.getStatusCode(), Status.BAD_REQUEST.getReasonPhrase(),
                                        message );
        }

        final String upgradeId = rollbackSpec.getId();
        if ( StringUtils.isBlank( upgradeId ) )
        {
            message = "'id' is a mandatory parameter for HMS Upgrade rollback.";
            throw new HMSRestException( Status.BAD_REQUEST.getStatusCode(), Status.BAD_REQUEST.getReasonPhrase(),
                                        message );
        }

        // check that rollback script exists
        UpgradeUtil.isRollbackScriptExists( upgradeId );

        // check that backup directory exists
        UpgradeUtil.isBackupDirExists( upgradeId );
    }

    /**
     * Checks if is rollback script exists.
     *
     * @param upgradeId the upgrade id
     * @return true, if is rollback script exists
     * @throws HMSRestException the HMS rest exception
     */
    private static boolean isRollbackScriptExists( final String upgradeId )
        throws HMSRestException
    {

        final String upgradeDir = UpgradeUtil.getUpgradeDir( upgradeId );
        final String rollbackScript = HmsConfigHolder.getHMSConfigProperty( ROLLABACK_SCRIPT );
        final String rollbackScriptAbsPath = FilenameUtils.concat( upgradeDir, rollbackScript );

        if ( !FileUtil.isFileExists( rollbackScriptAbsPath ) )
        {
            logger.debug( "Upgrade rollback script '{}' not found at '{}'.", rollbackScript, upgradeDir );
            throw new HMSRestException( Status.FORBIDDEN.getStatusCode(), Status.FORBIDDEN.getReasonPhrase(),
                                        "Rollback of upgrade failed." );
        }
        else
        {
            return true;
        }
    }

    /**
     * Checks if is backup dir exists.
     *
     * @param upgradeId the upgrade id
     * @return true, if is backup dir exists
     * @throws HMSRestException the HMS rest exception
     */
    private static boolean isBackupDirExists( final String upgradeId )
        throws HMSRestException
    {

        final String hmsParentDir = HmsConfigHolder.getHMSConfigProperty( HMS_PARENT_DIR );
        String backupDir = String.format( "%s/hms_backup_%s", hmsParentDir, upgradeId );

        if ( !FileUtil.isDirExists( backupDir ) )
        {
            final String message = String.format( "Backup directory %s does not exist.", backupDir );
            logger.debug( message );
            throw new HMSRestException( Status.FORBIDDEN.getStatusCode(), Status.FORBIDDEN.getReasonPhrase(), message );
        }
        else
        {
            return true;
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
        return UpgradeUtil.getProperty( "fileName", formDataMap );
    }

    public static String getUpgradeId( final Map<String, List<InputPart>> formDataMap )
    {
        return UpgradeUtil.getProperty( "upgradeId", formDataMap );
    }

    /**
     * Gets the property.
     *
     * @param propertyName the property name
     * @param formDataMap the form data map
     * @return the property
     */
    private static String getProperty( final String propertyName, final Map<String, List<InputPart>> formDataMap )
    {

        if ( formDataMap == null || propertyName == null )
        {
            return null;
        }

        List<InputPart> inputParts = formDataMap.get( propertyName );
        if ( ( inputParts == null ) || ( inputParts != null && inputParts.size() == 0 ) )
        {
            logger.debug( "Form data does not contain '{}' input.", propertyName );
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
                logger.error( "Error while reading '{}' input from the form data.", propertyName, e );
            }
        }
        else
        {
            logger.debug( "Form data does not contain '{}' input.", propertyName );
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
     * @param upgradeId the upgrade id
     */
    public static void deleteUpgradeFiles( final String upgradeId )
    {
        if ( StringUtils.isNotBlank( upgradeId ) )
        {
            final String upgradeDir = UpgradeUtil.getUpgradeDir( upgradeId );
            if ( !FileUtil.deleteDirectory( upgradeDir ) )
            {
                logger.warn( "Failed to delete HMS upgrade files at - '{}'", upgradeDir );
            }
            else
            {
                logger.info( "Deleted HMS upgrade files at - '{}'", upgradeDir );
            }
        }
    }

    /**
     * Gets the hms upgrade dir.
     *
     * @return the hms upgrade dir
     */
    public static String getHmsUpgradeDir()
    {
        return HmsConfigHolder.getHMSConfigProperty( UpgradeUtil.HMS_UPGRADE_DIR );
    }

    /**
     * Gets the upgrade dir.
     *
     * @param upgradeId the upgrade id
     * @return the upgrade dir
     */
    public static String getUpgradeDir( final String upgradeId )
    {
        return FilenameUtils.concat( UpgradeUtil.getHmsUpgradeDir(), upgradeId );
    }
}
