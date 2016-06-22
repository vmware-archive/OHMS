/* ********************************************************************************
 * TopologySingleNodeBulkPortsProcessor.java
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
import com.vmware.vrack.hms.common.switches.api.SwitchPort;

public class TopologySingleNodeBulkPortsProcessor
    implements Callable<CumulativeObject>
{
    String hmsIpAddr;

    int hmsPort;

    String node;

    static final String PORTSBULK = "PortsBulk";

    public TopologySingleNodeBulkPortsProcessor( String hmsIpAddr, int hmsPort, String node )
    {
        this.hmsIpAddr = hmsIpAddr;
        this.hmsPort = hmsPort;
        this.node = node;
    }

    public CumulativeObject call()
        throws Exception
    {
        SwitchPort[] portArray = getPortBulkForSwitch();
        CumulativeObject portsBulk = new CumulativeObject();
        portsBulk.setNodeName( node );
        portsBulk.setQueryType( PORTSBULK );
        portsBulk.setObject( portArray );
        return portsBulk;
    }

    private SwitchPort[] getPortBulkForSwitch()
        throws URISyntaxException
    {
        URI uri =
            new URI( "http", null, hmsIpAddr, hmsPort,
                     "/api/1.0/hms/switches/{nodeName}/portsbulk".replaceAll( "\\{nodeName\\}", node ), null, null );
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Accept", "application/json" );
        HttpEntity<Object> entity = new HttpEntity<Object>( null, headers );
        ParameterizedTypeReference<SwitchPort[]> typeRef = new ParameterizedTypeReference<SwitchPort[]>()
        {
        };
        ResponseEntity<SwitchPort[]> oobResponse = restTemplate.exchange( uri, HttpMethod.GET, entity, typeRef );
        SwitchPort[] portArray = oobResponse.getBody();
        return portArray;
    }
}
