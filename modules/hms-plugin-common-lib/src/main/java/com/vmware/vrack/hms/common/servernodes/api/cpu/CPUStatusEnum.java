/* ********************************************************************************
 * CPUStatusEnum.java
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
package com.vmware.vrack.hms.common.servernodes.api.cpu;

/**
 * ENUM for CPU status...
 * 
 * @author VMware Inc.
 */
public enum CPUStatusEnum
{

    /**
     * OK - The CPU Presence detected/CPU Enabled CPU is operating within normal operational parameters and without
     * error
     */
    OK( CPUStatusEnum._OK ),

    /**
     * CPU is Disabled
     */
    DISABLED( CPUStatusEnum._DISABLED ),

    /**
     * Degraded - The CPU is in working order and all functionality is provided. However, the CPU is not working to the
     * best of its abilities.
     */
    DEGRADED( CPUStatusEnum._DEGRADED ),

    /**
     * Configuration Error/Correctable Machine Check Error
     */
    ERROR( CPUStatusEnum._ERROR ),

    /**
     * POST failure/Startup/Initialization failure/BIST failure The CPU is non-functional and recovery might not be
     * possible
     */
    FAILURE( CPUStatusEnum._FAILURE ),

    /**
     * Uncorrectable CPU-complex Error/Machine Check Exception (Uncorrectable) The CPU has completely failed, and
     * recovery is not possible; completely non-functional
     */
    UNCORRECTABLE_ERROR( CPUStatusEnum._UNCORRECTABLE_ERROR ),

    /**
     * Unknown - The implementation cannot report on HealthState at this time
     */
    UNKNOWN( CPUStatusEnum._UNKNOWN );

    private static final String _OK = "ok";

    private static final String _DISABLED = "disabled";

    private static final String _FAILURE = "failure";

    private static final String _DEGRADED = "degraded";

    private static final String _ERROR = "error";

    private static final String _UNCORRECTABLE_ERROR = "UncorrectableError";

    private static final String _UNKNOWN = "unknown";

    private String code;

    CPUStatusEnum( String code )
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public static CPUStatusEnum getCpuStatus( String value )
    {
        if ( value != null )
        {
            switch ( value )
            {
                case _OK:
                    return OK;
                case _DISABLED:
                    return DISABLED;
                case _FAILURE:
                    return FAILURE;
                case _DEGRADED:
                    return DEGRADED;
                case _ERROR:
                    return ERROR;
                case _UNCORRECTABLE_ERROR:
                    return UNCORRECTABLE_ERROR;
                case _UNKNOWN:
                    return UNKNOWN;
                default:
                    throw new IllegalArgumentException( "Invalid value: " + value );
            }
        }
        else
        {
            throw new IllegalArgumentException( "Invalid value: " + value );
        }
    }

}
