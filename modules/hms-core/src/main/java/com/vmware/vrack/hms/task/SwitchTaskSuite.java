/* ********************************************************************************
 * SwitchTaskSuite.java
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

import com.jcraft.jsch.JSchException;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.switchnodes.api.HMSSwitchNode;
import com.vmware.vrack.hms.task.ib.cli.CLITaskConnector;

public abstract class SwitchTaskSuite
    implements IHmsTask
{

    public TaskResponse response;

    public HMSSwitchNode node;

    private static Logger logger = Logger.getLogger( SwitchTaskSuite.class );

    public CLITaskConnector sshConnector = null;

    public TaskResponse call()
        throws Exception
    {
        node = (HMSSwitchNode) response.getNode();
        getConnection();
        executeTask();
        destroy();

        return response;
    }

    public void getConnection()
    {

        try
        {
            sshConnector = new CLITaskConnector( node.getManagementUserName(), node.getManagementUserPassword(),
                                                 node.getManagementIp(), node.getSshPort() );
            sshConnector.createConnection();
            node.setDiscoverable( sshConnector.getSession().isConnected() );
            node.setPowered( sshConnector.getSession().isConnected() );

        }
        catch ( JSchException e )
        {
            logger.error( "Error creating connection for node " + node.getNodeID() + " errorMessage: "
                + e.getMessage() );
        }
        catch ( Exception e )
        {
            node.setDiscoverable( false );
            node.setPowered( false );

        }
    }

    public void destroy()
    {

        if ( sshConnector != null )
        {

            try
            {
                sshConnector.destroy();
                sshConnector = null;
            }
            catch ( Exception e )
            {
                // TODO Auto-generated catch block
                logger.error( e.getMessage() );
            }

        }
    }

}
