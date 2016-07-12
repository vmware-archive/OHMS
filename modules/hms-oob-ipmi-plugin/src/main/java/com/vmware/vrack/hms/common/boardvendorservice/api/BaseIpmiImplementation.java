/* ********************************************************************************
 * BaseIpmiImplementation.java
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
package com.vmware.vrack.hms.common.boardvendorservice.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.api.helper.BmcSensorHelper;
import com.vmware.vrack.hms.common.boardvendorservice.api.helper.SensorHelper;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.OperationNotSupportedOOBException;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.PowerOperationAction;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.EntityId;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.resource.fru.SensorType;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.servernodes.api.fan.FanInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;
import com.vmware.vrack.hms.ipmiservice.IpmiService;
import com.vmware.vrack.hms.ipmiservice.IpmiServiceExecutor;

/**
 * Abstract class, which provides generic default functionality for the API's. Partners can override the functionality
 * in the concrete class that extends the current class.
 *
 * @author VMware Inc.
 */
public abstract class BaseIpmiImplementation
    implements IBoardService
{
    private int fruReadPacketSize;

    private static Logger logger = Logger.getLogger( BaseIpmiImplementation.class );

    IpmiService ipmiServiceExecutor = new IpmiServiceExecutor();

    @Override
    public boolean getServerPowerStatus( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            logger.debug( "Power Status requested for Node (base ipmi implementation): " + node.getNodeID() );
            // logger.debug("Power Status requested for Node (base ipmi implementation): {} ",
            // node.getNodeID().toString());
            try
            {
                boolean powerStatus = ipmiServiceExecutor.getServerPowerStatus( node );
                return powerStatus;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while getting Server Power Status for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while getting Server Power Status for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in getServerPowerStatus - node either null or invalid." );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    private boolean powerDownServer( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            logger.debug( "Power down server - power operations request on Node: " + node.getNodeID() );
            try
            {
                boolean status = ipmiServiceExecutor.powerDownServer( node );
                return status;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while triggering power Down for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while triggering power Down for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in PowerDownServer - node either null or invalid." );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    private boolean powerUpServer( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            logger.debug( "Power up server - power operations request on Node: " + node.getNodeID() );
            try
            {
                boolean status = ipmiServiceExecutor.powerUpServer( node );
                return status;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while triggering power Up for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while triggering power Up for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in PowerUpServer - node either null or invalid." );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    private boolean powerResetServer( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            logger.debug( "Power reset server - power operations request on Node: " + node.getNodeID() );
            try
            {
                boolean status = ipmiServiceExecutor.powerResetServer( node );
                return status;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while triggering power Reset for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while triggering power Reset for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in PowerResetServer - node either null or invalid." );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    private boolean powerCycleServer( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            logger.debug( "Power cycle server - power operations request on Node:" + node.getNodeID() );
            try
            {
                boolean status = ipmiServiceExecutor.powerCycleServer( node );
                return status;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while triggering power Cycle for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while triggering power Cycle for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in PowerCycleServer - node either null or invalid" );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    private boolean coldResetServer( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            logger.debug( "Cold reset server - power operations request on Node: " + node.getNodeID() );
            try
            {
                boolean status = ipmiServiceExecutor.coldResetServer( node );
                return status;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while triggering Cold Reset for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while triggering Cold Reset for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in ColdResetServer - node either null or invalid" );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    @Override
    public AcpiPowerState getAcpiPowerState( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            AcpiPowerState acpiPowerState = null;
            logger.debug( "Acpi Power State request on Node: " + node.getNodeID() );
            try
            {
                acpiPowerState = ipmiServiceExecutor.getAcpiPowerState( node );
                return acpiPowerState;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while getting Acpi Power State for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while getting Acpi Power State for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in AcpiPowerState - node either null or invalid" );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    @Override
    public List<CPUInfo> getCpuInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation getCpuInfo not supported" );
    }

    /**
     * Get already set Boot Options
     *
     * @param serviceHmsNode
     * @return systemBootOptions
     */
    @Override
    public SystemBootOptions getBootOptions( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            SystemBootOptions systemBootOptions = null;
            logger.debug( "Get Boot Options request on Node: " + node.getNodeID() );
            try
            {
                systemBootOptions = ipmiServiceExecutor.getBootOptions( node );
                return systemBootOptions;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while getting System Boot Options for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while getting System Boot Options for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in getBootOptions - node either null or invalid" );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    /**
     * Set Boot Options
     *
     * @param serviceHmsNode
     * @param data
     * @return status
     */
    @Override
    public boolean setBootOptions( ServiceHmsNode serviceHmsNode, SystemBootOptions data )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode && data != null )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            logger.debug( "Set Boot Options request on Node: " + node.getNodeID() );
            try
            {
                boolean status = ipmiServiceExecutor.setBootOptions( node, data );
                return status;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while setting System Boot Options for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while setting System Boot Options for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in set Boot Options - node either null or invalid." );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            ServerNodeInfo nodeInfo = null;
            logger.debug( "Get Server Info request on Node: " + node.getNodeID() );
            try
            {
                if ( fruReadPacketSize != 0 )
                    nodeInfo = ipmiServiceExecutor.getServerInfo( node, fruReadPacketSize );
                else
                    nodeInfo = ipmiServiceExecutor.getServerInfo( node );
                /*
                 * If server info resides on FRUs other than "FRU 0", then override this function with code below to
                 * specify the FRU IDs explicitly. int fruId1 = 1; int fruId2 = 2; ArrayList<Integer> fruList = new
                 * ArrayList<Integer>(); fruList.add(fruId1); fruList.add(fruId2); nodeInfo =
                 * ipmiServiceExecutor.getServerInfo(node, fruReadPacketSize, fruList);
                 */
                return nodeInfo;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while getting Server Node Info for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while getting Server Node Info for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in get Server Info - node either null or invalid." );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    @Override
    public List<HddInfo> getHddInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation getHddInfo not supported" );
    }

    /**
     * Get the List of BMC/ME Users
     *
     * @param serviceHmsNode
     * @return List<BmcUser>
     */
    @Override
    public List<BmcUser> getManagementUsers( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            List<BmcUser> bmcUsers = new ArrayList<>();
            logger.debug( "Management Users requested for Node (base ipmi implementation):" + node.getNodeID() );
            try
            {
                bmcUsers = ipmiServiceExecutor.getBmcUsers( node );
                return bmcUsers;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while getting Bmc users for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while getting Bmc users for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in get Management Users - node either null or invalid" );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    @Override
    public List<EthernetController> getEthernetControllersInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation getEthernetControllersInfo not supported" );
    }

    /**
     * Get System Event Log Information Only. Gives idea about total entries count, last addition time, last erase time.
     * Skips the actual entries
     *
     * @param serviceHmsNode
     */
    public SelInfo getSelInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            SelInfo selInfo = null;
            logger.debug( "Sel Info requested for Node (base ipmi implementation): " + node.getNodeID() );
            try
            {
                selInfo = ipmiServiceExecutor.getSelInfo( node );
                return selInfo;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while getting Sel Info for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while getting Sel Info for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in get Sel Info - node either null or invalid." );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    /**
     * Get FAN Information
     *
     * @param serviceHmsNode
     */
    @Override
    public List<FanInfo> getFanInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation FAN info not supprted" );
    }

    /**
     * For a give HMS node and a given server component of the HMS node, this function returns the appropriate list of
     * HMS events generated by server component. This function throws exception if any of the server individual
     * component list generators fail to return the event list or if no server node is provided. Override this function
     * in the BoardService class to implement all the components if there is a specific implementation for the given
     * Server. This is provided as an example.
     *
     * @param serviceNode HMS node representing the Server for which event list is requested.
     * @param component Server component for which the event list is requested.
     * @return List of HMS events generated by a Server Component for a given Server Node.
     * @throws HmsException HmsException is raised if the individual component list generators fail to return an event
     *             list or if no server node is provided.
     */
    @Override
    public List<ServerComponentEvent> getComponentEventList( ServiceHmsNode serviceNode, ServerComponent component )
        throws HmsException
    {
        if ( serviceNode != null && serviceNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceNode;
            List<ServerComponentEvent> serverComponentSensor = new ArrayList<>();
            List<EntityId> entities = null;
            List<SensorType> typeList = new ArrayList<SensorType>();
            try
            {
                switch ( component )
                {
                    case CPU:
                        return getCpuEventList( node );
                    case MEMORY:
                        return getMemoryEventList( node );
                    case FAN:
                        return getFanEventList( node );
                    case POWERUNIT:
                        return getPowerUnitEventList( node );
                    case STORAGE:
                        return getDriveBayEventList( node );
                    case NIC:
                        return getNicEventList( node );
                    case BMC:
                        return getBmcEventList( node );
                    case SYSTEM:
                        return getSystemEventList( node );
                }
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while getting getComponentSensorList Data for node:" + serviceNode.getNodeID(),
                              e );
                throw e;
            }
        }
        else
        {
            throw new HmsException( "Node is Null or invalid" );
        }
        return null;
    }

    /**
     * Provides a list of events generated by the CPU for a given Server node. Override this function to implement
     * Server specific response to generate CPU event list.
     *
     * @param serviceNode HMS node representing the Server for which CPU event list is requested.
     * @return List of CPU events generated by this Server.
     * @throws HmsException Throws HmsException if Sensor information is unavailable.
     */
    public List<ServerComponentEvent> getCpuEventList( ServiceHmsNode serviceNode )
        throws HmsException
    {
        ServiceServerNode node = (ServiceServerNode) serviceNode;
        List<ServerComponentEvent> serverComponentSensor = null;
        List<EntityId> entities = new ArrayList<EntityId>();
        List<SensorType> typeList = new ArrayList<SensorType>();
        try
        {
            entities.add( EntityId.Processor );
            entities.add( EntityId.Processor2 );
            entities.add( EntityId.ProcessorBoard );
            entities.add( EntityId.ProcessorIoModule );
            entities.add( EntityId.ProcessorMemoryModule );
            typeList.add( SensorType.Processor );
            typeList.add( SensorType.Temperature );
            serverComponentSensor = SensorHelper.getSensorEvents( node, typeList, entities, ipmiServiceExecutor );
            return serverComponentSensor;
        }
        catch ( HmsException e )
        {
            logger.error( "Exception while getting getCpuEventList Data for node:" + serviceNode.getNodeID(), e );
            throw e;
        }
        catch ( Exception e )
        {
            logger.error( "Exception while getting getCpuEventList Data for node:" + serviceNode.getNodeID(), e );
            throw new HmsException( e );
        }
    }

    /**
     * Provides a list of events generated by the Memory for a given Server node. Override this function to implement
     * Server specific response to generate Memory event list.
     *
     * @param serviceNode HMS node representing the Server for which Memory event list is requested.
     * @return List of Memory events generated by this Server.
     * @throws HmsException Throws HmsException if Sensor information is unavailable.
     */
    public List<ServerComponentEvent> getMemoryEventList( ServiceHmsNode serviceNode )
        throws HmsException
    {
        ServiceServerNode node = (ServiceServerNode) serviceNode;
        List<ServerComponentEvent> serverComponentSensor = null;
        List<EntityId> entities = new ArrayList<EntityId>();
        List<SensorType> typeList = new ArrayList<SensorType>();
        try
        {
            entities.add( EntityId.MemoryDevice );
            entities.add( EntityId.MemoryModule );
            typeList.add( SensorType.Memory );
            typeList.add( SensorType.Temperature );
            serverComponentSensor = SensorHelper.getSensorEvents( node, typeList, entities, ipmiServiceExecutor );
            return serverComponentSensor;
        }
        catch ( HmsException e )
        {
            logger.error( "Exception while getting getMemoryEventList Data for node:" + serviceNode.getNodeID(), e );
            throw e;
        }
        catch ( Exception e )
        {
            logger.error( "Exception while getting getMemoryEventList Data for node:" + serviceNode.getNodeID(), e );
            throw new HmsException( e );
        }
    }

    /**
     * Provides a list of events generated by the Fan for a given Server node. Override this function to implement
     * Server specific response to generate Fan event list.
     *
     * @param serviceNode HMS node representing the Server for which Fan event list is requested.
     * @return List of Fan events generated by this Server.
     * @throws HmsException Throws HmsException if Sensor information is unavailable.
     */
    public List<ServerComponentEvent> getFanEventList( ServiceHmsNode serviceNode )
        throws HmsException
    {
        ServiceServerNode node = (ServiceServerNode) serviceNode;
        List<ServerComponentEvent> serverComponentSensor = null;
        List<EntityId> entities = null;
        List<SensorType> typeList = new ArrayList<SensorType>();
        try
        {
            typeList.add( SensorType.Fan );
            serverComponentSensor = SensorHelper.getSensorEvents( node, typeList, entities, ipmiServiceExecutor );
            return serverComponentSensor;
        }
        catch ( HmsException e )
        {
            logger.error( "Exception while getting getFanEventList Data for node:" + serviceNode.getNodeID(), e );
            throw e;
        }
        catch ( Exception e )
        {
            logger.error( "Exception while getting getFanEventList Data for node:" + serviceNode.getNodeID(), e );
            throw new HmsException( e );
        }
    }

    /**
     * Provides a list of events generated by the Drive Bay(Slot) for a given Server node. Override this function to
     * implement Server specific response to generate Drive Bay(Slot) event list.
     *
     * @param serviceNode HMS node representing the Server for which Drive Bay(Slot) event list is requested.
     * @return List of Drive Bay(Slot) events generated by this Server.
     * @throws HmsException Throws HmsException if Sensor information is unavailable.
     */
    public List<ServerComponentEvent> getDriveBayEventList( ServiceHmsNode serviceNode )
        throws HmsException
    {
        ServiceServerNode node = (ServiceServerNode) serviceNode;
        List<ServerComponentEvent> serverComponentSensor = null;
        List<EntityId> entities = null;
        List<SensorType> typeList = new ArrayList<SensorType>();
        try
        {
            typeList.add( SensorType.DriveBay );
            serverComponentSensor = SensorHelper.getSensorEvents( node, typeList, entities, ipmiServiceExecutor );
            return serverComponentSensor;
        }
        catch ( HmsException e )
        {
            logger.error( "Exception while getting getDriveBayEventList Data for node:" + serviceNode.getNodeID(), e );
            throw e;
        }
        catch ( Exception e )
        {
            logger.error( "Exception while getting getDriveBayEventList Data for node:" + serviceNode.getNodeID(), e );
            throw new HmsException( e );
        }
    }

    /**
     * Provides a list of events generated by the Power Unit for a given Server node. Override this function to
     * implement Server specific response to generate Power Unit event list.
     *
     * @param serviceNode HMS node representing the Server for which Power Unit event list is requested.
     * @return List of Power Unit events generated by this Server.
     * @throws HmsException Throws HmsException if Sensor information is unavailable.
     */
    public List<ServerComponentEvent> getPowerUnitEventList( ServiceHmsNode serviceNode )
        throws HmsException
    {
        ServiceServerNode node = (ServiceServerNode) serviceNode;
        List<ServerComponentEvent> serverComponentSensor = null;
        List<EntityId> entities = null;
        List<SensorType> typeList = new ArrayList<SensorType>();
        try
        {
            typeList.add( SensorType.PowerUnit );
            serverComponentSensor = SensorHelper.getSensorEvents( node, typeList, entities, ipmiServiceExecutor );
            return serverComponentSensor;
        }
        catch ( HmsException e )
        {
            logger.error( "Exception while getting getPowerUnitEventList Data for node:" + serviceNode.getNodeID(), e );
            throw e;
        }
        catch ( Exception e )
        {
            logger.error( "Exception while getting getPowerUnitEventList Data for node:" + serviceNode.getNodeID(), e );
            throw new HmsException( e );
        }
    }

    /**
     * Provides a list of events generated by the NIC for a given Server node. Override this function to implement
     * Server specific response to generate NIC event list.
     *
     * @param serviceNode HMS node representing the Server for which NIC event list is requested.
     * @return List of NIC events generated by this Server.
     * @throws HmsException Throws HmsException if Sensor information is unavailable.
     */
    public List<ServerComponentEvent> getNicEventList( ServiceHmsNode serviceNode )
        throws HmsException
    {
        ServiceServerNode node = (ServiceServerNode) serviceNode;
        List<ServerComponentEvent> serverComponentSensor = null;
        List<EntityId> entities = new ArrayList<EntityId>();
        List<SensorType> typeList = new ArrayList<SensorType>();
        // @TODO: Add sensor entities and types corresponding to sensors generating NIC events
        throw new HmsException( "Not implemented" );
    }

    /**
     * Provides a list of events generated by the BMC for a given Server node. Override this function to implement
     * Server specific response to generate BMC event list.
     *
     * @param serviceNode HMS node representing the Server for which BMC event list is requested.
     * @return List of BMC events generated by this Server.
     * @throws HmsException Throws HmsException if Sensor information is unavailable.
     */
    public List<ServerComponentEvent> getBmcEventList( ServiceHmsNode serviceNode )
        throws HmsException
    {
        ServiceServerNode node = (ServiceServerNode) serviceNode;
        List<ServerComponentEvent> serverComponentSensor = null;
        try
        {
            serverComponentSensor = BmcSensorHelper.getBmcSensor( node );
            return serverComponentSensor;
        }
        catch ( HmsException e )
        {
            logger.error( "Exception while getting getBmcEventList Data for node:" + serviceNode.getNodeID(), e );
            throw e;
        }
        catch ( Exception e )
        {
            logger.error( "Exception while getting getBmcEventList Data for node:" + serviceNode.getNodeID(), e );
            throw new HmsException( e );
        }
    }

    /**
     * Provides a list of events generated by the System for a given Server node. Override this function to implement
     * Server specific response to generate System event list.
     *
     * @param serviceNode HMS node representing the Server for which System event list is requested.
     * @return List of system events generated by this Server.
     * @throws HmsException Throws HmsException if Sensor information is unavailable.
     */
    public List<ServerComponentEvent> getSystemEventList( ServiceHmsNode serviceNode )
        throws HmsException
    {
        ServiceServerNode node = (ServiceServerNode) serviceNode;
        List<ServerComponentEvent> serverComponentSensor = null;
        List<EntityId> entities = null;
        List<SensorType> typeList = new ArrayList<SensorType>();
        try
        {
            typeList.add( SensorType.CriticalInterrupt );
            typeList.add( SensorType.SystemFirmwareProgess );
            serverComponentSensor = SensorHelper.getSensorEvents( node, typeList, entities, ipmiServiceExecutor );
            return serverComponentSensor;
        }
        catch ( HmsException e )
        {
            logger.error( "Exception while getting getSystemEventList Data for node:" + serviceNode.getNodeID(), e );
            throw e;
        }
        catch ( Exception e )
        {
            logger.error( "Exception while getting getSystemEventList Data for node:" + serviceNode.getNodeID(), e );
            throw new HmsException( e );
        }
    }

    @Override
    public List<HmsApi> getSupportedHmsApi( ServiceHmsNode serviceNode )
        throws HmsException
    {
        if ( serviceNode != null && serviceNode instanceof ServiceServerNode )
        {
            List<HmsApi> supportedAPI = new ArrayList<HmsApi>();
            try
            {
                supportedAPI.add( HmsApi.SYSTEM_INFO );
                supportedAPI.add( HmsApi.CPU_SENSOR_INFO );
                supportedAPI.add( HmsApi.MEMORY_SENSOR_INFO );
                supportedAPI.add( HmsApi.FAN_SENSOR_INFO );
                supportedAPI.add( HmsApi.SYSTEM_SENSOR_INFO );
                supportedAPI.add( HmsApi.STORAGE_SENSOR_INFO );
                supportedAPI.add( HmsApi.POWERUNIT_SENSOR_INFO );
                return supportedAPI;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while getting getSupportedHmsApi Status for node:" + serviceNode.getNodeID(),
                              e );
                throw new HmsException( e );
            }
        }
        else
        {
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    @Override
    public boolean powerOperations( ServiceHmsNode serviceHmsNode, PowerOperationAction powerOperationAction )
        throws HmsException
    {
        switch ( powerOperationAction )
        {
            case COLDRESET:
                return coldResetServer( serviceHmsNode );
            case POWERCYCLE:
                return powerCycleServer( serviceHmsNode );
            case HARDRESET:
                return powerResetServer( serviceHmsNode );
            case POWERDOWN:
                return powerDownServer( serviceHmsNode );
            case POWERUP:
                return powerUpServer( serviceHmsNode );
        }
        logger.error( "Error calling Power Operations function: " + powerOperationAction );
        throw new HmsException( "Power operation by name " + powerOperationAction + " not supported" );
    }

    @Override
    public String getManagementMacAddress( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            String macAddress = null;
            logger.debug( "Management Mac Address requested for Node (base ipmi implementation): " + node.getNodeID() );
            try
            {
                macAddress = ipmiServiceExecutor.getMacAddress( node );
                return macAddress;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while getting Mac Address for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while getting Mac Address for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in get Management Mac Address - node either null or inactive." );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    @Override
    public SelfTestResults runSelfTest( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            SelfTestResults selfTestResults = null;
            logger.debug( "Self Test requested for Node (base ipmi implementation: " + node.getNodeID() );
            try
            {
                selfTestResults = ipmiServiceExecutor.selfTest( node );
                return selfTestResults;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while getting Self Test Results for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while getting Self Test Results for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in run Self Test - node either null or inactive." );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    @Override
    public boolean setChassisIdentification( ServiceHmsNode serviceHmsNode, ChassisIdentifyOptions data )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode && data != null )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            logger.debug( "Chassis Identification requested from Node (base ipmi implementation): "
                + node.getNodeID() );
            try
            {
                boolean status = ipmiServiceExecutor.performChassisIdentification( node, data );
                return status;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while performing Chassis Identification operation for node:"
                    + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while performing Chassis Identification operation for node:"
                    + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in get Chassis Identification - node either null or invalid" );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    @Override
    public boolean setManagementIPAddress( ServiceHmsNode serviceHmsNode, String ipAddress )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation setManagementIPAddress not supported" );
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode, Integer recordCount, SelFetchDirection direction )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            SelInfo selInfo = null;
            logger.debug( "SEL Details requested for Node (base ipmi implementation): " + node.getNodeID() );
            try
            {
                selInfo = ipmiServiceExecutor.getSelDetails( node, recordCount, direction );
                return selInfo;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while getting Sel Details for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while getting Sel Details for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in get Sel Details - node either null or invalid." );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    @Override
    public boolean isHostManageable( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            logger.debug( "Is Host manageable request for Node (base ipmi implementation): " + node.getNodeID() );
            try
            {
                boolean status = ipmiServiceExecutor.isHostAvailable( node );
                return status;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception while getting Server Power Status for node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
            catch ( Exception e )
            {
                logger.error( "Exception while getting Server Power Status for node:" + serviceHmsNode.getNodeID(), e );
                throw new HmsException( e );
            }
        }
        else
        {
            logger.error( "Error in get Server Power Status - node either null or invalid" );
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    @Override
    public List<PhysicalMemory> getPhysicalMemoryInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation getPhysicalMemoryInfo not supported" );
    }

    @Override
    public List<StorageControllerInfo> getStorageControllerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation get Storage Controller Info not supported" );
    }

    @Override
    public boolean createManagementUser( ServiceHmsNode serviceHmsNode, BmcUser bmcUser )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation createManagementUser not supported" );
    }

    public void setFruReadPacketSize( int fruReadPacketSize )
    {
        this.fruReadPacketSize = fruReadPacketSize;
    }

    public void setIpmiServiceExecutor( IpmiService ipmiServiceExecutor )
    {
        this.ipmiServiceExecutor = ipmiServiceExecutor;
    }
}
