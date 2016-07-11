/* ********************************************************************************
 * TaskFactoryTest.java
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

/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package com.vmware.vrack.hms.task;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
/**
*
* @author tanvishah
*/
@Ignore
public class TaskFactoryTest {
private static Logger logger = Logger.getLogger(TaskFactoryTest.class);
public TaskFactoryTest() {
}


/**
* Test of getTask method, of class TaskFactory.
*/
@Test @Ignore
public void testGetTask_HMSBootUpTask() {
logger.info("[TS] : testGetTask_HMSBootUpTask");
TaskType taskType = TaskType.HMSBootUp;
ServerNode sNode =  new ServerNode("2","10.28.197.202","ADMIN","ADMIN");
sNode.setOsUserName("root");
sNode.setOsPassword("l@ni3r2o14");
sNode.setIbIpAddress("10.28.197.22");
IHmsTask result = TaskFactory.getTask(taskType, sNode);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNotNull(result);
}

/**
* Test of getTask method, of class TaskFactory.
*/
@Test
public void testGetTask_PowerDownServerTask() {
logger.info("[TS] : testGetTask_PowerDownServerTask");
TaskType taskType = TaskType.PowerDownServer;
ServerNode sNode =  new ServerNode("2","10.28.197.202","ADMIN","ADMIN");
sNode.setOsUserName("root");
sNode.setOsPassword("l@ni3r2o14");
sNode.setIbIpAddress("10.28.197.22");
IHmsTask result = TaskFactory.getTask(taskType, sNode);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNotNull(result);
}

/**
* Test of getTask method, of class TaskFactory.
*/
@Test
public void testGetTask_PowerResetServerTask() {
logger.info("[TS] : testGetTask_PowerResetServerTask");
TaskType taskType = TaskType.PowerResetServer;
ServerNode sNode =  new ServerNode("2","10.28.197.202","ADMIN","ADMIN");
sNode.setOsUserName("root");
sNode.setOsPassword("l@ni3r2o14");
sNode.setIbIpAddress("10.28.197.22");

IHmsTask result = TaskFactory.getTask(taskType, sNode);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNotNull(result);
}

/**
* Test of getTask method, of class TaskFactory.
*/
@Test
public void testGetTask_PowerUpServerTask() {
logger.info("[TS] : testGetTask_PowerUpServerTask");
TaskType taskType = TaskType.PowerUpServer;
ServerNode sNode =  new ServerNode("2","10.28.197.202","ADMIN","ADMIN");
sNode.setOsUserName("root");
sNode.setOsPassword("l@ni3r2o14");
sNode.setIbIpAddress("10.28.197.22");
IHmsTask result = TaskFactory.getTask(taskType, sNode);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNotNull(result);
}

/**
* Test of getTask method, of class TaskFactory.
*/
@Test
public void testGetTask_DiscoverSwitchTask() {
logger.info("[TS] : testGetTask_DiscoverSwitchTask");
TaskType taskType = TaskType.DiscoverSwitch;
ServerNode sNode =  new ServerNode("2","10.28.197.202","ADMIN","ADMIN");
sNode.setOsUserName("root");
sNode.setOsPassword("l@ni3r2o14");
sNode.setIbIpAddress("10.28.197.22");
IHmsTask result = TaskFactory.getTask(taskType, sNode);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNotNull(result);
}

/**
* Test of getTask method, of class TaskFactory.
*/
@Test @Ignore
public void testGetTask_MacAddressDiscoveryTask() {
logger.info("[TS] : testGetTask_MacAddressDiscoveryTask");
TaskType taskType = TaskType.MacAddressDiscovery;
ServerNode sNode =  new ServerNode("2","10.28.197.202","ADMIN","ADMIN");
sNode.setOsUserName("root");
sNode.setOsPassword("l@ni3r2o14");
sNode.setIbIpAddress("10.28.197.22");
IHmsTask result = TaskFactory.getTask(taskType, sNode);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNotNull(result);
}


/**
* Test of getTask method, of class TaskFactory.
*/
@Test
public void testGetTask_HMSBootUp_TaskResponse() {
logger.info("[TS] : testGetTask_HMSBootUp_TaskResponse");
TaskType taskType = TaskType.HMSBootUp;
TaskResponse TR = new TaskResponse(new ServerNode("2","10.28.197.202","ADMIN","ADMIN"));

IHmsTask result = TaskFactory.getTask(taskType, TR);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNotNull(result);
}

/**
* Test of getTask method, of class TaskFactory.
*/
@Test
public void testGetTask_PowerStatusServer_TaskResponse() {
logger.info("[TS] : testGetTask_PowerStatusServer_TaskResponse");
TaskType taskType = TaskType.PowerStatusServer;
TaskResponse TR = new TaskResponse(new ServerNode("2","10.28.197.202","ADMIN","ADMIN"));
IHmsTask result = TaskFactory.getTask(taskType, TR);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNotNull(result);
}
/**
* Test of getTask method, of class TaskFactory.
*/
@Test
public void testGetTask_ValidateServerOS_TaskResponse() {
logger.info("[TS] : testGetTask_ValidateServerOS_TaskResponse");
TaskType taskType = TaskType.ValidateServerOS;
TaskResponse TR = new TaskResponse(new ServerNode("2","10.28.197.202","ADMIN","ADMIN"));
IHmsTask result = TaskFactory.getTask(taskType, TR);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNotNull(result);
}

/**
* Test of getTask method, of class TaskFactory.
*/
@Test
public void testGetTask_ServerBoardInfo_TaskResponse() {
logger.info("[TS] : testGetTask_PowerStatusServer_TaskResponse");
TaskType taskType = TaskType.ServerBoardInfo;
TaskResponse TR = new TaskResponse(new ServerNode("2","10.28.197.202","ADMIN","ADMIN"));
IHmsTask result = TaskFactory.getTask(taskType, TR);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNotNull(result);
}
/**
* Test of getTask method, of class TaskFactory.
*/
@Test
public void testGetTask_MacAddressDiscovery_TaskResponse() {
logger.info("[TS] : testGetTask_ValidateServerOS_TaskResponse");
TaskType taskType = TaskType.MacAddressDiscovery;
TaskResponse TR = new TaskResponse(new ServerNode("2","10.28.197.202","ADMIN","ADMIN"));
IHmsTask result = TaskFactory.getTask(taskType, TR);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNotNull(result);
}

/**
* Test of getTask method, of class TaskFactory.
*/
@Test
public void testGetTask_TaskAsNull_TaskResponse() {
logger.info("[TS] : testGetTask_ValidateServerOS_TaskResponse");
TaskType taskType = TaskType.PowerStatusSwitch;
TaskResponse TR = new TaskResponse(new ServerNode("2","10.28.197.202","ADMIN","ADMIN"));
IHmsTask result = TaskFactory.getTask(taskType, TR);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNull(result);
}

/**
* Test of getTask method, of class TaskFactory.
*/
@Test
public void testGetTask_PowerStatusSwitch_TaskResponse() {
logger.info("[TS] : testGetTask_ValidateServerOS_TaskResponse");
TaskType taskType = TaskType.PowerStatusSwitch;
TaskResponse TR = new TaskResponse(new ServerNode("2","10.28.197.202","ADMIN","ADMIN"));
IHmsTask result = TaskFactory.getTask(taskType, TR);
logger.info("[TS] : Expected Result : Not Null , Actual Result : "+result);
assertNull(result);
}
}
