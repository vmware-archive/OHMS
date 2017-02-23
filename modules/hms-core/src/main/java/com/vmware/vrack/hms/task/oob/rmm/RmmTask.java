/* ********************************************************************************
 * RmmTask.java
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
package com.vmware.vrack.hms.task.oob.rmm;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.task.oob.OobTask;

public abstract class RmmTask
    extends OobTask
{
    public ServerNode node;

    public TaskResponse response;

    private static Logger logger = Logger.getLogger( RmmTask.class );

    public RmmTask()
    {
        super();
    }

    public RmmTask( TaskResponse response )
    {
        node = (ServerNode) response.getNode();
    }

    @Override
    public TaskResponse call()
        throws Exception
    {
        executeTask();
        return response;
    }

    public void setupConnection()
    {

    }

    @Override
    public void destroy()
    {

    }

}
