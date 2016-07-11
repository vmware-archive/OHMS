/* ********************************************************************************
 * EventDirection.java
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
package com.vmware.vrack.hms.common.resource.sel;

/**
 * @author VMware, Inc.
 */
public enum EventDirection
{
    Assertion( EventDirection.ASSERTION ), Deassertion( EventDirection.DEASSERTION ),;
    private static final int ASSERTION = 0;

    private static final int DEASSERTION = 1;

    private int code;

    EventDirection( int code )
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public static EventDirection parseInt( int value )
    {
        switch ( value )
        {
            case ASSERTION:
                return Assertion;
            case DEASSERTION:
                return Deassertion;
            default:
                throw new IllegalArgumentException( "Invalid value: " + value );
        }
    }
}
