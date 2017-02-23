/* ********************************************************************************
 * FileUtilTest.java
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.vmware.vrack.hms.common.upgrade.api.ChecksumMethod;

/**
 * <code>FileUtilTest</code> is ... <br>
 *
 * @author VMware, Inc.
 */
public class FileUtilTest
{

    /** The user home. */
    private String tmpDir;

    /**
     * Instantiates a new file util test.
     */
    public FileUtilTest()
    {
    }

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp()
        throws Exception
    {
        tmpDir = FilenameUtils.concat( System.getProperty( "java.io.tmpdir" ), this.getDateTimeInMillis() );
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown()
        throws Exception
    {
        File tmpDirFile = new File( tmpDir );
        if ( tmpDirFile.exists() && tmpDirFile.isDirectory() )
        {
            tmpDirFile.delete();
        }
    }

    /**
     * Test file exists with invalid file.
     */
    @Test
    public void testFileExistsWithInvalidFile()
    {
        String timeInMillis = this.getDateTimeInMillis();
        assertFalse( FileUtil.isFileExists( FilenameUtils.concat( tmpDir, timeInMillis ) ) );
        assertFalse( FileUtil.isFileExists( null ) );
        assertFalse( FileUtil.isFileExists( null, timeInMillis ) );
        assertFalse( FileUtil.isFileExists( tmpDir, null ) );
    }

    /**
     * Test file exists with invalid file.
     */
    @Test
    public void testFileExistsWithValidFile()
    {
        String timeInMillis = this.getDateTimeInMillis();
        String fileName = FilenameUtils.concat( tmpDir, timeInMillis );
        File f = new File( fileName );
        try
        {
            f.createNewFile();
            assertTrue( FileUtil.isFileExists( fileName ) );
            assertTrue( FileUtil.isFileExists( tmpDir, timeInMillis ) );
        }
        catch ( IOException e )
        {
        }
        finally
        {
            if ( f.exists() )
            {
                f.delete();
            }
        }
    }

    /**
     * Test dir exists with invalid directory.
     */
    @Test
    public void testDirExistsWithInvalidDirectory()
    {
        assertFalse( FileUtil.isDirExists( null ) );
        String fileName = FilenameUtils.concat( tmpDir, this.getDateTimeInMillis() );
        assertFalse( FileUtil.isDirExists( fileName ) );
        // create a file and check if it is a directory
        File f = new File( fileName );
        try
        {
            f.createNewFile();
            assertFalse( FileUtil.isDirExists( fileName ) );
        }
        catch ( IOException e )
        {
        }
        finally
        {
            if ( f.exists() )
            {
                f.delete();
            }
        }
    }

    /**
     * Test dir exists with valid dir.
     */
    @Test
    public void testDirExistsWithValidDir()
    {
        String dirName = FilenameUtils.concat( tmpDir, this.getDateTimeInMillis() );
        File f = new File( dirName );
        if ( f.mkdir() )
        {
            assertTrue( FileUtil.isDirExists( dirName ) );
        }
        if ( f.exists() )
        {
            f.delete();
        }
    }

    /**
     * Test find files.
     */
    @Test
    public void testFindFiles()
    {
        String timeInMillis = this.getDateTimeInMillis();
        assertNull( FileUtil.findFiles( tmpDir, null ) );
        assertNull( FileUtil.findFiles( null, timeInMillis ) );
        File[] files = FileUtil.findFiles( tmpDir, timeInMillis );
        assertNull( files );
        File f = new File( FilenameUtils.concat( tmpDir, "file." + timeInMillis ) );
        try
        {
            f.createNewFile();
            files = FileUtil.findFiles( tmpDir, timeInMillis );
            assertNotNull( files );
            assertTrue( files.length == 1 );
        }
        catch ( IOException e )
        {
        }
        finally
        {
            if ( f.exists() )
            {
                f.delete();
            }
        }
    }

    /**
     * Test setting files executable.
     */
    @Test
    public void testSettingFilesExecutable()
    {
        String timeInMillis = this.getDateTimeInMillis();

        // when files of extension not found in the directory, nothing to do. In
        // that case, setFilesExecutable will return false.
        assertTrue( !FileUtil.setFilesExecutable( tmpDir, timeInMillis ) );
        File f = new File( FilenameUtils.concat( tmpDir, "file." + timeInMillis ) );
        try
        {
            f.createNewFile();
            assertTrue( FileUtil.setFilesExecutable( tmpDir, timeInMillis ) );
            assertTrue( f.canExecute() );
        }
        catch ( IOException e )
        {
        }
        finally
        {
            if ( f.exists() )
            {
                f.delete();
            }
        }
    }

    /**
     * Test extract tar.
     *
     * @throws Exception the exception
     */
    @Test
    public void testExtractTar()
        throws Exception
    {

        File srcDir = new File( tmpDir );
        if ( !srcDir.exists() )
        {
            srcDir.mkdirs();
        }

        String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id ne cogitari quidem potest "
            + "quale sit, ut non repugnet ipsum sibi. Ita multa dicunt, quae vix intellegam. Quamquam id quidem, "
            + "infinitum est in hac urbe; Primum quid tu dicis breve? Aliis esse maiora, illud dubium, ad id, "
            + "quod summum bonum dicitis, ecquaenam possit fieri accessio. Quamquam tu hanc copiosiorem etiam "
            + "soles dicere.\n";

        String srcFileName = "lorem_ipsum.txt";
        String srcFileAbsPath = tmpDir + File.separator + srcFileName;
        File srcFile = new File( srcFileAbsPath );

        if ( srcFile.exists() )
        {
            srcFile.delete();
        }

        FileOutputStream fos = new FileOutputStream( srcFile );
        fos.write( text.getBytes() );
        fos.close();

        ArchiveStreamFactory asf = new ArchiveStreamFactory();
        String loremIpsumTarFileName = tmpDir + File.separator + "lorem_ipsum.tar";
        File loremIpsumTar = new File( loremIpsumTarFileName );
        loremIpsumTar.createNewFile();

        FileOutputStream fos1 = new FileOutputStream( loremIpsumTar );
        ArchiveOutputStream aos = asf.createArchiveOutputStream( ArchiveStreamFactory.TAR, fos1 );

        // create the new entry
        TarArchiveEntry entry = new TarArchiveEntry( srcFileName );
        entry.setSize( srcFile.length() );
        aos.putArchiveEntry( entry );
        IOUtils.copy( new FileInputStream( srcFile ), aos );
        aos.closeArchiveEntry();
        aos.finish();
        aos.close();

        String destDir = tmpDir + File.separator + "tmpdir";
        assertTrue( FileUtil.extractArchive( loremIpsumTarFileName, destDir ) );

        String destFileName = destDir + File.separator + srcFileName;
        File destFile = new File( destFileName );
        assertTrue( destFile.exists() );
        assertTrue( destFile.length() == srcFile.length() );

        // clean up
        destFile.delete();
        File dir = new File( destDir );
        if ( dir.exists() && dir.isDirectory() )
        {
            dir.delete();
        }

        if ( srcFile.exists() )
        {
            srcFile.delete();
        }

        File srcTarFile = new File( loremIpsumTarFileName );
        if ( srcTarFile.exists() && srcTarFile.isFile() )
        {
            srcTarFile.delete();
        }

        // delete src directory
        if ( srcDir.exists() )
        {
            srcDir.delete();
        }
    }

    /**
     * Test find files with file name pattern.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFindFilesWithFileNamePattern()
        throws Exception
    {

        // negative tests, pass null values
        assertNull( FileUtil.findFiles( null, "fileName_.*.sh", true ) );

        // negative tests, pass null values
        String nullFileName = null;
        assertNull( FileUtil.findFiles( tmpDir, nullFileName, true ) );

        // test with invalid regex pattern
        assertNull( FileUtil.findFiles( tmpDir, "***", true ) );

        // first create a directory in user home directory
        String dirName = tmpDir + File.separator + this.getDateTimeInMillis();
        File dir = new File( dirName );
        dir.mkdirs();

        // create a file
        String fileName = dirName + File.separator + "file_1.sh";
        File file = new File( fileName );
        file.createNewFile();

        String subDirName = dirName + File.separator + "sub_dir";
        File subDir = new File( subDirName );
        subDir.mkdirs();

        // create a file in subDir
        String subDirFileName = subDirName + File.separator + "file_2.sh";
        File f = new File( subDirFileName );
        f.createNewFile();

        // call findFiles with fileNamePattern and recursive false. only one
        // file should be returned.
        File[] files = FileUtil.findFiles( dirName, "file_.*.sh", false );
        assertNotNull( files );
        assertTrue( files.length == 1 );

        assertTrue( files[0].getAbsolutePath().equals( fileName ) );

        // call findFiles with fileNamePattern and recursive true. both files
        // should be returned.
        files = FileUtil.findFiles( dirName, "file_.*.sh", true );
        assertNotNull( files );
        assertTrue( files.length == 2 );

        // finally remove all
        f.delete();
        subDir.delete();
        file.delete();
        dir.delete();
    }

    /**
     * Test get file checksum.
     */
    @Test
    @Ignore
    public void testGetFileChecksum()
    {

        String checksum =
            FileUtil.getFileChecksum( "/Users/sivakrishna/work/bundle-EVORACK-0.0.2-hms.tar", ChecksumMethod.SHA1 );
        assertTrue( checksum.equalsIgnoreCase( "1be59e6a535497fc39eee2119bc158341d9abfd0" ) );
    }

    @Test
    @Ignore
    public void testUnzipFile()
    {
        final String zipFile = "/Users/sivakrishna/work/hms-aggregator.war";
        final String outputDir = "/tmp";
        assertTrue( FileUtil.unzipFile( zipFile, outputDir ) );
    }

    /**
     * Gets the date time in millis.
     *
     * @return the date time in millis
     */
    private String getDateTimeInMillis()
    {
        Calendar cal = Calendar.getInstance();
        return Long.toString( cal.getTimeInMillis() );
    }

    @Test
    public void testCreateNewFile()
    {
        final String fileAbsPath = FilenameUtils.concat( tmpDir, this.getDateTimeInMillis() + ".txt" );
        boolean created = FileUtil.createNewFile( fileAbsPath );
        assertTrue( created );
        assertTrue( FileUtil.isFileExists( fileAbsPath ) );
        assertTrue( FileUtil.deleteFile( fileAbsPath ) );
    }

    @Test
    public void testCreateOrUpdateFile()
        throws IOException
    {
        final String fileAbsPath = FilenameUtils.concat( tmpDir, this.getDateTimeInMillis() + ".txt" );
        String fileContent = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id ne cogitari quidem "
            + "potest quale sit, ut non repugnet ipsum sibi. Ita multa dicunt, quae vix intellegam. Quamquam "
            + "id quidem, infinitum est in hac urbe; Primum quid tu dicis breve? Aliis esse maiora, illud dubium,"
            + " ad id, quod summum bonum dicitis, ecquaenam possit fieri accessio. Quamquam tu hanc copiosiorem"
            + " etiam soles dicere.\n";

        // test file does not exist and is created.
        assertFalse( FileUtil.isFileExists( fileAbsPath ) );
        boolean created = FileUtil.createOrUpdateFile( fileAbsPath, fileContent );
        assertTrue( created );
        assertTrue( FileUtil.isFileExists( fileAbsPath ) );

        // test that if file exists already, its been updated.
        String fileContent2 = "File updated.";
        boolean updated = FileUtil.createOrUpdateFile( fileAbsPath, fileContent2 );
        assertTrue( updated );

        List<String> lines = Files.readAllLines( Paths.get( fileAbsPath ), StandardCharsets.UTF_8 );
        String text = lines.toString();
        assertFalse( text.contains( fileContent ) );
        assertTrue( text.contains( fileContent2 ) );
        assertTrue( FileUtil.deleteFile( fileAbsPath ) );
    }

    @Test
    public void testFindLatestFileByLastModified()
        throws IOException
    {

        // negative tests, pass null values
        assertNull( FileUtil.findFiles( null, "fileName_.*.sh", true ) );

        // negative tests, pass null values
        String nullFileName = null;
        assertNull( FileUtil.findFiles( tmpDir, nullFileName, true ) );

        // test with invalid regex pattern
        assertNull( FileUtil.findFiles( tmpDir, "***", true ) );

        // first create a directory in user home directory
        String dirName = tmpDir + File.separator + this.getDateTimeInMillis();
        File dir = new File( dirName );
        dir.mkdirs();

        // create a file
        String fileName = dirName + File.separator + "file_1.sh";
        File file = new File( fileName );
        file.createNewFile();

        // delay second file creation so that file created in the sub directory
        // is the latest file created one.
        HmsGenericUtil.sleepThread( false, 1000 );

        String subDirName = dirName + File.separator + "sub_dir";
        File subDir = new File( subDirName );
        subDir.mkdirs();

        // create a file in subDir
        String subDirFileName = subDirName + File.separator + "file_2.sh";
        File f = new File( subDirFileName );
        f.createNewFile();

        // call findLatestFileByLastModified with fileNamePattern and recursive
        // false. only one file should be returned.
        String latestFile = FileUtil.findLatestFileByLastModified( dirName, "file_.*.sh", false );
        assertNotNull( latestFile );
        assertTrue( StringUtils.equals( latestFile, fileName ) );

        // call findLatestFileByLastModified with recursive true. File name
        // returned must be the file created in the sub directory.
        latestFile = FileUtil.findLatestFileByLastModified( dirName, "file_.*.sh", true );
        assertNotNull( latestFile );
        assertTrue( StringUtils.equals( latestFile, subDirFileName ) );
    }
}
