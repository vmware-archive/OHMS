/* ********************************************************************************
 * UpgradeUtilTest.java
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

package com.vmware.vrack.hms.utils;

import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vrack.hms.common.HmsConfigHolder;

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
        assertFalse( UpgradeUtil.initiateUpgrade( null, null ) );
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
