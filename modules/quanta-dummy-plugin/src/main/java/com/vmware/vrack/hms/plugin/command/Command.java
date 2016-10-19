package com.vmware.vrack.hms.plugin.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Command
{
    public static BufferedReader executeCommand( String commandStr )
        throws IOException
    {
        Process p = Runtime.getRuntime().exec( commandStr );
        BufferedReader br = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
        return br;
    }

}
