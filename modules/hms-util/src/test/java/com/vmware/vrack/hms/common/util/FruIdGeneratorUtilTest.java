/* ********************************************************************************
 * FruIdGeneratorUtilTest.java
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
    public void serverFruIDGeneration()
    {

        logger.info( "Testing hmsutil FruIdGeneratorUtilTest" );

        ComponentIdentifier componentIdentifier = new ComponentIdentifier();

        try
        {
            componentIdentifier.setManufacturer( "DELL" );
            componentIdentifier.setProduct( "PowerEdge R630" );
            componentIdentifier.setPartNumber( "0CNCJWX36" );
            componentIdentifier.setSerialNumber( "CN747514550128" );
            String location = "2U";

            String fruID = FruIdGeneratorUtil.generateFruIdHashCode( componentIdentifier, location );
            assertNotNull( fruID );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil FruIdGeneratorUtilTest Failed" );
            e.printStackTrace();
        }
    }

    @Test
    public void memoryFruIDGeneration()
    {
        logger.info( "Testing hmsutil FruIdGeneratorUtilTest: Memory Fru ID generation" );
        ComponentIdentifier componentIdentifier = new ComponentIdentifier();

        try
        {
            componentIdentifier.setManufacturer( "Samsung" );
            componentIdentifier.setPartNumber( "M393B2G70QH0-YK0" );
            componentIdentifier.setSerialNumber( "1222EB94" );
            String location = "ChannelA_Dimm0";

            String fruID = FruIdGeneratorUtil.generateFruIdHashCode( componentIdentifier, location );
            assertNotNull( fruID );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil FruIdGeneratorUtilTest Failed" );
            e.printStackTrace();
        }
    }

    @Test
    public void memoryFruIDGeneration2()
    {
        logger.info( "Testing hmsutil FruIdGeneratorUtilTest: Memory Fru ID generation" );
        ComponentIdentifier componentIdentifier = new ComponentIdentifier();

        try
        {
            componentIdentifier.setManufacturer( "Samsung" );
            componentIdentifier.setPartNumber( "M393B2G70QH0-YK0" );
            componentIdentifier.setSerialNumber( "1222EB93" );
            String location = "ChannelA_Dimm1";

            String fruID = FruIdGeneratorUtil.generateFruIdHashCode( componentIdentifier, location );
            assertNotNull( fruID );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil FruIdGeneratorUtilTest Failed" );
            e.printStackTrace();
        }
    }

    @Test
    public void memoryFruIDGeneration3()
    {
        logger.info( "Testing hmsutil FruIdGeneratorUtilTest: Memory Fru ID generation" );
        ComponentIdentifier componentIdentifier = new ComponentIdentifier();

        try
        {
            componentIdentifier.setManufacturer( "Samsung" );
            componentIdentifier.setPartNumber( "M393B2G70QH0-YK0" );
            componentIdentifier.setSerialNumber( "1222EAE6" );
            String location = "ChannelE_Dimm0";

            String fruID = FruIdGeneratorUtil.generateFruIdHashCode( componentIdentifier, location );
            assertNotNull( fruID );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil FruIdGeneratorUtilTest Failed" );
            e.printStackTrace();
        }
    }

    @Test
    public void memoryFruIDGeneration4()
    {
        logger.info( "Testing hmsutil FruIdGeneratorUtilTest: Memory Fru ID generation" );
        ComponentIdentifier componentIdentifier = new ComponentIdentifier();

        try
        {
            componentIdentifier.setManufacturer( "Samsung" );
            componentIdentifier.setPartNumber( "M393B2G70QH0-YK0" );
            componentIdentifier.setSerialNumber( "1222EAE5" );
            String location = "ChannelE_Dimm1";

            String fruID = FruIdGeneratorUtil.generateFruIdHashCode( componentIdentifier, location );
            assertNotNull( fruID );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil FruIdGeneratorUtilTest Failed" );
            e.printStackTrace();
        }
    }

    @Test
    public void memoryFruIDGenerationEmptySlot()
    {
        logger.info( "Testing hmsutil FruIdGeneratorUtilTest: Memory Fru ID generation" );
        ComponentIdentifier componentIdentifier = new ComponentIdentifier();

        try
        {
            String location = "ChannelA_Dimm2";
            String fruIDMemory = FruIdGeneratorUtil.generateFruIdHashCode( componentIdentifier, location );
            String fruID = FruIdGeneratorUtil.generateFruIdHashCodeServerComponent( fruIDMemory, "1996455226" );

            assertNotNull( fruIDMemory );
            assertNotNull( fruID );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil FruIdGeneratorUtilTest Failed" );
            e.printStackTrace();
        }
    }

    @Test
    public void cpuFruIDGeneration()
    {
        logger.info( "Testing hmsutil FruIdGeneratorUtilTest: CPU Fru ID generation" );
        ComponentIdentifier componentIdentifier = new ComponentIdentifier();

        try
        {
            componentIdentifier.setManufacturer( "intel" );
            componentIdentifier.setProduct( "Intel(R) Xeon(R) CPU E5-2650 v2 @ 2.60GHz" );
            String location = "0";

            String fruIDCpu = FruIdGeneratorUtil.generateFruIdHashCode( componentIdentifier, location );
            String fruID = FruIdGeneratorUtil.generateFruIdHashCodeServerComponent( fruIDCpu, "1996455226" );

            assertNotNull( fruIDCpu );
            assertNotNull( fruID );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil FruIdGeneratorUtilTest Failed" );
            e.printStackTrace();
        }
    }

    @Test
    public void cpuFruIDGeneration2()
    {
        logger.info( "Testing hmsutil FruIdGeneratorUtilTest: CPU Fru ID generation" );
        ComponentIdentifier componentIdentifier = new ComponentIdentifier();

        try
        {
            componentIdentifier.setManufacturer( "intel" );
            componentIdentifier.setProduct( "Intel(R) Xeon(R) CPU E5-2650 v2 @ 2.60GHz" );
            String location = "1";

            String fruIDCpu = FruIdGeneratorUtil.generateFruIdHashCode( componentIdentifier, location );
            String fruID = FruIdGeneratorUtil.generateFruIdHashCodeServerComponent( fruIDCpu, "1996455226" );

            assertNotNull( fruIDCpu );
            assertNotNull( fruID );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil FruIdGeneratorUtilTest Failed" );
            e.printStackTrace();
        }
    }

}