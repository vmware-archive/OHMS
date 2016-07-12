/* ********************************************************************************
 * ComponentUpgradeConfiguration.java
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
package com.vmware.vrack.hms.common.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.component.lifecycle.resource.LowLevelComponent;

/**
 * The purpose of this class is for HMS Aggregator to pass the required configuration parameters to OOB Agent while
 * requesting for initiating a low level component upgrade.
 *
 * @author VMware Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ComponentUpgradeConfiguration
{
    /** The node id. */
    private String nodeId;

    /** The low level component. */
    private LowLevelComponent lowLevelComponent;

    /** The vendor. */
    private String vendor;

    /** The file. */
    private String file;

    /** The upgrade id. */
    private String upgradeId;

    /**
     * Gets the node id.
     *
     * @return the node id
     */
    public String getNodeId()
    {
        return nodeId;
    }

    /**
     * Sets the node id.
     *
     * @param nodeId the new node id
     */
    public void setNodeId( String nodeId )
    {
        this.nodeId = nodeId;
    }

    /**
     * Gets the low level component.
     *
     * @return the low level component
     */
    public LowLevelComponent getLowLevelComponent()
    {
        return lowLevelComponent;
    }

    /**
     * Sets the low level component.
     *
     * @param lowLevelComponent the new low level component
     */
    public void setLowLevelComponent( LowLevelComponent lowLevelComponent )
    {
        this.lowLevelComponent = lowLevelComponent;
    }

    /**
     * Gets the vendor.
     *
     * @return the vendor
     */
    public String getVendor()
    {
        return vendor;
    }

    /**
     * Sets the vendor.
     *
     * @param vendor the new vendor
     */
    public void setVendor( String vendor )
    {
        this.vendor = vendor;
    }

    /**
     * Gets the file.
     *
     * @return the file
     */
    public String getFile()
    {
        return file;
    }

    /**
     * Sets the file.
     *
     * @param file the new file
     */
    public void setFile( String file )
    {
        this.file = file;
    }

    /**
     * Gets the upgrade id.
     *
     * @return the upgrade id
     */
    public String getUpgradeId()
    {
        return upgradeId;
    }

    /**
     * Sets the upgrade id.
     *
     * @param upgradeId the new upgrade id
     */
    public void setUpgradeId( String upgradeId )
    {
        this.upgradeId = upgradeId;
    }
}
