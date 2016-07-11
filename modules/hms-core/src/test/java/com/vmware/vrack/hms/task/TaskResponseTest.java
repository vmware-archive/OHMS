/* ********************************************************************************
 * TaskResponseTest.java
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
public class TaskResponseTest {
private static Logger logger = Logger.getLogger(TaskResponseTest.class);
public TaskResponseTest() {
}

/**
* Test of equals method, of class TaskResponse when a similar object is passed as a parameter.
*/
@Test
public void testEqualsSimilarObjPassed() {
logger.info("[TS] : testEqualsSimilarObjPassed");
TaskResponse obj = new TaskResponse(new ServerNode("2","10.28.197.202","ADMIN","ADMIN"));

TaskResponse TR = new TaskResponse(new ServerNode("2","10.28.197.202","ADMIN","ADMIN"));

boolean result = TR.equals(obj);
logger.info("[TS] : Expected Result: TestResponse Equals=True, Actual Result: TestResponse Equals="+result);
assertTrue(result);
}

/**
* Test of equals method, of class TaskResponse when  dissimilar object is passed as a parameter.
*/
@Test
public void testEqualsNotSimilarObjPassed() {
logger.info("[TS] : testEqualsNotSimilarObjPassed");
TaskResponse obj = new TaskResponse(new ServerNode("3","10.28.197.203","ADMIN","ADMIN"));

TaskResponse TR = new TaskResponse(new ServerNode("2","10.28.197.202","ADMIN","ADMIN"));
boolean result = TR.equals(obj);
logger.info("[TS] : Expected Result: TestResponse Equals=False, Actual Result: TestResponse Equals="+result);
assertFalse(result);
}

}
