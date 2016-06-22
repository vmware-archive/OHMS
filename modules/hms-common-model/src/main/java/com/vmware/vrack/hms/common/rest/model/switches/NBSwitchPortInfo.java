/* ********************************************************************************
 * NBSwitchPortInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model.switches;

import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;

public class NBSwitchPortInfo
{
    private String name;

    private String macAddress;

    private NBSwitchPortConfig config;

    private FruOperationalStatus operationalStatus;

    private NodeAdminStatus adminStatus;

    private NBSwitchPortStats stats;

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
    public NBSwitchPortConfig getConfig()
    {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig( NBSwitchPortConfig config )
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
    public NBSwitchPortStats getStats()
    {
        return stats;
    }

    /**
     * @param stats the stats to set
     */
    public void setStats( NBSwitchPortStats stats )
    {
        this.stats = stats;
    }

    /**
     * @return the status
     */
    public NodeAdminStatus getAdminStatus()
    {
        return adminStatus;
    }

    /**
     * @param status the status to set
     */
    public void setAdminStatus( NodeAdminStatus status )
    {
        this.adminStatus = status;
    }
}
