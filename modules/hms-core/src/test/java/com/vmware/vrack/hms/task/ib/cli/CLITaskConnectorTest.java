/* ********************************************************************************
 * CLITaskConnectorTest.java
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
