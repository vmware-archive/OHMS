/* ********************************************************************************
 * SocketTunnel.java
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
package com.vmware.vrack.hms.common.tcptunnel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.remoteconsole.kvm.jnlpconsole.JnlpPortType;
import com.vmware.vrack.hms.common.boardvendorservice.remoteconsole.tcptunnel.management.JnlpTunnelManager;
import com.vmware.vrack.hms.common.boardvendorservice.remoteconsole.tcptunnel.management.TunnelThreadPool;

/**
 * Connects to service at (bounceHost,bouncePort) and starts stream connectors which connects remoteSocket's inputStream
 * to bounceSocket's outputStream and bounceSocket's inputStream to RemoteSocket's outputStream.
 * 
 * @author VMware, Inc.
 */
public class SocketTunnel
    implements Runnable
{
    private static Logger logger = Logger.getLogger( SocketTunnel.class );

    private UUID socketTunnelId;

    private Socket remoteSocket;

    private String bounceHost;

    private int bouncePort;

    private Object terminationHandler;

    private StreamConnector inConnect, outConnect;

    private TunnelThreadPool threadPool;

    private JnlpTunnelManager tunnelManager;

    public SocketTunnel( Socket remoteSocket, String bounceHost, int bouncePort, TunnelThreadPool threadPool,
                         JnlpTunnelManager tunnelManager, JnlpPortType tunnelType )
    {
        this.remoteSocket = remoteSocket;
        this.bounceHost = bounceHost;
        this.bouncePort = bouncePort;
        this.terminationHandler = new Object();
        this.threadPool = threadPool;
        this.tunnelManager = tunnelManager;
        socketTunnelId = UUID.randomUUID();
        if ( tunnelManager != null )
            this.tunnelManager.newSocketTunnelCreated( socketTunnelId, tunnelType );
    }

    public void run()
    {
        logger.debug( "Starting a socket tunnel for remote host " + remoteSocket.getInetAddress()
            + " and connecting to bounce host at " + bounceHost + " at port" + bouncePort );

        try (Socket bounceSocket = new Socket( bounceHost, bouncePort );)
        {
            remoteSocket.setTcpNoDelay( true );
            bounceSocket.setTcpNoDelay( true );
            // start Stream connectors to deal with forwarding data
            inConnect = new StreamConnector( remoteSocket.getInputStream(), bounceSocket.getOutputStream(),
                                             terminationHandler );
            outConnect = new StreamConnector( bounceSocket.getInputStream(), remoteSocket.getOutputStream(),
                                              terminationHandler );

            threadPool.execute( inConnect );
            threadPool.execute( outConnect );

            // wait till streamConnector notifies
            synchronized ( terminationHandler )
            {
                this.terminationHandler.wait();
            }
            remoteSocket.close();
            logger.debug( "Terminating socket tunnel for SocketTunnelId " + socketTunnelId );

        }
        catch ( Exception e )
        {

        }
        finally
        {
            if ( tunnelManager != null )
                tunnelManager.socketTunnelClosed( socketTunnelId );
        }
    }

    public void stopSocketTunnel()
    {

        if ( inConnect != null )
            inConnect.terminateStreamConnector();

        if ( outConnect != null )
            outConnect.terminateStreamConnector();

    }

    /**
     * Forwards all the content on the inputStream to the outputStream
     * 
     * @author tpanse
     */
    private static class StreamConnector
        implements Runnable
    {
        private InputStream inStream;

        private OutputStream outStream;

        private Object socketTunnelTerminationHandler;

        final static int bufferSize = 65536;

        private boolean serviceStatus;

        public StreamConnector( InputStream in, OutputStream out, Object socketTunnelTerminationHandler )
        {
            this.inStream = in;
            this.outStream = out;
            this.socketTunnelTerminationHandler = socketTunnelTerminationHandler;
            this.serviceStatus = true;
        }

        public void terminateStreamConnector()
        {

            try
            {
                inStream.close();
            }
            catch ( IOException e )
            {
                logger.debug( "exception caught which closing inputStream ", e );
            }
            try
            {
                outStream.close();
            }
            catch ( IOException e )
            {
                logger.debug( "exception caught which closing outputStream ", e );
            }
        }

        public void run()
        {
            logger.debug( "starting a stream connector" );
            byte[] buffer = new byte[bufferSize];
            try
            {
                while ( serviceStatus )
                {
                    int size = Math.max( 1, Math.min( inStream.available(), buffer.length ) );
                    size = inStream.read( buffer, 0, size );
                    if ( size != -1 )
                    {
                        try
                        {
                            outStream.write( buffer, 0, size );
                            outStream.flush();
                        }
                        catch ( SocketException se )
                        {
                            logger.debug( "bounce host has closed the socket. Closing stream connector" );
                            break;
                        }

                    }
                    else
                    {
                        logger.debug( "End of file detected by stream connector" );
                        break;
                    }

                }
            }
            catch ( IOException e )
            {
                logger.debug( "thread is blocked on IO operation and socket is closed", e );
            }
            finally
            {
                serviceStatus = false;
                terminateSocketTunnel();
                logger.debug( "Exiting stream connector" );
            }

        }

        /**
         * Responsible for releasing socketTunnel if it is waiting
         */
        public void terminateSocketTunnel()
        {
            synchronized ( socketTunnelTerminationHandler )
            {
                socketTunnelTerminationHandler.notify();
            }
        }

    }

}
