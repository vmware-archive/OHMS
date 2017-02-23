/* ********************************************************************************
 * CumulusUtil.java
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
package com.vmware.vrack.hms.switches.cumulus;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchSession;
import com.vmware.vrack.hms.common.util.SshExecResult;

/**
 * Encompasses Cumulus Switch Service utility functions. This class provides functionality for the switch service
 * utility functions .
 */
public class CumulusUtil
{

    /**
     * Return SSH session to switchNode. With provided switch node object, get the session and connect.
     * 
     * @param switchNode object
     * @return switch session that has been created for the provided switchnode
     */
    public static SwitchSession getSession( SwitchNode switchNode )
    {
        String id = switchNode.getSwitchId();
        SwitchSession switchSession = sessionMap.get( id );

        if ( switchSession != null && switchSession.isConnected() )
        {
            return switchSession;
        }

        try
        {
            switchSession = new CumulusTorSwitchSession();
            switchSession.setSwitchNode( switchNode );
            switchSession.connect();
        }
        catch ( HmsException e )
        {
            logger.error( "Error connecting to switch " + switchNode.getSwitchId(), e );
            if ( switchSession != null )
            {
                switchSession.disconnect();
                return null;
            }
        }

        sessionMap.put( id, switchSession );
        return switchSession;
    }

    /**
     * Upload a file as root user. The SSH connection is made as cumulus user, but this does not give us sufficient
     * permissions to create files in /etc/network/interfaces.d directory, for example. So, we need to upload to a
     * temporary directory and then copy to final destination.
     * 
     * @param switchNode object
     * @param bais bye array input stream
     * @param remoteFilename remote file name
     * @exception HmsException Thrown if file fails to be uploaded to switch
     */
    public static void uploadAsRoot( SwitchNode switchNode, ByteArrayInputStream bais, String remoteFilename )
        throws HmsException
    {
        CumulusTorSwitchSession session = (CumulusTorSwitchSession) getSession( switchNode );
        String temporaryFilename =
            CumulusConstants.PERSISTENT_TMP_DIR + "/" + remoteFilename.replaceAll( "/", "_" ).replaceAll( "^\\.*", "" );

        /* Upload the file to a temporary location where we have permission */
        boolean uploaded = session.upload( bais, temporaryFilename );

        if ( !uploaded )
        {
            throw new HmsException( "Failed to upload file " + remoteFilename + " to switch "
                + switchNode.getSwitchId() );
        }

        /* Copy the file to its final destination. */
        String copyCommand =
            CumulusConstants.COPY_AS_ROOT_COMMAND.replaceAll( "\\{password\\}",
                                                              qr( switchNode.getPassword() ) ).replaceAll( "\\{old\\}",
                                                                                                           temporaryFilename ).replaceAll( "\\{new\\}",
                                                                                                                                           remoteFilename );

        SshExecResult result = session.executeEnhanced( copyCommand );
        result.logIfError( logger );
        if ( result.getExitCode() != 0 )
        {
            throw new HmsException( "Failed to copy uploaded file " + remoteFilename + " on switch "
                + switchNode.getSwitchId() );
        }
    }

    /**
     * Configure Persistence Directory Persist all the relevant configuration files to be retained across
     * reboots/upgrades.
     * 
     * @param switchNode object
     * @return void
     * @throws HmsException if Setup of persistent director fails
     */
    public static void configurePersistenceDirectory( SwitchNode switchNode )
        throws HmsException
    {
        String password = switchNode.getPassword();
        String command = CumulusConstants.SETUP_PERSISTENCE_DIRECTORY.replaceAll( "\\{password\\}", qr( password ) );
        CumulusTorSwitchSession session = (CumulusTorSwitchSession) getSession( switchNode );

        SshExecResult result = session.executeEnhanced( command );
        result.logIfError( logger );

        logger.debug( "Finished configuring persistence directory on switch " + switchNode.getSwitchId() );
        return;
    }

    /**
     * Validate Source Clause Validate that the base interfaces file contains the source clause. If necessary, add it.
     * 
     * @param switchNode object
     * @return void
     * @throws HmsException thrown if there is a failure to add source clause to the interfaces on the switch
     */
    public static void validateSourceClause( SwitchNode switchNode )
        throws HmsException
    {
        CumulusTorSwitchSession session = (CumulusTorSwitchSession) getSession( switchNode );
        String command =
            CumulusConstants.ADD_SOURCE_CLAUSE_COMMAND.replaceAll( "\\{password\\}", qr( switchNode.getPassword() ) );

        SshExecResult result = session.executeEnhanced( command );
        result.logIfError( logger );
        if ( result.getExitCode() != 0 )
        {
            throw new HmsException( "Failed to add source clause to /etc/network/interfaces on switch "
                + switchNode.getSwitchId() );
        }
    }

    /**
     * getPortFileName Get port filename - helper function.
     * 
     * @param portName Name of port
     * @return string for filename for hms port
     */
    public static String getPortFilename( String portName )
    {
        String trimmed = ( portName == null ) ? "null" : portName.trim();
        return "/etc/network/interfaces.d/hms-port-" + trimmed;
    }

    /**
     * getLacpGroupFilename Get lacp group filename
     * 
     * @param lacpGroupName name of lacpgroup
     * @return string for filename for lacp group
     */
    public static String getLacpGroupFilename( String lacpGroupName )
    {
        String trimmed = ( lacpGroupName == null ) ? "null" : lacpGroupName.trim();
        return "/etc/network/interfaces.d/hms-lag-" + trimmed;
    }

    /**
     * getVlanFilename Get vlan filename
     * 
     * @param vlanName vlan name
     * @return string for filename for vlan
     */
    public static String getVlanFilename( String vlanName )
    {
        String trimmed = ( vlanName == null ) ? "null" : vlanName.trim();
        return "/etc/network/interfaces.d/hms-vlan-" + trimmed;
    }

    /**
     * getMclagFilename Get Mclag filename
     * 
     * @return hms-clag file name (location)
     */
    public static String getMclagFilename()
    {
        return "/etc/network/interfaces.d/hms-clag";
    }

    /**
     * QR function for quote replacement Helper function to return literal replacement for the provided string.
     * 
     * @param str for the replacement
     * @return literal replacement string for provided string
     */
    public static String qr( String str )
    {
        return Matcher.quoteReplacement( str );
    }

    /** Variable used to maintain the session mapping for each switch node object */
    private static Map<String, SwitchSession> sessionMap = new HashMap<String, SwitchSession>();

    private static Logger logger = Logger.getLogger( CumulusUtil.class );
}
