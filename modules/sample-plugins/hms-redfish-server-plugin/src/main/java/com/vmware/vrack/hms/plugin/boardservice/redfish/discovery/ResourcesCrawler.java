package com.vmware.vrack.hms.plugin.boardservice.redfish.discovery;

import com.fasterxml.jackson.databind.util.ClassUtil;
import com.vmware.vrack.hms.plugin.boardservice.redfish.client.IRedfishWebClient;
import com.vmware.vrack.hms.plugin.boardservice.redfish.client.RedfishClientException;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.OdataId;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.RedfishResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;
import static java.util.Collections.newSetFromMap;
import static java.util.Collections.unmodifiableMap;

public final class ResourcesCrawler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ResourcesCrawler.class );

    private final URI serviceRootUri;

    private final URI baseUri;

    private IRedfishWebClient webClient;

    public ResourcesCrawler( IRedfishWebClient webClient, URI serviceRootUri )
    {
        this.webClient = webClient;
        this.serviceRootUri = serviceRootUri;
        this.baseUri = UriBuilder.fromUri( serviceRootUri ).replacePath( null ).build();
    }

    public Map<String, RedfishResource> getAllResources()
    {
        Map<String, RedfishResource> resourcesMap = new HashMap<>();

        Set<URI> visited = newSetFromMap( new ConcurrentHashMap<URI, Boolean>() );
        Queue<URI> crawlQueue = new ArrayDeque<>();
        crawlQueue.add( serviceRootUri );

        while ( !crawlQueue.isEmpty() )
        {
            URI resourceUri = crawlQueue.poll();
            if ( isValidResourceUri( resourceUri ) )
            {
                if ( !visited.contains( resourceUri ) )
                {
                    try
                    {
                        RedfishResource resource = webClient.get( resourceUri );

                        // TODO @odata.id should be always available
                        resourcesMap.put( resource.getOdataId(), resource );

                        Set<URI> resourceLinks = processLinks( resource.getRelatedResources() );
                        resourceLinks.removeAll( visited );
                        crawlQueue.addAll( resourceLinks );
                    }
                    catch ( RedfishClientException e )
                    {
                        Throwable cause = ClassUtil.getRootCause( e );
                        if ( cause instanceof UnsupportedOperationException )
                        {
                            LOGGER.warn( "Problem while reading resource at URI {}: {}", e.getTargetUri(),
                                         cause.getMessage() );
                        }
                        else
                        {
                            // TODO consider retrying when ProcessingException is this exception's root cause
                            LOGGER.error( "Error while reading resource at URI {}", e.getTargetUri(), e );
                        }
                    }
                    visited.add( resourceUri );
                }
            }
            else
            {
                LOGGER.debug( format( "URI %s is not within crawled Service Root's URI namespace", resourceUri ) );
            }
        }

        return unmodifiableMap( resourcesMap );
    }

    private Set<URI> processLinks( Set<OdataId> relatedResources )
    {
        Set<URI> links = new HashSet<>();
        for ( OdataId relatedResourceOdataId : relatedResources )
        {
            URI relatedResource = relatedResourceOdataId.toUri();
            if ( relatedResource.isAbsolute() )
            {
                links.add( relatedResource );
            }
            else
            {
                links.add( URI.create( baseUri + relatedResource.toString() ) );
            }
        }
        return links;
    }

    private boolean isValidResourceUri( URI resourceUri )
    {
        if ( resourceUri == null )
        {
            return false;
        }
        return resourceUri.toString().startsWith( serviceRootUri.toString() );
    }
}
