/* ********************************************************************************
 * ServerComponentEvent.java
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
package com.vmware.vrack.hms.common.servernodes.api.event;

import java.util.HashMap;
import java.util.Map;

/**
 * @author VMware, Inc. Event to be associated with every server component
 */
public class ServerComponentEvent
{
    /** @EventUnitType unit - key to identify unit of the corresponding Value. */
    private EventUnitType unit;

    /** @float value - key to capture event Value, can hold float values for reading events. */
    private float value;

    /** @String key to capture event Value, can hold String values for discrete events. */
    private String discreteValue;

    /**
     * @String key to hold event id i.e this can be same for similar events overtime example: event id for CPU_0_Temp
     *         will be same every time event is created
     */
    private String evnetId;

    /** @NodeEvent eventName to hold name of corresponding event same is used to generate corresponding RACK events */
    private NodeEvent eventName;

    /** @String key to hold component id for which event is raised */
    private String componentId;

    public EventUnitType getUnit()
    {
        return unit;
    }

    public void setUnit( EventUnitType unit )
    {
        this.unit = unit;
    }

    public float getValue()
    {
        return value;
    }

    public void setValue( float value )
    {
        this.value = value;
    }

    public String getEventId()
    {
        return evnetId;
    }

    public void setEventId( String eventId )
    {
        this.evnetId = eventId;
    }

    public NodeEvent getEventName()
    {
        return eventName;
    }

    public void setEventName( NodeEvent eventName )
    {
        this.eventName = eventName;
    }

    public String getDiscreteValue()
    {
        return discreteValue;
    }

    public void setDiscreteValue( String discreteValue )
    {
        this.discreteValue = discreteValue;
    }

    public Map<String, String> toEventDataMap()
    {
        Map<String, String> data = new HashMap<String, String>();
        data.put( "eventId", evnetId );
        if ( eventName.getEventID() != null )
        {
            data.put( "eventName", eventName.getEventID().toString() );
        }
        if ( eventName.getValueType() == EventValueType.DISCRETE )
            data.put( "value", discreteValue );
        else if ( eventName.getValueType() == EventValueType.READING )
            data.put( "value", Float.toString( value ) );
        data.put( "unit", ( unit != null ) ? unit.toString() : EventUnitType.DISCRETE.toString() );
        return data;
    }

    public String getComponentId()
    {
        return componentId;
    }

    public void setComponentId( String componentId )
    {
        this.componentId = componentId;
    }
}
