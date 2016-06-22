/* ********************************************************************************
 * PowerOperationAction.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource;

public enum PowerOperationAction
{
    POWERUP( "power_up" ),
    POWERDOWN( "power_down" ),
    POWERCYCLE( "power_cycle" ),
    HARDRESET( "hard_reset" ),
    COLDRESET( "cold_reset" );
    private String powerActionString;

    private PowerOperationAction( String powerActionString )
    {
        this.powerActionString = powerActionString;
    }

    public String getPowerActionString()
    {
        return this.powerActionString;
    }
}
