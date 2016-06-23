/* ********************************************************************************
 * PowerCycleServerTask.java
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
import com.vmware.vrack.hms.common.notification.NodeActionStatus;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.resource.PowerOperationAction;

/**
 * Task to Power Cycle the Server Node
 * 
 * @author Yagnesh Chawda
 */
public class PowerCycleServerTask
    extends IpmiTask
{
    private static Logger logger = Logger.getLogger( PowerCycleServerTask.class );

    public PowerCycleServerTask( TaskResponse response )
    {
        super( response );
    }

    public void executeTask()
        throws Exception
    {
        try
        {
            ServiceServerNode serviceServerNode = (ServiceServerNode) node.getServiceObject();
            IBoardService boardService = BoardServiceProvider.getBoardService( serviceServerNode );
            if ( boardService != null )
            {
                response.setStatus( NodeActionStatus.RUNNING );
                Object[] paramsArray = new Object[] { serviceServerNode, PowerOperationAction.POWERCYCLE };
                boolean status = HmsPluginServiceCallWrapper.invokeHmsPluginService( boardService, serviceServerNode,
                                                                                     "powerOperations", paramsArray );
                if ( status )
                {
                    for ( int retryCount = 0; retryCount < taskCompletionVerifyRetries; retryCount++ )
                    {
                        Thread.sleep( taskCompletionRetryInterval );
                        if ( boardService.getServerPowerStatus( serviceServerNode ) )
                        {
                            response.setStatus( NodeActionStatus.SUCCESS );
                            return;
                        }
                    }
                    response.setStatus( NodeActionStatus.FAILURE );
                }
                else
                {
                    response.setStatus( NodeActionStatus.FAILURE );
                }
            }
            else
            {
                response.setStatus( NodeActionStatus.FAILURE );
                throw new Exception( "Board Service is NULL for node:" + node.getNodeID() );
            }
            return;
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
            logger.error( "Error while triggering Power Cycle Server for Node:" + node.getNodeID(), e );
            response.setStatus( NodeActionStatus.FAILURE );
            throw new HmsException( "Error while triggering Power Cycle Server for Node:" + node.getNodeID(), e );
        }
    }
}
