/* ********************************************************************************
 * SwitchNetworkPrefixDisassemblers.java
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

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchNetworkPrefix;

public class SwitchNetworkPrefixDisassemblers
{
    public static String fromSwitchNetworkPrefix( NBSwitchNetworkPrefix prefix )
    {
        String retVal = null;
        if ( prefix == null )
            return null;
        retVal = String.format( "%s/%d", prefix.getPrefix(), prefix.getPrefixLen() );
        return retVal;
    }
}
