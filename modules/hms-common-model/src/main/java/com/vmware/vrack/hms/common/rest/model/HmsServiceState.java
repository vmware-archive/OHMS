/* ********************************************************************************
 * HmsServiceState.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model;

import com.vmware.vrack.hms.common.service.ServiceState;

/**
 * Class for HMS Service State
 *
 * @author VMware Inc.
 */
public class HmsServiceState
{
    ServiceState hmsServiceState;

    public ServiceState getHmsServiceState()
    {
        return hmsServiceState;
    }

    public void setHmsServiceState( ServiceState hmsServiceState )
    {
        this.hmsServiceState = hmsServiceState;
    }

    @Override
    public String toString()
    {
        return "HmsServiceState [hmsServiceState=" + hmsServiceState + "]";
    }
}
