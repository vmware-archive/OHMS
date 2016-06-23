/* ********************************************************************************
 * GetSupportedAPI.java
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
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 * Task to Get API supported by Plugin
 * 
 * @author suket
 */
public class GetSupportedAPI
    extends IpmiTask
{
    private static Logger logger = Logger.getLogger( GetSupportedAPI.class );

    public ServerNode node;

    public TaskResponse response;

    public GetSupportedAPI( TaskResponse response )
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
            IBoardService boardService = BoardServiceProvider.getBoardService( serviceServerNode );
            if ( boardService != null )
            {
                Object[] paramsArray = new Object[] { serviceServerNode };
                List<HmsApi> supportedHMSAPI =
                    HmsPluginServiceCallWrapper.invokeHmsPluginService( boardService, serviceServerNode,
                                                                        "getSupportedHmsApi", paramsArray );
                this.node.setSupportedHMSAPI( supportedHMSAPI );
            }
            else
            {
                throw new Exception( "Board Service is NULL for node:" + node.getNodeID() );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while getting Supported HMS APIs for node id:" + node.getNodeID(), e );
            throw new HmsException( "Error while getting Supported HMS APIs for node id:" + node.getNodeID(), e );
        }
    }
}
