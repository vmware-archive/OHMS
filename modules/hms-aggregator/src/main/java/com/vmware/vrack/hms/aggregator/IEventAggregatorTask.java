/* ********************************************************************************
 * IEventAggregatorTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator;

import java.util.List;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;

/**
 * @author sgakhar Event Aggregator to be used by Event Rest Endpoints.
 */
public interface IEventAggregatorTask
{
    public List<Event> getAggregatedEvents( ServerNode node, ServerComponent component )
        throws HMSRestException;

    public List<Event> getAggregatedSwitchEvents( String switchId, SwitchComponentEnum component )
        throws HMSRestException;
}
