/* ********************************************************************************
 * HmsUpgradeUtil.java
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
package com.vmware.vrack.hms.common.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.rest.model.UpgradeStatus;

/**
 * <code>HmsUpgradeUtil</code><br>
 *
 * @author VMware, Inc.
 */
public class HmsUpgradeUtil
{
    /** The Constant mapper. */
    private static final ObjectMapper mapper = new ObjectMapper();

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger( HmsUpgradeUtil.class );

    /**
     * Instantiates a new hms upgrade util.
     */
    private HmsUpgradeUtil()
    {
        throw new AssertionError();
    }

    /**
     * Save upgrade status.
     *
     * @param fileNameAbsPath the file name abs path
     * @param upgradeStatus the upgrade status
     * @return true, if successful
     */
    public static boolean saveUpgradeStatus( final String fileNameAbsPath, final UpgradeStatus upgradeStatus )
    {
        if ( StringUtils.isBlank( fileNameAbsPath ) || upgradeStatus == null )
        {
            return false;
        }
        return HmsUpgradeUtil.saveUpgradeStatus( new File( fileNameAbsPath ), upgradeStatus );
    }

    /**
     * Save upgrade status.
     *
     * @param upgradeStatusFile the upgrade status file
     * @param upgradeStatus the upgrade status
     * @return true, if successful
     */
    public static boolean saveUpgradeStatus( final File upgradeStatusFile, final UpgradeStatus upgradeStatus )
    {
        if ( upgradeStatusFile == null || upgradeStatus == null )
        {
            return false;
        }
        try
        {
            if ( !upgradeStatusFile.exists() )
            {
                upgradeStatusFile.getParentFile().mkdirs();
                upgradeStatusFile.createNewFile();
            }
            mapper.writeValue( upgradeStatusFile, upgradeStatus );
            return true;
        }
        catch ( IOException e )
        {
            logger.error( "Error saving upgrade status to file - {}", upgradeStatusFile.getAbsolutePath(), e );
            return false;
        }
    }

    /**
     * Load upgrade status.
     *
     * @param fileNameAbsPath the file name abs path
     * @return the upgrade status
     */
    public static UpgradeStatus loadUpgradeStatus( final String fileNameAbsPath )
    {
        if ( StringUtils.isBlank( fileNameAbsPath ) )
        {
            return null;
        }
        File upgradeStatusFile = new File( fileNameAbsPath );
        if ( upgradeStatusFile.exists() && upgradeStatusFile.isFile() )
        {
            return HmsUpgradeUtil.loadUpgradeStatus( new File( fileNameAbsPath ) );
        }
        else
        {
            logger.error( "Either '{}' does not exist or is not a file.", fileNameAbsPath );
            return null;
        }
    }

    /**
     * Load upgrade status.
     *
     * @param upgradeStatusFile the upgrade status file
     * @return the upgrade status
     */
    public static UpgradeStatus loadUpgradeStatus( final File upgradeStatusFile )
    {
        if ( upgradeStatusFile == null )
        {
            return null;
        }
        String fileName = upgradeStatusFile.getAbsolutePath();
        if ( upgradeStatusFile.exists() && upgradeStatusFile.isFile() )
        {
            try
            {
                return mapper.readValue( upgradeStatusFile, UpgradeStatus.class );
            }
            catch ( IOException e )
            {
                logger.error( "Error reading '{}' as UpgradeStatus.", fileName, e );
                return null;
            }
        }
        else
        {
            logger.error( "'{}' is either does not exist or not a file.", fileName );
            return null;
        }
    }
}
