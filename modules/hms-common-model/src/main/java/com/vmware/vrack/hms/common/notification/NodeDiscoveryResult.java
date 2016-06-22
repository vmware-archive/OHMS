/* ********************************************************************************
 * NodeDiscoveryResult.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.notification;

import java.util.List;

public class NodeDiscoveryResult
{
    private List<NodeDiscoveryStatus> completed;

    private List<NodeDiscoveryStatus> inProgress;

    public List<NodeDiscoveryStatus> getCompleted()
    {
        return completed;
    }

    public void setCompleted( List<NodeDiscoveryStatus> completed )
    {
        this.completed = completed;
    }

    public List<NodeDiscoveryStatus> getInProgress()
    {
        return inProgress;
    }

    public void setInProgress( List<NodeDiscoveryStatus> inProgress )
    {
        this.inProgress = inProgress;
    }
}
