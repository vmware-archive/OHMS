/* ********************************************************************************
 * DhcpLeaseUtil.java
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
package com.vmware.vrack.hms.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.rest.model.DhcpLease;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.FileUtil;

/**
 * The Class DhcpLeaseUtil.
 */
public class DhcpLeaseUtil
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( DhcpLeaseUtil.class );

    /**
     * Instantiates a new dhcp lease util.
     */
    private DhcpLeaseUtil()
    {
        throw new AssertionError( "DhcpLeaseUtil contains all static methods. Instance not required." );
    }

    /**
     * Gets the dhcp leases.
     *
     * @return the dhcp leases
     */
    public static List<DhcpLease> getDhcpLeases()
    {
        // check the DHCP Lease file is configured.
        final String dhcpLeaseFile =
            HmsConfigHolder.getHMSConfigProperty( Constants.HMS_MGMT_SWITCH_DHCP_LEASE_FILE_KEY );
        if ( StringUtils.isBlank( dhcpLeaseFile ) )
        {
            logger.error( "In getDhcpLeases, DHCP file is either null or blank. Property '{}' is not configured.",
                          Constants.HMS_MGMT_SWITCH_DHCP_LEASE_FILE_KEY );
            return null;
        }

        // check the dhcp lease file exists
        if ( !FileUtil.isFileExists( dhcpLeaseFile ) )
        {
            logger.error( "In getDhcpLeases, DHCP Lease file '{}' does not exist.", dhcpLeaseFile );
            return null;
        }
        List<DhcpLease> dhcpLeases = DhcpLeaseParser.parseLeaseFile( dhcpLeaseFile );
        if ( dhcpLeases != null )
        {
            logger.info( "In getDhcpLeases, '{}' DHCP lease records found in DHCP lease file '{}'.", dhcpLeases.size(),
                         dhcpLeaseFile );
            return dhcpLeases;
        }
        else
        {
            logger.error( "In getDhcpLeases, Unable to parse DHCP lease file '{}'.", dhcpLeaseFile );
            return null;
        }
    }

    /**
     * Gets the active dhcp leases.
     *
     * @return the active dhcp leases
     */
    public static List<DhcpLease> getActiveDhcpLeases()
    {
        List<DhcpLease> allDhcpLeases = DhcpLeaseUtil.getDhcpLeases();
        if ( CollectionUtils.isEmpty( allDhcpLeases ) )
        {
            return null;
        }
        List<DhcpLease> activeDhcpLeases = new ArrayList<DhcpLease>();
        for ( DhcpLease dhcpLease : allDhcpLeases )
        {
            if ( StringUtils.equals( dhcpLease.getBindingState(), "active" ) )
            {
                activeDhcpLeases.add( dhcpLease );
            }
            else
            {
                logger.debug( "In getActiveDhcpLeases, DHCP Lease [ IP: {}, MAC: {} ] binding state is '{}'. "
                    + "Removed it from active DHCP lease records.", dhcpLease.getIpAddress(), dhcpLease.getMacAddress(),
                              dhcpLease.getBindingState() );
            }
        }
        return activeDhcpLeases;
    }
}
