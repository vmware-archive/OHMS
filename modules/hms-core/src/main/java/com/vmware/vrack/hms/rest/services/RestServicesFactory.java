/* ********************************************************************************
 * RestServicesFactory.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.rest.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.vmware.vrack.hms.rest.exception.ExceptionHandler;
import com.vmware.vrack.hms.rest.exception.HMSRestExceptionHandler;

public class RestServicesFactory
    extends Application
{
    private static Set<Class<?>> classes = new HashSet<Class<?>>();

    public RestServicesFactory()
    {
        // initialize restful services
        classes.add( ServerRestService.class );
        classes.add( SwitchRestService.class );
        classes.add( HMSManagementRestService.class );
        classes.add( SubscriberRestService.class );
        classes.add( HMSRestExceptionHandler.class );
        classes.add( HmsServiceFilters.class );
        classes.add( InventoryRestService.class );
        classes.add( AboutRestService.class );
        classes.add( ExceptionHandler.class );
        classes.add( ComponentEventRestService.class );
        classes.add( UpgradeRestService.class );
    }

    @Override
    public Set<Class<?>> getClasses()
    {
        return classes;
    }
}
