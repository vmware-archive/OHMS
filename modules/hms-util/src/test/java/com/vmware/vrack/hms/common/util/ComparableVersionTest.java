/* ********************************************************************************
 * ComparableVersionTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vmware.vrack.hms.common.util.ComparableVersion;

/**
 * The Class ComparableVersionTest.
 */
public class ComparableVersionTest
{
    /**
     * Test versions.
     */
    @Test
    public void testVersions()
    {
        ComparableVersion ver1 = new ComparableVersion( "0.0.1-SNAPSHOT-123456" );
        ComparableVersion ver2 = new ComparableVersion( "0.0.1-SNAPSHOT-123457" );
        this.testVersions( ver1, ver2 );
        ver1 = new ComparableVersion( "0.0.1-SNAPSHOT-123456" );
        ver2 = new ComparableVersion( "0.0.1-123456" );
        this.testVersions( ver1, ver2 );
        ver1 = new ComparableVersion( "0.0.1-123456" );
        ver2 = new ComparableVersion( "0.0.1-123457" );
        this.testVersions( ver1, ver2 );
        ver1 = new ComparableVersion( "0.0.1-123456" );
        ver2 = new ComparableVersion( "1.0.0-123456" );
        this.testVersions( ver1, ver2 );
        ver1 = new ComparableVersion( "0.0.1-123456" );
        ver2 = new ComparableVersion( "1.0.0-123457" );
        this.testVersions( ver1, ver2 );
        ver1 = new ComparableVersion( "0.0.1-123456" );
        ver2 = new ComparableVersion( "1.0.0-SNAPSHOT-123456" );
        this.testVersions( ver1, ver2 );
    }

    /**
     * Test versions. Assertion is made on the assumption that ver1 < ver2.
     *
     * @param ver1 the ver1
     * @param ver2 the ver2
     */
    private void testVersions( ComparableVersion ver1, ComparableVersion ver2 )
    {
        assertTrue( ver1.compareTo( ver2 ) == -1 );
        assertTrue( ver2.compareTo( ver1 ) == 1 );
    }
}
