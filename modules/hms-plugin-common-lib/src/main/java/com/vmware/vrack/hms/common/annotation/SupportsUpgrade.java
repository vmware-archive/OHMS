/* ********************************************************************************
 * SupportsUpgrade.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Every HmsComponentUpgradePlugin annotation must use this SupportsUpgrade annotation to tell which boards and
 * hypervisor combination it supports.
 *
 * @author VMware, Inc.
 */
@Documented
@Target( ElementType.TYPE )
@Inherited
@Retention( RetentionPolicy.RUNTIME )
public @interface SupportsUpgrade
{
    public String boardManufacturer();

    public String boardModel();

    public String hypervisorName();

    public String hypervisorProvider();
}
