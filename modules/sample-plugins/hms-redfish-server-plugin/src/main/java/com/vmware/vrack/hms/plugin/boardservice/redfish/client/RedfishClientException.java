package com.vmware.vrack.hms.plugin.boardservice.redfish.client;

import java.net.URI;

public class RedfishClientException
    extends Exception
{
    private URI targetUri;

    public RedfishClientException( String message, URI targetUri, Throwable cause )
    {
        super( message, cause );
        this.targetUri = targetUri;
    }

    public URI getTargetUri()
    {
        return targetUri;
    }
}
