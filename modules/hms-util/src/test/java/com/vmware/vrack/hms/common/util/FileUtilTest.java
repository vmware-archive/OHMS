/* ********************************************************************************
 * FileUtilTest.java
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
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
    private String userHome;

    /**
     * Instantiates a new file util test.
     */
    public FileUtilTest()
    {
        userHome = System.getProperty( "user.home" );
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
    }

    /**
     * Test file exists with invalid file.
     */
    @Test
    public void testFileExistsWithInvalidFile()
    {
        Calendar cal = Calendar.getInstance();
        String timeInMillis = Long.toString( cal.getTimeInMillis() );
        assertFalse( FileUtil.isFileExists( FilenameUtils.concat( userHome, timeInMillis ) ) );
        assertFalse( FileUtil.isFileExists( null ) );
        assertFalse( FileUtil.isFileExists( null, timeInMillis ) );
        assertFalse( FileUtil.isFileExists( userHome, null ) );
    }

    /**
     * Test file exists with invalid file.
     */
    @Test
    public void testFileExistsWithValidFile()
    {
        Calendar cal = Calendar.getInstance();
        String timeInMillis = Long.toString( cal.getTimeInMillis() );
        String fileName = FilenameUtils.concat( userHome, timeInMillis );
        File f = new File( fileName );
        try
        {
            f.createNewFile();
            assertTrue( FileUtil.isFileExists( fileName ) );
            assertTrue( FileUtil.isFileExists( userHome, timeInMillis ) );
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
        Calendar cal = Calendar.getInstance();
        String timeInMillis = Long.toString( cal.getTimeInMillis() );
        assertFalse( FileUtil.isDirExists( null ) );
        String fileName = FilenameUtils.concat( userHome, timeInMillis );
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
        Calendar cal = Calendar.getInstance();
        String timeInMillis = Long.toString( cal.getTimeInMillis() );
        String dirName = FilenameUtils.concat( userHome, timeInMillis );
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
        Calendar cal = Calendar.getInstance();
        String timeInMillis = Long.toString( cal.getTimeInMillis() );
        assertNull( FileUtil.findFiles( userHome, null ) );
        assertNull( FileUtil.findFiles( null, timeInMillis ) );
        File[] files = FileUtil.findFiles( userHome, timeInMillis );
        assertNull( files );
        File f = new File( FilenameUtils.concat( userHome, "file." + timeInMillis ) );
        try
        {
            f.createNewFile();
            files = FileUtil.findFiles( userHome, timeInMillis );
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
        Calendar cal = Calendar.getInstance();
        String timeInMillis = Long.toString( cal.getTimeInMillis() );
        // when files of extension not found in the directory, nothing to do. In
        // that case, setFilesExecutable will return false.
        assertTrue( !FileUtil.setFilesExecutable( userHome, timeInMillis ) );
        File f = new File( FilenameUtils.concat( userHome, "file." + timeInMillis ) );
        try
        {
            f.createNewFile();
            assertTrue( FileUtil.setFilesExecutable( userHome, timeInMillis ) );
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
        String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id ne cogitari quidem potest "
            + "quale sit, ut non repugnet ipsum sibi. Ita multa dicunt, quae vix intellegam. Quamquam id quidem, "
            + "infinitum est in hac urbe; Primum quid tu dicis breve? Aliis esse maiora, illud dubium, ad id, "
            + "quod summum bonum dicitis, ecquaenam possit fieri accessio. Quamquam tu hanc copiosiorem etiam "
            + "soles dicere.\n";
        String srcFileName = "lorem_ipsum.txt";
        String srcFileAbsPath = userHome + File.separator + srcFileName;
        File srcFile = new File( srcFileAbsPath );
        if ( srcFile.exists() )
        {
            srcFile.delete();
        }
        FileOutputStream fos = new FileOutputStream( srcFile );
        fos.write( text.getBytes() );
        fos.close();
        ArchiveStreamFactory asf = new ArchiveStreamFactory();
        String loremIpsumTarFileName = userHome + File.separator + "lorem_ipsum.tar";
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
        String destDir = userHome + File.separator + "tmpdir";
        assertTrue( FileUtil.extractArchive( loremIpsumTarFileName, destDir ) );
        String destFileName = destDir + File.separator + srcFileName;
        File destFile = new File( destFileName );
        assertTrue( destFile.exists() );
        assertTrue( destFile.length() == srcFile.length() );
        // clean up
        if ( srcFile.exists() )
        {
            srcFile.delete();
        }
        File srcTarFile = new File( loremIpsumTarFileName );
        if ( srcTarFile.exists() && srcTarFile.isFile() )
        {
            srcTarFile.delete();
        }
        File dir = new File( destDir );
        if ( dir.exists() && dir.isDirectory() )
        {
            dir.delete();
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
        Calendar cal = Calendar.getInstance();
        String timeInMillis = Long.toString( cal.getTimeInMillis() );
        // negative tests, pass null values
        assertNull( FileUtil.findFiles( null, "fileName_.*.sh", true ) );
        // negative tests, pass null values
        String nullFileName = null;
        assertNull( FileUtil.findFiles( userHome, nullFileName, true ) );
        // test with invalid regex pattern
        assertNull( FileUtil.findFiles( userHome, "***", true ) );
        // first create a directory in user home directory
        String dirName = userHome + File.separator + timeInMillis;
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
}
