/* ********************************************************************************
 * NBSwitchNtpConfig.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model.switches;

public class NBSwitchNtpConfig
{
    private String timeServerIpAddress;

    /**
     * @return the timeServerIpAddress
     */
    public String getTimeServerIpAddress()
    {
        return timeServerIpAddress;
    }

    /**
     * @param timeServerIpAddress the timeServerIpAddress to set
     */
    public void setTimeServerIpAddress( String timeServerIpAddress )
    {
        this.timeServerIpAddress = timeServerIpAddress;
    }
}
