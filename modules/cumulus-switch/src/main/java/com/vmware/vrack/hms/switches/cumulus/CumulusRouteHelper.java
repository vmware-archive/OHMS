/* ********************************************************************************
 * CumulusRouteHelper.java
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

import com.vmware.vrack.hms.common.switches.api.SwitchSession;
import com.vmware.vrack.hms.common.util.SshUtil;
import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkErrorCode;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.SshExecResult;
import com.vmware.vrack.hms.switches.cumulus.model.Configuration;
import com.vmware.vrack.hms.switches.cumulus.model.Ipv4DefaultRoute;
import com.vmware.vrack.hms.switches.cumulus.model.SwitchPort;

public class CumulusRouteHelper
{

    private static Logger logger = Logger.getLogger( CumulusRouteHelper.class );

    public void configureIpv4DefaultRoute( SwitchNode switchNode, String gateway, String portId )
        throws HmsOobNetworkException
    {
        Configuration configuration = null;

        CumulusTorSwitchSession session = (CumulusTorSwitchSession) CumulusUtil.getSession( switchNode );

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            session.download( baos, CumulusConstants.INTERFACES_FILE );
            configuration = Configuration.parse( new ByteArrayInputStream( baos.toByteArray() ) );
        }
        catch ( Exception e )
        {
            logger.error( "Error in reading/parsing interfaces file on switch " + switchNode.getSwitchId() );
            throw new HmsOobNetworkException( "Error in reading/parsing interfaces file on switch "
                + switchNode.getSwitchId() + ". Reason: " + e.getMessage(), e, HmsOobNetworkErrorCode.INTERNAL_ERROR );
        }

        /* Set the configuration */
        updateIpv4DefaultRoute( configuration, gateway, portId );
        logger.debug( configuration.getString() );

        /* Upload the revised configuration file now */
        /* Upload the file */
        ByteArrayInputStream bais = new ByteArrayInputStream( configuration.getString().getBytes() );
        try
        {
            CumulusUtil.validateSourceClause( switchNode );
            CumulusUtil.uploadAsRoot( switchNode, bais, CumulusConstants.INTERFACES_FILE );

            CumulusUtil.configurePersistenceDirectory( switchNode );

            /* Activate the configuration in the current session */
            SshExecResult sshExecResult =
                session.executeEnhanced( CumulusConstants.RELOAD_INTERFACES.replaceAll( "\\{password\\}",
                                                                                        CumulusUtil.qr( switchNode.getPassword() ) ) );
            sshExecResult.logIfError( logger );
        }
        catch ( HmsException e )
        {
            logger.error( "Failed to upload new configuration file to switch " + switchNode.getSwitchId() );
            throw new HmsOobNetworkException( "Failed to upload new configuration file to switch "
                + switchNode.getSwitchId() + ". Reason: " + e.getMessage(), e, HmsOobNetworkErrorCode.UPLOAD_FAILED );
        }

        /*
         * Finally, change the default route dynamically using "ip route " command
         */
        try
        {
            execUpdateIpv4DefaultRoute( switchNode, gateway, portId );
        }
        catch ( HmsException e )
        {
            logger.error( "Failed to set route on switch " + switchNode.getSwitchId() );
        }
    }

    /**
     * @param switchNode
     * @throws HmsOobNetworkException
     */
    public void deleteIpv4DefaultRoute( SwitchNode switchNode )
        throws HmsException
    {
        Configuration configuration = null;

        CumulusTorSwitchSession session = (CumulusTorSwitchSession) CumulusUtil.getSession( switchNode );

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            session.download( baos, CumulusConstants.INTERFACES_FILE );
            configuration = Configuration.parse( new ByteArrayInputStream( baos.toByteArray() ) );
        }
        catch ( Exception e )
        {
            logger.error( "Error in reading/parsing interfaces file on switch " + switchNode.getSwitchId() );
            throw new HmsOobNetworkException( "Error in reading/parsing interfaces file on switch "
                + switchNode.getSwitchId() + ". Reason: " + e.getMessage(), e, HmsOobNetworkErrorCode.INTERNAL_ERROR );
        }

        /* Delete the route from configuration */
        deleteIpv4DefaultRouteImpl( configuration, switchNode );
        logger.debug( configuration.getString() );

        /* Upload the revised configuration file now */
        /* Upload the file */
        ByteArrayInputStream bais = new ByteArrayInputStream( configuration.getString().getBytes() );
        try
        {
            CumulusUtil.validateSourceClause( switchNode );
            CumulusUtil.uploadAsRoot( switchNode, bais, CumulusConstants.INTERFACES_FILE );

            CumulusUtil.configurePersistenceDirectory( switchNode );

            /* Activate the configuration in the current session */
            SshExecResult sshExecResult =
                session.executeEnhanced( CumulusConstants.RELOAD_INTERFACES.replaceAll( "\\{password\\}",
                                                                                        CumulusUtil.qr( switchNode.getPassword() ) ) );
            sshExecResult.logIfError( logger );
        }
        catch ( HmsException e )
        {
            logger.error( "Failed to upload new configuration file to switch " + switchNode.getSwitchId() );
            throw new HmsOobNetworkException( "Failed to upload new configuration file to switch "
                + switchNode.getSwitchId() + ". Reason: " + e.getMessage(), e, HmsOobNetworkErrorCode.UPLOAD_FAILED );
        }
    }

    /**
     * There can only be 1 default route so when being set if an existing port is already used for default route then we
     * reset the route on that port and set it with the passed gateway on the new port
     *
     * @param configuration
     * @param gateway
     * @param portId
     */
    private void updateIpv4DefaultRoute( Configuration configuration, String gateway, String portId )
    {
        Boolean set = false;
        Boolean reset = false;

        for ( SwitchPort switchPort : configuration.switchPorts )
        {
            if ( switchPort.name.equals( portId ) )
            {
                set = true;
                Ipv4DefaultRoute ipv4DefaultRoute = new Ipv4DefaultRoute();
                ipv4DefaultRoute.setGateway( gateway );
                switchPort.setIpv4DefaultRoute( ipv4DefaultRoute );
            }
            else if ( switchPort.getIpv4DefaultRoute() != null )
            {
                logger.debug( "Resetting existing default route through port " + switchPort.name + ", with next-hop "
                    + switchPort.getIpv4DefaultRoute().getGateway() );
                reset = true;
                switchPort.setIpv4DefaultRoute( null );
            }

            if ( set && reset )
                break;
        }

        if ( !set )
        {
            set = true;
            SwitchPort switchPort = new SwitchPort();
            switchPort.name = portId;
            Ipv4DefaultRoute ipv4DefaultRoute = new Ipv4DefaultRoute();
            ipv4DefaultRoute.setGateway( gateway );
            switchPort.setIpv4DefaultRoute( ipv4DefaultRoute );
            configuration.switchPorts.add( switchPort );
        }

        if ( set )
            logger.debug( "Set new default route through port " + portId + ", with next-hop " + gateway );

    }

    private void execUpdateIpv4DefaultRoute( SwitchNode switchNode, String gateway, String portId )
        throws HmsException
    {
        String routeCmd =
            CumulusConstants.SET_DEFAULT_ROUTE_CMD.replaceAll( "\\{password\\}",
                                                               switchNode.getPassword() ).replaceAll( "\\{gateway\\}",
                                                                                                      gateway ).replaceAll( "\\{port\\}",
                                                                                                                            portId );

        CumulusTorSwitchSession session = (CumulusTorSwitchSession) CumulusUtil.getSession( switchNode );
        session.executeNoResponse( routeCmd );
    }

    /**
     * If a default route is set on any port on a switch, remove it.
     *
     * @param configuration
     * @throws HmsException
     */
    private void deleteIpv4DefaultRouteImpl( Configuration configuration, SwitchNode switchNode )
        throws HmsException
    {
        for ( SwitchPort switchPort : configuration.switchPorts )
        {
            String gateway, portId;
            if ( switchPort.getIpv4DefaultRoute() != null )
            {
                gateway = switchPort.getIpv4DefaultRoute().getGateway();
                portId = switchPort.name;
                logger.debug( "Resetting existing default route through port " + portId + ", with next-hop "
                    + gateway );
                if ( switchPort.name.startsWith( "eth" ) )
                    switchPort.setIpv4DefaultRoute( null );
                else
                    switchPort.deleteIpv4DefaultRoute();
            }
        }
    }
}
