/* ********************************************************************************
 * MonitoringTaskResponse.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.monitoring;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentSwitchEventInfoProvider;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;

/**
 * @author sgakhar Response class, contains data for MonitoringTask and Suite classes
 */
public class MonitoringTaskResponse
{
    /**
     * @HmsNode node for which monitoring is to be done
     */
    protected HmsNode node;

    protected long timeStamp;

    /**
     * @List<ServerComponent> componentList contains specific components in a node against which sensor data after
     *                        monitoring is to be set
     */
    protected List<ServerComponent> componentList;

    protected List<SwitchComponentEnum> switchComponentList;

    /**
     * @IComponentSensorInfoProvider sensorInfoProvider provider to be used to retrieve sensor data IB,OOB, etc...
     */
    protected IComponentEventInfoProvider sensorInfoProvider;

    protected IComponentSwitchEventInfoProvider switchEventInfoProvider;

    protected List<Event> events = new ArrayList<Event>();

    public MonitoringTaskResponse( HmsNode node, List<ServerComponent> componentList,
                                   IComponentEventInfoProvider sensorInfoProvider )
    {
        this.node = node;
        this.componentList = componentList;
        this.timeStamp = ( new Date() ).getTime();
        this.sensorInfoProvider = sensorInfoProvider;
    }

    public MonitoringTaskResponse( HmsNode node, List<SwitchComponentEnum> switchComponentList,
                                   IComponentSwitchEventInfoProvider switchEventInfoProvider )
    {
        this.node = node;
        this.switchComponentList = switchComponentList;
        this.timeStamp = ( new Date() ).getTime();
        this.switchEventInfoProvider = switchEventInfoProvider;
    }

    public MonitoringTaskResponse( HmsNode node, SwitchComponentEnum component,
                                   IComponentSwitchEventInfoProvider switchEventInfoProvider )
    {
        this.node = node;
        this.switchComponentList = new ArrayList<SwitchComponentEnum>();
        this.switchComponentList.add( component );
        this.timeStamp = ( new Date() ).getTime();
        this.switchEventInfoProvider = switchEventInfoProvider;
    }

    public MonitoringTaskResponse( HmsNode node, ServerComponent component,
                                   IComponentEventInfoProvider sensorInfoProvider )
    {
        this.node = node;
        this.componentList = new ArrayList<ServerComponent>();
        this.componentList.add( component );
        this.timeStamp = ( new Date() ).getTime();
        this.sensorInfoProvider = sensorInfoProvider;
    }

    public MonitoringTaskResponse( HmsNode node, IComponentEventInfoProvider sensorInfoProvider )
    {
        this.node = node;
        this.sensorInfoProvider = sensorInfoProvider;
        this.componentList =
            EventMonitoringSubscriptionHolder.getSupportedSensorServerComponnet( node, sensorInfoProvider );
        this.timeStamp = ( new Date() ).getTime();
    }

    public HmsNode getNode()
    {
        return node;
    }

    public void setNode( HmsNode node )
    {
        this.node = node;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp( long timeStamp )
    {
        this.timeStamp = timeStamp;
    }

    public List<ServerComponent> getComponentList()
    {
        return componentList;
    }

    public void setComponentList( List<ServerComponent> componentList )
    {
        this.componentList = componentList;
    }

    public List<SwitchComponentEnum> getSwitchComponentList()
    {
        return switchComponentList;
    }

    public void setSwitchComponentList( List<SwitchComponentEnum> switchComponentList )
    {
        this.switchComponentList = switchComponentList;
    }

    public String getHms_node_id()
    {
        return node.getNodeID();
    }

    public IComponentEventInfoProvider getSensorInfoProvider()
    {
        return sensorInfoProvider;
    }

    public void setSensorInfoProvider( IComponentEventInfoProvider sensorInfoProvider )
    {
        this.sensorInfoProvider = sensorInfoProvider;
    }

    public IComponentSwitchEventInfoProvider getSwitchEventInfoProvider()
    {
        return switchEventInfoProvider;
    }

    public void setSwitchEventInfoProvider( IComponentSwitchEventInfoProvider switchEventInfoProvider )
    {
        this.switchEventInfoProvider = switchEventInfoProvider;
    }

    public List<Event> getEvents()
    {
        return events;
    }

    public void setEvents( List<Event> events )
    {
        this.events = events;
    }
}
