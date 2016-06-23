/* ********************************************************************************
 * HmsInventoryConfigurationTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.configuration;

import static org.junit.Assert.*;

import java.io.File;
import org.apache.log4j.Logger;
import org.junit.Test;

public class HmsInventoryConfigurationTest
{
    private static Logger logger = Logger.getLogger( HmsInventoryConfigurationTest.class );

    @Test
    public void test()
    {
        logger.info( "Testing HmsInventoryConfigurationTest" );
        try
        {
            File file = null;
            file = new File( "test-inventory.json" );
            file.createNewFile();
            HmsInventoryConfiguration hmsInventoryConfiguration = new HmsInventoryConfiguration();
            hmsInventoryConfiguration.setFilename( "test-inventory.json" );
            assertNotNull( hmsInventoryConfiguration.getFilename() );
            hmsInventoryConfiguration.store( hmsInventoryConfiguration.getFilename() );
            file.delete();
        }
        catch ( Exception e )
        {
            logger.info( "Test HmsInventoryConfigurationTest Failed!" );
            e.printStackTrace();
        }
    }
}
