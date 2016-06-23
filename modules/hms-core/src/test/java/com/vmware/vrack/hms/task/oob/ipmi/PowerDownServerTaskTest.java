/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package com.vmware.vrack.hms.task.oob.ipmi;

import static org.junit.Assert.assertFalse;

import org.junit.Ignore;
import org.junit.Test;

import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
*
* @author tanvishah
*/
@Ignore
public class PowerDownServerTaskTest {

public PowerDownServerTaskTest() {
}

/**
* Test of executeTask method, of class PowerDownServerTask for discoverable node
*/
@Test
public void testExecutePowerDownTaskForDiscverableNode() throws Exception {
System.out.println("[TS]: testExecutePowerDownTaskForDiscverableNode");
//IpmiTaskConnector ITConnector=new IpmiTaskConnector("10.28.197.204","ADMIN","ADMIN");
// ITConnector.createConnection();
TaskResponse TR = new TaskResponse(new ServerNode("4","10.28.197.204","ADMIN","ADMIN"));

PowerDownServerTask PDSTask = new PowerDownServerTask(TR);
PDSTask.executeTask();
System.out.println("[TS]: Expected Result : Server Node is Powered = False , Actual Result : Server Node is Powered = "+TR.getNode().isPowered());
assertFalse(TR.getNode().isPowered());
}


/**
* Test of executeTask method, of class PowerDownServerTask for discoverable node
*/
@Test
public void testExecutePowerDownTaskForDiscverableNodeWrongOSCreds() throws Exception {
System.out.println("[TS]: testExecutePowerDownTaskForDiscverableNodeWrongOSCreds");
//        IpmiTaskConnector ITConnector=new IpmiTaskConnector("10.28.197.204","ADMIN","ADMIN");
//        ITConnector.createConnection();
TaskResponse TR = new TaskResponse(new ServerNode("4","10.28.197.204","ADMIN","ADMIN"));
PowerDownServerTask PDSTask = new PowerDownServerTask(TR);
PDSTask.executeTask();
System.out.println("[TS]: Expected Result : Server Node is Powered = False , Actual Result : Server Node is Powered = "+TR.getNode().isPowered());
assertFalse(TR.getNode().isPowered());
}



/**
* Test of executeTask method, of class PowerDownServerTask for discoverable node
*/
@Test
public void testExecutePowerDownTaskForNonDiscverableNodeWrongOSCreds() throws Exception {
System.out.println("[TS]: testExecutePowerDownTaskForNonDiscverableNodeWrongOSCreds");
//        IpmiTaskConnector ITConnector=new IpmiTaskConnector("10.28.197.204","ADMIN","ADMIN");
//        ITConnector.createConnection();
TaskResponse TR = new TaskResponse(new ServerNode("4","10.28.197.204","ADMIN","ADMIN"));
PowerDownServerTask PDSTask = new PowerDownServerTask(TR);
PDSTask.executeTask();
System.out.println("[TS]: Expected Result : Server Node is Powered = False , Actual Result : Server Node is Powered = "+TR.getNode().isPowered());
assertFalse(TR.getNode().isPowered());
}

/**
* Test of getTaskOutput method, of class PowerDownServerTask.
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
PowerDownServerTask PDSTask = new PowerDownServerTask(TRr);
String expResult = "";
// String result = PDSTask.getTaskOutput();
assertEquals(expResult, result);
System.out.println("[TS]: testGetTaskOutput");
}
*/
}
