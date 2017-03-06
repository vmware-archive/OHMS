/* ********************************************************************************
 * CumulusLacpGroupHelper.java
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsObjectNotFoundException;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkErrorCode;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.SshExecResult;
import com.vmware.vrack.hms.switches.cumulus.model.Bond;
import com.vmware.vrack.hms.switches.cumulus.model.Bridge;
import com.vmware.vrack.hms.switches.cumulus.model.ConfigBlock;
import com.vmware.vrack.hms.switches.cumulus.model.Configuration;
import com.vmware.vrack.hms.switches.cumulus.model.SwitchPort;
import com.vmware.vrack.hms.switches.cumulus.util.CumulusCache;

/**
 * Provides functionality on lacp groups. This class provides functionality to maintain, get, create and delete lacp
 * groups on the cumulus switch.
 */
public class CumulusLacpGroupHelper
{

    /**
     * Lacp Group helper class constructor Cumulus LacpGroup helper class constructor; sets the service for the
     * CumulusLacpGroupHelper object.
     *
     * @param service CumulusTorSwitchService object
     */
    public CumulusLacpGroupHelper( CumulusTorSwitchService service )
    {
        this.service = service;
    }

    /**
     * Get lacp groups for the switch node Using the switch node provided, fetch the list of bonds/LACP groups
     * associated with the switch node.
     *
     * @param switchNode switch node object
     * @return list of all lacp groups associated with the specified switch node
     */
    public List<String> getSwitchLacpGroups( SwitchNode switchNode )
    {
        CumulusTorSwitchSession session = (CumulusTorSwitchSession) CumulusUtil.getSession( switchNode );
        List<String> bondList = null;

        try
        {
            SshExecResult result = session.executeEnhanced( CumulusConstants.LIST_BONDS_COMMAND );
            result.logIfError( logger );

            String bondResult = new String( result.getStdout() ).trim();
            if ( result.getExitCode() == 0 && !bondResult.equals( "" ) )
            {
                bondList = Arrays.asList( bondResult.split( "\n" ) );
            }
            else
            {
                bondList = new ArrayList<String>();
            }
        }
        catch ( HmsException e )
        {
            logger.error( "Error received while fetching LACP groups", e );
        }

        return bondList;
    }

    /**
     * Get specified lacp group for a particular switch node Utilizing the switch node and the name of a particular lacp
     * group, get that lacp group object.
     *
     * @param switchNode switch node object
     * @param lacpGroupName name of one lacp group associated with this switch node
     * @return SwitchLacpGroup for the specified lacp group name
     * @throws HmsException thrown if LACP group is not found or does not exist for the CumulusTorSwitchSession
     */
    public SwitchLacpGroup getSwitchLacpGroup( SwitchNode switchNode, String lacpGroupName )
        throws HmsException
    {
        CumulusTorSwitchSession switchSession = (CumulusTorSwitchSession) CumulusUtil.getSession( switchNode );
        SwitchLacpGroup lag = new SwitchLacpGroup();
        List<String> lagMembers = new ArrayList<String>();
        Pattern mtuPattern = Pattern.compile( "(.*) (mtu) ([0-9]+) (.*)" );
        Integer mtu = 0;

        String command = CumulusConstants.LIST_LACP_GROUP_COMMAND.replaceAll( "\\{lacpGroupName\\}", lacpGroupName );

        SshExecResult result = switchSession.executeEnhanced( command );
        if ( result.getExitCode() != 0 )
        {
            throw new HmsObjectNotFoundException( "LACP group " + lacpGroupName + " does not exist." );
        }

        String lagOutput = new String( result.getStdout() ).trim();
        String[] lagDetails = lagOutput.split( "\n" );

        for ( String p : lagDetails )
        {
            if ( p != null && !p.equals( "" ) )
            {
                lagMembers.add( p.trim() );
            }
        }

        command = CumulusConstants.IP_LIST_PORT_COMMAND.replaceAll( "\\{portName\\}", lacpGroupName );

        result = switchSession.executeEnhanced( command );
        if ( result.getExitCode() != 0 )
        {
            throw new HmsObjectNotFoundException( "LACP group " + lacpGroupName + " does not exist." );
        }

        lagOutput = new String( result.getStdout() ).trim();

        Matcher matcher = mtuPattern.matcher( lagOutput );
        if ( matcher.matches() )
        {
            int count = matcher.groupCount();

            if ( count >= 4 )
            {
                mtu = Integer.parseInt( matcher.group( 3 ) );
            }
        }

        // Fetch the IP address of the bond if specified
        command = CumulusConstants.SHOW_LACP_IP_ADDR.replaceAll( "\\{lacpGroupName\\}", lacpGroupName );
        result = switchSession.executeEnhanced( command );
        if ( ( result.getExitCode() == 0 ) && ( result.getStdout().length > 0 ) )
        {
            String ipAddr = new String( result.getStdout() ).trim();
            if ( !ipAddr.isEmpty() )
                lag.setIpAddress( ipAddr );
        }

        /* Now initialize the result */
        lag.setName( lacpGroupName );
        lag.setMode( "802.3ad" );
        lag.setPorts( lagMembers );
        lag.setMtu( mtu );

        return lag;
    }

    /**
     * Delete the lacp group Delete lacp group (port list) on the switch node provided. Redo all the vlans that have the
     * provide lacp group name, remove configuration, and reload the interfaces post delete.
     *
     * @param switchNode switch node object
     * @param lacpGroupName lacp group name to be deleted
     * @return True if lacpgroup deleted successfully; False if lacpgroup failed to delete successfully
     * @throws HmsException if the lacpgroup is null, and thus could not be found.
     */
    public boolean deleteLacpGroup( SwitchNode switchNode, String lacpGroupName )
        throws HmsException
    {
        SwitchLacpGroup lag = null;

        try
        {
            lag = getSwitchLacpGroup( switchNode, lacpGroupName );
        }
        catch ( HmsObjectNotFoundException e )
        {
            return true; // we already in desired state
        }

        if ( lag == null )
        {
            throw new HmsObjectNotFoundException( "LACP group " + lacpGroupName + " could not be found." );
        }

        logger.info( "Deleting LACP group " + lacpGroupName + " on switch " + switchNode.getSwitchId() );

        CumulusTorSwitchSession session = (CumulusTorSwitchSession) CumulusUtil.getSession( switchNode );
        Configuration configuration = null;

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            session.download( baos, CumulusConstants.INTERFACES_FILE );
            configuration = Configuration.parse( new ByteArrayInputStream( baos.toByteArray() ) );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while reading or parsing interfaces file", e );
            throw new HmsException( "Exception received while reading or parsing interfaces file", e );
        }

        configuration = deleteLacpGroup( lag, configuration );

        /* Upload the file */
        ByteArrayInputStream bais = new ByteArrayInputStream( configuration.getString().getBytes() );
        CumulusUtil.uploadAsRoot( switchNode, bais, CumulusConstants.INTERFACES_FILE );
        CumulusUtil.configurePersistenceDirectory( switchNode );

        /* Reload interfaces */
        SshExecResult result =
            session.executeEnhanced( CumulusConstants.RELOAD_INTERFACES.replaceAll( "\\{password\\}",
                                                                                    CumulusUtil.qr( switchNode.getPassword() ) ) );
        result.logIfError( logger );

        return true;
    }

    public Configuration deleteLacpGroup( SwitchLacpGroup lag, Configuration configuration )
        throws HmsOobNetworkException
    {
        /* Redo all VLANs that have this LACP group as a sub-interface. */
        configuration.substituteInterface( lag.getName(), lag.getPorts() );

        /* Remove the configuration block for the LAG */
        configuration.removeConfigBlock( lag.getName() );

        return configuration;
    }

    public boolean deleteSwitchPortFromLacpGroup( SwitchNode switchNode, String lacpGroupName, String port )
        throws HmsOobNetworkException
    {
        SwitchLacpGroup lag = null;

        try
        {
            lag = getSwitchLacpGroup( switchNode, lacpGroupName );
        }
        catch ( HmsObjectNotFoundException e )
        {
            return true; // we already in desired state
        }
        catch ( HmsException e )
        {
            throw new HmsOobNetworkException( e.getMessage(), e, HmsOobNetworkErrorCode.GET_OPERATION_FAILED );
        }

        if ( lag == null )
        {
            throw new HmsOobNetworkException( "LACP group " + lacpGroupName + " could not be found.", null,
                                              HmsOobNetworkErrorCode.GET_OPERATION_FAILED );
        }

        logger.info( "Deleting LACP group " + lacpGroupName + " on switch " + switchNode.getSwitchId() );

        CumulusTorSwitchSession session = (CumulusTorSwitchSession) CumulusUtil.getSession( switchNode );
        Configuration configuration = null;

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            session.download( baos, CumulusConstants.INTERFACES_FILE );
            configuration = Configuration.parse( new ByteArrayInputStream( baos.toByteArray() ) );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while reading or parsing interfaces file", e );
            throw new HmsOobNetworkException( "Exception received while reading or parsing interfaces file", e,
                                              HmsOobNetworkErrorCode.DOWNLOAD_FAILED );
        }

        /* Delete the mentioned port its parent LACP bond. */
        deleteSwitchPortFromLacpGroup( lag, Arrays.asList( port ), configuration );

        /* Reload interfaces */
        SshExecResult result;
        try
        {
            /* Upload the file */
            ByteArrayInputStream bais = new ByteArrayInputStream( configuration.getString().getBytes() );
            CumulusUtil.uploadAsRoot( switchNode, bais, CumulusConstants.INTERFACES_FILE );
            CumulusUtil.configurePersistenceDirectory( switchNode );

            result =
                session.executeEnhanced( CumulusConstants.RELOAD_INTERFACES.replaceAll( "\\{password\\}",
                                                                                        CumulusUtil.qr( switchNode.getPassword() ) ) );

            result.logIfError( logger );
        }
        catch ( HmsException e )
        {
            throw new HmsOobNetworkException( e.getMessage(), e, HmsOobNetworkErrorCode.UPLOAD_FAILED );
        }

        return true;
    }

    /**
     * @param bondName
     * @param portList
     * @param configuration
     */
    public void deleteSwitchPortFromLacpGroup( SwitchLacpGroup lag, List<String> portList, Configuration configuration )
        throws HmsOobNetworkException
    {
        Bridge vRackBridge = configuration.bridges.get( 0 );
        Bond parent = null;

        if ( lag.getName() == null || portList == null || portList.isEmpty() )
            return;

        ConfigBlock fromBlock = configuration.getConfigBlock( lag.getName() );
        if ( !( fromBlock instanceof Bond ) )
            return;

        parent = (Bond) fromBlock;

        logger.debug( "Deleting inside parent " + lag.getName() + " the following ports "
            + Configuration.joinCollection( portList, " " ) );

        List<SwitchPort> switchPortList = configuration.getSwitchPortsFromNames( portList );

        /* Substitute interface in VLAN aware bridge configuration(s) */
        if ( vRackBridge != null )
        {
            for ( SwitchPort subif : switchPortList )
            {
                SwitchPort subExists = vRackBridge.getMemberByName( subif.name );
                if ( subExists == null )
                {
                    vRackBridge.members.add( subif );
                    subif.setParentBridge( vRackBridge );
                    subif.vlans = parent.vlans; // the deleted port inherits the VLANs of the parent Bond
                    subif.pvid = parent.pvid;
                }
                /* Delete ports in LAG configuration(s) */
                for ( SwitchPort sp : parent.slaves )
                {
                    if ( sp.name.equals( subif.name ) )
                    {
                        parent.slaves.remove( sp );
                        break;
                    }
                }
            }
        }

        if ( parent.slaves.isEmpty() )
        {
            SwitchPort bond = configuration.bridges.get( 0 ).getMemberByName( lag.getName() );
            if ( bond != null )
            {
                // this check can never fail but lets handle that gracefully
                configuration.bridges.get( 0 ).members.remove( bond );
            }
            else
            {
                logger.error( "The LAG from which ports were being deleted accidentally got deleted during the pcocess. Coding bug." );
            }
            configuration.removeConfigBlock( lag.getName() );
        }
    }

    @SuppressWarnings( "unused" )
    /** Instantiated Cumulus Switch Service object */
    private CumulusTorSwitchService service;

    private static Logger logger = Logger.getLogger( CumulusLacpGroupHelper.class );

}
