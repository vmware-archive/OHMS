/* ********************************************************************************
 * HddInfoHelperTest.java
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
package com.vmware.vrack.hms.boardservice.ib.api;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.vmware.vrack.hms.common.servernodes.api.hdd.HddSMARTData;

public class HddInfoHelperTest
{

    private static Logger logger = Logger.getLogger( HddInfoHelperTest.class );

    /**
     * Test SMART data Value is "10" and Threshold being "11" isDataConcerning is TRUE.
     */
    @Test
    public void testSmartDataThreshold()
    {
        logger.info( "Testing hms-inband HddInfoHelperTest::isSmartDataBeyondThreshold" );

        try
        {
            HddSMARTData hddSMARTData = new HddSMARTData();
            hddSMARTData.setParameter( "Raw Read Error Rate" );
            hddSMARTData.setValue( "10" );
            hddSMARTData.setThreshold( "11" );
            hddSMARTData.setWorst( "0" );

            boolean isDataConcerning = HddInfoHelper.isSmartDataBeyondThreshold( hddSMARTData, false );
            // System.out.println("isDataConcerning: " + isDataConcerning);
            assertTrue( isDataConcerning );
        }
        catch ( Exception e )
        {
            logger.info( "Test hms-inband HddInfoHelperTest::isSmartDataBeyondThreshold Failed" );
            e.printStackTrace();
        }
    }

    /**
     * Test SMART data Value and Threshold being "0" isDataConcerning is false.
     */
    @Test
    public void testSmartDataThreshold1()
    {
        logger.info( "Testing hms-inband HddInfoHelperTest::isSmartDataBeyondThreshold1" );

        try
        {
            HddSMARTData hddSMARTData = new HddSMARTData();
            hddSMARTData.setParameter( "Raw Read Error Rate" );
            hddSMARTData.setValue( "0" );
            hddSMARTData.setThreshold( "0" );
            hddSMARTData.setWorst( "0" );

            boolean isDataConcerning = HddInfoHelper.isSmartDataBeyondThreshold( hddSMARTData, false );
            // System.out.println("isDataConcerning1: " + isDataConcerning);
            assertFalse( isDataConcerning );
        }
        catch ( Exception e )
        {
            logger.info( "Test hms-inband HddInfoHelperTest::isSmartDataBeyondThreshold1 Failed" );
            e.printStackTrace();
        }
    }

    /**
     * Test SMART data Value is "10" and Threshold being "0" isDataConcerning is false.
     */
    @Test
    public void testSmartDataThreshold2()
    {
        logger.info( "Testing hms-inband HddInfoHelperTest::isSmartDataBeyondThreshold2" );

        try
        {
            HddSMARTData hddSMARTData = new HddSMARTData();

            hddSMARTData.setParameter( "Raw Read Error Rate" );
            hddSMARTData.setValue( "10" );
            hddSMARTData.setThreshold( "0" );
            hddSMARTData.setWorst( "0" );

            boolean isDataConcerning = HddInfoHelper.isSmartDataBeyondThreshold( hddSMARTData, false );
            // System.out.println("isDataConcerning2: " + isDataConcerning);
            assertFalse( isDataConcerning );
        }
        catch ( Exception e )
        {
            logger.info( "Test hms-inband HddInfoHelperTest::isSmartDataBeyondThreshold2 Failed" );
            e.printStackTrace();
        }
    }

    /**
     * Test SMART data Value is "10" and Threshold being "N/A" isDataConcerning is false.
     */
    @Test
    public void testSmartDataThreshold3()
    {
        logger.info( "Testing hms-inband HddInfoHelperTest::isSmartDataBeyondThreshold3" );

        try
        {
            HddSMARTData hddSMARTData = new HddSMARTData();
            hddSMARTData.setParameter( "Raw Read Error Rate" );
            hddSMARTData.setValue( "10" );
            hddSMARTData.setThreshold( "N/A" );
            hddSMARTData.setWorst( "0" );

            boolean isDataConcerning = HddInfoHelper.isSmartDataBeyondThreshold( hddSMARTData, false );
            // System.out.println("isDataConcerning3: " + isDataConcerning);
            assertFalse( isDataConcerning );
        }
        catch ( Exception e )
        {
            logger.info( "Test hms-inband HddInfoHelperTest::isSmartDataBeyondThreshold3 Failed" );
            e.printStackTrace();
        }
    }

    /**
     * Test SMART data Value and Threshold being "N/A" isDataConcerning is false.
     */
    @Test
    public void testSmartDataThreshold4()
    {
        logger.info( "Testing hms-inband HddInfoHelperTest::isSmartDataBeyondThreshold4" );

        try
        {
            HddSMARTData hddSMARTData = new HddSMARTData();
            hddSMARTData.setParameter( "Raw Read Error Rate" );
            hddSMARTData.setValue( "N/A" );
            hddSMARTData.setThreshold( "N/A" );
            hddSMARTData.setWorst( "0" );

            boolean isDataConcerning = HddInfoHelper.isSmartDataBeyondThreshold( hddSMARTData, false );
            // System.out.println("isDataConcerning4: " + isDataConcerning);
            assertFalse( isDataConcerning );
        }
        catch ( Exception e )
        {
            logger.info( "Test hms-inband HddInfoHelperTest::isSmartDataBeyondThreshold4 Failed" );
            e.printStackTrace();
        }
    }

    /**
     * Test SMART data Value is "20" and Threshold being "20" isDataConcerning is TRUE.
     */
    @Test
    public void testSmartDataThreshold5()
    {
        logger.info( "Testing hms-inband HddInfoHelperTest::isSmartDataBeyondThreshold5" );

        try
        {
            HddSMARTData hddSMARTData = new HddSMARTData();
            hddSMARTData.setParameter( "Raw Read Error Rate" );
            hddSMARTData.setValue( "20" );
            hddSMARTData.setThreshold( "20" );
            hddSMARTData.setWorst( "0" );

            boolean isDataConcerning = HddInfoHelper.isSmartDataBeyondThreshold( hddSMARTData, false );
            // System.out.println("isDataConcerning5: " + isDataConcerning);
            assertTrue( isDataConcerning );
        }
        catch ( Exception e )
        {
            logger.info( "Test hms-inband HddInfoHelperTest::isSmartDataBeyondThreshold5 Failed" );
            e.printStackTrace();
        }
    }

}