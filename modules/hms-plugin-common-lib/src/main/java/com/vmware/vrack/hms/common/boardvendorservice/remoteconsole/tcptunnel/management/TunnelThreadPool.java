/* ********************************************************************************
 * TunnelThreadPool.java
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

import java.util.ArrayList;
import java.util.List;

import com.vmware.vrack.hms.common.tcptunnel.SocketTunnel;
import com.vmware.vrack.hms.common.tcptunnel.TCPTunnel;

/**
 * Manages threads created by a JnlpTunnelManager,TCPTunnel,SocketTunnel
 * 
 * @author VMware, Inc.
 */
public class TunnelThreadPool
{
    // Threads
    private List<Thread> tunnelThreads;

    // Runnable tasks (type TCPTunnel,SocketTunnel,StreamConnector)
    private List<Runnable> tunnelTasks;

    public TunnelThreadPool()
    {
        tunnelThreads = new ArrayList<Thread>();
        tunnelTasks = new ArrayList<Runnable>();
    }

    /**
     * Execute the task and store additional information for later use
     * 
     * @param task
     */
    public void execute( Runnable task )
    {
        if ( task != null )
        {
            tunnelTasks.add( task );
            Thread tunnelTaskThread = new Thread( task );
            tunnelThreads.add( tunnelTaskThread );
            tunnelTaskThread.start();
        }
    }

    /**
     * Close all the tasks
     */
    public void shutDown()
    {
        // System.out.println("Thread Pool is shutting down");
        for ( Runnable task : tunnelTasks )
        {
            // Asks the TCPTunnel to stop listening for more connections
            if ( task instanceof TCPTunnel )
            {
                ( (TCPTunnel) task ).stopTunnel();
                // Asks the SocketTunnel to close the streamConnectors
                // and close itself
            }
            else if ( task instanceof SocketTunnel )
            {
                ( (SocketTunnel) task ).stopSocketTunnel();
            }
        }

    }

    public List<Runnable> getTunnelTasks()
    {
        return tunnelTasks;
    }

    public List<Thread> getTunnelThreads()
    {
        return tunnelThreads;
    }
}
