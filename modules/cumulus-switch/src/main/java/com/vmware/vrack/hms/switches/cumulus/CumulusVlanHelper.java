/* ********************************************************************************
 * CumulusVlanHelper.java
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsObjectNotFoundException;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkErrorCode;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchOsInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.util.JsonUtils;
import com.vmware.vrack.hms.common.util.SshExecResult;
import com.vmware.vrack.hms.switches.cumulus.model.Bond;
import com.vmware.vrack.hms.switches.cumulus.model.Bridge;
import com.vmware.vrack.hms.switches.cumulus.model.ConfigBlock;
import com.vmware.vrack.hms.switches.cumulus.model.Configuration;
import com.vmware.vrack.hms.switches.cumulus.model.SwitchPort;
import com.vmware.vrack.hms.switches.cumulus.util.CumulusCache;

/**
 * Provides functionality for vlan apis. This class provides functionality for vlans such as get Vlan and vlan bulk,
 * create vlan, update vlan etc.
 */
public class CumulusVlanHelper
{

    /**
     * Constructor for Vlan helper Constructor for Cumulus vlan helper class
     * 
     * @param service CumulusTorSwitchService object
     */
    public CumulusVlanHelper( CumulusTorSwitchService service )
    {
        this.service = service;
    }

    /**
     * Get the vlans for provided switch node Using an api to get vlan bulk list of vlans, compile all vlan names.
     *
     * @param switchNode object
     * @return list of strings (vlan names)
     */
    public List<String> getSwitchVlans( SwitchNode switchNode )
    {
        List<String> vlanNameList = new ArrayList<String>();
        List<SwitchVlan> vlanList = getSwitchVlansBulk( switchNode );

        for ( SwitchVlan vlan : vlanList )
        {
            vlanNameList.add( vlan.getName() );
        }

        return vlanNameList;
    }

    /**
     * Get a single vlan object Utilizing the vlan name, traverse vlanList associated with the switch node.
     *
     * @param switchNode object
     * @param vlanName name of vlan object to get
     * @return Switch Vlan object for provided vlanName
     * @throws HmsException if VLAN does not exist
     */
    public SwitchVlan getSwitchVlan( SwitchNode switchNode, String vlanName )
        throws HmsException
    {
        List<SwitchVlan> vlanList = getSwitchVlansBulk( switchNode );

        for ( SwitchVlan vlan : vlanList )
        {
            if ( vlan.getName().equals( vlanName ) )
                return vlan;
        }

        throw new HmsObjectNotFoundException( "VLAN " + vlanName + " does not exist" );
    }

    /**
     * Get the vlans bulk output For the provided switch node object, get the respective switchPorts. Traverse through
     * the tagged and untagged vlans, and all compile together.
     *
     * @param switchNode object
     * @return List of all switch vlans (tagged and untagged vlans)
     */
    public List<SwitchVlan> getSwitchVlansBulk( SwitchNode switchNode )
    {
        List<SwitchVlan> vlansList = vlansBulkCache.get( switchNode );
        if ( vlansList != null )
        {
            return vlansList;
        }
        else
        {
            vlansList = new ArrayList<SwitchVlan>();
        }

        Map<String, SwitchVlan> vlansMap = new HashMap<String, SwitchVlan>();

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
            logger.error( "Error in reading/parsing interfaces file on switch " + switchNode.getSwitchId()
                + ". Reason: " + e.getMessage(), e );
            return vlansList;
        }

        if ( configuration.switchPorts != null && !configuration.switchPorts.isEmpty() )
        {
            for ( SwitchPort sp : configuration.switchPorts )
            {
                /* Tagged VLAN */
                if ( sp.vlans != null && !sp.vlans.isEmpty() )
                {
                    if ( sp.getParentBridge() == null )
                        logger.warn( "Interface " + sp.name + " does not have a parent bridge." );

                    logger.debug( "Parsing interface " + sp.name + " belonging to tagged vlan(s) "
                        + Configuration.joinCollection( sp.vlans, " " ) );

                    for ( String v : sp.vlans )
                    {
                        SwitchVlan vlan = vlansMap.get( v );
                        if ( vlan == null )
                        {
                            vlan = new SwitchVlan();
                            vlan.setId( v );
                            vlan.setName( v );
                            vlan.setTaggedPorts( new HashSet<String>() );
                            vlan.setUntaggedPorts( new HashSet<String>() );
                            vlansMap.put( v, vlan );
                        }
                        vlan.getTaggedPorts().add( sp.name );
                    }
                }

                /* Untagged VLAN */
                if ( sp.pvid != null && !sp.pvid.equals( "" ) )
                {
                    logger.debug( "Parsing interface " + sp.name + " belonging to untagged vlan " + sp.pvid );

                    SwitchVlan vlan = vlansMap.get( sp.pvid );
                    if ( vlan == null )
                    {
                        vlan = new SwitchVlan();
                        vlan.setId( sp.pvid );
                        vlan.setName( sp.pvid );
                        vlan.setTaggedPorts( new HashSet<String>() );
                        vlan.setUntaggedPorts( new HashSet<String>() );
                        vlansMap.put( sp.pvid, vlan );
                    }
                    vlan.getUntaggedPorts().add( sp.name );
                }
            }
        }

        /*****************************************************************
         * Switch Virtual Interface handling
         *****************************************************************/
        try
        {
            CumulusSviHelper.getVlansFromConfigurarationIntoMap( vlansMap, configuration );
        }
        catch ( HmsOobNetworkException e )
        {
            /* This is a coding error */
            logger.error( e.getMessage() );
        }

        for ( Map.Entry<String, SwitchVlan> me : vlansMap.entrySet() )
        {
            vlansList.add( me.getValue() );

            if ( me.getValue().getTaggedPorts() != null && !me.getValue().getTaggedPorts().isEmpty() )
                logger.debug( "VLAN id " + me.getKey() + " has tagged sub interfaces "
                    + Configuration.joinCollection( me.getValue().getTaggedPorts(), " " ) );

            if ( me.getValue().getUntaggedPorts() != null && !me.getValue().getUntaggedPorts().isEmpty() )
                logger.debug( "VLAN id " + me.getKey() + " has untagged sub interfaces "
                    + Configuration.joinCollection( me.getValue().getUntaggedPorts(), " " ) );
        }

        vlansBulkCache.set( switchNode, vlansList );
        return vlansList;
    }

    /**
     * Get Switch Vlans Bulk (Pre25) Get all Vlans bulk output. (similar to getSwitchVlansBulk)
     *
     * @param switchNode object
     * @return List of Switch Vlan objects
     */
    @SuppressWarnings( { "unchecked", "unused" } )
    private List<SwitchVlan> getSwitchVlansBulkPre25( SwitchNode switchNode )
    {
        List<SwitchVlan> bridgeList = vlansBulkCache.get( switchNode );
        if ( bridgeList != null )
        {
            return bridgeList;
        }
        else
        {
            bridgeList = new ArrayList<SwitchVlan>();
        }

        CumulusTorSwitchSession switchSession = (CumulusTorSwitchSession) CumulusUtil.getSession( switchNode );
        String command = CumulusConstants.GET_ALL_VLANS_COMMAND;
        SshExecResult result = null;

        try
        {
            result = switchSession.executeEnhanced( command );
            result.logIfError( logger );
        }
        catch ( HmsException e )
        {
            logger.error( "Error received while fetching bridge list on switch " + switchNode.getSwitchId(), e );
            return bridgeList;
        }

        /* Check for empty output */
        if ( result.getStdout() == null || "".equals( new String( result.getStdout() ).trim() ) )
        {
            return bridgeList;
        }

        ObjectMapper objectMapper = JsonUtils.getDefaultMapper();
        List<Map<String, Object>> vlanDetailList;

        try
        {
            vlanDetailList = objectMapper.readValue( result.getStdout(), List.class );
        }
        catch ( IOException e )
        {
            logger.error( "Error received while parsing VLAN output on switch " + switchNode.getSwitchId(), e );
            return bridgeList;
        }

        /* Browse through each VLAN */
        for ( Map<String, Object> vlanDetail : vlanDetailList )
        {
            SwitchVlan vlan = new SwitchVlan();
            Set<String> taggedPorts = new HashSet<String>();
            Set<String> untaggedPorts = new HashSet<String>();
            Map<String, Object> config = (Map<String, Object>) vlanDetail.get( "config" );

            vlan.setName( (String) vlanDetail.get( "name" ) );
            vlan.setIpAddress( (String) config.get( "address" ) );
            vlan.setNetmask( (String) config.get( "netmask" ) );

            if ( config.get( "bridge-ports" ) != null )
            {
                String[] allPorts = ( (String) config.get( "bridge-ports" ) ).split( "\\s+" );
                for ( String p : allPorts )
                {
                    if ( p.contains( "." ) )
                    { // This is a tagged port.
                        String[] tokens = p.split( "\\." );
                        taggedPorts.add( tokens[0] ); /* 1st portion is the real port name */
                        vlan.setId( tokens[1] ); /* 2nd portion is the vlan id */
                    }
                    else
                    {
                        untaggedPorts.add( p );
                    }
                }
            }

            vlan.setTaggedPorts( taggedPorts );
            vlan.setUntaggedPorts( untaggedPorts );
            bridgeList.add( vlan );
        }

        if ( !bridgeList.isEmpty() )
        {
            vlansBulkCache.set( switchNode, bridgeList );
        }

        return bridgeList;
    }

    /**
     * Create Vlan object For provided switch node, create vlan object with provided Vlan object
     *
     * @param switchNode object
     * @param vlan object to be created (can be either null or new to either create a new vlan or update an existing
     *            vlan)
     * @return True if creation of Vlan was successful; False if unsuccessful
     * @throws HmsException Thrown if Cumulus OS version no determined
     */
    public boolean createVlan( SwitchNode switchNode, SwitchVlan vlan )
        throws HmsException
    {
        SwitchOsInfo osInfo = service.getSwitchOsInfo( switchNode );
        String osVersion = osInfo.getOsVersion();

        if ( osVersion == null || "".equals( osVersion.trim() ) )
        {
            throw new HmsException( "Couldn't determine Cumulus OS version number." );
        }

        if ( osVersion.matches( "2\\.[0-4]+.*" ) )
        {
            return createOrUpdateVlanPre25( switchNode, vlan, false );
        }
        else
        {
            return createOrUpdateVlan( switchNode, vlan );
        }
    }

    /**
     * Update vlan object provided Using the provided vlan name, for the switch node - either update or create vlan.
     *
     * @param switchNode object
     * @param vlanName string name for vlan to be updated
     * @param vlan vlan object that needs to be created or needs to be updated
     * @return True if update function was successful; False if unsuccessful
     * @throws HmsException Thrown when Cumulus OS version number could not be determined
     */
    public boolean updateVlan( SwitchNode switchNode, String vlanName, SwitchVlan vlan )
        throws HmsException
    {
        vlan.setName( vlanName );
        SwitchOsInfo osInfo = service.getSwitchOsInfo( switchNode );
        String osVersion = osInfo.getOsVersion();

        if ( osVersion == null || "".equals( osVersion.trim() ) )
        {
            throw new HmsException( "Couldn't determine Cumulus OS version number." );
        }

        if ( osVersion.matches( "2\\.[0-4]+.*" ) )
        {
            return createOrUpdateVlanPre25( switchNode, vlan, true );
        }
        else
        {
            return createOrUpdateVlan( switchNode, vlan );
        }
    }

    /**
     * Creates or Updates vlan object. For the provided switch node, upload the configuration interfaces file , and
     * activate vlan in the current session.
     *
     * @param switchNode object
     * @param vlan switch vlan object to be updated
     * @return True if create/update of a vlan was successful; False if create/update was unsuccessful
     * @throws HmsException Thrown when VLAN has empty/null id or did not have any ports
     */
    /* CL 2.5 and beyond implementation of Create VLAN */
    private boolean createOrUpdateVlan( SwitchNode switchNode, SwitchVlan vlan )
        throws HmsException
    {
        if ( vlan.getId() == null || vlan.getId().trim().equals( "" ) )
        {
            throw new HmsException( "Cannot create/update VLAN with empty/null id." );
        }
        else
        {
            try
            {
                int id = Integer.parseInt( vlan.getId().trim() );
                if ( id <= 0 )
                    throw new HmsException( "Cannot create VLAN with a non-positive VLAN id " + vlan.getId() );
            }
            catch ( NumberFormatException nfe )
            {
                throw new HmsException( "Cannot create VLAN with a non-numeric VLAN id " + vlan.getId() );
            }
        }

        if ( vlan.getMtu() != null && vlan.getMtu() <= 0 )
        {
            throw new HmsException( "Cannot create or update VLAN with a non-positive MTU value " + vlan.getMtu() );
        }

        if ( ( vlan.getTaggedPorts() == null || vlan.getTaggedPorts().size() == 0 )
            && ( vlan.getUntaggedPorts() == null || vlan.getUntaggedPorts().size() == 0 )
            && ( vlan.getIpAddress() == null ) )
        {
            throw new HmsException( "Cannot create/update VLAN without any ports and no IP address." );
        }

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
            logger.error( "Error in reading/parsing interfaces file on switch " + switchNode.getSwitchId()
                + ". Reason: " + e.getMessage(), e );
            return false;
        }

        configuration = updateVlanConfiguration( vlan, configuration );
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

        vlansBulkCache.setStale( switchNode );

        return true;
    }

    /**
     * Update LAG on switch ports For provided switch node, confirm the lacp group is not empty (port list), and upload
     * interfaces file and activate vlan in currennt session.
     *
     * @param switchNode object
     * @param lacpGroup object used for the update
     * @return True if update is successful; False if update is unsuccessful
     * @throws HmsException if LACP group name is null or empty
     */
    public boolean updateLAGOnSwitchPorts( SwitchNode switchNode, SwitchLacpGroup lacpGroup )
        throws HmsException
    {
        /* Validate the input */
        if ( lacpGroup == null || lacpGroup.getName() == null || lacpGroup.getName().trim().equals( "" ) )
        {
            throw new HmsException( "Cannot create LACP group with empty/null name." );
        }

        /* Validate the port list */
        if ( lacpGroup.getPorts() == null || lacpGroup.getPorts().isEmpty() )
        {
            throw new HmsException( "Cannot create LACP group with empty port list." );
        }

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
            logger.error( "Exception received while parsing interfaces file.", e );
        }

        try
        {
            configuration = updateLAGOnSwitchPorts( lacpGroup, configuration );
        }
        catch ( Exception e )
        {
            String errMsg = String.format( "Error on Switch %s : %s", switchNode.getSwitchId(), e.getMessage() );
            throw new HmsOobNetworkException( errMsg, e, HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR );
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

        return true;
    }

    /**
     * Protected class - update LAG on switch ports Utilizing the provided lacp group object, traverse through the
     * switch ports and confirm instance exist. If not exists create the switchport (bond) with the new details.These
     * details are stored in configuration object returned.
     *
     * @param lacpGroup object to check the lacpgroups exist
     * @param configuration Used to help maintain parsed details on configuration of a switch
     * @return Configuration object.
     * @throws HmsException if Update on the LAG on switch ports fails
     */
    protected Configuration updateLAGOnSwitchPorts( SwitchLacpGroup lacpGroup, Configuration configuration )
        throws HmsException
    {
        List<String> portNames = lacpGroup.getPorts();
        List<SwitchPort> switchPorts = configuration.convertToSwitchPorts( portNames );
        Bond bond = null;
        String ipAddress = lacpGroup.getIpAddress();

        /*
         * Make sure that the ports mentioned are already not part of any existing bonds
         */
        for ( SwitchPort switchPort : configuration.switchPorts )
        {
            if ( switchPort instanceof Bond )
            {
                if ( !switchPort.name.equals( lacpGroup.getName() ) )
                {
                    Bond tmpBond = (Bond) switchPort;
                    for ( String port : lacpGroup.getPorts() )
                    {
                        if ( tmpBond.containsPortName( port ) )
                        {
                            String errMsg =
                                String.format( "Port %s already is part of LACP bond %s", port, tmpBond.name );
                            logger.error( errMsg );
                            throw new HmsException( errMsg );
                        }
                    }
                }
                else
                {
                    bond = (Bond) switchPort; // we have found the matching LACP bond already. This is crucial if the
                                              // new set of
                                              // ports does not contain any of the existing ports which were already
                                              // part of
                                              // this LACP bond
                }
            }

        }

        /*
         * At this point we are sure that 'switchPorts' contains either new ports to be added or existing ports which
         * are returned as this bond itself. Lets create a new bond if no existing bond is found for this LACP group
         */

        if ( bond == null )
        {

            bond = new Bond();
            bond.name = lacpGroup.getName();
            bond.slaves = new ArrayList<>();
            configuration.addConfigBlock( bond );
        }

        /*
         * Update MTU & IP Address if so requested
         */
        if ( lacpGroup.getMtu() != null )
            bond.setMtu( lacpGroup.getMtu() );
        if ( !( ipAddress == null || ipAddress.isEmpty() ) )
            bond.setIpAddr( ipAddress );

        /*
         * Now add the bond slaves to this bond
         */
        for ( SwitchPort switchPort : switchPorts )
        {
            if ( switchPort instanceof Bond )
            {
                // When forcing bond, need to turn off the "lacp bypass".
                if ( bond.isAllowLacpBypass() )
                    bond.setAllowLacpBypass( false );
                continue;
            }
            else
            {

                if ( bond.pvid == null )
                    bond.pvid = switchPort.pvid;
                if ( bond.vlans == null )
                    bond.vlans = switchPort.vlans;
                if ( bond.portConfig == null )
                    bond.portConfig = switchPort.portConfig;
                if ( bond.otherConfig == null )
                    bond.otherConfig = switchPort.otherConfig;
                if ( bond.getParentBridge() == null )
                    bond.setParentBridge( switchPort.getParentBridge() );

                bond.slaves.add( switchPort );

                if ( switchPort.getParentBridge() != null )
                {
                    switchPort.getParentBridge().members.remove( switchPort );
                }

                if ( bond.getParentBridge() != null && !bond.getParentBridge().members.contains( bond ) )
                {
                    bond.getParentBridge().members.add( bond );
                }

                configuration.switchPorts.remove( switchPort );
            }
        }

        return configuration;
    }

    /**
     * Update vlan configuration details Using the tagged ports for the vlan, convert to Switch Ports. Using the
     * converted switch ports, add details for the provided vlan object if the details exist.
     *
     * @param vlan object that contains the details for the tagged/untagged ports
     * @param configuration Configuration details for how VLAN is setup
     * @return Configuration details of the updated vlan
     */
    protected Configuration updateVlanConfiguration( SwitchVlan vlan, Configuration configuration )
        throws HmsOobNetworkException
    {
        Bridge vRackBridge = configuration.bridges.get( 0 );

        List<SwitchPort> taggedPorts = configuration.convertToSwitchPorts( vlan.getTaggedPorts() );

        // TODO: Untagged ports !!!
        // List<SwitchPort> untaggedPorts = configuration.convertToSwitchPorts(vlan.getUntaggedPorts());

        if ( vRackBridge.vlans == null )
            vRackBridge.vlans = new ArrayList<>();

        if ( !vRackBridge.vlans.contains( vlan.getId() ) )
            vRackBridge.vlans.add( vlan.getId() );
        if ( taggedPorts != null )
        {
            for ( SwitchPort taggedPort : taggedPorts )
            {
                if ( taggedPort.vlans == null )
                    taggedPort.vlans = new ArrayList<>();

                if ( !taggedPort.vlans.contains( vlan.getId() ) )
                    taggedPort.vlans.add( vlan.getId() );

                if ( configuration.getConfigBlock( taggedPort.name ) == null )
                    configuration.switchPorts.add( taggedPort );

                taggedPort.setParentBridge( vRackBridge );

                if ( !vRackBridge.members.contains( taggedPort ) )
                    vRackBridge.members.add( taggedPort );
            }
        }

        /*****************************************************************
         * Switch Virtual Interface handling
         *****************************************************************/
        CumulusSviHelper.updateVlanInConfiguration( vRackBridge.name, vlan, configuration );

        return configuration;
    }

    public boolean deletePortFromVlanConfiguration( SwitchNode switchNode, String vlanId, String port )
        throws HmsOobNetworkException
    {
        SwitchVlan fromVlan;

        /* Validate the input just against NULL values */
        if ( vlanId == null || port == null )
        {
            throw new HmsOobNetworkException( "Cannot work with NULL port or VLAN name.",
                                              HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR );
        }

        // check if the VLAN exists or not
        try
        {
            fromVlan = getSwitchVlan( switchNode, vlanId );
        }
        catch ( HmsException e )
        {
            throw new HmsOobNetworkException( e.getMessage(), e, HmsOobNetworkErrorCode.GET_OPERATION_FAILED );
        }

        if ( fromVlan == null )
            throw new HmsOobNetworkException( String.format( "The VLAN Id %s does not exist on Switch %s", vlanId,
                                                             switchNode.getSwitchId() ),
                                              HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR );

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
            logger.error( "Exception received while parsing interfaces file.", e );
        }

        // do the real stuff now
        configuration = deletePortFromVlanConfiguration( fromVlan, configuration, port );

        if ( configuration == null )
        {
            throw new HmsOobNetworkException( "Not able to delete port/bond from the mentioned VLAN",
                                              HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR );
        }

        /* Upload the file */
        ByteArrayInputStream bais = new ByteArrayInputStream( configuration.getString().getBytes() );
        try
        {
            CumulusUtil.validateSourceClause( switchNode );
            CumulusUtil.uploadAsRoot( switchNode, bais, CumulusConstants.INTERFACES_FILE );

            CumulusUtil.configurePersistenceDirectory( switchNode );

            /* Activate the VLAN in the current session */
            SshExecResult result =
                session.executeEnhanced( CumulusConstants.RELOAD_INTERFACES.replaceAll( "\\{password\\}",
                                                                                        CumulusUtil.qr( switchNode.getPassword() ) ) );

            result.logIfError( logger );

            vlansBulkCache.setStale( switchNode );
        }
        catch ( HmsException e )
        {
            throw new HmsOobNetworkException( e.getMessage(), e, HmsOobNetworkErrorCode.UPLOAD_FAILED );
        }

        return true;
    }

    /**
     * Removes an existing port from a VLAN configuration only if the port is a tagged port with the said VLAN
     * 
     * @param existingVlan
     * @param configuration
     * @param port
     * @return
     * @throws HmsOobNetworkException
     */
    protected Configuration deletePortFromVlanConfiguration( SwitchVlan existingVlan, Configuration configuration,
                                                             String port )
        throws HmsOobNetworkException
    {
        Bridge vRackBridge = configuration.bridges.get( 0 );

        List<SwitchPort> taggedPorts = configuration.convertToSwitchPorts( existingVlan.getTaggedPorts() );

        if ( vRackBridge.vlans == null )
            return configuration; // nothing to do

        for ( SwitchPort taggedPort : taggedPorts )
        {
            if ( taggedPort.name.equals( port ) && taggedPort.vlans != null
                && taggedPort.vlans.contains( existingVlan.getId() ) )
            {
                taggedPort.vlans.remove( existingVlan.getId() );

                if ( existingVlan.getTaggedPorts().contains( port ) && existingVlan.getTaggedPorts().size() == 1 )
                {
                    // this was the only port so we can delete the entire vlan now
                    try
                    {
                        deleteVlan( existingVlan, configuration );
                    }
                    catch ( HmsException e )
                    {
                        throw new HmsOobNetworkException( e.getMessage(), e,
                                                          HmsOobNetworkErrorCode.DELETE_OPERATION_FAILED );
                    }
                }
            }
        }

        return configuration;
    }

    /**
     * Create or Update vlan Similar to createOrUpdateVlan, using provided switch node - confirm if vlan exists. Delete
     * existing vlan, and create new with merging details provided by the parameter vlan. Process the tagged and
     * untagged ports, and activate the updated vlan in the current state.
     * 
     * @param switchNode object
     * @param vlan details of the new updated values of the vlan
     * @param update determine if the vlan has to be created or updated.
     * @return True if the creation/update was successful on Vlan (Pre25); False if creation/update was unsuccessful
     * @throws HmsException if Vlan name was null or empty and Vlan had no ports
     */
    private boolean createOrUpdateVlanPre25( SwitchNode switchNode, SwitchVlan vlan, boolean update )
        throws HmsException
    {
        // Validate name, id, and ports
        if ( vlan.getName() == null || vlan.getName().trim().equals( "" ) )
        {
            throw new HmsException( "Cannot create VLAN with empty/null name." );
        }

        if ( !update )
        {
            if ( vlan.getId() == null || vlan.getId().trim().equals( "" ) )
            {
                throw new HmsException( "Cannot create VLAN with empty/null id." );
            }
        }

        if ( ( vlan.getTaggedPorts() == null || vlan.getTaggedPorts().size() == 0 )
            && ( vlan.getUntaggedPorts() == null || vlan.getUntaggedPorts().size() == 0 ) )
        {
            throw new HmsException( "Cannot create VLAN without any ports." );
        }

        // Just a sanity check to make sure the same port is not provided as untagged and tagged port lists
        if ( vlan.getTaggedPorts() != null && vlan.getTaggedPorts().size() > 0 && vlan.getUntaggedPorts() != null
            && vlan.getUntaggedPorts().size() > 0 )
        {
            for ( String tp : vlan.getTaggedPorts() )
            {
                if ( vlan.getUntaggedPorts().contains( tp ) )
                {
                    throw new HmsException( "Port " + tp + " cannot be present in both tagged and untagged lists" );
                }
            }
        }

        /* Check if VLAN already exists */
        String vlanName = vlan.getName().trim();
        SwitchVlan existingVlan = null;

        try
        {
            existingVlan = getSwitchVlan( switchNode, vlanName );
        }
        catch ( HmsObjectNotFoundException nfe )
        {
            if ( update )
            {
                throw new HmsObjectNotFoundException( "Requested VLAN " + vlanName
                    + " cannot be updated because it does not exist." );
            }
        }

        /* Delete existing VLAN configuration first */
        if ( !update && existingVlan != null )
        {
            deleteVlan( switchNode, vlanName );
        }

        /* Merge the settings */
        if ( update )
        {
            if ( vlan.getId() == null )
                vlan.setId( existingVlan.getId() );
            if ( vlan.getIpAddress() == null )
                vlan.setIpAddress( existingVlan.getIpAddress() );
            if ( vlan.getNetmask() == null )
                vlan.setNetmask( existingVlan.getNetmask() );

            Set<String> taggedPorts = vlan.getTaggedPorts() == null ? new HashSet<String>() : vlan.getTaggedPorts();
            taggedPorts.addAll( existingVlan.getTaggedPorts() );
            vlan.setTaggedPorts( taggedPorts );

            Set<String> untaggedPorts =
                vlan.getUntaggedPorts() == null ? new HashSet<String>() : vlan.getUntaggedPorts();
            untaggedPorts.addAll( existingVlan.getUntaggedPorts() );
            vlan.setUntaggedPorts( untaggedPorts );
        }

        String finalAssociatedPorts = "";
        StringBuilder fileContents = new StringBuilder();
        String vlanId = vlan.getId().trim();

        /* Process tagged ports */
        if ( vlan.getTaggedPorts() != null )
        {
            for ( String p : vlan.getTaggedPorts() )
            {
                String tpName = p + "." + vlanId;
                finalAssociatedPorts += tpName + " ";

                String stanza =
                    CumulusConstants.VLAN_SUBINTERFACE_STANZA.replaceAll( "\\{subifname\\}", p ).replaceAll( "\\{id\\}",
                                                                                                             vlanId );

                fileContents.append( stanza );
            }
        }

        /* Process untagged ports */
        if ( vlan.getUntaggedPorts() != null )
        {
            for ( String up : vlan.getUntaggedPorts() )
            {
                finalAssociatedPorts += up + " ";
            }
        }

        String mode = "manual";
        String ipStanza = "";
        if ( vlan.getIpAddress() != null && !vlan.getIpAddress().trim().equals( "" ) )
        {
            mode = "static";
            ipStanza = CumulusConstants.IPV4_LINE.replaceAll( "\\{address\\}",
                                                              vlan.getIpAddress() ).replaceAll( "\\{netmask\\}",
                                                                                                vlan.getNetmask() );
        }

        /* Add VLAN stanza */
        String vlanStanza =
            CumulusConstants.VLAN_STANZA.replaceAll( "\\{name\\}",
                                                     vlanName ).replaceAll( "\\{interfaces\\}",
                                                                            finalAssociatedPorts.trim() ).replaceAll( "\\{mode\\}",
                                                                                                                      mode ).replaceAll( "\\{ipv4\\}",
                                                                                                                                         ipStanza ).replaceAll( "\\{stp\\}",
                                                                                                                                                                "" ).replaceAll( "\\{vrr\\}",
                                                                                                                                                                                 "" );

        fileContents.append( vlanStanza );

        /* Upload the file */
        ByteArrayInputStream bais = new ByteArrayInputStream( fileContents.toString().getBytes() );
        String filename = CumulusUtil.getVlanFilename( vlanName );
        CumulusUtil.validateSourceClause( switchNode );
        CumulusUtil.uploadAsRoot( switchNode, bais, filename );
        CumulusUtil.configurePersistenceDirectory( switchNode );

        /* Activate the VLAN in the current session */
        String command =
            CumulusConstants.IFUP_COMMAND.replaceAll( "\\{password\\}",
                                                      CumulusUtil.qr( switchNode.getPassword() ) ).replaceAll( "\\{interfaces\\}",
                                                                                                               vlanName );

        CumulusTorSwitchSession session = (CumulusTorSwitchSession) CumulusUtil.getSession( switchNode );
        SshExecResult result = session.executeEnhanced( command );
        result.logIfError( logger );

        return true;
    }

    /**
     * Delete vlan on switch node Delete the vlan on provided switch noce, by removing the vlan id from each of the sub
     * interfaces. Remove the bridges between the vlans, upload the modified configuration, and de-activate the vlan in
     * the current session.
     *
     * @param switchNode object
     * @param vlanName name of vlan to delete
     * @return True if deletion of vlan was successful; False if vlan deletion failed
     * @throws HmsException if Vlan could not be found
     */
    public boolean deleteVlan( SwitchNode switchNode, String vlanName )
        throws HmsException
    {
        SwitchVlan vlan = getSwitchVlan( switchNode, vlanName );
        if ( vlan == null )
        {
            throw new HmsObjectNotFoundException( "VLAN " + vlanName + " could not be found." );
        }

        logger.debug( "Deleting VLAN " + vlanName + " on switch " + switchNode.getSwitchId() + " ..." );

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
            logger.error( "Error in reading/parsing interfaces file on switch " + switchNode.getSwitchId()
                + ". Reason: " + e.getMessage(), e );
            return false;
        }

        configuration = deleteVlan( vlan, configuration );

        /* Step 3. Upload the modified configuration */
        ByteArrayInputStream bais = new ByteArrayInputStream( configuration.getString().getBytes() );
        CumulusUtil.validateSourceClause( switchNode );
        CumulusUtil.uploadAsRoot( switchNode, bais, CumulusConstants.INTERFACES_FILE );
        CumulusUtil.configurePersistenceDirectory( switchNode );

        vlansBulkCache.setStale( switchNode );

        /* Step 4. De-activate the VLAN in the current session */
        SshExecResult result =
            session.executeEnhanced( CumulusConstants.RELOAD_INTERFACES.replaceAll( "\\{password\\}",
                                                                                    CumulusUtil.qr( switchNode.getPassword() ) ) );
        result.logIfError( logger );

        return true;
    }

    /**
     * @param vlan
     * @param configuration
     * @return
     * @throws HmsException
     */
    public Configuration deleteVlan( SwitchVlan vlan, Configuration configuration )
        throws HmsException
    {
        if ( vlan == null )
        {
            throw new HmsObjectNotFoundException( "VLAN cannot be NULL." );
        }

        /* Step 1. Remove the VLAN id from each sub interfaces' bridge-vids list */
        Set<ConfigBlock> toBeRemoved = new HashSet<ConfigBlock>();
        if ( vlan.getTaggedPorts() != null && !vlan.getTaggedPorts().isEmpty() )
        {
            for ( String portName : vlan.getTaggedPorts() )
            {
                ConfigBlock portBlock = configuration.getConfigBlock( portName );
                if ( portBlock.vlans != null && !portBlock.vlans.isEmpty() )
                {
                    portBlock.vlans.remove( vlan.getId() );
                    if ( portBlock.vlans.isEmpty() )
                    {
                        toBeRemoved.add( portBlock );
                        if ( portBlock instanceof SwitchPort )
                        {
                            ( (SwitchPort) portBlock ).setParentBridge( null );
                        }
                    }
                }
            }
        }

        /*****************************************************************
         * Switch Virtual Interface handling
         *****************************************************************/
        CumulusSviHelper.deleteVlanInConfiguration( vlan, configuration );

        // TODO: Repeat for untagged ports

        /* Step 2. Remove the VLAN id from bridge's bridge-vids list */
        if ( configuration.bridges == null || configuration.bridges.isEmpty() )
            return configuration;

        Bridge vRackBridge = configuration.bridges.get( 0 );
        if ( vRackBridge.vlans != null && !vRackBridge.vlans.isEmpty() )
        {
            vRackBridge.vlans.remove( vlan.getId() );
        }

        if ( vRackBridge.members != null && !vRackBridge.members.isEmpty() )
        {
            vRackBridge.members.removeAll( toBeRemoved );
        }

        return configuration;
    }

    /**
     * Delete vlan (similar function as deleteVlan) Using the vlan name provided for the switch node, bring all
     * interfaces down for the switch node.
     *
     * @param switchNode object
     * @param vlanName name of vlan to delete
     * @return True if deletion of vlan (pre25) was successful; False if not successful
     * @throws HmsException Vlan was not found
     */
    @SuppressWarnings( "unused" )
    private boolean deleteVlanPre25( SwitchNode switchNode, String vlanName )
        throws HmsException
    {
        SwitchVlan vlan = getSwitchVlan( switchNode, vlanName );
        if ( vlan == null )
        {
            throw new HmsObjectNotFoundException( "VLAN " + vlanName + " could not be found." );
        }

        logger.debug( "Deleting VLAN " + vlanName + " on switch " + switchNode.getSwitchId() + " ..." );

        /* Bring all interfaces down */
        String command =
            CumulusConstants.DELETE_VLAN_COMMAND.replaceAll( "\\{password\\}",
                                                             CumulusUtil.qr( switchNode.getPassword() ) ).replaceAll( "\\{vlan\\}",
                                                                                                                      vlanName ).replaceAll( "\\{filename\\}",
                                                                                                                                             CumulusUtil.getVlanFilename( vlanName ) );

        CumulusTorSwitchSession session = (CumulusTorSwitchSession) CumulusUtil.getSession( switchNode );
        SshExecResult result = session.executeEnhanced( command );
        result.logIfError( logger );

        CumulusUtil.configurePersistenceDirectory( switchNode );
        return true;

    }

    /** Variable used to represent the Cumulus Switch session */
    private CumulusTorSwitchService service;

    private static Logger logger = Logger.getLogger( CumulusVlanHelper.class );

    private CumulusCache<List<SwitchVlan>> vlansBulkCache = new CumulusCache<List<SwitchVlan>>( 300 );

}
