/* ********************************************************************************
 * FruEventStateChangeMessage.java
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

import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;

/**
 * HMS FRU Event Data Change Massager
 */
public class FruEventStateChangeMessage
    extends ApplicationEvent
{

    private List<Event> listEvent;

    private ServerComponent component;

    private SwitchComponentEnum switchComponent;

    /**
     * @param listEvent
     */
    public FruEventStateChangeMessage( List<Event> listEvent )
    {
        super( listEvent );
        this.setListEvent( listEvent );
    }

    /**
     * @param listEvent
     * @param component
     */
    public FruEventStateChangeMessage( List<Event> listEvent, ServerComponent component )
    {
        super( listEvent );
        this.setListEvent( listEvent );
        this.component = component;
    }

    /**
     * @param listEvent
     * @param switchComponent
     */
    public FruEventStateChangeMessage( List<Event> listEvent, SwitchComponentEnum switchComponent )
    {
        super( listEvent );
        this.setListEvent( listEvent );
        this.switchComponent = switchComponent;
    }

    /**
     * Get event list
     *
     * @return List<Event>
     */
    public List<Event> getListEvent()
    {
        return listEvent;
    }

    /**
     * Set the event list
     *
     * @param listEvent
     */
    public void setListEvent( List<Event> listEvent )
    {
        this.listEvent = listEvent;
    }

    /**
     * Get Server component
     *
     * @return ServerComponent
     */
    public ServerComponent getComponent()
    {
        return component;
    }

    /**
     * Set event component
     *
     * @param component
     */
    public void setComponent( ServerComponent component )
    {
        this.component = component;
    }

    /**
     * Get Switch component
     *
     * @return ServerComponent
     */
    public SwitchComponentEnum getSwitchComponent()
    {
        return switchComponent;
    }

    /**
     * Set Switch component
     *
     * @param switchComponent
     */
    public void setSwitchComponent( SwitchComponentEnum switchComponent )
    {
        this.switchComponent = switchComponent;
    }

}
