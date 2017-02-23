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
package com.vmware.vrack.hms.common.core.model.switches;

import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;

public class SwitchPortInfo
{

    private String name;

    private String macAddress;

    private SwitchPortConfig config;

    private FruOperationalStatus operationalStatus;

    private SwitchPortStats stats;

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * @return the macAddress
     */
    public String getMacAddress()
    {
        return macAddress;
    }

    /**
     * @param macAddress the macAddress to set
     */
    public void setMacAddress( String macAddress )
    {
        this.macAddress = macAddress;
    }

    /**
     * @return the config
     */
    public SwitchPortConfig getConfig()
    {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig( SwitchPortConfig config )
    {
        this.config = config;
    }

    /**
     * @return the operationalStatus
     */
    public FruOperationalStatus getOperationalStatus()
    {
        return operationalStatus;
    }

    /**
     * @param operationalStatus the operationalStatus to set
     */
    public void setOperationalStatus( FruOperationalStatus operationalStatus )
    {
        this.operationalStatus = operationalStatus;
    }

    /**
     * @return the stats
     */
    public SwitchPortStats getStats()
    {
        return stats;
    }

    /**
     * @param stats the stats to set
     */
    public void setStats( SwitchPortStats stats )
    {
        this.stats = stats;
    }
}
