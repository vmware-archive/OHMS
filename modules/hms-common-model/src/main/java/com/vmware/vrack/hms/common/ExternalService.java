package com.vmware.vrack.hms.common;

public final class ExternalService
{
    private final String serviceType;

    private final String serviceEndpoint;

    public ExternalService( String serviceType, String serviceEndpoint )
    {
        this.serviceType = serviceType;
        this.serviceEndpoint = serviceEndpoint;
    }

    public String getServiceType()
    {
        return serviceType;
    }

    public String getServiceEndpoint()
    {
        return serviceEndpoint;
    }
}
