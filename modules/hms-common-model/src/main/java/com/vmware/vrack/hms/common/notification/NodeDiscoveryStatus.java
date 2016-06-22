/* ********************************************************************************
 * NodeDiscoveryStatus.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.notification;

import com.vmware.vrack.hms.common.servernodes.api.ServerNodePowerStatus;

/**
 * Node discovery status for serverNode
 *
 * @author VMware Inc.
 */
public class NodeDiscoveryStatus
{
    private String nodeId;

    private ServerNodePowerStatus status;

    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeId( String nodeId )
    {
        this.nodeId = nodeId;
    }

    public ServerNodePowerStatus getStatus()
    {
        return status;
    }

    public void setStatus( ServerNodePowerStatus status )
    {
        this.status = status;
    }
}
