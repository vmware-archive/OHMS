/* ********************************************************************************
 * HMSStatusService.java
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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmware.vrack.hms.aggregator.ServiceManager;
import com.vmware.vrack.hms.common.StatusCode;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.rest.model.HmsServiceState;
import com.vmware.vrack.hms.common.service.ServiceState;

/**
 * Endpoint for setting the Admin State of HMS.
 * 
 * @author Vmware
 */
@Controller
@RequestMapping( "/state" )
public class HMSStatusService
{
    private static Logger logger = Logger.getLogger( HMSStatusService.class );

    @Value( "${monitor.shutdown.additional.waittime:60000}" )
    private Long shutdownMonitoringAdditionalWaitTime;

    @Value( "${hms.service.maintenance.max-wait-time:300000}" )
    private int serviceMaintenanceMaxWaittime;

    @Value( "${hms.service.maintenance.retry-interval:30000}" )
    private int serviceMaintenanceRetryInterval;

    @Value( "${hms.ib.inventory.location}" )
    private String hmsIbInventoryLocation;

    @Value( "${hms.oob.nodes.pathinfo}" )
    private String oobNodesEndpoint;

    @Value( "${hms.switch.host}" )
    private String oobHost;

    @Value( "${hms.switch.port}" )
    private int oobPort;

    /**
     * This method is used for setting the Admin Status of HMS
     * 
     * @param serviceState
     * @return
     * @throws HMSRestException
     * @throws Exception
     */
    @RequestMapping( method = RequestMethod.POST )
    @ResponseBody
    public BaseResponse setHMSAdminStatus( @RequestParam ServiceState serviceState )
        throws HMSRestException, Exception
    {
        boolean serviceStateChanged = false;
        BaseResponse response = new BaseResponse();
        try
        {
            if ( !( ServiceManager.getServiceState().equals( serviceState )
                || ( ServiceManager.getServiceState().equals( ServiceState.FORCE_MAINTENANCE )
                    && ServiceState.NORMAL_MAINTENANCE.equals( serviceState ) )
                || ( ServiceManager.getServiceState().equals( ServiceState.NORMAL_MAINTENANCE )
                    && ServiceState.FORCE_MAINTENANCE.equals( serviceState ) ) ) )
            {
                switch ( serviceState.getServiceState() )
                {
                    case "NORMAL_MAINTENANCE":
                    case "FORCE_MAINTENANCE":
                        serviceStateChanged =
                            ServiceManager.putServiceInMaintenance( shutdownMonitoringAdditionalWaitTime,
                                                                    serviceMaintenanceMaxWaittime,
                                                                    serviceMaintenanceRetryInterval );
                        break;
                    case "RUNNING":
                        serviceStateChanged = ServiceManager.setServiceInRunning( hmsIbInventoryLocation, oobHost,
                                                                                  oobPort, oobNodesEndpoint );
                        break;
                }
            }
            else
            {
                response.setStatusCode( StatusCode.NOT_MODIFIED.getValue() );
                response.setStatusMessage( String.format( "HMS service state already is in : %s state",
                                                          serviceState.getServiceState() ) );
                return response;
            }
        }
        catch ( Exception e )
        {
            response.setStatusCode( StatusCode.FAILED.getValue() );
            response.setStatusMessage( String.format( "Failed to process request to set HMS service state as: %s, Error Message : %s ",
                                                      serviceState.getServiceState(), e.getMessage() ) );
            return response;
        }
        if ( serviceStateChanged )
        {
            response.setStatusCode( StatusCode.OK.getValue() );
            response.setStatusMessage( String.format( "Setting HMS service state as: %s",
                                                      serviceState.getServiceState() ) );
            return response;
        }
        else
        {
            logger.error( "Error while setting the HMS Admin Status {}" + serviceState.getServiceState() );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                        "Error while setting the HMS Admin Status" + serviceState.getServiceState() );
        }
    }

    /**
     * This method gives the Admin Status of HMS
     * 
     * @return
     * @throws HMSRestException
     */
    @RequestMapping( method = RequestMethod.GET )
    @ResponseBody
    public HmsServiceState getHMSAdminStatus()
        throws HMSRestException
    {
        HmsServiceState hmsServiceState = new HmsServiceState();
        hmsServiceState.setHmsServiceState( ServiceManager.getServiceState() );
        return hmsServiceState;
    }
}
