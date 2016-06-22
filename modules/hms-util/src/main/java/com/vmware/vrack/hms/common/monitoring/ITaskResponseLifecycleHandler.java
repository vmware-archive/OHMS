/* ********************************************************************************
 * ITaskResponseLifecycleHandler.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.monitoring;

import com.vmware.vrack.hms.common.notification.TaskResponse;

public interface ITaskResponseLifecycleHandler
{
    public void init( TaskResponse taskResponse );

    public void onTaskComplete( TaskResponse taskResponse );
}
