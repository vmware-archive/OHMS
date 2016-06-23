/* ********************************************************************************
 * IHmsTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task;

import java.util.concurrent.Callable;

import com.vmware.vrack.hms.common.notification.TaskResponse;

public interface IHmsTask
    extends Callable<TaskResponse>
{
    public void executeTask()
        throws Exception;
}
