/* ********************************************************************************
 * BootDeviceType.java
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
 * Enum for Boot Device Type
 * 
 * @author VMware, Inc.
 */
public enum BootDeviceType
{
    Internal( 1 ), External( 0 );
    private int code;

    private BootDeviceType( int code )
    {
        this.code = code;
    }

    public int getCode()
    {
        return this.code;
    }

    public static BootDeviceType getBootDeviceType( int code )
    {
        if ( code == 1 )
        {
            return Internal;
        }
        else
        {
            return External;
        }
    }
}
