/* ********************************************************************************
 * ChassisIdentifyTask.java
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

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.boardservice.HmsPluginServiceCallWrapper;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsResourceBusyException;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 * Task to Identify Chassis via some kind of mechanism(i.e flashing lights, sounds, front panel LEDs).
 * 
 * @author Yagnesh Chawda
 */
public class ChassisIdentifyTask
    extends IpmiTask
{
    private static Logger logger = Logger.getLogger( ChassisIdentifyTask.class );

    public ServerNode node;

    public TaskResponse response;

    private ChassisIdentifyOptions preparedParameter;

    public ChassisIdentifyTask( TaskResponse response, Object preparedParameter )
    {
        // super(response);
        this.node = (ServerNode) response.getNode();
        this.preparedParameter = (ChassisIdentifyOptions) preparedParameter;
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
                boolean status =
                    HmsPluginServiceCallWrapper.invokeHmsPluginService( boardService, serviceServerNode,
                                                                        "setChassisIdentification", paramsArray );
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
            logger.error( "Error while performing Chassis Identification for Node:" + node.getNodeID(), e );
            throw new HmsException( "Error while performing Chassis Identification for Node:" + node.getNodeID(), e );
        }
    }

    public String getTaskOutput()
    {
        return "";
    }
}
