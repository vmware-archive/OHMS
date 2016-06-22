/* ********************************************************************************
 * IBTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ib;

import com.vmware.vrack.hms.task.IHmsTask;

public abstract class IBTask
    implements IHmsTask
{
    public abstract void destroy()
        throws Exception;
}
