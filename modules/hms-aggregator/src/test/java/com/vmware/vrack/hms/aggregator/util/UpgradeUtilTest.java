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
package com.vmware.vrack.hms.aggregator.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * The Class UpgradeUtilTest.
 */
public class UpgradeUtilTest
{
    /**
     * Test get patch file snapshot version.
     */
    @Test
    public void testGetPatchFileSnapshotVersion()
    {
        this.testGetPatchFileVersion( "0.0.1-SNAPSHOT", "123456" );
    }

    /**
     * Test get patch file release version.
     */
    @Test
    public void testGetPatchFileReleaseVersion()
    {
        this.testGetPatchFileVersion( "0.0.1", "123456" );
    }

    /**
     * Test get patch file version.
     *
     * @param hmsVersion the hms version
     * @param hmsBuildNumber the hms build number
     */
    private void testGetPatchFileVersion( final String hmsVersion, final String hmsBuildNumber )
    {
        String hmsBundleVersion = String.format( "%1$s-%2$s", hmsVersion, hmsBuildNumber );
        String patchFile =
            String.format( "/opt/vrack/upgrade/evorack-hms-bundle-%1$s-%2$s.tar", hmsVersion, hmsBuildNumber );
        String patchFileVersion = UpgradeUtil.getPatchFileVersion( patchFile );
        assertNotNull( patchFileVersion );
        assertEquals( patchFileVersion, hmsBundleVersion );
    }

    @Test
    public void testValidatePreviousVersion()
    {
        assertTrue( UpgradeUtil.validatePreviousVersion( "0.0.1-SNAPSHOT-123456", "0.0.1-SNAPSHOT-123456" ) );
        assertTrue( UpgradeUtil.validatePreviousVersion( "0.0.1-123456", "0.0.1-123456" ) );
        assertFalse( UpgradeUtil.validatePreviousVersion( "0.0.1-SNAPSHOT-123456", "0.0.1-SNAPSHOT-123457" ) );
        assertFalse( UpgradeUtil.validatePreviousVersion( "0.0.1-SNAPSHOT-123457", "0.0.1-SNAPSHOT-123456" ) );
        assertFalse( UpgradeUtil.validatePreviousVersion( "0.0.1-123456", "0.0.1-123457" ) );
        assertFalse( UpgradeUtil.validatePreviousVersion( "0.0.1-123457", "0.0.1-123456" ) );
        assertFalse( UpgradeUtil.validatePreviousVersion( "0.0.1-SNAPSHOT-123456", "0.0.1-123456" ) );
        assertFalse( UpgradeUtil.validatePreviousVersion( "0.0.1-123457", "0.0.1-SNAPSHOT-123457" ) );
        assertFalse( UpgradeUtil.validatePreviousVersion( "0.0.1-SNAPSHOT", "0.0.1-123456" ) );
        assertFalse( UpgradeUtil.validatePreviousVersion( "0.0.1-123457", "0.0.1-SNAPSHOT" ) );
    }
}
