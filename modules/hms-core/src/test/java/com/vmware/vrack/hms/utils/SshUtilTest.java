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
package com.vmware.vrack.hms.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author tanvishah
 */
@Ignore
public class SshUtilTest
{
    private static Logger logger = Logger.getLogger( SshUtilTest.class );

    public SshUtilTest()
    {
    }

    /**
     * Test of getSessionObject method, of class SshUtil when parameters passed are non null AND port range is between 0
     * and 65535
     */
    @Test
    public void testGetSessionObjectWithAcceptableParameters()
    {
        logger.info( "getSessionObject" );
        String username = "ADMIN";
        String password = "ADMIN";
        String hostname = "10.28.197.23";
        int port = 0;
        Properties sessionConfig = new java.util.Properties();
        sessionConfig.put( "StrictHostKeyChecking", "no" );
        Session expResult = null;
        Session result = null;
        result = SshUtil.getSessionObject( username, password, hostname, port, sessionConfig );
        boolean isResultNotNull = false;
        if ( result == null )
        {
            isResultNotNull = false;
        }
        else
        {
            isResultNotNull = true;
        }
        logger.info( "[TS] : Expected result : Session Object should be NOT NULL , actual result : Session Object is NOT NULL ="
            + isResultNotNull );
        assertNotNull( result );
    }

    /**
     * Test of getSessionObject method, of class SshUtil when port no doesn't belong to the range 0 - 65535
     */
    @Test( expected = JSchException.class )
    public void testGetSessionObjectWithInvalidPortNumber()
        throws JSchException
    {
        logger.info( "getSessionObject" );
        String username = "ADMIN";
        String password = "ADMIN";
        String hostname = "45.89.97.09";
        int port = 66636;
        Properties sessionConfig = new java.util.Properties();
        sessionConfig.put( "StrictHostKeyChecking", "no" );
        Session result = null;
        result = SshUtil.getSessionObject( username, password, hostname, port, sessionConfig );
        boolean isResultNull = false;
        if ( result == null )
        {
            isResultNull = true;
        }
        else
        {
            isResultNull = false;
        }
        logger.info( "[TS] : Expected result : Will throw an error when we will try to connect" );
        result.connect( 1000 );
    }

    /**
     * Test of getSessionObject method, of class SshUtil when one of the parameters is NULL
     */
    @Test( expected = Exception.class )
    public void testGetSessionObjectWithNullUsername()
        throws JSchException
    {
        logger.info( "[TS] : testGetSessionObjectWithNullSessionObj" );
        String username = null;
        String password = "ADMIN";
        String hostname = "10.28.197.23";
        int port = 65534;
        Properties sessionConfig = new java.util.Properties();
        sessionConfig.put( "StrictHostKeyChecking", "no" );
        Session result = null;
        result = SshUtil.getSessionObject( username, password, hostname, port, sessionConfig );
        result.connect( 1000 );
        logger.info( "[TS] : Expected result : Will throw an error when we will try to connect" );
        result.connect( 1000 );
    }

    /**
     * Test of executeCommand method, of class SshUtil.
     */
    @Test
    public void testExecuteCommand()
        throws JSchException
    {
        logger.info( "[TS] : testExecuteCommand" );
        String username = "root";
        String password = "root123";
        String hostname = "10.28.197.23";
        int port = 22;
        Properties sessionConfig = new java.util.Properties();
        sessionConfig.put( "StrictHostKeyChecking", "no" );
        Session sessionobj = null;
        sessionobj = SshUtil.getSessionObject( username, password, hostname, port, sessionConfig );
        sessionobj.connect( 6000 );
        String command = "vmware -v";
        String expResult = "";
        String result = null;
        try
        {
            result = SshUtil.executeCommand( sessionobj, command );
        }
        catch ( Exception ex )
        {
            logger.error( "Exception occured while executing command.", ex );
        }
        logger.info( "[TS] : Expected result : Result of the command is NOT NULL, actual result : Result of the command = "
            + result );
        assertNotNull( result );
    }

    /**
     * Test of executeCommand method, of class SshUtil when an un recognized cmd is sent as parameter
     */
    @Test
    public void testExecuteCommandWithIncorrectCmd()
        throws JSchException
    {
        logger.info( "[TS] :  testExecuteCommandWithIncorrectCmd" );
        String username = "root";
        String password = "root123";
        String hostname = "10.28.197.23";
        int port = 22;
        Properties sessionConfig = new java.util.Properties();
        sessionConfig.put( "StrictHostKeyChecking", "no" );
        Session sessionobj = null;
        sessionobj = SshUtil.getSessionObject( username, password, hostname, port, sessionConfig );
        sessionobj.connect( 6000 );
        String command = "random cmd";
        String expResult = "";
        String result = null;
        try
        {
            result = SshUtil.executeCommand( sessionobj, command );
        }
        catch ( JSchException ex )
        {
        }
        catch ( IOException ex )
        {
        }
        logger.info( "[TS] : Expected result : Result of the command is NOT NULL, actual result : Result of the command = "
            + result );
        assertNotNull( result );
    }

    /**
     * Test of executeCommand method, of class SshUtil when session object is null.
     */
    @Test
    public void testExecuteCommandWithNullSessionObj()
    {
        logger.info( "[TS] :  testExecuteCommandWithNullSessionObj" );
        Session sessionobj = null;
        String command = "random cmd";
        String expResult = "";
        String result = null;
        try
        {
            result = SshUtil.executeCommand( sessionobj, command );
        }
        catch ( JSchException ex )
        {
        }
        catch ( IOException ex )
        {
        }
        logger.info( "[TS] : Expected result : Result of the command is NULL, actual result : Result of the command = "
            + result );
        assertNull( result );
    }

    /**
     * Test of executeCommand method, of class SshUtil when the cmd sent is NULL.
     */
    @Test
    public void testExecuteCommandWithNullCmd()
        throws JSchException
    {
        logger.info( "[TS] : testExecuteCommandWithNullCmd" );
        String username = "root";
        String password = "root123";
        String hostname = "10.28.197.23";
        int port = 22;
        Properties sessionConfig = new java.util.Properties();
        sessionConfig.put( "StrictHostKeyChecking", "no" );
        Session sessionobj = null;
        sessionobj = SshUtil.getSessionObject( username, password, hostname, port, sessionConfig );
        sessionobj.connect( 6000 );
        String command = null;
        String result = null;
        try
        {
            result = SshUtil.executeCommand( sessionobj, command );
        }
        catch ( JSchException ex )
        {
        }
        catch ( IOException ex )
        {
        }
        logger.info( "[TS] : Expected result : Result of the command is NULL, actual result : Result of the command = "
            + result );
        assertNull( result );
    }
}
