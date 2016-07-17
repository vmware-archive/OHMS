/* ********************************************************************************
 * SwitchComponentEnum.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
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
