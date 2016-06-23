/* ********************************************************************************
 * SpringContextHelper.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * To get the spring application context
 */
@Component
public class SpringContextHelper
    implements ApplicationContextAware
{
    private static ApplicationContext context;

    @Override
    public void setApplicationContext( ApplicationContext context )
        throws BeansException
    {
        this.context = context;
    }

    public static ApplicationContext getApplicationContext()
    {
        return context;
    }
}
