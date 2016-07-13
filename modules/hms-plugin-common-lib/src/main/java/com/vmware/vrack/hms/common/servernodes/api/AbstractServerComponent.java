/* ********************************************************************************
 * AbstractServerComponent.java
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

/**
 * @author VMware, Inc.
 */
public class AbstractServerComponent
{
    /**
     * @String id to hold the id for a component, which should be uniquely identify component among similar type
     *         components Example On a server with 2 CPU, CPU_0 can be identifier for one CPU
     */
    protected String id;

    /**
     * @ServerComponent to hold the type of a component ServerComponnet defines various component HMS has to retrieve
     *                  data for
     */
    protected ServerComponent component;

    /**
     * @ComponentIdentifier - FRU component Identifier
     */
    protected ComponentIdentifier componentIdentifier;

    /**
     * @location - server component location
     */
    protected String location;

    /**
     * @List<ServerComponentSensor> componentSensors will hold all the sensors available for the component Example CPU_0
     *                              can hold list of sensors [CPU_TEMP,CPU_THERMAL_TRIP,CPU_VOLTS.. etc]
     */
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public ServerComponent getComponent()
    {
        return component;
    }

    public void setComponent( ServerComponent component )
    {
        this.component = component;
    }

    public ComponentIdentifier getComponentIdentifier()
    {
        return componentIdentifier;
    }

    public void setComponentIdentifier( ComponentIdentifier componentIdentifier )
    {
        this.componentIdentifier = componentIdentifier;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation( String location )
    {
        this.location = location;
    }

    @Override
    public int hashCode()
    {
        int hash = 1;
        if ( id != null )
        {
            hash = 31 * hash + id.hashCode();
        }
        if ( component != null )
        {
            hash = 31 * hash + component.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals( Object object )
    {
        boolean result = false;
        if ( object == null || !( object.getClass().equals( getClass() ) ) )
        {
            result = false;
        }
        else
        {
            AbstractServerComponent info = (AbstractServerComponent) object;
            if ( ( this.getId() != null && this.getId().equals( info.getId() ) ) )
            {
                if ( this.getComponent() == info.getComponent() )
                {
                    result = true;
                }
            }
        }
        return result;
    }
}
