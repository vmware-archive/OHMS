/* ********************************************************************************
 * SwitchPortInfo.java
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

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude( JsonInclude.Include.NON_NULL )
public class SwitchPortInfo
{
    private String speed;

    private String macAddress;

    private String componentId;

    private String type;

    private long mtu;

    private String adminStatus;

    private String operationalStatus;

    public String getMacAddress()
    {
        return macAddress;
    }

    public void setMacAddress( String macAddress )
    {
        this.macAddress = macAddress;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public long getMtu()
    {
        return mtu;
    }

    public void setMtu( long mtu )
    {
        this.mtu = mtu;
    }

    public String getSpeed()
    {
        return speed;
    }

    public void setSpeed( String speed )
    {
        this.speed = speed;
    }

    public String getComponentId()
    {
        return componentId;
    }

    public void setComponentId( String componentId )
    {
        this.componentId = componentId;
    }

    public String getOperationalStatus()
    {
        return operationalStatus;
    }

    public void setOperationalStatus( String operationalStatus )
    {
        this.operationalStatus = operationalStatus;
    }

    public String getAdminStatus()
    {
        return adminStatus;
    }

    public void setAdminStatus( String adminStatus )
    {
        this.adminStatus = adminStatus;
    }

}
