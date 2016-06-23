/* ********************************************************************************
 * NodeDiscoveryResponse.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.notification;

/**
 * Response that will be sent, when it enquires about the discovery statsus
 *
 * @author VMware Inc.
 */
public class NodeDiscoveryResponse
{
    private NodeActionStatus discoveryStatus;

    private DiscoveryResult result;

    public DiscoveryResult getResult()
    {
        return result;
    }

    public void setResult( DiscoveryResult result )
    {
        this.result = result;
    }

    public NodeActionStatus getDiscoveryStatus()
    {
        return discoveryStatus;
    }

    public void setDiscoveryStatus( NodeActionStatus discoveryStatus )
    {
        this.discoveryStatus = discoveryStatus;
    }
}
