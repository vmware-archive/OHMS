/* ********************************************************************************
 * SwitchNodeConnector.java
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
package com.vmware.vrack.hms.node.switches;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.boardservice.HmsPluginServiceCallWrapper;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentSwitchEventInfoProvider;
import com.vmware.vrack.hms.common.configuration.HmsInventoryConfiguration;
import com.vmware.vrack.hms.common.configuration.SwitchItem;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkErrorCode;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.common.monitoring.MonitorTaskSuite;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskRequestHandler;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.notification.NodeActionStatus;
import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchNtpConfig;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.switches.GetSwitchResponse;
import com.vmware.vrack.hms.common.switches.GetSwitchesResponse;
import com.vmware.vrack.hms.common.switches.GetSwitchesResponse.GetSwitchesResponseItem;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchHardwareInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchMclagInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode.SwitchRoleType;
import com.vmware.vrack.hms.common.switches.api.SwitchOsInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchType;
import com.vmware.vrack.hms.common.switches.api.SwitchUpdateInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchUpgradeInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.switches.api.SwitchVxlan;
import com.vmware.vrack.hms.common.switches.model.bulk.PluginSwitchBulkConfig;
import com.vmware.vrack.hms.common.switchnodes.api.HMSSwitchNode;
import com.vmware.vrack.hms.common.util.SwitchInfoHelperUtil;
import com.vmware.vrack.hms.node.NodeConnector;
import com.vmware.vrack.hms.utils.NodeDiscoveryUtil;

public class SwitchNodeConnector
    extends NodeConnector
{
    private static Logger logger = Logger.getLogger( SwitchNodeConnector.class );

    private static volatile SwitchNodeConnector instance;

    public Map<String, SwitchNode> switchNodeMap = new TreeMap<String, SwitchNode>();

    private SwitchIpv4RouteManager ipv4RouteManager = new SwitchIpv4RouteManager();

    /**
     * Map that will contain all SwitchService classes
     */
    private Map<String, ISwitchService> switchServiceMap = new TreeMap<String, ISwitchService>();

    public static SwitchNodeConnector getInstance()
    {
        if ( instance == null )
        {
            instance = new SwitchNodeConnector();
        }
        return instance;
    }

    private SwitchNodeConnector()
    {
        parseRackInventoryConfig();
        initSwitchMonitoring();
    }

    public GetSwitchesResponse getSwitchNodes()
    {
        GetSwitchesResponse switchesResponse = new GetSwitchesResponse();
        for ( String s : switchNodeMap.keySet() )
        {
            SwitchNode val = switchNodeMap.get( s );
            ISwitchService switchService = switchServiceMap.get( s );
            /*
             * Class<ISwitchService> switchServiceClass = switchServiceClassMap.get(s); ISwitchService switchService =
             * null; if(switchServiceClass != null) { try { switchService =
             * (ISwitchService)switchServiceClass.newInstance(); } catch (InstantiationException e) { logger.error(
             * "Exception during creating new Instance of class:" + switchServiceClass, e); } catch
             * (IllegalAccessException e) { logger.error("Exception during creating new Instance of class:" +
             * switchServiceClass, e); } } else { logger.error("Unable to get Switch Service for Switch node " + s); }
             */
            GetSwitchesResponseItem item = new GetSwitchesResponseItem( s, val.getIpAddress() );
            item.setType( switchService != null ? switchService.getSwitchType() : null );
            switchesResponse.add( item );
        }
        return switchesResponse;
    }

    /** Returns true if the switch node connector contains a switch with id switchId */
    public boolean contains( String switchId )
    {
        return ( switchNodeMap.containsKey( switchId ) );
    }

    public SwitchNode getSwitchNode( String switchId )
    {
        return switchNodeMap.get( switchId );
    }

    public void setSwitchNode( String switchId, SwitchNode switchNode )
    {
        switchNodeMap.put( switchId, switchNode );
    }

    public ISwitchService getSwitchService( String switchId )
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = switchServiceMap.get( switchId );
        if ( ( switchService == null ) && ( switchNode != null ) )
        {
            /* Re-discover switch because perhaps there was an issue in discovering the node. */
            discoverSwitch( switchNode );
            switchService = switchServiceMap.get( switchId );
        }
        return switchService;
    }

    /*
     * public ISwitchService getSwitchService(String switchId) { SwitchNode switchNode = getSwitchNode(switchId);
     * Class<ISwitchService> switchServiceClass = switchServiceClassMap.get(switchId); ISwitchService switchService =
     * null; if (switchServiceClass == null && switchNode != null) { /* Re-discover switch because perhaps there was an
     * issue in discovering the node. * / discoverSwitch(switchNode); switchServiceClass =
     * switchServiceClassMap.get(switchId); } if(switchServiceClass != null) { try { switchService =
     * switchServiceClass.newInstance(); } catch (InstantiationException e) { logger.error(
     * "Exception during creating new Instance of class:" + switchServiceClass, e); } catch (IllegalAccessException e) {
     * logger.error("Exception during creating new Instance of class:" + switchServiceClass, e); } } else {
     * logger.error("Unable to get Switch Service for Switch node " + switchId); } return switchService; }
     */
    public GetSwitchResponse getSwitchNodeInfo( String switchId, boolean refresh )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode };
        GetSwitchResponse response = new GetSwitchResponse( switchNode );
        response.setType( HmsPluginServiceCallWrapper.<String>invokeHmsPluginSwitchService( switchService, switchNode,
                                                                                            "getSwitchType", null ) );
        response.setPowered( HmsPluginServiceCallWrapper.<Boolean>invokeHmsPluginSwitchService( switchService,
                                                                                                switchNode,
                                                                                                "isPoweredOn",
                                                                                                paramsArray ) );
        response.setDiscoverable( switchService != null );
        response.setOsInfo( HmsPluginServiceCallWrapper.<SwitchOsInfo>invokeHmsPluginSwitchService( switchService,
                                                                                                    switchNode,
                                                                                                    "getSwitchOsInfo",
                                                                                                    paramsArray ) );
        response.setHardwareInfo( HmsPluginServiceCallWrapper.<SwitchHardwareInfo>invokeHmsPluginSwitchService( switchService,
                                                                                                                switchNode,
                                                                                                                "getSwitchHardwareInfo",
                                                                                                                paramsArray ) );
        response.setSwitchPortList( HmsPluginServiceCallWrapper.<List<String>>invokeHmsPluginSwitchService( switchService,
                                                                                                            switchNode,
                                                                                                            "getSwitchPortList",
                                                                                                            paramsArray ) );
        response.setSwitchVlans( HmsPluginServiceCallWrapper.<List<String>>invokeHmsPluginSwitchService( switchService,
                                                                                                         switchNode,
                                                                                                         "getSwitchVlans",
                                                                                                         paramsArray ) );
        response.setSwitchVxlans( HmsPluginServiceCallWrapper.<List<SwitchVxlan>>invokeHmsPluginSwitchService( switchService,
                                                                                                               switchNode,
                                                                                                               "getSwitchVxlans",
                                                                                                               paramsArray ) );
        response.setSwitchLacpGroups( HmsPluginServiceCallWrapper.<List<String>>invokeHmsPluginSwitchService( switchService,
                                                                                                              switchNode,
                                                                                                              "getSwitchLacpGroups",
                                                                                                              paramsArray ) );
        response.setSensorInfo( HmsPluginServiceCallWrapper.<SwitchSensorInfo>invokeHmsPluginSwitchService( switchService,
                                                                                                            switchNode,
                                                                                                            "getSwitchSensorInfo",
                                                                                                            paramsArray ) );
        return ( response );
    }

    // Returns the SwitchInfo object as per FRU model
    public SwitchInfo getSwitchInfo( String switchId, boolean refresh )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        SwitchInfo switchInfo = new SwitchInfo();
        SwitchInfoHelperUtil switchInfoHelperUtil = new SwitchInfoHelperUtil();
        Boolean isPoweredOn = false;
        GetSwitchResponse response = new GetSwitchResponse( switchNode );
        Object[] paramsArray = new Object[] { switchNode };
        response.setType( HmsPluginServiceCallWrapper.<String>invokeHmsPluginSwitchService( switchService, switchNode,
                                                                                            "getSwitchType", null ) );
        isPoweredOn =
            HmsPluginServiceCallWrapper.<Boolean>invokeHmsPluginSwitchService( switchService, switchNode,
                                                                               "isPoweredOn", paramsArray );
        response.setPowered( isPoweredOn );
        response.setDiscoverable( switchService != null );
        response.setSwitchId( switchId );
        if ( isPoweredOn )
        {
            response.setOsInfo( HmsPluginServiceCallWrapper.<SwitchOsInfo>invokeHmsPluginSwitchService( switchService,
                                                                                                        switchNode,
                                                                                                        "getSwitchOsInfo",
                                                                                                        paramsArray ) );
            response.setHardwareInfo( HmsPluginServiceCallWrapper.<SwitchHardwareInfo>invokeHmsPluginSwitchService( switchService,
                                                                                                                    switchNode,
                                                                                                                    "getSwitchHardwareInfo",
                                                                                                                    paramsArray ) );
            response.setSwitchPortList( HmsPluginServiceCallWrapper.<List<String>>invokeHmsPluginSwitchService( switchService,
                                                                                                                switchNode,
                                                                                                                "getSwitchPortList",
                                                                                                                paramsArray ) );
            response.setSwitchVlans( HmsPluginServiceCallWrapper.<List<String>>invokeHmsPluginSwitchService( switchService,
                                                                                                             switchNode,
                                                                                                             "getSwitchVlans",
                                                                                                             paramsArray ) );
            response.setSwitchVxlans( HmsPluginServiceCallWrapper.<List<SwitchVxlan>>invokeHmsPluginSwitchService( switchService,
                                                                                                                   switchNode,
                                                                                                                   "getSwitchVxlans",
                                                                                                                   paramsArray ) );
            response.setSwitchLacpGroups( HmsPluginServiceCallWrapper.<List<String>>invokeHmsPluginSwitchService( switchService,
                                                                                                                  switchNode,
                                                                                                                  "getSwitchLacpGroups",
                                                                                                                  paramsArray ) );
            response.setSensorInfo( HmsPluginServiceCallWrapper.<SwitchSensorInfo>invokeHmsPluginSwitchService( switchService,
                                                                                                                switchNode,
                                                                                                                "getSwitchSensorInfo",
                                                                                                                paramsArray ) );
        }
        switchInfo = switchInfoHelperUtil.convertSwitchNodeToSwitchInfo( response );
        return switchInfo;
    }

    public void updateSwitchNodeInfo( String switchId, SwitchUpdateInfo switchUpdateInfo )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        /* Check for time server update */
        String timeServer = switchUpdateInfo.getTimeServer();
        if ( ( timeServer != null ) && !"".equals( timeServer.trim() ) )
        {
            Object[] paramsArray = new Object[] { switchNode, timeServer.trim() };
            logger.debug( "Updating time server for switch " + switchId + " to " + timeServer.trim() );
            HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode,
                                                                      "updateSwitchTimeServer", paramsArray );
        }
        /* Update IP address on the switch */
        String ipAddress = switchUpdateInfo.getIpAddress();
        if ( ( ipAddress != null ) && !"".equals( ipAddress.trim() ) )
        {
            String netmask = switchUpdateInfo.getNetmask();
            String gateway = switchUpdateInfo.getGateway();
            if ( ( ( netmask == null ) || "".equals( netmask.trim() ) )
                && ( ( gateway == null ) || "".equals( gateway.trim() ) ) )
            {
                throw new HmsException( "Invalid IP address, netmask, and gateway provided." );
            }
            logger.debug( "Updating IP address for switch " + switchId + " to " + ipAddress );
            Object[] paramArray = new Object[] { switchNode, ipAddress, netmask, gateway };
            HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode,
                                                                      "updateSwitchIpAddress", paramArray );
            /* Update IP address on the switch node */
            switchNode.setIpAddress( ipAddress );
            setSwitchNode( switchId, switchNode );
            /* Update the IP address in the HMS inventory file */
            HmsInventoryConfiguration hic = HmsConfigHolder.getHmsInventoryConfiguration();
            boolean found = false;
            if ( ( hic.getSwitches() != null ) && ( hic.getSwitches().size() > 0 ) )
            {
                for ( int i = 0; i < hic.getSwitches().size(); i++ )
                {
                    SwitchItem si = hic.getSwitches().get( i );
                    if ( switchId.equals( si.getId() ) )
                    {
                        si.setIpAddress( ipAddress );
                        hic.getSwitches().set( i, si );
                        found = true;
                        break;
                    }
                }
            }
            /* Update the HMS inventory json file */
            if ( found )
            {
                HmsConfigHolder.setHmsInventoryConfiguration( hic );
            }
            else
            {
                logger.warn( "Couldn't update IP address for switch " + switchId );
            }
        }
    }

    public List<String> getSwitchPorts( String switchId )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode };
        return HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode,
                                                                         "getSwitchPortList", paramsArray );
    }

    public List<SwitchPort> getSwitchPortsBulk( String switchId )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode };
        return HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode,
                                                                         "getSwitchPortListBulk", paramsArray );
    }

    public SwitchPort getSwitchPort( String switchId, String portName )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, portName };
        return HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "getSwitchPort",
                                                                         paramsArray );
    }

    public void changeSwitchPortStatus( String switchId, String portName, SwitchPort.PortStatus portStatus )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, portName, portStatus };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "setSwitchPortStatus",
                                                                  paramsArray );
    }

    public void updateSwitchPort( String switchId, String portName, SwitchPort portInfo )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, portName, portInfo };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "updateSwitchPort",
                                                                  paramsArray );
    }

    public void rebootSwitch( String switchId )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "reboot", paramsArray );
    }

    public void upgradeSwitch( String switchId, SwitchUpgradeInfo upgradeInfo )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, upgradeInfo };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "upgrade", paramsArray );
    }

    public List<String> getSwitchLacpGroups( String switchId )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode };
        return HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode,
                                                                         "getSwitchLacpGroups", paramsArray );
    }

    public SwitchLacpGroup getSwitchLacpGroup( String switchId, String lacpGroupName )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, lacpGroupName };
        return HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode,
                                                                         "getSwitchLacpGroup", paramsArray );
    }

    public void createLacpGroup( String switchId, SwitchLacpGroup lacpGroup )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, lacpGroup };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "createLacpGroup",
                                                                  paramsArray );
    }

    public void deleteLacpGroup( String switchId, String lacpGroupName )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, lacpGroupName };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "deleteLacpGroup",
                                                                  paramsArray );
    }

    public List<String> getSwitchVlans( String switchId )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode };
        return HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "getSwitchVlans",
                                                                         paramsArray );
    }

    public List<SwitchVlan> getSwitchVlansBulk( String switchId )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode };
        return HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode,
                                                                         "getSwitchVlansBulk", paramsArray );
    }

    public SwitchVlan getSwitchVlan( String switchId, String vlanName )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, vlanName };
        return HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "getSwitchVlan",
                                                                         paramsArray );
    }

    public void createVlan( String switchId, SwitchVlan vlan )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, vlan };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "createVlan", paramsArray );
    }

    public void updateVlan( String switchId, String vlanName, SwitchVlan vlan )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, vlanName, vlan };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "updateVlan", paramsArray );
    }

    public void deleteVlan( String switchId, String vlanName )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, vlanName };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "deleteVlan", paramsArray );
    }

    public List<SwitchVxlan> getSwitchVxlans( String switchId )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode };
        return HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "getSwitchVxlans",
                                                                         paramsArray );
    }

    public List<SwitchVxlan> getSwitchVxlansMatchingVlan( String switchId, String vlanName )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, vlanName };
        return HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode,
                                                                         "getSwitchVxlansMatchingVlan", paramsArray );
    }

    public void createVxlan( String switchId, SwitchVxlan vxlan )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, vxlan };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "createVxlan", paramsArray );
    }

    public void deleteVxlan( String switchId, String vxlanName, String vlanName )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, vlanName };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "deleteVxlan", paramsArray );
    }

    public void configureOspf( String switchId, SwitchOspfConfig ospf )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, ospf };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "configureOspf",
                                                                  paramsArray );
    }

    public SwitchOspfConfig getOspf( String switchId )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode };
        return HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "getOspf",
                                                                         paramsArray );
    }

    public void configureBgp( String switchId, SwitchBgpConfig bgp )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, bgp };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "configureBgp",
                                                                  paramsArray );
    }

    public void configureMclag( String switchId, SwitchMclagInfo mclag )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        Object[] paramsArray = new Object[] { switchNode, mclag };
        HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode, "configureMclag",
                                                                  paramsArray );
    }

    public void applySwitchBulkConfigs( String switchId, List<PluginSwitchBulkConfig> configs )
        throws HmsOobNetworkException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        try
        {
            validate( switchId, switchNode, switchService );
        }
        catch ( HmsException e )
        {
            throw new HmsOobNetworkException( e.getMessage(), HmsOobNetworkErrorCode.SET_OPERATION_FAILED );
        }
        Object[] paramsArray = new Object[] { switchNode, configs };
        try
        {
            HmsPluginServiceCallWrapper.invokeHmsPluginSwitchService( switchService, switchNode,
                                                                      "applySwitchBulkConfigs", paramsArray );
        }
        catch ( Exception e )
        {
            throw new HmsOobNetworkException( e.getMessage(), HmsOobNetworkErrorCode.SET_OPERATION_FAILED );
        }
    }

    public void configureIpv4DefaultRoute( String switchId, String gateway, String portId )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        ipv4RouteManager.configureIpv4DefaultRoute( switchService, switchNode, gateway, portId );
    }

    public void deleteIpv4DefaultRoute( String switchId )
        throws HmsException
    {
        SwitchNode switchNode = getSwitchNode( switchId );
        ISwitchService switchService = getSwitchService( switchId );
        validate( switchId, switchNode, switchService );
        ipv4RouteManager.deleteIpv4DefaultRoute( switchService, switchNode );
    }

    public void configureSwitchNtp( NBSwitchNtpConfig ntpConfig )
        throws HmsOobNetworkException
    {
        String mgmtSwitchIp = null;
        /* Get IP address of the management switch first */
        /*
         * TODO: See if looping over the same map can be avoided or not, one solution can be introduction of management
         * IP address as part of a local static variable which can be initilized when creating switchNodeMap
         */
        for ( Map.Entry<String, SwitchNode> entry : switchNodeMap.entrySet() )
        {
            String switchId = entry.getKey();
            SwitchNode switchNode = entry.getValue();
            if ( switchNode.getRole().equals( SwitchRoleType.MANAGEMENT ) )
            {
                mgmtSwitchIp = switchNode.getIpAddress();
                break; // found so no need to keep looping
            }
        }
        if ( mgmtSwitchIp == null )
        {
            throw new HmsOobNetworkException( "Could not retrieve management switch IP address",
                                              HmsOobNetworkErrorCode.SET_OPERATION_FAILED );
        }
        /* Now invoke the config API to set server on all switches */
        for ( Map.Entry<String, SwitchNode> entry : switchNodeMap.entrySet() )
        {
            String switchId = entry.getKey();
            SwitchNode switchNode = entry.getValue();
            SwitchUpdateInfo update = new SwitchUpdateInfo();
            if ( switchNode.getRole().equals( SwitchRoleType.MANAGEMENT ) )
            {
                update.setTimeServer( ntpConfig.getTimeServerIpAddress() );
            }
            else if ( switchNode.getRole().equals( SwitchRoleType.TOR ) )
            {
                update.setTimeServer( mgmtSwitchIp );
            }
            try
            {
                updateSwitchNodeInfo( switchId, update );
            }
            catch ( HmsException e )
            {
                throw new HmsOobNetworkException( "Could not configure NTP Server on switch " + switchId, e,
                                                  HmsOobNetworkErrorCode.SET_OPERATION_FAILED );
            }
        }
    }

    public void parseRackInventoryConfig()
    {
        try
        {
            HmsInventoryConfiguration hic = HmsConfigHolder.getHmsInventoryConfiguration();
            /* Initialize all entries first */
            switchNodeMap.clear();
            switchServiceMap.clear();
            // switchServiceClassMap.clear();
            if ( ( hic != null ) && ( hic.getSwitches() != null ) )
            {
                for ( SwitchItem switchItem : hic.getSwitches() )
                {
                    /* Insert switch node into switch map. */
                    SwitchNode switchNode = new SwitchNode( switchItem );
                    switchNodeMap.put( switchItem.getId(), switchNode );
                    // Before proceeding further, it will put the node into the switch discoveryMap
                    NodeDiscoveryUtil.switchDiscoveryMap.put( switchNode.getSwitchId(), NodeActionStatus.RUNNING );
                    discoverSwitch( switchNode );
                    logger.info( "Inserting switch id " + switchItem.getId() + " into switch node map." );
                }
            }
        }
        catch ( HmsException e )
        {
            logger.fatal( "Error locating/reading HMS inventory configuration file.", e );
        }
    }

    private void discoverSwitch( SwitchNode switchNode )
    {
        SwitchServiceFactory ssFactory = SwitchServiceFactory.getSwitchServiceFactory();
        List<ISwitchService> switchServiceList = ssFactory.getSwitchServiceList();
        boolean discovered = false;
        // Retry logic
        /* Check type information in switch node */
        if ( ( switchNode.getType() != null ) && ( switchServiceList != null ) )
        {
            for ( ISwitchService switchService : switchServiceList )
            {
                List<SwitchType> switchTypes = switchService.getSupportedSwitchTypes();
                if ( switchTypes != null )
                {
                    for ( SwitchType switchType : switchTypes )
                    {
                        if ( switchType.matches( switchNode.getType() ) )
                        {
                            discovered = true;
                            logger.info( "Switch " + switchNode.getSwitchId() + " has been claimed by "
                                + switchService.getSwitchType() );
                            // switchServiceClassMap.put(switchNode.getSwitchId(), switchService.getClass());
                            switchServiceMap.put( switchNode.getSwitchId(), switchService );
                            HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode( switchNode.getSwitchId() );
                            break;
                        }
                    }
                    if ( discovered )
                    {
                        break;
                    }
                }
            }
        }
        /* Discover switch since no type information was specified. */
        if ( !discovered && ( switchServiceList != null ) )
        {
            for ( ISwitchService switchService : switchServiceList )
            {
                try
                {
                    discovered = switchService.discoverSwitch( switchNode );
                }
                catch ( Exception e )
                {
                    logger.warn( "Exception received from " + switchService.getSwitchType()
                        + " while discovering switch " + switchNode.getSwitchId(), e );
                    continue;
                }
                if ( discovered )
                {
                    logger.info( "Switch " + switchNode.getSwitchId() + " has been claimed by "
                        + switchService.getSwitchType() );
                    // switchServiceClassMap.put(switchNode.getSwitchId(), switchService.getClass());
                    switchServiceMap.put( switchNode.getSwitchId(), switchService );
                    HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode( switchNode.getSwitchId() );
                    break;
                }
            }
        }
        if ( !discovered )
        {
            logger.error( "Could not locate appropriate switch service for switch " + switchNode.getSwitchId() );
            // Discovery attempted but failed, update its discovery status
            NodeDiscoveryUtil.switchDiscoveryMap.put( switchNode.getSwitchId(), NodeActionStatus.FAILURE );
        }
        else
        {
            // Discovered SwitchNode, update its discovery status
            NodeDiscoveryUtil.switchDiscoveryMap.put( switchNode.getSwitchId(), NodeActionStatus.SUCCESS );
        }
        if ( switchServiceList == null )
        {
            logger.error( "No switch services were loaded." );
        }
    }

    private void validate( String switchId, SwitchNode switchNode, ISwitchService switchService )
        throws HmsException
    {
        if ( switchNode == null )
        {
            throw new HmsException( "Couldn't retrieve switch node for switch id " + switchId );
        }
        else if ( switchService == null )
        {
            throw new HmsException( "Couldn't locate applicable switch service for " + switchId );
        }
    }

    public static List<SwitchComponentEnum> getMonitoredSwitchComponents()
    {
        List<SwitchComponentEnum> monitoredComponents =
            new ArrayList<SwitchComponentEnum>( Arrays.asList( SwitchComponentEnum.values() ) );
        return monitoredComponents;
    }

    /* Initiate a monitoring task for each switch. */
    private void initSwitchMonitoring()
    {
        for ( String key : switchNodeMap.keySet() )
        {
            SwitchNode tsn = switchNodeMap.get( key );
            IComponentSwitchEventInfoProvider svc = switchServiceMap.get( key );
            HmsNode node = new HMSSwitchNode( key, tsn.getIpAddress(), tsn.getUsername(), tsn.getPassword() );
            long frequency = 60000;
            if ( HmsConfigHolder.getHMSConfigProperty( "HOST_NODE_MONITOR_FREQUENCY" ) != null )
            {
                frequency = Long.parseLong( HmsConfigHolder.getHMSConfigProperty( "HOST_NODE_MONITOR_FREQUENCY" ) );
            }
            if ( svc == null )
            {
                continue;
            }
            /*
             * List<ServerComponent> compList = new ArrayList<ServerComponent>(); compList.add(ServerComponent.SWITCH);
             * compList.add(ServerComponent.SWITCH_PORT); compList.add(ServerComponent.SWITCH_CHASSIS);
             * compList.add(ServerComponent.SWITCH_FAN); compList.add(ServerComponent.SWITCH_POWERUNIT);
             */
            /* Leverage the common monitoring framework. */
            MonitoringTaskResponse response = new MonitoringTaskResponse( node, getMonitoredSwitchComponents(), svc );
            MonitorTaskSuite task = new MonitorTaskSuite( response, frequency );
            try
            {
                MonitoringTaskRequestHandler.getInstance().executeServerMonitorTask( task );
            }
            catch ( Exception e )
            {
                logger.error( "Exception received while starting switch monitor for switch " + key, e );
            }
        }
    }
    /* Initiate a monitoring task for each switch. */
    /*
     * private void initSwitchMonitoring() { for (String key : switchNodeMap.keySet()) { IComponentEventInfoProvider svc
     * = null; SwitchNode tsn = switchNodeMap.get(key); Class<ISwitchService> switchServiceClass =
     * switchServiceClassMap.get(key); if(switchServiceClass != null) { try { svc = switchServiceClass.newInstance(); }
     * catch (InstantiationException e) { logger.error("Exception during creating new Instance of class:" +
     * switchServiceClass, e); } catch (IllegalAccessException e) { logger.error(
     * "Exception during creating new Instance of class:" + switchServiceClass, e); } } else { logger.error(
     * "Unable to get Switch Service for Switch node " + key); } HmsNode node = new HMSSwitchNode(key,
     * tsn.getIpAddress(), tsn.getUsername(), tsn.getPassword()); long frequency = 60000; if
     * (HmsConfigHolder.getHMSConfigProperty("HOST_NODE_MONITOR_FREQUENCY") != null) frequency =
     * Long.parseLong(HmsConfigHolder.getHMSConfigProperty("HOST_NODE_MONITOR_FREQUENCY")); if (svc == null) continue;
     * /* Leverage the common monitoring framework. * / MonitoringTaskResponse response = new
     * MonitoringTaskResponse(node, ServerComponent.SWITCH, svc); MonitorTaskSuite task = new MonitorTaskSuite(response,
     * frequency); try { MonitoringTaskRequestHandler.getInstance().executeServerMonitorTask(task); } catch (Exception
     * e) { logger.error("Exception received while starting switch monitor for switch " + key, e); } } }
     */
}
