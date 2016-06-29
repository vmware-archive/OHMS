/* ********************************************************************************
 * ServiceHmsNode.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.resource;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author VMware, Inc.
 */
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ServiceHmsNode
{
    private String nodeID = "";

    private String managementIp = "0.0.0.0";

    private String managementUserName = null;

    private String managementUserPassword = null;

    private String oobMacAddress = "";

    private String uuid = "";

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid( String uuid )
    {
        this.uuid = uuid;
    }

    public String getNodeID()
    {
        return nodeID;
    }

    public void setNodeID( String nodeID )
    {
        this.nodeID = nodeID;
    }

    public String getManagementIp()
    {
        return managementIp;
    }

    public void setManagementIp( String managementIp )
    {
        this.managementIp = managementIp;
    }

    public String getManagementUserName()
    {
        return managementUserName;
    }

    public void setManagementUserName( String managementUserName )
    {
        this.managementUserName = managementUserName;
    }

    public String getManagementUserPassword()
    {
        return managementUserPassword;
    }

    public void setManagementUserPassword( String managementUserPassword )
    {
        this.managementUserPassword = managementUserPassword;
    }

    public String getOobMacAddress()
    {
        return oobMacAddress;
    }

    public void setOobMacAddress( String oobMacAddress )
    {
        this.oobMacAddress = oobMacAddress;
    }
}
