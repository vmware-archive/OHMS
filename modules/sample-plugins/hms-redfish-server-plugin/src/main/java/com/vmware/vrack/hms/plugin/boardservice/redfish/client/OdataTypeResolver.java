/*
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.vmware.vrack.hms.plugin.boardservice.redfish.client;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.RedfishCollection;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.RedfishResource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class OdataTypeResolver
    extends TypeIdResolverBase
{
    private static final String REDFISH_RESOURCE_NAMESPACE_GROUP = "ResourceNamespace";

    private static final String REDFISH_RESOURCE_NAME_GROUP = "ResourceName";

    private static final Pattern REDFISH_RESOURCE =
        compile( "#(?<" + REDFISH_RESOURCE_NAMESPACE_GROUP + ">\\w+)"
                     + "\\.\\d\\.\\d\\.\\d\\."
                     + "(?<" + REDFISH_RESOURCE_NAME_GROUP + ">\\w+)" );

    private static final Pattern REDFISH_COLLECTION =
        compile( "#(?<RedfishCollectionName>\\w+)Collection\\.(?<RedfishCollectionNameRepeated>\\w+)Collection" );

    private static final String REDFISH_RESOURCE_CLASS_SUFFIX = "Resource";

    private JavaType baseType;

    @Override
    public void init( JavaType javaType )
    {
        this.baseType = javaType;
    }

    @Override
    public String idFromValue( Object value )
    {
        return idFromValueAndType( value, value.getClass() );
    }

    @Override
    public String idFromValueAndType( Object value, Class<?> suggestedType )
    {
        return null;
    }

    @Override
    public JavaType typeFromId( DatabindContext context, String id )
    {
        JavaType detectedClass = tryMatchRedfishResource( id );
        if ( detectedClass != null )
        {
            return detectedClass;
        }

        detectedClass = tryMatchRedfishCollection( id );
        if ( detectedClass != null )
        {
            return detectedClass;
        }

        throw new UnsupportedOperationException(
            "Could not determine class to deserialize into for \"@odata.type\": \"" + id + "\"" );
    }

    private JavaType tryMatchRedfishResource( String id )
    {
        Matcher resourceMatcher = REDFISH_RESOURCE.matcher( id );
        if ( resourceMatcher.matches() )
        {
            try
            {
                String redfishResourceClassName = getClassName( resourceMatcher.group( REDFISH_RESOURCE_NAME_GROUP ) );
                Class detectedClass = Class.forName( redfishResourceClassName );
                return TypeFactory.defaultInstance().constructSpecializedType( baseType, detectedClass );
            }
            catch ( ClassNotFoundException e )
            {
                throw new UnsupportedOperationException(
                    "Could not determine class to deserialize into for \"@odata.type\": \"" + id + "\"" );
            }
        }
        return null;
    }

    private String getClassName( String resourceName )
    {
        String resourcesPackage = RedfishResource.class.getPackage().getName();
        return resourcesPackage + "." + resourceName + REDFISH_RESOURCE_CLASS_SUFFIX;
    }

    private JavaType tryMatchRedfishCollection( String id )
    {
        Matcher resourceMatcher = REDFISH_COLLECTION.matcher( id );
        if ( resourceMatcher.matches() )
        {
            return TypeFactory.defaultInstance().constructSpecializedType( baseType, RedfishCollection.class );
        }
        return null;
    }

    @Override
    public JsonTypeInfo.Id getMechanism()
    {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
