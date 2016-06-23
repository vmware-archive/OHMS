/* ********************************************************************************
 * ListBmcUsersTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.oob.ipmi;

import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.boardservice.HmsPluginServiceCallWrapper;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsResourceBusyException;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 * This will populate the BMC user list, based on the maximum number of enabled users for that bmc
 * 
 * @author Yagnesh Chawda
 */
public class ListBmcUsersTask
    extends IpmiTask
{
    private static Logger logger = Logger.getLogger( ListBmcUsersTask.class );

    public ServerNode node;

    public TaskResponse response;

    public ListBmcUsersTask( TaskResponse response )
    {
        this.response = response;
        this.node = (ServerNode) response.getNode();
    }

    public void executeTask()
        throws Exception
    {
        try
        {
            ServiceServerNode serviceServerNode = (ServiceServerNode) node.getServiceObject();
            Object[] paramsArray = new Object[] { serviceServerNode };
            IBoardService boardService = BoardServiceProvider.getBoardService( serviceServerNode );
            List<BmcUser> bmcUsers =
                HmsPluginServiceCallWrapper.invokeHmsPluginService( boardService, serviceServerNode,
                                                                    "getManagementUsers", paramsArray );
            node.setBmcUserList( bmcUsers );
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
            logger.error( "Error while getting BMC Users for Node:" + node.getNodeID(), e );
            throw new HmsException( "Error while getting BMC Users for Node:" + node.getNodeID(), e );
        }
    }
}
