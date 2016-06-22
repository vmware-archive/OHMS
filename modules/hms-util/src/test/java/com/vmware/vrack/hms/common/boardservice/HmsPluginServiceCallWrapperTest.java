/* ********************************************************************************
 * HmsPluginServiceCallWrapperTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.vmware.vrack.hms.boardservice.HmsPluginServiceCallWrapper;
import com.vmware.vrack.hms.boardservice.NodeRateLimitModel;
import com.vmware.vrack.hms.common.boardvendorservice.api.BoardServiceTest;
import com.vmware.vrack.hms.common.boardvendorservice.api.IHmsComponentService;
import com.vmware.vrack.hms.common.boardvendorservice.api.ib.InbandBoardServiceTest;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsResourceBusyException;
import com.vmware.vrack.hms.common.exception.HmsResponseTimeoutException;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.rest.model.CpuInfo;
import com.vmware.vrack.hms.common.rest.model.MemoryInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchOsInfo;
import com.vmware.vrack.hms.common.switchservice.api.SwitchServiceTest;
import com.vmware.vrack.hms.common.util.CommonProperties;

/**
 * Test class to test HmsPluginServiceCallWrapper class
 * 
 * @author Vmware Inc.
 */
public class HmsPluginServiceCallWrapperTest
{
    @Before
    public void initializeTests()
    {
        CommonProperties commonProperties = new CommonProperties();
        commonProperties.setPluginThreadPoolCount( 2 );
        commonProperties.setPluginTaskTimeOut( 6000L );
    }

    @After
    public void teardown()
    {
        Field cachedNodeRateLimitObjectField =
            ReflectionUtils.findField( HmsPluginServiceCallWrapper.class, "cachedNodeRateLimitObject" );
        ReflectionUtils.makeAccessible( cachedNodeRateLimitObjectField );
        ReflectionUtils.setField( cachedNodeRateLimitObjectField, new HmsPluginServiceCallWrapper(),
                                  new ConcurrentHashMap<String, NodeRateLimitModel>() );
    }

    @Test
    public void addNodeRateLimitModelForNodeTest()
        throws NoSuchElementException, IllegalStateException, Exception
    {
        assertNull( HmsPluginServiceCallWrapper.getNodeRateLimitModelObject( "N1" ) );
        HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode( "N1" );
        assertNotNull( HmsPluginServiceCallWrapper.getNodeRateLimitModelObject( "N1" ) );
        NodeRateLimitModel nodeRateLimitModel = HmsPluginServiceCallWrapper.getNodeRateLimitModelObject( "N1" );
        assertNotNull( nodeRateLimitModel.getScheduledExecutorService() );
        assertNotNull( nodeRateLimitModel.getThreadLimitExecuterServiceObject() );
        assertNotNull( nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool() );
        assertNotNull( nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool().borrowObject( "N1" ) );
    }

    @Test( expected = NoSuchElementException.class )
    public void addNodeRateLimitModelForNodeTest2()
        throws NoSuchElementException, IllegalStateException, Exception
    {
        assertNull( HmsPluginServiceCallWrapper.getNodeRateLimitModelObject( "N1" ) );
        HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode( "N1" );
        assertNotNull( HmsPluginServiceCallWrapper.getNodeRateLimitModelObject( "N1" ) );
        NodeRateLimitModel nodeRateLimitModel = HmsPluginServiceCallWrapper.getNodeRateLimitModelObject( "N1" );
        assertNotNull( nodeRateLimitModel.getScheduledExecutorService() );
        assertNotNull( nodeRateLimitModel.getThreadLimitExecuterServiceObject() );
        assertNotNull( nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool() );
        assertNotNull( nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool().borrowObject( "N1" ) );
        assertNotNull( nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool().borrowObject( "N1" ) );
        assertNotNull( nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool().borrowObject( "N1" ) );
    }

    @Test
    public void invokeHmsPluginServiceTest()
        throws HmsResourceBusyException, HmsResponseTimeoutException, HmsException
    {
        assertNull( HmsPluginServiceCallWrapper.getNodeRateLimitModelObject( "N1" ) );
        HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode( "N1" );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setIbIpAddress( "10.28.197.28" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );
        IHmsComponentService boardServiceTest = new BoardServiceTest();
        List<BmcUser> bmcUsers =
            HmsPluginServiceCallWrapper.invokeHmsPluginService( boardServiceTest,
                                                                (ServiceServerNode) node.getServiceObject(),
                                                                "getManagementUsers",
                                                                new Object[] { node.getServiceObject() } );
        assertNotNull( bmcUsers );
        assertEquals( "total Bmc Users", 2, bmcUsers.size() );
    }

    @Test( expected = HmsException.class )
    public void invokeHmsPluginServiceTest_CallWrapperUnitialized()
        throws HmsResourceBusyException, HmsResponseTimeoutException, HmsException
    {
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setIbIpAddress( "10.28.197.28" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );
        IHmsComponentService boardServiceTest = new BoardServiceTest();
        List<BmcUser> bmcUsers =
            HmsPluginServiceCallWrapper.invokeHmsPluginService( boardServiceTest,
                                                                (ServiceServerNode) node.getServiceObject(),
                                                                "getManagementUsers",
                                                                new Object[] { node.getServiceObject() } );
        assertNotNull( bmcUsers );
    }

    @Test( expected = HmsException.class )
    public void invokeHmsPluginServiceTest_NullParam()
        throws HmsResourceBusyException, HmsResponseTimeoutException, HmsException
    {
        assertNull( HmsPluginServiceCallWrapper.getNodeRateLimitModelObject( "N1" ) );
        HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode( "N1" );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setIbIpAddress( "10.28.197.28" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );
        IHmsComponentService boardServiceTest = new BoardServiceTest();
        List<BmcUser> bmcUsers = HmsPluginServiceCallWrapper.invokeHmsPluginService( boardServiceTest,
                                                                                     (ServiceServerNode) node.getServiceObject(),
                                                                                     "getManagementUsers", null );
        assertNotNull( bmcUsers );
    }

    @Test
    public void invokeHmsPluginServiceTest_InbandRequests()
        throws HmsResourceBusyException, HmsResponseTimeoutException, HmsException
    {
        assertNull( HmsPluginServiceCallWrapper.getNodeRateLimitModelObject( "N1" ) );
        HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode( "N1" );
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setIbIpAddress( "10.28.197.28" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );
        IHmsComponentService inbandService = new InbandBoardServiceTest();
        List<CpuInfo> cpuInfos =
            HmsPluginServiceCallWrapper.invokeHmsPluginService( inbandService,
                                                                (ServiceServerNode) node.getServiceObject(),
                                                                "getCpuInfo",
                                                                new Object[] { node.getServiceObject() } );
        assertNotNull( cpuInfos );
        assertEquals( "total Cpus ", 2, cpuInfos.size() );
    }

    /*
     * @Test(expected=HmsResourceBusyException.class) public void
     * invokeHmsPluginServiceTest_InbandRequests_MultipleRequests() throws HmsResourceBusyException,
     * HmsResponseTimeoutException, HmsException {
     * assertNull(HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("N1"));
     * HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode("N1"); final ServerNode node = new ServerNode();
     * node.setNodeID("N1"); node.setIbIpAddress("10.28.197.28"); node.setOsUserName("root");
     * node.setOsPassword("root123"); ExecutorService executorService = Executors.newFixedThreadPool(5); Map<Integer,
     * Future<Object>> futures = new HashMap<Integer, Future<Object>>(); try { for (int i = 0; i < 5; i++) {
     * futures.put(i, executorService.submit(new Callable<Object>() { public Object call() throws
     * HmsResourceBusyException, HmsResponseTimeoutException, HmsException { IHmsComponentService inbandService = new
     * InbandBoardServiceTest(); return HmsPluginServiceCallWrapper.invokeHmsPluginService(inbandService,
     * (ServiceServerNode) node.getServiceObject(), HmsApi.MEMORY_INFO, new Object[] { node.getServiceObject() }); }
     * })); } } finally { executorService.shutdown(); } try { Thread.sleep(1000L); } catch (InterruptedException e) { }
     * }
     */
    @Test( expected = HmsResponseTimeoutException.class )
    public void invokeHmsPluginServiceTest_InbandRequests_Timeout()
        throws HmsResourceBusyException, HmsResponseTimeoutException, HmsException
    {
        assertNull( HmsPluginServiceCallWrapper.getNodeRateLimitModelObject( "N1" ) );
        HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode( "N1" );
        final ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setIbIpAddress( "10.28.197.28" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );
        IHmsComponentService inbandService = new InbandBoardServiceTest();
        List<MemoryInfo> memories =
            HmsPluginServiceCallWrapper.invokeHmsPluginService( inbandService,
                                                                (ServiceServerNode) node.getServiceObject(),
                                                                "getSystemMemoryInfo",
                                                                new Object[] { node.getServiceObject() } );
        assertNull( memories );
    }

    @Test
    public void invokeHmsPluginSwitchServiceTest()
        throws HmsResourceBusyException, HmsResponseTimeoutException, HmsException
    {
        assertNull( HmsPluginServiceCallWrapper.getNodeRateLimitModelObject( "S1" ) );
        HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode( "S1" );
        SwitchNode switchNode = new SwitchNode( "S1", "SSH", "10.28.197.248", 22, "lanier", "l@ni3r2o14" );
        ISwitchService switchService = new SwitchServiceTest();
        SwitchOsInfo osInfo =
            HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, 10000L,
                                                                      "getSwitchOsInfo", new Object[] { switchNode } );
        assertNotNull( osInfo );
        assertEquals( "Os Vendor", "Arista OS", osInfo.getOsName() );
    }
}
