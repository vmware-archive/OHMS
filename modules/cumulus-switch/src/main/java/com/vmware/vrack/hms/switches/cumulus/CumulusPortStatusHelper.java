/* ********************************************************************************
 * CumulusPortStatusHelper.java
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
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkErrorCode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchSession;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortStatus;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortType;
import com.vmware.vrack.hms.common.util.SshExecResult;
import com.vmware.vrack.hms.switches.cumulus.model.Configuration;

public class CumulusPortStatusHelper
{

    public CumulusPortStatusHelper( CumulusTorSwitchService service )
    {
        this.service = service;
    }

    public boolean setSwitchPortStatusRuntimeOnly( SwitchNode switchNode, String portName, PortStatus portStatus )
        throws HmsException
    {
        SwitchSession switchSession = service.getSession( switchNode );
        boolean success = false;

        if ( switchSession == null )
        {
            throw new HmsException( String.format( "Cannot ssh to switch %s (%s)", switchNode.getIpAddress(),
                                                   switchNode.getSwitchId() ) );
        }

        String command =
            CumulusConstants.CHANGE_PORT_STATUS_COMMAND.replaceAll( "\\{portName\\}",
                                                                    ( portName.trim() ) ).replaceAll( "\\{status\\}",
                                                                                                      portStatus == SwitchPort.PortStatus.UP
                                                                                                                      ? "up"
                                                                                                                      : "down" ).replaceAll( "\\{password\\}",
                                                                                                                                             ( switchNode.getPassword() ).trim() );

        SwitchPort port = service.getSwitchPort( switchNode, portName );

        if ( port.getType() != PortType.LOOPBACK && port.getType() != PortType.MANAGEMENT )
        {
            try
            {
                switchSession.execute( command );
                success = true;
            }
            catch ( Exception e )
            {
                logger.error( "Error while changing port status of " + portName + " to " + portStatus, e );
                throw new HmsException( "Error while changing port status of " + portName + " to " + portStatus, e );
            }
        }

        return ( success );
    }

    public boolean createNewPortStanzaIfAbsent( SwitchNode switchNode, String portName )
        throws HmsException
    {
        com.vmware.vrack.hms.switches.cumulus.model.SwitchPort port = null;
        /*
         * +++rsen: April, 2016, Sprint-55, BUG1630274: Persist port up/down status change
         * =================================================================================== If the port is already
         * not enabled through persistent settings then let's do that. This is a temporary hack which will be tuned more
         * in future We do not save down state as persistent because requires much more logic
         */
        // Temporary hack START --------------------------------------------------------------------------->
        CumulusTorSwitchSession session = (CumulusTorSwitchSession) CumulusUtil.getSession( switchNode );

        if ( session == null )
        {
            throw new HmsException( String.format( "Cannot ssh to switch %s (%s)", switchNode.getIpAddress(),
                                                   switchNode.getSwitchId() ) );
        }

        Configuration configuration = null;
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            session.download( baos, CumulusConstants.INTERFACES_FILE );
            configuration = Configuration.parse( new ByteArrayInputStream( baos.toByteArray() ) );
        }
        catch ( Exception e )
        {
            logger.error( "Error in reading/parsing interfaces file on switch " + switchNode.getSwitchId()
                + ". Reason: " + e.getMessage(), e );
            return false;
        }

        /*
         * now make sure that the non-existent port is also there
         */
        if ( configuration.getConfigBlock( portName ) == null )
        {
            List<com.vmware.vrack.hms.switches.cumulus.model.SwitchPort> ports =
                configuration.convertToSwitchPorts( Arrays.asList( portName ) );
            port = ports.get( 0 ); // always a hit
            port.vlans = null;
            configuration.addConfigBlock( port );
            if ( configuration.bridges != null && !configuration.bridges.isEmpty()
                && configuration.bridges.get( 0 ).getMemberByName( portName ) == null )
            {
                configuration.bridges.get( 0 ).members.add( port );
            }

            /* Upload the file */
            ByteArrayInputStream bais = new ByteArrayInputStream( configuration.getString().getBytes() );
            CumulusUtil.validateSourceClause( switchNode );
            CumulusUtil.uploadAsRoot( switchNode, bais, CumulusConstants.INTERFACES_FILE );

            CumulusUtil.configurePersistenceDirectory( switchNode );

            /* Activate the VLAN in the current session */
            SshExecResult result =
                session.executeEnhanced( CumulusConstants.RELOAD_INTERFACES.replaceAll( "\\{password\\}",
                                                                                        CumulusUtil.qr( switchNode.getPassword() ) ) );
            result.logIfError( logger );
        }
        else
        {
            // if the port is already part of persistent config then nothing to do
        }
        // Temporary hack END --------------------------------------------------------------------------->

        return true;
    }

    /** Variable used to represent the Cumulus Switch session */
    private CumulusTorSwitchService service;

    private static Logger logger = Logger.getLogger( CumulusPortStatusHelper.class );
}
