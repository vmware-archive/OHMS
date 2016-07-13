/* ********************************************************************************
 * SwitchIpv4RouteManager.java
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
package com.vmware.vrack.hms.node.switches;

import com.vmware.vrack.hms.boardservice.HmsPluginServiceCallWrapper;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;

public class SwitchIpv4RouteManager
{
    public void configureIpv4DefaultRoute( ISwitchService switchService, SwitchNode switchNode, String gateway,
                                           String portId )
                                               throws HmsException
    {
        Object[] paramsArray = new Object[] { switchNode, gateway, portId };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode,
                                                                  "configureIpv4DefaultRoute", paramsArray );
    }

    public void deleteIpv4DefaultRoute( ISwitchService switchService, SwitchNode switchNode )
        throws HmsException
    {
        Object[] paramsArray = new Object[] { switchNode };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "deleteIpv4DefaultRoute",
                                                                  paramsArray );
    }
}
