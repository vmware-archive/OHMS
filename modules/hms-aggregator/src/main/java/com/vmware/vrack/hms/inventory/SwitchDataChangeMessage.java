/* ********************************************************************************
 * SwitchDataChangeMessage.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.inventory;

import org.springframework.context.ApplicationEvent;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;

/**
 * Switch Data Change Massager
 */
public class SwitchDataChangeMessage
    extends ApplicationEvent
{
    private NBSwitchInfo switchInfo;

    SwitchComponentEnum component;

    /**
     * @param source
     * @param component
     */
    public SwitchDataChangeMessage( NBSwitchInfo source, SwitchComponentEnum component )
    {
        super( source );
        this.setSwitchInfo( source );
        this.component = component;
    }

    /**
     * Gets Switch Info
     *
     * @return SwitchInfo
     */
    public NBSwitchInfo getSwitchInfo()
    {
        return switchInfo;
    }

    /**
     * Set Switch Info
     *
     * @param switchInfo
     */
    public void setSwitchInfo( NBSwitchInfo switchInfo )
    {
        this.switchInfo = switchInfo;
    }

    /**
     * Get the Server component
     *
     * @return ServerComponent
     */
    public SwitchComponentEnum getComponent()
    {
        return component;
    }

    /**
     * Set Server component
     *
     * @param component
     */
    public void setComponent( SwitchComponentEnum component )
    {
        this.component = component;
    }
}
