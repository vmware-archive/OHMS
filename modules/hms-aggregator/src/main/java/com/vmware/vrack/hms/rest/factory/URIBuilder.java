/* ********************************************************************************
 * URIBuilder.java
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
package com.vmware.vrack.hms.rest.factory;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vmware.vrack.hms.common.exception.HmsException;

/**
 * Builder class for URI instances
 *
 * @author spolepalli
 */
@Component( value = "URIBuilder" )
public class URIBuilder
{

    private static final Logger LOG = LoggerFactory.getLogger( URIBuilder.class );

    private static String hmsIpAddr;

    private static int hmsDefaultPort;

    private static String hmsDefaultScheme;

    /**
     * Returns the instance of {@link URI} based on the params.
     *
     * @param path
     * @return
     * @throws HmsException
     */
    public static URI getURI( String path )
        throws HmsException
    {
        return getURI( path, null );
    }

    /**
     * Returns the instance of {@link URI} based on the params.
     *
     * @param path
     * @param query
     * @return
     * @throws HmsException
     */
    public static URI getURI( String path, String query )
        throws HmsException
    {

        LOG.debug( "creating URI for the path {}, query {}", path, query );

        String hmsIpAddr = getHmsIpAddr();
        int port = getHmsDefaultPort();
        String scheme = getHmsDefaultScheme();

        try
        {
            return new URI( scheme, null, hmsIpAddr, port, path, query, null );
        }
        catch ( URISyntaxException e )
        {
            LOG.error( "Exception occured creating instance of URI scheme: {}, hmsIpAdd: {}, port: {}, path: {}, "
                + "query: {} and the exception is {} ", scheme, hmsIpAddr, port, path, query, e );
            throw new HmsException( e );
        }
    }

    /**
     * Returns the instance of {@link URI} based on the params.
     *
     * @param scheme
     * @param ipAddress
     * @param port
     * @param path
     * @param query
     * @return
     * @throws HmsException
     */
    public static URI getURI( String scheme, String ipAddress, int port, String path, String query )
        throws HmsException
    {

        LOG.debug( "URI for the scheme: {}, hmsIpAdd: {}, port: {}, path: {}, query: {}", scheme, hmsIpAddr, port, path,
                   query );

        try
        {
            return new URI( scheme, null, ipAddress, port, path, query, null );
        }
        catch ( URISyntaxException e )
        {
            LOG.error( "Exception occured creating instance of URI scheme: {}, hmsIpAdd: {}, port: {}, path: {}, "
                + "query: {} and the exception is {} ", scheme, hmsIpAddr, port, path, query, e );
            throw new HmsException( e );
        }
    }

    public static String getHmsIpAddr()
    {
        return URIBuilder.hmsIpAddr;
    }

    @Value( "${hms.switch.host}" )
    public void setHmsIpAddr( String hmsIpAddr )
    {
        URIBuilder.hmsIpAddr = hmsIpAddr;
    }

    public static int getHmsDefaultPort()
    {
        return hmsDefaultPort;
    }

    @Value( "${hms.switch.port}" )
    public void setHmsDefaultPort( int hmsDefaultPort )
    {
        URIBuilder.hmsDefaultPort = hmsDefaultPort;
    }

    public static String getHmsDefaultScheme()
    {
        return hmsDefaultScheme;
    }

    @Value( "${hms.switch.default.scheme}" )
    public void setHmsDefaultScheme( String hmsDefaultScheme )
    {
        URIBuilder.hmsDefaultScheme = hmsDefaultScheme;
    }
}
