/* ********************************************************************************
 * SwitchBootTaskSuite.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.switchnodes.api.HMSSwitchNode;

public class SwitchBootTaskSuite
    extends SwitchTaskSuite
{
    private static Logger logger = Logger.getLogger( SwitchBootTaskSuite.class );

    public SwitchBootTaskSuite( HMSSwitchNode node )
    {
        super();
        this.node = node;
    }

    public SwitchBootTaskSuite( TaskResponse response )
    {
        super();
        this.response = response;
    }

    public void executeTask()
    {
        // nothing required as of now. power and discovery implemented in abstract class
        return;
    }
}
