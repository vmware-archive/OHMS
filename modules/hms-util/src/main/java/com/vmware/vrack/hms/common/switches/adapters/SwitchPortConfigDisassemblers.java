/* ********************************************************************************
 * SwitchPortConfigDisassemblers.java
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
package com.vmware.vrack.hms.common.switches.adapters;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchPortConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;

public class SwitchPortConfigDisassemblers
{
    public static SwitchPort fromSwitchPortConfig( NBSwitchPortConfig config )
    {
        SwitchPort lConfig = new SwitchPort();

        if ( config == null )
            return null;

        lConfig.setAutoneg( fromAutoNegMode( config.getAutoneg() ) );
        lConfig.setDuplex( fromDuplexMode( config.getDuplex() ) );
        lConfig.setMtu( config.getMtu() );
        lConfig.setSpeed( config.getSpeed() );
        lConfig.setType( fromType( config.getType() ) );
        lConfig.setIpAddress( SwitchNetworkPrefixDisassemblers.fromSwitchNetworkPrefix( config.getIpAddress() ) );

        return lConfig;
    }

    private static SwitchPort.PortAutoNegMode fromAutoNegMode( NBSwitchPortConfig.PortAutoNegMode mode )
    {
        SwitchPort.PortAutoNegMode lMode = null;

        if ( mode == null )
            return null;

        switch ( mode )
        {
            case OFF:
                lMode = SwitchPort.PortAutoNegMode.OFF;
                break;
            case ON:
                lMode = SwitchPort.PortAutoNegMode.ON;
                break;
            default:
                break;
        }

        return lMode;
    }

    private static SwitchPort.PortDuplexMode fromDuplexMode( NBSwitchPortConfig.PortDuplexMode mode )
    {
        SwitchPort.PortDuplexMode lMode = null;

        if ( mode == null )
            return null;

        switch ( mode )
        {
            case FULL:
                lMode = SwitchPort.PortDuplexMode.FULL;
                break;
            case HALF:
                lMode = SwitchPort.PortDuplexMode.HALF;
                break;
            default:
                break;
        }

        return lMode;
    }

    private static SwitchPort.PortType fromType( NBSwitchPortConfig.PortType type )
    {
        SwitchPort.PortType lType = null;

        if ( type == null )
            return null;

        switch ( type )
        {
            case EXTERNAL:
                lType = SwitchPort.PortType.EXTERNAL;
                break;
            case LOOPBACK:
                lType = SwitchPort.PortType.LOOPBACK;
                break;
            case MANAGEMENT:
                lType = SwitchPort.PortType.MANAGEMENT;
                break;
            case SERVER:
                lType = SwitchPort.PortType.SERVER;
                break;
            case SYNC:
                lType = SwitchPort.PortType.SYNC;
                break;
            case UPLINK:
                lType = SwitchPort.PortType.UPLINK;
                break;
            default:
                break;
        }

        return lType;
    }
}
