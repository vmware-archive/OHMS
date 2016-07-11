/* ********************************************************************************
 * BootOptionsValidity.java
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
 * Enum for Validity of SystemBootOptions
 * 
 * @author VMware, Inc.
 */
public enum BootOptionsValidity
{
    NextBootOnly( (byte) 0x00 ), Persistent( (byte) 0x01 );
    private byte code;

    private BootOptionsValidity( byte code )
    {
        this.code = code;
    }

    public byte getCode()
    {
        return this.code;
    }

    public static BootOptionsValidity getBootOptionsValidity( byte inputCode )
    {
        if ( ( inputCode & Persistent.getCode() ) != 0 )
        {
            return Persistent;
        }
        else
        {
            return NextBootOnly;
        }
    }
}
