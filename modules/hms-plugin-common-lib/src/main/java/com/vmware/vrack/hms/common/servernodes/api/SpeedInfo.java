/* ********************************************************************************
 * SpeedInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api;

/**
 * Speed related Information
 * 
 * @author VMware, Inc.
 */
public class SpeedInfo
{
    private Long speed;

    private SpeedUnit unit;

    public SpeedInfo()
    {
    }

    public SpeedInfo( Long speed, SpeedUnit unit )
    {
        this.speed = speed;
        this.unit = unit;
    }

    public Long getSpeed()
    {
        return speed;
    }

    public void setSpeed( Long speed )
    {
        this.speed = speed;
    }

    public SpeedUnit getUnit()
    {
        return unit;
    }

    public void setUnit( SpeedUnit unit )
    {
        this.unit = unit;
    }
}
