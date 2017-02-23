/* ********************************************************************************
 * JnlpTunnelManager.java
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
package com.vmware.vrack.hms.common.boardvendorservice.remoteconsole.tcptunnel.management;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.remoteconsole.kvm.jnlpconsole.JnlpBoardPortInfo;
import com.vmware.vrack.hms.common.boardvendorservice.remoteconsole.kvm.jnlpconsole.JnlpPortType;
import com.vmware.vrack.hms.common.tcptunnel.SocketTunnel;
import com.vmware.vrack.hms.common.tcptunnel.TCPTunnel;

/**
 * Manages the TCPTunnel for boards which use jnlp file for enabling remote console display For jnlpDisplays , the
 * Tunnel which connects to the VIDEO port is considered as the primary tunnel If this tunnel closes down, all the other
 * tunnels are asked to close down There will be only one JnlpTunnelManager for each host enabling remote console using
 * a jnlp file
 * 
 * @author VMware, Inc.
 */

public class JnlpTunnelManager
{

    /**
     * Each node which enables the remote console display using a jnlp file, will have an entry here if a request for
     * the remote console display is received and the remote console display is currently being viewed. Once some the
     * tunnel service for the node closes, the entry will be removed from here.
     */
    private static Map<String, JnlpTunnelManager> tunnelManagerByHostId = new HashMap<String, JnlpTunnelManager>();

    /**
     * Id of the host who's remote console has to be viewed e.g "N0","N1"
     */
    private String nodeId;

    /**
     * host that all the tunnels should connect to
     */
    private String host;

    /**
     * A mapping of ports to connect and their JnlpPortType(e.g HTTPPORT or VIDEOPORT etc)
     */
    private List<JnlpBoardPortInfo> ports;

    private Set<UUID> videoSocketTunnelIds;

    /**
     * Manager all threads created by the Tunnel
     */
    private TunnelThreadPool threadPool;

    private Logger logger = Logger.getLogger( JnlpTunnelManager.class );

    /**
     * TCPTunnel is asked to listen for incoming connections for a specific interval;. After this interval, the
     * {@link TCPTunnel} invokes the tunnelConnectionTimeOutAlert(tunnelType) of {@link JnlpTunnelManager} which decides
     * to terminate the Tunnel Service or not. The service will be terminated if the {@link TCPTunnel} is of type
     * VIDEOPORT and there are no {@link SocketTunnel}'s of type VIDEOPORT currently in execution. When there is a new
     * Remote Console request for a node, and assuming a request was already made sometime earlier for the same node and
     * the tunnel service is running, so we would like to use the same tunnel service and we would want to give the new
     * user some time for opening the jnlp file To handle this , we set newJnlpTunnelServiceRequest to true every time
     * startTunnelService() returns information about already created local ports for TCPTunnel service instead of
     * creating tunnels.(meaning the tunnel service has already started). So we can take In case, when two remote
     * console request are received, such that the second request is received when the TCPTunnel's listener is about to
     * time out(assuming the user just downloads the jnlp file and does not invoke it), then
     * tunnelConnectionTimeOutAlert(tunnelType) will not call the shutdownTunnelService() , instead it will set
     * newJnlpTunnelServiceRequest to false and proceed. Assuming the second user does not open the jnlp file too, then
     * again when TCPTunnel's listener times out and tunnelConnectionTimeOutAlert(tunnelType) is invoked, this time as
     * the newJnlpTunnelServiceRequest is false (meaning there is no new request and we have given enough time to the
     * second user to open the jnlp file) the shutdownTunnelService() is called which terminates all the threads freeing
     * all resources
     */
    private Boolean newJnlpTunnelServiceRequest;

    /**
     * There is one instance of JnlpTunnelManager for each host which enables remote console display via a jnlp file.
     * Thus there will only be one Tunnel Service started per node. If a tunnel service has been started for a certain
     * node(e.g "N1") and a second request for the viewing N1's remote console is received, then a new tunnel service
     * will not be created, existing tunnel service will be used, i.e this existing listOfLocalPortsOpened will be
     * returned where required
     */
    private List<JnlpBoardPortInfo> listOfLocalPortsOpened;

    private JnlpTunnelManager( String host, List<JnlpBoardPortInfo> ports, String nodeId )
    {
        this.host = host;
        this.ports = ports;
        this.listOfLocalPortsOpened = new ArrayList<JnlpBoardPortInfo>();
        this.newJnlpTunnelServiceRequest = false;
        this.nodeId = nodeId;
        this.videoSocketTunnelIds = new HashSet<UUID>();
    }

    /**
     * Default time out is 60 secs
     */
    private Integer tunnelServiceTimeout = 60 * 1000;

    /**
     * Starts the tunnel service (starts all the tunnels.) A Tunnel will be started for each JnlpPortType. e.g A video
     * Tunnel is only supposed to tunnel video data.
     * 
     * @return A mapping of new local ports started by the tunnels with their JnlpPortType.
     */
    private List<JnlpBoardPortInfo> startTunnelService()
    {

        List<JnlpBoardPortInfo> listOfLocalPortsOpened = new ArrayList<JnlpBoardPortInfo>();
        try
        {
            if ( host != null && !host.equals( "" ) )
            {
                if ( ports != null && ports.size() > 0 )
                {
                    // if there are no localPorts opened, this is the
                    // first startTunnelService() request that this
                    // JnlpTunnelManager has received
                    if ( this.listOfLocalPortsOpened.size() == 0 )
                    {
                        logger.debug( "Creating Tunnels for node id " + nodeId + " connecting to " + host );
                        threadPool = new TunnelThreadPool();
                        for ( JnlpBoardPortInfo portInfo : ports )
                        {
                            JnlpBoardPortInfo localPortInfo = new JnlpBoardPortInfo();
                            TCPTunnel tunnel = new TCPTunnel( host, portInfo.getPortOpened(), this, threadPool,
                                                              portInfo.getPortType(), tunnelServiceTimeout );
                            localPortInfo.setPortOpened( tunnel.getLocalPort() );
                            localPortInfo.setPortType( portInfo.getPortType() );
                            this.threadPool.execute( tunnel );
                            // this.jnlpTunnelServiceStarted = true;
                            listOfLocalPortsOpened.add( localPortInfo );
                        }
                        this.listOfLocalPortsOpened = listOfLocalPortsOpened;

                    }
                    else
                    {
                        logger.debug( "Existing tunnel will be used for nodeId " + nodeId
                            + " as Tunnel Service was already started" );
                        // System.out.println("existing tunnels used for nodeId "+nodeId+" and
                        // newJnlpTunnelServiceRequest is set to true");
                        newJnlpTunnelServiceRequest = true;

                    }

                }
            }
        }
        catch ( IOException e )
        {
            logger.error( "Could not start the tunnel server. Starting cleanup process to close all threads", e );
            // close all running thread
            startResourceCleanUp();
        }
        return this.listOfLocalPortsOpened;

    }

    /**
     * Stops the tunnel service
     */
    private void startResourceCleanUp()
    {

        shutdownTunnelService();
    }

    /**
     * This method is called by SocketTunnel When the TCPTunnel detects an incoming connection, a SocketTunnel is
     * created. When a socketTunnel is created, it generated a unique id which identifies it and calls this method. We
     * keep a record of all the {@link SocketTunnel}'s of the type VIDEOPORT created.
     * 
     * @param socketTunnelId : Unique identifier for the SocketTunnel newly created
     * @param tunnelType : This is the JnlpPortType (signifies what purpose this socket tunnel has been created for)
     */
    public synchronized void newSocketTunnelCreated( UUID socketTunnelId, JnlpPortType tunnelType )
    {
        // System.out.println("SocketTunnel is created with id "+socketTunnelId+" and type "+tunnelType + " for node id
        // "+nodeId);
        logger.debug( "SocketTunnel is created with id " + socketTunnelId + " and type " + tunnelType + " for node id "
            + nodeId );

        synchronized ( videoSocketTunnelIds )
        {
            if ( tunnelType == JnlpPortType.VIDEOPORT )
                videoSocketTunnelIds.add( socketTunnelId );
        }

    }

    /**
     * This method is called by the SocketTunnel When the TCPTunnel receives an incoming connection, it creates a
     * SocketTunnel to handle that connection(to connect it with the host or a tunnel connecting to the host) This
     * method is called when a SocketTunnel is closed down.
     * 
     * @param socketTunnelId : The unique identified associated with each SocketTunnel
     */
    public synchronized void socketTunnelClosed( UUID socketTunnelId )
    {
        logger.debug( "SocketTunnel closed, socketTunnelId is " + socketTunnelId );

        synchronized ( videoSocketTunnelIds )
        {
            videoSocketTunnelIds.remove( socketTunnelId );
        }
    }

    /**
     * This method is called by TCPTunnel TCPTunnel service listens on some localport for incoming connections If the
     * accept() times out (default timeout is 30 secs), then this method is called. The tunnel created for forwarding
     * video content times out, it may be because of 2 cases, case 1:the user never opened the jnlp file(which initiates
     * a connection) case 2:the user opened the jnlp file which initiated a connection(thus creating a socketTunnel to
     * handle the transmission of video content) and the TCPTunnel service just listening for more connection(times out
     * and again starts listening) For handling case 1, the set videoSocketTunnelIds will be empty, so we can close the
     * service as we are sure that video contents is not being transfered over the TCPTunnel For handling case 2: We
     * ignore this are the SET videoSocketTunnelIds is not empty, which means that there is at least one SocketTunnel
     * opened which is transferring video content. TCPTunnel Service can only be closed by this method. The service will
     * only be closed if there are no SocketTunnels of the type VIDEOPORT currently running and
     * newJnlpTunnelServiceRequest is false which means that a new request for the remote console for this node has not
     * been made in the past tunnelServiceTimeout millisecs
     * 
     * @param tunnelType : JnlpPortType for which the tunnel is created.
     */
    public synchronized void tunnelConnectionTimeOutAlert( JnlpPortType tunnelType )
    {
        logger.debug( "TCPTunnel TimeOut Alert for tunnel type " + tunnelType + " of node Id " + nodeId );
        // System.out.println("TCPTunnel TimeOut Alert for tunnel type "+tunnelType +" of node Id "+nodeId);

        boolean destroyTunnels = true;
        if ( tunnelType == JnlpPortType.VIDEOPORT )
        {
            synchronized ( videoSocketTunnelIds )
            {
                if ( videoSocketTunnelIds.size() > 0 )
                {
                    destroyTunnels = false;
                }
                if ( destroyTunnels )
                {
                    synchronized ( tunnelManagerByHostId )
                    {
                        if ( !newJnlpTunnelServiceRequest )
                        {
                            shutdownTunnelService();
                        }
                        newJnlpTunnelServiceRequest = false;
                    }
                }
            }

        }

    }

    /**
     * Shuts down the tunnel service and frees all resources
     */
    private void shutdownTunnelService()
    {
        // System.out.println("shutting down the tunnel service for node id "+nodeId);
        logger.debug( "Shutting down the Tunnel Service for node id " + nodeId );
        tunnelManagerByHostId.remove( nodeId );
        if ( threadPool != null )
            threadPool.shutDown();

    }

    public void setTunnelServiceTimeout( int timeout )
    {
        this.tunnelServiceTimeout = timeout;

    }

    /**
     * Creates a new instance of {@link JnlpTunnelManager} only if there is no existing instance for the node present
     * and Starts the tunnel service using the existing or newly created JnlpTunnelManager. Only one instance of a
     * {@link JnlpTunnelManager} is created per node.
     * 
     * @param nodeId : Id for the node who's remote console has to be viewed as a String e.g "N1"
     * @param host : IP Address of the hms-host or vrm-host depending on where the method is called from
     * @param ports : Local ports at the about mentioned hosts where the Tunnel Service has been started
     * @return : A mapping of Ports where the TCPTunnel service is listening and the type of service e.g (VIDEOPORT
     *         service started on port 50000, HTTPPORT service started on port 56000)
     */
    public static List<JnlpBoardPortInfo> startTunnelService( String nodeId, String host,
                                                              List<JnlpBoardPortInfo> ports )
    {
        synchronized ( tunnelManagerByHostId )
        {
            if ( tunnelManagerByHostId.get( nodeId ) != null )
            {
                // System.out.println("Existing TunnelManager Instance used for node "+nodeId);
                return tunnelManagerByHostId.get( nodeId ).startTunnelService();
            }
            else
            {
                // System.out.println("New TunnelManager created for node "+nodeId+" for host "+host);
                JnlpTunnelManager jnlpTunnelManager = new JnlpTunnelManager( host, ports, nodeId );
                tunnelManagerByHostId.put( nodeId, jnlpTunnelManager );
                return jnlpTunnelManager.startTunnelService();
            }
        }

    }

    /**
     * Creates a new instance of {@link JnlpTunnelManager} only if there is no existing instance for the node present
     * and Starts the tunnel service using the existing or newly created JnlpTunnelManager. Only one instance of a
     * {@link JnlpTunnelManager} is created per node.
     * 
     * @param nodeId : Id for the node who's remote console has to be viewed as a String e.g "N1"
     * @param host : IP Address of the hms-host or vrm-host depending on where the method is called from
     * @param ports : Local ports at the about mentioned hosts where the Tunnel Service has been started
     * @param tunnelServiceTimeout : Timeout in milliseconds for the which the TCPTunnel waits for an incoming
     *            connection. Once this time expires the {@link JnlpTunnelManager} for the node is invoked which takes a
     *            decision as to close the TCPTunnel service or to keep it running
     * @return A mapping of Ports where the TCPTunnel service is listening and the type of service e.g (VIDEOPORT
     *         service started on port 50000, HTTPPORT service started on port 56000)
     */
    public static List<JnlpBoardPortInfo> startTunnelService( String nodeId, String host, List<JnlpBoardPortInfo> ports,
                                                              Integer tunnelServiceTimeout )
    {
        synchronized ( tunnelManagerByHostId )
        {
            if ( tunnelManagerByHostId.get( nodeId ) != null )
            {
                // System.out.println("Existing TunnelManager Instance used for node "+nodeId);
                return tunnelManagerByHostId.get( nodeId ).startTunnelService();
            }
            else
            {
                // System.out.println("New TunnelManager created for node "+nodeId+" for host "+host);
                JnlpTunnelManager jnlpTunnelManager = new JnlpTunnelManager( host, ports, nodeId );
                jnlpTunnelManager.setTunnelServiceTimeout( tunnelServiceTimeout );
                tunnelManagerByHostId.put( nodeId, jnlpTunnelManager );
                return jnlpTunnelManager.startTunnelService();
            }
        }

    }

    /*************************
     * For testing and fine control over the JnlpTunnelManager if needed
     *************************/

    public static JnlpTunnelManager getJnlpTunnelManagerInstance( String nodeId )
    {
        synchronized ( tunnelManagerByHostId )
        {
            if ( tunnelManagerByHostId.get( nodeId ) != null )
                return tunnelManagerByHostId.get( nodeId );
            else
                return null;
        }
    }

    public List<JnlpBoardPortInfo> getListOfLocalPortsOpened()
    {
        return this.listOfLocalPortsOpened;
    }

    public TunnelThreadPool getThreadPool()
    {
        return this.threadPool;
    }

    public Set<UUID> getVideoSocketTunnelIds()
    {
        return videoSocketTunnelIds;
    }
    /*******************************************************************************************************************/
}
