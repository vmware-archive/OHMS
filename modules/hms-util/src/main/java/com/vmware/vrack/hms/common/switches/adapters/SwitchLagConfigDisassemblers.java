/* ********************************************************************************
 * SwitchLagConfigDisassemblers.java
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

import java.util.ArrayList;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchLagConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;

public class SwitchLagConfigDisassemblers
{
    public static SwitchLacpGroup fromSwitchLagConfig( NBSwitchLagConfig config )
    {
        SwitchLacpGroup lConfig = new SwitchLacpGroup();

        if ( config == null )
            return null;

        lConfig.setMode( config.getMode() );
        lConfig.setMtu( config.getMtu() );
        lConfig.setName( config.getName() );
        lConfig.setPorts( new ArrayList<String>() );
        lConfig.setIpAddress( SwitchNetworkPrefixDisassemblers.fromSwitchNetworkPrefix( config.getIpAddress() ) );

        if ( config.getPorts() != null )
        {
            for ( String port : config.getPorts() )
            {
                lConfig.getPorts().add( port );
            }
        }

        return lConfig;
    }
}
