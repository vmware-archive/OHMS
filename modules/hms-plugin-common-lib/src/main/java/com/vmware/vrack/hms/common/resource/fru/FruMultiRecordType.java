/* ********************************************************************************
 * FruMultiRecordType.java
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
package com.vmware.vrack.hms.common.resource.fru;

import org.apache.log4j.Logger;

/**
 * Identifies type of the information stored in FRU's MultiRecord Area entry.
 */
public enum FruMultiRecordType
{
    PowerSupplyInformation( FruMultiRecordType.POWERSUPPLYINFORMATION ),
    DcOutput( FruMultiRecordType.DCOUTPUT ),
    OemRecord( FruMultiRecordType.OEMRECORD ),
    DcLoad( FruMultiRecordType.DCLOAD ),
    ManagementAccessRecord( FruMultiRecordType.MANAGEMENTACCESSRECORD ),
    BaseCompatibilityRecord( FruMultiRecordType.BASECOMPATIBILITYRECORD ),
    ExtendedCompatibilityRecord( FruMultiRecordType.EXTENDEDCOMPATIBILITYRECORD ),
    Unspecified( FruMultiRecordType.UNSPECIFIED ),;
    private static final int POWERSUPPLYINFORMATION = 0;

    private static final int DCOUTPUT = 1;

    private static final int OEMRECORD = 192;

    private static final int DCLOAD = 2;

    private static final int MANAGEMENTACCESSRECORD = 3;

    private static final int BASECOMPATIBILITYRECORD = 4;

    private static final int EXTENDEDCOMPATIBILITYRECORD = 5;

    private static final int UNSPECIFIED = -1;

    private int code;

    private static Logger logger = Logger.getLogger( FruMultiRecordType.class );

    FruMultiRecordType( int code )
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public static FruMultiRecordType parseInt( int value )
    {
        if ( value >= OEMRECORD )
        {
            return OemRecord;
        }
        switch ( value )
        {
            case POWERSUPPLYINFORMATION:
                return PowerSupplyInformation;
            case DCOUTPUT:
                return DcOutput;
            case DCLOAD:
                return DcLoad;
            case MANAGEMENTACCESSRECORD:
                return ManagementAccessRecord;
            case BASECOMPATIBILITYRECORD:
                return BaseCompatibilityRecord;
            case EXTENDEDCOMPATIBILITYRECORD:
                return ExtendedCompatibilityRecord;
            default:
                logger.error( "Invalid value: " + value );
                return Unspecified;
        }
    }
}
