/* ********************************************************************************
 * ServerNodeTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.node.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 * @author tanvishah
 */
public class ServerNodeTest
{
    private static Logger logger = Logger.getLogger( ServerNodeTest.class );

    public ServerNodeTest()
    {
    }

    // Start - Commenting out test cases for methds whose signature has changed
    /**
     * Test of refreshNodeStatus method, of class ServerNode, when a Valid Discoverable IP address and Positive Node ID
     * is passed as parameters
     */
    @Ignore
    @Test
    public void testRefreshNodeStatusForValidDiscoverableIPAddressAndPositiveNodeID()
    {
        logger.info( "[TS] : testRefreshNodeStatusForValidDiscoverableIPAddressAndPositiveNodeID" );
        ServerNode sNode = new ServerNode( "2", "10.28.197.202", "ADMIN", "ADMIN" );
        // sNode.refreshNodePowerStatus();
        logger.info( "[TS] : Expected result : discoverable = true, actual result : discoverable = "
            + sNode.isDiscoverable() );
        assertTrue( sNode.isDiscoverable() );
    }

    /**
     * Test of refreshNodeStatus method, of class ServerNode, when a Valid Non Discoverable IP address and Positive Node
     * ID is passed as parameters
     */
    @Test
    public void testRefreshNodeStatusForValidNonDiscoverableIPAddressAndPositiveNodeID()
    {
        logger.info( "[TS] : testRefreshNodeStatusForValidNonDiscoverableIPAddressAndPositiveNodeID" );
        ServerNode sNode = new ServerNode( "2", "10.28.197.1", "ADMIN", "ADMIN" ); // IP address TO BE changed
        // sNode.refreshNodePowerStatus();
        logger.info( "[TS] : Expected result : discoverable = false, actual result : discoverable = "
            + sNode.isDiscoverable() );
        assertFalse( sNode.isDiscoverable() );
    }

    /**
     * Test of refreshNodeStatus method, of class ServerNode, when a Valid IP address and Negative Node ID is passed as
     * parameters
     */
    @Test
    public void testRefreshNodeStatusForValidIPAddressAndNegativeNodeID()
    {
        logger.info( "[TS] : testRefreshNodeStatusForValidIPAddressAndNegativeNodeID" );
        ServerNode sNode = new ServerNode( "-1", "0.0.0.0", "ADMIN", "ADMIN" );
        // sNode.refreshNodePowerStatus();
        logger.info( "[TS] : Expected result : isPowered = false, actual result : isPowered = " + sNode.isPowered() );
        assertFalse( sNode.isPowered() );
    }

    /**
     * Test of refreshNodeStatus method, of class ServerNode, when a Invalid IP address is passed as a parameter
     */
    @Test
    public void testRefreshNodeStatusForInvalidIPAddress()
    {
        logger.info( "[TS] : testRefreshNodeStatusForInvalidIPAddress" );
        ServerNode sNode = new ServerNode( "2", "10.1.162.300", "ADMIN", "ADMIN" );
        // sNode.refreshNodePowerStatus();
        logger.info( "[TS] : Expected result : discoverable = false, actual result : discoverable = "
            + sNode.isDiscoverable() + " AND  Expected result : isPowered = false, actual result : isPowered = "
            + sNode.isPowered() );
        assertFalse( sNode.isDiscoverable() );
        assertFalse( sNode.isPowered() );
    }

    /**
     * Test of refreshNodeStatus method, of class ServerNode, when empty string is passed in place of IP address
     * parameter
     */
    @Test
    public void testRefreshNodeStatusForNoIPAddress()
    {
        logger.info( "[TS] : testRefreshNodeStatusForNoIPAddress" );
        ServerNode sNode = new ServerNode( "2", "", "ADMIN", "ADMIN" );
        // sNode.refreshNodePowerStatus();
        logger.info( "[TS] : Expected result : discoverable = false, actual result : discoverable = "
            + sNode.isDiscoverable() + " AND Expected result : isPowered = false, actual result : isPowered = "
            + sNode.isPowered() );
        assertFalse( sNode.isDiscoverable() );
        assertFalse( sNode.isPowered() );
    }

    /**
     * Test of refreshNodeStatus method, of class ServerNode, when empty string is passed in place of IP address
     * parameter
     */
    @Test( expected = NullPointerException.class )
    public void testRefreshNodeStatusWhenServerNodeIsNull()
    {
        logger.info( "[TS] : testRefreshNodeStatusWhenServerNodeIsNull" );
        ServerNode sNode = null;
        // sNode.refreshNodePowerStatus();
        logger.info( "[TS] : Expected result : discoverable = false, actual result : discoverable = "
            + sNode.isDiscoverable() + " AND Expected result : isPowered = false, actual result : isPowered = "
            + sNode.isPowered() );
        assertFalse( sNode.isDiscoverable() );
        assertFalse( sNode.isPowered() );
    }
    /*
     * Test caes commented out as Power Command code is deleted from ServerNode / // /** // * Test of getSensorData
     * method, of class ServerNode when the username OR password is invalid //
     */
    // @Test @Ignore
    // public void testGetSensorDataInvalidUserNameOrPassword() {
    // logger.info("[TS] : testGetSensorData");
    //
    // ServerNode sNode = new ServerNode("2","10.28.197.202","ADMIN","ADMIN");
    // sNode.setOsUserName("root");
    // sNode.setOsPassword("l@ni3r2o14");
    // List<Object> result = sNode.getSensorData();
    //
    // logger.info("[TS] : Expected result : returned Array List Must be empty , actual result : Returned Array List is
    // empty = " + result.isEmpty() );
    // assertTrue(result.isEmpty());
    //
    // }
    //
    //
    // /**
    // * Test of powerCycleNode method, of class ServerNode when Power Down command is sent to a Discoverable Server
    // Node.
    // */
    //
    // @Test
    // public void testPowerDownDicoverableServer(){
    // logger.info("[TS] : testPowerDownDicoverableServer");
    // ServerNode sNode = new ServerNode("2","10.28.197.202","ADMIN","ADMIN");
    // PowerCommand powerCmd = PowerCommand.PowerDown;
    // sNode.powerCycleNode(powerCmd);
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
    // }
    //
    // /**
    // * Test of powerCycleNode method, of class ServerNode when Power Up command is sent to a Discoverable Server Node.
    // */
    //
    // @Test
    // public void testPowerUpDicoverableServer(){
    // logger.info("[TS] : testPowerUpDicoverableServer");
    // ServerNode sNode = new ServerNode("2","10.28.197.202","ADMIN","ADMIN");
    // PowerCommand powerCmd = PowerCommand.PowerUp;
    // sNode.powerCycleNode(powerCmd);
    // try {
    // //May be Put some Thread.sleep
    // Thread.sleep(4000);
    // } catch (InterruptedException ex) {
    //
    // }
    //
    // //sNode.refreshNodePowerStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= true, actual result : Server node isPowered=
    // "+sNode.isPowered());
    // assertTrue(sNode.isPowered());
    // }
    //
    // /**
    // * Test of powerCycleNode method, of class ServerNode when Hard Reset command is sent to a Discoverable Server
    // Node.
    // */
    //
    // @Test //need to find out what what the expected result should be
    // public void testHardResetDicoverableServer(){
    // logger.info("[TS] : testPowerUpNonDicoverableServer");
    // ServerNode sNode = new ServerNode("2","10.28.197.202","ADMIN","ADMIN");
    // //sNode.refreshNodePowerStatus();
    // boolean sNodePowerStatus=sNode.isPowered();
    // PowerCommand powerCmd = PowerCommand.HardReset;
    // sNode.powerCycleNode(powerCmd);
    // try {
    // //May be Put some Thread.sleep
    // Thread.sleep(4000);
    // } catch (InterruptedException ex) {
    //
    // }
    // //sNode.refreshNodePowerStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= TRUE , actual result : Server node isPowered=
    // "+sNode.isPowered());
    // assertTrue(sNode.isPowered());
    // }
    //
    // /**
    // * Test of powerCycleNode method, of class ServerNode when Power Down command is sent to a Non Discoverable Server
    // Node.
    // */
    //
    // @Test
    // public void testPowerDownNonDicoverableServer(){
    // logger.info("[TS] : testPowerDownNonDicoverableServer");
    // ServerNode sNode = new ServerNode("2","10.28.197.101","ADMIN","ADMIN");
    // //sNode.refreshNodePowerStatus();
    // boolean sNodePowerStatus=sNode.isPowered();
    // PowerCommand powerCmd = PowerCommand.PowerDown;
    // sNode.powerCycleNode(powerCmd);
    //
    // logger.info("[TS] : Expected Result : Sever node isPowered= "+sNodePowerStatus+", actual result : Server node
    // isPowered= "+sNode.isPowered());
    // assertEquals(sNodePowerStatus,sNode.isPowered());
    // }
    //
    // /**
    // * Test of powerCycleNode method, of class ServerNode when Power Up command is sent to a Non Discoverable Server
    // Node.
    // */
    //
    // @Test
    // public void testPowerUpNonDicoverableServer(){
    // logger.info("[TS] : testPowerUpNonDicoverableServer");
    // ServerNode sNode = new ServerNode("2","10.28.197.101","ADMIN","ADMIN");
    // //sNode.refreshNodePowerStatus();
    // boolean sNodePowerStatus=sNode.isPowered();
    // PowerCommand powerCmd = PowerCommand.PowerUp;
    // sNode.powerCycleNode(powerCmd);
    //
    // //sNode.refreshNodePowerStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= "+sNodePowerStatus+", actual result : Server node
    // isPowered= "+sNode.isPowered());
    // assertEquals(sNodePowerStatus,sNode.isPowered());
    // }
    //
    // /**
    // * Test of powerCycleNode method, of class ServerNode when Hard Reset command is sent to a Non Discoverable Server
    // Node.
    // */
    //
    // @Test
    // public void testHardResetNonDicoverableServer(){
    // logger.info("[TS] : testPowerUpNonDicoverableServer");
    // ServerNode sNode = new ServerNode("2","10.28.197.101","ADMIN","ADMIN");
    // //sNode.refreshNodePowerStatus();
    // boolean sNodePowerStatus=sNode.isPowered();
    // PowerCommand powerCmd = PowerCommand.HardReset;
    // sNode.powerCycleNode(powerCmd);
    //
    // //sNode.refreshNodePowerStatus();
    // logger.info("[TS] : Expected Result : Sever node isPowered= "+sNodePowerStatus+", actual result : Server node
    // isPowered= "+sNode.isPowered());
    // assertEquals(sNodePowerStatus,sNode.isPowered());
    // }
    //
    //
    // @Test
    // public void testRefreshNodeSensorStatus() throws IOException, Exception{
    // ServerNode sNode = new ServerNode("2","10.28.197.202","ADMIN","ADMIN");
    // IpmiTaskConnector ipmiTaskConnector = new IpmiTaskConnector(sNode.getManagementIpAddress());
    // ipmiTaskConnector.createConnection();
    // List sensorData = sNode.refreshNodeSensorStatus(ipmiTaskConnector);
    // assertFalse(sensorData.isEmpty());
    // }
    // End - Commenting out test cases for methds whose signature has changed
}
