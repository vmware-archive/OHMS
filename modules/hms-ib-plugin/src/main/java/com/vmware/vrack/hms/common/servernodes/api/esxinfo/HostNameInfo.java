/* ********************************************************************************
 * HostNameInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api.esxinfo;

public class HostNameInfo
{
    private String domainName;

    private String fullyQualifiedDomainName;

    private String hostName;

    public String getDomainName()
    {
        return domainName;
    }

    public void setDomainName( String domainName )
    {
        this.domainName = domainName;
    }

    public String getFullyQualifiedDomainName()
    {
        return fullyQualifiedDomainName;
    }

    public void setFullyQualifiedDomainName( String fullyQualifiedDomainName )
    {
        this.fullyQualifiedDomainName = fullyQualifiedDomainName;
    }

    public String getHostName()
    {
        return hostName;
    }

    public void setHostName( String hostName )
    {
        this.hostName = hostName;
    }
}
