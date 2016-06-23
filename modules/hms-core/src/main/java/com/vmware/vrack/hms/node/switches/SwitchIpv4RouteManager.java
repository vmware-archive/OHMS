/* ********************************************************************************
 * SwitchIpv4RouteManager.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
