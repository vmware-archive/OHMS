/* ********************************************************************************
 * EventDirection.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
