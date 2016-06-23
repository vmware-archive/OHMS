/* ********************************************************************************
 * DiscoveryResult.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.notification;

public class DiscoveryResult
{
    private NodeDiscoveryResult hosts;

    private NodeDiscoveryResult switches;

    public NodeDiscoveryResult getHosts()
    {
        return hosts;
    }

    public void setHosts( NodeDiscoveryResult hosts )
    {
        this.hosts = hosts;
    }

    public NodeDiscoveryResult getSwitches()
    {
        return switches;
    }

    public void setSwitches( NodeDiscoveryResult switches )
    {
        this.switches = switches;
    }
}
