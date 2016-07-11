/* ********************************************************************************
 * FruIdGeneratorUtilTest.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;

/**
 * Unit test for the FRU Id generation
 * 
 * @author Vmware
 */
public class FruIdGeneratorUtilTest
{
    private static Logger logger = Logger.getLogger( FruIdGeneratorUtilTest.class );

    @Test
    public void test()
    {
        logger.info( "Testing hmsutil FruIdGeneratorUtilTest" );
        FruIdGeneratorUtil fruIdGeneratorUtil = new FruIdGeneratorUtil();
        ComponentIdentifier componentIdentifier = new ComponentIdentifier();
        try
        {
            componentIdentifier.setDescription( "DellNode-R630" );
            componentIdentifier.setManufacturer( "DELL" );
            componentIdentifier.setManufacturingDate( "Mon May 12 09:27:00 2014" );
            componentIdentifier.setProduct( "PowerEdge R630" );
            componentIdentifier.setPartNumber( "0CNCJWX36" );
            componentIdentifier.setSerialNumber( "CN747514550128" );
            String location = "2U";
            long fruID = fruIdGeneratorUtil.generateFruIdHashCode( componentIdentifier, location );
            assertNotNull( fruID );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil FruIdGeneratorUtilTest Failed" );
            e.printStackTrace();
        }
    }
}
