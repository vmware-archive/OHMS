/* ********************************************************************************
 * TopologyAggregator.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import com.vmware.vrack.hms.aggregator.switches.HmsSwitchManager;
import com.vmware.vrack.hms.aggregator.topology.HostNameRetreival;
import com.vmware.vrack.hms.aggregator.topology.TopologySingleNodeBulkPortsProcessor;
import com.vmware.vrack.hms.aggregator.topology.TopologySingleNodeBulkValnsProcessor;
import com.vmware.vrack.hms.aggregator.topology.object.CumulativeObject;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.esxinfo.HostNameInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.topology.NetTopElement;
import com.vmware.vrack.hms.inventory.InventoryLoader;

@Component
public class TopologyAggregator
{
    private static Logger logger = Logger.getLogger( TopologyAggregator.class );

    static final String PORTSBULK = "PortsBulk";

    static final String VLANBULK = "VlanBulk";

    static final String HOSTNAME = "HostName";

    ExecutorService executor = Executors.newFixedThreadPool( 50 );

    Set<Future<CumulativeObject>> set = new HashSet<Future<CumulativeObject>>();

    @Value( "${hms.switch.host}" )
    private String hmsIpAddr;

    @Value( "${hms.switch.port}" )
    private int hmsPort;

    @Autowired
    HmsSwitchManager switchManager;

    public TopologyAggregator()
    {
    }

    public List<NetTopElement> getNetworkTopology( String body, HttpMethod method, HttpServletRequest request,
                                                   HttpServletResponse response )
                                                       throws HMSRestException
    {
        URI uri = null;
        Map<String, NetTopElement> db = new HashMap<String, NetTopElement>();
        NetTopElement elem = null, connElem = null;
        List<NetTopElement> netTopList = new ArrayList<NetTopElement>();
        List<String> torSwitchList = null;
        Map<String, String> ipAddress2NodeIdMap = new HashMap<String, String>();
        logger.debug( "START Network topology creation." );
        /* Get all switch ids first */
        try
        {
            torSwitchList = switchManager.getAllSwitchIds();
        }
        catch ( Exception e )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while connecting to hms."
                                            + ( ( uri != null ) ? uri.toString() : "" ) );
        }
        logger.debug( "DONE processing switch list." );
        HashMap<String, SwitchPort[]> switchPortBulk = new HashMap<String, SwitchPort[]>();
        HashMap<String, SwitchVlan[]> switchVlanBulk = new HashMap<String, SwitchVlan[]>();
        /**
         * Processing for all hosts for host names
         */
        Map<String, ServerNode> nodeMap = InventoryLoader.getInstance().getNodeMap();
        if ( nodeMap != null )
        {
            for ( ServerNode node : nodeMap.values() )
            {
                Callable<CumulativeObject> resultHostName = new HostNameRetreival( node );
                Future<CumulativeObject> futureBulkPort = executor.submit( resultHostName );
                set.add( futureBulkPort );
            }
        }
        logger.debug( "DONE processing all host names." );
        /**
         * Processing for all switches for port stats and vlan bulk
         */
        for ( String node : torSwitchList )
        {
            Callable<CumulativeObject> resultBulkPort =
                new TopologySingleNodeBulkPortsProcessor( hmsIpAddr, hmsPort, node );
            Callable<CumulativeObject> resultBulkVlans =
                new TopologySingleNodeBulkValnsProcessor( hmsIpAddr, hmsPort, node );
            Future<CumulativeObject> futureBulkPort = executor.submit( resultBulkPort );
            Future<CumulativeObject> futureBulkVlan = executor.submit( resultBulkVlans );
            set.add( futureBulkPort );
            set.add( futureBulkVlan );
        }
        for ( Future<CumulativeObject> future : set )
        {
            CumulativeObject output = null;
            try
            {
                output = future.get();
                if ( output != null )
                {
                    if ( output.getQueryType().equals( PORTSBULK ) )
                    {
                        switchPortBulk.put( output.getNodeName(), (SwitchPort[]) output.getObject() );
                    }
                    else if ( output.getQueryType().equals( HOSTNAME ) )
                    {
                        HostNameInfo hostnameInfo = (HostNameInfo) output.getObject();
                        String nodeId = output.getNodeName();
                        ipAddress2NodeIdMap.put( hostnameInfo.getHostName(), nodeId );
                        ipAddress2NodeIdMap.put( hostnameInfo.getFullyQualifiedDomainName(), nodeId );
                        logger.debug( "Inserting (" + hostnameInfo.getHostName() + ", " + nodeId
                            + ") into the IP address map." );
                        logger.debug( "Inserting (" + hostnameInfo.getFullyQualifiedDomainName() + ", " + nodeId
                            + ") into the IP address map." );
                    }
                    else
                    {
                        switchVlanBulk.put( output.getNodeName(), (SwitchVlan[]) output.getObject() );
                    }
                }
            }
            catch ( Exception e )
            {
                logger.error( "Exception occured while retriving details for topology aggregator" + e );
            }
        }
        try
        {
            for ( String node : torSwitchList )
            {
                /*
                 * First get list of all the ports configured on the switch uri = new URI( "http", null, hmsIpAddr,
                 * hmsPort, "/api/1.0/hms/switches/{nodeName}/portsbulk".replaceAll("\\{nodeName\\}", node), null,
                 * null); RestTemplate restTemplate = new RestTemplate(); HttpHeaders headers = new HttpHeaders();
                 * headers.add("Accept","application/json"); HttpEntity<Object> entity = new HttpEntity<Object>(null,
                 * headers); ParameterizedTypeReference<SwitchPort[]> typeRef = new
                 * ParameterizedTypeReference<SwitchPort[]>() {}; ResponseEntity<SwitchPort[]> oobResponse =
                 * restTemplate.exchange(uri, HttpMethod.GET, entity, typeRef);
                 */
                SwitchPort[] portArray = switchPortBulk.get( node );
                if ( portArray != null )
                {
                    for ( SwitchPort switchPort : portArray )
                    {
                        String remoteSystemId = null;
                        elem = new NetTopElement();
                        elem.setDeviceId( node );
                        elem.setPortName( switchPort.getName() );
                        elem.setMacAddress( switchPort.getMacAddress() );
                        if ( switchPort.getLinkedPort() != null )
                        {
                            connElem = new NetTopElement();
                            remoteSystemId = switchPort.getLinkedPort().getDeviceName();
                            /* Convert IP addresses to node/switch ids where appropriate */
                            if ( remoteSystemId != null && ipAddress2NodeIdMap.containsKey( remoteSystemId ) )
                            {
                                String oldRemoteSystemId = remoteSystemId;
                                remoteSystemId = ipAddress2NodeIdMap.get( oldRemoteSystemId );
                                logger.debug( "Transforming key " + oldRemoteSystemId + " to " + remoteSystemId );
                            }
                            connElem.setDeviceId( remoteSystemId );
                            connElem.setPortName( switchPort.getLinkedPort().getPortName() );
                            elem.setConnectedElement( connElem );
                        }
                        db.put( node + "." + switchPort.getName(), elem );
                    } // end of per port iteration
                }
                /*
                 * Now get list of all the VLANs configured on the switch
                 */
                /*
                 * uri = new URI( "http", null, hmsIpAddr, hmsPort,
                 * "/api/1.0/hms/switches/{nodeName}/vlansbulk".replaceAll("\\{nodeName\\}", node), null, null);
                 * restTemplate = new RestTemplate(); ParameterizedTypeReference<SwitchVlan[]> typeRef2 = new
                 * ParameterizedTypeReference<SwitchVlan[]>() {}; ResponseEntity<SwitchVlan[]> oobResponse2 =
                 * restTemplate.exchange(uri, HttpMethod.GET, entity, typeRef2);
                 */
                SwitchVlan[] vlanArray = switchVlanBulk.get( node );
                if ( vlanArray != null )
                {
                    for ( SwitchVlan vlan : vlanArray )
                    {
                        // Link the ports associated with the VLAN now
                        for ( String up : vlan.getUntaggedPorts() )
                        {
                            NetTopElement e = db.get( node + "." + up );
                            if ( e != null )
                            {
                                List<String> vlans = null;
                                if ( e.getVlans() == null )
                                {
                                    vlans = new ArrayList<String>();
                                    e.setVlans( vlans );
                                }
                                else
                                {
                                    vlans = e.getVlans();
                                }
                                vlans.add( vlan.getName() );
                            }
                        }
                        for ( String up : vlan.getTaggedPorts() )
                        {
                            NetTopElement e = db.get( node + "." + up );
                            if ( e != null )
                            {
                                List<String> vlans = null;
                                if ( e.getVlans() == null )
                                {
                                    vlans = new ArrayList<String>();
                                    e.setVlans( vlans );
                                }
                                else
                                {
                                    vlans = e.getVlans();
                                }
                                vlans.add( vlan.getName() );
                            }
                        }
                    }
                }
            }
        }
        catch ( HttpStatusCodeException e )
        {
        }
        catch ( Exception e )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while connecting to hms."
                                            + ( ( uri != null ) ? uri.toString() : "" ) );
        }
        logger.debug( "END processing ports on all switches." );
        for ( NetTopElement e : db.values() )
        {
            netTopList.add( e );
        }
        logger.debug( "END Network topology creation." );
        return netTopList;
    }
}
