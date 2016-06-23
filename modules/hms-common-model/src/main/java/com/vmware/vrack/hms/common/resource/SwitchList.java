/* ********************************************************************************
 * SwitchList.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource;

/**
 * @author ambi
 */
public class SwitchList
{
    private String managementIpAddress;

    private String name;

    private String type;

    public String getManagementIpAddress()
    {
        return managementIpAddress;
    }

    public void setManagementIpAddress( String managementIpAddress )
    {
        this.managementIpAddress = managementIpAddress;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }
}
