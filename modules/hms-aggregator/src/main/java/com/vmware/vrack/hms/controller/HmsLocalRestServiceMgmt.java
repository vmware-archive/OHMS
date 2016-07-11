/* ********************************************************************************
 * HmsLocalRestServiceMgmt.java
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
package com.vmware.vrack.hms.controller;

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.aggregator.logextractor.HmsLogExtractOptions;
import com.vmware.vrack.hms.aggregator.util.DebuggerUtil;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.inventory.InventoryLoader;

@Controller
public class HmsLocalRestServiceMgmt
{
    private static Logger logger = Logger.getLogger( HmsLocalRestServiceMgmt.class );

    @Value( "${hms.switch.host}" )
    private String hmsIpAddr;

    @Value( "${hms.switch.port}" )
    private int hmsPort;

    @Value( "${hms.switch.username}" )
    private String hmsOobUsername;

    @Value( "${hms.switch.password}" )
    private String hmsOobPassword;

    @Value( "${hms.log.archiver.script}" )
    private String hmsLogArchiverScript;

    @Value( "${hms.log.archive.dir}" )
    private String hmsLogArchiveLocation;

    @Value( "${hms.oob.log.location}" )
    private String hmsOobLogLocation;

    @Value( "${hms.ib.log.location}" )
    private String hmsIbLogLocation;

    @Value( "${hms.log.extract.lines:20000}" )
    private int hmsLogLineExtractLimit;

    /**
     * Debug endpoint, that will get events log, HMS IB and HMS OOB logs and put them in single archive
     *
     * @param host_id
     * @param body
     * @param method
     * @return
     * @throws HMSRestException
     */
    @RequestMapping( value = "/debug/host/{host_id}", method = RequestMethod.PUT )
    @ResponseBody
    public BaseResponse performLogArchiving( @PathVariable
    final String host_id, @RequestBody
    final String body, HttpMethod method )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        if ( !InventoryLoader.getInstance().getNodeMap().containsKey( host_id ) )
        {
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        }
        int noOfLines = hmsLogLineExtractLimit;
        if ( body != null )
        {
            ObjectMapper mapper = new ObjectMapper();
            try
            {
                HmsLogExtractOptions options = mapper.readValue( body, HmsLogExtractOptions.class );
                noOfLines = options.getNoOfLines();
            }
            catch ( Exception e )
            {
                logger.warn( "Cannot find number of lines to read from node [ " + host_id
                    + " ] IB and OOB logs, in request body. So using default value [ " + noOfLines + " ]" );
            }
        }
        try
        {
            String targetArchive = DebuggerUtil.archiveHmsDebugLogs( host_id, hmsIpAddr, hmsOobUsername,
                                                                     hmsLogArchiverScript, hmsLogArchiveLocation,
                                                                     hmsOobLogLocation, hmsIbLogLocation, noOfLines );
            response.setStatusCode( Status.OK.getStatusCode() );
            response.setStatusMessage( "Hms debug logs archive will be created shortly at " + targetArchive );
            return response;
        }
        catch ( IllegalArgumentException e )
        {
            String err = "Exception occured during Log archiving for node [ " + host_id + " ]";
            String debugString =
                String.format( "host_id [ %s ], hmsIpAddr [ %s ], hmsOobUsername [ %s ], hmsLogArchiverScript [ %s ], hmsLogArchiveLocation [ %s ], hmsOobLogLocation [ %s ], hmsIbLogLocation [ %s ], noOfLines[ %s ]",
                               host_id, hmsIpAddr, hmsOobUsername, hmsLogArchiverScript, hmsLogArchiveLocation,
                               hmsOobLogLocation, hmsIbLogLocation, noOfLines );
            logger.error( err + debugString );
            response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
            response.setStatusMessage( err );
            return response;
        }
    }

    /**
     * Rest endpoint to remove hms debug logs that are older than 2 days.
     *
     * @param body
     * @param method
     * @return
     * @throws HMSRestException
     */
    @RequestMapping( value = "/debug/host", method = RequestMethod.DELETE )
    @ResponseBody
    public BaseResponse cleanUpLogs( @RequestBody
    final String body, HttpMethod method )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        if ( hmsLogArchiveLocation != null && !"".equals( hmsLogArchiveLocation.trim() ) )
        {
            try
            {
                DebuggerUtil.cleanHmsDebugLogs( hmsLogArchiveLocation );
                response.setStatusCode( Status.OK.getStatusCode() );
                response.setStatusMessage( "Last 2 days Hms debug logs cleared from " + hmsLogArchiveLocation );
                return response;
            }
            catch ( HmsException e )
            {
                logger.error( "Exception occured during cleaning of hms debug logs at [ " + hmsLogArchiveLocation
                    + " ]" );
                response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
                response.setStatusMessage( "Unable to clear Hms debug logs from " + hmsLogArchiveLocation );
                return response;
            }
        }
        else
        {
            String err =
                "Unable to continue with clearing hms debug log archives because one or more parameters have been missing or null";
            String debugString = String.format( "hmsLogArchiveLocation [ %s ]", hmsLogArchiveLocation );
            logger.error( err + debugString );
            response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
            response.setStatusMessage( err );
            return response;
        }
    }
}
