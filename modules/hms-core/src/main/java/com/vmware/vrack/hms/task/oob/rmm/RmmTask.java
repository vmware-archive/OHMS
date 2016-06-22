/* ********************************************************************************
 * RmmTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
