/* ********************************************************************************
 * ServerItemHypervisorInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.resource;

/**
 * This will hold the hypervisor Name and Vendor of the Node
 * 
 * @author VMware, Inc.
 */
public class ServerItemHypervisorInfo
{
    private String name;

    private String provider;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider( String provider )
    {
        this.provider = provider;
    }
}
