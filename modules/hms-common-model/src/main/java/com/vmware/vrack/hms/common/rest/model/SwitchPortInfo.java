/* ********************************************************************************
 * SwitchPortInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
