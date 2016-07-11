/* ********************************************************************************
 * FileUtil.java
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.common.upgrade.api.ChecksumMethod;

/**
 * <code>FileUtil</code><br>
 * .
 *
 * @author VMware, Inc.
 */
public class FileUtil
{
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger( FileUtil.class );

    /**
     * Instantiates a new file util.
     */
    private FileUtil()
    {
        throw new AssertionError( "FileUtil class has all static utility methods. Instance not needed." );
    }

    /**
     * Checks if is file exists.
     *
     * @param dirAbsPath the directory
     * @param fileName the file name
     * @return true, if is file exists
     */
    public static boolean isFileExists( final String dirAbsPath, final String fileName )
    {
        return FileUtil.isFileExists( FilenameUtils.concat( dirAbsPath, fileName ) );
    }

    /**
     * Checks if is file exists.
     *
     * @param fileAbsPath the absolute path of the file
     * @return true, if is file exists
     */
    public static boolean isFileExists( final String fileAbsPath )
    {
        if ( fileAbsPath != null )
        {
            File file = new File( fileAbsPath );
            if ( file.exists() && !file.isDirectory() )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is dir exists.
     *
     * @param dirAbsPath the dir absolute path
     * @return true, if is dir exists
     */
    public static boolean isDirExists( final String dirAbsPath )
    {
        if ( dirAbsPath != null )
        {
            File file = new File( dirAbsPath );
            if ( file.exists() && file.isDirectory() )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Find files of given extension in the given directory location and returns Array of File objects. Will return the
     * File objects for the files that are directly under the given directory.
     * <p>
     * NOTE: If the files are within the sub-directories of the given directory, they will not be returned (as File
     * objects).
     * <p>
     * The array will be empty if the directory is empty or directory does not contain any files of the given extension.
     * <p>
     * Returns null if the given directory does not exist or is not a directory.
     *
     * @param dirAbsPath absolute path of the directory
     * @param ext the ext (.txt, .log etc.). The extension is case sensitive.
     * @return the file[]
     */
    public static File[] findFiles( final String dirAbsPath, final String ext )
    {
        if ( dirAbsPath != null && ext != null )
        {
            return FileUtil.findFiles( dirAbsPath, new String[] { ext }, false );
        }
        return null;
    }

    /**
     * Finds all the files with the given extension in the given directory and then sets each file as executable.
     *
     * @param dirAbsPath absolute path of the directory
     * @param ext File extension. (.txt, .log etc.). The extension is case sensitive.
     * @return
     *         <p>
     *         Returns true, if no files of the given extension found at the given directory or if all the files have
     *         been set to executable.
     *         <p>
     *         Returns false if either directory is null or extension is null or when failed to set a file as
     *         executable.
     */
    public static boolean setFilesExecutable( final String dirAbsPath, final String ext )
    {
        if ( dirAbsPath != null && ext != null )
        {
            File[] files = FileUtil.findFiles( dirAbsPath, ext );
            if ( files != null && files.length > 0 )
            {
                for ( File file : files )
                {
                    // make it executable to all (owner, group, etc.)
                    if ( file.setExecutable( true, false ) )
                    {
                        logger.debug( "Granted execute right to the file - {}.", file.getAbsolutePath() );
                    }
                    else
                    {
                        logger.debug( "Failed to grant execute right to the file - {}.", file.getAbsolutePath() );
                        return false;
                    }
                }
                return true;
            }
            else
            {
                logger.debug( "No files of '{}' extension found at {}.", ext, dirAbsPath );
                return false;
            }
        }
        return false;
    }

    /**
     * Extracts an archive to the given directory. The archive can be either a *.tar file or *.tar.gz file.
     *
     * @param archiveFileAbsPath Absolute path of the archive file. File can be a *.tar file or *.tar.gz file.
     * @param destDirAbsPath Absolute path of the directory to which the archive to be extracted to. If the directory
     *            does not exist, it will be created.
     * @return true, if successful
     */
    public static boolean extractArchive( final String archiveFileAbsPath, final String destDirAbsPath )
    {
        File tar = new File( archiveFileAbsPath );
        File directory = new File( destDirAbsPath );
        if ( tar.exists() && tar.isFile() )
        {
            if ( !directory.exists() )
            {
                directory.mkdirs();
            }
            if ( directory.isDirectory() )
            {
                return extractArchive( tar, directory );
            }
        }
        return false;
    }

    /**
     * Extracts an archive to the given directory. The archive can be either a *.tar file or *.tar.gz file. Archive will
     * be extracted to destination directory only if both archiveFile and destination directory exists.
     *
     * @param archiveFile Archive to be extracted to.
     * @param destDir Destination directory to which the archive need to be extracted to.
     * @return true, if successful
     */
    public static boolean extractArchive( final File archiveFile, final File destDir )
    {
        if ( ( archiveFile != null && archiveFile.isFile() && archiveFile.exists() )
            && ( destDir != null && destDir.exists() && destDir.isDirectory() ) )
        {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            GzipCompressorInputStream gis = null;
            TarArchiveInputStream tis = null;
            try
            {
                fis = new FileInputStream( archiveFile );
                bis = new BufferedInputStream( fis );
                if ( archiveFile.getPath().endsWith( ".gz" ) )
                {
                    gis = new GzipCompressorInputStream( bis );
                    tis = new TarArchiveInputStream( gis );
                }
                else if ( archiveFile.getPath().endsWith( ".tar" ) )
                {
                    tis = new TarArchiveInputStream( bis );
                }
                TarArchiveEntry tarEntry = null;
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;
                File destPath = null;
                int buffer = 1024;
                while ( ( tarEntry = (TarArchiveEntry) tis.getNextEntry() ) != null )
                {
                    destPath = new File( destDir.getPath() + File.separator + tarEntry.getName() );
                    if ( tarEntry.isDirectory() )
                    {
                        destPath.mkdirs();
                    }
                    else
                    {
                        destPath.createNewFile();
                        byte[] bytesToRead = new byte[buffer];
                        fos = new FileOutputStream( destPath );
                        bos = new BufferedOutputStream( fos, buffer );
                        int length = 0;
                        while ( ( length = tis.read( bytesToRead ) ) != -1 )
                        {
                            bos.write( bytesToRead, 0, length );
                        }
                        bos.close();
                        fos.close();
                    }
                }
                return true;
            }
            catch ( FileNotFoundException e )
            {
                logger.error( "Error while extracting tar.", e );
            }
            catch ( IOException e )
            {
                logger.error( "Error while extracting tar.", e );
            }
            finally
            {
                try
                {
                    if ( tis != null )
                    {
                        tis.close();
                    }
                    if ( gis != null )
                    {
                        gis.close();
                    }
                    if ( bis != null )
                    {
                        bis.close();
                    }
                    if ( fis != null )
                    {
                        fis.close();
                    }
                }
                catch ( IOException e )
                {
                    logger.error( "Error while closing input streams.", e );
                }
            }
        }
        return false;
    }

    /**
     * Find files recursively.
     *
     * @param dirAbsPath the dir abs path
     * @param extensions the extensions
     * @param recursive the recursive
     * @return the collection
     */
    public static File[] findFiles( final String dirAbsPath, final String[] extensions, boolean recursive )
    {
        if ( dirAbsPath != null )
        {
            File dir = new File( dirAbsPath );
            if ( dir.exists() && dir.isDirectory() )
            {
                return findFiles( dir, extensions, recursive );
            }
        }
        return null;
    }

    /**
     * Find files recursively.
     *
     * @param dirAbsPath the directory
     * @param extensions the extensions
     * @param recursive the recursive
     * @return the collection
     */
    public static File[] findFiles( final File dirAbsPath, final String[] extensions, boolean recursive )
    {
        if ( dirAbsPath != null )
        {
            Collection<File> files = FileUtils.listFiles( dirAbsPath, extensions, recursive );
            if ( files != null && files.size() > 0 )
            {
                return files.toArray( new File[files.size()] );
            }
        }
        return null;
    }

    /**
     * Find files matching the pattern in the given directory and returns their java.io.File objects as array.
     *
     * @param dirAbsPath Absolute path of the directory.
     * @param fileNamePattern Regular expression for the file name pattern
     * @param recursive Boolean indicating whether recursive search to be made or not while finding the files.
     * @return the file[]. Returns null if either dirAbsPath is null or fileNamePattern is null or fileNamePattern is
     *         not a valid regular expression or no files matching the pattern found.
     */
    public static File[] findFiles( final String dirAbsPath, final String fileNamePattern, boolean recursive )
    {
        if ( dirAbsPath == null || fileNamePattern == null )
        {
            return null;
        }
        try
        {
            Pattern.compile( fileNamePattern );
        }
        catch ( PatternSyntaxException e )
        {
            logger.error( "Not a valid regular expression - {}.", fileNamePattern, e );
            return null;
        }
        File dir = new File( dirAbsPath );
        if ( dir.exists() && dir.isDirectory() )
        {
            Collection<File> files = null;
            IOFileFilter dirFilter = null;
            if ( recursive )
            {
                dirFilter = TrueFileFilter.INSTANCE;
            }
            files = FileUtils.listFiles( dir, new RegexFileFilter( fileNamePattern ), dirFilter );
            if ( files != null && files.size() > 0 )
            {
                return files.toArray( new File[files.size()] );
            }
        }
        return null;
    }

    /**
     * Computes and returns the checksum of the given file using the given algorithm.
     *
     * @param fileAbsPath Absolute path of the file
     * @param checksumMethod the checksum method (MD5, SHA1, etc.)
     * @return the file checksum
     */
    public static String getFileChecksum( final String fileAbsPath, ChecksumMethod checksumMethod )
    {
        if ( ( fileAbsPath == null ) || ( checksumMethod == null ) )
        {
            return null;
        }
        File file = new File( fileAbsPath );
        if ( ( !file.exists() ) || ( !file.isFile() ) )
        {
            return null;
        }
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try
        {
            fis = new FileInputStream( file );
            bis = new BufferedInputStream( fis );
            if ( checksumMethod == ChecksumMethod.SHA1 )
            {
                return DigestUtils.sha1Hex( bis );
            }
            else if ( checksumMethod == ChecksumMethod.MD5 )
            {
                return DigestUtils.md5Hex( bis );
            }
        }
        catch ( FileNotFoundException e )
        {
            logger.error( "Error while computing checksum for the file - {} using algorithm - {}.", fileAbsPath,
                          checksumMethod.toString(), e );
        }
        catch ( IOException e )
        {
            logger.error( "Error while computing checksum for the file - {} using algorithm - {}.", fileAbsPath,
                          checksumMethod.toString(), e );
        }
        finally
        {
            try
            {
                if ( bis != null )
                {
                    bis.close();
                }
                if ( fis != null )
                {
                    fis.close();
                }
            }
            catch ( IOException e )
            {
            }
        }
        return null;
    }

    /**
     * Delete directory.
     *
     * @param directory the directory
     * @return true, if successful
     */
    public static boolean deleteDirectory( final String directory )
    {
        if ( StringUtils.isBlank( directory ) )
        {
            return false;
        }
        File dir = new File( directory );
        if ( dir.exists() && dir.isDirectory() )
        {
            return FileUtil.deleteDirectory( dir );
        }
        return false;
    }

    /**
     * Delete directory.
     *
     * @param directory the directory
     * @return true, if successful
     */
    public static boolean deleteDirectory( final File directory )
    {
        if ( directory == null || !directory.exists() || !directory.isDirectory() )
        {
            return false;
        }
        try
        {
            FileUtils.deleteDirectory( directory );
            if ( !directory.exists() )
            {
                logger.debug( "Deleted directory - '{}' successfully.", directory.getAbsolutePath() );
                return true;
            }
        }
        catch ( IOException e )
        {
            logger.error( "Error while deleting directory - '{}'.", directory.getAbsolutePath(), e );
        }
        return false;
    }
}
