/* ********************************************************************************
 * FileUtil.java
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
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

        if ( StringUtils.isNotBlank( fileAbsPath ) )
        {
            File file = null;
            try
            {
                file = new File( fileAbsPath );
                if ( file.exists() && !file.isDirectory() )
                {
                    return true;
                }
            }
            catch ( Exception e )
            {
                logger.error( "In isFileExists, error while checking file {} exists.", fileAbsPath, e );
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

        // check that directory name is not null or blank.
        if ( StringUtils.isBlank( dirAbsPath ) )
        {
            logger.warn( "In isDirExists, directory name is either null or blank." );
            return false;
        }
        File file = new File( dirAbsPath );
        if ( file.exists() && file.isDirectory() )
        {
            return true;
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

        // check that dirAbsPath and fileNamePattern are not null or blank
        if ( StringUtils.isBlank( dirAbsPath ) || StringUtils.isBlank( fileNamePattern ) )
        {
            logger.warn( "In findFiles, either directory or file name pattern is null or blank."
                + " Directory: {}, FileNamePattern: {}.", dirAbsPath, fileNamePattern );
            return null;
        }

        // check that file name pattern is a valid pattern.
        if ( !FileUtil.isValidPattern( fileNamePattern ) )
        {
            logger.warn( "In findFiles, file name pattern '{}' is not a valid pattern.", fileNamePattern );
            return null;
        }

        // check that directory exists and is a directory.
        File dir = new File( dirAbsPath );
        if ( !dir.exists() || !dir.isDirectory() )
        {
            logger.warn( "In findFiles, either directory '{}' does not exists or not a directory.", dirAbsPath );
            return null;
        }
        IOFileFilter dirFilter = recursive ? TrueFileFilter.INSTANCE : null;
        Collection<File> files = FileUtils.listFiles( dir, new RegexFileFilter( fileNamePattern ), dirFilter );
        if ( files == null || files.size() == 0 )
        {
            logger.warn( "In findLatestFileByLastModified, no files found matching pattern '{}' in directory '{}'.",
                         fileNamePattern, dirAbsPath );
            return null;
        }
        return files.toArray( new File[files.size()] );
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

        if ( ( StringUtils.isBlank( fileAbsPath ) ) || ( checksumMethod == null ) )
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
            else if ( checksumMethod == ChecksumMethod.SHA256 )
            {
                return DigestUtils.sha256Hex( bis );
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

    /**
     * Get the temporary folder for current runtime.
     *
     * @param fileName
     * @return
     */
    public static String getTemporaryFolderPath()
    {
        String tempDir = System.getProperty( "java.io.tmpdir" );
        logger.debug( "Temp Directory: {}", tempDir );
        if ( tempDir != null )
        {
            tempDir = tempDir.trim();
            if ( !tempDir.endsWith( File.separator ) )
            {
                tempDir += File.separator;
            }
        }
        return tempDir;
    }

    /**
     * Delete directory.
     *
     * @param filePath the directory
     * @return true, if successful
     */
    public static boolean deleteFile( final String filePath )
    {

        if ( StringUtils.isBlank( filePath ) )
        {
            return false;
        }

        try
        {
            File file = new File( filePath );
            if ( file.exists() && file.isFile() )
            {
                return file.delete();
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error deleting file - {}", filePath );
        }
        return false;
    }

    /**
     * Creates directory along with non existent parent Directory
     *
     * @param filePath
     * @return
     */
    public static boolean createDirectory( final String dirPath )
    {
        boolean dirCreated = false;
        File file = new File( dirPath );
        if ( !file.exists() )
        {
            dirCreated = file.mkdirs();
        }
        else
        {
            dirCreated = true;
        }
        return dirCreated;
    }

    /**
     * Gets the base name, minus the full path and extension, from a full filename.
     * <p>
     * This method will handle a file in either Unix or Windows format. The text after the last forward or backslash and
     * before the last dot is returned.
     * </p>
     * <p>
     * a/b/c.txt --> c <br>
     * a.txt --> a <br>
     * a/b/c --> c <br>
     * a/b/c/ --> ""
     * </p>
     *
     * @param fileName the file name
     * @return the file base name
     */
    public static String getFileBaseName( final String fileName )
    {
        if ( StringUtils.isBlank( fileName ) )
        {
            return null;
        }
        return FilenameUtils.getBaseName( fileName );
    }

    /**
     * Decompress a zip file to the output directory. For example, if the zip file is /a/b/c/some-zip-file.zip and
     * output directory is /out/put, then this utility will decompress the zip archive to /out/put/ folder.
     *
     * @param zipFileAbsPath the zip file abs path
     * @param outputDirAbsPath the output dir abs path
     * @return true, if successful
     */
    public static boolean unzipFile( final String zipFileAbsPath, final String outputDirAbsPath )
    {

        if ( StringUtils.isBlank( zipFileAbsPath ) || StringUtils.isBlank( outputDirAbsPath )
            || ( !FileUtil.isFileExists( zipFileAbsPath ) ) )
        {
            logger.debug( "" );
            return false;
        }
        ZipFile zipFile = null;
        try
        {
            zipFile = new ZipFile( zipFileAbsPath );
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while ( entries.hasMoreElements() )
            {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File( outputDirAbsPath, entry.getName() );
                if ( entry.isDirectory() )
                {
                    entryDestination.mkdirs();
                }
                else
                {
                    entryDestination.getParentFile().mkdirs();
                    InputStream in = zipFile.getInputStream( entry );
                    OutputStream out = new FileOutputStream( entryDestination );
                    IOUtils.copy( in, out );
                    IOUtils.closeQuietly( in );
                    IOUtils.closeQuietly( out );
                }
            }
            return true;
        }
        catch ( IOException e )
        {
            logger.error( "Error while unzipping zip file: {}", zipFileAbsPath, e );
        }
        finally
        {
            try
            {
                if ( zipFile != null )
                {
                    zipFile.close();
                }
            }
            catch ( IOException e )
            {
                logger.warn( "Error while closing zip file.", e );
            }
        }
        return false;
    }

    /**
     * Creates the or update file.
     *
     * @param filePath the file path
     * @param fileContent the file content
     * @return true, if successful
     */
    public static boolean createOrUpdateFile( final String filePath, final String fileContent )
    {
        return FileUtil.createOrUpdateFile( filePath, fileContent, false );
    }

    /**
     * Creates the or update file.
     *
     * @param filePath the file path
     * @param fileContent the file content
     * @return true, if successful
     */
    public static boolean createOrUpdateFile( final String filePath, final String fileContent,
                                              final boolean createBackup )
    {
        String message = null;
        if ( StringUtils.isBlank( filePath ) || StringUtils.isBlank( fileContent ) )
        {
            message =
                String.format( "Either File path '%s' or File content '%s' is null or blank.", filePath, fileContent );
            logger.error( message );
            return false;
        }
        Writer writer = null;
        File file = new File( filePath );
        try
        {
            if ( !file.exists() )
            {
                if ( FileUtil.createNewFile( filePath ) )
                {
                    logger.debug( "In createOrUpdateFile, file '{}' does not exist. New file created.", filePath );
                }
                else
                {
                    logger.error( "In createOrUpdateFile, file '{}' does not exist. Failed to create file.", filePath );
                    return false;
                }
            }
            else
            {
                // file exists.
                if ( createBackup )
                {
                    String backupFileName = file.getName() + ".bak." + file.lastModified();
                    File backupFile = new File( file.getParent(), backupFileName );
                    logger.debug( "In createOrUpdateFile, saving backup file '{}' as '{}'.", filePath,
                                  backupFile.getAbsolutePath() );
                    Files.copy( file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING,
                                StandardCopyOption.COPY_ATTRIBUTES );
                }
            }
            writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filePath ), "utf-8" ) );
            writer.write( fileContent );
            return true;
        }
        catch ( IOException e )
        {
            logger.error( "In createOrUpdateFile, error while writing content to file {}.", filePath, e );
        }
        catch ( Exception e )
        {
            logger.error( "In createOrUpdateFile, error while creating or updating file {}.", filePath, e );
        }
        finally
        {
            if ( writer != null )
            {
                try
                {
                    writer.close();
                }
                catch ( IOException e )
                {
                    logger.warn( "In createOrUpdateFile, error while closing Writer.", e );
                }
            }
        }
        return false;
    }

    /**
     * Creates the new file.
     *
     * @param fileAbsPath the file abs path
     * @return true, if successful
     */
    public static boolean createNewFile( final String fileAbsPath )
    {
        if ( StringUtils.isBlank( fileAbsPath ) )
        {
            logger.error( "In createNewFile, file absolute path '{}' is either null or blank.", fileAbsPath );
            return false;
        }
        File file = new File( fileAbsPath );
        String parent = file.getParent();
        File parentFile = new File( parent );
        if ( parentFile.exists() && parentFile.isDirectory() )
        {
            return FileUtil.createNewFile( file );
        }
        else
        {
            if ( FileUtil.createDirectory( parentFile.getAbsolutePath() ) )
            {
                return FileUtil.createNewFile( file );
            }
            else
            {
                logger.error( "In createNewFile, failed to create parent directory '{}' for creating file '{}'.",
                              parentFile.getAbsolutePath(), fileAbsPath );
                return false;
            }
        }
    }

    /**
     * Creates the new file.
     *
     * @param file the file
     * @return true, if successful
     */
    private static boolean createNewFile( final File file )
    {
        boolean created = false;
        try
        {
            created = file.createNewFile();
            logger.debug( "In createNewFile, created new file '{}'.", file.getAbsoluteFile() );
        }
        catch ( IOException e )
        {
            logger.error( "In createNewFile, error while creating new file '{}'.", file.getAbsolutePath(), e );
        }
        return created;
    }

    /**
     * Reads the given file and returns its contents as String. Returns null, if the file name is null or blank, or does
     * not exist or error reading the file using UTF-8 encoding.
     *
     * @param fileName the file name
     * @return the string
     */
    public static String readFileToString( final String fileName )
    {

        // check that file name is not null or blank.
        if ( StringUtils.isBlank( fileName ) )
        {
            logger.warn( "In readFileToString, file name is either null or blank." );
            return null;
        }

        // check that file exists.
        if ( !FileUtil.isFileExists( fileName ) )
        {
            logger.warn( "In readFileToString, file '{}' does not exist.", fileName );
            return null;
        }

        try
        {
            return FileUtils.readFileToString( new File( fileName ), "UTF-8" );
        }
        catch ( IOException e )
        {
            logger.error( "In readFileToString, error while reading the file '{}'.", fileName, e );
            return null;
        }
    }

    /**
     * Checks if the given regular expression is a is valid one.
     *
     * @param pattern the pattern
     * @return true, if is valid pattern
     */
    private static boolean isValidPattern( final String pattern )
    {
        try
        {
            Pattern.compile( pattern );
            return true;
        }
        catch ( PatternSyntaxException exception )
        {
            logger.warn( "In isValidPattern, '{}' is not a valid pattern.", pattern );
            return false;
        }
    }

    /**
     * Find latest file by last modified.
     *
     * @param fileNamePattern the file name pattern
     * @param directory the directory
     * @param recursive the recursive
     * @return the string
     */
    public static String findLatestFileByLastModified( final String directory, final String fileNamePattern,
                                                       final boolean recursive )
    {

        // check that file name is not null or blank and is a valid pattern
        if ( StringUtils.isBlank( fileNamePattern ) || !FileUtil.isValidPattern( fileNamePattern ) )
        {
            logger.warn( "In findFileByLastModified, file name '{}' is either null or blank or is not a valid pattern.",
                         fileNamePattern );
            return null;
        }

        // check that directory is not null or empty (string) and exists.
        if ( !FileUtil.isDirExists( directory ) )
        {
            logger.warn( "In findLatestFileByLastModified, directory '{}' is either null or empty or does not exist.",
                         directory );
            return null;
        }

        // find files matching pattern in the directory
        File[] files = FileUtil.findFiles( directory, fileNamePattern, recursive );
        if ( files == null || files.length == 0 )
        {
            logger.warn( "In findLatestFileByLastModified, no files found matching pattern '{}' in directory '{}'.",
                         fileNamePattern, directory );
            return null;
        }

        // Sort the array to get the newest file come first
        Arrays.sort( files, LastModifiedFileComparator.LASTMODIFIED_REVERSE );
        String fileName = files[0].getAbsolutePath();
        logger.debug( "In findLatestFileByLastModified, latest file in the directory '{}' "
            + "matching the file name pattern '{}' is '{}'.", directory, fileNamePattern, fileName );
        return fileName;
    }
}