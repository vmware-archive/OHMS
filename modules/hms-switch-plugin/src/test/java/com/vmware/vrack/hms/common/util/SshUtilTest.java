/* ********************************************************************************
 * SshUtilTest.java
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
package com.vmware.vrack.hms.common.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshUtilTest
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

    @Test( expected = JSchException.class )
    public void testAddHostKeyInKnownHostFile()
        throws JSchException
    {
        Properties sessionConfig = new Properties();
        sessionConfig.put( "StrictHostKeyChecking", "no" );
        Session session = SshUtil.getSessionObject( "username", "password", "localhost", 22, sessionConfig );
        final String userHomeDir = System.getProperty( "user.home" );
        final String knownHostsFile = String.format( "%s%s.ssh/known_hosts", userHomeDir, File.separator );
        boolean hostKeyAdded = SshUtil.addHostKeyInKnownHostFile( session, knownHostsFile );
        assertTrue( hostKeyAdded );
    }
}
