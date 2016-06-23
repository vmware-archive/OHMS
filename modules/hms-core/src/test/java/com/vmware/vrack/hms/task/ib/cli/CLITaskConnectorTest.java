/* ********************************************************************************
 * CLITaskConnectorTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ib.cli;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author tanvishah
 */
public class CLITaskConnectorTest
{
    private static Logger logger = Logger.getLogger( CLITaskConnectorTest.class );

    public CLITaskConnectorTest()
    {
    }

    /**
     * Test of getSession method, of class CLITaskConnector.
     */
    @Test
    @Ignore
    public void testGetSession()
        throws Exception
    {
        logger.info( "[TS] : testGetSession" );
        String username = "root";
        String password = "root123";
        String hostname = "10.28.197.24";
        int port = 22;
        CLITaskConnector instance = new CLITaskConnector( username, password, hostname, port );
        instance.createConnection();
        logger.info( "[TS] : Expected Result: Session = NOT NULL, Actual Result: Session=" + instance.getSession() );
        assertNotNull( instance.getSession() );
    }

    /**
     * Test of createConnection method, of class CLITaskConnector.
     */
    @Test
    @Ignore
    public void testCreateConnection()
        throws Exception
    {
        logger.info( "[TS] : testCreateConnection" );
        String username = "root";
        String password = "root123";
        String hostname = "10.28.197.24";
        int port = 22;
        CLITaskConnector instance = new CLITaskConnector( username, password, hostname, port );
        instance.createConnection();
        logger.info( "[TS] : Expected Result: Session = NOT NULL, Actual Result: Session=" + instance.getSession() );
        assertNotNull( instance.getSession() );
    }

    /**
     * Test of destroy method, of class CLITaskConnector.
     */
    @Test
    @Ignore
    public void testDestroy()
        throws Exception
    {
        logger.info( "[TS] : testCreateConnection" );
        String username = "root";
        String password = "root123";
        String hostname = "10.28.197.24";
        int port = 22;
        CLITaskConnector instance = new CLITaskConnector( username, password, hostname, port );
        instance.createConnection();
        instance.destroy();
        logger.info( "[TS] : Expected Result: Session = NULL, Actual Result: Session=" + instance.getSession() );
        assertNull( instance.getSession() );
    }
}
