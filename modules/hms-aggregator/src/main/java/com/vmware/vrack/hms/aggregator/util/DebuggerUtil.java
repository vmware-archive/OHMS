/* ********************************************************************************
 * DebuggerUtil.java
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
package com.vmware.vrack.hms.aggregator.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

/**
 * Utility class for HMS IB, OOB and Events log extraction and archiving
 *
 * @author VMware Inc.
 */
public class DebuggerUtil
{
    private static Logger logger = Logger.getLogger( DebuggerUtil.class );

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd_hh-mm-ss";

    /**
     * Regular expression for HMS log archive name. ex : N1_hms_logs_2015-01-24_04-10-26.zip
     */
    public static final String HMS_ARCHIVE_REGEX =
        "(.*)_hms_logs_((19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01]))_(([0-9]|0[0-9]|1[0-9]|2[0-3])-[0-5][0-9]-[0-5][0-9]).zip";

    /**
     * Extract timestamp from debug archive ex: N1_hms_logs_2015-01-24_04-10-26.zip will give timestamp as
     * 2015-01-24_04-10-26
     */
    public static final String HMS_TIMESTAMP_EXTRACTION_REGEX =
        "_hms_logs_(((19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01]))_(([0-9]|0[0-9]|1[0-9]|2[0-3])-[0-5][0-9]-[0-5][0-9]))(.*)";

    public static final long logClearDurationInHours = 48;

    /**
     * Method to get Events log for particular node, and collect hms oob as well as hms ib logs, and, then create an log
     * archive coantaining all 3 files.
     *
     * @param host_id
     * @param hmsIpAddr
     * @param hmsOobUsername
     * @param hmsLogArchiverScript
     * @param hmsLogArchiveLocation
     * @param hmsOobLogLocation
     * @param hmsIbLogLocation
     * @param noOfLines
     * @return
     * @throws IllegalArgumentException
     */
    public static String archiveHmsDebugLogs( final String host_id, final String hmsIpAddr, final String hmsOobUsername,
                                              final String hmsLogArchiverScript, final String hmsLogArchiveLocation,
                                              final String hmsOobLogLocation, final String hmsIbLogLocation,
                                              final int noOfLines )
                                                  throws IllegalArgumentException
    {
        final String timestamp = DebuggerUtil.getCurrentTimestamp();
        // Final name for the events log file
        final String eventsLogFileName = DebuggerUtil.getHmsEventLogFilename( host_id, timestamp );
        // name of hms archive file
        final String hmsArchiveName = DebuggerUtil.getHmsLogArchiveName( host_id, timestamp );
        if ( isParameterNotNullNonEmpty( host_id ) && isParameterNotNullNonEmpty( hmsIpAddr )
            && isParameterNotNullNonEmpty( hmsOobUsername ) && isParameterNotNullNonEmpty( hmsLogArchiverScript )
            && isParameterNotNullNonEmpty( hmsLogArchiveLocation ) && isParameterNotNullNonEmpty( timestamp )
            && isParameterNotNullNonEmpty( hmsOobLogLocation ) && isParameterNotNullNonEmpty( eventsLogFileName )
            && isParameterNotNullNonEmpty( hmsArchiveName ) && isParameterNotNullNonEmpty( hmsIbLogLocation )
            && noOfLines > 0 )
        {
            ExecutorService service = Executors.newFixedThreadPool( 1 );
            ;
            service.execute( new Runnable()
            {
                public void run()
                {
                    try
                    {
                        writeHmsLogs( host_id, hmsIpAddr, hmsOobUsername, hmsLogArchiverScript, hmsLogArchiveLocation,
                                      timestamp, noOfLines, eventsLogFileName, hmsArchiveName, hmsOobLogLocation,
                                      hmsIbLogLocation );
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Unable to collect debug logs for node: " + host_id, e );
                    }
                }
            } );
            // Make sure Executor will NOT take any new tasks further and will close itself once tasks are completed
            service.shutdown();
            return getAbsoluteFilePath( hmsLogArchiveLocation, hmsArchiveName );
        }
        else
        {
            String err = "Unable to continue with creating hms debug logs archive for node [ " + host_id
                + " ] because one or more parameters have been missing or null";
            String debugString =
                String.format( "host_id [ %s ], hmsIpAddr [ %s ], hmsOobUsername [ %s ], hmsLogArchiverScript [ %s ], hmsLogArchiveLocation [ %s ], timestamp [ %s ], hmsOobLogLocation [ %s ], hmsIbLogLocation [ %s ], noOfLines[ %s ]",
                               host_id, hmsIpAddr, hmsOobUsername, hmsLogArchiverScript, hmsLogArchiveLocation,
                               timestamp, hmsOobLogLocation, hmsIbLogLocation, noOfLines );
            logger.error( err + debugString );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Retrieves and writes all events into a log file at particular location
     *
     * @param node
     * @param fileName
     * @param logDirectoryPath
     * @return
     * @throws HmsException
     */
    public static boolean writeAllEventsToFile( ServerNode node, String fileName, String logDirectoryPath )
        throws HmsException
    {
        if ( node != null && fileName != null && logDirectoryPath != null )
        {
            String hostId = node.getNodeID();
            String eventsAsString;
            try
            {
                eventsAsString = getEventsDataAsString( node );
            }
            catch ( HmsException e )
            {
                String err = "Unable to get events data as string for node [ " + hostId + " ]";
                logger.error( err, e );
                // If there is some problem with the event data write the full stack trace in the file
                eventsAsString = err + ":\n" + e;
            }
            try
            {
                String absoluteEventsLogFilePath = getAbsoluteFilePath( logDirectoryPath, fileName );
                writeFile( absoluteEventsLogFilePath, eventsAsString );
                return true;
            }
            catch ( Exception e )
            {
                String err = "Unable to write events Log file for node [ " + node.getNodeID() + " ] at location ["
                    + logDirectoryPath + " ]";
                logger.error( err, e );
                throw new HmsException( err, e );
            }
        }
        else
        {
            String err = "hostId  [ " + node != null ? node.getNodeID()
                            : null + " ], target filename [ " + fileName + " ] , logDirectoryPath [ " + logDirectoryPath
                                + " ] for events log cannot be null";
            logger.error( err );
            throw new HmsException( err );
        }
    }

    /**
     * Write events data to log file at particular location
     *
     * @param eventsLogFilePath
     * @param content
     * @return
     * @throws HmsException
     */
    public static boolean writeFile( String eventsLogFilePath, String content )
        throws HmsException
    {
        if ( eventsLogFilePath != null && content != null )
        {
            try
            {
                File file = new File( eventsLogFilePath );
                // Write intermediate directories, if required
                if ( file.getParentFile() != null && ( file.getParentFile().exists() || file.getParentFile().mkdirs() )
                    && file.createNewFile() )
                {
                    logger.debug( "Events log file created at: " + eventsLogFilePath );
                    Writer writer = null;
                    try
                    {
                        writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( eventsLogFilePath ),
                                                                             "utf-8" ) );
                        writer.write( content );
                    }
                    catch ( IOException ex )
                    {
                        String err = "Unable to write Events log file at " + eventsLogFilePath;
                        logger.error( err, ex );
                        throw new HmsException( err, ex );
                    }
                    finally
                    {
                        try
                        {
                            writer.close();
                        }
                        catch ( Exception ex )
                        {
                            logger.error( "Unable to close writer.", ex );
                        }
                    }
                    return true;
                }
                else
                {
                    String err = "Failed to write events log file at " + eventsLogFilePath;
                    logger.error( err );
                    throw new HmsException( err );
                }
            }
            catch ( IOException e )
            {
                String err = "Unable to create and write events log data at " + eventsLogFilePath;
                logger.error( err, e );
                throw new HmsException( err );
            }
        }
        else
        {
            String err = "Failed to create events log file at " + eventsLogFilePath + "and content \n" + content + "\n";
            logger.error( err );
            throw new HmsException( err );
        }
    }

    /**
     * Generates absolute filesystem path for a file(handling extra slashes)
     *
     * @param directoryLocation
     * @param filename
     * @return
     */
    public static String getAbsoluteFilePath( String directoryLocation, String filename )
    {
        if ( directoryLocation != null && !"".equals( directoryLocation ) && filename != null
            && !"".equals( filename ) )
        {
            if ( !directoryLocation.endsWith( "/" ) )
            {
                directoryLocation = directoryLocation + "/";
            }
            return directoryLocation + filename;
        }
        else
        {
            return null;
        }
    }

    /**
     * Retrieves event list and serializes that to string
     *
     * @param node
     * @return
     * @throws HmsException
     */
    public static String getEventsDataAsString( ServerNode node )
        throws HmsException
    {
        IComponentEventInfoProvider sensorInfoProvider =
            InBandServiceProvider.getBoardService( node.getServiceObject() );
        MonitoringTaskResponse response = new MonitoringTaskResponse( node, sensorInfoProvider );
        try
        {
            List<Event> events = new ArrayList<Event>();
            response.setEvents( events );
            for ( ServerComponent component : ServerComponent.values() )
            {
                try
                {
                    logger.debug( "Iterating over Components for node [ " + node.getNodeID()
                        + " ]. Looking for possible events for component:" + component );
                    HmsLocalMonitorTask monitor = new HmsLocalMonitorTask( response, component );
                    monitor.executeTask();
                }
                catch ( Exception e )
                {
                    logger.error( "Error getting sensor information for component [ " + component + " ], node id [ "
                        + node.getNodeID() + " ]", e );
                }
            }
            String eventsAsString = null;
            try
            {
                if ( response.getEvents() != null )
                {
                    ObjectMapper mapper = new ObjectMapper();
                    eventsAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString( response.getEvents() );
                    return eventsAsString;
                }
                else
                {
                    String err = "Unable to get Events data for node [ " + node.getNodeID() + " ]";
                    logger.error( err );
                    throw new HmsException( err );
                }
            }
            catch ( Exception e )
            {
                String err = "Unable to get Events for Logging purpose from node [ " + node.getNodeID() + " ]";
                logger.error( err );
                throw e;
            }
        }
        catch ( Exception e )
        {
            String err = "Error getting sensor information for node id: " + node.getNodeID();
            logger.error( err, e );
            throw new HmsException( err, e );
        }
    }

    /**
     * Method that retrieves all event logs, writes them into one file and then executes hms log archiver shell script.
     *
     * @param host_id
     * @param hmsIpAddr
     * @param hmsOobUsername
     * @param hmsLogArchiverScript
     * @param hmsLogArchiveLocation
     * @param timestamp
     * @param noOfLines
     * @param eventsLogFileName
     * @param hmsArchiveName
     * @param hmsOobLogLocation
     * @param hmsIbLogLocation
     * @return
     * @throws HmsException
     */
    public static boolean writeHmsLogs( String host_id, String hmsIpAddr, String hmsOobUsername,
                                        String hmsLogArchiverScript, String hmsLogArchiveLocation, String timestamp,
                                        int noOfLines, String eventsLogFileName, String hmsArchiveName,
                                        String hmsOobLogLocation, String hmsIbLogLocation )
                                            throws HmsException
    {
        if ( isParameterNotNullNonEmpty( host_id ) && isParameterNotNullNonEmpty( hmsIpAddr )
            && isParameterNotNullNonEmpty( hmsOobUsername ) && isParameterNotNullNonEmpty( hmsLogArchiverScript )
            && isParameterNotNullNonEmpty( hmsLogArchiveLocation ) && isParameterNotNullNonEmpty( timestamp )
            && isParameterNotNullNonEmpty( eventsLogFileName ) && isParameterNotNullNonEmpty( hmsArchiveName )
            && isParameterNotNullNonEmpty( hmsOobLogLocation ) && isParameterNotNullNonEmpty( hmsIbLogLocation )
            && noOfLines > 0 )
        {
            try
            {
                // Checking for the write permissions on the directory, if the directory is write protected, return
                // immediately informing the same.
                File logDir = new File( hmsLogArchiveLocation );
                if ( !logDir.canWrite() )
                {
                    String error = "Log directory [ " + hmsLogArchiveLocation
                        + " ] is write protected. Please grant write permissions or change the directory to some other diectory.";
                    logger.error( error );
                    throw new HmsException( error );
                }
                ServerNode node = (ServerNode) InventoryLoader.getInstance().getNodeMap().get( host_id );
                DebuggerUtil.writeAllEventsToFile( node, eventsLogFileName, hmsLogArchiveLocation );
            }
            catch ( Exception e )
            {
                String err = "Unable to write Events Log for node [ " + host_id + " ] to location [ "
                    + hmsLogArchiveLocation + " ] with filename [ " + eventsLogFileName + " ]";
                logger.error( err );
            }
            logger.debug( String.format( "Using hms log archiver script [ %s ]", hmsLogArchiverScript ) );
            // Now call the Log archiver shell script
            return callLogArchiverShellScript( hmsLogArchiverScript, hmsIpAddr, hmsOobUsername, hmsLogArchiveLocation,
                                               eventsLogFileName, hmsArchiveName, host_id, timestamp, hmsOobLogLocation,
                                               hmsIbLogLocation, noOfLines );
        }
        else
        {
            String err = String.format(
                                        "Cannot continue with hms debug log archive creation. Some parameters are null or empty. "
                                            + "host_id [ %s ], hmsIpAddr [ %s ], hmsOobUsername [ %s ], hmsLogArchiverScript [ %s ], hmsLogArchiveLocation [ %s ],"
                                            + " timestamp [ %s ], eventsLogFileName [ %s ], hmsArchiveName [ %s ], hmsOobLogLocation [ %s ], hmsIbLogLocation [ %s ], noOfLines",
                                        host_id, hmsIpAddr, hmsOobUsername, hmsLogArchiverScript, hmsLogArchiveLocation,
                                        timestamp, eventsLogFileName, hmsArchiveName, hmsOobLogLocation,
                                        hmsIbLogLocation, noOfLines );
            logger.error( err );
            throw new HmsException( err );
        }
    }

    public static boolean callLogArchiverShellScript( String hmsLogArchiverScript, String hmsIpAddr,
                                                      String hmsOobUsername, String hmsLogArchiveLocation,
                                                      String eventsLogFileName, String hmsArchiveName, String host_id,
                                                      String timestamp, String hmsOobLogLocation,
                                                      String hmsIbLogLocation, int noOfLines )
                                                          throws HmsException
    {
        if ( hmsLogArchiverScript != null )
        {
            File hmsLogArchiverScriptFile = new File( hmsLogArchiverScript );
            // Check if the file physically exist on disk
            if ( hmsLogArchiverScriptFile.exists() )
            {
                logger.debug( String.format( "Using Credentials for Hms OOB: host [ %s ], Username [ %s ]", hmsIpAddr,
                                             hmsOobUsername ) );
                Process process = null;
                try
                {
                    process = Runtime.getRuntime().exec( String.format( Constants.GRANT_EXECUTE_RIGHTS,
                                                                        hmsLogArchiverScript ) );
                    process.waitFor();
                    int exitValue = process.exitValue();
                    if ( exitValue == 0 )
                    {
                        logger.debug( "Granted Execute rights to Log archiver Script: " + hmsLogArchiverScript );
                    }
                    else
                    {
                        logger.debug( "Cannot Grant Execute rights to Log archiver Script: " + hmsLogArchiverScript );
                    }
                    String hmsEventsLogAbsolutePath =
                        DebuggerUtil.getAbsoluteFilePath( hmsLogArchiveLocation, eventsLogFileName );
                    String hmsArchiveAbsolutePath =
                        DebuggerUtil.getAbsoluteFilePath( hmsLogArchiveLocation, hmsArchiveName );
                    if ( !isParameterNotNullNonEmpty( hmsEventsLogAbsolutePath )
                        && !isParameterNotNullNonEmpty( hmsArchiveAbsolutePath ) )
                    {
                        String err =
                            "Cannot calculate HMS archive absolute path and HMS events log absolute path for node "
                                + host_id + ". Provided Hms archive location [ " + hmsLogArchiveLocation + " ], "
                                + "Hms events log file [ " + eventsLogFileName + " ], Hms archive name [ "
                                + hmsArchiveName + " ]";
                        logger.debug( err );
                    }
                    String hmsLogArchiverScriptWithArguments =
                        String.format( "%s %s %s %s %s %s %s %s %s %s", hmsLogArchiverScript, hmsIpAddr, hmsOobUsername,
                                       hmsLogArchiveLocation, hmsArchiveName, timestamp, hmsOobLogLocation,
                                       hmsIbLogLocation, hmsEventsLogAbsolutePath, noOfLines );
                    logger.debug( "Final command for log archiver: " + hmsLogArchiverScriptWithArguments );
                    Process logArchiveProcess = Runtime.getRuntime().exec( hmsLogArchiverScriptWithArguments );
                    // Redirect Console output to logger
                    String line;
                    BufferedReader error =
                        new BufferedReader( new InputStreamReader( logArchiveProcess.getErrorStream() ) );
                    while ( ( line = error.readLine() ) != null )
                    {
                        logger.debug( line );
                    }
                    error.close();
                    BufferedReader input =
                        new BufferedReader( new InputStreamReader( logArchiveProcess.getInputStream() ) );
                    while ( ( line = input.readLine() ) != null )
                    {
                        logger.debug( line );
                    }
                    input.close();
                    OutputStream outputStream = logArchiveProcess.getOutputStream();
                    PrintStream printStream = new PrintStream( outputStream );
                    printStream.println();
                    printStream.flush();
                    printStream.close();
                    int logArchiveProcessExitValue = logArchiveProcess.exitValue();
                    logger.debug( "Hms Log archive Process Exit Value: " + logArchiveProcessExitValue );
                    File archive = new File( hmsArchiveAbsolutePath );
                    if ( logArchiveProcessExitValue == 0 && archive.exists() )
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                catch ( Exception e )
                {
                    String err = "Error getting logs and archiving them for node [ " + host_id + " ]";
                    logger.error( err, e );
                    throw new HmsException( err );
                }
            }
            else
            {
                String err = "Hms Log archiver script doesnot exist at " + hmsLogArchiverScript;
                logger.error( err );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", err );
            }
        }
        else
        {
            String err =
                "Cannot continue with log archiving as hms log script location is undefined in the properties.";
            logger.error( err );
            throw new HmsException( err );
        }
    }

    /**
     * Utility function to check, if any string is null or empty.
     *
     * @param param
     * @return
     */
    public static boolean isParameterNotNullNonEmpty( String param )
    {
        if ( param != null && !"".equals( param.trim() ) )
        {
            return true;
        }
        return false;
    }

    /**
     * Returns you human readable current time as string
     *
     * @return
     */
    public static String getCurrentTimestamp()
    {
        Date date = new Date();
        DateFormat formatter = new SimpleDateFormat( TIMESTAMP_FORMAT );
        final String timestamp = formatter.format( date );
        return timestamp;
    }

    /**
     * Generates HMS Event log filename
     *
     * @param host_id
     * @param timestamp
     * @return
     */
    public static String getHmsEventLogFilename( String host_id, String timestamp )
    {
        if ( isParameterNotNullNonEmpty( host_id ) && isParameterNotNullNonEmpty( timestamp ) )
        {
            return ( host_id + "_" + "hms_events_log_" + timestamp + ".log" );
        }
        return null;
    }

    /**
     * Generates HMS debug log archive name
     *
     * @param host_id
     * @param timestamp
     * @return
     */
    public static String getHmsLogArchiveName( String host_id, String timestamp )
    {
        if ( isParameterNotNullNonEmpty( host_id ) && isParameterNotNullNonEmpty( timestamp ) )
        {
            return ( host_id + "_hms_logs_" + timestamp + ".zip" );
        }
        return null;
    }

    /**
     * Clean HMS debug logs from the filesystem
     *
     * @param logArchiveDirectoryName
     * @return
     * @throws HmsException
     */
    public static boolean cleanHmsDebugLogs( String logArchiveDirectoryName )
        throws HmsException
    {
        logger.debug( "In cleanHmsDebugLogs(), trying to delete logs in this directory [ " + logArchiveDirectoryName
            + " ]" );
        if ( logArchiveDirectoryName != null )
        {
            File logArchiveName = new File( logArchiveDirectoryName );
            if ( logArchiveName.exists() && logArchiveName.isDirectory() && logArchiveName.canWrite() )
            {
                for ( File archive : logArchiveName.listFiles() )
                {
                    if ( archive.getName().matches( HMS_ARCHIVE_REGEX ) )
                    {
                        try
                        {
                            if ( isFileDeleteable( archive.getName() ) )
                            {
                                archive.delete();
                            }
                        }
                        catch ( Exception e )
                        {
                            logger.error( "Unable to delete log archive [ " + archive.getName() + " ] from filesystem.",
                                          e );
                        }
                    }
                }
                return true;
            }
            else
            {
                String err = "[ " + logArchiveDirectoryName + " ] doesnot exist or is not a directory.";
                logger.error( err );
                throw new HmsException( err );
            }
        }
        else
        {
            String err = "Absolute path for log directory [ " + logArchiveDirectoryName + " ] cannot be null";
            logger.error( err );
            throw new HmsException( err );
        }
    }

    /**
     * Checks if the particular hms debug log file was created within 2 days time. If yes, then it can be deleted.
     *
     * @param logArchiveName
     * @return
     * @throws HmsException
     */
    public static boolean isFileDeleteable( String logArchiveName )
        throws HmsException
    {
        if ( isParameterNotNullNonEmpty( logArchiveName ) && logArchiveName.matches( HMS_ARCHIVE_REGEX ) )
        {
            try
            {
                Date parsedDate = parseDateFromFileName( logArchiveName );
                Date currentDate = new Date();
                long duration = currentDate.getTime() - parsedDate.getTime();
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes( duration );
                if ( ( diffInMinutes - ( logClearDurationInHours * 60 ) ) > 0 )
                {
                    return true;
                }
            }
            catch ( HmsException e )
            {
                String err = "Exception occured when trying to get file duration since it was created for  file [ "
                    + logArchiveName + " ]";
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            return false;
        }
        else
        {
            String err = "Cannot get Date from log Archive name [ " + logArchiveName
                + " ], because either it is null or empty. or it doesnot matches the regular expression [ "
                + HMS_ARCHIVE_REGEX + " ]";
            logger.error( err );
            throw new HmsException( err );
        }
    }

    /**
     * parses date object from hms dbug log filename
     *
     * @param logArchiveName
     * @return
     * @throws HmsException
     */
    public static Date parseDateFromFileName( String logArchiveName )
        throws HmsException
    {
        if ( isParameterNotNullNonEmpty( logArchiveName ) && logArchiveName.matches( HMS_ARCHIVE_REGEX ) )
        {
            // The string has been checked with regex expression, so it is safe to use substring.
            // String fileTimeStamp = logArchiveName.substring(logArchiveName.length()-23, logArchiveName.length()-4);
            String fileTimeStamp = null;
            Pattern p = Pattern.compile( HMS_TIMESTAMP_EXTRACTION_REGEX );
            Matcher m = p.matcher( logArchiveName );
            if ( m.find() )
            {
                logger.debug( "Extracted timestamp for log archive [ " + logArchiveName + " ] is [ " + m.group( 1 )
                    + " ]" );
                fileTimeStamp = m.group( 1 );
            }
            DateFormat df = new SimpleDateFormat( TIMESTAMP_FORMAT );
            Date parsedDate;
            try
            {
                parsedDate = df.parse( fileTimeStamp );
                logger.debug( "Parsed date :" + parsedDate );
                return parsedDate;
            }
            catch ( ParseException e )
            {
                String err = "Unable to parse date from the filename [ " + logArchiveName + " ]";
                logger.error( err );
                throw new HmsException( err, e );
            }
        }
        else
        {
            String err = "Cannot get Date from log Archive name [ " + logArchiveName
                + " ], because either it is null or empty. or it doesnot matches the regular expression [ "
                + HMS_ARCHIVE_REGEX + " ]";
            logger.error( err );
            throw new HmsException( err );
        }
    }
}
