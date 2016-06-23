/* ********************************************************************************
 * Repeat.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.plugin.testlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface for Repeat annotation for tests
 *
 * @author VMware Inc.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD } )
public @interface Repeat
{
    int retryCount() default 3;

    long sleepTime() default 3000;
}
