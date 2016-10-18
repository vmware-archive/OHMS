package com.vmware.vrack.hms.plugin.command.chassis;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.plugin.command.Command;
import com.vmware.vrack.hms.plugin.command.boot.BootUtilHelper;
import com.vmware.vrack.hms.plugin.ipmicode.BmcConstants;
import com.vmware.vrack.hms.plugin.ipmicode.NetFunction;

public class ChassisIdentify
{
    private static final Pattern chassisIdentifyEndMessage = Pattern.compile( "ipmiutil cmd, completed successfully" );

    private static final String setChassisIdentifyCommand = "04";

    public static boolean setChassisIdentification( ServiceHmsNode serviceHmsNode,
                                                    ChassisIdentifyOptions chassisIdentifyOptions,
                                                    String command )
        throws HmsException
    {

        String commandStr = command + " cmd" + " -N " + serviceHmsNode.getManagementIp() + " -U "
            + serviceHmsNode.getManagementUserName() + " -P " + serviceHmsNode.getManagementUserPassword()
            + " -J 3 -V 4 ";

        /*
         * Adding bus number (usually 00), resource slave address (usually 0x20), NetFunction code and command code to
         * the command string
         */
        commandStr += BmcConstants.bus + " " + BmcConstants.rsSa + " " + NetFunction.CHASSIS_Request + " "
            + setChassisIdentifyCommand;

        byte[] data = getByteArrayFromChassisIdentifyOptions( chassisIdentifyOptions );

        String chassisIdentifyParameter = BootUtilHelper.convertByteArraytoHex( data );

        commandStr += chassisIdentifyParameter;

        try
        {
            BufferedReader br = Command.executeCommand( commandStr );
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                Matcher matcher = chassisIdentifyEndMessage.matcher( line );
                if ( matcher.find() )
                    return true;
            }
        }
        catch ( IOException e )
        {
            throw new HmsException(e);
        }
        throw new HmsException( "setChassisIdentification command execution got failed !" );
    }

    private static byte[] getByteArrayFromChassisIdentifyOptions( ChassisIdentifyOptions chassisIdentifyOptions )
    {
        byte[] data = null;
        if ( chassisIdentifyOptions != null )
        {
            Boolean identify = chassisIdentifyOptions.getIdentify();
            Boolean forceIdentify = chassisIdentifyOptions.getForceIdentifyChassis();
            Integer interval = chassisIdentifyOptions.getInterval();

            /*
             * If forceIdentify is true, then switch on the chassis identify option irrespective of other data.
             * 
             * If both forceIdentify as well as identify are false then switch off the chassis identify option
             * irrespective of other data.
             * 
             * Otherwise if only identify is true then switch on and set the chassis identify option with the given interval.
             */

            if ( forceIdentify != null && forceIdentify )
                data = new byte[] { 0x00, 0x01 };
            else if ( identify != null && !identify )
                data = new byte[] { 0x00 };
            else if ( identify != null && identify )
            {
                data = ( interval != null && interval > 0 && interval <= 255 )
                                ? new byte[] { interval.byteValue(), 0x00 } : null;
            }
            return data;
        }
        return data;
    }

}
