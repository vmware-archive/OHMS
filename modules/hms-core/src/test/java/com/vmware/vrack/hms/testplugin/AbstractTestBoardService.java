/* ********************************************************************************
 * AbstractTestBoardService.java
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

package com.vmware.vrack.hms.testplugin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.vmware.vrack.hms.common.StatusEnum;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
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
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.resource.sel.SelRecord;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.common.servernodes.api.SpeedInfo;
import com.vmware.vrack.hms.common.servernodes.api.SpeedUnit;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.servernodes.api.fan.FanInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.nic.NicStatus;
import com.vmware.vrack.hms.common.servernodes.api.nic.PortInfo;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;

public abstract class AbstractTestBoardService
    implements IBoardService
{

    private boolean power_status = true;

    private boolean host_manageable = true;

    @Override
    public List<ServerComponentEvent> getComponentEventList( ServiceHmsNode serviceNode, ServerComponent component )
        throws HmsException
    {

        List<ServerComponentEvent> serverComponentSensor = new ArrayList<>();
        ServerComponentEvent serverComponentSensorTemp = null;

        if ( component == ServerComponent.STORAGE )
        {
            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "HDD 0 status" );
            serverComponentSensorTemp.setComponentId( "HDD_0" );
            serverComponentSensorTemp.setEventName( NodeEvent.HDD_DOWN );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "DriveFault" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "HDD 1 status" );
            serverComponentSensorTemp.setComponentId( "HDD_1" );
            serverComponentSensorTemp.setEventName( NodeEvent.HDD_DOWN );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "DriveFault" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "HDD 2 status" );
            serverComponentSensorTemp.setComponentId( "HDD_2" );
            serverComponentSensorTemp.setEventName( NodeEvent.HDD_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "InCriticalArray" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "HDD 3 status" );
            serverComponentSensorTemp.setComponentId( "HDD_3" );
            serverComponentSensorTemp.setEventName( NodeEvent.HDD_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "HotSpare" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "HDD 4 status" );
            serverComponentSensorTemp.setComponentId( "HDD_4" );
            serverComponentSensorTemp.setEventName( NodeEvent.HDD_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "RebuildRemapAborted" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "HDD 5 status" );
            serverComponentSensorTemp.setEventId( "HDD_5" );
            serverComponentSensorTemp.setEventName( NodeEvent.HDD_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "PredictiveFailure" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "HDD 6 status" );
            serverComponentSensorTemp.setComponentId( "HDD_6" );
            serverComponentSensorTemp.setEventName( NodeEvent.HDD_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "InCriticalArray" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "HDD 7 status" );
            serverComponentSensorTemp.setComponentId( "HDD_7" );
            serverComponentSensorTemp.setEventName( NodeEvent.HDD_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "InFailedArray" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "HDD 8 status" );
            serverComponentSensorTemp.setComponentId( "HDD_8" );
            serverComponentSensorTemp.setEventName( NodeEvent.HDD_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "HotSpare" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "HDD 9 status" );
            serverComponentSensorTemp.setComponentId( "HDD_9" );
            serverComponentSensorTemp.setEventName( NodeEvent.HDD_DOWN );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "DriveFault" );

            serverComponentSensor.add( serverComponentSensorTemp );
        }
        if ( component == ServerComponent.CPU )
        {
            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "CPU0 Temp" );
            serverComponentSensorTemp.setComponentId( "0" );
            serverComponentSensorTemp.setEventName( NodeEvent.CPU_TEMP_ABOVE_THRESHHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 93 );
            serverComponentSensorTemp.setDiscreteValue( "AboveUpperNonCritical" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "CPU1 Temp" );
            serverComponentSensorTemp.setComponentId( "1" );
            serverComponentSensorTemp.setEventName( NodeEvent.CPU_TEMP_BELOW_THRESHHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 20 );
            serverComponentSensorTemp.setDiscreteValue( "BelowLowerNonCritical" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Processor 0 Hot" );
            serverComponentSensorTemp.setComponentId( "Proc 0 Hot" );
            serverComponentSensorTemp.setEventName( NodeEvent.CPU_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "StateAsserted" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Processor 1 Hot" );
            serverComponentSensorTemp.setComponentId( "Proc 1 Hot" );
            serverComponentSensorTemp.setEventName( NodeEvent.CPU_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "StateAsserted" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Processor 0" );
            serverComponentSensorTemp.setComponentId( "Processor 0" );
            serverComponentSensorTemp.setEventName( NodeEvent.CPU_INIT_ERROR );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "Frb3ProcessorStartupFailure" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Processor 0" );
            serverComponentSensorTemp.setComponentId( "Processor 0" );
            serverComponentSensorTemp.setEventName( NodeEvent.CPU_MACHINE_CHECK_ERROR );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "MachineCheckException" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Processor 1" );
            serverComponentSensorTemp.setComponentId( "Processor 1" );
            serverComponentSensorTemp.setEventName( NodeEvent.CPU_THERMAL_TRIP );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "ProcessorThermalTrip" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Processor 1" );
            serverComponentSensorTemp.setComponentId( "Processor 1" );
            serverComponentSensorTemp.setEventName( NodeEvent.CPU_POST_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "Frb2HangInPostFailure" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Processor 1" );
            serverComponentSensorTemp.setComponentId( "Processor 1" );
            serverComponentSensorTemp.setEventName( NodeEvent.CPU_CAT_ERROR );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "Ierr" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "PCH Temp" );
            serverComponentSensorTemp.setComponentId( "Platform controller hub" );
            serverComponentSensorTemp.setEventName( NodeEvent.PCH_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 94 );

            serverComponentSensor.add( serverComponentSensorTemp );
        }
        if ( component == ServerComponent.MEMORY )
        {
            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_A0" );
            serverComponentSensorTemp.setComponentId( "DIMM_A0" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 95 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_A1" );
            serverComponentSensorTemp.setComponentId( "DIMM_A1" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 95 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_B0" );
            serverComponentSensorTemp.setComponentId( "DIMM_B0" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 95 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_B1" );
            serverComponentSensorTemp.setComponentId( "DIMM_B1" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 95 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_C0" );
            serverComponentSensorTemp.setComponentId( "DIMM_C0" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 95 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_C1" );
            serverComponentSensorTemp.setComponentId( "DIMM_C1" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 95 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_D0" );
            serverComponentSensorTemp.setComponentId( "DIMM_D0" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 95 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_D1" );
            serverComponentSensorTemp.setComponentId( "DIMM_D1" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 95 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_E0" );
            serverComponentSensorTemp.setComponentId( "DIMM_E0" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 95 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_E1" );
            serverComponentSensorTemp.setComponentId( "DIMM_E1" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMPERATURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 40 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_F0" );
            serverComponentSensorTemp.setComponentId( "DIMM_F0" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 96 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_F1" );
            serverComponentSensorTemp.setComponentId( "DIMM_F1" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMPERATURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 45 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_G0" );
            serverComponentSensorTemp.setComponentId( "DIMM_G0" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMPERATURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 42 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_G1" );
            serverComponentSensorTemp.setComponentId( "DIMM_G1" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 97 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_H0" );
            serverComponentSensorTemp.setComponentId( "DIMM_H0" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMP_ABOVE_THRESHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 96 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Temp_DIMM_H1" );
            serverComponentSensorTemp.setComponentId( "DIMM_H1" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_TEMPERATURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DEGREES_CELSIUS );
            serverComponentSensorTemp.setValue( 48 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "MEM_AB Hot" );
            serverComponentSensorTemp.setComponentId( "MEM_AB Hot" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "StateAsserted" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "MEM_CD Hot" );
            serverComponentSensorTemp.setComponentId( "MEM_CD Hot" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "StateAsserted" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "MEM_EF Hot" );
            serverComponentSensorTemp.setComponentId( "MEM_EF Hot" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "StateAsserted" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "MEM_GH Hot" );
            serverComponentSensorTemp.setComponentId( "MEM_GH Hot" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "StateAsserted" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Memory Error" );
            serverComponentSensorTemp.setComponentId( "MEM_ERR" );
            serverComponentSensorTemp.setEventName( NodeEvent.MEMORY_ECC_ERROR );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "UncorrectableECC" );

            serverComponentSensor.add( serverComponentSensorTemp );
        }
        if ( component == ServerComponent.FAN )
        {
            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "SYS_FAN1-1" );
            serverComponentSensorTemp.setComponentId( "FAN1-1" );
            serverComponentSensorTemp.setEventName( NodeEvent.FAN_SPEED_THRESHHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.RPM );
            serverComponentSensorTemp.setValue( 7000 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "SYS_FAN1-2" );
            serverComponentSensorTemp.setComponentId( "FAN1-2" );
            serverComponentSensorTemp.setEventName( NodeEvent.FAN_STATUS_NON_RECOVERABLE );
            serverComponentSensorTemp.setUnit( EventUnitType.RPM );
            serverComponentSensorTemp.setValue( 300 );
            serverComponentSensorTemp.setDiscreteValue( "BelowLowerNonRecoverable" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "SYS_FAN2-1" );
            serverComponentSensorTemp.setComponentId( "FAN2-1" );
            serverComponentSensorTemp.setEventName( NodeEvent.FAN_STATUS_NON_RECOVERABLE );
            serverComponentSensorTemp.setUnit( EventUnitType.RPM );
            serverComponentSensorTemp.setValue( 8000 );
            serverComponentSensorTemp.setDiscreteValue( "AboveUpperNonRecoverable" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "SYS_FAN2-2" );
            serverComponentSensorTemp.setComponentId( "FAN2-2" );
            serverComponentSensorTemp.setEventName( NodeEvent.FAN_SPEED_THRESHHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.RPM );
            serverComponentSensorTemp.setValue( 7000 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "SYS_FAN3-1" );
            serverComponentSensorTemp.setComponentId( "FAN3-1" );
            serverComponentSensorTemp.setEventName( NodeEvent.FAN_SPEED_THRESHHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.RPM );
            serverComponentSensorTemp.setValue( 7000 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "SYS_FAN3-2" );
            serverComponentSensorTemp.setComponentId( "FAN3-2" );
            serverComponentSensorTemp.setEventName( NodeEvent.FAN_SPEED_THRESHHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.RPM );
            serverComponentSensorTemp.setValue( 7500 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "SYS_FAN4-1" );
            serverComponentSensorTemp.setComponentId( "FAN4-1" );
            serverComponentSensorTemp.setEventName( NodeEvent.FAN_SPEED_THRESHHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.RPM );
            serverComponentSensorTemp.setValue( 7200 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "SYS_FAN4-2" );
            serverComponentSensorTemp.setComponentId( "FAN4-2" );
            serverComponentSensorTemp.setEventName( NodeEvent.FAN_SPEED_THRESHHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.RPM );
            serverComponentSensorTemp.setValue( 7600 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "SYS_FAN5-1" );
            serverComponentSensorTemp.setComponentId( "FAN5-1" );
            serverComponentSensorTemp.setEventName( NodeEvent.FAN_SPEED_THRESHHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.RPM );
            serverComponentSensorTemp.setValue( 7300 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "SYS_FAN5-2" );
            serverComponentSensorTemp.setComponentId( "FAN5-2" );
            serverComponentSensorTemp.setEventName( NodeEvent.FAN_SPEED_THRESHHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.RPM );
            serverComponentSensorTemp.setValue( 7600 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "SYS_FAN6-1" );
            serverComponentSensorTemp.setComponentId( "FAN6-1" );
            serverComponentSensorTemp.setEventName( NodeEvent.FAN_SPEED_THRESHHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.RPM );
            serverComponentSensorTemp.setValue( 7300 );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "SYS_FAN6-2" );
            serverComponentSensorTemp.setComponentId( "FAN6-2" );
            serverComponentSensorTemp.setEventName( NodeEvent.FAN_SPEED_THRESHHOLD );
            serverComponentSensorTemp.setUnit( EventUnitType.RPM );
            serverComponentSensorTemp.setValue( 7700 );

            serverComponentSensor.add( serverComponentSensorTemp );

        }
        if ( component == ServerComponent.POWERUNIT )
        {
            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Power Unit" );
            serverComponentSensorTemp.setComponentId( "POWER_UNIT" );
            serverComponentSensorTemp.setEventName( NodeEvent.POWER_UNIT_STATUS_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "PowerUnitFailure" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Power Supply 0" );
            serverComponentSensorTemp.setComponentId( "Power Supply 0" );
            serverComponentSensorTemp.setEventName( NodeEvent.POWER_UNIT_STATUS_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "PowerSupplyInputLost" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "Power Supply 1" );
            serverComponentSensorTemp.setComponentId( "Power Supply 1" );
            serverComponentSensorTemp.setEventName( NodeEvent.POWER_UNIT_STATUS_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "PowerSupplyFailureDetected" );

            serverComponentSensor.add( serverComponentSensorTemp );
        }
        if ( component == ServerComponent.BMC )
        {
            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "BMC STATUS" );
            serverComponentSensorTemp.setComponentId( "xx.xx.xx.xx" );
            serverComponentSensorTemp.setEventName( NodeEvent.BMC_NOT_REACHABLE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "BMC not reachable" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "BMC AUTHENTICATION" );
            serverComponentSensorTemp.setEventName( NodeEvent.BMC_AUTHENTICATION_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setComponentId( "xx.xx.xx.xx" );
            serverComponentSensorTemp.setDiscreteValue( "BMC Authentication Failed" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "BMC FAILURE" );
            serverComponentSensorTemp.setEventName( NodeEvent.BMC_FAILURE );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setComponentId( "xx.xx.xx.xx" );
            serverComponentSensorTemp.setDiscreteValue( "BMC Management Failure" );

            serverComponentSensor.add( serverComponentSensorTemp );

        }

        if ( component == ServerComponent.SYSTEM )
        {
            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "PCIE BUS ERROR" );
            serverComponentSensorTemp.setComponentId( "PCIE BUS ERROR" );
            serverComponentSensorTemp.setEventName( NodeEvent.SYSTEM_PCIE_ERROR );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "BusFatalError" );

            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventId( "BIOS" );
            serverComponentSensorTemp.setComponentId( "BIOS" );
            serverComponentSensorTemp.setEventName( NodeEvent.SYSTEM_POST_ERROR );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "StateAsserted" );

            serverComponentSensor.add( serverComponentSensorTemp );
        }

        if ( component == ServerComponent.STORAGE_CONTROLLER )
        {
            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventName( NodeEvent.STORAGE_CONTROLLER_DOWN );
            serverComponentSensorTemp.setEventId( "Storage Controller down" );
            serverComponentSensorTemp.setComponentId( "STORAGE_CONTROLLER1" );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "STORAGE_CONTROLLER1 down" );
            serverComponentSensor.add( serverComponentSensorTemp );

            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp = new ServerComponentEvent();
            serverComponentSensorTemp.setEventName( NodeEvent.STORAGE_CONTROLLER_UP );
            serverComponentSensorTemp.setEventId( "Storage Controller up" );
            serverComponentSensorTemp.setComponentId( "STORAGE_CONTROLLER1" );
            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
            serverComponentSensorTemp.setDiscreteValue( "STORAGE_CONTROLLER1 up" );
            serverComponentSensor.add( serverComponentSensorTemp );
        }

        return serverComponentSensor;

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

        return power_status;
    }

    @Override
    public boolean powerOperations( ServiceHmsNode serviceHmsNode, PowerOperationAction powerOperationAction )
        throws HmsException
    {

        switch ( powerOperationAction )
        {
            case COLDRESET:
                power_status = true;
                host_manageable = true;
                break;
            case HARDRESET:
                power_status = true;
                host_manageable = true;
                break;
            case POWERCYCLE:
                power_status = true;
                host_manageable = true;
                break;
            case POWERDOWN:
                power_status = false;
                host_manageable = false;
                break;
            case POWERUP:
                power_status = true;
                host_manageable = true;
                break;
            default:
                break;

        }
        return true;
    }

    @Override
    public String getManagementMacAddress( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {

        return "MAC:ADDR:TEST:BOARD";
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

        CPUInfo info = new CPUInfo();
        ComponentIdentifier componentIdentifier = new ComponentIdentifier();
        componentIdentifier.setProduct( "TEST CPU 0" );
        info.setId( "CPU_0" );
        info.setComponent( ServerComponent.CPU );
        info.setTotalCpuCores( 2 );
        componentIdentifier.setManufacturer( "TEST_VENDOR" );
        info.setMaxClockFrequency( 63727387L );
        info.setComponentIdentifier( componentIdentifier );

        List<CPUInfo> cpu = new ArrayList<CPUInfo>();
        cpu.add( info );
        return cpu;
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

        List<EthernetController> nics = new ArrayList<EthernetController>();
        EthernetController controller1 = new EthernetController();
        ComponentIdentifier componentIdentifier = new ComponentIdentifier();
        componentIdentifier.setManufacturer( "Intel" );
        componentIdentifier.setProduct( "Ethernet Controller x540EC" );
        controller1.setComponentIdentifier( componentIdentifier );

        List<PortInfo> portInfos = new ArrayList<PortInfo>();
        PortInfo portInfo1 = new PortInfo();

        SpeedInfo info = new SpeedInfo();
        info.setSpeed( (long) 1000 );
        info.setUnit( SpeedUnit.Mbps );

        portInfo1.setLinkSpeedInMBps( info );
        portInfo1.setDeviceName( "vmnic0" );
        controller1.setComponent( ServerComponent.NIC );
        portInfo1.setLinkStatus( NicStatus.OK );
        portInfos.add( portInfo1 );

        PortInfo portInfo2 = new PortInfo();

        portInfo2.setLinkSpeedInMBps( info );
        portInfo2.setDeviceName( "vmnic1" );
        controller1.setComponent( ServerComponent.NIC );
        portInfo2.setLinkStatus( NicStatus.DISCONNECTED );
        portInfos.add( portInfo2 );

        controller1.setPortInfos( portInfos );
        nics.add( controller1 );
        return nics;

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<HddInfo> getHddInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        List<HddInfo> hddInfos = new ArrayList<HddInfo>();
        HddInfo hdd1 = new HddInfo();
        hdd1.setId( "HDD_0" );
        hdd1.setComponent( ServerComponent.STORAGE );
        hdd1.setDiskCapacityInMB( 8000000 );
        hdd1.setState( StatusEnum.OK );
        hdd1.setName( "HDD-80GB" );
        hdd1.setType( "HDD" );
        hddInfos.add( hdd1 );

        HddInfo hdd2 = new HddInfo();
        hdd2.setId( "HDD_1" );
        hdd2.setComponent( ServerComponent.STORAGE );
        hdd2.setDiskCapacityInMB( 16000000 );
        hdd2.setState( StatusEnum.OK );
        hdd2.setName( "HDD-160GB" );
        hdd2.setType( "HDD" );
        hddInfos.add( hdd2 );

        return hddInfos;
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
    public boolean setBmcPassword( ServiceHmsNode serviceHmsNode, String username, String newPassword )
        throws HmsException
    {
        return false;
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
        // TODO Auto-generated method stub
        return host_manageable;
    }

    @Override
    public List<PhysicalMemory> getPhysicalMemoryInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        List<PhysicalMemory> memories = new ArrayList<PhysicalMemory>();
        PhysicalMemory memory1 = new PhysicalMemory();
        ComponentIdentifier componentIdentifier1 = new ComponentIdentifier();
        memory1.setId( "MEMORY_0" );
        memory1.setLocation( "DIMM_0" );
        memory1.setCapacityInBytes( BigInteger.valueOf( 1600000 ) );
        memory1.setComponent( ServerComponent.MEMORY );
        componentIdentifier1.setProduct( "SDRAM" );
        componentIdentifier1.setManufacturer( "KINGSTON" );
        memory1.setMaxMemorySpeedInHertz( (long) 1600000 );
        memory1.setComponentIdentifier( componentIdentifier1 );
        memories.add( memory1 );

        PhysicalMemory memory2 = new PhysicalMemory();
        ComponentIdentifier componentIdentifier2 = new ComponentIdentifier();
        memory2.setId( "MEMORY_1" );
        memory2.setLocation( "DIMM_1" );
        memory2.setCapacityInBytes( BigInteger.valueOf( 1600000 ) );
        memory2.setComponent( ServerComponent.MEMORY );
        componentIdentifier2.setProduct( "SDRAM" );
        componentIdentifier2.setManufacturer( "KINGSTON" );
        memory2.setMaxMemorySpeedInHertz( (long) 1600000 );
        memory2.setComponentIdentifier( componentIdentifier2 );
        memories.add( memory2 );
        return memories;
    }

    @Override
    public List<StorageControllerInfo> getStorageControllerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {

        List<StorageControllerInfo> storageControllerInfoList = new ArrayList<StorageControllerInfo>();

        StorageControllerInfo storageControllerInfo1 = new StorageControllerInfo();
        ComponentIdentifier componentIdentifier1 = new ComponentIdentifier();
        componentIdentifier1.setManufacturer( "Intel Corporation" );
        componentIdentifier1.setProduct( "Patsburg 6 Port SATA AHCI Controller" );
        storageControllerInfo1.setComponentIdentifier( componentIdentifier1 );
        storageControllerInfo1.setComponent( ServerComponent.STORAGE_CONTROLLER );
        storageControllerInfo1.setDeviceName( "vmhba1" );
        storageControllerInfo1.setNumOfStorageDevicesConnected( 1 );
        storageControllerInfo1.setDriver( "ahci" );
        storageControllerInfo1.setFirmwareVersion( "23fh.56" );
        storageControllerInfoList.add( storageControllerInfo1 );

        StorageControllerInfo storageControllerInfo2 = new StorageControllerInfo();
        ComponentIdentifier componentIdentifier2 = new ComponentIdentifier();
        componentIdentifier2.setManufacturer( "LSI" );
        componentIdentifier2.setProduct( "LSI2008" );
        storageControllerInfo2.setComponentIdentifier( componentIdentifier2 );
        storageControllerInfo2.setComponent( ServerComponent.STORAGE_CONTROLLER );
        storageControllerInfo2.setDeviceName( "vmhba2" );
        storageControllerInfo2.setNumOfStorageDevicesConnected( 9 );
        storageControllerInfo2.setDriver( "mpt2sas" );
        storageControllerInfo2.setFirmwareVersion( "59fh.51" );
        storageControllerInfoList.add( storageControllerInfo2 );

        return storageControllerInfoList;
    }
}
