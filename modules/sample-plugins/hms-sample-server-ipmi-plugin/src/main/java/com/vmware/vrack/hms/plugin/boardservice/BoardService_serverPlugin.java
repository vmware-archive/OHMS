/* ********************************************************************************
 * BoardService_serverPlugin.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.plugin.boardservice;

import com.vmware.vrack.hms.common.boardvendorservice.api.BaseIpmiImplementation;
import com.vmware.vrack.hms.common.boardvendorservice.api.BoardServiceImplementation;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.component.lifecycle.api.IComponentLifecycleManager;
import com.vmware.vrack.hms.common.component.lifecycle.resource.FileServerConfiguration;
import com.vmware.vrack.hms.common.component.lifecycle.resource.LifecycleOperationConfiguration;
import com.vmware.vrack.hms.common.component.lifecycle.resource.LifecycleOperationStatus;
import com.vmware.vrack.hms.common.component.lifecycle.resource.LowLevelComponent;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.OperationNotSupportedOOBException;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.fan.FanInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;
import com.vmware.vrack.hms.plugin.ServerPluginConstants;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the sample plugin to provide framework for ipmi hms-plugin. Partner plugin specific code goes in this file.
 * 1) The Class name should be BoardService_<pluginName>, which should match with the
 * annotation @BoardServiceImplementation(name = <pluginName>) 2) Change the board name and board manufacturer details.
 * 3) The plugin implements default functionality in BoardService_AbstractServerPlugin. Tweak the plugin by overriding
 * the functions in this class.
 *
 * @author VMware Inc.
 */
@BoardServiceImplementation( name = ServerPluginConstants.BOARD_NAME )
public class BoardService_serverPlugin
    extends BaseIpmiImplementation
    implements IComponentLifecycleManager
{
    private List<BoardInfo> supportedBoards;

    private static Logger logger = Logger.getLogger( BaseIpmiImplementation.class );

    public BoardService_serverPlugin()
    {
        super();
        BoardInfo boardInfo = new BoardInfo();
        boardInfo.setBoardManufacturer( ServerPluginConstants.BOARD_MANUFACTURER );
        boardInfo.setBoardProductName( ServerPluginConstants.BOARD_NAME );
        addSupportedBoard( boardInfo );
        init();
    }

    /**
     * Use this method to initialize all the settings for the abstract class.
     */
    private void init()
    {
        /* Set FruRead Packet size to default value. Higher values imply better performance */
        super.setFruReadPacketSize( 20 );
    }

    public BoardService_serverPlugin( List<BoardInfo> supportedBoards )
    {
        this.supportedBoards = supportedBoards;
    }

    /**
     * Utility function to add the supported board to the supportedBoards list
     *
     * @param boardInfo
     * @return
     */
    public boolean addSupportedBoard( BoardInfo boardInfo )
    {
        if ( supportedBoards == null )
        {
            supportedBoards = new ArrayList<BoardInfo>();
        }
        return supportedBoards.add( boardInfo );
    }

    @Override
    public List<BoardInfo> getSupportedBoard()
    {
        return supportedBoards;
    }

    /**
     * Function to add the API's which have been implemented out of band in plugin.
     *
     * @param serviceNode
     * @return
     * @throws HmsException
     */
    @Override
    public List<HmsApi> getSupportedHmsApi( ServiceHmsNode serviceNode )
        throws HmsException
    {
        if ( serviceNode != null && serviceNode instanceof ServiceServerNode )
        {
            List<HmsApi> supportedAPI = new ArrayList<HmsApi>();
            try
            {
                /* Get lis tof API's already implemented in BaseImplementation */
                supportedAPI = super.getSupportedHmsApi( serviceNode );
                /* Add the supportedAPI's here */
                /* supportedAPI.add(HmsApi.CPU_INFO); */
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

    /**
     * Function to get CPU INFO
     * 
     * @TODO Provide partner specific implementation of the API
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    @Override
    public List<CPUInfo> getCpuInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation getCpuInfo not supported" );
    }

    /**
     * Function to get Memory Info
     * 
     * @TODO Provide partner specific implementation of the API
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    @Override
    public List<PhysicalMemory> getPhysicalMemoryInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation getPhysicalMemoryInfo not supported" );
    }

    /**
     * Function to get Ethernet Controllers Info
     * 
     * @TODO Provide partner specific implementation of the API
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    @Override
    public List<EthernetController> getEthernetControllersInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation getEthernetControllersInfo not supported" );
    }

    /**
     * Function to get HDD Info
     * 
     * @TODO Provide partner specific implementation of the API
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    @Override
    public List<HddInfo> getHddInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation getHddInfo not supported" );
    }

    /**
     * Function to get Fan Info
     * 
     * @TODO Provide partner specific implementation of the API
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    @Override
    public List<FanInfo> getFanInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation FAN info not supported" );
    }

    /**
     * Function to upgrade firmware component
     *
     * @param lifecycleOperationConfiguration The lifecycle operation configuration
     * @return
     * @throws HmsException
     */
    @Override
    public LifecycleOperationStatus upgradeComponent( LifecycleOperationConfiguration lifecycleOperationConfiguration )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation upgradeComponent not supported" );
    }

    /**
     * Function to advertise component upgrade capabilities
     *
     * @param serviceHmsNode the service hms node
     * @return
     * @throws HmsException
     */
    @Override
    public List<LowLevelComponent> getUpgradeCapabilities( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation getUpgradeCapabilities not supported" );
    }

    /**
     * Function to provide status on an ongoing upgrade operation
     *
     * @param handle The unique operation id that HMS passed to plugin at the time of initiating a low level component
     *            lifecycle operation.
     * @param serviceHmsNode the service hms node
     * @return
     * @throws HmsException
     */
    @Override
    public LifecycleOperationStatus getUpgradeStatus( String handle, ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation getUpgradeStatus not supported" );
    }

    /**
     * Function to fetch current firmware version of a server component
     *
     * @param lowLevelComponent the low level component
     * @param serviceHmsNode the service hms node
     * @return
     * @throws HmsException
     */
    @Override
    public String getCurrentFirmwareVersion( LowLevelComponent lowLevelComponent, ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation getCurrentFirmwareVersion not supported" );
    }

    /**
     * Update fileserver configuration data
     *
     * @param fileServerConfiguration the file server configuration
     * @throws HmsException
     */
    @Override
    public void notifyFileServerConfiguration( FileServerConfiguration fileServerConfiguration )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "Operation notifyFileServerConfiguration not supported" );
    }
}
