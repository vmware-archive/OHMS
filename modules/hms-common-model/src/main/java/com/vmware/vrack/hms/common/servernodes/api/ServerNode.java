/* ********************************************************************************
 * ServerNode.java
 *
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServerItemBoardInfo;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServerItemHypervisorInfo;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.configuration.ServerItem;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.CallbackRequestFactory;
import com.vmware.vrack.hms.common.notification.EventType;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.fan.FanInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.powerunit.PowerUnitInfo;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;

@JsonIgnoreProperties( ignoreUnknown = true )
public class ServerNode
    extends HmsNode
{
    private static Logger logger = Logger.getLogger( ServerNode.class );

    private String ibIpAddress = "0.0.0.0";

    private String osUserName;

    private String osPassword;

    private String osEncodedPassword;

    private String oobMacAddress = "";

    private String uuid = "";

    private List<BmcUser> bmcUserList = new ArrayList<BmcUser>();

    private SelfTestResults selfTestResults;

    private AcpiPowerState acpiPowerState;

    private List<CPUInfo> cpuInfo;

    private List<FanInfo> fanInfo;

    private String boardProductName;

    private String boardVendor; // Board Manufacturer

    private String boardSerialNumber;

    private String boardPartNumber;

    private String mfgDate;

    private SystemBootOptions sytemBootOptions;

    private List<EthernetController> ethernetController;

    private String location; // physical location of server

    private List<HddInfo> hddInfo;

    private List<StorageControllerInfo> storageControllerInfo;

    private String hypervisorName;

    private String hypervisorProvider;

    private List<PowerUnitInfo> powerUnitInfo;

    private List<PhysicalMemory> physicalMemoryInfo;

    private SelInfo selInfo;

    private String biosVersion;

    private String biosReleaseDate;

    private List<HmsApi> supportedHMSAPI;

    public ServerNode()
    {
    }

    public ServerNode( String nodeID, String ipAddress, String managementUserName, String managementUserPassword )
    {
        this.nodeID = nodeID;
        this.managementIp = ipAddress;
        this.managementUserName = managementUserName;
        this.managementUserPassword = managementUserPassword;
    }

    public String getOsUserName()
    {
        return osUserName;
    }

    public void setOsUserName( String osUserName )
    {
        this.osUserName = osUserName;
    }

    public String getOsPassword()
    {
        return osPassword;
    }

    public void setOsPassword( String osPassword )
    {
        this.osPassword = osPassword;
    }

    @JsonIgnore
    public String getOsEncodedPassword()
    {
        return osEncodedPassword;
    }

    public void setOsEncodedPassword( String osEncodedPassword )
    {
        this.osEncodedPassword = osEncodedPassword;
    }

    public String getIbIpAddress()
    {
        return ibIpAddress;
    }

    public void setIbIpAddress( String nodeIpAddress )
    {
        this.ibIpAddress = nodeIpAddress;
    }

    @Override
    public Map<String, Object> getNodeOsDetails()
    {
        return nodeOsDetails;
    }

    public String getOobMacAddress()
    {
        return oobMacAddress;
    }

    @JsonIgnore
    public String getUuid()
    {
        return uuid;
    }

    public void setUuid( String uuid )
    {
        this.uuid = uuid;
    }

    public void setOobMacAddress( String oobMacAddress )
    {
        this.oobMacAddress = oobMacAddress;
    }

    @Override
    public void broadcastNodeFailureEvent( List<Map<String, String>> data )
    {
        this.notifyObservers( CallbackRequestFactory.getNotificationRequest( EventType.HOST_FAILURE, this.nodeID, data ) );
    }

    @Override
    public void broadcastNodeAvailableEvent( List<Map<String, String>> data )
    {
        this.notifyObservers( CallbackRequestFactory.getNotificationRequest( EventType.HOST_UP, this.nodeID, data ) );
    }

    public List<BmcUser> getBmcUserList()
    {
        return bmcUserList;
    }

    public void setBmcUserList( List<BmcUser> bmcUserList )
    {
        this.bmcUserList = bmcUserList;
    }

    public SelfTestResults getSelfTestResults()
    {
        return selfTestResults;
    }

    public void setSelfTestResults( SelfTestResults selfTestResults )
    {
        this.selfTestResults = selfTestResults;
    }

    public AcpiPowerState getAcpiPowerState()
    {
        return acpiPowerState;
    }

    public void setAcpiPowerState( AcpiPowerState acpiPowerState )
    {
        this.acpiPowerState = acpiPowerState;
    }

    public List<CPUInfo> getCpuInfo()
    {
        return cpuInfo;
    }

    public void setCpuInfo( List<CPUInfo> cpuInfo )
    {
        this.cpuInfo = cpuInfo;
    }

    public List<FanInfo> getFanInfo()
    {
        return fanInfo;
    }

    public void setFanInfo( List<FanInfo> fanInfo )
    {
        this.fanInfo = fanInfo;
    }

    public String getBoardProductName()
    {
        return boardProductName;
    }

    public void setBoardProductName( String boardProductName )
    {
        this.boardProductName = boardProductName;
    }

    public String getBoardVendor()
    {
        return boardVendor;
    }

    public void setBoardVendor( String boardVendor )
    {
        this.boardVendor = boardVendor;
    }

    public String getBoardSerialNumber()
    {
        return boardSerialNumber;
    }

    public void setBoardSerialNumber( String boardSerialNumber )
    {
        this.boardSerialNumber = boardSerialNumber;
    }

    public String getBoardPartNumber()
    {
        return boardPartNumber;
    }

    public void setBoardPartNumber( String boardPartNumber )
    {
        this.boardPartNumber = boardPartNumber;
    }

    public String getBoardMfgDate()
    {
        return mfgDate;
    }

    public void setBoardMfgDate( String mfgDate )
    {
        this.mfgDate = mfgDate;
    }

    public SystemBootOptions getSytemBootOptions()
    {
        return sytemBootOptions;
    }

    public void setSytemBootOptions( SystemBootOptions sytemBootOptions )
    {
        this.sytemBootOptions = sytemBootOptions;
    }

    public List<EthernetController> getEthernetControllerList()
    {
        return ethernetController;
    }

    public void setEthernetControllerList( List<EthernetController> ethernetController )
    {
        this.ethernetController = ethernetController;
    }

    public void setEthernetController( EthernetController ethernet )
    {
        if ( this.ethernetController == null && ethernet != null )
        {
            ArrayList<EthernetController> controllers = new ArrayList<EthernetController>();
            controllers.add( ethernet );
            setEthernetControllerList( controllers );
        }
        else if ( ethernetController != null && !getEthernetControllerList().contains( ethernet ) )
        {
            getEthernetControllerList().add( ethernet );
        }
    }

    private void notifyHostChange( EventType event, Object data )
    {
        if ( this.countObservers() > 0 )
        {
            this.setChanged();
            TypeReference<List<Map<String, String>>> ref = new TypeReference<List<Map<String, String>>>()
            {
            };
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, String>> eventData = mapper.convertValue( data, ref );
            this.notifyObservers( CallbackRequestFactory.getNotificationRequest( event, String.valueOf( this.nodeID ),
                                                                                 eventData ) );
        }
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation( String location )
    {
        this.location = location;
    }

    public List<HddInfo> getHddInfo()
    {
        return hddInfo;
    }

    public void setHddInfo( List<HddInfo> hddInfo )
    {
        this.hddInfo = hddInfo;
    }

    public List<StorageControllerInfo> getStorageControllerInfo()
    {
        return storageControllerInfo;
    }

    public void setStorageControllerInfo( List<StorageControllerInfo> storageControllerInfo )
    {
        this.storageControllerInfo = storageControllerInfo;
    }

    @Override
    @JsonIgnore
    public ServiceHmsNode getServiceObject()
        throws HmsException
    {
        ServiceServerNode serviceServerNode = new ServiceServerNode();
        serviceServerNode.setNodeID( nodeID );
        serviceServerNode.setManagementIp( managementIp );
        serviceServerNode.setManagementUserName( managementUserName );
        serviceServerNode.setManagementUserPassword( managementUserPassword );
        serviceServerNode.setIbIpAddress( ibIpAddress );
        serviceServerNode.setOsUserName( osUserName );
        serviceServerNode.setOsPassword( osPassword );
        serviceServerNode.setSshPort( this.getSshPort() );
        // SEnding mac address for now, for hac in NIC info. But in future, we will stop sending this property
        serviceServerNode.setOobMacAddress( oobMacAddress );
        serviceServerNode.setBoardVendor( boardVendor );
        serviceServerNode.setBoardProductName( boardProductName );
        return serviceServerNode;
    }

    /**
     * Uses provided Board service to figureout if "this" node is discoverable by it or NOT.
     *
     * @param boardService
     * @return
     * @throws Exception
     */
    /*
     * @JsonIgnore public boolean getNodeDiscoverabilityState(IBoardService boardService) { boolean
     * isBoardDiscoverableViaBoardService = false; if(boardService != null) { //Check if the Board is Discoverable or
     * NOT List<ServiceHmsNode> serviceHmsNodes = new ArrayList<ServiceHmsNode>(); try {
     * serviceHmsNodes.add(this.getServiceObject()); List<ServiceHmsNode> retServiceHmsNodes =
     * boardService.discoverServerInventory(serviceHmsNodes); if(retServiceHmsNodes != null && retServiceHmsNodes.size()
     * == 1) { isBoardDiscoverableViaBoardService = true; } else { isBoardDiscoverableViaBoardService = false; } } catch
     * (Exception e) { isBoardDiscoverableViaBoardService = false; } } return isBoardDiscoverableViaBoardService; }
     */
    public String getHypervisorName()
    {
        return hypervisorName;
    }

    public void setHypervisorName( String hypervisorName )
    {
        this.hypervisorName = hypervisorName;
    }

    public String getHypervisorProvider()
    {
        return hypervisorProvider;
    }

    public void setHypervisorProvider( String hypervisorProvider )
    {
        this.hypervisorProvider = hypervisorProvider;
    }

    public List<PowerUnitInfo> getPowerUnitInfo()
    {
        return powerUnitInfo;
    }

    public void setPowerUnitInfo( List<PowerUnitInfo> powerUnitInfo )
    {
        this.powerUnitInfo = powerUnitInfo;
    }

    public List<PhysicalMemory> getPhysicalMemoryInfo()
    {
        return physicalMemoryInfo;
    }

    public void setPhysicalMemoryInfo( List<PhysicalMemory> physicalMemoryInfo )
    {
        this.physicalMemoryInfo = physicalMemoryInfo;
    }

    public SelInfo getSelInfo()
    {
        return selInfo;
    }

    public void setSelInfo( SelInfo selInfo )
    {
        this.selInfo = selInfo;
    }

    /**
     * getServerComponent searches for a component based on the componnetType and its id
     *
     * @param source
     * @param component_id
     * @return AbstractServerComponent
     * @throws HmsException
     */
    public AbstractServerComponent getServerComponent( EventComponent source, String component_id )
        throws HmsException
    {
        try
        {
            AbstractServerComponent component = new AbstractServerComponent();
            component.id = component_id;
            switch ( source )
            {
                case CPU:
                    component.setComponent( ServerComponent.CPU );
                    if ( cpuInfo.contains( component ) )
                    {
                        return cpuInfo.get( cpuInfo.indexOf( component ) );
                    }
                case STORAGE:
                    component.setComponent( ServerComponent.STORAGE );
                    if ( hddInfo.contains( component ) )
                    {
                        return hddInfo.get( hddInfo.indexOf( component ) );
                    }
                case MEMORY:
                    component.setComponent( ServerComponent.MEMORY );
                    if ( physicalMemoryInfo.contains( component ) )
                    {
                        return physicalMemoryInfo.get( physicalMemoryInfo.indexOf( component ) );
                    }
                case POWER_UNIT:
                    component.setComponent( ServerComponent.POWERUNIT );
                    if ( powerUnitInfo.contains( component ) )
                    {
                        return powerUnitInfo.get( powerUnitInfo.indexOf( component ) );
                    }
                default:
                    throw new HmsException( "No such componnet Available with the Board API." );
            }
        }
        catch ( Exception e )
        {
            throw new HmsException( "Exception getting board component.", e );
        }
    }

    /**
     * getServerComponentList returns all the server components of type AbstractServerComponent
     *
     * @return List<AbstractServerComponent>
     */
    @JsonIgnore
    public List<AbstractServerComponent> getServerComponentList()
    {
        List<AbstractServerComponent> componentList = new ArrayList<AbstractServerComponent>();
        if ( cpuInfo != null )
            componentList.addAll( cpuInfo );
        if ( hddInfo != null )
            componentList.addAll( hddInfo );
        if ( physicalMemoryInfo != null )
            componentList.addAll( physicalMemoryInfo );
        if ( powerUnitInfo != null )
            componentList.addAll( powerUnitInfo );
        return componentList;
    }

    @JsonIgnore
    public List<HmsApi> getSupportedHMSAPI()
    {
        return supportedHMSAPI;
    }

    public void setSupportedHMSAPI( List<HmsApi> supportedHMSAPI )
    {
        this.supportedHMSAPI = supportedHMSAPI;
    }

    public String getBiosVersion()
    {
        return biosVersion;
    }

    public void setBiosVersion( String biosVersion )
    {
        this.biosVersion = biosVersion;
    }

    public String getBiosReleaseDate()
    {
        return biosReleaseDate;
    }

    public void setBiosReleaseDate( String biosReleaseDate )
    {
        this.biosReleaseDate = biosReleaseDate;
    }

    /**
     * Wrapper method that will get the ServerItem object for the node
     *
     * @return
     */
    @JsonIgnore
    public ServerItem getServerItemObject()
    {
        ServerItem serverItem = new ServerItem();
        serverItem.setIbIpAddress( this.getIbIpAddress() );
        serverItem.setIbPassword( this.getOsPassword() );
        serverItem.setIbUsername( this.getOsUserName() );
        serverItem.setId( this.getNodeID() );
        serverItem.setLocation( this.getLocation() );
        serverItem.setOobIpAddress( this.getManagementIp() );
        serverItem.setOobUsername( this.getManagementUserName() );
        serverItem.setOobPassword( this.getManagementUserPassword() );
        ServerItemHypervisorInfo hypervisorInfo = new ServerItemHypervisorInfo();
        hypervisorInfo.setName( this.getHypervisorName() );
        hypervisorInfo.setProvider( this.getHypervisorProvider() );
        serverItem.setHypervisorInfo( hypervisorInfo );
        ServerItemBoardInfo boardInfo = new ServerItemBoardInfo();
        boardInfo.setManufacturer( this.boardVendor );
        boardInfo.setModel( this.boardProductName );
        serverItem.setBoardInfo( boardInfo );
        return serverItem;
    }

    @JsonIgnore
    public String getOperationalStatus()
    {
        if ( this.isDiscoverable() && this.isPowered() )
        {
            return "true";
        }
        else
        {
            return "false";
        }
    }

    @JsonIgnore
    public ServerInfo getServerInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();
        ComponentIdentifier identifier = new ComponentIdentifier();
        serverInfo.setNodeId( serverNode.getNodeID() );
        serverInfo.setManagementIpAddress( serverNode.getManagementIp() );
        serverInfo.setInBandIpAddress( serverNode.getIbIpAddress() );
        serverInfo.setPowered( serverNode.isPowered() );
        serverInfo.setDiscoverable( serverNode.isDiscoverable() );
        serverInfo.setOsName( serverNode.getHypervisorName() );
        serverInfo.setOsVendor( serverNode.getHypervisorProvider() );
        identifier.setManufacturer( serverNode.getBoardVendor() );
        identifier.setProduct( serverNode.getBoardProductName() );
        identifier.setSerialNumber( serverNode.getBoardSerialNumber() );
        identifier.setPartNumber( serverNode.getBoardPartNumber() );
        identifier.setManufacturingDate( serverNode.getBoardMfgDate() );
        serverInfo.setComponentIdentifier( identifier );
        serverInfo.setLocation( serverNode.getLocation() );
        serverInfo.setOperationalStatus( serverNode.getOperationalStatus() );
        serverInfo.setAdminStatus( serverNode.getAdminStatus().toString() );
        return serverInfo;
    }
}
