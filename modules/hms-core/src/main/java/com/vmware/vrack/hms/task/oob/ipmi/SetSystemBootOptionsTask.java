/* ********************************************************************************
 * SetSystemBootOptionsTask.java
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
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 * Task to Set System Boot Options
 *
 * @author Yagnesh Chawda
 */
public class SetSystemBootOptionsTask
    extends IpmiTask
{
    private static Logger logger = Logger.getLogger( SetSystemBootOptionsTask.class );

    public ServerNode node;

    public TaskResponse response;

    private SystemBootOptions preparedParameter;

    public SetSystemBootOptionsTask( TaskResponse response, Object preparedParameter )
    {
        this.response = response;
        this.node = (ServerNode) response.getNode();
        this.preparedParameter = (SystemBootOptions) preparedParameter;
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
                Object[] paramsArray = new Object[] { serviceServerNode, preparedParameter };
                boolean status = HmsPluginServiceCallWrapper.invokeHmsPluginService( boardService, serviceServerNode,
                                                                                     "setBootOptions", paramsArray );
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
            logger.error( "Error while setting Boot Options for Node:" + node.getNodeID(), e );
            throw new HmsException( "Error while setting Boot Options for Node:" + node.getNodeID(), e );
        }
    }
}
