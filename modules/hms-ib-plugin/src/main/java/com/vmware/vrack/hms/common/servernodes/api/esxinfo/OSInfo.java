/* ********************************************************************************
 * OSInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api.esxinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * wrapper class for hypervisor related Properties
 *
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class OSInfo
{
    private String version;

    private String build;

    private String productName;

    private String vendor;

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getBuild()
    {
        return build;
    }

    public void setBuild( String build )
    {
        this.build = build;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName( String productName )
    {
        this.productName = productName;
    }

    public String getVendor()
    {
        return vendor;
    }

    public void setVendor( String vendor )
    {
        this.vendor = vendor;
    }
}
