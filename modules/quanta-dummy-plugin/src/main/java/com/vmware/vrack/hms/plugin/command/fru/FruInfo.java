package com.vmware.vrack.hms.plugin.command.fru;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.plugin.command.Command;

public class FruInfo
{
    private static final Pattern mfgDataTime = Pattern.compile( "(Board Mfg DateTime\\s+\\|\\s+)(.*)" ),
                    serialNumber = Pattern.compile( "(Board Serial Number\\s+\\|\\s+)(.*)" ),
                    partNumber = Pattern.compile( "(Board Part Number\\s+\\|\\s+)(.*)" ),
                    fruEndMessage = Pattern.compile( "ipmiutil fru, completed successfully" );

    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode, String command )
        throws HmsException
    {
        ServerNodeInfo serverNodeInfo = new ServerNodeInfo();
        ComponentIdentifier serverComponentIdentifier = new ComponentIdentifier();
        boolean success = false;

        String commandStr = command + " fru" + " -bc" + " -N " + serviceHmsNode.getManagementIp() + " -U "
            + serviceHmsNode.getManagementUserName() + " -P " + serviceHmsNode.getManagementUserPassword() + " -J 3";

        BufferedReader br;
        try
        {
            br = Command.executeCommand( commandStr );
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                Matcher matcher = mfgDataTime.matcher( line );
                if ( matcher.find() )
                    serverComponentIdentifier.setManufacturingDate( matcher.group( 2 ) );

                matcher = serialNumber.matcher( line );
                if ( matcher.find() )
                    serverComponentIdentifier.setSerialNumber( matcher.group( 2 ) );

                matcher = partNumber.matcher( line );
                if ( matcher.find() )
                    serverComponentIdentifier.setPartNumber( matcher.group( 2 ) );
                
                matcher = fruEndMessage.matcher( line );
                if ( matcher.find() )
                    success = true;
            }
        }
        catch ( IOException e )
        {
            throw new HmsException( e );
        }

        if ( !success )
            throw new HmsException( "ifru command didn't execute successfully on BMC of "
                + serviceHmsNode.getNodeID() );

        serverNodeInfo.setComponentIdentifier( serverComponentIdentifier );
        return serverNodeInfo;
    }

}
