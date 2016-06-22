/* ********************************************************************************
 * OobTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.oob;

import com.vmware.vrack.hms.task.IHmsTask;

public abstract class OobTask
    implements IHmsTask
{
    public abstract void destroy()
        throws Exception;
}
