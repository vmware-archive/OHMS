/* ********************************************************************************
 * SelRecordType.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource.sel;

/**
 * @author VMware, Inc.
 */
public enum SelRecordType
{
    OemTimestamped( SelRecordType.OEMTIMESTAMPED ),
    System( SelRecordType.SYSTEM ),
    OemNonTimestamped( SelRecordType.OEMNONTIMESTAMPED ),;
    /**
     * Represents OEM timestamped record type (C0h-DFh)
     */
    private static final int OEMTIMESTAMPED = 192;

    private static final int SYSTEM = 2;

    /**
     * Represents NON OEM timestamped record type (E0h-FFh)
     */
    private static final int OEMNONTIMESTAMPED = 224;

    private int code;

    SelRecordType( int code )
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public static SelRecordType parseInt( int value )
    {
        if ( value == SYSTEM )
        {
            return System;
        }
        if ( value >= OEMNONTIMESTAMPED )
        {
            return OemNonTimestamped;
        }
        if ( value >= OEMTIMESTAMPED )
        {
            return OemTimestamped;
        }
        throw new IllegalArgumentException( "Invalid value: " + value );
    }
}
