/* ********************************************************************************
 * FakeIpmiServiceExecutorData.java
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
package com.vmware.vrack.hms.common.fakeipmiservice;

import com.vmware.vrack.coding.commands.application.DevicePowerState;
import com.vmware.vrack.coding.commands.application.SystemPowerState;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.BiosBootType;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceSelector;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceType;
import com.vmware.vrack.hms.common.resource.chassis.BootOptionsValidity;
import com.vmware.vrack.hms.common.resource.fru.SensorType;
import com.vmware.vrack.hms.common.resource.sel.*;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;

import java.util.*;

/**
 * Class to return test data to be used with Junits.
 *
 * @author VMware Inc.
 */
public class FakeIpmiServiceExecutorData
{
    private static final String MAC_ADDRESS = "AA:BB:CC:DD:EE:FF";

    private static final boolean powerStatus = true;

    private static final boolean setBootOptionsStatus = true;

    private static final boolean chassisIdentificationStatus = true;

    private static final boolean hostAvailable = true;

    private static final boolean powerUpServerStatus = true;

    private static final int FETCHED_SEL_COUNT = 10;

    private static List<BmcUser> bmcUsers;

    private static AcpiPowerState acpiPowerState;

    private static SystemBootOptions bootOptions;

    private static ServerNodeInfo serverInfo;

    private static SelInfo selInfo;

    private static SelInfo selDetails;

    private static List<Map<String, String>> sensorData = null;

    private static List<Map<String, String>> sensorDataForSensorTypeAndEntity;

    public static boolean getPowerStatus()
    {
        return powerStatus;
    }

    public static String getMacAddress()
    {
        return MAC_ADDRESS;
    }

    public static List<BmcUser> getBmcUsers()
    {
        bmcUsers = new ArrayList<BmcUser>();
        BmcUser tempBmcUser1 = new BmcUser();
        tempBmcUser1.setUserId( 3 );
        tempBmcUser1.setUserName( "TestUser3" );
        bmcUsers.add( tempBmcUser1 );
        tempBmcUser1 = new BmcUser();
        tempBmcUser1.setUserId( 4 );
        tempBmcUser1.setUserName( "TestUser4" );
        bmcUsers.add( tempBmcUser1 );
        return bmcUsers;
    }

    public static AcpiPowerState getAcpiPowerState()
    {
        acpiPowerState = new AcpiPowerState();
        acpiPowerState.setSystemAcpiPowerState( SystemPowerState.S0_G0.toString() );
        acpiPowerState.setDeviceAcpiPowerState( DevicePowerState.D0.toString() );
        return acpiPowerState;
    }

    public static SystemBootOptions getBootOptions()
    {
        bootOptions = new SystemBootOptions();
        bootOptions.setBiosBootType( BiosBootType.Legacy );
        bootOptions.setBootDeviceInstanceNumber( 1 );
        bootOptions.setBootDeviceSelector( BootDeviceSelector.PXE );
        bootOptions.setBootDeviceType( BootDeviceType.External );
        bootOptions.setBootFlagsValid( true );
        bootOptions.setBootOptionsValidity( BootOptionsValidity.NextBootOnly );
        return bootOptions;
    }

    public static boolean setBootOptions()
    {
        return setBootOptionsStatus;
    }

    public static ServerNodeInfo getServerInfo()
    {
        ComponentIdentifier componentIdentifier = new ComponentIdentifier();
        serverInfo = new ServerNodeInfo();
        componentIdentifier.setManufacturer( "testBoardVendor" );
        componentIdentifier.setProduct( "testBoardProductName" );
        componentIdentifier.setSerialNumber( "54545" );
        componentIdentifier.setPartNumber( "45FAB6" );
        serverInfo.setComponentIdentifier( componentIdentifier );
        return serverInfo;
    }

    public static boolean performChassisIdentification()
    {
        return chassisIdentificationStatus;
    }

    public static SelInfo getSelInfo()
    {
        selInfo = new SelInfo();
        selInfo.setTotalSelCount( 200 );
        selInfo.setFetchedSelCount( FETCHED_SEL_COUNT );
        selInfo.setSelVersion( 1 );
        Calendar cal = Calendar.getInstance();
        selInfo.setLastAddtionTimeStamp( cal.getTime() );
        cal.add( Calendar.DATE, -1 );
        selInfo.setLastEraseTimeStamp( cal.getTime() );
        return selInfo;
    }

    public static SelInfo getSelDetails()
    {
        selDetails = getSelInfo();
        Random rand = new Random( 19580427 );
        List<SelRecord> selRecords = new ArrayList<SelRecord>();
        SelRecord tempSelRecord = new SelRecord();
        for ( int i = 0; i < FETCHED_SEL_COUNT; i++ )
        {
            tempSelRecord = new SelRecord();
            tempSelRecord.setRecordId( i );
            tempSelRecord.setRecordType( SelRecordType.System );
            tempSelRecord.setTimestamp( new Date() );
            tempSelRecord.setSensorType( SensorType.Processor );
            tempSelRecord.setSensorNumber( i + 30 );
            tempSelRecord.setEventDirection( EventDirection.Assertion );
            tempSelRecord.setEvent( ReadingType.ProcessorPresenceDetected );
            tempSelRecord.setReading( (byte) rand.nextInt( 127 ) );
            selRecords.add( tempSelRecord );
        }
        selDetails.setSelRecords( selRecords );
        return selDetails;
    }

    public static boolean isHostAvailable()
    {
        return hostAvailable;
    }

    public static boolean powerUpServer()
    {
        return powerUpServerStatus;
    }

    public static SelfTestResults selfTest()
    {
        SelfTestResults selfTestResults = new SelfTestResults();
        selfTestResults.setSelfTestResultCode( (byte) 85 );
        selfTestResults.setSelfTestResult( null );
        selfTestResults.setSelfTestResultFailureCode( (byte) 0 );
        selfTestResults.setErrors( null );
        selfTestResults.setErrorMessage( null );
        selfTestResults.setStatusCode( null );
        selfTestResults.setStatusMessage( null );
        return selfTestResults;
    }

    public static List<Map<String, String>> getSensorData()
    {
        Map sensorData = new HashMap<String, String>();
        List<Map<String, String>> sensorDataList = new ArrayList<Map<String, String>>();
        /* CPU Sensor information */
        sensorData.put( "unit", "DegreesC" );
        sensorData.put( "sensorNumber", "43" );
        sensorData.put( "reading", "40.0" );
        sensorData.put( "State", "OK" );
        sensorData.put( "sensorType", "Temperature" );
        sensorData.put( "name", "CPU0 Temp" );
        sensorData.put( "entityId", "Processor" );
        sensorData.put( "StateByteCode", "0" );
        sensorDataList.add( sensorData );
        sensorData = new HashMap<String, String>();
        sensorData.put( "unit", "DegreesC" );
        sensorData.put( "sensorNumber", "44" );
        sensorData.put( "reading", "37.0" );
        sensorData.put( "State", "OK" );
        sensorData.put( "sensorType", "Temperature" );
        sensorData.put( "name", "CPU1 Temp" );
        sensorData.put( "entityId", "Processor" );
        sensorData.put( "StateByteCode", "0" );
        sensorDataList.add( sensorData );
        return sensorDataList;
    }

    public static List<Map<String, String>> getSensorDataForSensorTypeAndEntity( List<SensorType> typeList )
    {
        Map sensorData = new HashMap<String, String>();
        sensorDataForSensorTypeAndEntity = new ArrayList<Map<String, String>>();
        SensorType sensorType;
        if ( ( (SensorType) typeList.get( 0 ) ).equals( SensorType.Processor ) )
        {
            /* CPU Sensor information */
            sensorData.put( "unit", "DegreesC" );
            sensorData.put( "sensorNumber", "40" );
            sensorData.put( "reading", "20.0" );
            sensorData.put( "entityInstanceId", "0" );
            sensorData.put( "State", "Ok" );
            sensorData.put( "sensorType", "Temperature" );
            sensorData.put( "name", "Ambient_Temp" );
            sensorData.put( "entityId", "Processor" );
            sensorData.put( "StateByteCode", "0" );
            sensorDataForSensorTypeAndEntity.add( sensorData );
            sensorData = new HashMap<String, String>();
            sensorData.put( "unit", "DegreesC" );
            sensorData.put( "sensorNumber", "41" );
            sensorData.put( "reading", "41.0" );
            sensorData.put( "entityInstanceId", "0" );
            sensorData.put( "State", "Ok" );
            sensorData.put( "sensorType", "Temperature" );
            sensorData.put( "name", "Inlet_Temp" );
            sensorData.put( "entityId", "Processor" );
            sensorData.put( "StateByteCode", "0" );
            sensorDataForSensorTypeAndEntity.add( sensorData );
        }
        else if ( ( (SensorType) typeList.get( 0 ) ).equals( SensorType.Memory ) )
        {
            /* Memory Sensor Information */
            sensorData.put( "unit", "DegreesC" );
            sensorData.put( "sensorNumber", "45" );
            sensorData.put( "reading", "44.0" );
            sensorData.put( "entityInstanceId", "27" );
            sensorData.put( "State", "Ok" );
            sensorData.put( "sensorType", "Temperature" );
            sensorData.put( "name", "Temp_DIMM_A0" );
            sensorData.put( "entityId", "MemoryDevice" );
            sensorData.put( "StateByteCode", "0" );
            sensorDataForSensorTypeAndEntity.add( sensorData );
            sensorData = new HashMap<String, String>();
            sensorData.put( "unit", "DegreesC" );
            sensorData.put( "sensorNumber", "46" );
            sensorData.put( "reading", "42.0" );
            sensorData.put( "entityInstanceId", "27" );
            sensorData.put( "State", "Ok" );
            sensorData.put( "sensorType", "Temperature" );
            sensorData.put( "name", "Temp_DIMM_A1" );
            sensorData.put( "entityId", "MemoryDevice" );
            sensorData.put( "StateByteCode", "0" );
            sensorDataForSensorTypeAndEntity.add( sensorData );
        }
        else if ( ( (SensorType) typeList.get( 0 ) ).equals( SensorType.Fan ) )
        {
            /* Fan Sensor Information */
            sensorData.put( "unit", "Rpm" );
            sensorData.put( "sensorNumber", "80" );
            sensorData.put( "reading", "6900.0" );
            sensorData.put( "entityInstanceId", "0" );
            sensorData.put( "State", "Ok" );
            sensorData.put( "sensorType", "Fan" );
            sensorData.put( "name", "SYS_FAN1-1" );
            sensorData.put( "entityId", "SystemBoard" );
            sensorData.put( "StateByteCode", "0" );
            sensorDataForSensorTypeAndEntity.add( sensorData );
            sensorData = new HashMap<String, String>();
            sensorData.put( "unit", "Rpm" );
            sensorData.put( "sensorNumber", "81" );
            sensorData.put( "reading", "4900.0" );
            sensorData.put( "entityInstanceId", "0" );
            sensorData.put( "State", "Ok" );
            sensorData.put( "sensorType", "Fan" );
            sensorData.put( "name", "SYS_FAN1-2" );
            sensorData.put( "entityId", "SystemBoard" );
            sensorData.put( "StateByteCode", "0" );
            sensorDataForSensorTypeAndEntity.add( sensorData );
        }
        else if ( ( (SensorType) typeList.get( 0 ) ).equals( SensorType.DriveBay ) )
        {
            /* HDD Sensor Information */
            sensorData.put( "unit", "Unspecified" );
            sensorData.put( "sensorNumber", "226" );
            sensorData.put( "reading", "" );
            sensorData.put( "entityInstanceId", "1" );
            sensorData.put( "State", "DrivePresence" );
            sensorData.put( "sensorType", "DriveBay" );
            sensorData.put( "name", "HDD 0 status" );
            sensorData.put( "entityId", "RemotemanagementCommunicationDevice" );
            sensorData.put( "StateByteCode", "880384" );
            sensorDataForSensorTypeAndEntity.add( sensorData );
            sensorData = new HashMap<String, String>();
            sensorData.put( "unit", "Unspecified" );
            sensorData.put( "sensorNumber", "227" );
            sensorData.put( "reading", "" );
            sensorData.put( "entityInstanceId", "2" );
            sensorData.put( "State", "DrivePresence" );
            sensorData.put( "sensorType", "DriveBay" );
            sensorData.put( "name", "HDD 1 status" );
            sensorData.put( "entityId", "RemotemanagementCommunicationDevice" );
            sensorData.put( "StateByteCode", "880384" );
            sensorDataForSensorTypeAndEntity.add( sensorData );
        }
        return sensorDataForSensorTypeAndEntity;
    }
}
