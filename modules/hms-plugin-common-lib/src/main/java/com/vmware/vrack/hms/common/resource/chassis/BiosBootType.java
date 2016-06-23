/* ********************************************************************************
 * BiosBootType.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource.chassis;

/**
 * Enum for Bios Boot Type
 * 
 * @author VMware, Inc.
 */
public enum BiosBootType
{
    Legacy( (byte) 0x00 ), EFI( (byte) 0x20 );
    private byte code;

    private BiosBootType( byte code )
    {
        this.code = code;
    }

    public byte getCode()
    {
        return this.code;
    }

    public static BiosBootType getBiosBootType( byte inputCode )
    {
        if ( ( inputCode & EFI.getCode() ) != 0 )
        {
            return EFI;
        }
        else
        {
            return Legacy;
        }
    }
}
