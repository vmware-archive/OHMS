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

package com.vmware.vrack.hms.plugin.boardservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vmware.vrack.hms.plugin.boardservice.redfish.client.IRedfishWebClient;
import com.vmware.vrack.hms.plugin.boardservice.redfish.client.RedfishClientException;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.RedfishResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockRedfishClient
    implements IRedfishWebClient
{
    private final ObjectMapper mapper;

    private final Map<String, String> resources = new HashMap<>();

    public MockRedfishClient( InputStream mocksStream )
    {
        mapper = new ObjectMapper();

        try
        {
            TypeReference<List<Mock>> typeRef = new TypeReference<List<Mock>>()
            {
            };
            List<Mock> mocks = mapper.readValue( mocksStream, typeRef );
            for ( Mock mock : mocks )
            {
                resources.put( mock.url, mock.body );
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public RedfishResource get( URI targetUri )
        throws RedfishClientException
    {
        String resource = resources.get( targetUri.toString() );
        if ( resource == null )
        {
            throw new RedfishClientException( "", targetUri, null );
        }
        try
        {
            RedfishResource redfishResource = mapper.readValue( resource, RedfishResource.class );
            redfishResource.setOrigin( URI.create( "" ) );
            return redfishResource;
        }
        catch ( IOException | UnsupportedOperationException e )
        {
            throw new RedfishClientException( "", targetUri, e );
        }
    }

    @Override
    public <T> void post( URI targetUri, T body )
        throws RedfishClientException
    {

    }

    @Override
    public <T> void patch( URI targetUri, T body )
        throws RedfishClientException
    {

    }

    @Override
    public void close()
    {

    }

    static class Mock
    {
        @JsonProperty( "url" )
        String url;

        @JsonProperty( "body" )
        @JsonDeserialize( using = JsonStringSerializer.class )
        String body;
    }

    static class JsonStringSerializer
        extends JsonDeserializer<String>
    {
        @Override
        public String deserialize( JsonParser p, DeserializationContext ctxt )
            throws IOException, JsonProcessingException
        {
            return p.getCodec().readTree( p ).toString();
        }
    }
}