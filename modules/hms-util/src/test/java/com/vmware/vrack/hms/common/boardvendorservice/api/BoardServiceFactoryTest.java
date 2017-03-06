/* ********************************************************************************
 * BoardServiceFactoryTest.java
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