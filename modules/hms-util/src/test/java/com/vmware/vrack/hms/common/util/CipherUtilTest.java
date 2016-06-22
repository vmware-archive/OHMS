/* ********************************************************************************
 * CipherUtilTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

public class CipherUtilTest
{
    private static Logger logger = Logger.getLogger( CipherUtilTest.class );

    @Test
    public void test()
    {
        logger.info( "Testing hmsutil CipherUtilTest" );
        try
        {
            String encryptString = CipherUtil.encrypt( "hmsutiltest" );
            assertNotNull( encryptString );
            String decryptString = CipherUtil.decrypt( encryptString );
            assertNotNull( decryptString );
        }
        catch ( Exception e )
        {
            logger.info( "Test hmsutil CipherUtilTest Failed" );
            e.printStackTrace();
        }
    }
}
