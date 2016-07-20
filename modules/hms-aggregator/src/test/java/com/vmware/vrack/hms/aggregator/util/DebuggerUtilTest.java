/* ********************************************************************************
 * DebuggerUtilTest.java
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.controller.InbandServiceTestImpl;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

@RunWith( PowerMockRunner.class )
@PrepareForTest( { DebuggerUtil.class, MonitoringUtil.class } )
public class DebuggerUtilTest
{
    private static Logger logger = Logger.getLogger( DebuggerUtilTest.class );

    @Before
    public void initialize()
        throws HmsException
    {
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setIbIpAddress( "1.2.3.4" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );
        nodeMap.put( "N1", node );
        // populating Nodemap before we could query its peripheral info
        InventoryLoader.getInstance().setNodeMap( nodeMap );
        // Adding our test Implementation class to provide sample data.
        InBandServiceProvider.addBoardService( node.getServiceObject(), new InbandServiceTestImpl(), true );
    }

    @Test
    public void archiveHmsDebugLogs_test()
        throws Exception
    {
        mockStatic( DebuggerUtil.class );
        when( DebuggerUtil.archiveHmsDebugLogs( anyString(), anyString(), anyString(), anyString(), anyString(),
                                                anyString(), anyString(), anyInt() ) ).thenCallRealMethod();
        when( DebuggerUtil.getAbsoluteFilePath( anyString(), anyString() ) ).thenCallRealMethod();
        when( DebuggerUtil.getCurrentTimestamp() ).thenCallRealMethod();
        when( DebuggerUtil.callLogArchiverShellScript( anyString(), anyString(), anyString(), anyString(), anyString(),
                                                       anyString(), anyString(), anyString(), anyString(), anyString(),
                                                       anyInt() ) ).thenReturn( true );
        when( DebuggerUtil.isParameterNotNullNonEmpty( anyString() ) ).thenCallRealMethod();
        when( DebuggerUtil.getEventsDataAsString( any( ServerNode.class ) ) ).thenCallRealMethod();
        when( DebuggerUtil.getHmsEventLogFilename( anyString(), anyString() ) ).thenCallRealMethod();
        when( DebuggerUtil.getHmsLogArchiveName( anyString(), anyString() ) ).thenCallRealMethod();
        when( DebuggerUtil.writeAllEventsToFile( any( ServerNode.class ), anyString(),
                                                 anyString() ) ).thenCallRealMethod();
        when( DebuggerUtil.writeHmsLogs( anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                                         anyInt(), anyString(), anyString(), anyString(),
                                         anyString() ) ).thenCallRealMethod();
        when( DebuggerUtil.writeFile( any( String.class ), any( String.class ) ) ).thenReturn( true );
        PowerMockito.mockStatic( MonitoringUtil.class );
        when( MonitoringUtil.getOnDemandEventsOOB( any( String.class ),
                                                   any( ServerComponent.class ) ) ).thenReturn( null );
        File fileMock = mock( File.class );
        whenNew( File.class ).withArguments( anyString() ).thenReturn( fileMock );
        when( fileMock.canWrite() ).thenReturn( true );
        when( fileMock.getParentFile() ).thenReturn( fileMock );
        when( fileMock.getParentFile().exists() ).thenReturn( true );
        when( fileMock.getParentFile().mkdirs() ).thenReturn( true );
        when( fileMock.createNewFile() ).thenReturn( true );
        String archiveName =
            DebuggerUtil.archiveHmsDebugLogs( "N1", "0.0.0.0", "oobusername", "hmsLogArchiveScript",
                                              "hmsLogArchiveLocation", "hmsOobLogLocation", "hmsIbLogLocation", 10000 );
        assertNotNull( archiveName );
        assertTrue( archiveName.contains( "N1_hms_logs" ) );
        assertTrue( archiveName.endsWith( ".zip" ) );
        boolean writeHmsLogsStatus =
            DebuggerUtil.writeHmsLogs( "N1", "0.0.0.0", "oobusername", "hmsLogArchiveScript", "hmsLogArchiveLocation",
                                       "1970-01-01", 1000, "eventsLogFileName", "hmsArchiveName", "hmsOobLogLocation",
                                       "hmsIbLogLocation" );
        assertTrue( writeHmsLogsStatus );
    }

    @Test
    public void parseDateFromFileName_validInput()
        throws HmsException
    {
        String currentTimeString = "N1_hms_logs_2015-03-03_00-00-00.zip";
        Date currentDate = DebuggerUtil.parseDateFromFileName( currentTimeString );
        assertNotNull( currentDate );
    }

    @Test( expected = HmsException.class )
    public void parseDateFromFileName_invalidInput()
        throws HmsException
    {
        String invalidTimeString = "N1_hms_logs_2015-03-03_00";
        Date currentDate = DebuggerUtil.parseDateFromFileName( invalidTimeString );
        assertNotNull( currentDate );
    }

    @Test( expected = HmsException.class )
    public void isFileDeleteable_invalidInput()
        throws HmsException
    {
        String invalidTimeString = "N1_hms_logs_2015-03-03_00";
        boolean status = DebuggerUtil.isFileDeleteable( invalidTimeString );
        assertNotNull( status );
    }

    @Test
    public void isFileDeleteable_validInput()
        throws HmsException
    {
        Date currentDate = new Date();
        long currMillisecs = currentDate.getTime();
        long milliSecsOneDayAgo = currMillisecs - ( 24 * 60 * 60 * 1000 );
        long milliSecsThreeDayAgo = currMillisecs - ( 3 * 24 * 60 * 60 * 1000 );
        DateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd_hh-mm-ss" );
        final String oneDayAgoDateString = formatter.format( new Date( milliSecsOneDayAgo ) );
        final String threeDayAgoDateString = formatter.format( new Date( milliSecsThreeDayAgo ) );
        String fileCreatedOneDayAgo = "N1_hms_logs_" + oneDayAgoDateString + ".zip";
        String fileCreatedThreeDayAgo = "N1_hms_logs_" + threeDayAgoDateString + ".zip";
        boolean status = DebuggerUtil.isFileDeleteable( fileCreatedOneDayAgo );
        assertFalse( status );
        boolean status2 = DebuggerUtil.isFileDeleteable( fileCreatedThreeDayAgo );
        assertTrue( status2 );
    }

    @Test( expected = HmsException.class )
    public void cleanHmsLogs_invalidInput()
        throws HmsException
    {
        assertTrue( DebuggerUtil.cleanHmsDebugLogs( null ) );
    }

    @Test( expected = HmsException.class )
    public void cleanHmsLogs_validInput_dirNotExist()
        throws HmsException
    {
        // Calling with file that doesnot exist on system
        DebuggerUtil.cleanHmsDebugLogs( "sampleDir" );
    }

    @Test
    public void cleanHmsLogs_validInput()
        throws Exception
    {
        File fileMock = mock( File.class );
        when( fileMock.canWrite() ).thenReturn( true );
        when( fileMock.getParentFile() ).thenReturn( fileMock );
        when( fileMock.isDirectory() ).thenReturn( true );
        when( fileMock.exists() ).thenReturn( true );
        File[] dummyFiles = { fileMock };
        when( fileMock.listFiles() ).thenReturn( dummyFiles );
        when( fileMock.getName() ).thenReturn( "N1_hms_logs_2015-03-03_00-00-00.zip" );
        when( fileMock.delete() ).thenReturn( true );
        whenNew( File.class ).withArguments( anyString() ).thenReturn( fileMock );
        DebuggerUtil.cleanHmsDebugLogs( "testFile" );
    }
}
