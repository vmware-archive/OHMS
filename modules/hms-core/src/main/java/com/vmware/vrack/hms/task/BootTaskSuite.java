/* ********************************************************************************
 * BootTaskSuite.java
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
package com.vmware.vrack.hms.task;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.NodeActionStatus;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.utils.NodeDiscoveryUtil;

public class BootTaskSuite
    extends TaskSuite
{
    private static Logger logger = Logger.getLogger( BootTaskSuite.class );

    public BootTaskSuite( ServerNode node )
    {
        super();
        this.node = node;
    }

    public BootTaskSuite( TaskResponse response )
    {
        super();
        this.response = response;
    }

    public void executeTask()
    {
        IBoardService boardService = null;
        try
        {
            ServiceServerNode serviceServerNode = (ServiceServerNode) node.getServiceObject();
            try
            {
                boardService = BoardServiceProvider.getBoardService( serviceServerNode );
                int attempts = 0;
                do
                {
                    try
                    {
                        if ( attempts != 0 )
                        {
                            Thread.sleep( Long.parseLong( HmsConfigHolder.getHMSConfigProperty( HmsConfigHolder.HMS_NODE_DISCOVERY_REATTEMPT_WAIT ) ) );
                        }
                        Boolean powerStatus = boardService.getServerPowerStatus( serviceServerNode );
                        this.node.setPowered( powerStatus );
                        this.node.setDiscoverable( true );
                    }
                    catch ( Exception e )
                    {
                        this.node.setDiscoverable( false );
                        logger.error( "Unable to perform Power Status task during HmsBootUp task for node [ "
                            + node.getNodeID() + " ]" );
                        attempts++;
                    }
                }
                while ( !this.node.isDiscoverable()
                    && attempts < Integer.parseInt( HmsConfigHolder.getHMSConfigProperty( HmsConfigHolder.HMS_NODE_DISCOVERY_REATTEMPTS ) ) );
                attempts = 0;
            }
            catch ( HmsException hmsException )
            {
                logger.error( "Unable to get Board Service provider for the node:" + this.node.getNodeID(),
                              hmsException );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while executing BOOT_TASK_SUITE", e );
            logger.error( e.getStackTrace() );
        }
        finally
        {
            // Update the nodeDiscoveryMap with the current discovery status of the node
            NodeDiscoveryUtil.hostDiscoveryMap.put( node.getNodeID(), this.node.isDiscoverable()
                            ? NodeActionStatus.SUCCESS : NodeActionStatus.FAILURE );
        }
        /*
         * finally { if(boardService != null) { boardService.stopConnectionSharing(); } }
         */
    }
}
