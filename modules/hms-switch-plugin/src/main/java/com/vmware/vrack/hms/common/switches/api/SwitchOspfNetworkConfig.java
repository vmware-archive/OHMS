/* ********************************************************************************
 * SwitchOspfNetworkConfig.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.api;

public class SwitchOspfNetworkConfig
{
    public String getArea()
    {
        return area;
    }

    public String getNetwork()
    {
        return network;
    }

    public void setArea( String area )
    {
        this.area = area;
    }

    public void setNetwork( String network )
    {
        this.network = network;
    }

    private String area;

    private String network;
}
