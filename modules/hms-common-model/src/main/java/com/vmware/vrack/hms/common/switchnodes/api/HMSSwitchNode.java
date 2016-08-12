/* ********************************************************************************
 * HMSSwitchNode.java
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
package com.vmware.vrack.hms.common.switchnodes.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.notification.CallbackRequestFactory;
import com.vmware.vrack.hms.common.notification.EventType;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortType;

public class HMSSwitchNode
    extends HmsNode
{
    private String managementMacAddress;

    private String hardwareModel;

    private String serialNumber;

    private String chassisId;

    private Date lastBoot;

    private List<SwitchPort> portList;

    private Object switchConfiguration;

    private boolean validatedConfiguration;

    public HMSSwitchNode( String nodeID, String ipAddress, String managementUserName, String managementUserPassword )
    {
        this.nodeID = nodeID;
        this.managementIp = ipAddress;
        this.managementUserName = managementUserName;
        this.managementUserPassword = managementUserPassword;
    }

    public HMSSwitchNode( String nodeID, String ipAddress )
    {
        this.nodeID = nodeID;
        this.managementIp = ipAddress;
    }

    @Override
    public void setPowered( boolean isPowered )
    {
        this.isPowered = isPowered;
    }

    @Override
    public void setDiscoverable( boolean isDiscoverable )
    {
        this.isDiscoverable = isDiscoverable;
    }

    @Override
    public String getManagementIp()
    {
        return managementIp;
    }

    @Override
    public void setManagementIp( String managementIp )
    {
        this.managementIp = managementIp;
    }

    public String getManagementMacAddress()
    {
        return managementMacAddress;
    }

    public void setManagementMacAddress( String managementMacAddress )
    {
        this.managementMacAddress = managementMacAddress;
    }

    public String getHardwareModel()
    {
        return hardwareModel;
    }

    public void setHardwareModel( String model )
    {
        this.hardwareModel = model;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber( String serialNumber )
    {
        this.serialNumber = serialNumber;
    }

    public String getChassisId()
    {
        return chassisId;
    }

    public void setChassisId( String chassisId )
    {
        this.chassisId = chassisId;
    }

    public Date getLastBoot()
    {
        return lastBoot;
    }

    public void setLastBoot( Date lastBoot )
    {
        this.lastBoot = lastBoot;
    }

    public List<SwitchPort> getPortList()
    {
        return portList;
    }

    public void setPortList( List<SwitchPort> portList )
    {
        this.portList = portList;
        ArrayList<Map<String, String>> ports = new ArrayList<Map<String, String>>();
        // Set management MAC address
        for ( SwitchPort p : portList )
        {
            ports.add( p.getObjMap() );
            if ( p.getType() == PortType.MANAGEMENT )
            {
                setManagementMacAddress( p.getMacAddress() );
                break;
            }
        }
        this.setChanged();
        this.notifyObservers( CallbackRequestFactory.getNotificationRequest( EventType.SWITCH_MONITOR, this.nodeID,
                                                                             ports ) );
    }

    public Object getSwitchConfiguration()
    {
        return switchConfiguration;
    }

    public void setSwitchConfiguration( Object switchConfiguration )
    {
        this.switchConfiguration = switchConfiguration;
    }

    public boolean isValidatedConfiguration()
    {
        return validatedConfiguration;
    }

    public void setValidatedConfiguration( boolean validatedConfiguration )
    {
        this.validatedConfiguration = validatedConfiguration;
    }

    @Override
    public void broadcastNodeAvailableEvent( List<Map<String, String>> data )
    {
        this.notifyObservers( CallbackRequestFactory.getNotificationRequest( EventType.SWITCH_UP, this.nodeID, data ) );
    }

    @Override
    public void broadcastNodeFailureEvent( List<Map<String, String>> data )
    {
        this.notifyObservers( CallbackRequestFactory.getNotificationRequest( EventType.SWITCH_FAILURE, this.nodeID,
                                                                             data ) );
    }
}
