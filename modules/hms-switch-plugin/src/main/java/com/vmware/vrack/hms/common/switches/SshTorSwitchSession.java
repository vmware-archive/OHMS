/* ********************************************************************************
 * SshTorSwitchSession.java
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
package com.vmware.vrack.hms.common.switches;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchSession;
import com.vmware.vrack.hms.common.util.SshExecResult;
import com.vmware.vrack.hms.common.util.SshUtil;

public class SshTorSwitchSession
    implements SwitchSession
{
    private static final int DEFAULT_CONNECTION_TIMEOUT = 20000;

    @Override
    public void setSwitchNode( SwitchNode switchNode )
    {
        this.switchNode = switchNode;
    }

    @Override
    public SwitchNode getSwitchNode()
    {
        return switchNode;
    }

    @Override
    public synchronized void connect()
        throws HmsException
    {
        connect( DEFAULT_CONNECTION_TIMEOUT );
    }

    @Override
    public synchronized void connect( int timeout )
        throws HmsException
    {
        // Clean up existing stale session, if any
        if ( sshSession != null )
        {
            if ( sshSession.isConnected() )
            {
                return;
            }
            else
            {
                sshSession.disconnect();
            }
        }
        Properties config = new Properties();
        config.put( "StrictHostKeyChecking", "no" );
        String protocol = switchNode.getProtocol();
        String username = switchNode.getUsername();
        String password = switchNode.getPassword();
        String ipAddress = switchNode.getIpAddress();
        int port = ( switchNode.getPort() != null ) ? switchNode.getPort().intValue() : 22;
        try
        {
            sshSession = SshUtil.getSessionObject( username, password, ipAddress, port, config );
            sshSession.setDaemonThread( true );
            sshSession.setTimeout( timeout );
            logger.debug( "Connecting with timeout set to " + timeout + " milliseconds." );
            sshSession.connect();
        }
        catch ( JSchException je )
        {
            logger.error( "Received exception connecting to switch " + switchNode.getSwitchId() + " at " + protocol
                + "://" + ipAddress + ":" + port, je );
            disconnect();
        }
    }

    @Override
    public synchronized boolean isConnected()
    {
        return ( sshSession != null && sshSession.isConnected() );
    }

    @Override
    public synchronized String execute( String command )
        throws HmsException
    {
        String trimmedResult = null;
        try
        {
            String result = SshUtil.executeCommand( sshSession, command );
            if ( result != null )
            {
                trimmedResult = result.trim();
            }
        }
        catch ( IOException | JSchException e )
        {
            throw new HmsException( "Received exception while executing SSH command " + command, e );
        }
        return trimmedResult;
    }

    // No response expected.
    public synchronized void executeNoResponse( String command )
        throws HmsException
    {
        try
        {
            SshUtil.executeCommandNoResponse( sshSession, command );
        }
        catch ( IOException | JSchException e )
        {
            throw new HmsException( "Received exception while executing SSH command " + command, e );
        }
    }

    // this method definition does not come from TorSwitchSession since it is specific for SSH
    public synchronized String execute( String[] commands )
        throws HmsException
    {
        String trimmedResult = null;
        try
        {
            String result = SshUtil.executeCommand( sshSession, commands );
            if ( result != null )
            {
                trimmedResult = result.trim();
            }
        }
        catch ( IOException | JSchException e )
        {
            throw new HmsException( "Received exception while executing SSH commands " + commands, e );
        }
        return trimmedResult;
    }

    public synchronized SshExecResult executeEnhanced( String command )
        throws HmsException
    {
        SshExecResult result = SshUtil.executeCommandEnhanced( sshSession, command );
        return result;
    }

    @Override
    public synchronized boolean upload( InputStream localInputStream, String remoteFilename )
        throws HmsException
    {
        boolean success = false;
        try
        {
            SshUtil.upload( sshSession, localInputStream, remoteFilename );
            success = true;
        }
        catch ( JSchException | SftpException e )
        {
            throw new HmsException( "Received exception while uploading file " + remoteFilename, e );
        }
        return success;
    }

    @Override
    public synchronized boolean download( OutputStream localOutputStream, String remoteFilename )
        throws HmsException
    {
        boolean success = false;
        try
        {
            SshUtil.download( sshSession, localOutputStream, remoteFilename );
            success = true;
        }
        catch ( JSchException | SftpException e )
        {
            throw new HmsException( "Received exception while downloading file " + remoteFilename, e );
        }
        return success;
    }

    @Override
    public synchronized void disconnect()
    {
        if ( sshSession != null )
            sshSession.disconnect();
    }

    private SwitchNode switchNode;

    private volatile Session sshSession;

    private Logger logger = Logger.getLogger( SshTorSwitchSession.class );
}
