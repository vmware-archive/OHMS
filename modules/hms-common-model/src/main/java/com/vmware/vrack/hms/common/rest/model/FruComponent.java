/* ********************************************************************************
 * FruComponent.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model;

import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;

/**
 * Class for FRU component identifiers All the Server component (CPU/MEMORY/HDD/NIC) will have FRU component Identifies
 *
 * @author VMware Inc.
 */
public class FruComponent
{
    private String fruId;

    private ComponentIdentifier componentIdentifier;

    private String location;

    public String getLocation()
    {
        return location;
    }

    public void setLocation( String location )
    {
        this.location = location;
    }

    public ComponentIdentifier getComponentIdentifier()
    {
        return componentIdentifier;
    }

    public void setComponentIdentifier( ComponentIdentifier componentIdentifier )
    {
        this.componentIdentifier = componentIdentifier;
    }

    public String getFruId()
    {
        return fruId;
    }

    public void setFruId( String fruId )
    {
        this.fruId = fruId;
    }
}
