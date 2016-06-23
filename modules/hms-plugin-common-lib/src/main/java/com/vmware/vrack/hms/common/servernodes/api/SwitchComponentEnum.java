/* ********************************************************************************
 * SwitchComponentEnum.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api;

import com.vmware.vrack.common.event.enums.EventComponent;

/**
 * ENUM to list various HW components available on the Switch
 */
public enum SwitchComponentEnum
{
    SWITCH( EventComponent.SWITCH ), SWITCH_PORT( EventComponent.SWITCH_PORT );
    private EventComponent source;

    private HmsApi infoAPI;

    private HmsApi sensorAPI;

    private SwitchComponentEnum( EventComponent source )
    {
        this.source = source;
    }

    private SwitchComponentEnum( EventComponent source, HmsApi infoAPI, HmsApi sensorAPI )
    {
        this.source = source;
        this.infoAPI = infoAPI;
        this.sensorAPI = sensorAPI;
    }

    public EventComponent getEventComponent()
    {
        return this.source;
    }

    public HmsApi getComponentInfoAPI()
    {
        return this.infoAPI;
    }

    public HmsApi getComponentSensorAPI()
    {
        return this.sensorAPI;
    }
}
