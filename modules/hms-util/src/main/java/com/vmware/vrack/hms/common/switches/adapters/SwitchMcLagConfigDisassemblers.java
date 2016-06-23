/* ********************************************************************************
 * SwitchMcLagConfigDisassemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.adapters;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchMcLagConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchMclagInfo;

public class SwitchMcLagConfigDisassemblers
{
    public static SwitchMclagInfo fromSwitchMcLagConfig( NBSwitchMcLagConfig config )
    {
        SwitchMclagInfo lConfig = new SwitchMclagInfo();
        if ( config == null )
            return null;
        lConfig.setInterfaceName( config.getInterfaceName() );
        lConfig.setIpAddress( config.getMyIp() );
        lConfig.setNetmask( config.getNetmask() );
        lConfig.setPeerIp( config.getPeerIp() );
        lConfig.setSharedMac( config.getSystemId() );
        return lConfig;
    }
}
