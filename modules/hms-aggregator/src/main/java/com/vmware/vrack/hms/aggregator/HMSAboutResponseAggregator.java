/* ********************************************************************************
 * HMSAboutResponseAggregator.java
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
package com.vmware.vrack.hms.aggregator;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.AboutResponse;
import com.vmware.vrack.hms.common.util.Constants;

/**
 * @author sgakhar Purpose of this Class is to aggregate inband and out of band Agent Version.
 */
public class HMSAboutResponseAggregator
{
    private static Logger logger = Logger.getLogger( HMSAboutResponseAggregator.class );

    /**
     * gets HMS About/Version/Build information aggregated from OOB and IB agents
     * 
     * @return
     * @throws HmsException
     */
    public Map<String, AboutResponse> getHMSAboutResponse()
        throws HmsException
    {
        Map<String, AboutResponse> aboutMap = new HashMap<String, AboutResponse>();
        aboutMap.put( "OOBAgent", getOOBAboutResponse() );
        aboutMap.put( "IBAgent", getInBandAboutResponse() );
        return aboutMap;
    }

    /**
     * get HMS version for OOB Agent
     * 
     * @param
     * @return
     */
    private AboutResponse getOOBAboutResponse()
    {
        AboutResponse response = new AboutResponse();
        try
        {
            response = MonitoringUtil.getServerEndpointAboutResponseOOB( Constants.HMS_OOB_ABOUT_ENDPOINT );
        }
        catch ( Exception e )
        {
            logger.debug( "Can't get OOB Agent version.", e );
        }
        return response;
    }

    /**
     * Retrieves for HMS IB Agent version info from Manifest file
     * 
     * @param
     * @return
     */
    private AboutResponse getInBandAboutResponse()
    {
        AboutResponse response = new AboutResponse();
        String classPath = HMSAboutResponseAggregator.class.getResource( "/" ).getPath();
        logger.debug( "Found HMSAboutResponseAggregator.class from " + classPath );
        String manifestPath =
            "file:" + classPath.substring( 0, classPath.lastIndexOf( "/WEB-INF" ) ) + "/META-INF/MANIFEST.MF";
        logger.debug( "MANIFEST path is " + manifestPath );
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

    public static final String BUILD_VERSION = "Build-Version";

    public static final String BUILT_BY = "Built-By";

    public static final String BUILD_DATE = "Build-Date";

    public static final String BUILD_JDK = "Build-Jdk";
}
