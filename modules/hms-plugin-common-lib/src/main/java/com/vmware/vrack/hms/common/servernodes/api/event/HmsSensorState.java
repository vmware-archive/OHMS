/* ********************************************************************************
 * HmsSensorState.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api.event;

public enum HmsSensorState
{
    BelowLowerNonRecoverable( HmsSensorState.BELOWLOWERNONRECOVERABLE ),
    AboveUpperNonCritical( HmsSensorState.ABOVEUPPERNONCRITICAL ),
    AboveUpperNonRecoverable( HmsSensorState.ABOVEUPPERNONRECOVERABLE ),
    BelowLowerNonCritical( HmsSensorState.BELOWLOWERNONCRITICAL ),
    BelowLowerCritical( HmsSensorState.BELOWLOWERCRITICAL ),
    AboveUpperCritical( HmsSensorState.ABOVEUPPERCRITICAL ),
    Ok( HmsSensorState.OK ),
    Invalid( HmsSensorState.INVALID );
    private static final int BELOWLOWERNONRECOVERABLE = 4;

    private static final int ABOVEUPPERNONCRITICAL = 8;

    private static final int ABOVEUPPERNONRECOVERABLE = 32;

    private static final int BELOWLOWERNONCRITICAL = 1;

    private static final int BELOWLOWERCRITICAL = 2;

    private static final int ABOVEUPPERCRITICAL = 16;

    private static final int OK = 0;

    private static final int INVALID = -1;

    private int code;

    HmsSensorState( int code )
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public static HmsSensorState parseInt( int value )
    {
        if ( ( value & BELOWLOWERNONRECOVERABLE ) != 0 )
        {
            return BelowLowerNonRecoverable;
        }
        if ( ( value & BELOWLOWERCRITICAL ) != 0 )
        {
            return BelowLowerCritical;
        }
        if ( ( value & ABOVEUPPERNONCRITICAL ) != 0 )
        {
            return BelowLowerNonCritical;
        }
        if ( ( value & ABOVEUPPERNONRECOVERABLE ) != 0 )
        {
            return AboveUpperNonRecoverable;
        }
        if ( ( value & ABOVEUPPERCRITICAL ) != 0 )
        {
            return AboveUpperCritical;
        }
        if ( ( value & ABOVEUPPERNONCRITICAL ) != 0 )
        {
            return AboveUpperNonCritical;
        }
        if ( value == OK )
        {
            return Ok;
        }
        return Invalid;
    }
}
