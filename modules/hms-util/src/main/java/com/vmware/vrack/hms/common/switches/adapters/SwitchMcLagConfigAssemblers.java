/* ********************************************************************************
 * SwitchMcLagConfigAssemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.adapters;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchMcLagConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchMclagInfo;

public final class SwitchMcLagConfigAssemblers
{
    public static NBSwitchMcLagConfig toSwitchMcLagConfig( SwitchMclagInfo config )
    {
        NBSwitchMcLagConfig lConfig = new NBSwitchMcLagConfig();
        if ( config == null )
            return null;
        lConfig.setInterfaceName( config.getInterfaceName() );
        lConfig.setMyIp( config.getIpAddress() );
        lConfig.setNetmask( config.getNetmask() );
        lConfig.setPeerIp( config.getPeerIp() );
        lConfig.setSystemId( config.getSharedMac() );
        return lConfig;
    }
}
