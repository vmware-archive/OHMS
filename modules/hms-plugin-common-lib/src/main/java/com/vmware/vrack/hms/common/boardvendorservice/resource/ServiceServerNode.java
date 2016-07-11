/* ********************************************************************************
 * ServiceServerNode.java
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
package com.vmware.vrack.hms.common.boardvendorservice.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ServiceServerNode
    extends ServiceHmsNode
{
    private String ibIpAddress = "0.0.0.0";

    private String osUserName;

    private String osPassword;

    private int sshPort = 22;

    private String boardProductName;

    private String boardVendor;

    public String getIbIpAddress()
    {
        return ibIpAddress;
    }

    public void setIbIpAddress( String ibIpAddress )
    {
        this.ibIpAddress = ibIpAddress;
    }

    public String getOsUserName()
    {
        return osUserName;
    }

    public void setOsUserName( String osUserName )
    {
        this.osUserName = osUserName;
    }

    public String getOsPassword()
    {
        return osPassword;
    }

    public void setOsPassword( String osPassword )
    {
        this.osPassword = osPassword;
    }

    public String getBoardProductName()
    {
        return boardProductName;
    }

    public void setBoardProductName( String boardProductName )
    {
        this.boardProductName = boardProductName;
    }

    public String getBoardVendor()
    {
        return boardVendor;
    }

    public void setBoardVendor( String boardVendor )
    {
        this.boardVendor = boardVendor;
    }

    public int getSshPort()
    {
        return sshPort;
    }

    public void setSshPort( int sshPort )
    {
        this.sshPort = sshPort;
    }
}
