/* ********************************************************************************
 * SwitchPortConfigAssemblers.java
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
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;

public final class SwitchPortConfigAssemblers
{
    public static NBSwitchPortConfig toSwitchPortConfig( SwitchPort config )
    {
        NBSwitchPortConfig lConfig = new NBSwitchPortConfig();
        if ( config == null )
            return null;
        lConfig.setAutoneg( toAutoNegMode( config.getAutoneg() ) );
        lConfig.setDuplex( toDuplexMode( config.getDuplex() ) );
        lConfig.setMtu( config.getMtu() );
        lConfig.setSpeed( config.getSpeed() );
        lConfig.setType( toType( config.getType() ) );
        lConfig.setIpAddress( SwitchNetworkPrefixAssemblers.toSwitchNetworkPrefix( config.getIpAddress() ) );
        return lConfig;
    }

    private static NBSwitchPortConfig.PortAutoNegMode toAutoNegMode( SwitchPort.PortAutoNegMode mode )
    {
        NBSwitchPortConfig.PortAutoNegMode lMode = null;
        if ( mode == null )
            return null;
        switch ( mode )
        {
            case OFF:
                lMode = NBSwitchPortConfig.PortAutoNegMode.OFF;
                break;
            case ON:
                lMode = NBSwitchPortConfig.PortAutoNegMode.ON;
                break;
            default:
                break;
        }
        return lMode;
    }

    private static NBSwitchPortConfig.PortDuplexMode toDuplexMode( SwitchPort.PortDuplexMode mode )
    {
        NBSwitchPortConfig.PortDuplexMode lMode = null;
        if ( mode == null )
            return null;
        switch ( mode )
        {
            case FULL:
                lMode = NBSwitchPortConfig.PortDuplexMode.FULL;
                break;
            case HALF:
                lMode = NBSwitchPortConfig.PortDuplexMode.HALF;
                break;
            default:
                break;
        }
        return lMode;
    }

    private static NBSwitchPortConfig.PortType toType( SwitchPort.PortType type )
    {
        NBSwitchPortConfig.PortType lType = null;
        if ( type == null )
            return null;
        switch ( type )
        {
            case EXTERNAL:
                lType = NBSwitchPortConfig.PortType.EXTERNAL;
                break;
            case LOOPBACK:
                lType = NBSwitchPortConfig.PortType.LOOPBACK;
                break;
            case MANAGEMENT:
                lType = NBSwitchPortConfig.PortType.MANAGEMENT;
                break;
            case SERVER:
                lType = NBSwitchPortConfig.PortType.SERVER;
                break;
            case SYNC:
                lType = NBSwitchPortConfig.PortType.SYNC;
                break;
            case UPLINK:
                lType = NBSwitchPortConfig.PortType.UPLINK;
                break;
            default:
                break;
        }
        return lType;
    }
}
