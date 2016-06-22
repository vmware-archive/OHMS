/* ********************************************************************************
 * SwitchNetworkPrefixDisassemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
