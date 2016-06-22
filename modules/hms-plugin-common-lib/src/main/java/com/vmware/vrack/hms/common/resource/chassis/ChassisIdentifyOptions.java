/* ********************************************************************************
 * ChassisIdentifyOptions.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource.chassis;

/**
 * Class to Hold Options for Chassis Identify Command
 * 
 * @author VMware, Inc.
 */
public class ChassisIdentifyOptions
{
    private Boolean identify;

    private Integer interval;

    private Boolean forceIdentifyChassis;

    public Integer getInterval()
    {
        return interval;
    }

    public void setInterval( Integer interval )
    {
        this.interval = interval;
    }

    public Boolean getForceIdentifyChassis()
    {
        return forceIdentifyChassis;
    }

    public void setForceIdentifyChassis( Boolean forceIdentifyChassis )
    {
        this.forceIdentifyChassis = forceIdentifyChassis;
    }

    public Boolean getIdentify()
    {
        return identify;
    }

    public void setIdentify( Boolean identify )
    {
        this.identify = identify;
    }
}
