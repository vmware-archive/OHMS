/* ********************************************************************************
 * SwitchDataChangeMessage.java
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
