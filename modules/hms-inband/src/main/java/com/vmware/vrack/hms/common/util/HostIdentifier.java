/* ********************************************************************************
 * HostIdentifier.java
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

package com.vmware.vrack.hms.common.util;

/**
 * Class to be used for caching HostProxy in Inband implementation.
 * 
 * @author Vmware
 */
public class HostIdentifier
{
    private String nodeId;

    private String ibIpAddress;

    private String osUserName;

    private String osPassword;

    public HostIdentifier( String nodeId, String ibIpAddress, String osUserName, String osPassword )
    {
        this.nodeId = nodeId;
        this.ibIpAddress = ibIpAddress;
        this.osUserName = osUserName;
        this.osPassword = osPassword;
    }

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

    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeId( String nodeId )
    {
        this.nodeId = nodeId;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( ibIpAddress == null ) ? 0 : ibIpAddress.hashCode() );
        result = prime * result + ( ( nodeId == null ) ? 0 : nodeId.hashCode() );
        result = prime * result + ( ( osPassword == null ) ? 0 : osPassword.hashCode() );
        result = prime * result + ( ( osUserName == null ) ? 0 : osUserName.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        HostIdentifier other = (HostIdentifier) obj;
        if ( ibIpAddress == null )
        {
            if ( other.ibIpAddress != null )
                return false;
        }
        else if ( !ibIpAddress.equals( other.ibIpAddress ) )
            return false;
        if ( nodeId == null )
        {
            if ( other.nodeId != null )
                return false;
        }
        else if ( !nodeId.equals( other.nodeId ) )
            return false;
        if ( osPassword == null )
        {
            if ( other.osPassword != null )
                return false;
        }
        else if ( !osPassword.equals( other.osPassword ) )
            return false;
        if ( osUserName == null )
        {
            if ( other.osUserName != null )
                return false;
        }
        else if ( !osUserName.equals( other.osUserName ) )
            return false;
        return true;
    }

}
