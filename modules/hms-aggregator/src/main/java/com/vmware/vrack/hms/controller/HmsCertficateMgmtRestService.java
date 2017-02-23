/* ********************************************************************************
 * HmsCertficateMgmtRestService.java
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmware.vrack.hms.aggregator.ServiceManager;
//import com.vmware.vrack.hms.aggregator.util.UpgradeUtil;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.service.ServiceState;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;

/**
 * Service for Managing the certificates for Hms oob agent.
 *
 * @author VMware, Inc.
 */
@Controller
@RequestMapping( "/certificate" )
public class HmsCertficateMgmtRestService
{

    /** The logger. */
    private final Logger LOG = LoggerFactory.getLogger( HmsCertficateMgmtRestService.class );

    @Value( "${prm.host}" )
    private String prmHost;

    @Value( "${prm.basic.username}" )
    private String prmUserName;

    @Value( "${prm.basic.password}" )
    private String prmPassword;

    /**
     * Creates the required key files for Hms certificate mgmt
     *
     * @return
     * @throws HMSRestException
     * @throws HmsException
     */
    @RequestMapping( value = "/create", method = RequestMethod.POST )
    @ResponseBody
    public ResponseEntity<byte[]> createCertificate()
        throws HMSRestException
    {
        LOG.debug( "certificate creation starts" );

        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE );
        HmsOobAgentRestTemplate<Object> restTemplate;
        ResponseEntity<byte[]> response = null;

        try
        {
            restTemplate = new HmsOobAgentRestTemplate<Object>( headers );
            response = restTemplate.exchange( HttpMethod.POST, Constants.HMS_OOB_CREATE_KEYS, byte[].class );
        }
        catch ( Exception e )
        {
            LOG.error( "Exception creating the certificate files", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "can't create CSR file", e );
        }

        LOG.debug( "certificate creation completed" );

        return response;
    }

    // /**
    // ** API for uploading the VMCA signed certificate. <br>
    // * 1. Clear all the subscriptions <br>
    // * 2. Put Hms under forced maintenance mode <br>
    // * 3. Notify PRM about Hms forced maintenance <br>
    // * 4. Upload cert - calls oob agent for uploading the cert content <br>
    // *
    // * @param inputData
    // * @return
    // * @throws HMSRestException
    // */
    // @RequestMapping(value = "/upload",
    // method = RequestMethod.POST)
    // @ResponseBody
    // public ResponseEntity<Object> uploadSignedCertificate(@RequestBody byte[] inputData) throws HMSRestException {
    // LOG.debug("upload certificate process starts");
    //
    // if (inputData == null) {
    // LOG.debug("Input data does not contain 'fileContent'.");
    // return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
    // }
    //
    // EventMonitoringSubscriptionHolder.getInstance().removeAllMonitoringSubscriptions();
    // ServiceManager.setServiceState(ServiceState.FORCE_MAINTENANCE);
    //
    // LOG.debug("Server is kept under forced maitenance");
    //
    // boolean result = UpgradeUtil
    // .notifyPRMService(ServiceState.FORCE_MAINTENANCE, prmHost, prmUserName,
    // prmPassword);
    //
    // if (result) {
    // LOG.debug("notify PRM about server under maintenance is successful");
    //
    // try {
    // LOG.debug("Upload process starts");
    //
    // HmsOobAgentRestTemplate<byte[]> restTemplate = new HmsOobAgentRestTemplate<byte[]>(inputData);
    // ResponseEntity<Object> response = restTemplate.exchange(HttpMethod.POST,
    // Constants.HMS_OOB_UPLOAD_CERTIFICATE,
    // Object.class);
    // LOG.debug("certificate upload process completed");
    //
    // return response;
    // } catch (HmsException e) {
    // LOG.error("Exception uploading the certificate content", e);
    // throw new HMSRestException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
    // "Can't proceed upload cert, uploadSignedCertificate on oob fails", e);
    // }
    // } else {
    // LOG.error("unable to notify PRM service about Hms maintenance state");
    // throw new HMSRestException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Can't proceed upload cert",
    // "unable to notify PRM service about Hms maintenance state");
    // }
    // }
}