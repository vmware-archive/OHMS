/* ********************************************************************************
 * SshExecResult.java
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

import java.nio.charset.StandardCharsets;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SshExecResult
{
    private String command;

    private byte[] stdout;

    private byte[] stderr;

    private int exitCode;

    private long startTime;

    private long endTime;

    public String getCommand()
    {
        return command;
    }

    public void setCommand( String command )
    {
        this.command = command;
    }

    public byte[] getStdout()
    {
        return stdout;
    }

    public String getStdoutAsString()
    {
        String ret = null;
        try
        {
            if ( stdout != null )
                ret = new String( stdout, StandardCharsets.UTF_8 );
        }
        catch ( Exception e )
        {
            ret = "Could not retrieve the output. Exception: " + e.getMessage();
        }
        return ret;
    }

    public void setStdout( byte[] stdout )
    {
        this.stdout = stdout;
    }

    public byte[] getStderr()
    {
        return stderr;
    }

    public String getStderrAsString()
    {
        String ret = null;
        try
        {
            if ( stderr != null )
                ret = new String( stderr, StandardCharsets.UTF_8 );
        }
        catch ( Exception e )
        {
            ret = "Could not retrieve the error. Exception: " + e.getMessage();
        }
        return ret;
    }

    public void setStderr( byte[] stderr )
    {
        this.stderr = stderr;
    }

    public int getExitCode()
    {
        return exitCode;
    }

    public void setExitCode( int exitCode )
    {
        this.exitCode = exitCode;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime( long startTime )
    {
        this.startTime = startTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public void setEndTime( long endTime )
    {
        this.endTime = endTime;
    }

    public void logIfError( Logger logger )
    {
        if ( exitCode != 0 )
        {
            logger.error( "Execution of the following command failed with exit code " + exitCode );
            logger.error( command );
            if ( stderr.length > 0 )
                logger.error( "stderr: " + new String( stderr ) );
            if ( stdout.length > 0 )
                logger.error( "stdout: " + new String( stdout ) );
        }
    }

    public void log( Logger logger, Level level )
    {
        logger.log( level, "command: " + command );
        logger.log( level, "exit code: " + exitCode );
        if ( stdout.length > 0 )
            logger.log( level, "stdout: " + new String( stdout ) );
        if ( stderr.length > 0 )
            logger.log( level, "stderr: " + new String( stderr ) );
    }
}
