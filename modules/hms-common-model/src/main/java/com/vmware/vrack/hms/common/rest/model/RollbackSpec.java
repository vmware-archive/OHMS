/* ********************************************************************************
 * RollbackSpec.java
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
