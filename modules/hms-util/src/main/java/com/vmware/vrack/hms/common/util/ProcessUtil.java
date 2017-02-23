/* ********************************************************************************
 * ProcessUtil.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>ProcessUtil</code><br>
 * .
 *
 * @author VMware, Inc.
 */
public class ProcessUtil
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( ProcessUtil.class );

    /**
     * Instantiates a new process util.
     */
    private ProcessUtil()
    {
        throw new AssertionError();
    }

    /**
     * Executes the given command and waits till its execution is terminated and then return command exit value.
     *
     * @param cmdWithArgs the cmd with args as list
     * @return the command exit value
     */
    @Deprecated
    public static int getCommandExitValue( final List<String> cmdWithArgs )
    {

        if ( cmdWithArgs != null )
        {

            int cmdsLength = cmdWithArgs.size();
            if ( cmdsLength > 0 )
            {

                String cmd = ProcessUtil.getCommand( cmdWithArgs );
                cmd = maskPassword( cmd );
                final Process process = ProcessUtil.execute( cmdWithArgs );
                if ( process != null )
                {
                    int processId = ProcessUtil.getProcessId( process );
                    if ( processId != -1 )
                    {
                        logger.debug( "Command '{}' running as Process: '{}'.", cmd, processId );
                    }
                    try
                    {

                        logger.info( "Waiting for the command '{}' execution.", cmd );
                        int status = process.waitFor();
                        extractOutput( process );
                        extractException( process );

                        return status;
                    }
                    catch ( InterruptedException e )
                    {
                        logger.error( "Error while waiting for the command '{}' execution, exception: {}", cmd, e );
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Executes the given command and waits till its execution is terminated and then return command exit value.
     *
     * @param cmdWithArgs the cmd with args as list
     * @return the command exit value
     */
    public static int executeCommand( final List<String> cmdWithArgs )
    {

        if ( cmdWithArgs != null )
        {

            int cmdsLength = cmdWithArgs.size();
            if ( cmdsLength > 0 )
            {

                String cmd = ProcessUtil.getCommand( cmdWithArgs );
                cmd = maskPassword( cmd );
                final Process process = ProcessUtil.execute( cmdWithArgs );
                if ( process != null )
                {
                    int processId = ProcessUtil.getProcessId( process );
                    if ( processId != -1 )
                    {
                        logger.debug( "Command '{}' running as Process: '{}'.", cmd, processId );
                    }
                    try
                    {

                        logger.debug( "Waiting for the command '{}' execution.", cmd );
                        int status = process.waitFor();
                        extractOutput( process );
                        extractException( process );

                        return status;
                    }
                    catch ( InterruptedException e )
                    {
                        logger.error( "Error while waiting for the command '{}' execution, exception: {}", cmd, e );
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Method to extract process output
     *
     * @param process
     */
    private static void extractOutput( final Process process )
    {
        new Thread( new Runnable()
        {

            @Override
            public void run()
            {
                BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
                String line = "";
                StringBuilder output = new StringBuilder();
                try
                {
                    while ( ( line = reader.readLine() ) != null )
                    {
                        output.append( line + "\n" );
                    }
                }
                catch ( Exception e )
                {
                    logger.error( "Exception while extracting output: {}", e );
                }
                logger.debug( "output for the command: {}, exit value: {}", maskPassword( output.toString() ),
                              process.exitValue() );
            }
        } ).start();
    }

    /**
     * Method to extract process exceptions
     *
     * @param process
     */
    private static void extractException( final Process process )
    {
        new Thread( new Runnable()
        {

            @Override
            public void run()
            {
                BufferedReader reader = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
                String line = "";
                StringBuilder output = new StringBuilder();
                try
                {
                    while ( ( line = reader.readLine() ) != null )
                    {
                        output.append( line + "\n" );
                    }
                }
                catch ( Exception e )
                {
                    logger.error( "Exception while extracting exceptions: ", e );
                }
                logger.debug( "Error while executing the command: {}, exit value: {}",
                              maskPassword( output.toString() ), process.exitValue() );
            }
        } ).start();
    }

    /**
     * Executes the given command with arguments and returns its Process.
     *
     * @param cmdWithArgs the cmd with args as list
     * @return the process
     */
    public static Process execute( final List<String> cmdWithArgs )
    {

        if ( cmdWithArgs != null )
        {

            int cmdsLength = cmdWithArgs.size();
            if ( cmdsLength > 0 )
            {

                String cmd = ProcessUtil.getCommand( cmdWithArgs );
                cmd = maskPassword( cmd );

                ProcessBuilder processBuilder = new ProcessBuilder( cmdWithArgs );
                try
                {
                    logger.debug( "Executing the command: [{}]", cmd );
                    return processBuilder.start();

                }
                catch ( IOException e )
                {
                    logger.error( "Error while executing the command : {}, exception: {}", cmd, e );
                }
            }
        }
        return null;
    }

    /**
     * Returns the String built from the command with arguments.
     *
     * @param cmdWithArgs the cmd with args
     * @return the command
     */
    private static String getCommand( final List<String> cmdWithArgs )
    {

        if ( cmdWithArgs != null )
        {

            int cmdsLength = cmdWithArgs.size();
            if ( cmdsLength > 0 )
            {

                StringBuffer sb = new StringBuffer();
                for ( int index = 0; index < cmdsLength; index++ )
                {
                    sb.append( cmdWithArgs.get( index ) );
                    sb.append( " " );
                }
                return sb.toString().trim();
            }
        }
        return null;
    }

    /**
     * Gets the process id.
     *
     * @param process the process
     * @return the process id
     */
    public static int getProcessId( Process process )
    {

        if ( process != null )
        {

            if ( process.getClass().getName().equals( "java.lang.UNIXProcess" ) )
            {

                try
                {

                    Field f = process.getClass().getDeclaredField( "pid" );
                    f.setAccessible( true );
                    return f.getInt( process );

                }
                catch ( Exception e )
                {

                    logger.debug( "Error while getting PID.", e );
                }
            }
        }
        return -1;
    }

    /**
     * Masks password in the command
     *
     * @param command
     * @return
     */
    private static final String maskPassword( String command )
    {
        if ( StringUtils.isNotBlank( command ) )
        {
            return command.replaceAll( "(?<=echo\\s)[^|]++", "*****" );
        }
        return null;
    }
}
