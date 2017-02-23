/* ********************************************************************************
 * HmsGenericUtil.java
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
package com.vmware.vrack.hms.common.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcabi.manifests.Manifests;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;

/**
 * Util class for all HMS generic utilities.
 *
 * @author spolepalli
 */
public class HmsGenericUtil
{

    /** The Constant MASK_DATA. */
    private static final String MASK_DATA = "****";

    /** The log. */
    private static Logger logger = LoggerFactory.getLogger( HmsGenericUtil.class );

    /**
     * This method clones the original object using {@link BeanUtils} and masks the password with the defaulted
     * characters.
     *
     * @param obj the obj
     * @return the object
     */
    public static Object maskPassword( Object obj )
    {
        if ( obj != null )
        {
            if ( obj instanceof HmsNode )
            {
                ServerNode copy = new ServerNode();
                BeanUtils.copyProperties( obj, copy );
                copy.setOsPassword( MASK_DATA );
                copy.setManagementUserPassword( MASK_DATA );
                return copy;
            }
            else if ( obj instanceof SwitchNode )
            {
                SwitchNode copy = new SwitchNode();
                BeanUtils.copyProperties( obj, copy );
                copy.setPassword( MASK_DATA );
                return copy;
            }
        }
        else
        {
            logger.warn( "Null input provided" );
        }
        return null;
    }

    /**
     * Gets the build version.
     *
     * @return the build version
     */
    public static String getBuildVersion()
    {
        return Manifests.read( Constants.BUILD_VERSION );
    }

    /**
     * Gets the base response.
     *
     * @param status the status
     * @param errorMessage the error message
     * @return the base response
     */
    public static BaseResponse getBaseResponse( final Status status, final String errorMessage )
    {
        if ( status != null )
        {
            return HmsGenericUtil.getBaseResponse( status.getStatusCode(), status.getReasonPhrase(), errorMessage );
        }
        return null;
    }

    /**
     * Gets the base response.
     *
     * @param statusCode the status code
     * @param statusMessage the status message
     * @param errorMessage the error message
     * @return the base response
     */
    public static BaseResponse getBaseResponse( final int statusCode, final String statusMessage,
                                                final String errorMessage )
    {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatusCode( statusCode );
        baseResponse.setStatusMessage( statusMessage );
        baseResponse.setErrorMessage( errorMessage );
        return baseResponse;
    }

    /**
     * Sleeps the thread for the specified time interval if the task is not completed yet
     *
     * @param isTaskCompleted
     * @param sleepInterval
     */
    public static void sleepThread( boolean isTaskCompleted, long sleepInterval )
    {
        if ( !isTaskCompleted )
        {
            try
            {
                Thread.sleep( sleepInterval );
            }
            catch ( InterruptedException e )
            {
                logger.error( "Can't sleep the thread, exception: {}", e );

            }
        }
    }

    /**
     * Parses the input json value and converts to {@link Map<String, Object>}
     *
     * @param input
     * @return
     * @throws HmsException
     */
    public static Map<String, Object> parseJson( String input )
        throws HmsException
    {
        if ( input != null )
        {
            ObjectMapper mapper = new ObjectMapper();
            try
            {
                return mapper.readValue( input, new TypeReference<Map<String, Object>>()
                {
                } );
            }
            catch ( Exception e )
            {
                logger.error( "Can't parse json input, exception is: {}", e );
                throw new HmsException( e );
            }
        }
        return null;
    }

    /**
     * Parses the string as class using Jackson.
     *
     * @param <T> the generic type
     * @param content the content
     * @param valueType the value type
     * @return the t
     */
    public static <T> T parseStringAsValueType( final String content, Class<T> valueType )
    {

        // check that String is not null or blank
        if ( StringUtils.isBlank( content ) )
        {
            logger.warn( "In parseStringAsClass, String content is either null or blank." );
            return null;
        }

        // check that class type is not null
        if ( valueType == null )
        {
            logger.warn( "In parseStringAsClass, Class is null." );
            return null;
        }

        // Convert String to Object using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            return objectMapper.readValue( content, valueType );
        }
        catch ( IOException e )
        {
            logger.error( "In parseStringAsClass, error while parsing String '{}' as '{}'.", content,
                          valueType.getSimpleName(), e );
            return null;
        }
    }

    /**
     * Parses the string as type reference.
     *
     * @param <T> the generic type
     * @param content the content
     * @param typeRef the type ref
     * @return the t
     */
    public static <T> T parseStringAsTypeReference( final String content, TypeReference<T> typeRef )
    {
        // check that String is not null or blank
        if ( StringUtils.isBlank( content ) )
        {
            logger.warn( "In parseStringAsClass, String content is either null or blank." );
            return null;
        }

        // check that class type is not null
        if ( typeRef == null )
        {
            logger.warn( "In parseStringAsClass, Type reference is null." );
            return null;
        }
        // Convert String to TypeReference using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            return objectMapper.readValue( content, typeRef );
        }
        catch ( IOException e )
        {
            logger.error( "In parseStringAsClass, error while parsing String '{}' as '{}'.", content,
                          typeRef.toString(), e );
            return null;
        }
    }

    /**
     * Converts Object to String using Jackson.
     *
     * @param <T> the generic type
     * @param object the object
     * @return the payload
     */
    public static <T> String getPayload( T object )
    {

        // check that object is not null
        if ( object == null )
        {
            logger.warn( "In getPayload, Object is null." );
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            return objectMapper.writeValueAsString( object );
        }
        catch ( IOException e )
        {
            logger.error( "In getPayload, error while converting Object to String.", e );
            return null;
        }
    }

    /**
     * Identifies if the host is reachable or not. Reachability of Ip will be verified based on the command -
     *
     * @param ipAddress the ip address
     * @return true, if is host reachable
     */
    public static boolean isHostReachable( String ipAddress )
    {
        try
        {
            final String pingCommandTemplate = Constants.HMS_HOST_REACHABILITY_VERIFICATION_COMMAND_TEMPLATE;
            String command = String.format( pingCommandTemplate, ipAddress );
            List<String> commands = new ArrayList<String>();
            commands.add( "bash" );
            commands.add( "-c" );
            commands.add( command );
            int status = ProcessUtil.executeCommand( commands );
            logger.debug( "Ip '{}' reachability status: {}", ipAddress, status );
            if ( status == 0 )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch ( Exception e )
        {
            String message = String.format( "Exception while trying to reach ip: '%s'.", ipAddress );
            logger.error( message, e );
            return false;
        }
    }

    /**
     * Delete latest inventory backup.
     */
    public static void deleteLatestInventoryBackup()
    {
        // clean up
        String invFilename = HmsConfigHolder.getInventoryConfigFileName();
        if ( invFilename != null )
        {
            File file = new File( invFilename );
            if ( file.exists() && file.isFile() )
            {
                String backupFileNamePattern = file.getName() + ".bak.*";
                String backupFileDirectory = file.getParent();
                String backupFile =
                    FileUtil.findLatestFileByLastModified( backupFileDirectory, backupFileNamePattern, true );
                if ( backupFile != null )
                {
                    FileUtil.deleteFile( backupFile );
                }
            }
        }
    }
}
