/* ********************************************************************************
 * HmsPluginServiceCallWrapperTest.java
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

// package com.vmware.vrack.hms.common.boardservice;

/**
 * Test class to test HmsPluginServiceCallWrapper class
 * 
 * @author Vmware Inc.
 */

/*
 * public class HmsPluginServiceCallWrapperTest {
 * @Before public void initializeTests() { CommonProperties commonProperties = new CommonProperties();
 * commonProperties.setPluginThreadPoolCount(2); commonProperties.setPluginTaskTimeOut(6000L); }
 * @After public void teardown() { Field cachedNodeRateLimitObjectField =
 * ReflectionUtils.findField(HmsPluginServiceCallWrapper.class, "cachedNodeRateLimitObject");
 * ReflectionUtils.makeAccessible(cachedNodeRateLimitObjectField);
 * ReflectionUtils.setField(cachedNodeRateLimitObjectField, new HmsPluginServiceCallWrapper() , new
 * ConcurrentHashMap<String, NodeRateLimitModel>()); }
 * @Test public void addNodeRateLimitModelForNodeTest() throws NoSuchElementException, IllegalStateException, Exception
 * { assertNull(HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("N1"));
 * HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode("N1");
 * assertNotNull(HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("N1")); NodeRateLimitModel nodeRateLimitModel =
 * HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("N1");
 * assertNotNull(nodeRateLimitModel.getScheduledExecutorService());
 * assertNotNull(nodeRateLimitModel.getThreadLimitExecuterServiceObject());
 * assertNotNull(nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool());
 * assertNotNull(nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool().borrowObject("N1")); }
 * @Test(expected=NoSuchElementException.class) public void addNodeRateLimitModelForNodeTest2() throws
 * NoSuchElementException, IllegalStateException, Exception {
 * assertNull(HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("N1"));
 * HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode("N1");
 * assertNotNull(HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("N1")); NodeRateLimitModel nodeRateLimitModel =
 * HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("N1");
 * assertNotNull(nodeRateLimitModel.getScheduledExecutorService());
 * assertNotNull(nodeRateLimitModel.getThreadLimitExecuterServiceObject());
 * assertNotNull(nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool());
 * assertNotNull(nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool().borrowObject("N1"));
 * assertNotNull(nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool().borrowObject("N1"));
 * assertNotNull(nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool().borrowObject("N1")); }
 * @Test public void invokeHmsPluginServiceTest() throws HmsResourceBusyException, HmsResponseTimeoutException,
 * HmsException { assertNull(HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("N1"));
 * HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode("N1"); ServerNode node = new ServerNode();
 * node.setNodeID("N1"); node.setIbIpAddress("10.28.197.28"); node.setOsUserName("root"); node.setOsPassword("root123");
 * IHmsComponentService boardServiceTest = new BoardServiceTest(); List<BmcUser> bmcUsers =
 * HmsPluginServiceCallWrapper.invokeHmsPluginService(boardServiceTest, (ServiceServerNode)node.getServiceObject(),
 * "getManagementUsers", new Object[]{node.getServiceObject()}); assertNotNull(bmcUsers); assertEquals("total Bmc Users"
 * , 2, bmcUsers.size()); }
 * @Test(expected=HmsException.class) public void invokeHmsPluginServiceTest_CallWrapperUnitialized() throws
 * HmsResourceBusyException, HmsResponseTimeoutException, HmsException { ServerNode node = new ServerNode();
 * node.setNodeID("N1"); node.setIbIpAddress("10.28.197.28"); node.setOsUserName("root"); node.setOsPassword("root123");
 * IHmsComponentService boardServiceTest = new BoardServiceTest(); List<BmcUser> bmcUsers =
 * HmsPluginServiceCallWrapper.invokeHmsPluginService(boardServiceTest, (ServiceServerNode)node.getServiceObject(),
 * "getManagementUsers", new Object[]{node.getServiceObject()}); assertNotNull(bmcUsers); }
 * @Test(expected=HmsException.class) public void invokeHmsPluginServiceTest_NullParam() throws
 * HmsResourceBusyException, HmsResponseTimeoutException, HmsException {
 * assertNull(HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("N1"));
 * HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode("N1"); ServerNode node = new ServerNode();
 * node.setNodeID("N1"); node.setIbIpAddress("10.28.197.28"); node.setOsUserName("root"); node.setOsPassword("root123");
 * IHmsComponentService boardServiceTest = new BoardServiceTest(); List<BmcUser> bmcUsers =
 * HmsPluginServiceCallWrapper.invokeHmsPluginService(boardServiceTest, (ServiceServerNode)node.getServiceObject(),
 * "getManagementUsers", null); assertNotNull(bmcUsers); }
 * @Test public void invokeHmsPluginServiceTest_InbandRequests() throws HmsResourceBusyException,
 * HmsResponseTimeoutException, HmsException {
 * assertNull(HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("N1"));
 * HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode("N1"); ServerNode node = new ServerNode();
 * node.setNodeID("N1"); node.setIbIpAddress("10.28.197.28"); node.setOsUserName("root"); node.setOsPassword("root123");
 * IHmsComponentService inbandService = new InbandBoardServiceTest(); List<CpuInfo> cpuInfos =
 * HmsPluginServiceCallWrapper.invokeHmsPluginService(inbandService, (ServiceServerNode)node.getServiceObject(),
 * "getCpuInfo", new Object[]{node.getServiceObject()}); assertNotNull(cpuInfos); assertEquals("total Cpus ", 2,
 * cpuInfos.size()); }
 * @Test(expected=HmsResourceBusyException.class) public void
 * invokeHmsPluginServiceTest_InbandRequests_MultipleRequests() throws HmsResourceBusyException,
 * HmsResponseTimeoutException, HmsException {
 * assertNull(HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("N1"));
 * HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode("N1"); final ServerNode node = new ServerNode();
 * node.setNodeID("N1"); node.setIbIpAddress("10.28.197.28"); node.setOsUserName("root"); node.setOsPassword("root123");
 * ExecutorService executorService = Executors.newFixedThreadPool(5); Map<Integer, Future<Object>> futures = new
 * HashMap<Integer, Future<Object>>(); try { for (int i = 0; i < 5; i++) { futures.put(i, executorService.submit(new
 * Callable<Object>() { public Object call() throws HmsResourceBusyException, HmsResponseTimeoutException, HmsException
 * { IHmsComponentService inbandService = new InbandBoardServiceTest(); return
 * HmsPluginServiceCallWrapper.invokeHmsPluginService(inbandService, (ServiceServerNode) node.getServiceObject(),
 * HmsApi.MEMORY_INFO, new Object[] { node.getServiceObject() }); } })); } } finally { executorService.shutdown(); } try
 * { Thread.sleep(1000L); } catch (InterruptedException e) { } }
 * @Test(expected=HmsResponseTimeoutException.class) public void invokeHmsPluginServiceTest_InbandRequests_Timeout()
 * throws HmsResourceBusyException, HmsResponseTimeoutException, HmsException {
 * assertNull(HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("N1"));
 * HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode("N1"); final ServerNode node = new ServerNode();
 * node.setNodeID("N1"); node.setIbIpAddress("10.28.197.28"); node.setOsUserName("root"); node.setOsPassword("root123");
 * IHmsComponentService inbandService = new InbandBoardServiceTest(); List<MemoryInfo> memories=
 * HmsPluginServiceCallWrapper.invokeHmsPluginService(inbandService, (ServiceServerNode) node.getServiceObject(),
 * "getSystemMemoryInfo", new Object[] { node.getServiceObject() }); assertNull(memories); }
 * @Test public void invokeHmsPluginSwitchServiceTest() throws HmsResourceBusyException, HmsResponseTimeoutException,
 * HmsException { assertNull(HmsPluginServiceCallWrapper.getNodeRateLimitModelObject("S1"));
 * HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode("S1"); SwitchNode switchNode = new SwitchNode("S1", "SSH",
 * "10.28.197.248", 22, "lanier", "l@ni3r2o14"); ISwitchService switchService = new SwitchServiceTest(); SwitchOsInfo
 * osInfo= HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService(switchService, switchNode, 10000L,
 * "getSwitchOsInfo", new Object[]{switchNode}); assertNotNull(osInfo); assertEquals("Os Vendor", "Arista OS",
 * osInfo.getOsName()); } }
 */
