package com.vmware.vrack.hms.plugin.boardservice.redfish.client;

import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.RedfishResource;

import java.net.URI;

public interface IRedfishWebClient
    extends AutoCloseable
{
    RedfishResource get( URI targetUri )
        throws RedfishClientException;

    <T> void post( URI targetUri, T body )
        throws RedfishClientException;

    <T> void patch( URI targetUri, T body )
        throws RedfishClientException;

    @Override
    void close();
}
