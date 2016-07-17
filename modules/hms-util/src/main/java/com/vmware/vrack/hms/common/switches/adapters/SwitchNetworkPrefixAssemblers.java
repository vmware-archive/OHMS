/* ********************************************************************************
 * SwitchNetworkPrefixAssemblers.java
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

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchNetworkPrefix;

public final class SwitchNetworkPrefixAssemblers
{
    public static NBSwitchNetworkPrefix toSwitchNetworkPrefix( String prefix )
    {
        NBSwitchNetworkPrefix lPrefix = new NBSwitchNetworkPrefix();
        String tokens[] = null;
        Integer prefixLen = 0;
        if ( prefix == null )
            return null;
        tokens = prefix.split( "/" );
        if ( tokens.length != 2 )
        {
            prefixLen = 32; // host only IP when prefix length is not specified
        }
        else
        {
            try
            {
                prefixLen = Integer.parseInt( tokens[1] ); // if present then has
                // to be a valid prefix
                // length
            }
            catch ( NumberFormatException e )
            {
                return null;
            }
        }
        if ( prefixLen < 1 || prefixLen > 32 )
            return null;
        lPrefix.setPrefix( tokens[0] );
        lPrefix.setPrefixLen( prefixLen );
        return lPrefix;
    }
}
