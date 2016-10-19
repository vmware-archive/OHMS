package com.vmware.vrack.hms.plugin.command.chassis;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.plugin.command.Command;

public class ChassisState
{
    private static final Pattern chassisPower = Pattern.compile( "chassis_power += (.*)" ),
                    resetEndMessage = Pattern.compile( "ipmiutil reset, completed successfully" );

    public static boolean getChassisPowerStatus( ServiceHmsNode serviceHmsNode, String command )
        throws IOException
    {
        String commandStr = command + " health" + " -N " + serviceHmsNode.getManagementIp() + " -U "
            + serviceHmsNode.getManagementUserName() + " -P " + serviceHmsNode.getManagementUserPassword() + " -J 3";
        BufferedReader br = Command.executeCommand( commandStr );
        String line;
        while ( ( line = br.readLine() ) != null )
        {
            Matcher matcher = chassisPower.matcher( line );
            if ( matcher.find() )
                return ( matcher.group( 1 ).contains( "on" ) ? true : false );

        }

        return false;
    }

    public static boolean powerUpChassis( ServiceHmsNode serviceHmsNode, String command )
        throws IOException
    {
        String commandStr = command + " reset -u" + " -N " + serviceHmsNode.getManagementIp() + " -U "
            + serviceHmsNode.getManagementUserName() + " -P " + serviceHmsNode.getManagementUserPassword() + " -J 3";
        BufferedReader br = Command.executeCommand( commandStr );
        String line;
        while ( ( line = br.readLine() ) != null )
        {
            Matcher matcher = resetEndMessage.matcher( line );
            if ( matcher.find() )
                return true;
        }
        return false;
    }

    public static boolean powerDownChassis( ServiceHmsNode serviceHmsNode, String command )
        throws IOException
    {
        String commandStr = command + " reset -d" + " -N " + serviceHmsNode.getManagementIp() + " -U "
            + serviceHmsNode.getManagementUserName() + " -P " + serviceHmsNode.getManagementUserPassword() + " -J 3";
        BufferedReader br = Command.executeCommand( commandStr );
        String line;
        while ( ( line = br.readLine() ) != null )
        {
            Matcher matcher = resetEndMessage.matcher( line );
            if ( matcher.find() )
                return true;
        }
        return false;
    }

    public static boolean powerCycleChassis( ServiceHmsNode serviceHmsNode, String command )
        throws IOException
    {
        String commandStr = command + " reset -c" + " -N " + serviceHmsNode.getManagementIp() + " -U "
            + serviceHmsNode.getManagementUserName() + " -P " + serviceHmsNode.getManagementUserPassword() + " -J 3";
        BufferedReader br = Command.executeCommand( commandStr );
        String line;
        while ( ( line = br.readLine() ) != null )
        {
            Matcher matcher = resetEndMessage.matcher( line );
            if ( matcher.find() )
                return true;
        }
        return false;
    }

    public static boolean hardResetChassis( ServiceHmsNode serviceHmsNode, String command )
        throws IOException
    {
        String commandStr = command + " reset -r" + " -N " + serviceHmsNode.getManagementIp() + " -U "
            + serviceHmsNode.getManagementUserName() + " -P " + serviceHmsNode.getManagementUserPassword() + " -J 3";
        BufferedReader br = Command.executeCommand( commandStr );
        String line;
        while ( ( line = br.readLine() ) != null )
        {
            Matcher matcher = resetEndMessage.matcher( line );
            if ( matcher.find() )
                return true;
        }
        return false;
    }

    public static boolean coldResetChassis( ServiceHmsNode serviceHmsNode, String command )
        throws IOException
    {
        String commandStr = command + " reset -k" + " -N " + serviceHmsNode.getManagementIp() + " -U "
            + serviceHmsNode.getManagementUserName() + " -P " + serviceHmsNode.getManagementUserPassword() + " -J 3";
        BufferedReader br = Command.executeCommand( commandStr );
        String line;
        while ( ( line = br.readLine() ) != null )
        {
            Matcher matcher = resetEndMessage.matcher( line );
            if ( matcher.find() )
                return true;
        }
        return false;
    }

}
