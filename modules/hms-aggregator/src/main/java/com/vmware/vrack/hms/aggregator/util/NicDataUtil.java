/* ********************************************************************************
 * NicDataUtil.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.util;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.servernodes.api.nic.PortInfo;
import com.vmware.vrack.hms.common.topology.NetTopElement;

/**
 * Enriches the NIC info retuned by Inband API
 *
 * @author Yagnesh Chawda
 */
public class NicDataUtil
{
    private static Logger logger = LoggerFactory.getLogger( NicDataUtil.class );

    private static final String TOPOLOGY_PATH = "/hms-local/api/1.0/hms/topology";

    public static final String hmsIpAddr = "localhost";

    public static final int hmsPort = 8080;

    /**
     * Gets additional info for NIC like, getting topology info for switch and mapping that in to the NIC info for the
     * host to figure-out which switch and port on the switch, which nic port is connected to.
     *
     * @param ethernetControllers
     * @param nodeId
     * @return
     * @throws HmsException
     */
    public static List<EthernetController> getAdditionalNicInfo( List<EthernetController> ethernetControllers,
                                                                 String nodeId )
                                                                     throws HmsException
    {
        if ( ethernetControllers != null && !ethernetControllers.isEmpty() && nodeId != null
            && !"".equals( nodeId.trim() ) )
        {
            Map<String, NetTopElement> netTopologyMap = getConnectedNetTopElementMap();
            // If netTopologyMap is found null, exit with Exception
            if ( netTopologyMap == null )
            {
                throw new HmsException( ErrorMessages.NO_ADDITIONAL_NIC_DETAILS_FOUND );
            }
            for ( EthernetController ethController : ethernetControllers )
            {
                List<PortInfo> portInfos = ethController.getPortInfos();
                for ( PortInfo portInfo : portInfos )
                {
                    String key = getKeyForConnectedElementHashMap( nodeId, portInfo.getDeviceName() );
                    if ( key != null && netTopologyMap.containsKey( key ) )
                    {
                        NetTopElement netTopElement = netTopologyMap.get( key );
                        portInfo.setSwitchName( netTopElement.getDeviceId() );
                        portInfo.setSwitchPort( netTopElement.getPortName() );
                        portInfo.setSwitchPortMac( netTopElement.getMacAddress() );
                    }
                }
            }
            return ethernetControllers;
        }
        else
        {
            throw new HmsException( ErrorMessages.ETHERNET_CONTROLLER_LIST_EMPTY );
        }
    }

    /**
     * Prepares and returns Hashmap which contains only the connectedElements(NetTopologyElements) which are found after
     * iterating all NetTopologyElements
     * 
     * @return
     */
    private static Map<String, NetTopElement> getConnectedNetTopElementMap()
    {
        Map<String, NetTopElement> connectedElementsList = null;
        List<NetTopElement> netTopElements = null;
        try
        {
            netTopElements = getNetTopologyList();
        }
        catch ( Exception e )
        {
            logger.error( ErrorMessages.ERROR_GETTING_NET_TOPOLOGY_LIST + e );
        }
        if ( netTopElements != null )
        {
            connectedElementsList = new HashMap<String, NetTopElement>();
            for ( NetTopElement netTopElement : netTopElements )
            {
                NetTopElement connectedElement = netTopElement.getConnectedElement();
                if ( connectedElement != null )
                {
                    String connElemDeviceId = connectedElement.getDeviceId();
                    String connElemPortName = connectedElement.getPortName();
                    String key = getKeyForConnectedElementHashMap( connElemDeviceId, connElemPortName );
                    // If both device Id and PortName are present then only add in Map
                    if ( key != null )
                    {
                        connectedElementsList.put( key, netTopElement );
                    }
                }
            }
        }
        return connectedElementsList;
    }

    /**
     * Gets List of NetTopology Element by calling /hms/topology endpoint
     * 
     * @return
     * @throws HmsException
     */
    public static List<NetTopElement> getNetTopologyList()
        throws HmsException
    {
        URI uri = null;
        try
        {
            uri = new URI( "http", null, hmsIpAddr, hmsPort, TOPOLOGY_PATH, null, null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", MediaType.APPLICATION_JSON.toString() );
            HttpEntity<Object> entity = new HttpEntity<Object>( headers );
            ParameterizedTypeReference<List<NetTopElement>> typeRef =
                new ParameterizedTypeReference<List<NetTopElement>>()
                {
                };
            ResponseEntity<List<NetTopElement>> oobResponse =
                restTemplate.exchange( uri, HttpMethod.GET, entity, typeRef );
            List<NetTopElement> netTopElements = oobResponse.getBody();
            return netTopElements;
        }
        catch ( Exception e )
        {
            logger.error( ErrorMessages.ERROR_GETTING_NIC_INFO + e );
            throw new HmsException( e );
        }
    }

    /**
     * Generate Unique Key for Combination [Server1||vnc01]
     * 
     * @param deviceId
     * @param portName
     * @return
     */
    public static String getKeyForConnectedElementHashMap( String deviceId, String portName )
    {
        String result = null;
        if ( deviceId != null && portName != null && !"".equals( deviceId.trim() ) && !"".equals( portName.trim() ) )
        {
            result = deviceId + "||" + portName;
            result = result.toLowerCase();
        }
        return result;
    }
}
