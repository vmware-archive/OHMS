/* ********************************************************************************
 * SwitchVlanConfigDisassemblers.java
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

import java.util.HashSet;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchVlanConfig;
import com.vmware.vrack.hms.common.switches.IpUtils;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.switches.api.SwitchVlanIgmp;

public class SwitchVlanConfigDisassemblers
{
    public static SwitchVlan fromSwitchVlanConfig( NBSwitchVlanConfig config )
    {
        SwitchVlan lConfig = new SwitchVlan();
        String netmask = null;
        if ( config == null )
            return null;
        if ( config.getIpAddress() != null )
        {
            netmask = IpUtils.prefixLenToNetmask( config.getIpAddress().getPrefixLen() );
        }
        lConfig.setIgmp( fromIgmp( config.getIgmp() ) );
        if ( config.getIpAddress() != null )
            lConfig.setIpAddress( config.getIpAddress().getPrefix() );
        lConfig.setNetmask( netmask );
        lConfig.setId( config.getVid() );
        lConfig.setTaggedPorts( new HashSet<String>() );
        lConfig.setUntaggedPorts( new HashSet<String>() );
        if ( config.getTaggedPorts() != null )
        {
            for ( String port : config.getTaggedPorts() )
            {
                lConfig.getTaggedPorts().add( port );
            }
        }
        if ( config.getUntaggedPorts() != null )
        {
            for ( String port : config.getUntaggedPorts() )
            {
                lConfig.getUntaggedPorts().add( port );
            }
        }
        return lConfig;
    }

    private static SwitchVlanIgmp fromIgmp( NBSwitchVlanConfig.Igmp config )
    {
        SwitchVlanIgmp lConfig = new SwitchVlanIgmp();
        if ( config == null )
            return null;
        lConfig.setIgmpQuerier( config.getIgmpQuerier() );
        return lConfig;
    }
}
