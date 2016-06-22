/* ********************************************************************************
 * SwitchVlanIgmp.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.api;

public class SwitchVlanIgmp
{
    private Boolean igmpQuerier;

    public Boolean getIgmpQuerier()
    {
        return igmpQuerier;
    }

    public void setIgmpQuerier( Boolean igmpQuerier )
    {
        this.igmpQuerier = igmpQuerier;
    }
}
