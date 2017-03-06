/* ********************************************************************************
 * HMSDebuggerComponentTest.java
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
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.aggregator.HostDataAggregator;
import com.vmware.vrack.hms.aggregator.switches.HmsSwitchManager;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.util.FileUtil;
import com.vmware.vrack.hms.controller.InbandServiceTestImpl;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

@Ignore
@RunWith( PowerMockRunner.class )
@PrepareForTest( { HMSDebuggerComponent.class, MonitoringUtil.class, FileUtil.class, FileUtils.class } )
@PowerMockIgnore( { "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl",
    "com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager" } )
public class HMSDebuggerComponentTest
{
    private static Logger logger = Logger.getLogger( HMSDebuggerComponentTest.class );

    HMSDebuggerComponent debuggerUtil = mock( HMSDebuggerComponent.class );

    @Mock
    HmsSwitchManager hmsSwitchManager;

    @Mock
    HostDataAggregator hostDataAggregator;

    @Before
    public void initialize()
        throws HmsException, Exception
    {
        NBSwitchInfo nbSwitchInfo = new NBSwitchInfo();
        nbSwitchInfo.setSwitchId( "N1" );

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setNodeId( "N1" );

        when( hostDataAggregator.getOOBLogs() ).thenReturn( null );
        when( hostDataAggregator.getServerInfo( anyString() ) ).thenReturn( serverInfo );

        PowerMockito.mockStatic( FileUtil.class );
        when( FileUtil.createDirectory( anyString() ) ).thenReturn( true );

        PowerMockito.mockStatic( FileUtils.class );
        // when(FileUtils.copyFileToDirectory(any(File.class), any(File.class))).doNothing();
        when( hmsSwitchManager.getSwitchInfo( anyString() ) ).thenReturn( nbSwitchInfo );
        Method method = HMSDebuggerComponent.class.getMethod( "setHmsSwitchManager", HmsSwitchManager.class );
        when( debuggerUtil, method ).withArguments( any( HmsSwitchManager.class ) ).thenCallRealMethod();

        when( debuggerUtil.archiveHmsDebugLogs( anyString(), anyString(), anyString(), anyString(), anyString(),
                                                anyString(), anyString(), any( Integer.class ),
                                                any( EventComponent.class ) ) ).thenCallRealMethod();
        when( debuggerUtil.getAbsoluteFilePath( anyString(), anyString() ) ).thenCallRealMethod();
        when( debuggerUtil.getCurrentTimestamp() ).thenCallRealMethod();
        when( debuggerUtil.parseDateFromFileName( anyString() ) ).thenCallRealMethod();
        when( debuggerUtil.isFileDeleteable( anyString() ) ).thenCallRealMethod();
        when( debuggerUtil.cleanHmsDebugLogs( anyString() ) ).thenCallRealMethod();

        when( debuggerUtil.callServerLogArchiverShellScript( anyString(), anyString(), anyString(), anyString(),
                                                             anyString(), anyString(), anyString(), anyString(),
                                                             anyString(), any( Integer.class ),
                                                             anyString() ) ).thenReturn( true );
        when( debuggerUtil.callLogArchiverShellScript( anyString(), anyString(), anyString(), anyString(), anyString(),
                                                       anyString(), anyString(), anyString(), anyString(),
                                                       any( Integer.class ) ) ).thenReturn( true );
        when( debuggerUtil.getHmsEventsLogFilename( anyString(), anyString() ) ).thenCallRealMethod();
        when( debuggerUtil.isParameterNotNullNonEmpty( anyString() ) ).thenCallRealMethod();
        when( debuggerUtil.getEventsDataAsString( any( ServerNode.class ) ) ).thenCallRealMethod();
        when( debuggerUtil.getServerInfoOrNBSwitchInfoLogFilename( anyString(), anyString(),
                                                                   Matchers.any( EventComponent.class ) ) ).thenCallRealMethod();
        when( debuggerUtil.getHmsLogArchiveName( anyString(), anyString() ) ).thenCallRealMethod();
        when( debuggerUtil.writeAllEventsToFile( any( ServerNode.class ), anyString(),
                                                 anyString() ) ).thenCallRealMethod();
        when( debuggerUtil.writeServerInfoToFile( any( ServerInfo.class ), anyString(),
                                                  anyString() ) ).thenCallRealMethod();
        when( debuggerUtil.writeHmsLogs( anyString(), anyString(), anyString(), anyString(), any( Integer.class ),
                                         anyString(), anyString(), anyString(), anyString(),
                                         any( EventComponent.class ) ) ).thenCallRealMethod();
        when( debuggerUtil.writeFile( any( String.class ), any( String.class ) ) ).thenReturn( true );

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

        String archiveName = debuggerUtil.archiveHmsDebugLogs( "N1", "0.0.0.0", "oobusername", "hmsLogArchiveScript",
                                                               "hmsLogArchiveLocation", "hmsOobLogLocation",
                                                               "hmsIbLogLocation", 10000, EventComponent.SERVER );
        assertNotNull( archiveName );
        assertTrue( archiveName.contains( "N1_hms_logs" ) );
        assertTrue( archiveName.endsWith( ".zip" ) );

        boolean writeHmsLogsStatus =
            debuggerUtil.writeHmsLogs( "N1", "hmsLogArchiveScript", "hmsLogArchiveLocation", "1970-01-01", 1000,
                                       "eventsLogFileName", "hmsArchiveName", "hmsOobLogLocation", "hmsIbLogLocation",
                                       EventComponent.SERVER );
        assertTrue( writeHmsLogsStatus );
    }

    @Test
    public void archiveHmsDebugLogsSwitch_test()
        throws Exception
    {

        String archiveName = debuggerUtil.archiveHmsDebugLogs( "N1", "0.0.0.0", "oobusername", "hmsLogArchiveScript",
                                                               "hmsLogArchiveLocation", "hmsOobLogLocation",
                                                               "hmsIbLogLocation", 10000, EventComponent.SWITCH );
        assertNotNull( archiveName );
        assertTrue( archiveName.contains( "N1_hms_logs" ) );
        assertTrue( archiveName.endsWith( ".zip" ) );

        boolean writeHmsLogsStatus =
            debuggerUtil.writeHmsLogs( "N1", "hmsLogArchiveScript", "hmsLogArchiveLocation", "1970-01-01", 1000,
                                       "eventsLogFileName", "hmsArchiveName", "hmsOobLogLocation", "hmsIbLogLocation",
                                       EventComponent.SWITCH );
        assertTrue( writeHmsLogsStatus );
    }

    @Test
    public void parseDateFromFileName_validInput()
        throws HmsException
    {
        String currentTimeString = "N1_hms_logs_2015-03-03_00-00-00.zip";
        Date currentDate = debuggerUtil.parseDateFromFileName( currentTimeString );
        assertNotNull( currentDate );
    }

    @Test( expected = HmsException.class )
    public void parseDateFromFileName_invalidInput()
        throws HmsException
    {
        String invalidTimeString = "N1_hms_logs_2015-03-03_00";

        Date currentDate = debuggerUtil.parseDateFromFileName( invalidTimeString );
        assertNotNull( currentDate );
    }

    @Test( expected = HmsException.class )
    public void isFileDeleteable_invalidInput()
        throws HmsException
    {
        String invalidTimeString = "N1_hms_logs_2015-03-03_00";

        boolean status = debuggerUtil.isFileDeleteable( invalidTimeString );
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

        boolean status = debuggerUtil.isFileDeleteable( fileCreatedOneDayAgo );
        assertFalse( status );

        boolean status2 = debuggerUtil.isFileDeleteable( fileCreatedThreeDayAgo );
        assertTrue( status2 );
    }

    @Test( expected = HmsException.class )
    public void cleanHmsLogs_invalidInput()
        throws HmsException
    {
        assertTrue( debuggerUtil.cleanHmsDebugLogs( null ) );
    }

    @Test( expected = HmsException.class )
    public void cleanHmsLogs_validInput_dirNotExist()
        throws HmsException
    {
        // Calling with file that doesnot exist on system
        debuggerUtil.cleanHmsDebugLogs( "sampleDir" );
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

        debuggerUtil.cleanHmsDebugLogs( "testFile" );
    }
}
