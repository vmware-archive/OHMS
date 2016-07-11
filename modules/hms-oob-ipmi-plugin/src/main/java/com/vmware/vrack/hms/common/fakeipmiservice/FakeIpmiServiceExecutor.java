/* ********************************************************************************
 * FakeIpmiServiceExecutor.java
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
package com.vmware.vrack.hms.common.fakeipmiservice;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.EntityId;
import com.vmware.vrack.hms.common.resource.fru.SensorType;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.resource.sel.SelRecord;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.ipmiservice.IpmiService;
import com.vmware.vrack.hms.task.ipmi.IpmiTaskConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to implement IpmiService for testing purposes. Relevant functions call Test Data class to return the test data
 * for junits.
 *
 * @author VMware Inc.
 */
public class FakeIpmiServiceExecutor
    implements IpmiService
{
    @Override
    public boolean coldResetServer( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return false;
    }

    @Override
    public boolean coldResetServer( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return false;
    }

    @Override
    public boolean getServerPowerStatus( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getPowerStatus();
    }

    @Override
    public boolean getServerPowerStatus( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getPowerStatus();
    }

    @Override
    public boolean isHostAvailable( ServiceServerNode serviceHmsNode )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.isHostAvailable();
    }

    @Override
    public boolean isHostAvailable( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.isHostAvailable();
    }

    @Override
    public boolean powerCycleServer( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return false;
    }

    @Override
    public boolean powerCycleServer( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return false;
    }

    @Override
    public boolean powerDownServer( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return false;
    }

    @Override
    public boolean powerDownServer( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return false;
    }

    @Override
    public boolean powerResetServer( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return false;
    }

    @Override
    public boolean powerResetServer( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return false;
    }

    @Override
    public boolean powerUpServer( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.powerUpServer();
    }

    @Override
    public boolean powerUpServer( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.powerUpServer();
    }

    @Override
    public SelfTestResults selfTest( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.selfTest();
    }

    @Override
    public SelfTestResults selfTest( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.selfTest();
    }

    @Override
    public AcpiPowerState getAcpiPowerState( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getAcpiPowerState();
    }

    @Override
    public AcpiPowerState getAcpiPowerState( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getAcpiPowerState();
    }

    @Override
    public List<BmcUser> getBmcUsers( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getBmcUsers();
    }

    @Override
    public List<BmcUser> getBmcUsers( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getBmcUsers();
    }

    @Override
    public SystemBootOptions getBootOptions( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getBootOptions();
    }

    @Override
    public SystemBootOptions getBootOptions( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getBootOptions();
    }

    @Override
    public List<Object> getFruInfo( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return null;
    }

    @Override
    public List<Object> getFruInfo( ServiceHmsNode serviceHmsNode, Integer fruReadPacketSize )
        throws Exception
    {
        return null;
    }

    @Override
    public List<Object> getFruInfo( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector,
                                    Integer fruReadPacketSize )
                                        throws Exception
    {
        return null;
    }

    @Override
    public String getMacAddress( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getMacAddress();
    }

    @Override
    public String getMacAddress( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getMacAddress();
    }

    @Override
    public boolean setBootOptions( ServiceHmsNode serviceHmsNode, SystemBootOptions bootOptions )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.setBootOptions();
    }

    @Override
    public boolean setBootOptions( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector,
                                   SystemBootOptions bootOptions )
                                       throws Exception
    {
        return FakeIpmiServiceExecutorData.setBootOptions();
    }

    @Override
    public List<Map<String, String>> getSensorData( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getSensorData();
    }

    @Override
    public List<Map<String, String>> getSensorData( ServiceHmsNode serviceHmsNode, List<Integer> listSensorNumber )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getSensorData();
    }

    @Override
    public List<Map<String, String>> getSensorDataForSensorTypeAndEntity( ServiceHmsNode node,
                                                                          List<SensorType> typeList,
                                                                          List<EntityId> entityList )
                                                                              throws Exception
    {
        return FakeIpmiServiceExecutorData.getSensorDataForSensorTypeAndEntity( typeList );
    }

    @Override
    public List<Map<String, String>> getSensorData( ServiceHmsNode serviceHmsNode, Integer headerSize,
                                                    Integer initialChunkSize, Integer chunkSize,
                                                    List<Integer> listSensorNumber )
                                                        throws Exception
    {
        return FakeIpmiServiceExecutorData.getSensorData();
    }

    @Override
    public List<Map<String, String>> getSensorData( ServiceHmsNode serviceHmsNode, IpmiTaskConnector connector,
                                                    Integer headerSize, Integer initialChunkSize, Integer chunkSize,
                                                    List<Integer> listSensorNumber )
                                                        throws Exception
    {
        return FakeIpmiServiceExecutorData.getSensorData();
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getServerInfo();
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode, Integer fruReadPacketSize )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getServerInfo();
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode, Integer fruReadPacketSize,
                                         ArrayList<Integer> fruList )
                                             throws Exception
    {
        return FakeIpmiServiceExecutorData.getServerInfo();
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode, IpmiTaskConnector connector,
                                         Integer fruReadPacketSize )
                                             throws Exception
    {
        return FakeIpmiServiceExecutorData.getServerInfo();
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode, IpmiTaskConnector connector,
                                         Integer fruReadPacketSize, ArrayList<Integer> fruList )
                                             throws Exception
    {
        return FakeIpmiServiceExecutorData.getServerInfo();
    }

    @Override
    public boolean performChassisIdentification( ServiceHmsNode serviceHmsNode,
                                                 ChassisIdentifyOptions chassisIdentifyOptions )
                                                     throws Exception
    {
        return FakeIpmiServiceExecutorData.performChassisIdentification();
    }

    @Override
    public boolean performChassisIdentification( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector,
                                                 ChassisIdentifyOptions chassisIdentifyOptions )
                                                     throws Exception
    {
        return FakeIpmiServiceExecutorData.performChassisIdentification();
    }

    @Override
    public SelInfo getSelInfo( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getSelInfo();
    }

    @Override
    public SelInfo getSelInfo( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getSelInfo();
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getSelDetails();
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode, List<SelRecord> selFilters )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getSelDetails();
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode, SelFetchDirection direction,
                                  List<SelRecord> selFilters )
                                      throws Exception
    {
        return FakeIpmiServiceExecutorData.getSelDetails();
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode node, Integer recordCount, SelFetchDirection direction )
        throws Exception
    {
        return FakeIpmiServiceExecutorData.getSelDetails();
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode, Integer recordCount, SelFetchDirection direction,
                                  List<SelRecord> selFilters )
                                      throws Exception
    {
        return null;
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode, IpmiTaskConnector ipmiConnector, Integer recordCount,
                                  SelFetchDirection direction, List<SelRecord> selFilters )
                                      throws Exception
    {
        return null;
    }
}
