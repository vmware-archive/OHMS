/* ********************************************************************************
 * ServerInfoServerTask.java
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
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;

public class ServerInfoServerTask
    extends IpmiTask
{
    private static Logger logger = Logger.getLogger( ServerInfoServerTask.class );

    // private final int FRU_READ_PACKET_SIZE = 255;
    // private final int DEFAULT_FRU_ID = 0;
    public TaskResponse response;

    public ServerNode node;

    public ServerInfoServerTask( TaskResponse response )
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
            if ( boardService != null )
            {
                Object[] paramsArray = new Object[] { serviceServerNode };
                ServerNodeInfo serverNodeInfo =
                    HmsPluginServiceCallWrapper.invokeHmsPluginService( boardService, serviceServerNode,
                                                                        "getServerInfo", paramsArray );
                // We are getting following two properties from hms-inventory file, so not overwriting those values.
                // node.setBoardProductName(serverNodeInfo.getBoardProductName());
                // node.setBoardVendor(serverNodeInfo.getBoardVendor());
                this.node.setBoardSerialNumber( serverNodeInfo.getComponentIdentifier().getSerialNumber() );
                this.node.setBoardPartNumber( serverNodeInfo.getComponentIdentifier().getPartNumber() );
                this.node.setBoardMfgDate( serverNodeInfo.getComponentIdentifier().getManufacturingDate() );
                node.setDiscoverable( true );
            }
            else
            {
                throw new Exception( "Board Service is NULL for node:" + node.getNodeID() );
            }
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
            logger.error( "Error while getting Server Info for Node:" + node.getNodeID(), e );
            throw new HmsException( "Error while getting Server Info for Node:" + node.getNodeID(), e );
        }
    }
}
