/* ********************************************************************************
 * InbandProperties.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class that holds properties that are specific to hms-inband
 * 
 * @author Vmware
 */
@Component
public class InbandProperties
{
    private static int vsphereClientTimeoutInMs;

    public static int getVsphereClientTimeoutInMs()
    {
        return vsphereClientTimeoutInMs;
    }

    /**
     * Timeout for vsphere client to connect a host.
     * 
     * @param vsphereClientTimeoutInMs
     */
    @Value( "${vsphere.connection.timeout.ms:30000}" )
    public void setVsphereClientTimeoutInMs( int vsphereClientTimeoutInMs )
    {
        InbandProperties.vsphereClientTimeoutInMs = vsphereClientTimeoutInMs;
    }
}
