/* ********************************************************************************
 * CLITask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ib.cli;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.task.ib.IBTask;

public abstract class CLITask
    extends IBTask
{
    public ServerNode node;

    public TaskResponse response;

    private static Logger logger = Logger.getLogger( CLITask.class );

    public CLITaskConnector connector;

    public Session session = null;

    public CLITask()
    {
        super();
    }

    public CLITask( TaskResponse response )
    {
        node = (ServerNode) response.getNode();
    }

    public TaskResponse call()
        throws Exception
    {
        if ( connector != null )
            executeTask();
        else
        {
            getConnection();
            executeTask();
            destroy();
        }
        return response;
    }

    public void getConnection()
    {
        if ( node.isDiscoverable() && node.isPowered() )
            try
            {
                connector = new CLITaskConnector( node.getOsUserName(), node.getOsPassword(), node.getIbIpAddress(),
                                                  node.getSshPort() );
                connector.createConnection();
            }
            catch ( JSchException jSchException )
            {
                // TODO: Log exception
                if ( connector != null )
                {
                    try
                    {
                        connector.destroy();
                        connector = null;
                        logger.error( "Error in CLITask connector Initialization", jSchException );
                    }
                    catch ( Exception e )
                    {
                        connector = null;
                        logger.error( "Error in CLITask connection", e );
                    }
                }
            }
            catch ( Exception e )
            {
                logger.error( "Error in getting CLI task connection.", e );
            }
    }

    public void destroy()
        throws Exception
    {
        if ( connector != null )
        {
            try
            {
                connector.destroy();
                connector = null;
            }
            catch ( Exception e )
            {
                connector = null;
                logger.error( "Error while Destroying CLconnection: ", e );
            }
        }
    }
}
