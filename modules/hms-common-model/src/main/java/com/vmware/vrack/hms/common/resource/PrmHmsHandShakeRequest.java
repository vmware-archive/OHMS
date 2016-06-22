/* ********************************************************************************
 * PrmHmsHandShakeRequest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource;

public class PrmHmsHandShakeRequest
{
    String prmVersion;

    String timeStamp;

    public String getPrmVersion()
    {
        return prmVersion;
    }

    public void setPrmVersion( String prmVersion )
    {
        this.prmVersion = prmVersion;
    }

    public String getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp( String timeStamp )
    {
        this.timeStamp = timeStamp;
    }
}
