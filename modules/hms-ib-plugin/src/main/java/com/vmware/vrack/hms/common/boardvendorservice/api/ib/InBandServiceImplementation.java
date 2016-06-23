/* ********************************************************************************
 * InBandServiceImplementation.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.api.ib;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Every InBand Service implementation must annotate its class with the InBandServiceImplementation annotation.
 * 
 * @author VMware, Inc.
 */
@Documented
@Target( ElementType.TYPE )
@Inherited
@Retention( RetentionPolicy.RUNTIME )
public @interface InBandServiceImplementation
{
    String name();
}
