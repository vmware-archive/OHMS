/* ********************************************************************************
 * HostProxyProviderTest.java
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
package com.vmware.vrack.hms.common.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.vsphere.HostProxy;

@Ignore
public class HostProxyProviderTest
{
    private static Logger logger = Logger.getLogger( HostProxyProviderTest.class );

    private static final String HMS_NODE_ID = "hms.node.id";

    private static final String HMS_IB_IP_ADDRESS = "hms.ib.ip.address";

    private static final String HMS_IB_USERNAME = "hms.ib.username";

    private static final String HMS_IB_PASSWORD = "hms.ib.password";

    private static final String HMS_CHANGED_IB_IP_ADDRESS = "hms.ib.changed.ip.address";

    private static final String HMS_CHANGED_IB_USERNAME = "hms.ib.changed.os.username";

    private static final String HMS_CHANGED_IB_PASSWORD = "hms.ib.changed.os.password";

    private static final String HMS_DUMMY_IB_IP_ADDRESS = "hms.ib.dummy.ip.address";

    private static Properties properties;

    @Test
    public void initializeProperties()
    {
        properties = new Properties();
        try
        {
            properties.load( this.getClass().getResourceAsStream( "/test.properties" ) );
        }
        catch ( Exception e )
        {
            logger.error( "Unable to Load Properties file" );
        }
    }

    /**
     * First Time, no cached hostProxy will be found, and it should go create a new HostProxy
     * 
     * @throws HmsException
     */
    @Test
    public void getHostProxyTest()
        throws HmsException
    {
        ServiceServerNode node = new ServiceServerNode();
        node.setNodeID( properties.getProperty( HMS_NODE_ID ) );
        node.setIbIpAddress( properties.getProperty( HMS_IB_IP_ADDRESS ) );
        node.setOsUserName( properties.getProperty( HMS_IB_USERNAME ) );
        node.setOsPassword( properties.getProperty( HMS_IB_PASSWORD ) );
        HostProxy hostProxy = HostProxyProvider.getInstance().getHostProxy( node );
        assertTrue( hostProxy.getHostSystem() != null );
        assertTrue( hostProxy.getHostSystem().getHardware() != null );
        assertTrue( hostProxy.getHostSystem().getHardware().getCpuPkg().length > 0 );
    }

    /**
     * Simulating ip reconfiguration. In this case, it should cache the HostProxy for new Ip.
     * 
     * @throws HmsException
     */
    @Test
    public void getHostProxyTest_IpReconfig()
        throws HmsException
    {
        ServiceServerNode node = new ServiceServerNode();
        node.setNodeID( properties.getProperty( HMS_NODE_ID ) );
        node.setIbIpAddress( properties.getProperty( HMS_CHANGED_IB_IP_ADDRESS ) );
        node.setOsUserName( properties.getProperty( HMS_CHANGED_IB_USERNAME ) );
        node.setOsPassword( properties.getProperty( HMS_CHANGED_IB_PASSWORD ) );
        HostProxy hostProxy = HostProxyProvider.getInstance().getHostProxy( node );
        assertNotNull( hostProxy.getHostSystem() );
        assertNotNull( hostProxy.getHostSystem().getHardware() );
        assertTrue( hostProxy.getHostSystem().getHardware().getCpuPkg().length > 0 );
    }

    /**
     * Simulating Host Down or unreachable, Should throw HmsException
     * 
     * @throws HmsException
     */
    @Test( expected = HmsException.class )
    public void getHostProxyTest_InvalidHostProxy()
        throws HmsException
    {
        ServiceServerNode node = new ServiceServerNode();
        node.setNodeID( properties.getProperty( HMS_NODE_ID ) );
        node.setIbIpAddress( properties.getProperty( HMS_DUMMY_IB_IP_ADDRESS ) );
        node.setOsUserName( properties.getProperty( HMS_CHANGED_IB_USERNAME ) );
        node.setOsPassword( properties.getProperty( HMS_CHANGED_IB_PASSWORD ) );
        HostProxyProvider.getInstance().getHostProxy( node );
    }

    /**
     * Will recache HostProxy as it will be cleared by now
     * 
     * @throws HmsException
     */
    @Test
    public void getHostProxyTest_Recache()
        throws HmsException
    {
        ServiceServerNode node = new ServiceServerNode();
        node.setNodeID( properties.getProperty( HMS_NODE_ID ) );
        node.setIbIpAddress( properties.getProperty( HMS_IB_IP_ADDRESS ) );
        node.setOsUserName( properties.getProperty( HMS_IB_USERNAME ) );
        node.setOsPassword( properties.getProperty( HMS_IB_PASSWORD ) );
        HostProxy hostProxy = HostProxyProvider.getInstance().getHostProxy( node );
        assertNotNull( hostProxy.getHostSystem() );
        assertNotNull( hostProxy.getHostSystem().getHardware() );
        assertTrue( hostProxy.getHostSystem().getHardware().getCpuPkg().length > 0 );
    }
}
