/* ********************************************************************************
 * FruOperationalStatus.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource.fru;

/**
 * ENUM for FRU operational status
 */
public enum FruOperationalStatus
{
    /*
     * If the FRU is operational set the Operational status as "Operational" otherwise it's "Non Operational"
     */
    Operational, NonOperational, UnKnown;
}
