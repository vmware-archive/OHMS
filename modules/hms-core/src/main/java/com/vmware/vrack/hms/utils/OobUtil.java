/* ********************************************************************************
 * OobUtil.java
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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.Constants;

/**
 * The Class OobUtil.
 */
public class OobUtil
{

    /**
     * Instantiates a new oob util.
     */
    private OobUtil()
    {
        throw new AssertionError( "OobUtil is a utility class." );
    }

    /**
     * Method to retrieve the props for a specific file. Also loads the props if its not available in line memory by
     * reading from file.
     *
     * @param filePath
     * @param propHolderKey
     * @param property
     * @return
     */
    public static String getProperty( String filePath, String propHolderKey, String property )
    {
        Properties properties = HmsConfigHolder.getProperties( propHolderKey );

        if ( properties == null )
        {
            HmsConfigHolder.initializePropertiesHolder( propHolderKey, filePath );
            properties = HmsConfigHolder.getProperties( propHolderKey );
        }
        return properties.getProperty( property );
    }

    /**
     * Method to replace the quotes in the provided input
     *
     * @param input
     * @return
     */
    public static String removeQuotes( String input )
    {
        if ( StringUtils.isNotBlank( input ) )
        {
            input = input.replaceAll( "\"", "" );
            input = input.trim();
        }
        return input;
    }

    /**
     * Extracts List<ServerNode> from the provided input
     *
     * @param inventoryMap
     * @return
     * @throws HMSRestException
     */
    public static List<ServerNode> extractServerNodes( Map<String, Object[]> inventoryMap )
        throws HMSRestException
    {

        if ( inventoryMap != null )
        {
            Object[] hosts = inventoryMap.get( Constants.HOSTS );

            if ( hosts == null )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                            "hosts are null in the provided input", "invalid inventory" );
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue( hosts, new TypeReference<List<ServerNode>>()
            {
            } );

        }
        else
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                        "inventoryMap is null in the provided input", "invalid inventory provided" );
        }
    }

    /**
     * Extracts List<SwitchNode> from the provided input
     *
     * @param inventoryMap
     * @return
     * @throws HMSRestException
     */
    public static List<SwitchNode> extractSwitchNodes( Map<String, Object[]> inventoryMap )
        throws HMSRestException
    {

        if ( inventoryMap != null )
        {
            Object[] switches = inventoryMap.get( Constants.SWITCHES );

            if ( switches == null )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                            "switches are null in the provided input", "invalid inventory" );
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue( switches, new TypeReference<List<SwitchNode>>()
            {
            } );

        }
        else
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                        "inventoryMap is null in the provided input", "invalid inventory provided" );
        }
    }
}
