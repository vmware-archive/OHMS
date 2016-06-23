/* ********************************************************************************
 * SwitchNodeConnectorTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.node.switches;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.switches.GetSwitchesResponse;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortStatus;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tanvishah
 */
public class SwitchNodeConnectorTest
{
    private static Logger logger = Logger.getLogger( SwitchNodeConnectorTest.class );

    public SwitchNodeConnectorTest()
    {
    }

    /**
     * Test of getSwitchNodes method, of class SwitchNodeConnector.
     */
    @Test
    public void testGetSwitchNodes()
    {
        logger.info( "[TS] : testGetSwitchNodes" );
        GetSwitchesResponse result = SwitchNodeConnector.getInstance().getSwitchNodes();
        logger.info( "[TS] : Expected result : Result(String Array) length = 1 or more than 1, actual result : Result(String Array) length = "
            + result.getSwitchList().size() );
        assertNotNull( result.getSwitchList() );
    }

    /**
     * Test of getSwitchNode method, of class SwitchNodeConnector for a valid switch ID.
     */
    @Ignore
    @Test
    public void testGetSwitchNodeForValidSwitchId()
        throws Exception
    {
        logger.info( "[TS] : testGetSwitchNodeForValidSwitchId" );
        String switchId = "S0";
        Object result = SwitchNodeConnector.getInstance().getSwitchNode( switchId );
        logger.info( "[TS] : Expected result : Result Switch Node= om.vmware.vrack.hms.node.switches.SwitchNode@xxx..., actual result : Result Switch Node= "
            + result );
        assertNotNull( result );
    }

    /**
     * Test of getSwitchNode method, of class SwitchNodeConnector for a invalid switch ID.
     */
    @Ignore
    @Test( expected = HmsException.class )
    public void testGetSwitchNodeForInvalidSwitchId()
        throws Exception
    {
        logger.info( "[TS] : testGetSwitchNodeForInvalidSwitchId" );
        String switchId = "S99";
        logger.info( "[TS] : Expected result : HmsException" );
        Object result = SwitchNodeConnector.getInstance().getSwitchNode( switchId );
        assertNull( result );
    }

    /**
     * Test of getSwitchPorts method, of class SwitchNodeConnector for a valid switch ID.
     */
    @Ignore
    @Test
    public void testGetSwitchPortsForValidSwitchId()
        throws Exception
    {
        logger.info( "[TS] : testGetSwitchPortsForValidSwitchId" );
        String switchId = "S0";
        List<String> result = SwitchNodeConnector.getInstance().getSwitchPorts( switchId );
        logger.info( "[TS] : Expected result : Result List Is empty = false, actual result : Result List Is empty ="
            + result.isEmpty() );
        assertFalse( result.isEmpty() );
    }

    /**
     * Test of getSwitchPorts method, of class SwitchNodeConnector for a invalid switch ID.
     */
    @Ignore
    @Test( expected = ArrayIndexOutOfBoundsException.class )
    public void testGetSwitchPortsForInvalidSwitchId()
        throws Exception
    {
        logger.info( "[TS] : testGetSwitchPortsForInvalidSwitchId" );
        String switchId = "S99";
        logger.info( "[TS] : Expected result : ArrayIndexOutOfBoundsException" );
        List<String> result = SwitchNodeConnector.getInstance().getSwitchPorts( switchId );
        logger.info( "[TS] : Expected result : Result List Is empty = true, actual result : Result List Is empty ="
            + result.isEmpty() );
        assertTrue( result.isEmpty() );
    }

    /**
     * Test of getSwitchPort method, of class SwitchNodeConnector for valid switch Id
     */
    @Ignore
    @Test
    public void testGetSwitchPortForValidSwitchIdAndKnownPortName()
        throws Exception
    {
        logger.info( "[TS] : testGetSwitchPortForValidSwitchIdAndKnownPortName" );
        String switchId = "S0";
        String portName = "swp1";
        SwitchPort unExpResult = new SwitchPort();
        SwitchPort result = SwitchNodeConnector.getInstance().getSwitchPort( switchId, portName );
        logger.info( "[TS] : Expected result : swp1, actual result : Result Name =" + result.getName() );
        logger.info( "[TS] : Expected result : 70:72:cf:9d:5b:19, actual result : Result Mac Address="
            + result.getMacAddress() );
        assertNotSame( unExpResult, result );
        assertEquals( "swp1", result.getName() );
        assertEquals( "70:72:cf:9d:5b:19", result.getMacAddress() );
    }

    /**
     * Test of getSwitchPort method, of class SwitchNodeConnector for valid switch Id and unknown port name
     */
    @SuppressWarnings( "unused" )
    @Test( expected = HmsException.class )
    public void testGetSwitchPortForValidSwitchIdAndUnknownPortName()
        throws Exception
    {
        logger.info( "[TS] : testGetSwitchPortForValidSwitchIdAndUnknownPortName" );
        String switchId = "S0";
        String portName = "random";
        SwitchPort unExpResult = new SwitchPort();
        logger.info( "[TS] : Expected result : HmsException" );
        SwitchPort result = SwitchNodeConnector.getInstance().getSwitchPort( switchId, portName );
    }

    /**
     * Test of getSwitchPort method, of class SwitchNodeConnector for invalid switch Id
     */
    @Ignore
    @Test( expected = ArrayIndexOutOfBoundsException.class )
    public void testGetSwitchPortForInvalidSwitchId()
        throws Exception
    {
        logger.info( "[TS] : testGetSwitchPortForInvalidSwitchId" );
        String switchId = "S99";
        String portName = "random";
        SwitchPort expResult = new SwitchPort();
        logger.info( "[TS] : Expected result : ArrayIndexOutOfBoundsException" );
        SwitchPort result = SwitchNodeConnector.getInstance().getSwitchPort( switchId, portName );
        assertEquals( expResult, result );
    }

    /**
     * Test of changeSwitchPortStatus method, of class SwitchNodeConnector for valid Switch Id and Port Name
     */
    @Ignore
    @Test
    public void testChangeSwitchPortStatusToPowerDownForValidSwitchIdAndPortName()
        throws Exception
    {
        logger.info( "[TS] : testChangeSwitchPortStatusToUpForValidSwitchIdAndPortName" );
        String switchId = "S0";
        String portName = "swp1";
        PortStatus portStatus = SwitchPort.PortStatus.DOWN;
        SwitchNodeConnector.getInstance().changeSwitchPortStatus( switchId, portName, portStatus );
    }

    /**
     * Test of changeSwitchPortStatus method, of class SwitchNodeConnector for invalid Switch Id
     */
    @Ignore
    @Test( expected = ArrayIndexOutOfBoundsException.class )
    public void testChangeSwitchPortStatusToPowerDownForInvalidSwitchId()
        throws Exception
    {
        logger.info( "[TS] : testChangeSwitchPortStatusToUpForValidSwitchIdAndPortName" );
        String switchId = "S99";
        String portName = "swp1";
        PortStatus portStatus = SwitchPort.PortStatus.DOWN;
        logger.info( "[TS] : Expected result : ArrayIndexOutOfBoundsException" );
        SwitchNodeConnector.getInstance().changeSwitchPortStatus( switchId, portName, portStatus );
    }
    // /**
    // * Test of changeSwitchPortStatus method, of class SwitchNodeConnector for valid Switch Id and Port Name
    // */
    // @Test
    // public void testChangeSwitchPortStatusToPowerDownForValidSwitchIdAndPortName() throws Exception {
    // logger.info("[TS] : testChangeSwitchPortStatusToUpForValidSwitchIdAndPortName");
    // int switchId = 1;
    // String portName = "swp1";
    // PortStatus portStatus = TorSwitchPort.PortStatus.DOWN;
    // String result = SwitchNodeConnector.getInstance().changeSwitchPortStatus(switchId, portName, portStatus);
    // logger.info("[TS] : Expected result : swp1, actual result : Result Name =" + result);
    // assertEquals("swp1", result);
    // }

    /**
     * Test of RebootSwitch method, of class SwitchNodeConnector for valid Switch ID
     */
    @Ignore
    @Test
    public void testRebootSwtich()
        throws Exception
    {
        logger.info( "[TS] : testRebootSwtich" );
        String switchId = "S0";
        SwitchNodeConnector.getInstance().rebootSwitch( switchId );
    }

    /**
     * Test of RebootSwitch method, of class SwitchNodeConnector for invalid Switch ID
     */
    @Ignore
    @Test
    public void testRebootSwtichForInvalidSwitchId()
        throws Exception
    {
        logger.info( "[TS] : testRebootSwtich" );
        String switchId = "S99";
        SwitchNodeConnector.getInstance().rebootSwitch( switchId );
    }
}
