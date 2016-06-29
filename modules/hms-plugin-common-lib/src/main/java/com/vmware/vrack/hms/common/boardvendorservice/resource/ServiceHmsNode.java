/*
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
