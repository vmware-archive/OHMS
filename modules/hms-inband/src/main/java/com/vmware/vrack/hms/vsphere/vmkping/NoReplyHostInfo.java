/* ********************************************************************************
 * NoReplyHostInfo.java
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

package com.vmware.vrack.hms.vsphere.vmkping;

import java.io.Serializable;

/**
 * Author: Tao Ma Date: 3/3/14
 */
public class NoReplyHostInfo
    implements Serializable
{
    /*
     * Re-factor hard coded string 2014-08-07
     */
    private static final String VMKNIC_IP_ADDR = ", vmknicIpAddr=";

    private static final String HOST_ID = ", hostId=";

    private static final String NO_REPLY_HOST_INFO_HOST_NAME = "NoReplyHostInfo [hostName=";

    private static final long serialVersionUID = 1L;

    private String hostName;

    private String hostId;

    private String vmknicIpAddr;

    /**
     * @return the hostName
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public void setHostName( String hostName )
    {
        this.hostName = hostName;
    }

    /**
     * @return the hostId
     */
    public String getHostId()
    {
        return hostId;
    }

    /**
     * @param hostId the hostId to set
     */
    public void setHostId( String hostId )
    {
        this.hostId = hostId;
    }

    /**
     * @return the vmknicIpAddr
     */
    public String getVmknicIpAddr()
    {
        return vmknicIpAddr;
    }

    /**
     * @param vmknicIpAddr the vmknicIpAddr to set
     */
    public void setVmknicIpAddr( String vmknicIpAddr )
    {
        this.vmknicIpAddr = vmknicIpAddr;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( NO_REPLY_HOST_INFO_HOST_NAME );
        builder.append( hostName );
        builder.append( HOST_ID );
        builder.append( hostId );
        builder.append( VMKNIC_IP_ADDR );
        builder.append( vmknicIpAddr );
        builder.append( "]" );
        return builder.toString();
    }
}