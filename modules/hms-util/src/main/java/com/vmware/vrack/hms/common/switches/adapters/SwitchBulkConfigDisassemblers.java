/* ********************************************************************************
 * SwitchBulkConfigDisassemblers.java
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
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.ArrayList;
import java.util.List;

import com.vmware.vrack.hms.common.rest.model.switches.bulk.*;
import com.vmware.vrack.hms.common.switches.model.bulk.*;

public class SwitchBulkConfigDisassemblers
{
    public static List<PluginSwitchBulkConfig> toSwitchBulkConfigs( List<NBSwitchBulkConfig> configs )
    {
        List<PluginSwitchBulkConfig> lConfigs = new ArrayList<PluginSwitchBulkConfig>();
        if ( configs == null )
        {
            return null;
        }
        for ( NBSwitchBulkConfig tmpConfig : configs )
        {
            lConfigs.add( toSwitchBulkConfig( tmpConfig ) );
        }
        return lConfigs;
    }

    public static PluginSwitchBulkConfig toSwitchBulkConfig( NBSwitchBulkConfig config )
    {
        PluginSwitchBulkConfig lConfig = new PluginSwitchBulkConfig();
        if ( config == null )
        {
            return null;
        }
        lConfig.setType( toSwitchBulkConfigEnum( config.getType() ) );
        lConfig.setFilters( getCopyOfListOfString( config.getFilters() ) );
        lConfig.setValues( getCopyOfListOfString( config.getValues() ) );
        return lConfig;
    }

    public static PluginSwitchBulkConfigEnum toSwitchBulkConfigEnum( NBSwitchBulkConfigEnum enumVal )
    {
        PluginSwitchBulkConfigEnum lEnumVL = null;
        if ( enumVal == null )
        {
            return null;
        }
        if ( enumVal.equals( NBSwitchBulkConfigEnum.PHYSICAL_SWITCH_PORT_MTU ) )
        {
            lEnumVL = PluginSwitchBulkConfigEnum.PHYSICAL_SWITCH_PORT_MTU;
        }
        else if ( enumVal.equals( NBSwitchBulkConfigEnum.BOND_MTU ) )
        {
            lEnumVL = PluginSwitchBulkConfigEnum.BOND_MTU;
        }
        return lEnumVL;
    }

    /**
     * Returns a deep copy of the list of String values
     *
     * @param list
     * @return
     */
    private static List<String> getCopyOfListOfString( List<String> list )
    {
        List<String> newList = null;
        if ( list == null )
        {
            return null;
        }
        newList = new ArrayList<String>();
        for ( String value : list )
        {
            newList.add( value );
        }
        return newList;
    }
}
