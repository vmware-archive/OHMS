/* ********************************************************************************
 * TopologySingleNodeBulkValnsProcessor.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.topology;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vmware.vrack.hms.aggregator.topology.object.CumulativeObject;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;

public class TopologySingleNodeBulkValnsProcessor
    implements Callable<CumulativeObject>
{
    String hmsIpAddr;

    int hmsPort;

    String node;

    static final String VLANBULK = "VlanBulk";

    public TopologySingleNodeBulkValnsProcessor( String hmsIpAddr, int hmsPort, String node )
    {
        this.hmsIpAddr = hmsIpAddr;
        this.hmsPort = hmsPort;
        this.node = node;
    }

    public CumulativeObject call()
        throws Exception
    {
        CumulativeObject vlanBulk = new CumulativeObject();
        SwitchVlan[] bulkVlans = getAllVlansForSwitch();
        vlanBulk.setNodeName( node );
        vlanBulk.setQueryType( VLANBULK );
        vlanBulk.setObject( bulkVlans );
        return vlanBulk;
    }

    SwitchVlan[] getAllVlansForSwitch()
        throws URISyntaxException
    {
        /*
         * Now get list of all the VLANs configured on the switch
         */
        URI uri =
            new URI( "http", null, hmsIpAddr, hmsPort,
                     "/api/1.0/hms/switches/{nodeName}/vlansbulk".replaceAll( "\\{nodeName\\}", node ), null, null );
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Accept", "application/json" );
        HttpEntity<Object> entity = new HttpEntity<Object>( null, headers );
        ParameterizedTypeReference<SwitchVlan[]> typeRef = new ParameterizedTypeReference<SwitchVlan[]>()
        {
        };
        ResponseEntity<SwitchVlan[]> oobResponse = restTemplate.exchange( uri, HttpMethod.GET, entity, typeRef );
        SwitchVlan[] vlanArray = oobResponse.getBody();
        return vlanArray;
    }
}
