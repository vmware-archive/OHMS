/* ********************************************************************************
 * UpgradeUtilTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.utils;

import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vrack.hms.common.HmsConfigHolder;

// TODO: Auto-generated Javadoc
/**
 * <code>UpgradeUtilTest</code> <br>
 * .
 *
 * @author VMware, Inc.
 */
public class UpgradeUtilTest
{
    /**
     * Instantiates a new upgrade util test.
     */
    public UpgradeUtilTest()
    {
        HmsConfigHolder.initializeHmsAppProperties();
    }

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp()
        throws Exception
    {
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown()
        throws Exception
    {
    }

    /**
     * Test init upgrade with null spec.
     */
    @Test
    public void testInitUpgradeWithNullSpec()
    {
        assertFalse( UpgradeUtil.initiateUpgrade( null ) );
    }

    /**
     * Test rollback upgrade with null spec.
     */
    @Test
    public void testRollbackUpgradeWithNullSpec()
    {
        assertFalse( UpgradeUtil.rollbackUpgrade( null ) );
    }
}
