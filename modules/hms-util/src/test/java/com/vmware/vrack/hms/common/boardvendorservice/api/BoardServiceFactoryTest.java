/* ********************************************************************************
 * BoardServiceFactoryTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.api;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

public class BoardServiceFactoryTest
{
    private static Logger logger = Logger.getLogger( BoardServiceFactoryTest.class );

    @Test
    public void test()
    {
        logger.info( "Test hmsutil BoardServiceFactoryTest" );
        try
        {
            BoardServiceFactory boardServiceFactory = BoardServiceFactory.getBoardServiceFactory();
            boardServiceFactory.getBoardServiceImplementationClasses();
            BoardServiceFactory.initialize();
            assertNotNull( boardServiceFactory.getBoardServiceImplementationClasses() );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil BoardServiceFactoryTest Failed!" );
            e.printStackTrace();
        }
    }
}
