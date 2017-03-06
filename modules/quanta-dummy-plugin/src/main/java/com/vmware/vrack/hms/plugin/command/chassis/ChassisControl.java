/* ********************************************************************************
 * ChassisControl.java
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
package com.vmware.vrack.hms.plugin.command.chassis;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.plugin.command.Command;

public class ChassisControl
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

    /**
     * Power cycles the target chassis. If the chassis is in off state, then just powers up the chassis else, restart
     * the chassis.
     * 
     * @param serviceHmsNode
     * @param command
     * @return boolean
     * @throws IOException
     */
    public static boolean powerCycleChassis( ServiceHmsNode serviceHmsNode, String command )
        throws IOException
    {
        if ( !getChassisPowerStatus( serviceHmsNode, command ) )
            return powerUpChassis( serviceHmsNode, command );

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
