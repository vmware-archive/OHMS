/* ********************************************************************************
 * AggregatorTestUtil.java
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.vmware.vrack.hms.common.exception.HmsException;

/**
 * @author spolepalli
 */
public class AggregatorTestUtil
{

    public static void initSelf( Object mock, Class<?> clazz, String field )
        throws HmsException
    {
        Field fields;
        try
        {
            fields = clazz.getDeclaredField( field );
        }
        catch ( NoSuchFieldException | SecurityException e )
        {
            throw new HmsException( e );
        }
        setFinalStatic( fields, mock );
    }

    private static void setFinalStatic( Field field, Object newValue )
        throws HmsException
    {
        field.setAccessible( true );

        Field modifiersField;
        try
        {
            modifiersField = Field.class.getDeclaredField( "modifiers" );
            modifiersField.setAccessible( true );
            modifiersField.setInt( field, field.getModifiers() & ~Modifier.FINAL );

            field.set( null, newValue );
        }
        catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e )
        {
            throw new HmsException( e );
        }

    }

}
