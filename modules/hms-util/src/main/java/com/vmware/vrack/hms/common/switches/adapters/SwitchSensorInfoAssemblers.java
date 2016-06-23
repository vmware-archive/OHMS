/* ********************************************************************************
 * SwitchSensorInfoAssemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.ArrayList;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchSensorInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo;

public final class SwitchSensorInfoAssemblers
{
    public static NBSwitchSensorInfo toSwitchSensorInfo( SwitchSensorInfo info )
    {
        NBSwitchSensorInfo lInfo = new NBSwitchSensorInfo();
        if ( info == null )
            return null;
        lInfo.setChassisTemps( new ArrayList<NBSwitchSensorInfo.ChassisTemp>() );
        lInfo.setFanSpeeds( new ArrayList<NBSwitchSensorInfo.FanSpeed>() );
        lInfo.setPsuStatus( new ArrayList<NBSwitchSensorInfo.PsuStatus>() );
        if ( info.getChassisTemps() != null )
        {
            for ( SwitchSensorInfo.ChassisTemp chassisTemp : info.getChassisTemps() )
            {
                lInfo.getChassisTemps().add( toChassisTemp( chassisTemp ) );
            }
        }
        if ( info.getFanSpeeds() != null )
        {
            for ( SwitchSensorInfo.FanSpeed fanSpeed : info.getFanSpeeds() )
            {
                lInfo.getFanSpeeds().add( toFanSpeed( fanSpeed ) );
            }
        }
        if ( info.getPsuStatus() != null )
        {
            for ( SwitchSensorInfo.PsuStatus psuStatus : info.getPsuStatus() )
            {
                lInfo.getPsuStatus().add( toPsuStatus( psuStatus ) );
            }
        }
        lInfo.setTimestamp( info.getTimestamp() );
        return lInfo;
    }

    private static NBSwitchSensorInfo.ChassisTemp toChassisTemp( SwitchSensorInfo.ChassisTemp info )
    {
        NBSwitchSensorInfo.ChassisTemp lInfo = new NBSwitchSensorInfo.ChassisTemp();
        lInfo.setStatus( info.getStatus() );
        lInfo.setTempId( info.getTempId() );
        lInfo.setTempName( info.getTempName() );
        lInfo.setUnit( info.getUnit() );
        lInfo.setValue( info.getValue() );
        return lInfo;
    }

    private static NBSwitchSensorInfo.FanSpeed toFanSpeed( SwitchSensorInfo.FanSpeed info )
    {
        NBSwitchSensorInfo.FanSpeed lInfo = new NBSwitchSensorInfo.FanSpeed();
        lInfo.setFanId( info.getFanId() );
        lInfo.setFanName( info.getFanName() );
        lInfo.setStatus( info.getStatus() );
        lInfo.setUnit( info.getUnit() );
        lInfo.setValue( info.getValue() );
        return lInfo;
    }

    private static NBSwitchSensorInfo.PsuStatus toPsuStatus( SwitchSensorInfo.PsuStatus info )
    {
        NBSwitchSensorInfo.PsuStatus lInfo = new NBSwitchSensorInfo.PsuStatus();
        lInfo.setPsuId( info.getPsuId() );
        lInfo.setPsuName( info.getPsuName() );
        lInfo.setStatus( info.getStatus() );
        return lInfo;
    }
}
