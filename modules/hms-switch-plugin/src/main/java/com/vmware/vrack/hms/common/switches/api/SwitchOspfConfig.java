/* ********************************************************************************
 * SwitchOspfConfig.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.api;

public class SwitchOspfConfig
{
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    public SwitchOspfGlobalConfig getGlobal()
    {
        return global;
    }

    public void setGlobal( SwitchOspfGlobalConfig global )
    {
        this.global = global;
    }

    private boolean enabled;

    private SwitchOspfGlobalConfig global;
}
