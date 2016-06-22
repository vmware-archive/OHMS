/* ********************************************************************************
 * HmsUpgradeSpec.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
