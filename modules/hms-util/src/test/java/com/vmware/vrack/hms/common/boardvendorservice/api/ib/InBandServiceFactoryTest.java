/* ********************************************************************************
 * InBandServiceFactoryTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.api.ib;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

public class InBandServiceFactoryTest
{
    private static Logger logger = Logger.getLogger( InBandServiceFactoryTest.class );

    @Test
    public void test()
    {
        logger.info( "Test hmsutil InBandServiceFactoryTest" );
        try
        {
            InBandServiceFactory inBandServiceFactory = InBandServiceFactory.getBoardServiceFactory();
            InBandServiceFactory.initialize();
            assertNotNull( inBandServiceFactory.getBoardServiceImplementationClasses() );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil InBandServiceFactoryTest Failed!" );
            e.printStackTrace();
        }
    }
}
