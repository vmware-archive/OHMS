/* ********************************************************************************
 * CPUStatusCIMEnum.java
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
package com.vmware.vrack.hms.boardservice.ib.api.cim;

/*
 * Enum for the Health and current status of the Processor. 
 */
public enum CPUStatusCIMEnum
{

    /**
     * CPU is Enabled
     */
    CPU_Enabled( CPUStatusCIMEnum.CPU_ENABLED ),

    /**
     * CPU Disabled by User
     */
    CPU_Disabled_By_User( CPUStatusCIMEnum.CPU_DISABLED_BY_USER ),

    /**
     * CPU Disabled By BIOS
     */
    CPU_Disabled_By_BIOS( CPUStatusCIMEnum.CPU_DISABLED_BY_BIOS ),

    /**
     * CPU is Idle
     */
    CPU_Is_Idle( CPUStatusCIMEnum.CPU_IS_IDLE ),

    /**
     * OK - The element is fully functional and is operating within normal operational parameters and without error
     */
    Ok( CPUStatusCIMEnum.OK ),

    /**
     * Unknown - The implementation cannot report on HealthState at this time
     */
    Unknown( CPUStatusCIMEnum.UNKNOWN ), Other( CPUStatusCIMEnum.OTHER ),

    /**
     * Degraded - The CPU is in working order and all functionality is provided
     */
    Degraded( CPUStatusCIMEnum.DEGRADED ),

    /**
     * Minor Failure - All functionality is available but some might be degraded
     */
    Minor_Failure( CPUStatusCIMEnum.MINOR_FAILURE ),

    /**
     * Major Failure -The CPU is failing. It is possible that some or all of the functionality of this component is
     * degraded or not working
     */
    Major_Failure( CPUStatusCIMEnum.MAJOR_FAILURE ),

    /**
     * Critical Failure - The CPU is non-functional and recovery might not be possible
     */
    Critical_Failure( CPUStatusCIMEnum.CRITICAL_FAILURE ),

    /**
     * Non-recoverable error - The CPU has completely failed, and recovery is not possible; completely non-functional
     */
    Non_Recoverable_Error( CPUStatusCIMEnum.NON_RECOVERABLE_ERROR );

    private static final int UNKNOWN = 0;

    private static final int CPU_ENABLED = 1;

    private static final int CPU_DISABLED_BY_USER = 2;

    private static final int CPU_DISABLED_BY_BIOS = 3;

    private static final int CPU_IS_IDLE = 4;

    private static final int OK = 5;

    private static final int OTHER = 7;

    private static final int DEGRADED = 10;

    private static final int MINOR_FAILURE = 15;

    private static final int MAJOR_FAILURE = 20;

    private static final int CRITICAL_FAILURE = 25;

    private static final int NON_RECOVERABLE_ERROR = 30;

    private int code;

    CPUStatusCIMEnum( int code )
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public static CPUStatusCIMEnum getCpuHealthState( int value )
    {
        if ( value >= 0 )
        {
            switch ( value )
            {
                case UNKNOWN:
                    return Unknown;
                case OK:
                    return Ok;
                case CPU_ENABLED:
                    return CPU_Enabled;
                case CPU_DISABLED_BY_USER:
                    return CPU_Disabled_By_User;
                case CPU_DISABLED_BY_BIOS:
                    return CPU_Disabled_By_BIOS;
                case CPU_IS_IDLE:
                    return CPU_Is_Idle;
                case OTHER:
                    return Other;
                case DEGRADED:
                    return Degraded;
                case MINOR_FAILURE:
                    return Minor_Failure;
                case MAJOR_FAILURE:
                    return Major_Failure;
                case CRITICAL_FAILURE:
                    return Critical_Failure;
                case NON_RECOVERABLE_ERROR:
                    return Non_Recoverable_Error;
                default:
                    return Unknown;
            }
        }
        else
        {
            return Unknown;
        }
    }

}