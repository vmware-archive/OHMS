/* ********************************************************************************
 * RollbackSpec.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <code>RollbackSpec</code><br>
 *
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class RollbackSpec
{
    /** The scripts location. */
    private String scriptsLocation;

    /** The HMS token. */
    private String id;

    /**
     * Gets the HMS token.
     *
     * @return the HMS token
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the HMS token.
     *
     * @param hmsToken the new HMS token
     */
    public void setId( String hmsToken )
    {
        this.id = hmsToken;
    }

    /**
     * Gets the scripts location.
     *
     * @return the scriptsLocation
     */
    public String getScriptsLocation()
    {
        return scriptsLocation;
    }

    /**
     * Sets the scripts location.
     *
     * @param scriptsLocation the scriptsLocation to set
     */
    public void setScriptsLocation( String scriptsLocation )
    {
        this.scriptsLocation = scriptsLocation;
    }
}
