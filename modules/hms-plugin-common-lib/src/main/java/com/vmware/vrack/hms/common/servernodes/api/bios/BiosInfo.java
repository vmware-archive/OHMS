/* ********************************************************************************
 * BiosInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api.bios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * wrapper class for Bios related Properties
 *
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class BiosInfo
{
    private String biosVersion;

    private String biosReleaseDate;

    public String getBiosVersion()
    {
        return biosVersion;
    }

    public void setBiosVersion( String biosVersion )
    {
        this.biosVersion = biosVersion;
    }

    public String getBiosReleaseDate()
    {
        return biosReleaseDate;
    }

    public void setBiosReleaseDate( String biosReleaseDate )
    {
        this.biosReleaseDate = biosReleaseDate;
    }
}
