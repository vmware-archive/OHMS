/* ********************************************************************************
 * AboutResponse.java
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
