/* ********************************************************************************
 * ServerItem.java
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

package com.vmware.vrack.hms.common.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServerItemBoardInfo;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServerItemHypervisorInfo;

@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ServerItem
{
    private String id;

    private String oobProtocol;

    private String oobIpAddress;

    private Integer oobPort;

    private String oobUsername;

    private String oobPassword;

    private String ibProtocol;

    private String ibIpAddress;

    private Integer ibPort;

    private String ibUsername;

    private String ibPassword;

    private String location;

    private ServerItemBoardInfo boardInfo;

    private ServerItemHypervisorInfo hypervisorInfo;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getOobProtocol()
    {
        return oobProtocol;
    }

    public void setOobProtocol( String oobProtocol )
    {
        this.oobProtocol = oobProtocol;
    }

    public String getOobIpAddress()
    {
        return oobIpAddress;
    }

    public void setOobIpAddress( String oobIpAddress )
    {
        this.oobIpAddress = oobIpAddress;
    }

    public Integer getOobPort()
    {
        return oobPort;
    }

    public void setOobPort( Integer oobPort )
    {
        this.oobPort = oobPort;
    }

    public String getOobUsername()
    {
        return oobUsername;
    }

    public void setOobUsername( String oobUsername )
    {
        this.oobUsername = oobUsername;
    }

    public String getOobPassword()
    {
        return oobPassword;
    }

    public void setOobPassword( String oobPassword )
    {
        this.oobPassword = oobPassword;
    }

    public String getIbProtocol()
    {
        return ibProtocol;
    }

    public void setIbProtocol( String ibProtocol )
    {
        this.ibProtocol = ibProtocol;
    }

    public String getIbIpAddress()
    {
        return ibIpAddress;
    }

    public void setIbIpAddress( String ibIpAddress )
    {
        this.ibIpAddress = ibIpAddress;
    }

    public Integer getIbPort()
    {
        return ibPort;
    }

    public void setIbPort( Integer ibPort )
    {
        this.ibPort = ibPort;
    }

    public String getIbUsername()
    {
        return ibUsername;
    }

    public void setIbUsername( String ibUsername )
    {
        this.ibUsername = ibUsername;
    }

    public String getIbPassword()
    {
        return ibPassword;
    }

    public void setIbPassword( String ibPassword )
    {
        this.ibPassword = ibPassword;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation( String location )
    {
        this.location = location;
    }

    public ServerItemBoardInfo getBoardInfo()
    {
        return boardInfo;
    }

    public void setBoardInfo( ServerItemBoardInfo boardInfo )
    {
        this.boardInfo = boardInfo;
    }

    public ServerItemHypervisorInfo getHypervisorInfo()
    {
        return hypervisorInfo;
    }

    public void setHypervisorInfo( ServerItemHypervisorInfo hypervisorInfo )
    {
        this.hypervisorInfo = hypervisorInfo;
    }

}
