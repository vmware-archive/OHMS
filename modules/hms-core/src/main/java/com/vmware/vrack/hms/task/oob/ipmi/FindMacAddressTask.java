/* ********************************************************************************
 * FindMacAddressTask.java
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
package com.vmware.vrack.hms.task.oob.ipmi;

/**
 * Finds Mac Address of Bmc Node
 * @author Yagnesh Chawda
 */
import org.apache.log4j.Logger;

import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsResourceBusyException;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

@SuppressWarnings( "deprecation" )
public class FindMacAddressTask
    extends IpmiTask
{
    private static Logger logger = Logger.getLogger( FindMacAddressTask.class );

    public ServerNode node;

    public TaskResponse response;

    public FindMacAddressTask( TaskResponse response )
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
                // Object[] paramsArray = new Object[] { serviceServerNode };
                String macAddress = boardService.getManagementMacAddress( serviceServerNode );
                // String macAddress =
                // HmsPluginServiceCallWrapper.invokeHmsPluginService(boardService,
                // serviceServerNode, "getManagementUsers", paramsArray);
                this.node.setOobMacAddress( macAddress );
            }
            else
            {
                throw new Exception( "Board Service is NULL for node: " + node.getNodeID() );
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
            throw new HmsException( "Error while getting MAC Address for Node: " + node.getNodeID(), e );
        }
    }

}
