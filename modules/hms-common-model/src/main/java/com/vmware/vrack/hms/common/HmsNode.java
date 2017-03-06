/* ********************************************************************************
 * HmsNode.java
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
package com.vmware.vrack.hms.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmware.vrack.common.event.enums.EventSeverity;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;

public abstract class HmsNode
    extends Observable
{

    private static Logger logger = Logger.getLogger( HmsNode.class );

    protected String managementIp = "0.0.0.0";

    protected String managementUserName = null;

    protected String managementUserPassword = null;

    protected boolean isPowered = false;

    protected boolean isDiscoverable = false;

    protected String nodeID = "";

    protected Map<String, Object> nodeOsDetails = new HashMap<String, Object>();

    private int sshPort = 22;

    private long lastUpdatedTimestamp = 0L;

    private String monitorExecutionLog = "Not Available";

    protected NodeAdminStatus adminStatus = NodeAdminStatus.OPERATIONAL;

    @JsonIgnore
    private Map<ServerComponent, List<ServerComponentEvent>> componentSensorData =
        new ConcurrentHashMap<ServerComponent, List<ServerComponentEvent>>();

    @JsonIgnore
    private Map<SwitchComponentEnum, List<ServerComponentEvent>> componentSwitchSensorData =
        new ConcurrentHashMap<SwitchComponentEnum, List<ServerComponentEvent>>();

    public NodeAdminStatus getAdminStatus()
    {
        return adminStatus;
    }

    public void setAdminStatus( NodeAdminStatus adminStatus )
    {
        this.adminStatus = adminStatus;
    }

    @JsonIgnore
    public int getSshPort()
    {
        return sshPort;
    }

    public void setSshPort( int sshPort )
    {
        this.sshPort = sshPort;
    }

    public String getManagementIp()
    {
        return managementIp;
    }

    public void setManagementIp( String managementIp )
    {
        this.managementIp = managementIp;
    }

    public boolean isPowered()
    {
        return isPowered;
    }

    public void setPowered( boolean isPowered )
    {
        this.isPowered = isPowered;

        /*
         * if(this.isPowered) broadcastNodeAvailableEvent(null); else broadcastNodeFailureEvent(null);
         */
    }

    public boolean isDiscoverable()
    {
        return isDiscoverable;
    }

    public void setDiscoverable( boolean isDiscoverable )
    {
        if ( this.isDiscoverable != isDiscoverable )
            this.setChanged();

        this.isDiscoverable = isDiscoverable;
        if ( this.isDiscoverable )
            broadcastNodeAvailableEvent( null );
        else
            broadcastNodeFailureEvent( null );

    }

    public String getNodeID()
    {
        return nodeID;
    }

    public void setNodeID( String nodeID )
    {
        this.nodeID = nodeID;
    }

    public Map<String, Object> getNodeOsDetails()
    {
        return nodeOsDetails;
    }

    public void setNodeOsDetails( Map<String, Object> nodeOsDetails )
    {
        this.nodeOsDetails = nodeOsDetails;
    }

    public String getManagementUserName()
    {
        return managementUserName;
    }

    public void setManagementUserName( String managementUserName )
    {
        this.managementUserName = managementUserName;
    }

    public String getManagementUserPassword()
    {
        return managementUserPassword;
    }

    public void setManagementUserPassword( String managementUserPassword )
    {
        this.managementUserPassword = managementUserPassword;
    }

    @Override
    protected synchronized void setChanged()
    {
        setLastUpdatedTimestamp( ( new Date() ).getTime() );
        super.setChanged();
    }

    @JsonIgnore
    public String getMonitorExecutionLog()
    {
        return monitorExecutionLog;
    }

    public void setMonitorExecutionLog( String monitorExecutionLog, boolean append )
    {
        if ( append )
            this.monitorExecutionLog = this.monitorExecutionLog + " , " + monitorExecutionLog;
        else
            this.monitorExecutionLog = monitorExecutionLog;
    }

    @JsonIgnore
    public long getLastUpdatedTimestamp()
    {
        return lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp( long lastUpdatedTimestamp )
    {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    abstract public void broadcastNodeFailureEvent( List<Map<String, String>> data );

    abstract public void broadcastNodeAvailableEvent( List<Map<String, String>> data );

    @JsonIgnore
    public ServiceHmsNode getServiceObject()
        throws HmsException
    {
        ServiceHmsNode serviceHmsNode = new ServiceHmsNode();

        if ( !isNodeOperational() )
            throw new HmsException( this.getAdminStatus().getMessage( this.nodeID ) );

        serviceHmsNode.setNodeID( nodeID );
        serviceHmsNode.setManagementIp( managementIp );
        serviceHmsNode.setManagementUserName( managementUserName );
        serviceHmsNode.setManagementUserPassword( managementUserPassword );

        return serviceHmsNode;
    }

    @JsonIgnore
    public ServiceHmsNode getServiceSwitchObject()
        throws HmsException
    {
        ServiceHmsNode serviceHmsNode = new ServiceHmsNode();

        serviceHmsNode.setNodeID( nodeID );
        serviceHmsNode.setManagementIp( managementIp );
        serviceHmsNode.setManagementUserName( managementUserName );
        serviceHmsNode.setManagementUserPassword( managementUserPassword );

        return serviceHmsNode;
    }

    @JsonIgnore
    public boolean isNodeOperational()
    {
        if ( this.getAdminStatus() == NodeAdminStatus.OPERATIONAL )
            return true;
        else
            return false;
    }

    @JsonIgnore
    public Map<ServerComponent, List<ServerComponentEvent>> getComponentSensorData()
    {
        return componentSensorData;
    }

    @JsonIgnore
    public Map<SwitchComponentEnum, List<ServerComponentEvent>> getSwitchComponentSensorData()
    {
        return componentSwitchSensorData;
    }

    @JsonIgnore
    public void setComponentSensorData( Map<ServerComponent, List<ServerComponentEvent>> componentSensorData )
    {
        this.componentSensorData = componentSensorData;
    }

    @JsonIgnore
    public void setSwitchComponentSensorData( Map<SwitchComponentEnum, List<ServerComponentEvent>> componentSwitchSensorData )
    {
        this.componentSwitchSensorData = componentSwitchSensorData;
    }

    @JsonIgnore
    public void addComponentSensorData( ServerComponent component, List<ServerComponentEvent> sensorData )
    {
        if ( sensorData == null )
        {
            logger.warn( "Not adding SensorData which is set to null for the component: " + component );
        }
        else
        {
            this.componentSensorData.put( component, sensorData );
        }
    }

    @JsonIgnore
    public void addSwitchComponentSensorData( SwitchComponentEnum component, List<ServerComponentEvent> sensorData )
    {
        if ( sensorData == null )
        {
            logger.warn( "Not adding SensorData which is set to null for the component: " + component );
        }
        else
        {
            this.componentSwitchSensorData.put( component, sensorData );
        }
    }

    @JsonIgnore
    public List<ServerComponentEvent> getComponentSensor( ServerComponent component )
    {
        return this.componentSensorData.get( component );
    }

    @JsonIgnore
    public List<ServerComponentEvent> getSwitchComponentSensor( SwitchComponentEnum component )
    {
        return this.componentSwitchSensorData.get( component );
    }

    @JsonIgnore
    public List<ServerComponentEvent> getCriticalComponentSensor( ServerComponent component )
    {
        List<ServerComponentEvent> sensors = this.componentSensorData.get( component );
        List<ServerComponentEvent> criticalSensors = new ArrayList<ServerComponentEvent>();
        if ( sensors != null )
            for ( ServerComponentEvent sensor : sensors )
            {
                if ( sensor.getEventName().getEventID() != null
                    && ( sensor.getEventName().getEventID().getSeverity() == EventSeverity.CRITICAL
                        || sensor.getEventName().getEventID().getSeverity() == EventSeverity.ERROR ) )
                    criticalSensors.add( sensor );
            }
        return criticalSensors;
    }

    @JsonIgnore
    public List<ServerComponentEvent> getCriticalSwitchComponentSensor( SwitchComponentEnum component )
    {
        List<ServerComponentEvent> sensors = this.componentSwitchSensorData.get( component );
        List<ServerComponentEvent> criticalSensors = new ArrayList<ServerComponentEvent>();
        if ( sensors != null )
            for ( ServerComponentEvent sensor : sensors )
            {
                if ( sensor.getEventName().getEventID() != null
                    && ( sensor.getEventName().getEventID().getSeverity() == EventSeverity.CRITICAL
                        || sensor.getEventName().getEventID().getSeverity() == EventSeverity.ERROR ) )
                    criticalSensors.add( sensor );
            }
        return criticalSensors;
    }

}
