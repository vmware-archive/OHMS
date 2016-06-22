/* ********************************************************************************
 * ServerNodeBoardVendorTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ServerNodeBoardVendorTest
{
    private static Logger logger = Logger.getLogger( ServerNodeBoardVendorTest.class );

    @Test
    public void test()
    {
        logger.info( "Testing hmsutil ServerNodeBoardVendorTest" );
        try
        {
            assertNotNull( ServerNodeBoardVendor.getServerNodeBoardVendor( ServerNodeBoardVendor.QUANTA.toString() ) );
            assertNotNull( ServerNodeBoardVendor.getServerNodeBoardVendor( ServerNodeBoardVendor.INTEL.toString() ) );
            assertNotNull( ServerNodeBoardVendor.getServerNodeBoardVendor( "Dell" ) );
            assertNotNull( ServerNodeBoardVendor.getServerNodeBoardVendor( ServerNodeBoardVendor.SUPERMICRO.toString() ) );
            assertNotNull( ServerNodeBoardVendor.getServerNodeBoardVendor( ServerNodeBoardVendor.OTHERS.toString() ) );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil ServerNodeBoardVendorTest Failed" );
            e.printStackTrace();
        }
    }
}
