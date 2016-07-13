/* ********************************************************************************
 * HmsUpgradeTask.java
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.aggregator.ServiceManager;
import com.vmware.vrack.hms.common.resource.UpgradeStatusCode;
import com.vmware.vrack.hms.common.rest.model.OobUpgradeSpec;
import com.vmware.vrack.hms.common.rest.model.UpgradeStatus;
import com.vmware.vrack.hms.common.service.ServiceState;
import com.vmware.vrack.hms.common.upgrade.api.ChecksumMethod;
import com.vmware.vrack.hms.common.util.FileUtil;
import com.vmware.vrack.hms.common.util.HmsUpgradeUtil;
import com.vmware.vrack.hms.common.util.ProcessUtil;
import com.vmware.vrack.primitive.reference.rest.model.v1.UpgradeSpec;

/**
 * <code>HmsUpgradeTask</code><br>
 *
 * @author VMware, Inc.
 */
public class HmsUpgradeTask
    implements Callable<Void>
{
    /** The hms upgrade spec. */
    private UpgradeSpec upgradeSpec;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger( HmsUpgradeTask.class );

    /** The oob agent host. */
    private String oobAgentHost;

    /** The oob agent port. */
    private int oobAgentPort;

    /** The oob upgrade dir. */
    private String oobUpgradeDir;

    /** The bundle extracted dir abs path. */
    private String bundleExtractedDirAbsPath;

    /** The oob upgrade max timeout. */
    private int oobUpgradeMaxTimeout;

    /** The oob upgrade retry interval. */
    private int oobUpgradeRetryInterval;

    /** The hms upgrade script. */
    private String hmsUpgradeScript;

    /** The hms upgrade dir. */
    private String hmsUpgradeDir;

    /** The hms backup dir. */
    private String hmsBackupDir;

    /** The shutdown monitoring max waittime. */
    private Long shutdownMonitoringMaxWaittime;

    /** The service maintenance max waittime. */
    private int serviceMaintenanceMaxWaittime;

    /** The service maintenance retry interval. */
    private int serviceMaintenanceRetryInterval;

    /** The oob nodes endpoint. */
    private String oobNodesEndpoint;

    /** The hms ib inventory location. */
    private String hmsIbInventoryLocation;

    /** The upgrade status file name abs path. */
    private String upgradeStatusFileNameAbsPath;

    /** The oob agent upgrade bundle abs path. */
    private String oobAgentUpgradeBundleAbsPath;

    /** The dest dir on the oob agent host. */
    private String destDirOnTheOobAgentHost;

    /** The prm user name. */
    private String prmUserName;

    /** The prm password. */
    private String prmPassword;

    /**
     * Instantiates a new hms upgrade task.
     *
     * @param upgradeSpec the hms upgrade spec
     * @param bundleExtractedDirAbsPath the bundle extracted dir abs path
     * @param hmsUpgradeScript the hms upgrade script
     * @param hmsBackupDir the hms backup dir
     * @param shutdownMonitoringMaxWaittime the shutdown monitoring max waittime
     * @param serviceMaintenanceMaxWaittime the service maintenance max waittime
     * @param serviceMaintenanceRetryInterval the service maintenance retry interval
     * @param hmsIbInventoryLocation the hms ib inventory location
     * @param oobAgentUpgradeBundleAbsPath the oob agent upgrade bundle abs path
     * @param oobAgentHost the oob agent host
     * @param oobAgentPort the oob agent port
     * @param oobUpgradeDir the oob upgrade dir
     * @param oobUpgradeMaxTimeout the oob upgrade max timeout
     * @param oobUpgradeRetryInterval the oob upgrade retry interval
     * @param oobNodesEndpoint the oob nodes endpoint
     * @param prmUserName the prm user name
     * @param prmPassword the prm password
     */
    public HmsUpgradeTask( UpgradeSpec upgradeSpec, String hmsUpgradeDir, String bundleExtractedDirAbsPath,
                           String hmsUpgradeScript, String hmsBackupDir, Long shutdownMonitoringMaxWaittime,
                           int serviceMaintenanceMaxWaittime, int serviceMaintenanceRetryInterval,
                           String hmsIbInventoryLocation, String oobAgentUpgradeBundleAbsPath, String oobAgentHost,
                           int oobAgentPort, String oobUpgradeDir, int oobUpgradeMaxTimeout,
                           int oobUpgradeRetryInterval, String oobNodesEndpoint, String prmUserName,
                           String prmPassword )
    {
        this.upgradeSpec = upgradeSpec;
        this.hmsUpgradeDir = hmsUpgradeDir;
        this.oobUpgradeDir = oobUpgradeDir;
        this.oobAgentPort = oobAgentPort;
        this.oobAgentHost = oobAgentHost;
        this.bundleExtractedDirAbsPath = bundleExtractedDirAbsPath;
        this.oobUpgradeMaxTimeout = oobUpgradeMaxTimeout;
        this.oobUpgradeRetryInterval = oobUpgradeRetryInterval;
        this.hmsUpgradeScript = hmsUpgradeScript;
        this.hmsBackupDir = hmsBackupDir;
        this.shutdownMonitoringMaxWaittime = shutdownMonitoringMaxWaittime;
        this.serviceMaintenanceMaxWaittime = serviceMaintenanceMaxWaittime;
        this.serviceMaintenanceRetryInterval = serviceMaintenanceRetryInterval;
        this.hmsIbInventoryLocation = hmsIbInventoryLocation;
        this.oobNodesEndpoint = oobNodesEndpoint;
        this.oobAgentUpgradeBundleAbsPath = oobAgentUpgradeBundleAbsPath;
        this.prmPassword = prmPassword;
        this.prmUserName = prmUserName;
        upgradeStatusFileNameAbsPath =
            String.format( "%1$s/%2$s.json", this.hmsUpgradeDir, upgradeSpec.getUpgradeId() );
        /*
         * Use oobUpgradeDir + hmsUpgradeSpec.getId() as the upgrade directory on the oob agent host (Management Switch)
         */
        destDirOnTheOobAgentHost = String.format( "%s/%s", this.oobUpgradeDir, upgradeSpec.getUpgradeId() );
    }

    /**
     * Call.
     *
     * @return the hms upgrade status
     * @throws Exception the exception
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Void call()
        throws Exception
    {
        UpgradeStatus upgradeStatus = new UpgradeStatus();
        upgradeStatus.setId( upgradeSpec.getUpgradeId() );
        boolean hmsUpgradeScriptCopied = this.copyHmsUpgradeScripts();
        if ( !hmsUpgradeScriptCopied )
        {
            // delete bundleExtractedDirAbsPath
            if ( !FileUtil.deleteDirectory( bundleExtractedDirAbsPath ) )
            {
                logger.warn( "Failed to delete HMS upgrade bundle extracted directory -'{}'.",
                             bundleExtractedDirAbsPath );
            }
            logger.error( "Unable to copy and grant execution rights to HMS upgrade scripts at '{}'.",
                          bundleExtractedDirAbsPath );
            upgradeStatus.setStatusCode( UpgradeStatusCode.HMS_UPGRADE_FORBIDDEN );
            this.saveUpgradeStatus( upgradeStatus );
            return null;
        }
        // STEP#1: OUT SERVICE IN MAINTENANCE
        ServiceManager.putServiceInMaintenance( shutdownMonitoringMaxWaittime, serviceMaintenanceMaxWaittime,
                                                serviceMaintenanceRetryInterval );
        // STEP#2: get current oob agent version
        String oobVersionBeforeUpgrade = UpgradeUtil.getOobBuildVersion();
        if ( oobVersionBeforeUpgrade == null )
        {
            logger.warn( "Unable to get OOB Agent version before upgrade." );
            /*
             * We are not checking OOB Agent version before and after upgrade. To determine whether upgrade succeeded or
             * failed, we are depending on the status returned by upgrade monitoring api of the oob agent.
             */
            // this.setHmsInRunning();
            // return null;
        }
        else
        {
            logger.info( "HMS OOB Agent version before upgrade '{}'.", oobVersionBeforeUpgrade );
        }
        // STEP#3: Upload OOB Agent upgrade files to management switch.
        boolean oobUpgradeFileUploaded = this.uploadOobUpgradeFiles();
        if ( !oobUpgradeFileUploaded )
        {
            // delete bundleExtractedDirAbsPath
            if ( !FileUtil.deleteDirectory( bundleExtractedDirAbsPath ) )
            {
                logger.warn( "Failed to delete HMS upgrade bundle extracted directory -'{}'.",
                             bundleExtractedDirAbsPath );
            }
            logger.error( "Unable to upload OOB Agent upgrade files to OOB Agent Host - {}.", oobAgentHost );
            upgradeStatus.setStatusCode( UpgradeStatusCode.HMS_UPGRADE_FORBIDDEN );
            this.setHmsInRunning( upgradeStatus );
            return null;
        }
        // STEP#5: initiate oob agent upgrade
        boolean oobAgentUpgradeInitiated = this.initiateOobAgentUpgrade();
        if ( !oobAgentUpgradeInitiated )
        {
            // delete bundleExtractedDirAbsPath
            if ( !FileUtil.deleteDirectory( bundleExtractedDirAbsPath ) )
            {
                logger.warn( "Failed to delete HMS upgrade bundle extracted directory -'{}'.",
                             bundleExtractedDirAbsPath );
            }
            upgradeStatus.setStatusCode( UpgradeStatusCode.HMS_UPGRADE_FORBIDDEN );
            this.setHmsInRunning( upgradeStatus );
            return null;
        }
        UpgradeStatus oobUpgradeStatus =
            UpgradeUtil.getOobUpgradeStatus( oobAgentHost, oobAgentPort, upgradeSpec.getUpgradeId(),
                                             oobUpgradeMaxTimeout, oobUpgradeRetryInterval );
        if ( oobUpgradeStatus == null )
        {
            // delete bundleExtractedDirAbsPath
            if ( !FileUtil.deleteDirectory( bundleExtractedDirAbsPath ) )
            {
                logger.warn( "Failed to delete HMS upgrade bundle extracted directory -'{}'.",
                             bundleExtractedDirAbsPath );
            }
            /*
             * if oobUpgradeStatus is null, that means something went wrong with OOB Upgrade and needs manual
             * intervention to bring it into running state. UpgradeStatus should reflect this fact.
             */
            upgradeStatus.setStatusCode( UpgradeStatusCode.HMS_UPGRADE_FAILED_RESTART_REQUIRED );
            upgradeStatus.setMoreInfo( "HMS OOB did not come up after initiating upgrade." );
            this.setHmsInRunning( upgradeStatus );
            return null;
        }
        else
        {
            UpgradeStatusCode oobUpgradeStatusCode = oobUpgradeStatus.getStatusCode();
            if ( oobUpgradeStatusCode != null
                && !oobUpgradeStatusCode.equals( UpgradeStatusCode.HMS_OOB_UPGRADE_SUCCESS ) )
            {
                // delete bundleExtractedDirAbsPath
                if ( !FileUtil.deleteDirectory( bundleExtractedDirAbsPath ) )
                {
                    logger.warn( "Failed to delete HMS upgrade bundle extracted directory -'{}'.",
                                 bundleExtractedDirAbsPath );
                }
                logger.error( "HMS OOB Agent upgrade failed. Status Code: {}, Status Code Message: {}.",
                              oobUpgradeStatusCode.toString(), oobUpgradeStatusCode.getStatusMessage() );
                upgradeStatus.setStatusCode( UpgradeStatusCode.HMS_UPGRADE_FAILED );
                if ( oobUpgradeStatus.getMoreInfo() != null )
                {
                    upgradeStatus.setMoreInfo( oobUpgradeStatus.getMoreInfo() );
                }
                else
                {
                    upgradeStatus.setMoreInfo( "HMS OOB Agent upgrade failed." );
                }
                this.setHmsInRunning( upgradeStatus );
                return null;
            }
        }
        // STEP#6: validate oob agent upgrade
        String oobVersionAfterUpgrade = UpgradeUtil.getOobBuildVersion();
        if ( ( oobVersionAfterUpgrade == null ) || ( oobVersionBeforeUpgrade.equals( oobVersionAfterUpgrade ) ) )
        {
            // OOB Upgrade FAILED.
            // TODO: Need to check if we want to do additional version check
            // after upgrade.
            if ( oobVersionAfterUpgrade == null )
            {
                logger.error( "Unable to get OOB Agent version after upgrade." );
            }
            else if ( oobVersionBeforeUpgrade != null && oobVersionBeforeUpgrade.equals( oobVersionAfterUpgrade ) )
            {
                logger.warn( "Before and after upgrade, OOB version is the same." );
            }
            else
            {
                logger.info( "HMS OOB Agent version after upgrade '{}'.", oobVersionAfterUpgrade );
            }
        }
        // STEP#9: execute hms aggregator upgrade script
        String hmsUpgradeScriptAbsPath = bundleExtractedDirAbsPath + File.separator + hmsUpgradeScript;
        // build script command
        List<String> cmdWithArgs = new ArrayList<String>();
        cmdWithArgs.add( 0, hmsUpgradeScriptAbsPath );
        // use id as token
        cmdWithArgs.add( 1, upgradeSpec.getUpgradeId() );
        // bundleExtractedDirAbsPath is the scripts dir
        cmdWithArgs.add( 2, bundleExtractedDirAbsPath );
        // backup dir
        cmdWithArgs.add( 3, hmsBackupDir );
        // hms upgrade bundle
        File[] f = FileUtil.findFiles( bundleExtractedDirAbsPath, "hms-local.war", true );
        String hmsUpgradeBundle = f[0].getName();
        String hmsUpgradeBundleDir = f[0].getParent();
        // hms upgrade binary dir
        cmdWithArgs.add( 4, hmsUpgradeBundleDir );
        cmdWithArgs.add( 5, hmsUpgradeBundle );
        // oob agent host
        cmdWithArgs.add( 6, oobAgentHost );
        /*
         * directory on the oob agent host, to which oob agent binary and upgrade scripts have been uploaded to
         */
        cmdWithArgs.add( 7, destDirOnTheOobAgentHost );
        int scriptExitValue = ProcessUtil.getCommandExitValue( cmdWithArgs );
        if ( scriptExitValue != -1 )
        {
            logger.info( "HMS Aggregator upgrade script executed successfully." );
        }
        else
        {
            // delete bundleExtractedDirAbsPath
            if ( !FileUtil.deleteDirectory( bundleExtractedDirAbsPath ) )
            {
                logger.warn( "Failed to delete HMS upgrade bundle extracted directory -'{}'.",
                             bundleExtractedDirAbsPath );
            }
            logger.error( "Failed to execute HMS Aggregator upgrade script." );
            upgradeStatus.setStatusCode( UpgradeStatusCode.HMS_UPGRADE_FAILED );
            this.setHmsInRunning( oobUpgradeStatus );
            return null;
        }
        return null;
    }

    /**
     * Sets the hms in running.
     */
    private void setHmsInRunning( final UpgradeStatus upgradeStatus )
    {
        // PUT BACK SERVICE IN RUNNING STATE
        boolean running =
            ServiceManager.setServiceInRunning( hmsIbInventoryLocation, oobAgentHost, oobAgentPort, oobNodesEndpoint );
        if ( running )
        {
            logger.info( "Successfully set service back into running state." );
            // Notify PRM about HMS is back to RUNNING state
            if ( !UpgradeUtil.notifyPRMService( ServiceState.RUNNING, prmUserName, prmPassword ) )
            {
                logger.error( "Unable to notify PRM about HMS in RUNNING state." );
                upgradeStatus.setStatusCode( UpgradeStatusCode.HMS_UPGRADE_FAILED_RESTART_REQUIRED );
            }
        }
        else
        {
            /*
             * Monitoring API should reflect the status that HMS needs manual intervention to bring it back to normal.
             */
            upgradeStatus.setStatusCode( UpgradeStatusCode.HMS_UPGRADE_FAILED_RESTART_REQUIRED );
            logger.info( "Unable to set service back into running state." );
        }
        this.saveUpgradeStatus( upgradeStatus );
    }

    /**
     * Save upgrade status.
     *
     * @param upgradeStatus the upgrade status
     */
    private void saveUpgradeStatus( final UpgradeStatus upgradeStatus )
    {
        if ( !HmsUpgradeUtil.saveUpgradeStatus( upgradeStatusFileNameAbsPath, upgradeStatus ) )
        {
            logger.error( "Unable to save UpgradeStatus - '{}' to file '{}'.", upgradeStatus,
                          upgradeStatusFileNameAbsPath );
        }
        else
        {
            logger.debug( "Saved UpgradeStatus - '{}' to file '{}'.", upgradeStatus, upgradeStatusFileNameAbsPath );
        }
    }

    /**
     * Upload oob upgrade files.
     *
     * @return true, if successful
     */
    private boolean uploadOobUpgradeFiles()
    {
        // Upload upgrade bundle to oob agent host
        boolean uploaded =
            UpgradeUtil.uploadFileToManagementSwitch( oobAgentHost, oobAgentPort, oobAgentUpgradeBundleAbsPath,
                                                      destDirOnTheOobAgentHost );
        if ( !uploaded )
        {
            logger.error( "Unable to upload OOB Agent upgrade bundle to OOB Agent host - {}.", oobAgentHost );
            return false;
        }
        // Upload upgrade scripts to oob agent host
        uploaded = UpgradeUtil.uploadOobAgentUpgradeScriptsToOobAgentHost( oobAgentHost, oobAgentPort,
                                                                           destDirOnTheOobAgentHost );
        if ( !uploaded )
        {
            logger.error( "Unable to upload OOB Agent upgrade scripts to OOB Agent host - {}.", oobAgentHost );
            return false;
        }
        return true;
    }

    /**
     * Initiate oob agent upgrade.
     *
     * @return true, if successful
     */
    public boolean initiateOobAgentUpgrade()
    {
        OobUpgradeSpec oobUpgradeSpec = new OobUpgradeSpec();
        oobUpgradeSpec.setId( upgradeSpec.getUpgradeId() );
        File oobAgentUpgradeBundleFile = new File( oobAgentUpgradeBundleAbsPath );
        String oobAgentUpgradeBundle = oobAgentUpgradeBundleFile.getName();
        oobUpgradeSpec.setFileName( oobAgentUpgradeBundle );
        oobUpgradeSpec.setLocation( destDirOnTheOobAgentHost );
        oobUpgradeSpec.setScriptsLocation( destDirOnTheOobAgentHost );
        String oobAgentUpgradeBundleChecksum =
            FileUtil.getFileChecksum( oobAgentUpgradeBundleAbsPath, ChecksumMethod.SHA1 );
        oobUpgradeSpec.setChecksum( oobAgentUpgradeBundleChecksum );
        boolean oobAgentUpgradeInitiated = UpgradeUtil.initiateOobUpgrade( oobAgentHost, oobAgentPort, oobUpgradeSpec );
        if ( !oobAgentUpgradeInitiated )
        {
            logger.error( "Unable to initiate OOB Agent upgrade." );
            return false;
        }
        else
        {
            logger.info( "OOB Agent upgrade initiated." );
            return true;
        }
    }

    /**
     * Copy hms upgrade scripts.
     *
     * @return true, if successful
     */
    public boolean copyHmsUpgradeScripts()
    {
        // STEP#7: copy aggregator upgrade scripts to bundleExtractedDirAbsPath
        boolean hmsUpgradeScriptsCopied = UpgradeUtil.copyHmsUpgradeScripts( bundleExtractedDirAbsPath );
        if ( !hmsUpgradeScriptsCopied )
        {
            logger.error( "Unable to copy HMS Aggregator upgrade scripts to - {}.", bundleExtractedDirAbsPath );
            return false;
        }
        // STEP#8: Grant execute rights to aggregator upgrade scripts at
        // bundleExtractedDirAbsPath
        boolean grantedExecuteRights = FileUtil.setFilesExecutable( bundleExtractedDirAbsPath, "sh" );
        if ( !grantedExecuteRights )
        {
            logger.error( "Unable to grant execution rights to HMS Aggregator upgrade scripts at - {}.",
                          bundleExtractedDirAbsPath );
            return false;
        }
        return true;
    }
}
