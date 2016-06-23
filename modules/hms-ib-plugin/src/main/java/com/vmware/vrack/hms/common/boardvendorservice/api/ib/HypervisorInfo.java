/* ********************************************************************************
 * HypervisorInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.api.ib;

public class HypervisorInfo
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
