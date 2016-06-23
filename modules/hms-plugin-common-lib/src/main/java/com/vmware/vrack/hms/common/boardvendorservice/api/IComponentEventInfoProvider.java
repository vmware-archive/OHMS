/* ********************************************************************************
 * IComponentEventInfoProvider.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.api;

import java.util.List;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;

public interface IComponentEventInfoProvider
    extends IHmsComponentService
{
    /**
     * Get Server Component Specific Event Data
     * 
     * @param ServiceHmsNode
     * @param ServerComponent
     * @param componentID
     * @return List<ServerComponentEvent>
     * @throws HmsException
     */
    public List<ServerComponentEvent> getComponentEventList( ServiceHmsNode serviceNode, ServerComponent component )
        throws HmsException;

    /**
     * Get HMS Api supported by various OOB or IB plugin for a given node.
     * 
     * @param ServiceHmsNode
     * @return List<HmsApi>
     * @throws HmsException
     */
    public List<HmsApi> getSupportedHmsApi( ServiceHmsNode serviceNode )
        throws HmsException;
}
