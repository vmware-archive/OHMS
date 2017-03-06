/* ********************************************************************************
 * SshTimer.java
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

import java.util.Properties;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/** Usage: SshTimer -host <host> -port <port> -username <username> -password <password> */
public class SshTimer
{

    private static Logger logger = Logger.getLogger( SshTimer.class );

    public static void main( String[] args )
    {
        String hostname = null;
        int port = 22;
        String username = null;
        String password = null;
        int timeout = 20000;

        Properties config = new java.util.Properties();
        config.put( "StrictHostKeyChecking", "no" );

        for ( int i = 0; i < args.length; i++ )
        {
            if ( args[i].equalsIgnoreCase( "-host" ) )
            {
                hostname = args[i + 1];
                i++;
            }
            else if ( args[i].equalsIgnoreCase( "-port" ) )
            {
                port = Integer.parseInt( args[i + 1] );
                i++;
            }
            else if ( args[i].equalsIgnoreCase( "-username" ) )
            {
                username = args[i + 1];
                i++;
            }
            else if ( args[i].equalsIgnoreCase( "-password" ) )
            {
                password = args[i + 1];
                i++;
            }
        }

        if ( hostname == null )
        {
            logger.error( "Hostname cannot be null." );
        }
        else if ( username == null )
        {
            logger.error( "Username cannot be null." );
        }
        else if ( password == null )
        {
            logger.error( "Password cannot be null." );
        }

        long start = System.currentTimeMillis();
        logger.info( "Start of processing." );
        JSch jsch = new JSch();
        try
        {
            Session session = jsch.getSession( username, hostname, port );
            session.setPassword( password );
            session.setDaemonThread( true );
            session.setConfig( config );
            logger.info( "Session default timeout " + session.getTimeout() );

            session.setTimeout( timeout );
            logger.info( "Session created after " + ( System.currentTimeMillis() - start ) / 1000.0 + " seconds." );
            session.connect();
            logger.info( "Connection established after " + ( System.currentTimeMillis() - start ) / 1000.0
                + " seconds." );
        }
        catch ( JSchException e )
        {
            logger.info( "Connection aborted after " + ( System.currentTimeMillis() - start ) / 1000.0 + " seconds." );
            logger.error( "Received exception.", e );
        }

    }

}
