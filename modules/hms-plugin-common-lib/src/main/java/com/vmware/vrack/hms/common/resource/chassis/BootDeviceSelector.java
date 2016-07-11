/* ********************************************************************************
 * BootDeviceSelector.java
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
package com.vmware.vrack.hms.common.resource.chassis;

/**
 * Enum for Boot Device Selector in Get System Boot Options
 * 
 * @author VMware, Inc.
 */
public enum BootDeviceSelector
{
    No_Override( (byte) 0x00 ),
    PXE( (byte) 0x04 ),
    Default_Hard_Disk( (byte) 0x08 ),
    Default_Hard_Disk_Safe_Mode( (byte) 0x0C ),
    Boot_From_Remotely_Connected_Hard_Disk( (byte) 0x2C );
    private byte code;

    private final static byte MASK_BITS = 0x3C;

    private BootDeviceSelector( byte code )
    {
        this.code = code;
    }

    public byte getCode()
    {
        return this.code;
    }

    public static BootDeviceSelector getBootDeviceSelector( byte inputCode )
    {
        if ( ( TypeConverter.intToByte( MASK_BITS ) & inputCode ) == PXE.getCode() )
        {
            return PXE;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS ) & inputCode ) == Default_Hard_Disk.getCode() )
        {
            return Default_Hard_Disk;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS ) & inputCode ) == Default_Hard_Disk_Safe_Mode.getCode() )
        {
            return Default_Hard_Disk_Safe_Mode;
        }
        else if ( ( TypeConverter.intToByte( MASK_BITS )
            & inputCode ) == Boot_From_Remotely_Connected_Hard_Disk.getCode() )
        {
            return Boot_From_Remotely_Connected_Hard_Disk;
        }
        else
        {
            return No_Override;
        }
    }
}
