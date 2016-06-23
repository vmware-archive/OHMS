/* ********************************************************************************
 * PowerStatusServerTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.oob.ipmi;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.boardservice.HmsPluginServiceCallWrapper;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsResourceBusyException;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

public class PowerStatusServerTask
    extends IpmiTask
{
    private static Logger logger = Logger.getLogger( PowerStatusServerTask.class );

    public ServerNode node;

    public TaskResponse response;

    public PowerStatusServerTask( TaskResponse response )
    {
        this.response = response;
        this.node = (ServerNode) response.getNode();
    }

    @Override
    public void executeTask()
        throws Exception
    {
        try
        {
            ServiceServerNode serviceServerNode = (ServiceServerNode) node.getServiceObject();
            IBoardService boardService = BoardServiceProvider.getBoardService( serviceServerNode );
            Object[] paramsArray = new Object[] { serviceServerNode };
            Boolean powerStatus =
                HmsPluginServiceCallWrapper.invokeHmsPluginService( boardService, serviceServerNode,
                                                                    "getServerPowerStatus", paramsArray );
            this.node.setPowered( powerStatus );
            this.node.setDiscoverable( true );
        }
        catch ( HmsResourceBusyException e )
        {
            String error =
                String.format( "HMS Resource is Busy for the node [%s]. Please try after some time", node.getNodeID() );
            logger.debug( error, e );
            throw e;
        }
        catch ( Exception e )
        {
            this.node.setDiscoverable( false );
            logger.error( "Error while getting Server Power State for Node:" + node.getNodeID(), e );
            throw new HmsException( "Error while getting Server Power State for Node:" + node.getNodeID(), e );
        }
    }
}
