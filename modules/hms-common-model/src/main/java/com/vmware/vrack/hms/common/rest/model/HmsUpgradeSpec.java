/* ********************************************************************************
 * HmsUpgradeSpec.java
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

/**
 * <code>UpgradeParameters</code> is the model used for building/consuming request body for the aggregator upgrade api.
 * <br>
 *
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class HmsUpgradeSpec
{

    /** Unique id for the upgrade. */
    private String id;

    /** The bundle path. */
    private String bundlePath;

    /** The checksum. */
    private String checksum;

    /**
     * Gets the bundle path.
     *
     * @return the bundle path
     */
    public String getBundlePath()
    {
        return bundlePath;
    }

    /**
     * Sets the bundle path.
     *
     * @param bundlePath the new bundle path
     */
    public void setBundlePath( String bundlePath )
    {
        this.bundlePath = bundlePath;
    }

    /**
     * Gets the checksum.
     *
     * @return the checksum
     */
    public String getChecksum()
    {
        return checksum;
    }

    /**
     * Sets the checksum.
     *
     * @param checksum the new checksum
     */
    public void setChecksum( String checksum )
    {
        this.checksum = checksum;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId( String id )
    {
        this.id = id;
    }
}
