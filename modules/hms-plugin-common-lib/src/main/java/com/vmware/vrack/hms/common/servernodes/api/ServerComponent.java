/* ********************************************************************************
 * ServerComponent.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api;

import com.vmware.vrack.common.event.enums.EventComponent;

/**
 * @author VMware, Inc. ENUM to list various HW components available on the board
 */
public enum ServerComponent
{
    CPU( EventComponent.CPU, HmsApi.CPU_INFO, HmsApi.CPU_SENSOR_INFO ),
    MEMORY( EventComponent.MEMORY, HmsApi.MEMORY_INFO, HmsApi.MEMORY_SENSOR_INFO ),
    POWERUNIT( EventComponent.POWER_UNIT, HmsApi.POWERUNIT_INFO, HmsApi.POWERUNIT_SENSOR_INFO ),
    STORAGE( EventComponent.STORAGE, HmsApi.STORAGE_INFO, HmsApi.STORAGE_SENSOR_INFO ),
    FAN( EventComponent.FAN, HmsApi.FAN_INFO, HmsApi.FAN_SENSOR_INFO ),
    NIC( EventComponent.NIC, HmsApi.NIC_INFO, HmsApi.NIC_SENSOR_INFO ),
    STORAGE_CONTROLLER( EventComponent.STORAGE_CONTROLLER, HmsApi.STORAGE_CONTROLLER_INFO,
        HmsApi.STORAGE_CONTROLLER_SENSOR_INFO ),
    BMC( EventComponent.BMC, HmsApi.SYSTEM_INFO, HmsApi.SYSTEM_SENSOR_INFO ),
    SYSTEM( EventComponent.SYSTEM, HmsApi.SYSTEM_INFO, HmsApi.SYSTEM_SENSOR_INFO ),
    SERVER( EventComponent.SERVER, HmsApi.SYSTEM_INFO, HmsApi.SYSTEM_SENSOR_INFO ),
    HMS( EventComponent.HMS ),
    OPERATING_SYSTEM( EventComponent.OPERATING_SYSTEM ),
    BIOS( EventComponent.BIOS ),
    RACK( EventComponent.RACK );
    private EventComponent source;

    private HmsApi infoAPI;

    private HmsApi sensorAPI;

    private ServerComponent( EventComponent source )
    {
        this.source = source;
    }

    private ServerComponent( EventComponent source, HmsApi infoAPI, HmsApi sensorAPI )
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
