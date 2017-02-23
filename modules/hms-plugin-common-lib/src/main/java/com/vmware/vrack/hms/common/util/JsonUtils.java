/* ********************************************************************************
 * JsonUtils.java
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

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class JsonUtils
{
    private static ObjectMapper mapper = new ObjectMapper();

    private static Logger logger = Logger.getLogger( JsonUtils.class );

    public static <T> T getBeanFromJson( String json, Class<T> classname )
    {
        T contents = null;
        try
        {
            contents = mapper.readValue( json, classname );
        }
        catch ( IOException e )
        {
            logger.error( "Exception in getBeanFromJson", e );
        }
        return contents;
    }

    public static String getJsonString( Object obj )
    {
        String contents = "ERROR OCCURED";
        try
        {
            contents = getDefaultMapper().writeValueAsString( obj );
        }
        catch ( IOException e )
        {
            logger.error( "Exception in getJsonString", e );
        }

        return contents;
    }

    public static <T> List<T> getBeanCollectionFromJsonString( String json, Class<T> class_name )
    {
        List<T> contents = null;
        try
        {
            contents =
                getDefaultMapper().readValue( json,
                                              TypeFactory.defaultInstance().constructCollectionType( List.class,
                                                                                                     class_name ) );
        }
        catch ( IOException e )
        {
            logger.error( "Exception in getBeanCollectionFromJsonString", e );
        }

        return contents;
    }

    public static JsonNode getJsonTreeFromJsonString( String json )
        throws IOException, JsonProcessingException
    {
        JsonNode jNode = null;

        jNode = getDefaultMapper().readTree( json );

        return jNode;
    }

    public static ObjectMapper getDefaultMapper()
    {
        return mapper;
    }
}
