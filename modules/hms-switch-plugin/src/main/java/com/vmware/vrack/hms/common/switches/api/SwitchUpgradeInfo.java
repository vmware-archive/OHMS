/* ********************************************************************************
 * SwitchUpgradeInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.api;

public class SwitchUpgradeInfo
{
    public String getPackageUrl()
    {
        return packageUrl;
    }

    public void setPackageUrl( String packageUrl )
    {
        this.packageUrl = packageUrl;
    }

    private String packageUrl;
}
