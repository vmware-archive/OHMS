/* ********************************************************************************
 * VersionCheckTest.java
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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class VersionCheckTest
{
    private final String SEMANTIC_VERSION_REGEX = "(\\d+.){0,2}\\d+";

    private final String HMS_SNAPSHOT_VERSION_REGEX = SEMANTIC_VERSION_REGEX + "-SNAPSHOT-\\d+";

    private final String HMS_RELEASE_VERSION_REGEX = SEMANTIC_VERSION_REGEX + "-\\d+";

    @Test
    public void testVersion()
    {
        String version = "1.12.123-SNAPSHOT";
        String buildNumber = "123456";
        String hmsSnapshotVersion = version + "-" + buildNumber;
        assertTrue( hmsSnapshotVersion.matches( HMS_SNAPSHOT_VERSION_REGEX ) );
        version = "1.12.123";
        String hmsReleaseVersion = version + "-" + buildNumber;
        assertTrue( hmsReleaseVersion.matches( HMS_RELEASE_VERSION_REGEX ) );
    }

    @Test
    public void testVersionPattern()
    {
        this.testVersionPattern( "0.0.1", "123456" );
        this.testVersionPattern( "0.0.1-SNAPSHOT", "123456" );
    }

    private void testVersionPattern( final String hmsReleaseVrsion, final String hmsBuildNumber )
    {
        String hmsBundleVersion = String.format( "%1$s-%2$s", hmsReleaseVrsion, hmsBuildNumber );
        System.out.println( "HMS Bundle Version: " + hmsBundleVersion );
        String patchFile =
            String.format( "/opt/vrack/upgrade/evorack-hms-bundle-%1$s-%2$s.tar", hmsReleaseVrsion, hmsBuildNumber );
        File f = new File( patchFile );
        String patchFileName = f.getName();
        // first check if the bundle is a release version one
        Pattern pattern = Pattern.compile( "^.*?(" + HMS_RELEASE_VERSION_REGEX + ").*$" );
        Matcher matcher = pattern.matcher( patchFileName );
        String version = null;
        if ( matcher.find() )
        {
            version = matcher.group( 1 );
            System.out.println( "Version: " + version );
            assertTrue( version.equals( hmsBundleVersion ) );
        }
        else
        {
            // Check if the bundle is a SNAPSHOT version one
            pattern = Pattern.compile( "^.*?(" + HMS_SNAPSHOT_VERSION_REGEX + ").*$" );
            matcher = pattern.matcher( patchFileName );
            if ( matcher.find() )
            {
                version = matcher.group( 1 );
                System.out.println( "Version: " + version );
                assertTrue( version.equals( hmsBundleVersion ) );
            }
        }
    }

    @Test
    public void testGetPatchFileVersion()
    {
        String hmsVersion = "0.0.1-SNAPSHOT";
        String hmsBuildNumber = "123456";
        String hmsBundleVersion = String.format( "%1$s-%2$s", hmsVersion, hmsBuildNumber );
        System.out.println( "HMS Bundle Version: " + hmsBundleVersion );
        String patchFile =
            String.format( "/opt/vrack/upgrade/evorack-hms-bundle-%1$s-%2$s.tar", hmsVersion, hmsBuildNumber );
    }
}
