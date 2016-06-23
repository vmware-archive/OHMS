/* ********************************************************************************
 * ProcessUtilTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * <code>ProcessUtilTest</code> is ... <br>
 *
 * @author VMware, Inc.
 */
public class ProcessUtilTest
{
    /** The user home. */
    private String userHome;

    /** The time in millis. */
    private String timeInMillis;

    /**
     * Instantiates a new process util test.
     */
    public ProcessUtilTest()
    {
        userHome = System.getProperty( "user.home" );
        Calendar cal = Calendar.getInstance();
        timeInMillis = Long.toString( cal.getTimeInMillis() );
    }

    /**
     * Test.
     */
    @Test
    @Ignore
    public void testgetCommandExitValue()
    {
        List<String> cmdWithArgs = new ArrayList<String>();
        if ( SystemUtils.IS_OS_LINUX )
        {
            cmdWithArgs.add( 0, "ls" );
        }
        else if ( SystemUtils.IS_OS_WINDOWS )
        {
            cmdWithArgs.add( 0, "dir" );
        }
        cmdWithArgs.add( 1, userHome );
        int exitValue = ProcessUtil.getCommandExitValue( cmdWithArgs );
        assertTrue( exitValue != -1 );
        assertTrue( exitValue == 0 );
        cmdWithArgs.remove( 1 );
        cmdWithArgs.add( 1, userHome + File.separator + timeInMillis + File.separator + timeInMillis );
        exitValue = ProcessUtil.getCommandExitValue( cmdWithArgs );
        assertTrue( exitValue != -1 );
        assertTrue( exitValue == 1 );
    }
}
