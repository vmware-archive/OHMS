/* ********************************************************************************
 * NodeConnector.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.node;

import org.apache.log4j.Logger;

public class NodeConnector
{
    private static Logger logger = Logger.getLogger( NodeConnector.class );

    public int UDP_PORT = 20;
    /*
     * public HmsNode refreshNodeStatus(HmsNode node) { IpmiConnector connector = null; ConnectionHandle handle = null;
     * try { connector= IpmiConnectionFactory.getIpmiConnector(UDP_PORT); handle
     * =IpmiConnectionFactory.getIpmiHandle(connector, node.getManagementIp(), "ADMIN", "ADMIN"); CipherSuite cs
     * =handle.getCipherSuite(); GetChassisStatusResponseData rd = (GetChassisStatusResponseData) connector
     * .sendMessage(handle, new GetChassisStatus(IpmiVersion.V20, cs, AuthenticationType.RMCPPlus));
     * node.setDiscoverable(true); node.setPowered(rd.isPowerOn()); // connector.closeSession(handle); } catch(Exception
     * e) { logger.error(e.getMessage()); node.setDiscoverable(false); node.setPowered(false); return node; } finally {
     * if(connector!=null && handle!=null) { // try { // IpmiConnectionFactory.closeHandle(connector, handle,
     * node.ipAddress, "ADMIN"); // } catch (Exception e) { // logger.error("message", e); // } // } } return node; }
     * public void resetServerPower(HmsNode node,PowerCommand powerCmd) throws Exception { IpmiConnector connector=
     * IpmiConnectionFactory.getIpmiConnector(UDP_PORT); ConnectionHandle handle
     * =IpmiConnectionFactory.getIpmiHandle(connector, node.getManagementIp(), "ADMIN", "ADMIN"); try { CipherSuite cs
     * =handle.getCipherSuite(); ChassisControl chassisControl = null; chassisControl = new
     * ChassisControl(IpmiVersion.V20, cs, AuthenticationType.RMCPPlus, powerCmd); ChassisControlResponseData data =
     * (ChassisControlResponseData) connector.sendMessage(handle, chassisControl); if(data!=null) Thread.sleep(3000); }
     * catch(Exception e) { logger.error(e.getMessage(), e); } finally { if(connector!=null && handle!=null) { // try {
     * // IpmiConnectionFactory.closeHandle(connector, handle, node.ipAddress, "ADMIN"); // } catch (Exception e) { //
     * logger.error ("message", e); // } // } } } public void destroy() { }
     */
}
