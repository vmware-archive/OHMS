/* ********************************************************************************
 * NodeConnectorTest.java
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
package com.vmware.vrack.hms.node;

import org.apache.log4j.Logger;

/**
 * @author tanvishah
 */
public class NodeConnectorTest
{
    private static Logger logger = Logger.getLogger( NodeConnectorTest.class );

    public NodeConnectorTest()
    {
    }

    /**
     * Test of refreshNodeStatus method, of class NodeConnector, when a Valid Discoverable IP address and Positive Node
     * ID is passed as parameters
     */
    // @Test
    // public void testRefreshNodeStausForValidDiscoverableIPAddressAndPositiveNodeID() {
    //
    // logger.info("[TS] : testRefreshNodeStausForValidDiscoverableIPAddressAndPositiveNodeID");
    // HmsNode sNode = new ServerNode("3","10.28.197.204","ADMIN","ADMIN");
    // NodeConnector SConnector = new NodeConnector();
    // sNode=SConnector.refreshNodeStatus(sNode);
    // logger.info("[TS] : Expected result : discoverable = true, actual result : discoverable = " +
    // sNode.isDiscoverable() );
    // assertTrue(sNode.isDiscoverable());
    // }

    /**
     * Test of refreshNodeStatus method, of class NodeConnector, when a Valid Non Discoverable IP address and Positive
     * Node ID is passed as parameters
     */
    // @Test
    // public void testRefreshNodeStausForValidNonDiscoverableIPAddressAndPositiveNodeID() {
    //
    // logger.info("[TS] : testRefreshNodeStausForValidNonDiscoverableIPAddressAndPositiveNodeID");
    // HmsNode sNode = new ServerNode("3","10.28.197.101","ADMIN","ADMIN"); //IP address TO BE changed
    // NodeConnector SConnector = new NodeConnector();
    // sNode=SConnector.refreshNodeStatus(sNode);
    // logger.info("[TS] : Expected result : discoverable = false, actual result : discoverable = " +
    // sNode.isDiscoverable() );
    // assertFalse(sNode.isDiscoverable());
    // }

    /**
     * Test of refreshNodeStatus method, of class NodeConnector, when a Valid IP address and Negative Node ID is passed
     * as parameters
     */
    // @Test
    // public void testRefreshNodeStausForValidIPAddressAndNegativeNodeID() {
    // logger.info("[TS] : testRefreshNodeStausForValidIPAddressAndNegativeNodeID");
    // HmsNode sNode = new ServerNode("-1","0.0.0.0","ADMIN","ADMIN");
    // NodeConnector SConnector = new NodeConnector();
    // sNode=SConnector.refreshNodeStatus(sNode);
    // logger.info("[TS] : Expected result : isPowered = false, actual result : isPowered = " + sNode.isPowered() );
    // assertFalse(sNode.isPowered());
    // }

    /**
     * Test of refreshNodeStatus method, of class NodeConnector, when a Invalid IP address is passed as a parameter
     */
    // @Test
    // public void testRefreshNodeStausForInvalidIPAddress() {
    // logger.info("[TS] : testRefreshNodeStausForInvalidIPAddress");
    // HmsNode sNode = new ServerNode("3","10.1.162.300","ADMIN","ADMIN");
    // NodeConnector SConnector = new NodeConnector();
    // sNode=SConnector.refreshNodeStatus(sNode);
    // logger.info("[TS] : Expected result : discoverable = false, actual result : discoverable = " +
    // sNode.isDiscoverable()+" AND Expected result : isPowered = false, actual result : isPowered = " +
    // sNode.isPowered() );
    // assertFalse(sNode.isDiscoverable());
    // assertFalse(sNode.isPowered());
    // }

    /**
     * Test of refreshNodeStatus method, of class NodeConnector, when empty string is passed in place of IP address
     * parameter
     */
    // @Test
    // public void testRefreshNodeStausForNoIPAddress() {
    // logger.info("[TS] : testRefreshNodeStausForNoIPAddress");
    // HmsNode sNode = new ServerNode("3","","ADMIN","ADMIN");
    // NodeConnector SConnector = new NodeConnector();
    // sNode=SConnector.refreshNodeStatus(sNode);
    // logger.info("[TS] : Expected result : discoverable = false, actual result : discoverable = " +
    // sNode.isDiscoverable() +" AND Expected result : isPowered = false, actual result : isPowered = " +
    // sNode.isPowered() );
    // assertFalse(sNode.isDiscoverable());
    // assertFalse(sNode.isPowered());
    // }

    /**
     * Test of destroy method, of class NodeConnector.
     */
    // @Test
    // public void testDestroy() {
    // logger.info("[TS] : testDestroy");
    // NodeConnector SConnector = new NodeConnector();
    // SConnector.destroy();
    // logger.info("[TS] :End testDestroy");
    // //Need to find out IpmiConnector's stateafter Tear Down
    // //assertNull(SConnector.getConnector());
    // //logger.info("[TS] : Expected result : connector=NULL , actual result : connector="+ SConnector.getConnector());
    // }

    /**
     * Test of ResetServerPower method, of class NodeConnector when Power Down command is sent to a Discoverable Server
     * Node.
     */

    // @Test
    // public void testPowerDownDicoverableServer(){
    //
    // logger.info("[TS] : testPowerDownDicoverableServer");
    // NodeConnector SConnector = new NodeConnector();
    // ServerNode sNode = new ServerNode("3","10.28.197.204","ADMIN","ADMIN");
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
    // ////sNode.refreshNodeStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= false, actual result : Server node isPowered=
    // "+sNode.isPowered());
    // assertFalse(sNode.isPowered());
    //
    // }

    /**
     * Test of ResetServerPower method, of class NodeConnector when Power Up command is sent to a Discoverable Server
     * Node.
     */

    // @Test
    // public void testPowerUpDicoverableServer(){
    // logger.info("[TS] : testPowerUpDicoverableServer");
    // NodeConnector SConnector = new NodeConnector();
    // ServerNode sNode = new ServerNode("3","10.28.197.204","ADMIN","ADMIN");
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
    // //sNode.refreshNodeStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= true, actual result : Server node isPowered=
    // "+sNode.isPowered());
    // assertTrue(sNode.isPowered());
    // }

    /**
     * Test of ResetServerPower method, of class NodeConnector when Hard Reset command is sent to aDiscoverable Server
     * Node.
     */

    // @Test //need to find out what what the expected result should be
    // public void testHardResetDicoverableServer(){
    // logger.info("[TS] : testPowerUpNonDicoverableServer");
    // NodeConnector SConnector = new NodeConnector();
    // ServerNode sNode = new ServerNode("3","10.28.197.204","ADMIN","ADMIN");
    // //sNode.refreshNodeStatus();
    // boolean sNodePowerStatus=sNode.isPowered();
    // PowerCommand powerCmd = PowerCommand.HardReset;
    // try {
    // SConnector.resetServerPower(sNode, powerCmd);
    // } catch (Exception ex) {
    // }
    //
    // //sNode.refreshNodeStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= True, actual result : Server node isPowered=
    // "+sNode.isPowered());
    // assertTrue(sNode.isPowered());
    // }

    /**
     * Test of ResetServerPower method, of class NodeConnector when Power Down command is sent to a Non Discoverable
     * Server Node.
     */

    // @Test
    // public void testPowerDownNonDicoverableServer(){
    // logger.info("[TS] : testPowerDownNonDicoverableServer");
    // NodeConnector SConnector = new NodeConnector();
    // ServerNode sNode = new ServerNode("3","10.28.197.101","ADMIN","ADMIN");
    // //sNode.refreshNodeStatus();
    // boolean sNodePowerStatus=sNode.isPowered();
    // PowerCommand powerCmd = PowerCommand.PowerDown;
    // try {
    // SConnector.resetServerPower(sNode, powerCmd);
    // } catch (Exception ex) {
    // }
    //
    // //sNode.refreshNodeStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= "+sNodePowerStatus+", actual result : Server node
    // isPowered= "+sNode.isPowered());
    // assertEquals(sNodePowerStatus,sNode.isPowered());
    // }

    /**
     * Test of ResetServerPower method, of class NodeConnector when Power Up command is sent to a Non Discoverable
     * Server Node.
     */

    // @Test
    // public void testPowerUpNonDicoverableServer() {
    // logger.info("[TS] : testPowerUpNonDicoverableServer");
    // NodeConnector SConnector = new NodeConnector();
    // ServerNode sNode = new ServerNode("3","10.28.197.101","ADMIN","ADMIN");
    // //sNode.refreshNodeStatus();
    // boolean sNodePowerStatus=sNode.isPowered();
    // PowerCommand powerCmd = PowerCommand.PowerUp;
    // try {
    // SConnector.resetServerPower(sNode, powerCmd);
    // } catch (Exception ex) {
    //
    // }
    //
    // //sNode.refreshNodeStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= "+sNodePowerStatus+", actual result : Server node
    // isPowered= "+sNode.isPowered());
    // assertEquals(sNodePowerStatus,sNode.isPowered());
    // }

    /**
     * Test of ResetServerPower method, of class NodeConnector when Hard Reset command is sent to a Non Discoverable
     * Server Node.
     */

    // @Test
    // public void testHardResetNonDicoverableServer() {
    // logger.info("[TS] : testPowerUpNonDicoverableServer");
    // NodeConnector SConnector = new NodeConnector();
    // ServerNode sNode = new ServerNode("3","10.28.197.101","ADMIN","ADMIN");
    // //sNode.refreshNodeStatus();
    // boolean sNodePowerStatus=sNode.isPowered();
    // PowerCommand powerCmd = PowerCommand.HardReset;
    // try {
    // SConnector.resetServerPower(sNode, powerCmd);
    // } catch (Exception ex) {
    //
    // }
    //
    // //sNode.refreshNodeStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= "+sNodePowerStatus+", actual result : Server node
    // isPowered= "+sNode.isPowered());
    // assertEquals(sNodePowerStatus,sNode.isPowered());
    // }

}
