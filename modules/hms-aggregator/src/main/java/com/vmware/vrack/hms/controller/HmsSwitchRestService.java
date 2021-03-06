/* ********************************************************************************
 * HmsSwitchRestService.java
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
package com.vmware.vrack.hms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmware.vrack.hms.aggregator.switches.HmsSwitchManager;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchBgpConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchLagConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchMcLagConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchOspfv2Config;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchPortConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchPortInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchSnmpConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchVlanConfig;
import com.vmware.vrack.hms.common.rest.model.switches.bulk.NBSwitchBulkConfig;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.inventory.SwitchDataChangeMessage;

@Controller
@RequestMapping( "/napi/switches" )
public class HmsSwitchRestService
{

    @Autowired
    private HmsSwitchManager hmsSwitchManager;

    @Autowired
    private ApplicationContext context;

    @RequestMapping( value = { "", "/" }, method = RequestMethod.GET )
    @ResponseBody
    public List<NBSwitchInfo> getAllSwitchInfos()
        throws HMSRestException
    {
        return hmsSwitchManager.getAllSwitchInfos();
    }

    @RequestMapping( value = "/{switch_id}", method = RequestMethod.GET )
    @ResponseBody
    public NBSwitchInfo getSwitchInfo( @PathVariable String switch_id )
        throws HMSRestException
    {
        NBSwitchInfo nbSwitchInfo = hmsSwitchManager.getSwitchInfo( switch_id );
        context.publishEvent( new SwitchDataChangeMessage( nbSwitchInfo, SwitchComponentEnum.SWITCH ) );
        return nbSwitchInfo;
    }

    @RequestMapping( value = "/{switch_id}/bgp", method = RequestMethod.GET )
    @ResponseBody
    public NBSwitchBgpConfig getSwitchBgpConfig( @PathVariable String switch_id )
        throws HMSRestException
    {
        return hmsSwitchManager.getSwitchBgpConfig( switch_id );
    }

    @RequestMapping( value = "/{switch_id}/snmp", method = RequestMethod.GET )
    @ResponseBody
    public NBSwitchSnmpConfig getSwitchSnmpConfig( @PathVariable String switch_id )
        throws HMSRestException
    {
        return hmsSwitchManager.getSwitchSnmpConfig( switch_id );
    }

    @RequestMapping( value = "/{switch_id}/bgp", method = RequestMethod.DELETE )
    @ResponseBody
    public BaseResponse deleteSwitchBgpConfig( @PathVariable String switch_id )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.deleteSwitchBgpConfig( switch_id );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/snmp", method = RequestMethod.DELETE )
    @ResponseBody
    public BaseResponse disableSwitchSnmp( @PathVariable String switch_id )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.disableSwitchSnmp( switch_id );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/bgp", method = RequestMethod.PUT )
    @ResponseBody
    public BaseResponse createOrUpdateSwitchBgpConfig( @PathVariable String switch_id,
                                                       @RequestBody( required = false ) NBSwitchBgpConfig config )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.createOrUpdateSwitchBgpConfig( switch_id, config );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/snmp", method = RequestMethod.PUT )
    @ResponseBody
    public BaseResponse configureSwitchSnmp( @PathVariable String switch_id,
                                             @RequestBody( required = false ) NBSwitchSnmpConfig config )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.configureSnmp( switch_id, config );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/ospfv2", method = RequestMethod.GET )
    @ResponseBody
    public NBSwitchOspfv2Config getSwitchOspfv2Config( @PathVariable String switch_id )
        throws HMSRestException
    {
        return hmsSwitchManager.getSwitchOspfv2Config( switch_id );
    }

    @RequestMapping( value = "/{switch_id}/ospfv2", method = RequestMethod.DELETE )
    @ResponseBody
    public BaseResponse deleteSwitchOspfv2Config( @PathVariable String switch_id )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.deleteSwitchOspfv2Config( switch_id );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/ospfv2", method = RequestMethod.PUT )
    @ResponseBody
    public BaseResponse createOrUpdateSwitchOspfv2Config( @PathVariable String switch_id,
                                                          @RequestBody( required = false ) NBSwitchOspfv2Config config )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.createOrUpdateSwitchOspfv2Config( switch_id, config );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/mclag", method = RequestMethod.GET )
    @ResponseBody
    public NBSwitchMcLagConfig getSwitchMcLagConfig( @PathVariable String switch_id )
        throws HMSRestException
    {
        return hmsSwitchManager.getSwitchMcLagConfig( switch_id );
    }

    @RequestMapping( value = "/{switch_id}/mclag", method = RequestMethod.DELETE )
    @ResponseBody
    public BaseResponse deleteSwitchMcLagConfig( @PathVariable String switch_id )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.deleteSwitchMcLagConfig( switch_id );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/mclag", method = RequestMethod.PUT )
    @ResponseBody
    public BaseResponse createOrUpdateSwitchMcLagConfig( @PathVariable String switch_id,
                                                         @RequestBody( required = false ) NBSwitchMcLagConfig config )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.createOrUpdateSwitchMcLagConfig( switch_id, config );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/lags/{lag_id}", method = RequestMethod.GET )
    @ResponseBody
    public NBSwitchLagConfig getSwitchLagConfig( @PathVariable String switch_id, @PathVariable String lag_id )
        throws HMSRestException
    {
        return hmsSwitchManager.getSwitchLagConfig( switch_id, lag_id );
    }

    @RequestMapping( value = "/{switch_id}/lags/{lag_id}", method = RequestMethod.DELETE )
    @ResponseBody
    public BaseResponse deleteSwitchLagConfig( @PathVariable String switch_id, @PathVariable String lag_id )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.deleteSwitchLagConfig( switch_id, lag_id );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/lags", method = RequestMethod.PUT )
    @ResponseBody
    public BaseResponse createOrUpdateSwitchLagConfig( @PathVariable String switch_id,
                                                       @RequestBody( required = false ) NBSwitchLagConfig config )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.createOrUpdateSwitchLagConfig( switch_id, config );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/lags", method = RequestMethod.GET )
    @ResponseBody
    public List<NBSwitchLagConfig> getSwitchAllLagsConfigs( @PathVariable String switch_id )
        throws HMSRestException
    {
        return hmsSwitchManager.getSwitchAllLagsConfigs( switch_id );
    }

    @RequestMapping( value = "/{switch_id}/vlans/{vlan_id}", method = RequestMethod.GET )
    @ResponseBody
    public NBSwitchVlanConfig getSwitchVlanConfig( @PathVariable String switch_id, @PathVariable String vlan_id )
        throws HMSRestException
    {
        return hmsSwitchManager.getSwitchVlanConfig( switch_id, vlan_id );
    }

    @RequestMapping( value = "/{switch_id}/vlans/{vlan_id}", method = RequestMethod.DELETE )
    @ResponseBody
    public BaseResponse deleteSwitchVlanConfig( @PathVariable String switch_id, @PathVariable String vlan_id )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.deleteSwitchVlanConfig( switch_id, vlan_id );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/vlans", method = RequestMethod.PUT )
    @ResponseBody
    public BaseResponse createOrUpdateSwitchVlanConfig( @PathVariable String switch_id,
                                                        @RequestBody( required = false ) NBSwitchVlanConfig config )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.createOrUpdateSwitchVlanConfig( switch_id, config );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/vlans", method = RequestMethod.GET )
    @ResponseBody
    public List<NBSwitchVlanConfig> getSwitchAllVlansConfigs( @PathVariable String switch_id )
        throws HMSRestException
    {
        return hmsSwitchManager.getSwitchAllVlansConfigs( switch_id );
    }

    @RequestMapping( value = "/{switch_id}/ports/{port_id}", method = RequestMethod.GET )
    @ResponseBody
    public NBSwitchPortInfo getSwitchPortInfo( @PathVariable String switch_id, @PathVariable String port_id )
        throws HMSRestException
    {
        return hmsSwitchManager.getSwitchPortInfo( switch_id, port_id );
    }

    @RequestMapping( value = "/{switch_id}/ports/{port_id}", method = RequestMethod.PUT )
    @ResponseBody
    public BaseResponse updateSwitchPortConfig( @PathVariable String switch_id, @PathVariable String port_id,
                                                @RequestBody( required = false ) NBSwitchPortConfig config )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.updateSwitchPortConfig( switch_id, port_id, config );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/ports/{port_id}/{isEnabled}", method = RequestMethod.PUT )
    @ResponseBody
    public BaseResponse switchPortEnable( @PathVariable String switch_id, @PathVariable String port_id,
                                          @PathVariable boolean isEnabled )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.switchPortEnable( switch_id, port_id, isEnabled );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/ports", method = RequestMethod.GET )
    @ResponseBody
    public List<NBSwitchPortInfo> getSwitchAllPortInfos( @PathVariable String switch_id )
        throws HMSRestException
    {
        return hmsSwitchManager.getSwitchAllPortInfos( switch_id );
    }

    @RequestMapping( value = "/{switch_id}/bulkconfigs", method = RequestMethod.PUT )
    @ResponseBody
    public BaseResponse applyBulkConfigs( @PathVariable String switch_id,
                                          @RequestBody( required = false ) List<NBSwitchBulkConfig> configs )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.applyBulkConfigs( switch_id, configs );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/ipv4defaultroute",
                     params = { "gateway", "port" },
                     method = RequestMethod.PUT )
    @ResponseBody
    public BaseResponse configureIpv4DefaultRoute( @PathVariable String switch_id,
                                                   @RequestParam( value = "gateway" ) String gateway,
                                                   @RequestParam( value = "port" ) String port )
        throws HMSRestException
    {
        return hmsSwitchManager.configureIpv4DefaultRoute( switch_id, gateway, port );
    }

    @RequestMapping( value = "/{switch_id}/time", params = { "value" }, method = RequestMethod.PUT )
    @ResponseBody
    public BaseResponse configureSwitchTime( @PathVariable String switch_id,
                                             @RequestParam( value = "value" ) long time )
        throws HMSRestException
    {
        return hmsSwitchManager.configureSwitchTime( switch_id, time );
    }

    @RequestMapping( value = "/{switch_id}/ipv4defaultroute", method = RequestMethod.DELETE )
    @ResponseBody
    public BaseResponse deleteIpv4DefaultRoute( @PathVariable String switch_id )
        throws HMSRestException
    {
        return hmsSwitchManager.deleteIpv4DefaultRoute( switch_id );
    }

    @RequestMapping( value = "/{switch_id}/vlans/{vlan_id}/{port_or_bond_id}", method = RequestMethod.DELETE )
    @ResponseBody
    public BaseResponse deletePortOrBondFromVlan( @PathVariable String switch_id, @PathVariable String vlan_id,
                                                  @PathVariable String port_or_bond_id )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.deletePortOrBondFromVlan( switch_id, vlan_id, port_or_bond_id );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

    @RequestMapping( value = "/{switch_id}/lags/{lag_id}/{port_id}", method = RequestMethod.DELETE )
    @ResponseBody
    public BaseResponse deletePortFromLag( @PathVariable String switch_id, @PathVariable String lag_id,
                                           @PathVariable String port_id )
        throws HMSRestException
    {
        BaseResponse response = null;
        response = hmsSwitchManager.deletePortFromLag( switch_id, lag_id, port_id );
        hmsSwitchManager.sendEventToUpdateSwitchCache( response, switch_id );
        return response;
    }

}
