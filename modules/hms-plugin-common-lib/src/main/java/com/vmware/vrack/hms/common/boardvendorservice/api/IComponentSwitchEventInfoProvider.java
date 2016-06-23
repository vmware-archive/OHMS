/* ********************************************************************************
 * IComponentSwitchEventInfoProvider.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.api;

import java.util.List;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;

/**
 * IComponentSwitchEventInfoProvider
 */
public interface IComponentSwitchEventInfoProvider
    extends IHmsComponentService
{
    /**
     * Get Switch Component Specific Event Data
     *
     * @param serviceNode
     * @param component
     * @return List<ServerComponentEvent>
     * @throws HmsException
     */
    public List<ServerComponentEvent> getComponentSwitchEventList( ServiceHmsNode serviceNode,
                                                                   SwitchComponentEnum component )
                                                                       throws HmsException;

    /**
     * Get HMS Switch Api supported by various OOB or IB plugin for a given node.
     *
     * @param serviceNode
     * @return List<HmsApi>
     * @throws HmsException
     */
    public List<HmsApi> getSupportedHmsSwitchApi( ServiceHmsNode serviceNode )
        throws HmsException;
}
