/* ********************************************************************************
 * HmsSshKeyMgmtRestService.java
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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;

/**
 * {@link HmsSshKeyMgmtRestService} is responsible for generating ssh key set for management switch
 *
 * @author VMware, Inc.
 */
@Controller
@RequestMapping( "/sshkeys" )
public class HmsSshKeyMgmtRestService
{

    /** The logger. */
    private final Logger LOG = LoggerFactory.getLogger( HmsSshKeyMgmtRestService.class );

    private static final String HMS_MGMT_SWITH_IP = "hms.mgmt.swith.ip";

    @Value( "${hms.switch.host}" )
    private String hmsIpAddr;

    /**
     * Creates the SSH key-set and returns the public key as the response.
     *
     * @return
     * @throws HMSRestException
     */
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    @POST
    @RequestMapping( value = "/create", method = RequestMethod.POST )
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createKeySet()
        throws HMSRestException
    {

        LOG.debug( "SSH keys creation starts" );

        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE );
        HmsOobAgentRestTemplate<Object> restTemplate;
        Map<String, Object> map = new HashMap<String, Object>();

        try
        {
            restTemplate = new HmsOobAgentRestTemplate<Object>( headers );
            ResponseEntity<Map> response =
                restTemplate.exchange( HttpMethod.POST, Constants.HMS_MGMT_SWITCH_CREATE_SSH_KEYS, Map.class );
            map = response.getBody();
            map.put( HMS_MGMT_SWITH_IP, hmsIpAddr );

        }
        catch ( Exception e )
        {
            LOG.error( "Exception creating the ssh keys", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "can't create SSH keys", e );
        }
        ResponseEntity<Map<String, Object>> responseEntity =
            new ResponseEntity<Map<String, Object>>( map, HttpStatus.OK );

        LOG.debug( "SSH keys creation ends." );

        return responseEntity;
    }
}
