/* ********************************************************************************
 * BoardServiceTest.java
 *
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.PowerOperationAction;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.servernodes.api.fan.FanInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;

/*
 * BoardServiceTest class is dummy class to unit test BoardServiceFactory
 */
@BoardServiceImplementation( name = "test-board" )
public class BoardServiceTest
    implements IBoardService
{
    private List<BoardInfo> supportedBoards;

    public BoardServiceTest()
    {
        super();
        BoardInfo boardInfo = new BoardInfo();
        boardInfo.setBoardManufacturer( "Test_Board_Manufacturer" );
        boardInfo.setBoardProductName( "Test_Board_Model" );
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

    @Override
    public List<ServerComponentEvent> getComponentEventList( ServiceHmsNode serviceNode, ServerComponent component )
        throws HmsException
    {
        List<ServerComponentEvent> events = new ArrayList<ServerComponentEvent>();
        switch ( component )
        {
            case CPU:
                ServerComponentEvent cpuEvent = new ServerComponentEvent();
                cpuEvent.setEventName( NodeEvent.CPU_TEMP_ABOVE_THRESHHOLD );
                cpuEvent.setEventId( "CPU 1 Temp" );
                cpuEvent.setComponentId( "CPU1" );
                cpuEvent.setUnit( EventUnitType.DEGREES_CELSIUS );
                cpuEvent.setValue( 78.0f );
                events.add( cpuEvent );
                cpuEvent = new ServerComponentEvent();
                cpuEvent.setEventName( NodeEvent.CPU_TEMP_BELOW_THRESHHOLD );
                cpuEvent.setEventId( "CPU 1 Temp" );
                cpuEvent.setComponentId( "CPU1" );
                cpuEvent.setUnit( EventUnitType.DEGREES_CELSIUS );
                cpuEvent.setValue( 18.0f );
                events.add( cpuEvent );
                cpuEvent = new ServerComponentEvent();
                cpuEvent.setEventName( NodeEvent.CPU_MACHINE_CHECK_ERROR );
                cpuEvent.setEventId( "CPU 1 machine check Error" );
                cpuEvent.setComponentId( "CPU1" );
                cpuEvent.setUnit( EventUnitType.DISCRETE );
                cpuEvent.setDiscreteValue( "CPU Machine Check Error" );
                events.add( cpuEvent );
                cpuEvent = new ServerComponentEvent();
                cpuEvent.setEventName( NodeEvent.CPU_INIT_ERROR );
                cpuEvent.setEventId( "CPU 1 Init Error" );
                cpuEvent.setComponentId( "CPU1" );
                cpuEvent.setUnit( EventUnitType.DISCRETE );
                cpuEvent.setDiscreteValue( "CPU Init Error" );
                events.add( cpuEvent );
                cpuEvent = new ServerComponentEvent();
                cpuEvent.setEventName( NodeEvent.CPU_THERMAL_TRIP );
                cpuEvent.setEventId( "CPU 1 Thermal trip" );
                cpuEvent.setComponentId( "CPU1" );
                cpuEvent.setUnit( EventUnitType.DISCRETE );
                cpuEvent.setDiscreteValue( "CPU 1 Thermal Trip" );
                events.add( cpuEvent );
                cpuEvent = new ServerComponentEvent();
                cpuEvent.setEventName( NodeEvent.CPU_POST_FAILURE );
                cpuEvent.setEventId( "CPU 1 POST failure" );
                cpuEvent.setComponentId( "CPU1" );
                cpuEvent.setUnit( EventUnitType.DISCRETE );
                cpuEvent.setDiscreteValue( "CPU 1 POST failure" );
                events.add( cpuEvent );
                cpuEvent = new ServerComponentEvent();
                cpuEvent.setEventName( NodeEvent.CPU_CAT_ERROR );
                cpuEvent.setEventId( "CPU 1 Catastrophic error" );
                cpuEvent.setComponentId( "CPU1" );
                cpuEvent.setUnit( EventUnitType.DISCRETE );
                cpuEvent.setDiscreteValue( "CPU 1 Catastrophic error" );
                events.add( cpuEvent );
                cpuEvent = new ServerComponentEvent();
                cpuEvent.setEventName( NodeEvent.PCH_TEMP_ABOVE_THRESHOLD );
                cpuEvent.setEventId( "PCH temperature above threshold" );
                cpuEvent.setComponentId( "PCH" );
                cpuEvent.setUnit( EventUnitType.DEGREES_CELSIUS );
                cpuEvent.setValue( 79.3f );
                events.add( cpuEvent );
                break;
            case MEMORY:
                ServerComponentEvent memoryEvent = new ServerComponentEvent();
                memoryEvent.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
                memoryEvent.setEventId( "Dimm 1 Temp" );
                memoryEvent.setComponentId( "DIMM1" );
                memoryEvent.setUnit( EventUnitType.DEGREES_CELSIUS );
                memoryEvent.setValue( 78.0f );
                events.add( memoryEvent );
                memoryEvent = new ServerComponentEvent();
                memoryEvent.setEventName( NodeEvent.MEMORY_ECC_ERROR );
                memoryEvent.setEventId( "Dimm 1 ECC error" );
                memoryEvent.setComponentId( "DIMM 1" );
                memoryEvent.setUnit( EventUnitType.DISCRETE );
                memoryEvent.setDiscreteValue( "Dimm 1 ECC error" );
                events.add( memoryEvent );
                break;
            case STORAGE:
                ServerComponentEvent hddEvent = new ServerComponentEvent();
                hddEvent.setEventName( NodeEvent.HDD_DOWN );
                hddEvent.setEventId( "Hdd 1 Down" );
                hddEvent.setComponentId( "HDD1" );
                hddEvent.setUnit( EventUnitType.DISCRETE );
                hddEvent.setDiscreteValue( "Hdd 1 DOWN" );
                events.add( hddEvent );
                hddEvent = new ServerComponentEvent();
                hddEvent.setEventName( NodeEvent.HDD_READ_ERROR );
                hddEvent.setEventId( "HDD 1 Excessive Read Error" );
                hddEvent.setComponentId( "HDD1" );
                hddEvent.setUnit( EventUnitType.DISCRETE );
                hddEvent.setDiscreteValue( "HDD 1 Excessive Read Error" );
                events.add( hddEvent );
                hddEvent = new ServerComponentEvent();
                hddEvent.setEventName( NodeEvent.HDD_WRITE_ERROR );
                hddEvent.setEventId( "HDD 1 Excessive write Error" );
                hddEvent.setComponentId( "HDD1" );
                hddEvent.setUnit( EventUnitType.DISCRETE );
                hddEvent.setDiscreteValue( "HDD 1 Excessive write Error" );
                events.add( hddEvent );
                hddEvent = new ServerComponentEvent();
                hddEvent.setEventName( NodeEvent.HDD_TEMP_ABOVE_THRESHOLD );
                hddEvent.setEventId( "HDD 1 temp above threshold" );
                hddEvent.setComponentId( "HDD1" );
                hddEvent.setUnit( EventUnitType.DEGREES_CELSIUS );
                hddEvent.setValue( 55.0f );
                events.add( hddEvent );
                hddEvent = new ServerComponentEvent();
                hddEvent.setEventName( NodeEvent.HDD_WEAROUT_ABOVE_THRESHOLD );
                hddEvent.setEventId( "HDD 1 wearout above threshold" );
                hddEvent.setComponentId( "HDD1" );
                hddEvent.setUnit( EventUnitType.DISCRETE );
                hddEvent.setDiscreteValue( "HDD 1 wearout above threshold" );
                events.add( hddEvent );
                hddEvent = new ServerComponentEvent();
                hddEvent.setEventName( NodeEvent.HDD_SLOT_FULL );
                hddEvent.setEventId( "HDD 1 " );
                hddEvent.setComponentId( "HDD1" );
                hddEvent.setUnit( EventUnitType.DISCRETE );
                hddEvent.setDiscreteValue( "HDD 1 wearout above threshold" );
                events.add( hddEvent );
                break;
            case NIC:
                ServerComponentEvent nicEvent = new ServerComponentEvent();
                nicEvent.setEventName( NodeEvent.NIC_TEMPERATURE_ABOVE_THRESHHOLD );
                nicEvent.setEventId( "NIC 1 temp above threshold" );
                nicEvent.setComponentId( "NIC1" );
                nicEvent.setUnit( EventUnitType.DEGREES_CELSIUS );
                nicEvent.setValue( 66.0f );
                events.add( nicEvent );
                nicEvent = new ServerComponentEvent();
                nicEvent.setEventName( NodeEvent.NIC_LINK_DOWN );
                nicEvent.setEventId( "NIC 1 link down" );
                nicEvent.setComponentId( "NIC1" );
                nicEvent.setUnit( EventUnitType.DISCRETE );
                nicEvent.setDiscreteValue( "NIC 1 link down" );
                events.add( nicEvent );
                nicEvent = new ServerComponentEvent();
                nicEvent.setEventName( NodeEvent.NIC_PORT_DOWN );
                nicEvent.setEventId( "NIC 1 port down" );
                nicEvent.setComponentId( "NIC1" );
                nicEvent.setUnit( EventUnitType.DISCRETE );
                nicEvent.setDiscreteValue( "NIC 1 port down" );
                events.add( nicEvent );
                nicEvent = new ServerComponentEvent();
                nicEvent.setEventName( NodeEvent.NIC_PACKET_DROP_ABOVE_THRESHHOLD );
                nicEvent.setEventId( "NIC 1 packet above threshold" );
                nicEvent.setComponentId( "NIC1" );
                nicEvent.setUnit( EventUnitType.PERCENT );
                nicEvent.setValue( 23.0f );
                events.add( nicEvent );
                nicEvent = new ServerComponentEvent();
                nicEvent.setEventName( NodeEvent.HDD_TEMP_ABOVE_THRESHOLD );
                nicEvent.setEventId( "HDD 1 temp above threshold" );
                nicEvent.setComponentId( "HDD1" );
                nicEvent.setUnit( EventUnitType.DEGREES_CELSIUS );
                nicEvent.setValue( 55.0f );
                events.add( nicEvent );
                nicEvent = new ServerComponentEvent();
                nicEvent.setEventName( NodeEvent.HDD_WEAROUT_ABOVE_THRESHOLD );
                nicEvent.setEventId( "HDD 1 wearout above threshold" );
                nicEvent.setComponentId( "HDD1" );
                nicEvent.setUnit( EventUnitType.DISCRETE );
                nicEvent.setDiscreteValue( "HDD 1 wearout above threshold" );
                events.add( nicEvent );
                break;
            case BMC:
                ServerComponentEvent bmcEvent = new ServerComponentEvent();
                bmcEvent.setEventName( NodeEvent.BMC_NOT_REACHABLE );
                bmcEvent.setEventId( "BMC not reachable" );
                bmcEvent.setComponentId( "BMC" );
                bmcEvent.setUnit( EventUnitType.DISCRETE );
                bmcEvent.setDiscreteValue( "BMC not reachable" );
                events.add( bmcEvent );
                bmcEvent = new ServerComponentEvent();
                bmcEvent.setEventName( NodeEvent.BMC_AUTHENTICATION_FAILURE );
                bmcEvent.setEventId( "BMC authentication failure" );
                bmcEvent.setComponentId( "BMC" );
                bmcEvent.setUnit( EventUnitType.DISCRETE );
                bmcEvent.setDiscreteValue( "BMC authentication failure" );
                events.add( bmcEvent );
                bmcEvent = new ServerComponentEvent();
                bmcEvent.setEventName( NodeEvent.BMC_FAILURE );
                bmcEvent.setEventId( "BMC management failure" );
                bmcEvent.setComponentId( "BMC" );
                bmcEvent.setUnit( EventUnitType.DISCRETE );
                bmcEvent.setDiscreteValue( "BMC management failure" );
                events.add( bmcEvent );
                break;
            case SYSTEM:
                ServerComponentEvent systemEvent = new ServerComponentEvent();
                systemEvent.setEventName( NodeEvent.SYSTEM_PCIE_ERROR );
                systemEvent.setEventId( "Server PCIe error" );
                systemEvent.setComponentId( "SYSTEM" );
                systemEvent.setUnit( EventUnitType.DISCRETE );
                systemEvent.setDiscreteValue( "Server PCIe error" );
                events.add( systemEvent );
                systemEvent = new ServerComponentEvent();
                systemEvent.setEventName( NodeEvent.SYSTEM_POST_ERROR );
                systemEvent.setEventId( "Server POST error" );
                systemEvent.setComponentId( "SYSTEM" );
                systemEvent.setUnit( EventUnitType.DISCRETE );
                systemEvent.setDiscreteValue( "Server POST error" );
                events.add( systemEvent );
                systemEvent = new ServerComponentEvent();
                systemEvent.setEventName( NodeEvent.SYSTEM_POST_ERROR );
                systemEvent.setEventId( "Server POST error" );
                systemEvent.setComponentId( "SYSTEM" );
                systemEvent.setUnit( EventUnitType.DISCRETE );
                systemEvent.setDiscreteValue( "Server POST error" );
                events.add( systemEvent );
                break;
            case HMS:
                ServerComponentEvent hmsEvent = new ServerComponentEvent();
                hmsEvent.setEventName( NodeEvent.HMS_AGENT_NON_RESPONSIVE );
                hmsEvent.setEventId( "HMS agent responding" );
                hmsEvent.setComponentId( "HMS" );
                hmsEvent.setUnit( EventUnitType.DISCRETE );
                hmsEvent.setDiscreteValue( "Hms Agent responsive" );
                events.add( hmsEvent );
                break;
            case SERVER:
                ServerComponentEvent serverEvent = new ServerComponentEvent();
                serverEvent.setEventName( NodeEvent.HOST_OS_NOT_RESPONSIVE );
                serverEvent.setEventId( "Host OS not responsive" );
                serverEvent.setComponentId( "SERVER" );
                serverEvent.setUnit( EventUnitType.DISCRETE );
                serverEvent.setDiscreteValue( "Host OS not responsive" );
                events.add( serverEvent );
                serverEvent = new ServerComponentEvent();
                serverEvent.setEventName( NodeEvent.HOST_DOWN );
                serverEvent.setEventId( "Host Down" );
                serverEvent.setComponentId( "SERVER" );
                serverEvent.setUnit( EventUnitType.DISCRETE );
                serverEvent.setDiscreteValue( "Host Down" );
                events.add( serverEvent );
                serverEvent = new ServerComponentEvent();
                serverEvent.setEventName( NodeEvent.HOST_UP );
                serverEvent.setEventId( "Host up" );
                serverEvent.setComponentId( "SERVER" );
                serverEvent.setUnit( EventUnitType.DISCRETE );
                serverEvent.setDiscreteValue( "Host up" );
                events.add( serverEvent );
                break;
            default:
                throw new HmsException( "Not supported yet" );
        }
        return events;
    }

    public static void main( String[] args )
        throws HmsException
    {
        // TODO: Remove
        BoardServiceTest test = new BoardServiceTest();
        List<ServerComponentEvent> events = test.getComponentEventList( null, ServerComponent.CPU );
        System.out.println( "Total events:" + events.size() );
        for ( ServerComponentEvent event : events )
            System.out.println( "Event:" + event.getEventName() );
    }

    @Override
    public List<HmsApi> getSupportedHmsApi( ServiceHmsNode serviceNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return Arrays.asList( HmsApi.values() );
    }

    @Override
    public boolean getServerPowerStatus( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean powerOperations( ServiceHmsNode serviceHmsNode, PowerOperationAction powerOperationAction )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getManagementMacAddress( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BmcUser> getManagementUsers( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        BmcUser user = new BmcUser();
        user.setUserId( 1 );
        user.setUserName( "test" );
        BmcUser user2 = new BmcUser();
        user.setUserId( 2 );
        user.setUserName( "test2" );
        List<BmcUser> users = new ArrayList<BmcUser>();
        users.add( user );
        users.add( user2 );
        return users;
    }

    @Override
    public SelfTestResults runSelfTest( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AcpiPowerState getAcpiPowerState( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CPUInfo> getCpuInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<FanInfo> getFanInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EthernetController> getEthernetControllersInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SystemBootOptions getBootOptions( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean setBootOptions( ServiceHmsNode serviceHmsNode, SystemBootOptions data )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<HddInfo> getHddInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean setChassisIdentification( ServiceHmsNode serviceHmsNode, ChassisIdentifyOptions data )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setManagementIPAddress( ServiceHmsNode serviceHmsNode, String ipAddress )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean createManagementUser( ServiceHmsNode serviceHmsNode, BmcUser bmcUser )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode, Integer recordCount, SelFetchDirection direction )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isHostManageable( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<PhysicalMemory> getPhysicalMemoryInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<StorageControllerInfo> getStorageControllerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
