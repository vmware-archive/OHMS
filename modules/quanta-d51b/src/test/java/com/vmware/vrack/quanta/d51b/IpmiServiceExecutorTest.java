/* Copyright 2015 VMware, Inc. All rights reserved. */

package com.vmware.vrack.quanta.d51b;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

/**
 * Class to implement IpmiService for testing purposes. Relevant functions call Test Data class to
 * return the test data for junits.
 *
 * @author VMware Inc.
 */
public class IpmiServiceExecutorTest implements IpmiService {

    @Override
    public boolean coldResetServer(ServiceHmsNode serviceHmsNode) throws Exception {
    return IpmiServiceExecutorTestData.coldResetServer();
    }

    @Override
    public boolean coldResetServer(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
    return IpmiServiceExecutorTestData.coldResetServer();
    }

    @Override
    public boolean getServerPowerStatus(ServiceHmsNode serviceHmsNode) throws Exception {
	return IpmiServiceExecutorTestData.getPowerStatus();
    }

    @Override
    public boolean getServerPowerStatus(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
	return IpmiServiceExecutorTestData.getPowerStatus();
    }

    @Override
    public boolean isHostAvailable(ServiceServerNode serviceHmsNode) throws Exception {
	return IpmiServiceExecutorTestData.isHostAvailable();
    }

    @Override
    public boolean isHostAvailable(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
	return IpmiServiceExecutorTestData.isHostAvailable();
    }

    @Override
    public boolean powerCycleServer(ServiceHmsNode serviceHmsNode) throws Exception {
    return IpmiServiceExecutorTestData.powerCycleServer();
    }

    @Override
    public boolean powerCycleServer(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
    return IpmiServiceExecutorTestData.powerCycleServer();
    }

    @Override
    public boolean powerDownServer(ServiceHmsNode serviceHmsNode) throws Exception {
    return IpmiServiceExecutorTestData.powerDownServer();
    }

    @Override
    public boolean powerDownServer(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
    return IpmiServiceExecutorTestData.powerDownServer();
    }

    @Override
    public boolean powerResetServer(ServiceHmsNode serviceHmsNode) throws Exception {
    	return IpmiServiceExecutorTestData.powerResetServer();
    }

    @Override
    public boolean powerResetServer(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
    	return IpmiServiceExecutorTestData.powerResetServer();
    }

    @Override
    public boolean powerUpServer(ServiceHmsNode serviceHmsNode) throws Exception {
	return IpmiServiceExecutorTestData.powerUpServer();
    }

    @Override
    public boolean powerUpServer(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
	return IpmiServiceExecutorTestData.powerUpServer();
    }

    @Override
    public SelfTestResults selfTest(ServiceHmsNode serviceHmsNode) throws Exception {
	return IpmiServiceExecutorTestData.selfTest();
    }

    @Override
    public SelfTestResults selfTest(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
	return IpmiServiceExecutorTestData.selfTest();
    }

    @Override
    public AcpiPowerState getAcpiPowerState(ServiceHmsNode serviceHmsNode)
		    throws Exception {
	return IpmiServiceExecutorTestData.getAcpiPowerState();
    }

    @Override
    public AcpiPowerState getAcpiPowerState(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
	return IpmiServiceExecutorTestData.getAcpiPowerState();
    }

    @Override
    public List<BmcUser> getBmcUsers(ServiceHmsNode serviceHmsNode) throws Exception {
	return IpmiServiceExecutorTestData.getBmcUsers();
    }

    @Override
    public List<BmcUser> getBmcUsers(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
	return IpmiServiceExecutorTestData.getBmcUsers();
    }

    @Override
    public SystemBootOptions getBootOptions(ServiceHmsNode serviceHmsNode)
		    throws Exception {
	return IpmiServiceExecutorTestData.getBootOptions();
    }

    @Override
    public SystemBootOptions getBootOptions(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
	return IpmiServiceExecutorTestData.getBootOptions();
    }

    @Override
    public List<Object> getFruInfo(ServiceHmsNode serviceHmsNode) throws Exception {
	return IpmiServiceExecutorTestData.getFruInfo();
    }

    @Override
    public List<Object> getFruInfo(ServiceHmsNode serviceHmsNode,
		    Integer fruReadPacketSize) throws Exception {
	return null;
    }

    @Override
    public List<Object> getFruInfo(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector, Integer fruReadPacketSize) throws Exception {
	return null;
    }

    @Override
    public String getMacAddress(ServiceHmsNode serviceHmsNode) throws Exception {
	return IpmiServiceExecutorTestData.getMacAddress();
    }

    @Override
    public String getMacAddress(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
	return IpmiServiceExecutorTestData.getMacAddress();
    }

    @Override
    public boolean setBootOptions(ServiceHmsNode serviceHmsNode,
		    SystemBootOptions bootOptions) throws Exception {
	return IpmiServiceExecutorTestData.setBootOptions();
    }

    @Override
    public boolean setBootOptions(ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector, SystemBootOptions bootOptions)
		    throws Exception {
	return IpmiServiceExecutorTestData.setBootOptions();
    }

    @Override
    public List<Map<String, String>> getSensorData(ServiceHmsNode serviceHmsNode)
		    throws Exception {
	return IpmiServiceExecutorTestData.getSensorData();
    }

    @Override
    public List<Map<String, String>> getSensorData( ServiceHmsNode serviceHmsNode,
		    List<Integer> listSensorNumber) throws Exception {
	return IpmiServiceExecutorTestData.getSensorData(listSensorNumber);
    }

    @Override
    public List<Map<String, String>> getSensorDataForSensorTypeAndEntity(
                    ServiceHmsNode node, List<SensorType> typeList, List<EntityId> entityList)
                    throws Exception {
        return IpmiServiceExecutorTestData.getSensorDataForSensorTypeAndEntity(typeList);
    }

    @Override
    public List<Map<String, String>> getSensorData( ServiceHmsNode serviceHmsNode,
		    Integer headerSize, Integer initialChunkSize, Integer chunkSize,
		    List<Integer> listSensorNumber) throws Exception {
	return IpmiServiceExecutorTestData.getSensorData();
    }

    @Override
    public List<Map<String, String>> getSensorData( ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector, Integer headerSize, Integer initialChunkSize,
		    Integer chunkSize, List<Integer> listSensorNumber) throws Exception {
	return IpmiServiceExecutorTestData.getSensorData();
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
	return IpmiServiceExecutorTestData.getServerInfo();
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode,
		    Integer fruReadPacketSize) throws Exception {
	return IpmiServiceExecutorTestData.getServerInfo();
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode,
		    Integer fruReadPacketSize, ArrayList<Integer> fruList) throws Exception {
	return IpmiServiceExecutorTestData.getServerInfo();
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector, Integer fruReadPacketSize) throws Exception {
	return IpmiServiceExecutorTestData.getServerInfo();
    }

    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector, Integer fruReadPacketSize,
		    ArrayList<Integer> fruList) throws Exception {
	return IpmiServiceExecutorTestData.getServerInfo();
    }

    @Override
    public boolean performChassisIdentification( ServiceHmsNode serviceHmsNode,
		    ChassisIdentifyOptions chassisIdentifyOptions) throws Exception {
	return IpmiServiceExecutorTestData.performChassisIdentification();
    }

    @Override
    public boolean performChassisIdentification( ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector, ChassisIdentifyOptions chassisIdentifyOptions)
		    throws Exception {
	return IpmiServiceExecutorTestData.performChassisIdentification();
    }

    @Override
    public SelInfo getSelInfo( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
	return IpmiServiceExecutorTestData.getSelInfo();
    }

    @Override
    public SelInfo getSelInfo( ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector) throws Exception {
	return IpmiServiceExecutorTestData.getSelInfo();
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
	return IpmiServiceExecutorTestData.getSelDetails();
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode,
		    List<SelRecord> selFilters) throws Exception {
	return IpmiServiceExecutorTestData.getSelDetails();
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode,
		    SelFetchDirection direction, List<SelRecord> selFilters) throws Exception {
	return IpmiServiceExecutorTestData.getSelDetails();
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode node, Integer recordCount,
		    SelFetchDirection direction) throws Exception {
	return IpmiServiceExecutorTestData.getSelDetails();
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode, Integer recordCount,
		    SelFetchDirection direction, List<SelRecord> selFilters) throws Exception {
	return null;
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode,
    		IpmiTaskConnector ipmiConnector, Integer recordCount,
		    SelFetchDirection direction, List<SelRecord> selFilters) throws Exception {
	return null;
    }
}