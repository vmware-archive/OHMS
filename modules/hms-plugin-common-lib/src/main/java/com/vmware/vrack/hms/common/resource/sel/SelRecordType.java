/* ********************************************************************************
 * SelRecordType.java
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
