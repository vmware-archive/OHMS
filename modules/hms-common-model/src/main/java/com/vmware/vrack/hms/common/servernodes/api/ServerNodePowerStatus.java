/* ********************************************************************************
 * ServerNodePowerStatus.java
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties( ignoreUnknown = true )
public class ServerNodePowerStatus
{
    private boolean isPowered = false;

    private boolean isDiscoverable = false;

    private String operationalStatus = "false";

    public boolean isDiscoverable()
    {
        return isDiscoverable;
    }

    public void setDiscoverable( boolean isDiscoverable )
    {
        this.isDiscoverable = isDiscoverable;
    }

    public boolean isPowered()
    {
        return isPowered;
    }

    public void setPowered( boolean isPowered )
    {
        this.isPowered = isPowered;
    }

    public String getOperationalStatus()
    {
        return operationalStatus;
    }

    public void setOperationalStatus( String operationalStatus )
    {
        this.operationalStatus = operationalStatus;
    }
}
