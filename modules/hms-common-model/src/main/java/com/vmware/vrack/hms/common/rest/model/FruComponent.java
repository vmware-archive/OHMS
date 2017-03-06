/* ********************************************************************************
 * FruComponent.java
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
package com.vmware.vrack.hms.common.rest.model;

import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;

/**
 * Class for FRU component identifiers All the Server component (CPU/MEMORY/HDD/NIC) will have FRU component Identifies
 * 
 * @author VMware Inc.
 */
public class FruComponent
{
    private String fruId;

    private ComponentIdentifier componentIdentifier;

    private String location;

    public String getLocation()
    {
        return location;
    }

    public void setLocation( String location )
    {
        this.location = location;
    }

    public ComponentIdentifier getComponentIdentifier()
    {
        return componentIdentifier;
    }

    public void setComponentIdentifier( ComponentIdentifier componentIdentifier )
    {
        this.componentIdentifier = componentIdentifier;
    }

    public String getFruId()
    {
        return fruId;
    }

    public void setFruId( String fruId )
    {
        this.fruId = fruId;
    }

}
