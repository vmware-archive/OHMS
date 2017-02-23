/* ********************************************************************************
 * DhcpLeaseParserTest.java
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
package com.vmware.vrack.hms.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vmware.vrack.hms.common.rest.model.DhcpLease;

public class DhcpLeaseParserTest
{

    @BeforeClass
    public static void setUpBeforeClass()
        throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass()
        throws Exception
    {
    }

    @Before
    public void setUp()
        throws Exception
    {
    }

    @After
    public void tearDown()
        throws Exception
    {
    }

    @Test
    public void testParseLeaseContent()
    {

        final String leaseContent = "lease 192.168.0.50 {\n" + "        starts 4 2011/09/22 20:27:28;\n"
            + "        ends 1 2011/09/26 20:27:28;\n" + "        tstp 1 2011/09/26 20:27:28;\n"
            + "        binding state free;\n" + "        hardware ethernet 00:00:00:00:00:00;\n"
            + "        uid \"\\001\\000\\033w\\223\\241i\";\n client-hostname \"QCT2C600CFC761D\";\n}";
        DhcpLease dhcpLease = DhcpLeaseParser.parseLeaseContent( leaseContent );
        assertNotNull( dhcpLease );
        assertNotNull( dhcpLease.getIpAddress() );
        assertTrue( StringUtils.equals( dhcpLease.getIpAddress(), "192.168.0.50" ) );

        assertNotNull( dhcpLease.getMacAddress() );
        assertTrue( StringUtils.equals( dhcpLease.getMacAddress(), "00:00:00:00:00:00" ) );
    }

    @Test
    public void testParseLeaseFile()
    {
        final String dhcpLeaseFile = "src/test/resources/test-dhcpd.lease";
        List<DhcpLease> dhcpLeases = DhcpLeaseParser.parseLeaseFile( dhcpLeaseFile );
        assertNotNull( dhcpLeases );
        assertTrue( dhcpLeases.size() > 0 );
        assertTrue( dhcpLeases.size() == 8 );
        DhcpLease dhcpLease = dhcpLeases.get( 0 );
        assertNotNull( dhcpLease );
        assertTrue( StringUtils.equals( dhcpLease.getStarts(), "2016/06/19 09:55:11" ) );
        assertTrue( StringUtils.equals( dhcpLease.getEnds(), "2016/06/20 09:55:11" ) );
    }
}
