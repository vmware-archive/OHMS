/* ********************************************************************************
 * SwitchRestService.java
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
package com.vmware.vrack.hms.rest.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsObjectNotFoundException;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.rest.model.SetNodePassword;
import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchNtpConfig;
import com.vmware.vrack.hms.common.switches.GetSwitchesResponse;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchMclagInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortStatus;
import com.vmware.vrack.hms.common.switches.api.SwitchSnmpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchUpdateInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchUpgradeInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.switches.api.SwitchVxlan;
import com.vmware.vrack.hms.common.switches.model.bulk.PluginSwitchBulkConfig;
import com.vmware.vrack.hms.node.switches.SwitchNetworkConfigurationManager;
import com.vmware.vrack.hms.node.switches.SwitchNodeConnector;

@Path( "/switches" )
public class SwitchRestService
{

    private Logger logger = LoggerFactory.getLogger( SwitchRestService.class );

    private SwitchNodeConnector switchConnector = SwitchNodeConnector.getInstance();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    @Path( "/" )
    public GetSwitchesResponse getSwitches()
    {
        return switchConnector.getSwitchNodes();
    }

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    @Path( "/{switch_id}" )
    public SwitchInfo getSwitch( @PathParam( "switch_id" ) String switch_id )
        throws HMSRestException
    {
        SwitchInfo switchInfo = new SwitchInfo();
        validateSwitchId( switch_id );

        try
        {
            switchInfo = switchConnector.getSwitchInfo( switch_id, true );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while getting switch information", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( switchInfo );
    }

    @PUT
    @Path( "/{switch_id}" )
    @Produces( { MediaType.APPLICATION_JSON } )
    @Consumes( { MediaType.APPLICATION_JSON } )
    public BaseResponse updateSwitch( @PathParam( "switch_id" ) String switch_id, SwitchUpdateInfo switchUpdateInfo )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        logInputData( "Updating switch " + switch_id + " with input", switchUpdateInfo );
        validateSwitchId( switch_id );

        try
        {
            switchConnector.updateSwitchNodeInfo( switch_id, switchUpdateInfo );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while updating switch information", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Update of switch " + switch_id + " completed successfully." );
        return ( response );
    }

    @PUT
    @Path( "/{switch_id}/setpassword" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse setSwitchPassword( @PathParam( "switch_id" ) String switchId, SetNodePassword nodePassword )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        logger.debug( "Attempting to rotate password on switch " + switchId + " for user "
            + nodePassword.getUsername() );
        validateSwitchId( switchId );

        SwitchNode switchNode = switchConnector.getSwitchNode( switchId );
        if ( !( nodePassword.getCurrentPassword().equals( switchNode.getPassword() )
            && nodePassword.getUsername().equals( switchNode.getUsername() ) ) )
        {
            throw new HMSRestException( Status.UNAUTHORIZED.getStatusCode(), "Server Error",
                                        "Unauthorized access to resource" );
        }
        try
        {
            switchConnector.setPassword( switchId, nodePassword ); // Update remote password
            switchNode.setPassword( nodePassword.getNewPassword() ); // Update in-memory password
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while rotating password on switch", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Password rotation on " + switchId + " have finished successfully." );
        return ( response );
    }

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    @Path( "/{switch_id}/ports" )
    public List<String> getSwitchPorts( @PathParam( "switch_id" ) String switch_id )
        throws HMSRestException
    {
        List<String> response = null;
        validateSwitchId( switch_id );

        try
        {
            response = switchConnector.getSwitchPorts( switch_id );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while getting switch ports", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( response );
    }

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    @Path( "/{switch_id}/portsbulk" )
    public List<SwitchPort> getSwitchPortsBulk( @PathParam( "switch_id" ) String switch_id )
        throws HMSRestException
    {
        List<SwitchPort> response = null;
        validateSwitchId( switch_id );

        try
        {
            response = switchConnector.getSwitchPortsBulk( switch_id );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while getting switch ports", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( response );
    }

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    @Path( "/{switch_id}/ports/{port_id}" )
    public SwitchPort getSwitchPort( @PathParam( "switch_id" ) String switch_id,
                                     @PathParam( "port_id" ) String port_id )
        throws HMSRestException
    {
        try
        {
            port_id = URLDecoder.decode( port_id, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e1 )
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e1.getMessage() );
        }
        SwitchPort response = null;
        validateSwitchId( switch_id );
        validatePortId( switch_id, port_id );

        try
        {
            response = switchConnector.getSwitchPort( switch_id, port_id );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while getting switch port", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( response );
    }

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    @Path( "/{switch_id}/lacpgroups" )
    public List<String> getSwitchLacpGroups( @PathParam( "switch_id" ) String switch_id )
        throws HMSRestException
    {
        List<String> response = null;
        validateSwitchId( switch_id );

        try
        {
            response = switchConnector.getSwitchLacpGroups( switch_id );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while getting switch LACP groups", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( response );
    }

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    @Path( "/{switch_id}/lacpgroups/{lacpgroup_name}" )
    public SwitchLacpGroup getSwitchLacpGroup( @PathParam( "switch_id" ) String switchId,
                                               @PathParam( "lacpgroup_name" ) String lacpGroupName )
        throws HMSRestException
    {
        SwitchLacpGroup response = null;
        validateSwitchId( switchId );

        try
        {
            response = switchConnector.getSwitchLacpGroup( switchId, lacpGroupName );
        }
        catch ( HmsObjectNotFoundException e )
        {
            logger.error( "Exception received while fetching LACP Group", e );
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Not Found Error", e.getMessage() );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while fetching LACP Group", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( response );
    }

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    @Path( "/{switchId}/snmp" )
    public SwitchSnmpConfig getSnmp( @PathParam( "switchId" ) String switchId )
        throws HMSRestException
    {
        SwitchSnmpConfig response = null;
        validateSwitchId( switchId );

        try
        {
            response = switchConnector.getSnmp( switchId );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while getting SNMP configuration", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( response );
    }

    @PUT
    @Path( "/{switch_id}/lacpgroups" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse createLacpGroup( @PathParam( "switch_id" ) String switchId, SwitchLacpGroup lacpGroup )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        logInputData( "Creating LACP group on switch " + switchId + " with input", lacpGroup );
        validateSwitchId( switchId );
        validatePortIds( switchId, lacpGroup.getPorts() );

        try
        {
            switchConnector.createLacpGroup( switchId, lacpGroup );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while creating LACP group", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Created LACP group with name " + lacpGroup.getName() + " successfully." );
        return ( response );
    }

    @DELETE
    @Path( "/{switch_id}/lacpgroups/{lacpgroup_name}" )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse deleteLacpGroup( @PathParam( "switch_id" ) String switchId,
                                         @PathParam( "lacpgroup_name" ) String lacpGroupName )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        validateSwitchId( switchId );

        try
        {
            switchConnector.deleteLacpGroup( switchId, lacpGroupName );
        }
        catch ( HmsObjectNotFoundException e )
        {
            logger.error( "Exception received while deleting LACP group", e );
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Not Found Error", e.getMessage() );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while deleting LACP group", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Deleted LACP group with name " + lacpGroupName + " successfully." );
        return ( response );
    }

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    @Path( "/{switch_id}/vlans" )
    public List<String> getSwitchVlans( @PathParam( "switch_id" ) String switch_id )
        throws HMSRestException
    {
        List<String> response = null;
        validateSwitchId( switch_id );

        try
        {
            response = switchConnector.getSwitchVlans( switch_id );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while getting switch VLANs", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( response );
    }

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    @Path( "/{switch_id}/vlansbulk" )
    public List<SwitchVlan> getSwitchVlansBulk( @PathParam( "switch_id" ) String switch_id )
        throws HMSRestException
    {
        List<SwitchVlan> response = null;
        validateSwitchId( switch_id );

        try
        {
            response = switchConnector.getSwitchVlansBulk( switch_id );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while getting switch VLANs", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( response );
    }

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    @Path( "/{switch_id}/vlans/{vlan_name}" )
    public SwitchVlan getSwitchVlan( @PathParam( "switch_id" ) String switchId,
                                     @PathParam( "vlan_name" ) String vlanName )
        throws HMSRestException
    {
        SwitchVlan response = null;
        validateSwitchId( switchId );

        try
        {
            response = switchConnector.getSwitchVlan( switchId, vlanName );
        }
        catch ( HmsObjectNotFoundException e )
        {
            logger.error( "Exception received while fetching VLAN", e );
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Not Found Error", e.getMessage() );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while fetching VLAN", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( response );
    }

    @PUT
    @Path( "/{switch_id}/vlans" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse createVlan( @PathParam( "switch_id" ) String switchId, SwitchVlan vlan )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        logInputData( "Creating VLAN on switch " + switchId + " with input", vlan );
        validateSwitchId( switchId );

        try
        {
            switchConnector.createVlan( switchId, vlan );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while creating VLAN", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Created VLAN with name " + vlan.getName() + " successfully." );
        return ( response );
    }

    @PUT
    @Path( "/{switch_id}/vlans/{vlan_name}" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse updateVlan( @PathParam( "switch_id" ) String switchId,
                                    @PathParam( "vlan_name" ) String vlanName, SwitchVlan vlan )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        logInputData( "Updating VLAN on switch " + switchId + " with input", vlan );
        validateSwitchId( switchId );

        try
        {
            switchConnector.updateVlan( switchId, vlanName, vlan );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while updating VLAN", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Created VLAN with name " + vlan.getName() + " successfully." );
        return ( response );
    }

    @DELETE
    @Path( "/{switch_id}/vlans/{vlan_name}" )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse deleteVlan( @PathParam( "switch_id" ) String switchId,
                                    @PathParam( "vlan_name" ) String vlanName )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        validateSwitchId( switchId );

        try
        {
            switchConnector.deleteVlan( switchId, vlanName );
        }
        catch ( HmsObjectNotFoundException e )
        {
            logger.error( "Exception received while deleting VLAN", e );
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Not Found Error", e.getMessage() );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while deleting VLAN", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Deleted VLAN with name " + vlanName + " successfully." );
        return ( response );
    }

    @GET
    @Path( "/{switch_id}/vxlans" )
    @Produces( { MediaType.APPLICATION_JSON } )
    public List<SwitchVxlan> getSwitchVxlans( @PathParam( "switch_id" ) String switch_id )
        throws HMSRestException
    {
        List<SwitchVxlan> response = null;
        validateSwitchId( switch_id );

        try
        {
            response = switchConnector.getSwitchVxlans( switch_id );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while getting switch VXLANs", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( response );
    }

    @GET
    @Path( "/{switch_id}/vlans/{vlan_name}/vxlans" )
    @Produces( { MediaType.APPLICATION_JSON } )
    public List<SwitchVxlan> getSwitchVxlansMatchingVlan( @PathParam( "switch_id" ) String switch_id,
                                                          @PathParam( "vlan_name" ) String vlanName )
        throws HMSRestException
    {
        List<SwitchVxlan> response = null;
        validateSwitchId( switch_id );

        try
        {
            response = switchConnector.getSwitchVxlansMatchingVlan( switch_id, vlanName );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while getting switch VXLANs", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( response );
    }

    @PUT
    @Path( "/{switch_id}/vlans/{vlan_name}/vxlans" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse createVxlan( @PathParam( "switch_id" ) String switchId, SwitchVxlan vxlan )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        logInputData( "Creating VxLAN on switch " + switchId + " with input", vxlan );
        validateSwitchId( switchId );

        try
        {
            switchConnector.createVxlan( switchId, vxlan );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while creating VXLAN " + vxlan.getName(), e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Created VXLAN with name " + vxlan.getName() + " successfully." );
        return ( response );
    }

    @DELETE
    @Path( "/{switch_id}/vlans/{vlan_name}/vxlans/{vxlan_name}" )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse deleteVxlan( @PathParam( "switch_id" ) String switchId,
                                     @PathParam( "vlan_name" ) String vlanName,
                                     @PathParam( "vxlan_name" ) String vxlanName )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        validateSwitchId( switchId );

        try
        {
            switchConnector.deleteVxlan( switchId, vxlanName, vlanName );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while deleting VXLAN " + vxlanName, e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Deleted VXLAN with name " + vxlanName + " successfully." );
        return ( response );
    }

    @PUT
    @Path( "/{switch_id}/ospf" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse configureOspf( @PathParam( "switch_id" ) String switchId, SwitchOspfConfig ospf )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        logInputData( "Configuring OSPF on switch " + switchId + " with input", ospf );
        validateSwitchId( switchId );

        try
        {
            switchConnector.configureOspf( switchId, ospf );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while configuring OSPF on switch " + switchId, e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Configured OSPF on switch " + switchId + " successfully." );
        return ( response );
    }

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    @Path( "/{switch_id}/ospf" )
    public SwitchOspfConfig getOspf( @PathParam( "switch_id" ) String switch_id )
        throws HMSRestException
    {
        SwitchOspfConfig response = null;
        validateSwitchId( switch_id );

        try
        {
            response = switchConnector.getOspf( switch_id );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while getting switch OSPF", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        return ( response );
    }

    @PUT
    @Path( "/{switch_id}/bgp" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse configureBgp( @PathParam( "switch_id" ) String switchId, SwitchBgpConfig bgp )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        logInputData( "Configuring BGP on switch " + switchId + " with input", bgp );
        validateSwitchId( switchId );

        try
        {
            switchConnector.configureBgp( switchId, bgp );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while configuring BGP on switch " + switchId, e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Configured BGP on switch " + switchId + " successfully." );
        return ( response );
    }

    /** This method is deprecated. Please use PUT /{switch_id}/ports/{port_id} instead. */
    @PUT
    @Deprecated
    @Path( "/{switch_id}/ports/{port_id}/{action : up|down}" )
    @Produces( "application/json" )
    public BaseResponse updateSwitchPort( @PathParam( "switch_id" ) String switchId,
                                          @PathParam( "port_id" ) String portId, @PathParam( "action" ) String action )
        throws HMSRestException
    {

        try
        {
            portId = URLDecoder.decode( portId, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e1 )
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e1.getMessage() );
        }

        BaseResponse response = new BaseResponse();
        validateSwitchId( switchId );
        validatePortId( switchId, portId );

        PortStatus upDown = action.equalsIgnoreCase( "up" ) ? PortStatus.UP : PortStatus.DOWN;
        try
        {
            switchConnector.changeSwitchPortStatus( switchId, portId, upDown );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while updating switch port", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.ACCEPTED.getStatusCode() );
        response.setStatusMessage( "Requested update triggered successfully." );
        return response;
    }

    @PUT
    @Path( "/{switch_id}/ports/{port_id}" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse updateSwitchPort( @PathParam( "switch_id" ) String switchId,
                                          @PathParam( "port_id" ) String portId, SwitchPort portInfo )
        throws HMSRestException
    {

        try
        {
            portId = URLDecoder.decode( portId, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e1 )
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e1.getMessage() );
        }

        BaseResponse response = new BaseResponse();
        logInputData( "Updating port " + portId + " on switch " + switchId + " with input", portInfo );
        validateSwitchId( switchId );
        validatePortId( switchId, portId );

        try
        {
            switchConnector.updateSwitchPort( switchId, portId, portInfo );
        }
        catch ( HmsOobNetworkException e )
        {
            String msg = e.getErrorCode().toString() + ": " + e.getMessage();
            /* Error will be logged from callee */
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", msg );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while updating switch " + switchId + " port " + portId, e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Update of switch " + switchId + " port " + portId + " completed successfully." );
        return response;
    }

    @PUT
    @Path( "/{switch_id}/ports/{port_id}/{isEnabled}" )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse switchPortEnable( @PathParam( "switch_id" ) String switchId,
                                          @PathParam( "port_id" ) String portId,
                                          @PathParam( "isEnabled" ) boolean isEnabled )
        throws HMSRestException
    {

        try
        {
            portId = URLDecoder.decode( portId, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e1 )
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e1.getMessage() );
        }

        String action = isEnabled ? "Enabling" : "Disabling";
        BaseResponse response = new BaseResponse();
        logger.debug( String.format( "%s port %s on switch %s", action, portId, switchId ) );
        validateSwitchId( switchId );
        validatePortId( switchId, portId );

        try
        {
            switchConnector.switchPortEnable( switchId, portId, isEnabled );
        }
        catch ( HmsOobNetworkException e )
        {
            String msg = e.getErrorCode().toString() + ": " + e.getMessage();
            /* Error will be logged from callee */
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", msg );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while " + action + " port " + portId + " on switch " + switchId, e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( action + " port " + portId + " on switch " + switchId + " completed successfully." );
        return response;
    }

    @PUT
    @Path( "/{switch_id}/reboot" )
    @Produces( "application/json" )
    public BaseResponse rebootSwitch( @PathParam( "switch_id" ) String switchId )
        throws HMSRestException
    {

        BaseResponse response = new BaseResponse();
        validateSwitchId( switchId );

        try
        {
            switchConnector.rebootSwitch( switchId );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while rebooting switch", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.ACCEPTED.getStatusCode() );
        response.setStatusMessage( "Requested update triggered successfully." );
        return response;
    }

    @PUT
    @Path( "/{switch_id}/upgrade" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse upgradeSwitch( @PathParam( "switch_id" ) String switchId, SwitchUpgradeInfo upgradeInfo )
        throws HMSRestException
    {

        BaseResponse response = new BaseResponse();
        logInputData( "Upgrading switch " + switchId + " with input", upgradeInfo );
        validateSwitchId( switchId );

        try
        {
            switchConnector.upgradeSwitch( switchId, upgradeInfo );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while upgrading switch", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.ACCEPTED.getStatusCode() );
        response.setStatusMessage( "Requested upgrade triggered successfully." );
        return response;
    }

    @PUT
    @Path( "/{switch_id}/configuration/{config_name}" )
    @Produces( "application/json" )
    public BaseResponse configureSwitchNework( @PathParam( "switch_id" ) String switchId,
                                               @PathParam( "config_name" ) String configName )
        throws HMSRestException
    {

        BaseResponse response = new BaseResponse();
        validateSwitchId( switchId );
        validateConfigName( configName );

        try
        {
            switchConnector.applyNetworkConfiguration( switchId, configName );
        }
        catch ( HmsObjectNotFoundException e )
        {
            logger.error( "Exception received while applying network configuration.", e );
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Not Found Error", e.getMessage() );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while applying network configuration.", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.ACCEPTED.getStatusCode() );
        response.setStatusMessage( "Requested update triggered successfully." );
        return response;
    }

    @PUT
    @Path( "/{switch_id}/mclag" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse configureMclag( @PathParam( "switch_id" ) String switchId, SwitchMclagInfo mclag )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        logInputData( "Configuring MLAG on switch " + switchId + " with input", mclag );
        validateSwitchId( switchId );

        try
        {
            switchConnector.configureMclag( switchId, mclag );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while configuring MC-LAG on switch " + switchId, e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Configured MC-LAG on switch " + switchId + " successfully." );
        return ( response );
    }

    @PUT
    @Path( "/{switch_id}/bulkconfigure" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse applySwitchBulkConfig( @PathParam( "switch_id" ) String switchId,
                                               List<PluginSwitchBulkConfig> configs )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        logInputData( "Applying bulk configuration on switch " + switchId + " with input", configs );
        validateSwitchId( switchId );
        try
        {
            switchConnector.applySwitchBulkConfigs( switchId, configs );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while handling bulk configuration on switch(es)", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Applied bulk configuration on switch " + switchId + " successfully" );
        return ( response );
    }

    @PUT
    @Path( "/ntp" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse configureSwitchNtp( NBSwitchNtpConfig ntpConfig )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        logInputData( "Configuring NTP Server on ToR and Management switches with input", ntpConfig );
        try
        {
            switchConnector.configureSwitchNtp( ntpConfig );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while confifguring NTP Server on switch(es)", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Configured NTP Server on ToR and Management Switches successfully." );
        return ( response );
    }

    @PUT
    @Path( "/{switch_id}/ipv4defaultroute" )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse configureIpv4DefaultRoute( @PathParam( "switch_id" ) String switchId,
                                                   @QueryParam( "port" ) String portId,
                                                   @QueryParam( "gateway" ) String gateway )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        validateSwitchId( switchId );

        logger.debug( "Configuring default IPv4 route with gateway = {} and port = {}", gateway, portId );

        try
        {
            switchConnector.configureIpv4DefaultRoute( switchId, gateway, portId );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while configuring default IPv4 Route on switch {}. Exception : {} ",
                          switchId, e.getMessage() );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Switch Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Configured default IPv4 Route on switch " + switchId + " successfully." );
        return ( response );
    }

    @PUT
    @Path( "/{switch_id}/time" )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse configureSwitchTime( @PathParam( "switch_id" ) String switchId,
                                             @QueryParam( "value" ) long time )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        validateSwitchId( switchId );

        logger.debug( "Configuring current time as {} on switch {}", time, switchId );

        try
        {
            switchConnector.setSwitchTime( switchId, time );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while configuring current time on switch {}. Exception : {} ", switchId,
                          e.getMessage() );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Switch Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Configured current time on switch " + switchId + " successfully." );
        return ( response );
    }

    @DELETE
    @Path( "/{switch_id}/ipv4defaultroute" )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse deleteIpv4DefaultRoute( @PathParam( "switch_id" ) String switchId )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        validateSwitchId( switchId );

        logger.debug( "Deleting default IPv4 route from Switch = {}", switchId );

        try
        {
            switchConnector.deleteIpv4DefaultRoute( switchId );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while deleting default IPv4 Route on switch {}. Exception : {} ",
                          switchId, e.getMessage() );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Switch Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Deleted default IPv4 Route on switch " + switchId + " successfully." );
        return ( response );
    }

    @DELETE
    @Path( "/{switch_id}/vlans/{vlanName}/{portOrBondName}" )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse deletePortOrBondFromVlan( @PathParam( "switch_id" ) String switchId,
                                                  @PathParam( "vlanName" ) String vlanName,
                                                  @PathParam( "portOrBondName" ) String portOrBondName )
        throws HMSRestException
    {

        try
        {
            portOrBondName = URLDecoder.decode( portOrBondName, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e1 )
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e1.getMessage() );
        }

        BaseResponse response = new BaseResponse();
        validateSwitchId( switchId );

        logger.debug( "Deleting port/bond with id {} from VLAN {} on Switch = {}", portOrBondName, vlanName, switchId );

        try
        {
            switchConnector.deletePortOrBondFromVlan( switchId, vlanName, portOrBondName );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while deleting port/bond with id {} from VLAN {} on switch {}. Exception : {} ",
                          portOrBondName, vlanName, switchId, e.getMessage() );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Switch Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( String.format( "Deleted port/bond with id %s from VLAN %s on switch %s successfully",
                                                  portOrBondName, vlanName, switchId ) );
        return ( response );
    }

    @DELETE
    @Path( "/{switch_id}/lacpgroups/{lacpGroupName}/{portName}" )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse deletePortFromLacpGroup( @PathParam( "switch_id" ) String switchId,
                                                 @PathParam( "lacpGroupName" ) String lacpGroupName,
                                                 @PathParam( "portName" ) String portName )
        throws HMSRestException
    {

        try
        {
            portName = URLDecoder.decode( portName, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e1 )
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e1.getMessage() );
        }

        BaseResponse response = new BaseResponse();
        validateSwitchId( switchId );

        logger.debug( "Deleting port with id {} from LACP Group {} on Switch = {}", portName, lacpGroupName, switchId );

        try
        {
            switchConnector.deletePortFromLacpGroup( switchId, lacpGroupName, portName );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while deleting port with id {} from LACP Group {} on switch {}. Exception : {} ",
                          portName, lacpGroupName, switchId, e.getMessage() );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Switch Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( String.format( "Deleted port with id %s from LACP Group %s on switch %s successfully",
                                                  portName, lacpGroupName, switchId ) );
        return ( response );
    }

    @PUT
    @Path( "/{switchId}/snmp" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public BaseResponse configureSnmp( @PathParam( "switchId" ) String switchId, SwitchSnmpConfig config )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        logInputData( "Configuring SNMP on switch " + switchId + " with input", config );
        validateSwitchId( switchId );

        try
        {
            switchConnector.configureSnmp( switchId, config );
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while configuring SNMP", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

        response.setStatusCode( Status.OK.getStatusCode() );
        response.setStatusMessage( "Configured SNMP successfully on Switch " + switchId );
        return ( response );
    }

    /*
     * Internal methods =====================
     */
    private void validateSwitchId( String switchId )
        throws HMSRestException
    {
        if ( !switchConnector.contains( switchId ) )
        {
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Unknown switch id " + switchId );
        }
    }

    private void validatePortId( String switchId, String portId )
        throws HMSRestException
    {
        List<String> portIds = new ArrayList<String>();

        portIds.add( portId );

        validatePortIds( switchId, portIds );
    }

    private void validatePortIds( String switchId, List<String> portIds )
        throws HMSRestException
    {
        String msg = "";

        validateSwitchId( switchId );

        ISwitchService switchService = switchConnector.getSwitchService( switchId );
        SwitchNode switchNode = switchConnector.getSwitchNode( switchId );

        List<String> portNameList = switchService.getSwitchPortList( switchNode );
        for ( String portId : portIds )
        {
            if ( !portNameList.contains( portId ) )
            {
                msg = msg.concat( portId + "," );
            }
        }
        if ( msg.length() > 0 )
        {
            msg = msg.substring( 0, msg.length() - 1 ); // Strip off the last ','
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Unknown port id(s) " + msg + " on switch " + switchId );
        }
    }

    private void validateConfigName( String configName )
        throws HMSRestException
    {
        SwitchNetworkConfigurationManager configManager = new SwitchNetworkConfigurationManager();
        if ( !configManager.isValidConfigName( configName ) )
        {
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Unknown network configuration name " + configName );
        }
    }

    private void logInputData( String prefix, Object input )
    {
        ByteArrayOutputStream inputBaos = new ByteArrayOutputStream();

        try
        {
            if ( input != null )
            {
                objectMapper.writeValue( inputBaos, input );
            }
        }
        catch ( IOException e )
        {
            logger.debug( "Error serializing object to JSON string for debugging purpose only.", e );
        }

        logger.debug( prefix + " " + inputBaos.toString() );
    }
}
