/* ********************************************************************************
 * TopologySingleNodeBulkValnsProcessor.java
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
package com.vmware.vrack.hms.aggregator.topology;

import java.util.concurrent.Callable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.vmware.vrack.hms.aggregator.topology.object.CumulativeObject;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;

public class TopologySingleNodeBulkValnsProcessor
    implements Callable<CumulativeObject>
{

    String node;

    static final String VLANBULK = "VlanBulk";

    public TopologySingleNodeBulkValnsProcessor( String node )
    {
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
        throws HmsException
    {
        /*
         * Now get list of all the VLANs configured on the switch
         */
        String path = "/api/1.0/hms/switches/{nodeName}/vlansbulk".replaceAll( "\\{nodeName\\}", node );
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Accept", MediaType.APPLICATION_JSON_VALUE );
        ParameterizedTypeReference<SwitchVlan[]> typeRef = new ParameterizedTypeReference<SwitchVlan[]>()
        {
        };
        HmsOobAgentRestTemplate<Object> restTemplate = new HmsOobAgentRestTemplate<Object>( headers );
        ResponseEntity<SwitchVlan[]> oobResponse = restTemplate.exchange( HttpMethod.GET, path, typeRef );
        SwitchVlan[] vlanArray = oobResponse.getBody();
        return vlanArray;
    }

}
