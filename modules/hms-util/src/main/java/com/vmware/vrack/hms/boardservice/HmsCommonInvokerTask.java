/* ********************************************************************************
 * HmsCommonInvokerTask.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.boardservice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.api.IHmsComponentService;
import com.vmware.vrack.hms.common.exception.HmsException;

/**
 * @author VMware Inc.
 */
public class HmsCommonInvokerTask
    implements Callable<Object>
{
    private IHmsComponentService service;

    private String methodName;

    private Object[] methodArgs;

    public HmsCommonInvokerTask( IHmsComponentService service, String methodName, Object[] methodArgs )
    {
        this.service = service;
        this.methodName = methodName;
        this.methodArgs = methodArgs;
    }

    private static Logger logger = Logger.getLogger( HmsCommonInvokerTask.class );

    public Object call()
        throws Exception
    {
        return callMethod( service, methodName, methodArgs );
    }

    /**
     * Call the method on the object via reflection
     *
     * @param serviceObject
     * @param methodName
     * @param params
     * @return
     * @throws HmsException
     */
    public static Object callMethod( Object serviceObject, String methodName, Object[] params )
        throws HmsException
    {
        if ( serviceObject == null || methodName == null )
        {
            throw new HmsException( String.format( "Error in calling method[%s] on object[%s], Either of these is null",
                                                   methodName, serviceObject ) );
        }
        Class<?> cls = serviceObject.getClass();
        Method[] methods = cls.getMethods();
        Method toBeInvokedMethod = null;
        for ( Method method : methods )
        {
            if ( !methodName.equals( method.getName() ) )
            {
                continue;
            }
            Class<?>[] paramTypes = method.getParameterTypes();
            if ( ( params == null || params.length == 0 ) && ( paramTypes == null || paramTypes.length == 0 ) )
            {
                toBeInvokedMethod = method;
                break;
            }
            else if ( params == null || paramTypes == null || paramTypes.length != params.length )
            {
                continue;
            }
            boolean foundMethod = true;
            for ( int i = 0; i < params.length; ++i )
            {
                if ( !paramTypes[i].isAssignableFrom( params[i].getClass() ) )
                {
                    foundMethod = false;
                    break;
                }
            }
            if ( foundMethod )
            {
                toBeInvokedMethod = method;
            }
            else
            {
                continue;
            }
        }
        if ( toBeInvokedMethod != null )
        {
            try
            {
                return toBeInvokedMethod.invoke( serviceObject, params );
            }
            catch ( InvocationTargetException e )
            {
                String err = String.format( "Unable to invoke method[%s] on object[%s]", methodName,
                                            serviceObject.getClass().getName() );
                logger.error( err, e );
                Throwable cause = e.getCause();
                if ( cause instanceof HmsException )
                {
                    throw (HmsException) cause;
                }
                else
                {
                    throw new HmsException( err, e );
                }
            }
            catch ( Exception e )
            {
                String err = String.format( "Unable to invoke method[%s] on object[%s]", methodName,
                                            serviceObject.getClass().getName() );
                logger.error( err, e );
                throw new HmsException( err, e );
            }
        }
        throw new HmsException( String.format( "Could NOT call method[%s] on object[%s]", methodName,
                                               serviceObject.getClass().getName() ) );
    }
}
