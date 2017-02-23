/* ********************************************************************************
 * SpringContextHelper.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/

package com.vmware.vrack.hms.aggregator.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
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

    private static String ibInventoryLocaiton;

    @Override
    public void setApplicationContext( ApplicationContext context )
        throws BeansException
    {
        SpringContextHelper.context = context;
    }

    public static ApplicationContext getApplicationContext()
    {
        return context;
    }

    public static String getIbInventoryLocaiton()
    {
        return ibInventoryLocaiton;
    }

    @Value( "${hms.ib.inventory.location}" )
    public void setIbInventoryLocaiton( String ibInventoryLocaiton )
    {
        SpringContextHelper.ibInventoryLocaiton = ibInventoryLocaiton;
    }
}
