/* ********************************************************************************
 * SwitchInfoAssemblers.java
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

import java.util.List;

import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchMclagInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode.SwitchRoleType;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;

public final class SwitchInfoAssemblers
{

    public static NBSwitchInfo toSwitchInfo( List<SwitchPort> ports, List<SwitchLacpGroup> lags, List<SwitchVlan> vlans,
                                             SwitchOspfConfig ospf, SwitchBgpConfig bgp, SwitchMclagInfo mcLag,
                                             SwitchInfo switchAttributes, SwitchSensorInfo sensorInfo )
    {
        NBSwitchInfo lInfo = new NBSwitchInfo();

        /* All that can be configured except ports */
        lInfo.setConfig( SwitchConfigAssemblers.toSwitchConfig( lags, vlans, ospf, bgp, mcLag, switchAttributes ) );

        /* Port specific information and configurations */
        lInfo.setPorts( SwitchPortInfoAssemblers.toSwitchPortInfos( ports ) );

        /* Global switch attributes */

        if ( switchAttributes != null )
        {
            lInfo.setComponentIdentifier( switchAttributes.getComponentIdentifier() );
            lInfo.setFirmwareName( switchAttributes.getFirmwareName() );
            lInfo.setFirmwareVersion( switchAttributes.getFirmwareVersion() );
            lInfo.setFruId( switchAttributes.getFruId() );
            lInfo.setIpAddress( switchAttributes.getIpAddress() );
            lInfo.setLocation( switchAttributes.getLocation() );
            lInfo.setMangementPort( switchAttributes.getMangementPort() );
            lInfo.setOperationalStatus( extractOperationalStatus( switchAttributes ) );
            lInfo.setOsName( switchAttributes.getOsName() );
            lInfo.setOsVersion( switchAttributes.getOsVersion() );
            lInfo.setRole( toSwitchRoleType( switchAttributes.getRole() ) );
            lInfo.setSwitchId( switchAttributes.getSwitchId() );
        }

        lInfo.setSensors( SwitchSensorInfoAssemblers.toSwitchSensorInfo( sensorInfo ) );

        return lInfo;
    }

    public static NBSwitchInfo toSwitchInfo( SwitchInfo switchAttributes )
    {
        NBSwitchInfo lInfo = new NBSwitchInfo();

        /* Global switch attributes */
        if ( switchAttributes != null )
        {

            if ( switchAttributes.isPowered() )
            {
                lInfo.setComponentIdentifier( switchAttributes.getComponentIdentifier() );
                lInfo.setFirmwareName( switchAttributes.getFirmwareName() );
                lInfo.setFirmwareVersion( switchAttributes.getFirmwareVersion() );
                lInfo.setFruId( switchAttributes.getFruId() );
                lInfo.setMangementPort( switchAttributes.getMangementPort() );
                lInfo.setOsName( switchAttributes.getOsName() );
                lInfo.setOsVersion( switchAttributes.getOsVersion() );
            }

            lInfo.setOperationalStatus( extractOperationalStatus( switchAttributes ) );
            lInfo.setAdminStatus( NodeAdminStatus.OPERATIONAL );
            lInfo.setSwitchId( switchAttributes.getSwitchId() );
            lInfo.setRole( toSwitchRoleType( switchAttributes.getRole() ) );
            lInfo.setLocation( switchAttributes.getLocation() );
            lInfo.setIpAddress( switchAttributes.getIpAddress() );
        }

        return lInfo;
    }

    private static NBSwitchInfo.SwitchRoleType toSwitchRoleType( SwitchRoleType roleType )
    {
        NBSwitchInfo.SwitchRoleType lRoleType = null;

        if ( roleType == null )
            return null;

        switch ( roleType )
        {
            case MANAGEMENT:
                lRoleType = NBSwitchInfo.SwitchRoleType.MANAGEMENT;
                break;
            case SPINE:
                lRoleType = NBSwitchInfo.SwitchRoleType.SPINE;
                break;
            case TOR:
                lRoleType = NBSwitchInfo.SwitchRoleType.TOR;
                break;
            default:
                break;
        }

        return lRoleType;
    }

    private static FruOperationalStatus extractOperationalStatus( SwitchInfo switchAttributes )
    {
        FruOperationalStatus lStatus = FruOperationalStatus.UnKnown;

        switch ( switchAttributes.getOperational_status() )
        {
            case "true":
                lStatus = FruOperationalStatus.Operational;
                break;
            default:
                lStatus = FruOperationalStatus.NonOperational;
                break;
        }

        return lStatus;
    }

    /**
     * Convert SwitchNode to NBSwitchInfo
     * 
     * @param switchNode
     * @return
     */
    public static NBSwitchInfo toNBSwitchInfoFromSwitchNode( SwitchNode switchNode )
    {
        NBSwitchInfo nbSwitchInfo = new NBSwitchInfo();

        if ( switchNode != null )
        {
            nbSwitchInfo.setComponentIdentifier( switchNode.getComponentIdentifier() );
            nbSwitchInfo.setFirmwareName( switchNode.getFirmwareName() );
            nbSwitchInfo.setFirmwareVersion( switchNode.getFirmwareVersion() );
            nbSwitchInfo.setMangementPort( String.valueOf( switchNode.getPort() ) );
            nbSwitchInfo.setOsName( switchNode.getOsName() );
            nbSwitchInfo.setOperationalStatus( FruOperationalStatus.NonOperational );
            nbSwitchInfo.setAdminStatus( NodeAdminStatus.OPERATIONAL );
            nbSwitchInfo.setSwitchId( switchNode.getSwitchId() );
            nbSwitchInfo.setRole( toSwitchRoleType( switchNode.getRole() ) );
            nbSwitchInfo.setLocation( switchNode.getLocation() );
            nbSwitchInfo.setIpAddress( switchNode.getIpAddress() );
        }
        return nbSwitchInfo;
    }

}
