/* ********************************************************************************
 * NBSwitchMcLagConfig.java
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
package com.vmware.vrack.hms.common.rest.model.switches;

public class NBSwitchMcLagConfig
{
    private String interfaceName;

    private String myIp;

    private String peerIp;

    private String netmask;

    private String systemId;

    /**
     * @return the interfaceName
     */
    public String getInterfaceName()
    {
        return interfaceName;
    }

    /**
     * @param interfaceName the interfaceName to set
     */
    public void setInterfaceName( String interfaceName )
    {
        this.interfaceName = interfaceName;
    }

    /**
     * @return the myIp
     */
    public String getMyIp()
    {
        return myIp;
    }

    /**
     * @param myIp the myIp to set
     */
    public void setMyIp( String myIp )
    {
        this.myIp = myIp;
    }

    /**
     * @return the peerIp
     */
    public String getPeerIp()
    {
        return peerIp;
    }

    /**
     * @param peerIp the peerIp to set
     */
    public void setPeerIp( String peerIp )
    {
        this.peerIp = peerIp;
    }

    /**
     * @return the netmask
     */
    public String getNetmask()
    {
        return netmask;
    }

    /**
     * @param netmask the netmask to set
     */
    public void setNetmask( String netmask )
    {
        this.netmask = netmask;
    }

    /**
     * @return the systemId
     */
    public String getSystemId()
    {
        return systemId;
    }

    /**
     * @param systemId the systemId to set
     */
    public void setSystemId( String systemId )
    {
        this.systemId = systemId;
    }
}
