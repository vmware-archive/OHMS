/// *
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
// package com.vmware.vrack.hms;
//
// import com.vmware.vrack.hms.node.server.ServerNode;
// import java.util.logging.Level;
// import java.util.logging.Logger;
// import org.junit.After;
// import org.junit.AfterClass;
// import org.junit.Before;
// import org.junit.BeforeClass;
// import org.junit.Test;
// import static org.junit.Assert.*;
// import com.veraxsystems.vxipmi.coding.commands.chassis.PowerCommand;
// import org.junit.Ignore;
// import org.junit.experimental.categories.Categories.ExcludeCategory;
// import org.junit.runner.RunWith;
//
/// **
// *
// * @author tanvishah
// */
// public class ServerConnectorTest {
//
// public ServerConnectorTest() {
// }
//
// // Start - Commenting out test cases for methds whose signature has changed
//
// /**
// * Test of refreshNodeStatus method, of class ServerConnector, when a Valid Discoverable IP address and Positive Node
/// ID is passed as parameters
// */
// @Test
// public void testRefreshNodeStausForValidDiscoverableIPAddressAndPositiveNodeID() {
//
// System.out.println("[TS] : testRefreshNodeStausForValidDiscoverableIPAddressAndPositiveNodeID");
// ServerNode sNode = new ServerNode(1,"10.28.197.202",false,false);
// ServerConnector SConnector = ServerConnector.getInstance();
// sNode=SConnector.refreshNodeStatus(sNode);
// System.out.println("[TS] : Expected result : discoverable = true, actual result : discoverable = " +
/// sNode.isDiscoverable() );
// assertTrue(sNode.isDiscoverable());
// }
//
//
// /**
// * Test of refreshNodeStatus method, of class ServerConnector, when a Valid Non Discoverable IP address and Positive
/// Node ID is passed as parameters
// */
// @Test
// public void testRefreshNodeStausForValidNonDiscoverableIPAddressAndPositiveNodeID() {
//
// System.out.println("[TS] : testRefreshNodeStausForValidNonDiscoverableIPAddressAndPositiveNodeID");
// ServerNode sNode = new ServerNode(1,"10.28.197.101",false,false); //IP address TO BE changed
// ServerConnector SConnector = ServerConnector.getInstance();
// sNode=SConnector.refreshNodeStatus(sNode);
// System.out.println("[TS] : Expected result : discoverable = false, actual result : discoverable = " +
/// sNode.isDiscoverable() );
// assertFalse(sNode.isDiscoverable());
// }
//
// /**
// * Test of refreshNodeStatus method, of class ServerConnector, when a Valid IP address and Negative Node ID is passed
/// as parameters
// */
// @Test
// public void testRefreshNodeStausForValidIPAddressAndNegativeNodeID() {
// System.out.println("[TS] : testRefreshNodeStausForValidIPAddressAndNegativeNodeID");
// ServerNode sNode = new ServerNode(-1,"0.0.0.0",false,false);
// ServerConnector SConnector = ServerConnector.getInstance();
// sNode=SConnector.refreshNodeStatus(sNode);
// System.out.println("[TS] : Expected result : isPowered = false, actual result : isPowered = " + sNode.isPowered() );
// assertFalse(sNode.isPowered());
// }
//
//
//
// /**
// * Test of refreshNodeStatus method, of class ServerConnector, when a Invalid IP address is passed as a parameter
// */
// @Test
// public void testRefreshNodeStausForInvalidIPAddress() {
// System.out.println("[TS] : testRefreshNodeStausForInvalidIPAddress");
// ServerNode sNode = new ServerNode(1,"10.1.162.300",false,false);
// ServerConnector SConnector = ServerConnector.getInstance();
// sNode=SConnector.refreshNodeStatus(sNode);
// System.out.println("[TS] : Expected result : discoverable = false, actual result : discoverable = " +
/// sNode.isDiscoverable()+" AND Expected result : isPowered = false, actual result : isPowered = " + sNode.isPowered()
/// );
// assertFalse(sNode.isDiscoverable());
// assertFalse(sNode.isPowered());
// }
//
// /**
// * Test of refreshNodeStatus method, of class ServerConnector, when empty string is passed in place of IP address
/// parameter
// */
// @Test
// public void testRefreshNodeStausForNoIPAddress() {
// System.out.println("[TS] : testRefreshNodeStausForNoIPAddress");
// ServerNode sNode = new ServerNode(1,"",false,false);
// ServerConnector SConnector = ServerConnector.getInstance();
// sNode=SConnector.refreshNodeStatus(sNode);
// System.out.println("[TS] : Expected result : discoverable = false, actual result : discoverable = " +
/// sNode.isDiscoverable() +" AND Expected result : isPowered = false, actual result : isPowered = " + sNode.isPowered()
/// );
// assertFalse(sNode.isDiscoverable());
// assertFalse(sNode.isPowered());
// }
//
//
//
//
//
//
// /**
// * Test of destroy method, of class ServerConnector.
// */
// @Test
// public void testDestroy() {
// System.out.println("[TS] : testDestroy");
// ServerConnector SConnector = ServerConnector.getInstance();
// SConnector.destroy();
// System.out.println("[TS] :End testDestroy");
// //Need to find out IpmiConnector's stateafter Tear Down
// //assertNull(SConnector.getConnector());
// //System.out.println("[TS] : Expected result : connector=NULL , actual result : connector="+
/// SConnector.getConnector());
// }
//
//
//
// /**
// * Test of ResetServerPower method, of class ServerConnector when Power Down command is sent to a Discoverable Server
/// Node.
// */
//
// @Test
// public void testPowerDownDicoverableServer(){
//
// System.out.println("[TS] : testPowerDownDicoverableServer");
// ServerConnector SConnector = ServerConnector.getInstance();
// ServerNode sNode = new ServerNode(1,"10.28.197.202",false,false);
// PowerCommand powerCmd = PowerCommand.PowerDown;
// try {
// SConnector.resetServerPower(sNode, powerCmd);
// } catch (Exception ex) {
//
// }
// try {
// //May be Put some Thread.sleep
// Thread.sleep(4000);
// } catch (InterruptedException ex) {
//
// }
// sNode.refreshNodeStatus();
// System.out.println("[TS] : Expected Result : Sever node isPowered= false, actual result : Server node isPowered=
/// "+sNode.isPowered());
// assertFalse(sNode.isPowered());
//
// }
//
// /**
// * Test of ResetServerPower method, of class ServerConnector when Power Up command is sent to a Discoverable Server
/// Node.
// */
//
// @Test
// public void testPowerUpDicoverableServer(){
// System.out.println("[TS] : testPowerUpDicoverableServer");
// ServerConnector SConnector = ServerConnector.getInstance();
// ServerNode sNode = new ServerNode(1,"10.28.197.202",false,false);
// PowerCommand powerCmd = PowerCommand.PowerUp;
// try {
// SConnector.resetServerPower(sNode, powerCmd);
// } catch (Exception ex) {
// }
// try {
// //May be Put some Thread.sleep
// Thread.sleep(4000);
// } catch (InterruptedException ex) {
//
// }
// sNode.refreshNodeStatus();
// System.out.println("[TS] : Expected Result : Sever node isPowered= true, actual result : Server node isPowered=
/// "+sNode.isPowered());
// assertTrue(sNode.isPowered());
// }
//
// /**
// * Test of ResetServerPower method, of class ServerConnector when Hard Reset command is sent to aDiscoverable Server
/// Node.
// */
//
// @Test //need to find out what what the expected result should be
// public void testHardResetDicoverableServer(){
// System.out.println("[TS] : testPowerUpNonDicoverableServer");
// ServerConnector SConnector = ServerConnector.getInstance();
// ServerNode sNode = new ServerNode(1,"10.28.197.202",false,false);
// sNode.refreshNodeStatus();
// boolean sNodePowerStatus=sNode.isPowered();
// PowerCommand powerCmd = PowerCommand.HardReset;
// try {
// SConnector.resetServerPower(sNode, powerCmd);
// } catch (Exception ex) {
// }
//
// sNode.refreshNodeStatus();
// System.out.println("[TS] : Expected Result : Sever node isPowered= True, actual result : Server node isPowered=
/// "+sNode.isPowered());
// assertTrue(sNode.isPowered());
// }
//
// /**
// * Test of ResetServerPower method, of class ServerConnector when Power Down command is sent to a Non Discoverable
/// Server Node.
// */
//
// @Test
// public void testPowerDownNonDicoverableServer(){
// System.out.println("[TS] : testPowerDownNonDicoverableServer");
// ServerConnector SConnector = ServerConnector.getInstance();
// ServerNode sNode = new ServerNode(1,"10.28.197.101",false,false);
// sNode.refreshNodeStatus();
// boolean sNodePowerStatus=sNode.isPowered();
// PowerCommand powerCmd = PowerCommand.PowerDown;
// try {
// SConnector.resetServerPower(sNode, powerCmd);
// } catch (Exception ex) {
// }
//
// sNode.refreshNodeStatus();
// System.out.println("[TS] : Expected Result : Sever node isPowered= "+sNodePowerStatus+", actual result : Server node
/// isPowered= "+sNode.isPowered());
// assertEquals(sNodePowerStatus,sNode.isPowered());
// }
//
// /**
// * Test of ResetServerPower method, of class ServerConnector when Power Up command is sent to a Non Discoverable
/// Server Node.
// */
//
// @Test
// public void testPowerUpNonDicoverableServer() {
// System.out.println("[TS] : testPowerUpNonDicoverableServer");
// ServerConnector SConnector = ServerConnector.getInstance();
// ServerNode sNode = new ServerNode(1,"10.28.197.101",false,false);
// sNode.refreshNodeStatus();
// boolean sNodePowerStatus=sNode.isPowered();
// PowerCommand powerCmd = PowerCommand.PowerUp;
// try {
// SConnector.resetServerPower(sNode, powerCmd);
// } catch (Exception ex) {
//
// }
//
// sNode.refreshNodeStatus();
// System.out.println("[TS] : Expected Result : Sever node isPowered= "+sNodePowerStatus+", actual result : Server node
/// isPowered= "+sNode.isPowered());
// assertEquals(sNodePowerStatus,sNode.isPowered());
// }
//
// /**
// * Test of ResetServerPower method, of class ServerConnector when Hard Reset command is sent to a Non Discoverable
/// Server Node.
// */
//
// @Test
// public void testHardResetNonDicoverableServer() {
// System.out.println("[TS] : testPowerUpNonDicoverableServer");
// ServerConnector SConnector = ServerConnector.getInstance();
// ServerNode sNode = new ServerNode(1,"10.28.197.101",false,false);
// sNode.refreshNodeStatus();
// boolean sNodePowerStatus=sNode.isPowered();
// PowerCommand powerCmd = PowerCommand.HardReset;
// try {
// SConnector.resetServerPower(sNode, powerCmd);
// } catch (Exception ex) {
//
// }
//
// sNode.refreshNodeStatus();
// System.out.println("[TS] : Expected Result : Sever node isPowered= "+sNodePowerStatus+", actual result : Server node
/// isPowered= "+sNode.isPowered());
// assertEquals(sNodePowerStatus,sNode.isPowered());
// }
//
//
//
// // End - Commenting out test cases for methds whose signature has changed
//
// /**
// * Test of isValidOs method, of class ServerConnector when valid OS name is sent as parameter.
// */
//
// @Test
//
// public void isValidOsForValidOsName(){
// System.out.println("[TS] : isValidOsForValidOsName");
// ServerConnector SConnector = ServerConnector.getInstance();
// boolean isValid = SConnector.isValidOs("VMware ESXi");
//
// System.out.println("[TS] : Expected Result : IsValidOS = TRUE , actual result : IsValidOS = "+isValid);
// assertTrue(isValid);
// }
//
// /**
// * Test of isValidOs method, of class ServerConnector when invalid OS name is sent as parameter.
// */
//
// @Test
//
// public void isValidOsForInvalidOsName(){
// System.out.println("[TS] : isValidOsForInvalidOsName");
// ServerConnector SConnector = ServerConnector.getInstance();
// boolean isValid = SConnector.isValidOs("OS Abc");
//
// System.out.println("[TS] : Expected Result : IsValidOS = FALSE, actual result : IsValidOS = "+isValid);
// assertFalse(isValid);
// }
//
// /**
// * Test of getOsName method, of class ServerConnector when a string composed of at least 2 words is sent as parameter.
// */
//
// @Test
// public void getOsNameWhenAtleastTwoWordsAreSent(){
// System.out.println("[TS] : getOsNameWhenAtleastTwoWordsAreSent");
// ServerConnector SConnector = ServerConnector.getInstance();
// String result=SConnector.getOsName("Vmware ESXi 5.5.0 build-423");
//
// System.out.println("[TS] : Expected Result : OS Name = Vmware ESXi , actual result : OS Name = "+result);
// assertEquals("Vmware ESXi",result);
// }
//
// /**
// * Test of getOsName method, of class ServerConnector when Empty String is sent as parameter.
// */
//
// @Test
// public void getOsNameWhenEmptyStringIsSent(){
// System.out.println("[TS] : getOsNameWhenEmptyStringIsSent");
// ServerConnector SConnector = ServerConnector.getInstance();
// String result=SConnector.getOsName("");
//
// System.out.println("[TS] : Expected Result : OS Name = EMPTY STRING, actual result : OS Name = "+result);
// assertEquals("",result);
// }
//
// /**
// * Test of getOsName method, of class ServerConnector when a string composed of 1 word is sent as parameter.
// */
//
// @Test
// public void getOsNameWhenOneWordIsSent(){
// System.out.println("[TS] : getOsNameWhenOneWordIsSent");
// ServerConnector SConnector = ServerConnector.getInstance();
// String result=SConnector.getOsName("Vmware");
//
// System.out.println("[TS] : Expected Result : OS Name = Vmware, actual result : OS Name = "+result);
// assertEquals("Vmware",result);
// }
//
//
// /**
// * Test of getOsVersion method, of class ServerConnector when a string composed of at least 3 words is sent as
/// parameter.
// */
//
// @Test
// public void getOsVersionWhenAtleastThreeWordsAreSent(){
// System.out.println("[TS] : getOsVersionWhenAtleastThreeWordsAreSent");
// ServerConnector SConnector = ServerConnector.getInstance();
// String result=SConnector.getOsVersion("Vmware ESXi 5.5.0 build-423");
//
// System.out.println("[TS] : Expected Result : OS Version = 5.5.0 , actual result : OS Version = "+result);
// assertEquals("5.5.0",result);
// }
//
// /**
// * Test of getOsVersion method, of class ServerConnector when Empty String is sent as parameter.
// */
//
// @Test
// public void getOsVersionWhenEmptyStringIsSent(){
// System.out.println("[TS] : getOsVersionWhenEmptyStringIsSent");
// ServerConnector SConnector = ServerConnector.getInstance();
// String result=SConnector.getOsVersion("");
//
// System.out.println("[TS] : Expected Result : OS Version = NULL, actual result : OS Version = "+result);
// assertNull(result);
// }
//
// /**
// * Test of getOsVersion method, of class ServerConnector when a string composed of less than 3 words is sent as
/// parameter.
// */
//
// @Test
// public void getOsVersionWhenOneWordIsSent(){
// System.out.println("[TS] : getOsVersionWhenOneWordIsSent");
// ServerConnector SConnector = ServerConnector.getInstance();
// String result=SConnector.getOsVersion("Vmware");
//
// System.out.println("[TS] : Expected Result : OS Version = NULL, actual result : OS Version = "+result);
// assertNull(result);
// }
//
//
// /**
// * Test of getOsBuildVersion method, of class ServerConnector when a string with '-' is sent as parameter.
// */
//
// @Test
// public void getOsBuildVersionWhenStringWithDashIsSentAsAParam(){
// System.out.println("[TS] : getOsVersionWhenEmptyStringIsSent");
// ServerConnector SConnector = ServerConnector.getInstance();
// String result=SConnector.getOsBuildVersion("Vmware ESXi 5.5.0 build-423");
//
// System.out.println("[TS] : Expected Result : OS Version = NULL, actual result : OS Version = "+result);
// assertEquals("423",result);
// }
//
// /**
// * Test of getOsBuildVersion method, of class ServerConnector when a string without '-' is sent as parameter.
// */
//
// @Test
// public void getOsBuildVersionWhenStringWithoutDashIsSentAsAParam(){
// System.out.println("[TS] : getOsVersionWhenOneWordIsSent");
// ServerConnector SConnector = ServerConnector.getInstance();
// String result=SConnector.getOsBuildVersion("Vmware ESXi 5.5.0 build 423");
//
// System.out.println("[TS] : Expected Result : OS Version = NULL, actual result : OS Version = "+result);
// assertNull(result);
// }
//
//
// //TO DO
// /**
// * Test of updateNodeOsDetails method, of class ServerConnector when Discoverable Node is sent as parameter.
// */
// @Test
// public void updateNodeOsDetailsWithDiscoverableNodeAsParam() throws Exception{
// System.out.println("[TS] : updateNodeOsDetailsWithDiscoverableNodeAsParam");
// ServerConnector SConnector = ServerConnector.getInstance();
// ServerNode sNode = new ServerNode(1,"10.28.197.202",false,false);
// SConnector.updateNodeOsDetails(sNode);
// System.out.println("[TS] :End updateNodeOsDetailsWithDiscoverableNodeAsParam");
// }
//
// /**
// * Test of updateNodeOsDetails method, of class ServerConnector when Non Discoverable Node is sent as parameter.
// */
// @Test
// public void updateNodeOsDetailsWithNonDiscoverableNodeAsParam() throws Exception{
// System.out.println("[TS] : updateNodeOsDetailsWithNonDiscoverableNodeAsParam");
// ServerConnector SConnector = ServerConnector.getInstance();
// ServerNode sNode = new ServerNode(1,"10.28.197.101",false,false);
// SConnector.updateNodeOsDetails(sNode);
// System.out.println("[TS] :End updateNodeOsDetailsWithDiscoverableNodeAsParam");
// }
//
// /**
// * Test of updateNodeOsDetails method, of class ServerConnector when Non Discoverable Node is sent as parameter.
// */
//
//
// @Test
// public void updateServerNodeOsDetailsWithDiscoverableNodeAsParam(){
// System.out.println("[TS] : updateServerNodeOsDetailsWithDiscoverableNodeAsParam");
// ServerConnector SConnector = ServerConnector.getInstance();
// ServerNode sNode = new ServerNode(1,"10.28.197.101",false,false);
// SConnector.updateServerNodeOsDetails(sNode);
// System.out.println("[TS] : updateServerNodeOsDetailsWithDiscoverableNodeAsParam");
// }
//
// }
