/* ********************************************************************************
 * SwitchBootTaskSuite.java
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
