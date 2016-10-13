/* ********************************************************************************
 * BoardService_Dummy.java
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

package com.vmware.vrack.hms.plugin.boardservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.api.BoardServiceImplementation;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.OperationNotSupportedOOBException;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.PowerOperationAction;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.BiosBootType;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceSelector;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceType;
import com.vmware.vrack.hms.common.resource.chassis.BootOptionsValidity;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.resource.sel.SelRecord;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.servernodes.api.fan.FanInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;
import com.vmware.vrack.hms.plugin.ServerPluginConstants;
import com.vmware.vrack.hms.plugin.command.chassis.ChassisState;

/*
 * This is a sample code which services the OOB agent requests with dummy data.
 * Actual implementation will utilize IPMI protocol to talk to and get the information from BMC.
 */

@BoardServiceImplementation( name = ServerPluginConstants.BOARD_NAME )
public class BoardService_Dummy
    implements IBoardService
{
    private String command;

    private List<BoardInfo> supportedBoards;

    private static Logger logger = Logger.getLogger( BoardService_Dummy.class );

    private boolean power_status = true;

    private boolean host_manageable = true;

    public BoardService_Dummy()
    {
        super();

        String osName = System.getProperty( "os.name" );
        if ( osName.contains( "Windows" ) )
            command = System.getProperty( "user.home" ) + "\\Win-ipmiutil\\ipmiutil";
        else if ( osName.contains( "Linux" ) )
            command = "ipmiutil";

        BoardInfo boardInfo = new BoardInfo();
        boardInfo.setBoardManufacturer( ServerPluginConstants.BOARD_MANUFACTURER );
        boardInfo.setBoardProductName( ServerPluginConstants.BOARD_NAME );
        addSupportedBoard( boardInfo );
    }

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

    public String getCommand()
    {
        return command;
    }

    public void setCommand( String command )
    {
        this.command = command;
    }

    @Override
    public List<ServerComponentEvent> getComponentEventList( ServiceHmsNode serviceNode, ServerComponent component )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
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
    public boolean getServerPowerStatus( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        try
        {
            return ChassisState.getChassisPowerStatus( serviceHmsNode, this.getCommand() );
        }
        catch ( IOException e )
        {
            throw new HmsException( e );
        }
    }

    @Override
    public boolean powerOperations( ServiceHmsNode serviceHmsNode, PowerOperationAction powerOperationAction )
        throws HmsException
    {
        try
        {
            switch ( powerOperationAction )
            {
                case COLDRESET:
                    host_manageable = true;
                    return ChassisState.coldResetChassis( serviceHmsNode, command );
                case HARDRESET:
                    host_manageable = true;
                    return ChassisState.hardResetChassis( serviceHmsNode, command );
                case POWERCYCLE:
                    host_manageable = true;
                    if(ChassisState.getChassisPowerStatus(serviceHmsNode, command))
                    return ChassisState.powerCycleChassis( serviceHmsNode, command );
                    else return ChassisState.powerUpChassis(serviceHmsNode, command);
                case POWERDOWN:
                    host_manageable = false;
                    return ChassisState.powerDownChassis( serviceHmsNode, command );
                case POWERUP:
                    host_manageable = true;
                    return ChassisState.powerUpChassis( serviceHmsNode, command );
                default:
                    break;
            }
            return false;
        }
        catch ( IOException e )
        {
            throw new HmsException( e );
        }
    }

    @Override
    public String getManagementMacAddress( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        return "MAC:ADDR:DUMMY:BOARD";
    }

    @Override
    public List<BmcUser> getManagementUsers( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        BmcUser user1 = new BmcUser();
        user1.setUserId( 1 );
        user1.setUserName( "dummy1" );
        BmcUser user2 = new BmcUser();
        user2.setUserId( 2 );
        user2.setUserName( "dummy2" );
        List<BmcUser> users = new ArrayList<BmcUser>();
        users.add( user1 );
        users.add( user2 );
        return users;
    }

    @Override
    public SelfTestResults runSelfTest( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        SelfTestResults results = new SelfTestResults();
        results.setSelfTestResult( "OK" );
        results.setSelfTestResultCode( (byte) 50 );
        return results;
    }

    @Override
    public AcpiPowerState getAcpiPowerState( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        AcpiPowerState state = new AcpiPowerState();
        state.setDeviceAcpiPowerState( "D0" );
        state.setSystemAcpiPowerState( "S0" );
        return state;
    }

    @Override
    public List<CPUInfo> getCpuInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        return null;
    }

    @Override
    public List<FanInfo> getFanInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        return null;
    }

    @Override
    public List<EthernetController> getEthernetControllersInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        return null;
    }

    @Override
    public SystemBootOptions getBootOptions( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        SystemBootOptions systemBootOptions = new SystemBootOptions();
        systemBootOptions.setBiosBootType( BiosBootType.Legacy );
        systemBootOptions.setBootDeviceInstanceNumber( 0 );
        systemBootOptions.setBootDeviceSelector( BootDeviceSelector.PXE );
        systemBootOptions.setBootDeviceType( BootDeviceType.Internal );
        systemBootOptions.setBootOptionsValidity( BootOptionsValidity.Persistent );
        return systemBootOptions;
    }

    @Override
    public boolean setBootOptions( ServiceHmsNode serviceHmsNode, SystemBootOptions data )
        throws HmsException
    {
        return true;
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        ServerNodeInfo serverNodeInfo = new ServerNodeInfo();
        ComponentIdentifier serverComponentIdentifier = new ComponentIdentifier();
        serverComponentIdentifier.setSerialNumber( "QTF3EV41900143" );
        serverComponentIdentifier.setPartNumber( "31S2RMB00H0" );
        serverComponentIdentifier.setManufacturingDate( "Mon May 19 13:22:00 2014" );
        serverNodeInfo.setComponentIdentifier( serverComponentIdentifier );
        return serverNodeInfo;
    }

    @Override
    public List<HddInfo> getHddInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        return null;
    }

    @Override
    public List<StorageControllerInfo> getStorageControllerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        return null;
    }

    @Override
    public boolean setChassisIdentification( ServiceHmsNode serviceHmsNode, ChassisIdentifyOptions data )
        throws HmsException
    {
        return true;
    }

    @Override
    public boolean setManagementIPAddress( ServiceHmsNode serviceHmsNode, String ipAddress )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "setManagementIPAddress not supported." );
    }

    @Override
    public boolean createManagementUser( ServiceHmsNode serviceHmsNode, BmcUser bmcUser )
        throws HmsException
    {
        throw new OperationNotSupportedOOBException( "createManagementUser not supported." );
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode, Integer recordCount, SelFetchDirection direction )
        throws HmsException
    {
        SelInfo selInfo = new SelInfo();
        selInfo.setFetchedSelCount( 2 );
        selInfo.setLastAddtionTimeStamp( new Date() );
        selInfo.setSelVersion( 2 );
        selInfo.setTotalSelCount( 2 );
        List<SelRecord> selRecords = new ArrayList<SelRecord>();
        selRecords.add( new SelRecord() );
        selRecords.add( new SelRecord() );
        selInfo.setSelRecords( selRecords );
        return selInfo;
    }

    @Override
    public boolean isHostManageable( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        return host_manageable;
    }

    @Override
    public List<PhysicalMemory> getPhysicalMemoryInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        return null;
    }

}
