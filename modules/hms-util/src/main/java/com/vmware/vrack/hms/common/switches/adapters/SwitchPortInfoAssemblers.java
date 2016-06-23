/* ********************************************************************************
 * SwitchPortInfoAssemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.ArrayList;
import java.util.List;

import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchPortInfo;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;

public final class SwitchPortInfoAssemblers
{
    public static List<NBSwitchPortInfo> toSwitchPortInfos( List<SwitchPort> infos )
    {
        List<NBSwitchPortInfo> lInfos = new ArrayList<NBSwitchPortInfo>();
        if ( infos == null )
            return lInfos;
        for ( SwitchPort info : infos )
        {
            lInfos.add( toSwitchPortInfo( info ) );
        }
        return lInfos;
    }

    public static NBSwitchPortInfo toSwitchPortInfo( SwitchPort info )
    {
        NBSwitchPortInfo lInfo = new NBSwitchPortInfo();
        if ( info == null )
            return null;
        lInfo.setConfig( SwitchPortConfigAssemblers.toSwitchPortConfig( info ) );
        lInfo.setMacAddress( info.getMacAddress() );
        lInfo.setName( info.getName() );
        lInfo.setOperationalStatus( extractOperationalStatus( info ) );
        lInfo.setStats( SwitchPortStatsAssemblers.toSwitchPortStats( info ) );
        lInfo.setAdminStatus( extractAdminStatus( info ) );
        return lInfo;
    }

    private static FruOperationalStatus extractOperationalStatus( SwitchPort port )
    {
        FruOperationalStatus lStatus = FruOperationalStatus.UnKnown;
        if ( port == null || port.getStatus() == null )
            return null;
        switch ( port.getStatus() )
        {
            case UP:
                lStatus = FruOperationalStatus.Operational;
                break;
            case DOWN:
                lStatus = FruOperationalStatus.NonOperational;
                break;
        }
        return lStatus;
    }

    private static NodeAdminStatus extractAdminStatus( SwitchPort port )
    {
        NodeAdminStatus lStatus = null;
        if ( port == null || port.getStatus() == null )
            return null;
        switch ( port.getStatus() )
        {
            case UP:
                lStatus = NodeAdminStatus.OPERATIONAL;
                break;
            case DOWN:
                lStatus = NodeAdminStatus.DECOMISSION;
                break;
        }
        return lStatus;
    }
}
