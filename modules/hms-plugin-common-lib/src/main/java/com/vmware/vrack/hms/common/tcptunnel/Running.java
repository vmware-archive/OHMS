/* ********************************************************************************
 * Running.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.tcptunnel;

public class Running
{
    boolean running = false;

    public void setRunning( Boolean running )
    {
        this.running = running;
    }

    public Boolean isRunning()
    {
        return this.running;
    }
}
