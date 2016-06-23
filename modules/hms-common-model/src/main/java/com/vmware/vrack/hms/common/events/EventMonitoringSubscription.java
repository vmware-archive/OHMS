/* ********************************************************************************
 * EventMonitoringSubscription.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.common.event.enums.EventComponent;

/**
 * Detailed Event Registration object to be used for Event Subscription
 *
 * @author Yagnesh Chawda
 */
@JsonInclude( JsonInclude.Include.NON_NULL )
public class EventMonitoringSubscription
    extends BaseEventMonitoringSubscription
{
    private String nodeId;

    private String subscriberId;

    private EventComponent component;

    /**
     * Node on to be monitored for Event. Could be Server node or Switch Node
     *
     * @return
     */
    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeId( String nodeId )
    {
        this.nodeId = nodeId;
    }

    /**
     * Logical identification of the subscriber of the Events
     *
     * @return
     */
    public String getSubscriberId()
    {
        return subscriberId;
    }

    public void setSubscriberId( String subscriberId )
    {
        this.subscriberId = subscriberId;
    }

    /**
     * Components to be monitored on the Host/switch
     *
     * @return
     */
    public EventComponent getComponent()
    {
        return component;
    }

    public void setComponent( EventComponent component )
    {
        this.component = component;
    }
}
