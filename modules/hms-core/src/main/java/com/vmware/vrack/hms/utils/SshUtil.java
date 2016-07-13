/* ********************************************************************************
 * SshUtil.java
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
package com.vmware.vrack.hms.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

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
     * Class to create a ssh channel to remote machine and execute commands remotely on that machine. The following gets
     * the session object and opens the 'exec' channel to feed commands. We have suppressed the StrictHostKeyChecking
     * because we will be using this within Vmware network only
     * 
     * @param args
     * @throws JSchException
     * @throws IOException
     */
    /*
     * public static void main(String[] args) throws JSchException, IOException { // following gets the Session Object
     * from Properties config = new java.util.Properties(); config.put("StrictHostKeyChecking", "no"); Session session =
     * getSessionObject("root", "l@ni3r2o14", "10.28.197.22", 22, config); session.connect(); // logger.debug(
     * "session connect"); String command = "vmware -v"; String result = executeCommand(session, command); //
     * logger.debug("The returned result is : \n" + result); session.disconnect(); }
     */
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
        logger.debug( "Starting to execute command [" + command + "]" );
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
                logger.debug( "End of execution of command [" + command + "]" );
            }
            return builder.toString();
        }
        return null;
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
}
