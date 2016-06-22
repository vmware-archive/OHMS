/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
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
public class PowerUpServerTaskTest {

public PowerUpServerTaskTest() {
}

/**
* Test of executeTask method, of class PowerUpServerTask for discoverable node
*/
@Test
public void testExecutePowerUpTaskForDiscverableNode() throws Exception {
System.out.println("[TS]: testExecutePowerUpTaskForDiscverableNode");
//        IpmiTaskConnector ITConnector=new IpmiTaskConnector("10.28.197.204","ADMIN","ADMIN");
//        ITConnector.createConnection();
TaskResponse TR = new TaskResponse(new ServerNode("4","10.28.197.204","ADMIN","ADMIN"));
PowerUpServerTask PUSTask = new PowerUpServerTask(TR);
PUSTask.executeTask();
//TR.getNode().refreshNodeStatus();
System.out.println("[TS]: Expected Result : Server Node is Powered = True , Actual Result : Server Node is Powered = "+TR.getNode().isPowered());
assertTrue(TR.getNode().isPowered());
}


/**
* Test of executeTask method, of class PowerUpServerTask for discoverable node
*/
@Test
public void testExecutePowerUpTaskForDiscverableNodeWrongOSCreds() throws Exception {
System.out.println("[TS]: testExecutePowerUpTaskForDiscverableNodeWrongOSCreds");
//        IpmiTaskConnector ITConnector=new IpmiTaskConnector("10.28.197.204","ADMIN","ADMIN");
//        ITConnector.createConnection();
TaskResponse TR = new TaskResponse(new ServerNode("4","10.28.197.204","ADMIN","ADMIN"));
PowerUpServerTask PUSTask = new PowerUpServerTask(TR);
PUSTask.executeTask();
//TR.getNode().refreshNodeStatus();
System.out.println("[TS]: Expected Result : Server Node is Powered = True , Actual Result : Server Node is Powered = "+TR.getNode().isPowered());
assertTrue(TR.getNode().isPowered());
}



/**
* Test of executeTask method, of class PowerUpServerTask for discoverable node
*/
@Test
public void testExecutePowerUpTaskForNonDiscverableNodeWrongOSCreds() throws Exception {
System.out.println("[TS]: testExecutePowerUpTaskForNonDiscverableNodeWrongOSCreds");
//        IpmiTaskConnector ITConnector=new IpmiTaskConnector("10.28.197.204","ADMIN","ADMIN");
//        ITConnector.createConnection();
TaskResponse TR = new TaskResponse(new ServerNode("4","10.28.197.204","ADMIN","ADMIN"));
PowerUpServerTask PUSTask = new PowerUpServerTask(TR);
PUSTask.executeTask();
//TR.getNode().refreshNodeStatus();
System.out.println("[TS]: Expected Result : Server Node is Powered = false , Actual Result : Server Node is Powered = "+TR.getNode().isPowered());
assertFalse(TR.getNode().isPowered());
}

/**
* Test of getTaskOutput method, of class PowerUpServerTask.
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
PowerUpServerTask PUSTask = new PowerUpServerTask(TR,ITConnector);
String expResult = "";
String result = PUSTask.getTaskOutput();
assertEquals(expResult, result);
System.out.println("[TS]: testGetTaskOutput");
}
*/
}
