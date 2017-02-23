/* ********************************************************************************
 * TCPTunnel.java
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.remoteconsole.kvm.jnlpconsole.JnlpPortType;
import com.vmware.vrack.hms.common.boardvendorservice.remoteconsole.tcptunnel.management.JnlpTunnelManager;
import com.vmware.vrack.hms.common.boardvendorservice.remoteconsole.tcptunnel.management.TunnelThreadPool;

/**
 * This is the TCPTunnel thread. Responsible for starting a local service which listens on a local port. When a
 * connection is made to this service, it connects to the a service on the given host,port and is concerned with
 * forwarding data from the remote host to the bounce host and vice versa
 * 
 * @author VMware, Inc.
 */
public class TCPTunnel
    implements Runnable
{
    private static Logger logger = Logger.getLogger( TCPTunnel.class );

    private Integer localPort;

    private String host;

    private Integer port;

    private ServerSocket listener;

    private final Running running = new Running();

    private JnlpTunnelManager tunnelManager;

    private TunnelThreadPool threadPool;

    private JnlpPortType tunnelType;

    public TCPTunnel( String host, Integer port, JnlpTunnelManager manager, TunnelThreadPool threadPool,
                      JnlpPortType tunnelType, Integer tunnelServiceWaitTimeInMilliSec )
        throws IOException
    {
        this.running.setRunning( false );
        this.host = host;
        this.port = port;
        listener = new ServerSocket( 0 );
        // localport where the service is listening
        this.localPort = listener.getLocalPort();
        // by default, we make the service to wait for connections for 30 sec.
        // if a connection is not made till this time, the video tunnel will
        // close down, and will close all the secondary tunnels
        this.listener.setSoTimeout( tunnelServiceWaitTimeInMilliSec );
        this.tunnelManager = manager;
        this.tunnelType = tunnelType;
        this.threadPool = threadPool;
    }

    public void run()
    {
        try
        {
            this.running.setRunning( true );
            // System.out.println("TCP tunnel is starting on port "+localPort+" and will connect to "+host+" at port
            // "+port);
            logger.debug( "TCP tunnel is starting on port " + localPort + " and will connect to " + host + " at port "
                + port );
            while ( this.running.isRunning() )
            {
                Socket listenerSocket;
                try
                {
                    listenerSocket = listener.accept();
                }
                catch ( SocketTimeoutException e )
                {
                    if ( tunnelManager != null )
                        tunnelManager.tunnelConnectionTimeOutAlert( tunnelType );
                    continue;
                }
                threadPool.execute( new SocketTunnel( listenerSocket, host, port, threadPool, tunnelManager,
                                                      tunnelType ) );
            }

        }
        catch ( SocketException e )
        {
            logger.debug( "TCPTunnel Listener asked to close down when blocked on listening at port " + localPort, e );
        }
        catch ( Exception e )
        {
            logger.warn( "Exception in TCP tunnel listening at local port" + localPort, e );

        }
    }

    public void setTunnelServiceTimeOut( int timeOut )
    {
        try
        {
            this.listener.setSoTimeout( timeOut );
        }
        catch ( SocketException e )
        {
            logger.warn( "Could not set Service timeout for the TCPtunnel listening at " + localPort, e );
        }
    }

    public void setRunning( Boolean run )
    {
        synchronized ( this.running )
        {
            this.running.setRunning( run );
        }
    }

    public Boolean isRunning()
    {
        return this.running.isRunning();
    }

    public Integer getLocalPort()
    {
        return localPort;
    }

    public void setLocalPort( Integer localPort )
    {
        this.localPort = localPort;
    }

    public void stopTunnel()
    {
        setRunning( false );
        try
        {
            listener.close();
        }
        catch ( IOException e )
        {
            logger.debug( "Thread was blocked, listening on " + localPort, e );
        }
        logger.debug( "Shutting down TCPTunnel of type " + tunnelType );
    }

}