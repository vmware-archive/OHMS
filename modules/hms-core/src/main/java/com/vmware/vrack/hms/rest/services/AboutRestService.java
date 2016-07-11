/* ********************************************************************************
 * AboutRestService.java
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
package com.vmware.vrack.hms.rest.services;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.resource.AboutResponse;

@Path( "/about" )
public class AboutRestService
{
    @GET
    @Path( "/" )
    @Produces( "application/json" )
    public AboutResponse aboutHms()
        throws HMSRestException
    {
        AboutResponse response = new AboutResponse();
        Class<AboutRestService> clazz = AboutRestService.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource( className ).toString();
        logger.debug( "Found AboutRestService.class from " + classPath );
        if ( !classPath.startsWith( "jar" ) )
        {
            logger.warn( "AboutRestService.class not found in a jar, therefore no manifest information available." );
            return response;
        }
        String manifestPath = classPath.substring( 0, classPath.lastIndexOf( "!" ) + 1 ) + "/META-INF/MANIFEST.MF";
        try
        {
            Manifest mf = new Manifest( new URL( manifestPath ).openStream() );
            Attributes attributes = mf.getMainAttributes();
            if ( attributes != null )
            {
                response.setBuildVersion( attributes.getValue( BUILD_VERSION ) );
                response.setBuildOwner( attributes.getValue( BUILT_BY ) );
                response.setBuildDate( attributes.getValue( BUILD_DATE ) );
                response.setBuildJdk( attributes.getValue( BUILD_JDK ) );
            }
        }
        catch ( IOException e )
        {
            logger.warn( "Exception received while parsing MANIFEST.MF file.", e );
        }
        return response;
    }

    private Logger logger = Logger.getLogger( AboutRestService.class );

    public static final String BUILD_VERSION = "Build-Version";

    public static final String BUILT_BY = "Built-By";

    public static final String BUILD_DATE = "Build-Date";

    public static final String BUILD_JDK = "Build-Jdk";
}
