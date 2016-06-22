/* ********************************************************************************
 * ServerNodePowerStatus.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties( ignoreUnknown = true )
public class ServerNodePowerStatus
{
    private boolean isPowered = false;

    private boolean isDiscoverable = false;

    private String operationalStatus = "false";

    public boolean isDiscoverable()
    {
        return isDiscoverable;
    }

    public void setDiscoverable( boolean isDiscoverable )
    {
        this.isDiscoverable = isDiscoverable;
    }

    public boolean isPowered()
    {
        return isPowered;
    }

    public void setPowered( boolean isPowered )
    {
        this.isPowered = isPowered;
    }

    public String getOperationalStatus()
    {
        return operationalStatus;
    }

    public void setOperationalStatus( String operationalStatus )
    {
        this.operationalStatus = operationalStatus;
    }
}
