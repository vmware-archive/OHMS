/* ********************************************************************************
 * SshUtil.java
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class SshUtil
{
    private static Logger logger = Logger.getLogger( SshUtil.class );

    /**
     * @param command
     * @return maskedString
     */
    public static String maskedPasswordString( String command )
    {
        String maskedString = command;
        Pattern passwdPattern = Pattern.compile( ".*echo\\s+('\\S+')\\s*\\|\\s*sudo\\s+-S\\s+.+$" );
        Matcher matcher = passwdPattern.matcher( command );
        // Replace all matched password with all '*' pattern.
        if ( matcher.matches() )
        {
            maskedString = command.replaceAll( matcher.group( 1 ), "********" );
        }
        return maskedString;
    }

    /**
     * Retunrs Session object for host and other params passed. Caller to open channel to perform actual operation.
     *
     * @param userName
     * @param password
     * @param hostName
     * @param port
     * @param sessionConfig
     * @return
     * @throws JSchException
     */
    public static Session getSessionObject( String userName, String password, String hostName, int port,
                                            Properties sessionConfig )
    {
        Session session = null;
        // logger.debug("In getSessionObject");
        if ( userName != null && !"".equals( userName ) && password != null && hostName != null
            && !"".equals( hostName ) )
        {
            JSch jsch = new JSch();
            try
            {
                session = jsch.getSession( userName, hostName, port );
                session.setPassword( password );
                if ( sessionConfig != null )
                {
                    session.setConfig( sessionConfig );
                }
                // logger.debug("COnfig set");
            }
            catch ( Exception e )
            {
                logger.error( "Creating SSH session failed", e );
            }
            return session;
        }
        // logger.debug("Before returning Session object");
        return null;
    }

    /**
     * It executes the command and returns the response back to the calling function. This function will expect Session
     * object and command string as parameters .
     *
     * @param sessionObj
     * @param command
     * @return
     * @throws JSchException
     * @throws IOException
     */
    public static String executeCommand( Session sessionObj, String command )
        throws JSchException, IOException
    {
        StringBuilder builder = null;
        logger.debug( "Starting to execute command [" + maskedPasswordString( command ) + "]" );
        if ( sessionObj != null && command != null && !"".equals( command ) )
        {
            builder = new StringBuilder();
            Channel channel = null;
            int arrMaxSize = 1024;
            try
            {
                channel = sessionObj.openChannel( "exec" );
                ( (ChannelExec) channel ).setCommand( command );
                channel.setInputStream( null );
                ( (ChannelExec) channel ).setErrStream( System.err );
                InputStream in = channel.getInputStream();
                channel.connect();
                byte[] tmp = new byte[arrMaxSize];
                while ( true )
                {
                    while ( in.available() > 0 )
                    {
                        int i = in.read( tmp, 0, arrMaxSize );
                        if ( i < 0 )
                            break;
                        builder.append( new String( tmp, 0, i ) );
                    }
                    if ( channel.isClosed() )
                    {
                        break;
                    }
                    try
                    {
                        Thread.sleep( 500 );
                    }
                    catch ( Exception ee )
                    {
                    }
                }
                if ( channel.isClosed() && channel.getExitStatus() != 0 )
                {
                    logger.debug( "Command exited with error code " + channel.getExitStatus() );
                }
            }
            catch ( Exception e )
            {
                logger.error( "Received exception during command execution", e );
            }
            finally
            {
                if ( channel != null && channel.isConnected() )
                {
                    channel.disconnect();
                }
                logger.debug( "End of execution of command [" + maskedPasswordString( command ) + "]" );
            }
            return builder.toString();
        }
        return null;
    }

    public static SshExecResult executeCommandEnhanced( Session session, String command )
    {
        SshExecResult result = null;
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        long startTime = 0;
        long endTime = 0;
        if ( command == null || command.trim().length() == 0 )
        {
            logger.warn( "Cannot execute null or invalid command specified [" + maskedPasswordString( command ) + "]" );
            return result;
        }
        if ( session == null || !session.isConnected() )
        {
            logger.warn( "Cannot execute null or invalid session specified [" + session + "]" );
            return result;
        }
        ChannelExec channel = null;
        try
        {
            channel = (ChannelExec) session.openChannel( "exec" );
            channel.setCommand( command );
            channel.setOutputStream( stdout );
            channel.setErrStream( stderr );
            startTime = System.currentTimeMillis();
            channel.connect();
            while ( !channel.isClosed() )
            {
                endTime = System.currentTimeMillis();
            }
            ;
            result = new SshExecResult();
            result.setCommand( command );
            result.setStdout( stdout.toByteArray() );
            result.setStderr( stderr.toByteArray() );
            result.setExitCode( channel.getExitStatus() );
            result.setStartTime( startTime );
            result.setEndTime( endTime );
        }
        catch ( JSchException e )
        {
            logger.error( "Error executing command [" + maskedPasswordString( command ) + "]", e );
        }
        finally
        {
            if ( channel != null && channel.isConnected() )
            {
                channel.disconnect();
            }
        }
        return ( result );
    }

    public static void executeCommandNoResponse( Session sessionObj, String command )
        throws JSchException, IOException
    {
        logger.debug( "Starting to execute command [" + maskedPasswordString( command ) + "]" );
        if ( sessionObj != null && command != null && !"".equals( command ) )
        {
            Channel channel = null;
            try
            {
                channel = sessionObj.openChannel( "exec" );
                ( (ChannelExec) channel ).setCommand( command );
                channel.setInputStream( null );
                ( (ChannelExec) channel ).setErrStream( System.err );
                channel.getInputStream();
                channel.connect();
                /* We do not care about whether the command succeeds or not */
                if ( channel.isClosed() && channel.getExitStatus() != 0 )
                {
                    logger.debug( "Command exited with error code " + channel.getExitStatus() );
                }
            }
            catch ( Exception e )
            {
                logger.error( "Received exception during command execution", e );
            }
            finally
            {
                if ( channel != null && channel.isConnected() )
                {
                    channel.disconnect();
                }
                logger.debug( "End of execution of command [" + maskedPasswordString( command ) + "]" );
            }
        }
    }

    /**
     * This method uploads a stream/file to the remote server using sftp.
     *
     * @param session JSch session
     * @param localInputStream Local InputStream
     * @param remoteFile Absolute path of the remote file
     * @throws JSchException
     * @throws SftpException
     */
    public static void upload( Session session, InputStream localInputStream, String remoteFile )
        throws JSchException, SftpException
    {
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel( "sftp" );
        sftpChannel.connect();
        sftpChannel.put( localInputStream, remoteFile );
        sftpChannel.disconnect();
    }

    /**
     * This method downloads a file from the remote server using sftp.
     *
     * @param session JSch session
     * @param localOutputStream Local OutputStream
     * @param remoteFile Absolute path of the remote file
     * @throws JSchException
     * @throws SftpException
     */
    public static void download( Session session, OutputStream localOutputStream, String remoteFile )
        throws JSchException, SftpException
    {
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel( "sftp" );
        sftpChannel.connect();
        sftpChannel.get( remoteFile, localOutputStream );
        sftpChannel.disconnect();
    }

    /**
     * This method returns the file attributes of the remote file.
     *
     * @param session JSch session
     * @param remoteFile Absolute path of the remote file
     * @return SftpATTRS object that contains the file attributes
     * @throws JSchException
     * @throws SftpException
     */
    public static SftpATTRS lstat( Session session, String remoteFile )
        throws JSchException, SftpException
    {
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel( "sftp" );
        sftpChannel.connect();
        SftpATTRS fileStat = sftpChannel.lstat( remoteFile );
        sftpChannel.disconnect();
        return fileStat;
    }

    /**
     * It executes the set of commands and returns the last response back to the calling function. This function will
     * expect Session object and an array of String[][] objects as parameters. It is an array of Strings where the
     * format is as follows: command[0] = <login prompt> command[1] = <send command-1> command[2] = <expected output>
     * command[3] = <send command-2> command[4] = <expected output> Thus there should always be odd number of elements
     * in the array starting with index-0 holding the lgin prompt always. The expectation should be exact match of how
     * the expected prompt ends.\ Send command need not have newline at its end.
     *
     * @param sessionObj
     * @param commands
     * @return
     * @throws JSchException
     * @throws IOException
     */
    public static String executeCommand( Session sessionObj, String[] commands )
        throws JSchException, IOException
    {
        StringBuilder builder = null;
        int index = 0;
        String lastOutput = null;
        if ( sessionObj != null && commands != null )
        {
            builder = new StringBuilder();
            Channel channel = null;
            int arrMaxSize = 1024;
            try
            {
                channel = sessionObj.openChannel( "shell" );
                channel.setInputStream( null );
                InputStream in = channel.getInputStream();
                OutputStream out = channel.getOutputStream();
                channel.connect();
                byte[] tmp = new byte[arrMaxSize];
                while ( true )
                {
                    while ( in.available() > 0 )
                    {
                        int i = in.read( tmp, 0, arrMaxSize );
                        if ( i < 0 )
                            break;
                        builder.append( new String( tmp, 0, i ) );
                    }
                    if ( channel.isEOF() )
                    {
                        try
                        {
                            Thread.sleep( 500 );
                        }
                        catch ( Exception ee )
                        {
                        }
                    }
                    else
                    {
                        lastOutput = builder.toString();
                        if ( lastOutput.endsWith( commands[index] ) )
                        {
                            ++index;
                            if ( index >= commands.length )
                            {
                                break;
                            }
                            else
                            {
                                builder.setLength( 0 ); // reset builder
                            }
                            // send next command
                            out.write( ( commands[index++] + "\n" ).getBytes() );
                            out.flush();
                        }
                    }
                }
                if ( channel.isClosed() && channel.getExitStatus() != 0 )
                {
                    logger.debug( "Command exited with error code " + channel.getExitStatus() );
                }
            }
            catch ( Exception e )
            {
                logger.error( "Received exception during command execution" );
            }
            finally
            {
                if ( channel != null && channel.isConnected() )
                {
                    channel.disconnect();
                }
            }
            return lastOutput;
        }
        return lastOutput;
    }
}
