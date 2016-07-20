/*******************************************************************************
 * Copyright (c) 2014 VMware, Inc. All rights reserved.
 ******************************************************************************/

package com.vmware.vrack.hms.common.switches.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * Every Switch implementation must annotate its class with the SwitchImplementation annotation.
 * The name is the name of the implementation, e.g. "cumulus", "arista", etc.
 * @author VMware, Inc.
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
public @interface SwitchServiceImplementation {
	String name();
}
