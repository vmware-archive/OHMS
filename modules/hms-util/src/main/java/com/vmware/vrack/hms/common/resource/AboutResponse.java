/* ********************************************************************************
 * AboutResponse.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude( JsonInclude.Include.NON_NULL )
public class AboutResponse
{
    private String buildVersion;

    private String buildDate;

    private String buildOwner;

    private String buildJdk;

    public String getBuildVersion()
    {
        return buildVersion;
    }

    public void setBuildVersion( String buildVersion )
    {
        this.buildVersion = buildVersion;
    }

    public String getBuildDate()
    {
        return buildDate;
    }

    public void setBuildDate( String buildDate )
    {
        this.buildDate = buildDate;
    }

    public String getBuildOwner()
    {
        return buildOwner;
    }

    public void setBuildOwner( String buildOwner )
    {
        this.buildOwner = buildOwner;
    }

    public String getBuildJdk()
    {
        return buildJdk;
    }

    public void setBuildJdk( String buildJdk )
    {
        this.buildJdk = buildJdk;
    }

    @JsonIgnore
    public boolean isValid()
    {
        if ( ( buildVersion != null && !buildVersion.isEmpty() ) || ( buildDate != null && !buildDate.isEmpty() )
            || ( buildOwner != null && !buildOwner.isEmpty() ) || ( buildJdk != null && !buildJdk.isEmpty() ) )
            return true;
        else
            return false;
    }
}
