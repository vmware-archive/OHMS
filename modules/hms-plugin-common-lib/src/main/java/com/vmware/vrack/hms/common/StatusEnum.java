/* ********************************************************************************
 * StatusEnum.java
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

package com.vmware.vrack.hms.common;

public enum StatusEnum
{

    UNKNOWNSTATE( StatusEnum._UNKNOWN_STATE ),
    OK( StatusEnum._OK ),
    ERROR( StatusEnum._ERROR ),
    OFF( StatusEnum._OFF ),
    QUIESCED( StatusEnum._QUIESCED ),
    DEGRADED( StatusEnum._DEGRADED ),
    LOSTCOMMUNICATION( StatusEnum._LOST_COMMUNICATION ),
    TIMEOUT( StatusEnum._TIMEOUT ),
    UNKNOWN( StatusEnum._UNKNOWN ),
    FAILURE( StatusEnum._FAILURE ),
    DEASSERTED( StatusEnum._DEASSERTED ),
    POST_FAILURE( StatusEnum._POST_FAILURE ),
    POWER_UP( StatusEnum._POWER_UP ),
    POWER_DOWN( StatusEnum._POWER_DOWN );

    private static final String _UNKNOWN_STATE = "unknownstate";

    private static final String _OK = "ok";

    private static final String _ERROR = "error";

    private static final String _OFF = "off";

    private static final String _QUIESCED = "quiesced";

    private static final String _DEGRADED = "degraded";

    private static final String _LOST_COMMUNICATION = "lostcommunication";

    private static final String _TIMEOUT = "timeout";

    private static final String _UNKNOWN = "unknown";

    private static final String _FAILURE = "failure";

    private static final String _DEASSERTED = "deasserted";

    private static final String _POST_FAILURE = "post_failure";

    private static final String _POWER_UP = "power_up";

    private static final String _POWER_DOWN = "power_down";

    private String code;

    StatusEnum( String code )
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public static StatusEnum getHddState( String value )
    {
        if ( value != null )
        {
            switch ( value.toLowerCase() )
            {
                case _UNKNOWN_STATE:
                    return UNKNOWNSTATE;
                case _OK:
                    return OK;
                case _ERROR:
                    return ERROR;
                case _OFF:
                    return OFF;
                case _QUIESCED:
                    return QUIESCED;
                case _DEGRADED:
                    return DEGRADED;
                case _LOST_COMMUNICATION:
                    return LOSTCOMMUNICATION;
                case _TIMEOUT:
                    return TIMEOUT;
                case _UNKNOWN:
                    return UNKNOWN;
                case _FAILURE:
                    return FAILURE;
                case _DEASSERTED:
                    return DEASSERTED;
                case _POST_FAILURE:
                    return POST_FAILURE;
                case _POWER_UP:
                    return POWER_UP;
                case _POWER_DOWN:
                    return POWER_DOWN;
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
