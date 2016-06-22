/* ********************************************************************************
 * StatusCode.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common;

public enum StatusCode
{
    OK( 200 ), FAILED( -1 ), NOT_MODIFIED( 304 );
    private int value;

    private StatusCode( int value )
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
