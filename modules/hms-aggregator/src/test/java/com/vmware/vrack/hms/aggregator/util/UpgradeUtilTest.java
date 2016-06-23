/* ********************************************************************************
 * UpgradeUtilTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
