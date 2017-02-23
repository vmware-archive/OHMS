/* ********************************************************************************
 * TaskSuite.java
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
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.task.ib.cli.CLITaskConnector;

public abstract class TaskSuite
    implements IHmsTask
{

    public TaskResponse response;

    public ServerNode node;

    private static Logger logger = Logger.getLogger( TaskSuite.class );

    public CLITaskConnector sshConnector = null;

    public TaskResponse call()
        throws Exception
    {
        node = (ServerNode) response.getNode();
        getConnection();
        executeTask();
        destroy();

        return response;
    }

    public void getConnection()
    {

        // getIPMIConnection();
        // getCLIConnection();
        // getCIMClient();
    }

    /*
     * protected void getIPMIConnection() { try { ipmiConnector = new
     * IpmiTaskConnector(this.node.getManagementIp(),node.getManagementUserName(),node.getManagementUserPassword());
     * ipmiConnector.createConnection(); node.setDiscoverable(true); //PowerStatusServerTask powerStatus = new
     * PowerStatusServerTask(this.response,ipmiConnector); //TODO: Commented by Yagnesh. //PowerStatusServerTask
     * powerStatus = new PowerStatusServerTask(this.response); //powerStatus.executeTask(); } catch(Exception e) {
     * logger.error("Unable to create IPMIConnector Successfully:", e); if(ipmiConnector != null) { try {
     * ipmiConnector.destroy(); ipmiConnector = null; } catch (Exception exception) { ipmiConnector = null;
     * logger.error("Exception while destroying corrupted ipmiConnector: ", exception); } } node.setDiscoverable(false);
     * node.setPowered(false); } }
     */

    protected void getCLIConnection()
    {
        try
        {
            if ( node.isPowered() )
            {
                sshConnector = new CLITaskConnector( node.getOsUserName(), node.getOsPassword(), node.getIbIpAddress(),
                                                     node.getSshPort() );
                sshConnector.createConnection();
            }
        }
        catch ( JSchException e )
        {
            if ( sshConnector != null )
            {
                try
                {
                    sshConnector.destroy();
                    sshConnector = null;
                }
                catch ( Exception exception )
                {
                    sshConnector = null;
                    logger.error( "While Destroying corrupted sshConnector: ", exception );
                }
            }
            logger.error( "Error creating connection for node " + node.getNodeID() + " errorMessage: "
                + e.getMessage() );
        }
    }

    public void destroy()
    {
        /*
         * if(ipmiConnector != null) { try { ipmiConnector.destroy(); ipmiConnector = null; } catch (Exception e) {
         * logger.error("Error while destroying ipmiConnector: ", e); } }
         */

        if ( sshConnector != null )
        {
            try
            {
                sshConnector.destroy();
                sshConnector = null;
            }
            catch ( Exception e )
            {
                logger.error( "While Destroying sshConnector: ", e );
            }
        }
    }

}
