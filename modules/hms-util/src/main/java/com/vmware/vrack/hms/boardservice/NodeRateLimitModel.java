/* ********************************************************************************
 * NodeRateLimitModel.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.boardservice;

import java.util.concurrent.ScheduledExecutorService;

/**
 * This class is used to keep a ScheduledExecutorService and a ThreadPool for each node
 *
 * @author Vmware inc
 */
public class NodeRateLimitModel
{
    ThreadLimitExecuterServiceObjectPool threadLimitExecuterServiceObject;

    ScheduledExecutorService scheduledExecutorService;

    public ThreadLimitExecuterServiceObjectPool getThreadLimitExecuterServiceObject()
    {
        return threadLimitExecuterServiceObject;
    }

    public void setThreadLimitExecuterServiceObject( ThreadLimitExecuterServiceObjectPool threadLimitExecuterServiceObject )
    {
        this.threadLimitExecuterServiceObject = threadLimitExecuterServiceObject;
    }

    public ScheduledExecutorService getScheduledExecutorService()
    {
        return scheduledExecutorService;
    }

    public void setScheduledExecutorService( ScheduledExecutorService scheduledExecutorService )
    {
        this.scheduledExecutorService = scheduledExecutorService;
    }
}
