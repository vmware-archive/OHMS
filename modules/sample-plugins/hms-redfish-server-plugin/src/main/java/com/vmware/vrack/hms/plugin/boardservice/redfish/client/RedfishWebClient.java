package com.vmware.vrack.hms.plugin.boardservice.redfish.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.RedfishResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

public class RedfishWebClient
    implements IRedfishWebClient
{
    private static Logger LOGGER = LoggerFactory.getLogger( RedfishWebClient.class );

    private final Client client;

    public RedfishWebClient()
    {
        ResteasyJackson2Provider jackson2Provider = new ResteasyJackson2Provider();

        ObjectMapper mapper = new ObjectMapper()
            .enable( DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL )
            .enable( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES )
            .disable( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );

        jackson2Provider.setMapper( mapper );

        this.client = ( (ResteasyClientBuilder) ResteasyClientBuilder.newBuilder() )
            .register( jackson2Provider )
            .disableTrustManager()
            .build();
    }

    @Override
    public RedfishResource get( URI targetUri )
        throws RedfishClientException
    {
        WebTarget target = client.target( targetUri );
        try
        {
            Response response = target.request( MediaType.APPLICATION_JSON_TYPE ).buildGet().invoke();
            RedfishResource redfishResource = response.readEntity( RedfishResource.class );
            redfishResource.setOrigin( target.getUri() );
            LOGGER.debug( "Response code {} returned for request: GET {}", response.getStatus(), target.getUri() );
            return redfishResource;
        }
        catch ( ProcessingException e )
        {
            LOGGER.debug( "Processing exception occurred for request: GET {}", target.getUri() );
            throw new RedfishClientException( "Could not perform GET request on {}", targetUri, e );
        }
        catch ( WebApplicationException e )
        {
            LOGGER.debug( "Error {} while performing request: GET {}", e.getResponse().getStatus(), target.getUri() );
            throw new RedfishClientException( "Could not perform GET request on {}", targetUri, e );
        }
    }

    @Override
    public <T> void post( URI targetUri, T body )
        throws RedfishClientException
    {
        WebTarget target = client.target( targetUri );
        Entity<T> json = json( body );
        LOGGER.trace( "Sending POST request to {} with body: {} ", targetUri, json.getEntity() );
        Response response = target.request().post( json );

        LOGGER.debug( "Response code {} returned for request: POST {}", response.getStatus(), target.getUri() );
        if ( !response.getStatusInfo().getFamily().equals( SUCCESSFUL ) )
        {
            LOGGER.debug( "Error {} while performing request: POST {}", response.getStatus(), target.getUri() );
            throw new RedfishClientException(
                "POST request returned non-successful status code " + response.getStatus(),
                targetUri,
                null
            );
        }
    }

    @Override
    public <T> void patch( URI targetUri, T body )
        throws RedfishClientException
    {
        WebTarget target = client.target( targetUri );
        Entity<T> json = json( body );
        LOGGER.trace( "Sending PATCH request to {} with body: {} ", targetUri, json.getEntity() );
        Response response = target.request().method( "PATCH", json );
        LOGGER.debug( "Response code {} returned for request: PATCH {}", response.getStatus(), target.getUri() );
        if ( !response.getStatusInfo().getFamily().equals( SUCCESSFUL ) )
        {
            LOGGER.debug( "Error {} while performing request: PATCH {}", response.getStatus(), target.getUri() );
            throw new RedfishClientException(
                "PATCH request returned non-successful status code " + response.getStatus(),
                targetUri,
                null
            );
        }
    }

    @Override
    public void close()
    {
        client.close();
    }

}
