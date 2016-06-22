/* ********************************************************************************
 * BoardServiceImplementation.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Every Board Service implementation must annotate its class with the BoardServiceImplementation annotation.
 *
 * @author VMware, Inc.
 */
@Documented
@Target( ElementType.TYPE )
@Inherited
@Retention( RetentionPolicy.RUNTIME )
public @interface BoardServiceImplementation
{
    String name();
}
