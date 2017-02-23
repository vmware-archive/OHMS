/* ********************************************************************************
 * PowerResetServerTaskTest.java
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
package com.vmware.vrack.hms.task.oob.ipmi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 *
 * @author tanvishah
 */
@Ignore
public class PowerResetServerTaskTest {
    
    public PowerResetServerTaskTest() {
    }

    /**
     * Test of executeTask method, of class PowerResetServerTask for discoverable node
     */
    @Test
    public void testExecutePowerResetTaskForDiscverableNode() throws Exception {
        System.out.println("[TS]: testExecutePowerResetTaskForDiscverableNode");
//        IpmiTaskConnector ITConnector=new IpmiTaskConnector("10.28.197.204","ADMIN","ADMIN");
//        ITConnector.createConnection();
        TaskResponse TR = new TaskResponse(new ServerNode("4","10.28.197.204","ADMIN","ADMIN"));
        
        PowerResetServerTask PRSTask = new PowerResetServerTask(TR);
        PRSTask.executeTask();
        //TR.getNode().refreshNodeStatus();
        System.out.println("[TS]: Expected Result : Server Node is Powered = True , Actual Result : Server Node is Powered = "+TR.getNode().isPowered());
        assertTrue(TR.getNode().isPowered());
    }
    
    
    /**
     * Test of executeTask method, of class PowerResetServerTask for discoverable node
     */
    @Test
    public void testExecutePowerResetTaskForDiscverableNodeWrongOSCreds() throws Exception {
        System.out.println("[TS]: testExecutePowerResetTaskForDiscverableNodeWrongOSCreds");
//        IpmiTaskConnector ITConnector=new IpmiTaskConnector("10.28.197.204","ADMIN","ADMIN");
//        ITConnector.createConnection();
        TaskResponse TR = new TaskResponse(new ServerNode("4","10.28.197.204","ADMIN","ADMIN"));
        PowerResetServerTask PRSTask = new PowerResetServerTask(TR);
        PRSTask.executeTask();
        //TR.getNode().refreshNodeStatus();
        System.out.println("[TS]: Expected Result : Server Node is Powered = True , Actual Result : Server Node is Powered = "+TR.getNode().isPowered());
        assertTrue(TR.getNode().isPowered());
    }

    
       
    /**
     * Test of executeTask method, of class PowerResetServerTask for discoverable node
     */
    @Test
    public void testExecutePowerResetTaskForNonDiscverableNodeWrongOSCreds() throws Exception {
        System.out.println("[TS]: testExecutePowerResetTaskForNonDiscverableNodeWrongOSCreds");
//        IpmiTaskConnector ITConnector=new IpmiTaskConnector("10.28.197.204","ADMIN","ADMIN");
//        ITConnector.createConnection();
        TaskResponse TR = new TaskResponse(new ServerNode("4","10.28.197.204","ADMIN","ADMIN"));
        PowerResetServerTask PRSTask = new PowerResetServerTask(TR);
        PRSTask.executeTask();
        //TR.getNode().refreshNodeStatus();
        System.out.println("[TS]: Expected Result : Server Node is Powered = False , Actual Result : Server Node is Powered = "+TR.getNode().isPowered());
        assertFalse(TR.getNode().isPowered());
    }
    
    /**
     * Test of getTaskOutput method, of class PowerResetServerTask.
     */
    /*
    @Test 
    public void testGetTaskOutput() throws Exception {
        System.out.println("[TS]: testGetTaskOutput");
        IpmiTaskConnector ITConnector=new IpmiTaskConnector("10.28.197.204","ADMIN","ADMIN");
        ITConnector.createConnection();
        TaskResponse TR = new TaskResponse();
        TR.getNode()= new ServerNode("4","10.28.197.204","ADMIN","ADMIN");
        TR.hms_node_id="4";
        PowerResetServerTask PRSTask = new PowerResetServerTask(TR,ITConnector);
        String expResult = "";
        String result = PRSTask.getTaskOutput();
        assertEquals(expResult, result);
        System.out.println("[TS]: testGetTaskOutput");
    }
    */
}
