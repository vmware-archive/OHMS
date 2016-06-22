/* ********************************************************************************
 * HmsComponentUpgradePlugin.java
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
 * Every LifeCycleComponentManager implementation that supports upgrade must annotate its class with the
 * HmsComponentUpgradePlugin annotation.
 *
 * @author VMware, Inc.
 */
@Documented
@Target( ElementType.TYPE )
@Inherited
@Retention( RetentionPolicy.RUNTIME )
public @interface HmsComponentUpgradePlugin
{
    public SupportsUpgrade[] supportsUpgradeFor();
}
