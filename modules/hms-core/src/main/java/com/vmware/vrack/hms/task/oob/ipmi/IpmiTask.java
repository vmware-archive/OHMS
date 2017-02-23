/* ********************************************************************************
 * IpmiTask.java
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

import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.task.oob.OobTask;

//TODO : Now, as HMS core does NOT need to know if IPMI or other way is required to perform operation on the server node for getting / setting any property, we need to get rid of this class, or may be name it better.
public abstract class IpmiTask
    extends OobTask
{

    public ServerNode node;

    public TaskResponse response;

    private static Logger logger = Logger.getLogger( IpmiTask.class );

    protected int taskCompletionVerifyRetries = 3;

    protected int taskCompletionRetryInterval = 3000;
    // public IpmiTaskConnector connector = null;

    public IpmiTask()
    {
        super();
    }

    public IpmiTask( TaskResponse response )
    {
        this.response = response;
        node = (ServerNode) response.getNode();
    }

    public TaskResponse call()
        throws Exception
    {
        try
        {
            executeTask();
        }
        finally
        {
            response.processTaskCompletion();
        }

        /*
         * if(connector != null) executeTask(); else { setupConnection(); executeTask(); destroy(); }
         */

        return response;
    }

    public void setupConnection()
    {
        /*
         * connector = new IpmiTaskConnector(node.getManagementIp() ,node.getManagementUserName(),
         * node.getManagementUserPassword()); try { connector.createConnection(); node.setDiscoverable(true); }
         * catch(Exception e) { if(node != null) { logger.error(
         * "Unable to create IPMIConnector Successfully for missing Node [" + node.getNodeID() + "]@" +
         * node.getManagementIp() + ":", e); } else { logger.error(
         * "Unable to create IPMIConnector Successfully for Empty Node:", e); } if(connector != null) { try {
         * connector.destroy(); connector = null; } catch (Exception exception) { connector = null; logger.error(
         * "Exception while destroying corrupted ipmiConnector: ", exception); } } node.setDiscoverable(false); }
         */
    }

    public void destroy()
    {
        /*
         * if(connector != null) { try { connector.destroy(); connector = null; } catch (Exception e) { logger.error(
         * "Exception while destroying Ipmi connector: ", e); } }
         */
    }

}
