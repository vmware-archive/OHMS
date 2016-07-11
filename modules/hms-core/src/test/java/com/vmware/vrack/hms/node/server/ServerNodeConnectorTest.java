/* ********************************************************************************
 * ServerNodeConnectorTest.java
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
package com.vmware.vrack.hms.node.server;

import org.apache.log4j.Logger;

/**
 * @author tanvishah
 */
public class ServerNodeConnectorTest
{
    private static Logger logger = Logger.getLogger( ServerNodeConnector.class );

    public ServerNodeConnectorTest()
    {
    }
    /*
     * Test caes commented out as OS details code is deleted from ServerNodeConnector / // // /** // * Test of isValidOs
     * method, of class ServerNodeConnector when valid OS name is sent as parameter. //
     */
    //
    // @Test
    //
    // public void isValidOsForValidOsName(){
    // logger.info("[TS] : isValidOsForValidOsName");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // boolean isValid = SNConnector.isValidOs("VMware ESXi");
    //
    // logger.info("[TS] : Expected Result : IsValidOS = TRUE , actual result : IsValidOS = "+isValid);
    // assertTrue(isValid);
    // }
    //
    // /**
    // * Test of isValidOs method, of class ServerNodeConnector when invalid OS name is sent as parameter.
    // */
    //
    // @Test
    //
    // public void isValidOsForInvalidOsName(){
    // logger.info("[TS] : isValidOsForInvalidOsName");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // boolean isValid = SNConnector.isValidOs("OS Abc");
    //
    // logger.info("[TS] : Expected Result : IsValidOS = FALSE, actual result : IsValidOS = "+isValid);
    // assertFalse(isValid);
    // }
    //
    // /**
    // * Test of getOsName method, of class ServerNodeConnector when a string composed of at least 2 words is sent as
    // parameter.
    // */
    //
    // @Test
    // public void getOsNameWhenAtleastTwoWordsAreSent(){
    // logger.info("[TS] : getOsNameWhenAtleastTwoWordsAreSent");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // String result=SNConnector.getOsName("Vmware ESXi 5.5.0 build-423");
    //
    // logger.info("[TS] : Expected Result : OS Name = Vmware ESXi , actual result : OS Name = "+result);
    // assertEquals("Vmware ESXi",result);
    // }
    //
    // /**
    // * Test of getOsName method, of class ServerNodeConnector when Empty String is sent as parameter.
    // */
    //
    // @Test
    // public void getOsNameWhenEmptyStringIsSent(){
    // logger.info("[TS] : getOsNameWhenEmptyStringIsSent");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // String result=SNConnector.getOsName("");
    //
    // logger.info("[TS] : Expected Result : OS Name = EMPTY STRING, actual result : OS Name = "+result);
    // assertEquals("",result);
    // }
    //
    // /**
    // * Test of getOsName method, of class ServerNodeConnector when a string composed of 1 word is sent as parameter.
    // */
    //
    // @Test
    // public void getOsNameWhenOneWordIsSent(){
    // logger.info("[TS] : getOsNameWhenOneWordIsSent");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // String result=SNConnector.getOsName("Vmware");
    //
    // logger.info("[TS] : Expected Result : OS Name = Vmware, actual result : OS Name = "+result);
    // assertEquals("Vmware",result);
    // }
    //
    //
    // /**
    // * Test of getOsVersion method, of class ServerNodeConnector when a string composed of at least 3 words is sent as
    // parameter.
    // */
    //
    // @Test
    // public void getOsVersionWhenAtleastThreeWordsAreSent(){
    // logger.info("[TS] : getOsVersionWhenAtleastThreeWordsAreSent");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // String result=SNConnector.getOsVersion("Vmware ESXi 5.5.0 build-423");
    //
    // logger.info("[TS] : Expected Result : OS Version = 5.5.0 , actual result : OS Version = "+result);
    // assertEquals("5.5.0",result);
    // }
    //
    // /**
    // * Test of getOsVersion method, of class ServerNodeConnector when Empty String is sent as parameter.
    // */
    //
    // @Test
    // public void getOsVersionWhenEmptyStringIsSent(){
    // logger.info("[TS] : getOsVersionWhenEmptyStringIsSent");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // String result=SNConnector.getOsVersion("");
    //
    // logger.info("[TS] : Expected Result : OS Version = NULL, actual result : OS Version = "+result);
    // assertNull(result);
    // }
    //
    // /**
    // * Test of getOsVersion method, of class ServerNodeConnector when a string composed of less than 3 words is sent
    // as parameter.
    // */
    //
    // @Test
    // public void getOsVersionWhenOneWordIsSent(){
    // logger.info("[TS] : getOsVersionWhenOneWordIsSent");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // String result=SNConnector.getOsVersion("Vmware");
    //
    // logger.info("[TS] : Expected Result : OS Version = null, actual result : OS Version = "+result);
    // assertNull(result);
    // }
    //
    //
    // /**
    // * Test of getOsBuildVersion method, of class ServerNodeConnector when a string with '-' is sent as parameter.
    // */
    //
    // @Test
    // public void getOsBuildVersionWhenStringWithDashIsSentAsAParam(){
    // logger.info("[TS] : getOsVersionWhenEmptyStringIsSent");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // String result=SNConnector.getOsBuildVersion("Vmware ESXi 5.5.0 build-423");
    //
    // logger.info("[TS] : Expected Result : OS Version = 423, actual result : OS Version = "+result);
    // assertEquals("423",result);
    // }
    //
    // /**
    // * Test of getOsBuildVersion method, of class ServerNodeConnector when a string without '-' is sent as parameter.
    // */
    //
    // @Test
    // public void getOsBuildVersionWhenStringWithoutDashIsSentAsAParam(){
    // logger.info("[TS] : getOsVersionWhenOneWordIsSent");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // String result=SNConnector.getOsBuildVersion("Vmware ESXi 5.5.0 build 423");
    //
    // logger.info("[TS] : Expected Result : OS Version = NULL, actual result : OS Version = "+result);
    // assertNull(result);
    // }
    //
    //
    // //TO DO
    // /**
    // * Test of updateNodeOsDetails method, of class ServerNodeConnector when Discoverable Node is sent as parameter.
    // */
    // @Test
    // public void updateNodeOsDetailsWithDiscoverableNodeAsParam() throws Exception{
    // logger.info("[TS] : updateNodeOsDetailsWithDiscoverableNodeAsParam");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // HmsNode sNode = new ServerNode("3","10.28.197.204","ADMIN","ADMIN");
    // SNConnector.updateNodeOsDetails(sNode);
    // logger.info("[TS] :End updateNodeOsDetailsWithDiscoverableNodeAsParam");
    // }
    //
    /**
     * Test of refreshNodeStatus method, of class ServerNodeConnector, when a Valid Discoverable IP address and Positive
     * Node ID is passed as parameters
     */
    // @Test
    // public void testRefreshNodeStausForValidDiscoverableIPAddressAndPositiveNodeID() {
    //
    // logger.info("[TS] : testRefreshNodeStausForValidDiscoverableIPAddressAndPositiveNodeID");
    // HmsNode sNode = new ServerNode("3","10.28.197.204","ADMIN","ADMIN");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // sNode=SNConnector.refreshNodeStatus(sNode);
    // logger.info("[TS] : Expected result : discoverable = true, actual result : discoverable = " +
    // sNode.isDiscoverable() );
    // assertTrue(sNode.isDiscoverable());
    // }
    //
    //
    // /**
    // * Test of refreshNodeStatus method, of class ServerNodeConnector, when a Valid Non Discoverable IP address and
    // Positive Node ID is passed as parameters
    // */
    // @Test
    // public void testRefreshNodeStausForValidNonDiscoverableIPAddressAndPositiveNodeID() {
    //
    // logger.info("[TS] : testRefreshNodeStausForValidNonDiscoverableIPAddressAndPositiveNodeID");
    // HmsNode sNode = new ServerNode("3","10.28.197.101","ADMIN","ADMIN"); //IP address TO BE changed
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // sNode = SNConnector.refreshNodeStatus(sNode);
    // logger.info("[TS] : Expected result : discoverable = false, actual result : discoverable = " +
    // sNode.isDiscoverable() );
    // assertFalse(sNode.isDiscoverable());
    // }
    //
    // /**
    // * Test of refreshNodeStatus method, of class ServerNodeConnector, when a Valid IP address and Negative Node ID is
    // passed as parameters
    // */
    // @Test
    // public void testRefreshNodeStausForValidIPAddressAndNegativeNodeID() {
    // logger.info("[TS] : testRefreshNodeStausForValidIPAddressAndNegativeNodeID");
    // HmsNode sNode = new ServerNode("-1","0.0.0.0","ADMIN","ADMIN");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // sNode=SNConnector.refreshNodeStatus(sNode);
    // logger.info("[TS] : Expected result : isPowered = false, actual result : isPowered = " + sNode.isPowered() );
    // assertFalse(sNode.isPowered());
    // }
    //
    //
    //
    // /**
    // * Test of refreshNodeStatus method, of class ServerNodeConnector, when a Invalid IP address is passed as a
    // parameter
    // */
    // @Test
    // public void testRefreshNodeStausForInvalidIPAddress() {
    // logger.info("[TS] : testRefreshNodeStausForInvalidIPAddress");
    // HmsNode sNode = new ServerNode("3","10.1.162.300","ADMIN","ADMIN");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // sNode=SNConnector.refreshNodeStatus(sNode);
    // logger.info("[TS] : Expected result : discoverable = false, actual result : discoverable = " +
    // sNode.isDiscoverable()+" AND Expected result : isPowered = false, actual result : isPowered = " +
    // sNode.isPowered() );
    // assertFalse(sNode.isDiscoverable());
    // assertFalse(sNode.isPowered());
    // }
    //
    // /**
    // * Test of refreshNodeStatus method, of class ServerNodeConnector, when empty string is passed in place of IP
    // address parameter
    // */
    // @Test
    // public void testRefreshNodeStausForNoIPAddress() {
    // logger.info("[TS] : testRefreshNodeStausForNoIPAddress");
    // HmsNode sNode = new ServerNode("3","","ADMIN","ADMIN");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // sNode=SNConnector.refreshNodeStatus(sNode);
    // logger.info("[TS] : Expected result : discoverable = false, actual result : discoverable = " +
    // sNode.isDiscoverable() +" AND Expected result : isPowered = false, actual result : isPowered = " +
    // sNode.isPowered() );
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
    // * Test of destroy method, of class ServerNodeConnector.
    // */
    // @Test
    // public void testDestroy() {
    // logger.info("[TS] : testDestroy");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // SNConnector.destroy();
    // logger.info("[TS] :End testDestroy");
    // //Need to find out IpmiConnector's stateafter Tear Down
    // //assertNull(SNConnector.getConnector());
    // //logger.info("[TS] : Expected result : connector=NULL , actual result : connector="+
    // SNConnector.getConnector());
    // }
    //
    //
    //
    // /**
    // * Test of ResetServerPower method, of class ServerNodeConnector when Power Down command is sent to a Discoverable
    // Server Node.
    // */
    //
    // @Test
    // public void testPowerDownDicoverableServer(){
    //
    // logger.info("[TS] : testPowerDownDicoverableServer");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // HmsNode sNode = new ServerNode("3","10.28.197.204","ADMIN","ADMIN");
    // PowerCommand powerCmd = PowerCommand.PowerDown;
    // try {
    // SNConnector.resetServerPower(sNode, powerCmd);
    // } catch (Exception ex) {
    //
    // }
    // try {
    // //May be Put some Thread.sleep
    // Thread.sleep(4000);
    // } catch (InterruptedException ex) {
    //
    // }
    // //sNode.refreshNodePowerStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= false, actual result : Server node isPowered=
    // "+sNode.isPowered());
    // assertFalse(sNode.isPowered());
    //
    // }
    //
    // /**
    // * Test of ResetServerPower method, of class ServerNodeConnector when Power Up command is sent to a Discoverable
    // Server Node.
    // */
    //
    // @Test
    // public void testPowerUpDicoverableServer(){
    // logger.info("[TS] : testPowerUpDicoverableServer");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // HmsNode sNode = new ServerNode("1","10.28.197.202","ADMIN","ADMIN");
    // PowerCommand powerCmd = PowerCommand.PowerUp;
    // try {
    // SNConnector.resetServerPower(sNode, powerCmd);
    // } catch (Exception ex) {
    // }
    // try {
    // //May be Put some Thread.sleep
    // Thread.sleep(4000);
    // } catch (InterruptedException ex) {
    //
    // }
    // //sNode.refreshNodePowerStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= true, actual result : Server node isPowered=
    // "+sNode.isPowered());
    // assertTrue(sNode.isPowered());
    // }
    //
    // /**
    // * Test of ResetServerPower method, of class ServerNodeConnector when Hard Reset command is sent to aDiscoverable
    // Server Node.
    // */
    //
    // @Test //need to find out what what the expected result should be
    // public void testHardResetDicoverableServer(){
    // logger.info("[TS] : testPowerUpNonDicoverableServer");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // HmsNode sNode = new ServerNode("1","10.28.197.202","ADMIN","ADMIN");
    // //sNode.refreshNodePowerStatus();
    // boolean sNodePowerStatus=sNode.isPowered();
    // PowerCommand powerCmd = PowerCommand.HardReset;
    // try {
    // SNConnector.resetServerPower(sNode, powerCmd);
    // } catch (Exception ex) {
    // }
    //
    // //sNode.refreshNodePowerStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= True, actual result : Server node isPowered=
    // "+sNode.isPowered());
    // assertTrue(sNode.isPowered());
    // }
    //
    // /**
    // * Test of ResetServerPower method, of class ServerNodeConnector when Power Down command is sent to a Non
    // Discoverable Server Node.
    // */
    //
    // @Test
    // public void testPowerDownNonDicoverableServer(){
    // logger.info("[TS] : testPowerDownNonDicoverableServer");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // HmsNode sNode = new ServerNode("3","10.28.197.101","ADMIN","ADMIN");
    // //sNode.refreshNodePowerStatus();
    // boolean sNodePowerStatus=sNode.isPowered();
    // PowerCommand powerCmd = PowerCommand.PowerDown;
    // try {
    // SNConnector.resetServerPower(sNode, powerCmd);
    // } catch (Exception ex) {
    // }
    //
    // //sNode.refreshNodePowerStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= "+sNodePowerStatus+", actual result : Server node
    // isPowered= "+sNode.isPowered());
    // assertEquals(sNodePowerStatus,sNode.isPowered());
    // }
    //
    // /**
    // * Test of ResetServerPower method, of class ServerNodeConnector when Power Up command is sent to a Non
    // Discoverable Server Node.
    // */
    //
    // @Test
    // public void testPowerUpNonDicoverableServer() {
    // logger.info("[TS] : testPowerUpNonDicoverableServer");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // HmsNode sNode = new ServerNode("3","10.28.197.101","ADMIN","ADMIN");
    // //sNode.refreshNodePowerStatus();
    // boolean sNodePowerStatus=sNode.isPowered();
    // PowerCommand powerCmd = PowerCommand.PowerUp;
    // try {
    // SNConnector.resetServerPower(sNode, powerCmd);
    // } catch (Exception ex) {
    //
    // }
    //
    // //sNode.refreshNodePowerStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= "+sNodePowerStatus+", actual result : Server node
    // isPowered= "+sNode.isPowered());
    // assertEquals(sNodePowerStatus,sNode.isPowered());
    // }
    //
    // /**
    // * Test of ResetServerPower method, of class ServerNodeConnector when Hard Reset command is sent to a Non
    // Discoverable Server Node.
    // */
    //
    // @Test
    // public void testHardResetNonDicoverableServer() {
    // logger.info("[TS] : testPowerUpNonDicoverableServer");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // HmsNode sNode = new ServerNode("3","10.28.197.101","ADMIN","ADMIN");
    // //sNode.refreshNodePowerStatus();
    // boolean sNodePowerStatus=sNode.isPowered();
    // PowerCommand powerCmd = PowerCommand.HardReset;
    // try {
    // SNConnector.resetServerPower(sNode, powerCmd);
    // } catch (Exception ex) {
    //
    // }
    //
    // //sNode.refreshNodePowerStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= "+sNodePowerStatus+", actual result : Server node
    // isPowered= "+sNode.isPowered());
    // assertEquals(sNodePowerStatus,sNode.isPowered());
    // }
    //
    //
    /*
     * Test caes commented out as OS details code is deleted from ServerNodeConnector / // // /** // * Test of
     * updateNodeOsDetails method, of class ServerNodeConnector when Non Discoverable Node is sent as parameter. //
     */
    // @Test
    // public void updateNodeOsDetailsWithNonDiscoverableNodeAsParam() throws Exception{
    // logger.info("[TS] : updateNodeOsDetailsWithNonDiscoverableNodeAsParam");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // ServerNode sNode = new ServerNode("3","10.28.197.101","ADMIN","ADMIN");
    // sNode.setIbIpAddress("10.28.197.12");
    // sNode.setOsUserName("root");
    // sNode.setOsPassword("root123");
    // SNConnector.updateNodeOsDetails(sNode);
    // logger.info("[TS] : updateNodeOsDetailsWithDiscoverableNodeAsParam");
    //// assertEquals(null,(boolean)sNode.getNodeOsDetails().get("isEsxiNode"));
    //
    // }
    //
    // /**
    // * Test of updateNodeOsDetails method, of class ServerNodeConnector when Discoverable Node is sent as parameter.
    // */
    //
    //
    // @Test
    // public void updateServerNodeOsDetailsWithDiscoverableNodeAsParam(){
    // logger.info("[TS] : updateServerNodeOsDetailsWithDiscoverableNodeAsParam");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // ServerNode sNode = new ServerNode("3","10.28.197.204","ADMIN","ADMIN");
    // sNode.setIbIpAddress("10.28.197.24");
    // sNode.setOsUserName("root");
    // sNode.setOsPassword("root123");
    // SNConnector.updateServerNodeOsDetails(sNode);
    //
    // logger.info("[TS] : Expected result : OS version= 5.5.0 , actual result : OS Version=
    // "+sNode.getNodeOsDetails().get("osVersion"));
    // logger.info("[TS] : Expected result : isEsxiNode= true , actual result : isEsxiNode=
    // "+sNode.getNodeOsDetails().get("isEsxiNode"));
    // logger.info("[TS] : updateServerNodeOsDetailsWithDiscoverableNodeAsParam");
    // assertEquals(true,sNode.getNodeOsDetails().get("isEsxiNode"));
    // assertEquals("5.5.0", sNode.getNodeOsDetails().get("osVersion"));
    // }
    //
    // /**
    // * Test of updateNodeOsDetails method, of class ServerNodeConnector when Discoverable Node with IB IP address set
    // to null is sent as parameter.
    // */
    //
    //
    // @Test
    // public void updateServerNodeOsDetailsWithDiscoverableNodeAndNullIBIPAddressAsParam(){
    // logger.info("[TS] : updateServerNodeOsDetailsWithDiscoverableNodeAsParam");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // ServerNode sNode = new ServerNode("3","10.28.197.204","ADMIN","ADMIN");
    // sNode.setIbIpAddress(null);
    // sNode.setOsUserName("root");
    // sNode.setOsPassword("root123");
    // SNConnector.updateServerNodeOsDetails(sNode);
    //
    // logger.info("[TS] : Expected result : OS version= null , actual result : OS Version=
    // "+sNode.getNodeOsDetails().get("osVersion"));
    // logger.info("[TS] : Expected result : isEsxiNode= null , actual result : isEsxiNode=
    // "+sNode.getNodeOsDetails().get("isEsxiNode"));
    // assertNull(sNode.getNodeOsDetails().get("isEsxiNode"));
    // assertNull(sNode.getNodeOsDetails().get("osVersion"));
    // }
    // /**
    // * Test of updateNodeOsDetails method, of class ServerNodeConnector when Discoverable Node and empty User name are
    // sent as parameters.
    // */
    //
    //
    // @Test
    // public void updateServerNodeOsDetailsWithDiscoverableNodeAndEmptyUserNamseAsParam(){
    // logger.info("[TS] : updateServerNodeOsDetailsWithDiscoverableNodeAsParam");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // ServerNode sNode = new ServerNode("3","10.28.197.204","ADMIN","ADMIN");
    // sNode.setIbIpAddress(null);
    // sNode.setOsUserName("");
    // sNode.setOsPassword("root123");
    // SNConnector.updateServerNodeOsDetails(sNode);
    //
    // logger.info("[TS] : Expected result : OS version= null , actual result : OS Version=
    // "+sNode.getNodeOsDetails().get("osVersion"));
    // logger.info("[TS] : Expected result : isEsxiNode= null , actual result : isEsxiNode=
    // "+sNode.getNodeOsDetails().get("isEsxiNode"));
    // assertNull(sNode.getNodeOsDetails().get("isEsxiNode"));
    // assertNull(sNode.getNodeOsDetails().get("osVersion"));
    // }
    //
    // /**
    // * Test of updateNodeOsDetails method, of class ServerNodeConnector when Discoverable Node but wrong OS
    // credentials is sent as parameter.
    // */
    //
    //
    //
    // public void updateServerNodeOsDetailsWithDiscoverableNodeWrongCredsAsParam(){
    // logger.info("[TS] : updateServerNodeOsDetailsWithDiscoverableNodeWrongCredsAsParam");
    // ServerNodeConnector SNConnector = ServerNodeConnector.getInstance();
    // ServerNode sNode = new ServerNode("3","10.28.197.204","ADMIN","ADMIN");
    // sNode.setIbIpAddress("10.28.197.24");
    // sNode.setOsUserName("root");
    // sNode.setOsPassword("root");
    // SNConnector.updateServerNodeOsDetails(sNode);
    // logger.info("[TS] : Expected result : OS version= null , actual result : OS Version=
    // "+sNode.getNodeOsDetails().get("osVersion"));
    // logger.info("[TS] : Expected result : isEsxiNode= null , actual result : isEsxiNode=
    // "+sNode.getNodeOsDetails().get("isEsxiNode"));
    // logger.info("[TS] : updateServerNodeOsDetailsWithDiscoverableNodeWrongCredsAsParam");
    // assertNull(sNode.getNodeOsDetails().get("isEsxiNode"));
    // }
    //
    //
}
