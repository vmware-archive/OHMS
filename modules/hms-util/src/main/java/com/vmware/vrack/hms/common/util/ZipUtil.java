/* ********************************************************************************
 * ZipUtil.java
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

package com.vmware.vrack.hms.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>FileUtil</code><br>
 * Utiliy class to perform all zip related operations
 * 
 * @author VMware, Inc.
 */
public class ZipUtil
{

    private static final Logger logger = LoggerFactory.getLogger( ZipUtil.class );

    /**
     * Creates a zip file for all the file paths which are passed as arguments
     * 
     * @param outputZipFile
     * @param inputFilesPaths
     * @return
     */
    public static boolean zipFiles( String outputZipFile, String... inputFilesPaths )
    {

        byte[] buffer = new byte[65536];
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try
        {
            int countFile = 0;
            fos = new FileOutputStream( outputZipFile );
            zos = new ZipOutputStream( fos );
            logger.debug( "Creating Zip file at: {}", outputZipFile );

            for ( String inputFile : inputFilesPaths )
            {
                FileInputStream in = null;
                try
                {
                    String fileName = FilenameUtils.getName( inputFile );
                    countFile = countFile + 1;
                    File logFile = new File( inputFile );
                    if ( !logFile.exists() && !logFile.isFile() )
                    {
                        if ( countFile == 1 )
                        {
                            zos.close();
                            return false;
                        }
                        else
                        {
                            break;
                        }
                    }

                    logger.debug( "Adding file in the zip : {}", inputFile );
                    ZipEntry ze = new ZipEntry( fileName );
                    zos.putNextEntry( ze );

                    in = new FileInputStream( inputFile );

                    int len;
                    while ( ( len = in.read( buffer ) ) > 0 )
                    {
                        zos.write( buffer, 0, len );
                    }
                }
                catch ( Exception e )
                {
                    logger.error( "Error adding entry in the Zip for file : {}", inputFile, e );
                }
                finally
                {
                    try
                    {
                        in.close();
                    }
                    catch ( Exception e )
                    {

                    }
                }
            }

            return true;

        }
        catch ( Exception ex )
        {
            logger.error( "Error creating Zip file at location: {}", outputZipFile, ex );
        }
        finally
        {

            try
            {
                zos.closeEntry();
            }
            catch ( IOException e )
            {

            }
            // close output stream
            try
            {
                zos.close();
            }
            catch ( IOException e )
            {

            }

            logger.debug( "Done compression of {}", outputZipFile );
        }

        return false;
    }
}