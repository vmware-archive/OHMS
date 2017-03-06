/* ********************************************************************************
 * SwitchBgpConfigAssemblers.java
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

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchBgpConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchNetworkPrefix;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;

public final class SwitchBgpConfigAssemblers
{

    public static NBSwitchBgpConfig toSwitchBgpConfig( SwitchBgpConfig config )
    {
        NBSwitchBgpConfig lConfig = new NBSwitchBgpConfig();

        if ( config == null || config.isEnabled() == false )
            return null;

        lConfig.setLocalAsn( config.getLocalAsn() );
        lConfig.setLocalIpAddress( config.getLocalIpAddress() );
        lConfig.setPeerAsn( config.getPeerAsn() );
        lConfig.setPeerIpAddress( config.getPeerIpAddress() );
        lConfig.setExportedNetworks( new ArrayList<NBSwitchNetworkPrefix>() );

        if ( config.getExportedNetworks() != null )
        {
            for ( String prefix : config.getExportedNetworks() )
            {
                lConfig.getExportedNetworks().add( SwitchNetworkPrefixAssemblers.toSwitchNetworkPrefix( prefix ) );
            }
        }

        return lConfig;
    }
}
