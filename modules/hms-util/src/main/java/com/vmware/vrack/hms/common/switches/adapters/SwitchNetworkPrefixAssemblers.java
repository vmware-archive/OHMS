/* ********************************************************************************
 * SwitchNetworkPrefixAssemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
